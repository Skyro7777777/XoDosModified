package com.xodos.shared.xodos.shell.am;

import android.content.Context;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.xodos.shared.errors.Error;
import com.xodos.shared.logger.Logger;
import com.xodos.shared.net.socket.local.LocalClientSocket;
import com.xodos.shared.net.socket.local.LocalServerSocket;
import com.xodos.shared.net.socket.local.LocalSocketManager;
import com.xodos.shared.net.socket.local.LocalSocketManagerClientBase;
import com.xodos.shared.net.socket.local.LocalSocketRunConfig;
import com.xodos.shared.shell.am.AmSocketServerRunConfig;
import com.xodos.shared.shell.am.AmSocketServer;
import com.xodos.shared.xodos.xodosConstants;
import com.xodos.shared.xodos.crash.xodosCrashUtils;
import com.xodos.shared.xodos.plugins.xodosPluginUtils;
import com.xodos.shared.xodos.settings.properties.xodosAppSharedProperties;
import com.xodos.shared.xodos.settings.properties.xodosPropertyConstants;
import com.xodos.shared.xodos.shell.command.environment.xodosAppShellEnvironment;

/**
 * A wrapper for {@link AmSocketServer} for xodos-app usage.
 *
 * The static {@link #xodosAmSocketServer} variable stores the {@link LocalSocketManager} for the
 * {@link AmSocketServer}.
 *
 * The {@link xodosAmSocketServerClient} extends the {@link AmSocketServer.AmSocketServerClient}
 * class to also show plugin error notifications for errors and disallowed client connections in
 * addition to logging the messages to logcat, which are only logged by {@link LocalSocketManagerClientBase}
 * if log level is debug or higher for privacy issues.
 *
 * It uses a filesystem socket server with the socket file at
 * {@link xodosConstants.xodos_APP#xodos_AM_SOCKET_FILE_PATH}. It would normally only allow
 * processes belonging to the xodos user and root user to connect to it. If commands are sent by the
 * root user, then the am commands executed will be run as the xodos user and its permissions,
 * capabilities and selinux context instead of root.
 *
 * The `$PREFIX/bin/xodos-am` client connects to the server via `$PREFIX/bin/xodos-am-socket` to
 * run the am commands. It provides similar functionality to "$PREFIX/bin/am"
 * (and "/system/bin/am"), but should be faster since it does not require starting a dalvik vm for
 * every command as done by "am" via xodos/xodosAm.
 *
 * The server is started by xodos-app Application class but is not started if
 * {@link xodosPropertyConstants#KEY_RUN_xodos_AM_SOCKET_SERVER} is `false` which can be done by
 * adding the prop with value "false" to the "~/.xodos/xodos.properties" file. Changes
 * require xodos-app to be force stopped and restarted.
 *
 * The current state of the server can be checked with the
 * {@link xodosAppShellEnvironment#ENV_xodos_APP__AM_SOCKET_SERVER_ENABLED} env variable, which is exported
 * for all shell sessions and tasks.
 *
 * https://github.com/xodos/xodos-am-socket
 * https://github.com/xodos/XodosAm
 */
public class xodosAmSocketServer {

    public static final String LOG_TAG = "xodosAmSocketServer";

    public static final String TITLE = "xodosAm";

    /** The static instance for the {@link xodosAmSocketServer} {@link LocalSocketManager}. */
    private static LocalSocketManager xodosAmSocketServer;

    /** Whether {@link xodosAmSocketServer} is enabled and running or not. */
    @Keep
    protected static Boolean xodos_APP_AM_SOCKET_SERVER_ENABLED;

    /**
     * Setup the {@link AmSocketServer} {@link LocalServerSocket} and start listening for
     * new {@link LocalClientSocket} if enabled.
     *
     * @param context The {@link Context} for {@link LocalSocketManager}.
     */
    public static void setupxodosAmSocketServer(@NonNull Context context) {
        // Start xodos-am-socket server if enabled by user
        boolean enabled = false;
        if (xodosAppSharedProperties.getProperties().shouldRunxodosAmSocketServer()) {
            Logger.logDebug(LOG_TAG, "Starting " + TITLE + " socket server since its enabled");
            start(context);
            if (xodosAmSocketServer != null && xodosAmSocketServer.isRunning()) {
                enabled = true;
                Logger.logDebug(LOG_TAG, TITLE + " socket server successfully started");
            }
        } else {
            Logger.logDebug(LOG_TAG, "Not starting " + TITLE + " socket server since its not enabled");
        }

        // Once xodos-app has started, the server state must not be changed since the variable is
        // exported in shell sessions and tasks and if state is changed, then env of older shells will
        // retain invalid value. User should force stop the app to update state after changing prop.
        xodos_APP_AM_SOCKET_SERVER_ENABLED = enabled;
        xodosAppShellEnvironment.updatexodosAppAMSocketServerEnabled(context);
    }

    /**
     * Create the {@link AmSocketServer} {@link LocalServerSocket} and start listening for new {@link LocalClientSocket}.
     */
    public static synchronized void start(@NonNull Context context) {
        stop();

        AmSocketServerRunConfig amSocketServerRunConfig = new AmSocketServerRunConfig(TITLE,
            xodosConstants.xodos_APP.xodos_AM_SOCKET_FILE_PATH, new xodosAmSocketServerClient());

        xodosAmSocketServer = AmSocketServer.start(context, amSocketServerRunConfig);
    }

    /**
     * Stop the {@link AmSocketServer} {@link LocalServerSocket} and stop listening for new {@link LocalClientSocket}.
     */
    public static synchronized void stop() {
        if (xodosAmSocketServer != null) {
            Error error = xodosAmSocketServer.stop();
            if (error != null) {
                xodosAmSocketServer.onError(error);
            }
            xodosAmSocketServer = null;
        }
    }
    
    /**
     * Update the state of the {@link AmSocketServer} {@link LocalServerSocket} depending on current
     * value of {@link xodosPropertyConstants#KEY_RUN_xodos_AM_SOCKET_SERVER}.
     */
    public static synchronized void updateState(@NonNull Context context) {
        xodosAppSharedProperties properties = xodosAppSharedProperties.getProperties();
        if (properties.shouldRunxodosAmSocketServer()) {
            if (xodosAmSocketServer == null) {
                Logger.logDebug(LOG_TAG, "updateState: Starting " + TITLE + " socket server");
                start(context);
            }
        } else {
            if (xodosAmSocketServer != null) {
                Logger.logDebug(LOG_TAG, "updateState: Disabling " + TITLE + " socket server");
                stop();
            }
        }
    }
    
    /**
     * Get {@link #xodosAmSocketServer}.
     */
    public static synchronized LocalSocketManager getxodosAmSocketServer() {
        return xodosAmSocketServer;
    }

    /**
     * Show an error notification on the {@link xodosConstants#xodos_PLUGIN_COMMAND_ERRORS_NOTIFICATION_CHANNEL_ID}
     * {@link xodosConstants#xodos_PLUGIN_COMMAND_ERRORS_NOTIFICATION_CHANNEL_NAME} with a call
     * to {@link xodosPluginUtils#sendPluginCommandErrorNotification(Context, String, CharSequence, String, String)}.
     *
     * @param context The {@link Context} to send the notification with.
     * @param error The {@link Error} generated.
     * @param localSocketRunConfig The {@link LocalSocketRunConfig} for {@link LocalSocketManager}.
     * @param clientSocket The optional {@link LocalClientSocket} for which the error was generated.
     */
    public static synchronized void showErrorNotification(@NonNull Context context, @NonNull Error error,
                                                          @NonNull LocalSocketRunConfig localSocketRunConfig,
                                                          @Nullable LocalClientSocket clientSocket) {
        xodosPluginUtils.sendPluginCommandErrorNotification(context, LOG_TAG,
            localSocketRunConfig.getTitle() + " Socket Server Error", error.getMinimalErrorString(),
            LocalSocketManager.getErrorMarkdownString(error, localSocketRunConfig, clientSocket));
    }



    public static Boolean getxodosAppAMSocketServerEnabled(@NonNull Context currentPackageContext) {
        boolean isxodosApp = xodosConstants.xodos_PACKAGE_NAME.equals(currentPackageContext.getPackageName());
        if (isxodosApp) {
            return xodos_APP_AM_SOCKET_SERVER_ENABLED;
        } else {
            // Currently, unsupported since plugin app processes don't know that value is set in xodos
            // app process xodosAmSocketServer class. A binder API or a way to check if server is actually
            // running needs to be used. Long checks would also not be possible on main application thread
            return null;
        }

    }





    /** Enhanced implementation for {@link AmSocketServer.AmSocketServerClient} for {@link xodosAmSocketServer}. */
    public static class xodosAmSocketServerClient extends AmSocketServer.AmSocketServerClient {

        public static final String LOG_TAG = "xodosAmSocketServerClient";

        @Nullable
        @Override
        public Thread.UncaughtExceptionHandler getLocalSocketManagerClientThreadUEH(
            @NonNull LocalSocketManager localSocketManager) {
            // Use xodos crash handler for socket listener thread just like used for main app process thread.
            return xodosCrashUtils.getCrashHandler(localSocketManager.getContext());
        }

        @Override
        public void onError(@NonNull LocalSocketManager localSocketManager,
                            @Nullable LocalClientSocket clientSocket, @NonNull Error error) {
            // Don't show notification if server is not running since errors may be triggered
            // when server is stopped and server and client sockets are closed.
            if (localSocketManager.isRunning()) {
                xodosAmSocketServer.showErrorNotification(localSocketManager.getContext(), error,
                    localSocketManager.getLocalSocketRunConfig(), clientSocket);
            }

            // But log the exception
            super.onError(localSocketManager, clientSocket, error);
        }

        @Override
        public void onDisallowedClientConnected(@NonNull LocalSocketManager localSocketManager,
                                                @NonNull LocalClientSocket clientSocket, @NonNull Error error) {
            // Always show notification and log error regardless of if server is running or not
            xodosAmSocketServer.showErrorNotification(localSocketManager.getContext(), error,
                localSocketManager.getLocalSocketRunConfig(), clientSocket);
            super.onDisallowedClientConnected(localSocketManager, clientSocket, error);
        }



        @Override
        protected String getLogTag() {
            return LOG_TAG;
        }

    }

}
