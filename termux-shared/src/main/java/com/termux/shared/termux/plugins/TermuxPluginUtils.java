package com.xodos.shared.xodos.plugins;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.os.Build;
import android.os.Environment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.xodos.shared.R;
import com.xodos.shared.activities.ReportActivity;
import com.xodos.shared.file.FileUtils;
import com.xodos.shared.xodos.file.xodosFileUtils;
import com.xodos.shared.shell.command.result.ResultConfig;
import com.xodos.shared.shell.command.result.ResultData;
import com.xodos.shared.errors.Errno;
import com.xodos.shared.errors.Error;
import com.xodos.shared.notification.NotificationUtils;
import com.xodos.shared.xodos.models.UserAction;
import com.xodos.shared.xodos.notification.xodosNotificationUtils;
import com.xodos.shared.xodos.settings.preferences.xodosPreferenceConstants;
import com.xodos.shared.shell.command.result.ResultSender;
import com.xodos.shared.shell.ShellUtils;
import com.xodos.shared.android.AndroidUtils;
import com.xodos.shared.xodos.xodosConstants;
import com.xodos.shared.xodos.xodosConstants.xodos_APP.xodos_SERVICE;
import com.xodos.shared.logger.Logger;
import com.xodos.shared.xodos.settings.preferences.xodosAppSharedPreferences;
import com.xodos.shared.xodos.settings.preferences.xodosPreferenceConstants.xodos_APP;
import com.xodos.shared.models.ReportInfo;
import com.xodos.shared.xodos.settings.properties.xodosAppSharedProperties;
import com.xodos.shared.shell.command.ExecutionCommand;
import com.xodos.shared.data.DataUtils;
import com.xodos.shared.markdown.MarkdownUtils;
import com.xodos.shared.xodos.xodosUtils;

public class xodosPluginUtils {

    private static final String LOG_TAG = "xodosPluginUtils";

    /**
     * Process {@link ExecutionCommand} result.
     *
     * The ExecutionCommand currentState must be greater or equal to
     * {@link ExecutionCommand.ExecutionState#EXECUTED}.
     * If the {@link ExecutionCommand#isPluginExecutionCommand} is {@code true} and
     * {@link ResultConfig#resultPendingIntent} or {@link ResultConfig#resultDirectoryPath}
     * is not {@code null}, then the result of commands are sent back to the command caller.
     *
     * @param context The {@link Context} that will be used to send result intent to the {@link PendingIntent} creator.
     * @param logTag The log tag to use for logging.
     * @param executionCommand The {@link ExecutionCommand} to process.
     */
    public static void processPluginExecutionCommandResult(final Context context, String logTag, final ExecutionCommand executionCommand) {
        if (executionCommand == null) return;

        logTag = DataUtils.getDefaultIfNull(logTag, LOG_TAG);
        Error error = null;
        ResultData resultData = executionCommand.resultData;

        if (!executionCommand.hasExecuted()) {
            Logger.logWarn(logTag, executionCommand.getCommandIdAndLabelLogString() + ": Ignoring call to processPluginExecutionCommandResult() since the execution command state is not higher than the ExecutionState.EXECUTED");
            return;
        }

        boolean isPluginExecutionCommandWithPendingResult = executionCommand.isPluginExecutionCommandWithPendingResult();
        boolean isExecutionCommandLoggingEnabled = Logger.shouldEnableLoggingForCustomLogLevel(executionCommand.backgroundCustomLogLevel);

        // Log the output. ResultData should not be logged if pending result since ResultSender will do it
        // or if logging is disabled
        Logger.logDebugExtended(logTag, ExecutionCommand.getExecutionOutputLogString(executionCommand, true,
            !isPluginExecutionCommandWithPendingResult, isExecutionCommandLoggingEnabled));

        // If execution command was started by a plugin which expects the result back
        if (isPluginExecutionCommandWithPendingResult) {
            // Set variables which will be used by sendCommandResultData to send back the result
            if (executionCommand.resultConfig.resultPendingIntent != null)
                setPluginResultPendingIntentVariables(executionCommand);
            if (executionCommand.resultConfig.resultDirectoryPath != null)
                setPluginResultDirectoryVariables(executionCommand);

            // Send result to caller
            error = ResultSender.sendCommandResultData(context, logTag, executionCommand.getCommandIdAndLabelLogString(),
                executionCommand.resultConfig, executionCommand.resultData, isExecutionCommandLoggingEnabled);
            if (error != null) {
                // error will be added to existing Errors
                resultData.setStateFailed(error);
                Logger.logDebugExtended(logTag, ExecutionCommand.getExecutionOutputLogString(executionCommand, true, true, isExecutionCommandLoggingEnabled));

                // Flash and send notification for the error
                sendPluginCommandErrorNotification(context, logTag, null,
                    ResultData.getErrorsListMinimalString(resultData),
                    ExecutionCommand.getExecutionCommandMarkdownString(executionCommand),
                    false, true, xodosUtils.AppInfoMode.xodos_AND_CALLING_PACKAGE,true,
                    executionCommand.resultConfig.resultPendingIntent != null ? executionCommand.resultConfig.resultPendingIntent.getCreatorPackage(): null);
            }

        }

        if (!executionCommand.isStateFailed() && error == null)
            executionCommand.setState(ExecutionCommand.ExecutionState.SUCCESS);
    }

    /**
     * Set {@link ExecutionCommand} state to {@link Errno#ERRNO_FAILED} with {@code errmsg} and
     * process error with {@link #processPluginExecutionCommandError(Context, String, ExecutionCommand, boolean)}.
     *
     *
     * @param context The {@link Context} for operations.
     * @param logTag The log tag to use for logging.
     * @param executionCommand The {@link ExecutionCommand} that failed.
     * @param forceNotification If set to {@code true}, then a flash and notification will be shown
     *                          regardless of if pending intent is {@code null} or
     *                          {@link xodos_APP#KEY_PLUGIN_ERROR_NOTIFICATIONS_ENABLED}
     *                          is {@code false}.
     * @param errmsg The error message to set.
     */
    public static void setAndProcessPluginExecutionCommandError(final Context context, String logTag,
                                                          final ExecutionCommand executionCommand,
                                                          boolean forceNotification,
                                                          @NonNull String errmsg) {
        executionCommand.setStateFailed(Errno.ERRNO_FAILED.getCode(), errmsg);
        processPluginExecutionCommandError(context, logTag, executionCommand, forceNotification);
    }

    /**
     * Process {@link ExecutionCommand} error.
     *
     * The ExecutionCommand currentState must be equal to {@link ExecutionCommand.ExecutionState#FAILED}.
     * The {@link ResultData#getErrCode()} must have been set to a value greater than
     * {@link Errno#ERRNO_SUCCESS}.
     * The {@link ResultData#errorsList} must also be set with appropriate error info.
     *
     * If the {@link ExecutionCommand#isPluginExecutionCommand} is {@code true} and
     * {@link ResultConfig#resultPendingIntent} or {@link ResultConfig#resultDirectoryPath}
     * is not {@code null}, then the errors of commands are sent back to the command caller.
     *
     * Otherwise if the {@link xodos_APP#KEY_PLUGIN_ERROR_NOTIFICATIONS_ENABLED} is
     * enabled, then a flash and a notification will be shown for the error as well
     * on the {@link xodosConstants#xodos_PLUGIN_COMMAND_ERRORS_NOTIFICATION_CHANNEL_NAME} channel instead of just logging
     * the error.
     *
     * @param context The {@link Context} for operations.
     * @param logTag The log tag to use for logging.
     * @param executionCommand The {@link ExecutionCommand} that failed.
     * @param forceNotification If set to {@code true}, then a flash and notification will be shown
     *                          regardless of if pending intent is {@code null} or
     *                          {@link xodos_APP#KEY_PLUGIN_ERROR_NOTIFICATIONS_ENABLED}
     *                          is {@code false}.
     */
    public static void processPluginExecutionCommandError(final Context context, String logTag,
                                                          final ExecutionCommand executionCommand,
                                                          boolean forceNotification) {
        if (context == null || executionCommand == null) return;

        logTag = DataUtils.getDefaultIfNull(logTag, LOG_TAG);
        Error error;
        ResultData resultData = executionCommand.resultData;

        if (!executionCommand.isStateFailed()) {
            Logger.logWarn(logTag, executionCommand.getCommandIdAndLabelLogString() + ": Ignoring call to processPluginExecutionCommandError() since the execution command is not in ExecutionState.FAILED");
            return;
        }

        boolean isPluginExecutionCommandWithPendingResult = executionCommand.isPluginExecutionCommandWithPendingResult();
        boolean isExecutionCommandLoggingEnabled = Logger.shouldEnableLoggingForCustomLogLevel(executionCommand.backgroundCustomLogLevel);

        // Log the error and any exception. ResultData should not be logged if pending result since ResultSender will do it
        Logger.logError(logTag, "Processing plugin execution error for:\n" + executionCommand.getCommandIdAndLabelLogString());
        Logger.logError(logTag, "Set log level to debug or higher to see error in logs");
        Logger.logErrorPrivateExtended(logTag, ExecutionCommand.getExecutionOutputLogString(executionCommand, true,
            !isPluginExecutionCommandWithPendingResult, isExecutionCommandLoggingEnabled));

        // If execution command was started by a plugin which expects the result back
        if (isPluginExecutionCommandWithPendingResult) {
            // Set variables which will be used by sendCommandResultData to send back the result
            if (executionCommand.resultConfig.resultPendingIntent != null)
                setPluginResultPendingIntentVariables(executionCommand);
            if (executionCommand.resultConfig.resultDirectoryPath != null)
                setPluginResultDirectoryVariables(executionCommand);

            // Send result to caller
            error = ResultSender.sendCommandResultData(context, logTag, executionCommand.getCommandIdAndLabelLogString(),
                executionCommand.resultConfig, executionCommand.resultData, isExecutionCommandLoggingEnabled);
            if (error != null) {
                // error will be added to existing Errors
                resultData.setStateFailed(error);
                Logger.logErrorPrivateExtended(logTag, ExecutionCommand.getExecutionOutputLogString(executionCommand, true, true, isExecutionCommandLoggingEnabled));
                forceNotification = true;
            }

            // No need to show notifications if a pending intent was sent, let the caller handle the result himself
            if (!forceNotification) return;
        }

        // Flash and send notification for the error
        sendPluginCommandErrorNotification(context, logTag, null,
            ResultData.getErrorsListMinimalString(resultData),
            ExecutionCommand.getExecutionCommandMarkdownString(executionCommand),
            forceNotification, true, xodosUtils.AppInfoMode.xodos_AND_CALLING_PACKAGE, true,
            executionCommand.resultConfig.resultPendingIntent != null ? executionCommand.resultConfig.resultPendingIntent.getCreatorPackage(): null);
    }

    /** Set variables which will be used by {@link ResultSender#sendCommandResultData(Context, String, String, ResultConfig, ResultData, boolean)}
     * to send back the result via {@link ResultConfig#resultPendingIntent}. */
    public static void setPluginResultPendingIntentVariables(ExecutionCommand executionCommand) {
        ResultConfig resultConfig = executionCommand.resultConfig;

        resultConfig.resultBundleKey = xodos_SERVICE.EXTRA_PLUGIN_RESULT_BUNDLE;
        resultConfig.resultStdoutKey = xodos_SERVICE.EXTRA_PLUGIN_RESULT_BUNDLE_STDOUT;
        resultConfig.resultStdoutOriginalLengthKey = xodos_SERVICE.EXTRA_PLUGIN_RESULT_BUNDLE_STDOUT_ORIGINAL_LENGTH;
        resultConfig.resultStderrKey = xodos_SERVICE.EXTRA_PLUGIN_RESULT_BUNDLE_STDERR;
        resultConfig.resultStderrOriginalLengthKey = xodos_SERVICE.EXTRA_PLUGIN_RESULT_BUNDLE_STDERR_ORIGINAL_LENGTH;
        resultConfig.resultExitCodeKey = xodos_SERVICE.EXTRA_PLUGIN_RESULT_BUNDLE_EXIT_CODE;
        resultConfig.resultErrCodeKey = xodos_SERVICE.EXTRA_PLUGIN_RESULT_BUNDLE_ERR;
        resultConfig.resultErrmsgKey = xodos_SERVICE.EXTRA_PLUGIN_RESULT_BUNDLE_ERRMSG;
    }

    /** Set variables which will be used by {@link ResultSender#sendCommandResultData(Context, String, String, ResultConfig, ResultData, boolean)}
     * to send back the result by writing it to files in {@link ResultConfig#resultDirectoryPath}. */
    public static void setPluginResultDirectoryVariables(ExecutionCommand executionCommand) {
        ResultConfig resultConfig = executionCommand.resultConfig;

        resultConfig.resultDirectoryPath = xodosFileUtils.getCanonicalPath(resultConfig.resultDirectoryPath, null, true);
        resultConfig.resultDirectoryAllowedParentPath = xodosFileUtils.getMatchedAllowedxodosWorkingDirectoryParentPathForPath(resultConfig.resultDirectoryPath);

        // Set default resultFileBasename if resultSingleFile is true to `<executable_basename>-<timestamp>.log`
        if (resultConfig.resultSingleFile && resultConfig.resultFileBasename == null)
            resultConfig.resultFileBasename = ShellUtils.getExecutableBasename(executionCommand.executable) + "-" + AndroidUtils.getCurrentMilliSecondLocalTimeStamp() + ".log";
    }




    /**
     * Send a plugin error report notification for {@link xodosConstants#xodos_PLUGIN_COMMAND_ERRORS_NOTIFICATION_CHANNEL_ID}
     * and {@link xodosConstants#xodos_PLUGIN_COMMAND_ERRORS_NOTIFICATION_CHANNEL_NAME}.
     *
     * @param currentPackageContext The {@link Context} of current package.
     * @param logTag The log tag to use for logging.
     * @param title The title for the error report and notification.
     * @param message The message for the error report.
     * @param throwable The {@link Throwable} for the error report.
     */
    public static void sendPluginCommandErrorNotification(final Context currentPackageContext, String logTag,
                                                         CharSequence title, String message, Throwable throwable) {
        sendPluginCommandErrorNotification(currentPackageContext, logTag,
            title, message,
            MarkdownUtils.getMarkdownCodeForString(Logger.getMessageAndStackTraceString(message, throwable), true),
            false, false, true);
    }

    /**
     * Send a plugin error report notification for {@link xodosConstants#xodos_PLUGIN_COMMAND_ERRORS_NOTIFICATION_CHANNEL_ID}
     * and {@link xodosConstants#xodos_PLUGIN_COMMAND_ERRORS_NOTIFICATION_CHANNEL_NAME}.
     *
     * @param currentPackageContext The {@link Context} of current package.
     * @param logTag The log tag to use for logging.
     * @param title The title for the error report and notification.
     * @param notificationTextString The text of the notification.
     * @param message The message for the error report.
     */
    public static void sendPluginCommandErrorNotification(final Context currentPackageContext, String logTag,
                                                         CharSequence title, String notificationTextString,
                                                         String message) {
        sendPluginCommandErrorNotification(currentPackageContext, logTag,
            title, notificationTextString, message,
            false, false, true);
    }

    /**
     * Send a plugin error report notification for {@link xodosConstants#xodos_PLUGIN_COMMAND_ERRORS_NOTIFICATION_CHANNEL_ID}
     * and {@link xodosConstants#xodos_PLUGIN_COMMAND_ERRORS_NOTIFICATION_CHANNEL_NAME}.
     *
     * @param currentPackageContext The {@link Context} of current package.
     * @param logTag The log tag to use for logging.
     * @param title The title for the error report and notification.
     * @param notificationTextString The text of the notification.
     * @param message The message for the error report.
     * @param forceNotification If set to {@code true}, then a notification will be shown
     *                          regardless of if pending intent is {@code null} or
     *                          {@link xodosPreferenceConstants.xodos_APP#KEY_PLUGIN_ERROR_NOTIFICATIONS_ENABLED}
     *                          is {@code false}.
     * @param showToast If set to {@code true}, then a toast will be shown for {@code notificationTextString}.
     * @param addDeviceInfo If set to {@code true}, then device info should be appended to the message.
     */
    public static void sendPluginCommandErrorNotification(final Context currentPackageContext, String logTag,
                                                         CharSequence title, String notificationTextString,
                                                         String message, boolean forceNotification,
                                                         boolean showToast,
                                                         boolean addDeviceInfo) {
        sendPluginCommandErrorNotification(currentPackageContext, logTag,
            title, notificationTextString, "## " + title + "\n\n" + message + "\n\n",
            forceNotification, showToast, xodosUtils.AppInfoMode.xodos_AND_PLUGIN_PACKAGE, addDeviceInfo, null);
    }

    /**
     * Send a plugin error notification for {@link xodosConstants#xodos_PLUGIN_COMMAND_ERRORS_NOTIFICATION_CHANNEL_ID}
     * and {@link xodosConstants#xodos_PLUGIN_COMMAND_ERRORS_NOTIFICATION_CHANNEL_NAME}.
     *
     * @param currentPackageContext The {@link Context} of current package.
     * @param logTag The log tag to use for logging.
     * @param title The title for the error report and notification.
     * @param notificationTextString The text of the notification.
     * @param message The message for the error report.
     * @param forceNotification If set to {@code true}, then a notification will be shown
     *                          regardless of if pending intent is {@code null} or
     *                          {@link xodosPreferenceConstants.xodos_APP#KEY_PLUGIN_ERROR_NOTIFICATIONS_ENABLED}
     *                          is {@code false}.
     * @param showToast If set to {@code true}, then a toast will be shown for {@code notificationTextString}.
     * @param appInfoMode The {@link xodosUtils.AppInfoMode} to use to add app info to the message.
     *                    Set to {@code null} if app info should not be appended to the message.
     * @param addDeviceInfo If set to {@code true}, then device info should be appended to the message.
     * @param callingPackageName The optional package name of the app for which the plugin command
     *                           was run.
     */
    public static void sendPluginCommandErrorNotification(Context currentPackageContext, String logTag,
                                                          CharSequence title,
                                                          String notificationTextString,
                                                          String message, boolean forceNotification,
                                                          boolean showToast,
                                                          xodosUtils.AppInfoMode appInfoMode,
                                                          boolean addDeviceInfo,
                                                          String callingPackageName) {
        // Note: Do not change currentPackageContext or xodosPackageContext passed to functions or things will break

        if (currentPackageContext == null) return;
        String currentPackageName = currentPackageContext.getPackageName();

        final Context xodosPackageContext = xodosUtils.getxodosPackageContext(currentPackageContext);
        if (xodosPackageContext == null) {
            Logger.logWarn(LOG_TAG, "Ignoring call to sendPluginCommandErrorNotification() since failed to get \"" + xodosConstants.xodos_PACKAGE_NAME + "\" package context from \"" + currentPackageName + "\" context");
            return;
        }

        xodosAppSharedPreferences preferences = xodosAppSharedPreferences.build(xodosPackageContext);
        if (preferences == null) return;

        // If user has disabled notifications for plugin commands, then just return
        if (!preferences.arePluginErrorNotificationsEnabled(true) && !forceNotification)
            return;

        logTag = DataUtils.getDefaultIfNull(logTag, LOG_TAG);

        if (showToast)
            Logger.showToast(currentPackageContext, notificationTextString, true);

        // Send a notification to show the error which when clicked will open the ReportActivity
        // to show the details of the error
        if (title == null || title.toString().isEmpty())
            title = xodosConstants.xodos_APP_NAME + " Plugin Execution Command Error";

        Logger.logDebug(logTag, "Sending \"" + title + "\" notification.");

        StringBuilder reportString = new StringBuilder(message);

        if (appInfoMode != null)
            reportString.append("\n\n").append(xodosUtils.getAppInfoMarkdownString(currentPackageContext, appInfoMode,
                callingPackageName != null ? callingPackageName : currentPackageName));

        if (addDeviceInfo)
            reportString.append("\n\n").append(AndroidUtils.getDeviceInfoMarkdownString(currentPackageContext, true));

        String userActionName = UserAction.PLUGIN_EXECUTION_COMMAND.getName();

        ReportInfo reportInfo = new ReportInfo(userActionName, logTag, title.toString());
        reportInfo.setReportString(reportString.toString());
        reportInfo.setReportStringSuffix("\n\n" + xodosUtils.getReportIssueMarkdownString(currentPackageContext));
        reportInfo.setAddReportInfoHeaderToMarkdown(true);
        reportInfo.setReportSaveFileLabelAndPath(userActionName,
            Environment.getExternalStorageDirectory() + "/" +
                FileUtils.sanitizeFileName(xodosConstants.xodos_APP_NAME + "-" + userActionName + ".log", true, true));

        ReportActivity.NewInstanceResult result = ReportActivity.newInstance(xodosPackageContext, reportInfo);
        if (result.contentIntent == null) return;

        // Must ensure result code for PendingIntents and id for notification are unique otherwise will override previous
        int nextNotificationId = xodosNotificationUtils.getNextNotificationId(xodosPackageContext);

        PendingIntent contentIntent = PendingIntent.getActivity(xodosPackageContext, nextNotificationId, result.contentIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        PendingIntent deleteIntent = null;
        if (result.deleteIntent != null)
            deleteIntent = PendingIntent.getBroadcast(xodosPackageContext, nextNotificationId, result.deleteIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Setup the notification channel if not already set up
        setupPluginCommandErrorsNotificationChannel(xodosPackageContext);

        // Use markdown in notification
        CharSequence notificationTextCharSequence = MarkdownUtils.getSpannedMarkdownText(xodosPackageContext, notificationTextString);
        //CharSequence notificationTextCharSequence = notificationTextString;

        // Build the notification
        Notification.Builder builder = getPluginCommandErrorsNotificationBuilder(currentPackageContext, xodosPackageContext,
            title, notificationTextCharSequence, notificationTextCharSequence, contentIntent, deleteIntent,
            NotificationUtils.NOTIFICATION_MODE_VIBRATE);
        if (builder == null) return;

        // Send the notification
        NotificationManager notificationManager = NotificationUtils.getNotificationManager(xodosPackageContext);
        if (notificationManager != null)
            notificationManager.notify(nextNotificationId, builder.build());
    }

    /**
     * Get {@link Notification.Builder} for {@link xodosConstants#xodos_PLUGIN_COMMAND_ERRORS_NOTIFICATION_CHANNEL_ID}
     * and {@link xodosConstants#xodos_PLUGIN_COMMAND_ERRORS_NOTIFICATION_CHANNEL_NAME}.
     *
     * @param currentPackageContext The {@link Context} of current package.
     * @param xodosPackageContext The {@link Context} of xodos package.
     * @param title The title for the notification.
     * @param notificationText The second line text of the notification.
     * @param notificationBigText The full text of the notification that may optionally be styled.
     * @param contentIntent The {@link PendingIntent} which should be sent when notification is clicked.
     * @param deleteIntent The {@link PendingIntent} which should be sent when notification is deleted.
     * @param notificationMode The notification mode. It must be one of {@code NotificationUtils.NOTIFICATION_MODE_*}.
     * @return Returns the {@link Notification.Builder}.
     */
    @Nullable
    public static Notification.Builder getPluginCommandErrorsNotificationBuilder(final Context currentPackageContext,
                                                                                 final Context xodosPackageContext,
                                                                                 final CharSequence title,
                                                                                 final CharSequence notificationText,
                                                                                 final CharSequence notificationBigText,
                                                                                 final PendingIntent contentIntent,
                                                                                 final PendingIntent deleteIntent,
                                                                                 final int notificationMode) {
        return xodosNotificationUtils.getxodosOrPluginAppNotificationBuilder(
            currentPackageContext, xodosPackageContext,
            xodosConstants.xodos_PLUGIN_COMMAND_ERRORS_NOTIFICATION_CHANNEL_ID, Notification.PRIORITY_HIGH,
            title, notificationText, notificationBigText, contentIntent, deleteIntent, notificationMode);
    }

    /**
     * Setup the notification channel for {@link xodosConstants#xodos_PLUGIN_COMMAND_ERRORS_NOTIFICATION_CHANNEL_ID} and
     * {@link xodosConstants#xodos_PLUGIN_COMMAND_ERRORS_NOTIFICATION_CHANNEL_NAME}.
     *
     * @param context The {@link Context} for operations.
     */
    public static void setupPluginCommandErrorsNotificationChannel(final Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return;
        NotificationUtils.setupNotificationChannel(context, xodosConstants.xodos_PLUGIN_COMMAND_ERRORS_NOTIFICATION_CHANNEL_ID,
            xodosConstants.xodos_PLUGIN_COMMAND_ERRORS_NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
    }



    /**
     * Check if {@link xodosConstants#PROP_ALLOW_EXTERNAL_APPS} property is not set to "true".
     *
     * @param context The {@link Context} to get error string.
     * @return Returns the {@code error} if policy is violated, otherwise {@code null}.
     */
    public static String checkIfAllowExternalAppsPolicyIsViolated(final Context context, String apiName) {
        String errmsg = null;

        xodosAppSharedProperties mProperties = xodosAppSharedProperties.getProperties();
        if (mProperties == null || !mProperties.shouldAllowExternalApps()) {
            errmsg = context.getString(R.string.error_allow_external_apps_ungranted, apiName,
                xodosFileUtils.getUnExpandedxodosPath(xodosConstants.xodos_PROPERTIES_PRIMARY_FILE_PATH));
        }

        return errmsg;
    }

}
