package com.xodos.app;

import android.app.Application;
import android.content.Context;

import com.xodos.BuildConfig;
import com.xodos.shared.errors.Error;
import com.xodos.shared.logger.Logger;
import com.xodos.shared.xodos.xodosBootstrap;
import com.xodos.shared.xodos.xodosConstants;
import com.xodos.shared.xodos.crash.xodosCrashUtils;
import com.xodos.shared.xodos.file.xodosFileUtils;
import com.xodos.shared.xodos.settings.preferences.xodosAppSharedPreferences;
import com.xodos.shared.xodos.settings.properties.xodosAppSharedProperties;
import com.xodos.shared.xodos.shell.command.environment.xodosShellEnvironment;
import com.xodos.shared.xodos.shell.am.xodosAmSocketServer;
import com.xodos.shared.xodos.shell.xodosShellManager;
import com.xodos.shared.xodos.theme.xodosThemeUtils;

public class xodosApplication extends Application {

    private static final String LOG_TAG = "xodosApplication";

    public void onCreate() {
        super.onCreate();

        Context context = getApplicationContext();

        // Set crash handler for the app
        xodosCrashUtils.setDefaultCrashHandler(this);

        // Set log config for the app
        setLogConfig(context);

        Logger.logDebug("Starting Application");

        // Set xodosBootstrap.xodos_APP_PACKAGE_MANAGER and xodosBootstrap.xodos_APP_PACKAGE_VARIANT
        xodosBootstrap.setxodosPackageManagerAndVariant(BuildConfig.xodos_PACKAGE_VARIANT);

        // Init app wide SharedProperties loaded from xodos.properties
        xodosAppSharedProperties properties = xodosAppSharedProperties.init(context);

        // Init app wide shell manager
        xodosShellManager shellManager = xodosShellManager.init(context);

        // Set NightMode.APP_NIGHT_MODE
        xodosThemeUtils.setAppNightMode(properties.getNightMode());

        // Check and create xodos files directory. If failed to access it like in case of secondary
        // user or external sd card installation, then don't run files directory related code
        Error error = xodosFileUtils.isxodosFilesDirectoryAccessible(this, true, true);
        boolean isxodosFilesDirectoryAccessible = error == null;
        if (isxodosFilesDirectoryAccessible) {
            Logger.logInfo(LOG_TAG, "xodos files directory is accessible");

            error = xodosFileUtils.isAppsxodosAppDirectoryAccessible(true, true);
            if (error != null) {
                Logger.logErrorExtended(LOG_TAG, "Create apps/xodos-app directory failed\n" + error);
                return;
            }

            // Setup xodos-am-socket server
            xodosAmSocketServer.setupxodosAmSocketServer(context);
        } else {
            Logger.logErrorExtended(LOG_TAG, "xodos files directory is not accessible\n" + error);
        }

        // Init xodosShellEnvironment constants and caches after everything has been setup including xodos-am-socket server
        xodosShellEnvironment.init(this);

        if (isxodosFilesDirectoryAccessible) {
            xodosShellEnvironment.writeEnvironmentToFile(this);
        }
    }

    public static void setLogConfig(Context context) {
        Logger.setDefaultLogTag(xodosConstants.xodos_APP_NAME);

        // Load the log level from shared preferences and set it to the {@link Logger.CURRENT_LOG_LEVEL}
        xodosAppSharedPreferences preferences = xodosAppSharedPreferences.build(context);
        if (preferences == null) return;
        preferences.setLogLevel(null, preferences.getLogLevel());
    }

}
