package com.xodos.shared.xodos.shell.command.runner.terminal;

import android.content.Context;
import android.system.OsConstants;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.common.base.Joiner;
import com.xodos.shared.R;
import com.xodos.shared.shell.command.ExecutionCommand;
import com.xodos.shared.shell.command.environment.ShellEnvironmentUtils;
import com.xodos.shared.shell.command.environment.UnixShellEnvironment;
import com.xodos.shared.shell.command.result.ResultData;
import com.xodos.shared.errors.Errno;
import com.xodos.shared.logger.Logger;
import com.xodos.shared.shell.command.environment.IShellEnvironment;
import com.xodos.shared.shell.ShellUtils;
import com.xodos.terminal.TerminalSession;
import com.xodos.terminal.TerminalSessionClient;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * A class that maintains info for foreground xodos sessions.
 * It also provides a way to link each {@link TerminalSession} with the {@link ExecutionCommand}
 * that started it.
 */
public class xodosSession {

    private final TerminalSession mTerminalSession;
    private final ExecutionCommand mExecutionCommand;
    private final xodosSessionClient mxodosSessionClient;
    private final boolean mSetStdoutOnExit;

    private static final String LOG_TAG = "xodosSession";

    private xodosSession(@NonNull final TerminalSession terminalSession, @NonNull final ExecutionCommand executionCommand,
                          final xodosSessionClient xodosSessionClient, final boolean setStdoutOnExit) {
        this.mTerminalSession = terminalSession;
        this.mExecutionCommand = executionCommand;
        this.mxodosSessionClient = xodosSessionClient;
        this.mSetStdoutOnExit = setStdoutOnExit;
    }

    /**
     * Start execution of an {@link ExecutionCommand} with {@link Runtime#exec(String[], String[], File)}.
     *
     * The {@link ExecutionCommand#executable}, must be set, {@link ExecutionCommand#commandLabel},
     * {@link ExecutionCommand#arguments} and {@link ExecutionCommand#workingDirectory} may optionally
     * be set.
     *
     * If {@link ExecutionCommand#executable} is {@code null}, then a default shell is automatically
     * chosen.
     *
     * @param currentPackageContext The {@link Context} for operations. This must be the context for
     *                              the current package and not the context of a `sharedUserId` package,
     *                              since environment setup may be dependent on current package.
     * @param executionCommand The {@link ExecutionCommand} containing the information for execution command.
     * @param terminalSessionClient The {@link TerminalSessionClient} interface implementation.
     * @param xodosSessionClient The {@link xodosSessionClient} interface implementation.
     * @param shellEnvironmentClient The {@link IShellEnvironment} interface implementation.
     * @param additionalEnvironment The additional shell environment variables to export. Existing
     *                              variables will be overridden.
     * @param setStdoutOnExit If set to {@code true}, then the {@link ResultData#stdout}
     *                        available in the {@link xodosSessionClient#onxodosSessionExited(xodosSession)}
     *                        callback will be set to the {@link TerminalSession} transcript. The session
     *                        transcript will contain both stdout and stderr combined, basically
     *                        anything sent to the the pseudo terminal /dev/pts, including PS1 prefixes.
     *                        Set this to {@code true} only if the session transcript is required,
     *                        since this requires extra processing to get it.
     * @return Returns the {@link xodosSession}. This will be {@code null} if failed to start the execution command.
     */
    public static xodosSession execute(@NonNull final Context currentPackageContext, @NonNull ExecutionCommand executionCommand,
                                        @NonNull final TerminalSessionClient terminalSessionClient, final xodosSessionClient xodosSessionClient,
                                        @NonNull final IShellEnvironment shellEnvironmentClient,
                                        @Nullable HashMap<String, String> additionalEnvironment,
                                        final boolean setStdoutOnExit) {
        if (executionCommand.executable != null && executionCommand.executable.isEmpty())
            executionCommand.executable = null;
        if (executionCommand.workingDirectory == null || executionCommand.workingDirectory.isEmpty())
            executionCommand.workingDirectory = shellEnvironmentClient.getDefaultWorkingDirectoryPath();
        if (executionCommand.workingDirectory.isEmpty())
            executionCommand.workingDirectory = "/";

        String defaultBinPath = shellEnvironmentClient.getDefaultBinPath();
        if (defaultBinPath.isEmpty())
            defaultBinPath = "/system/bin";

        boolean isLoginShell = false;
        if (executionCommand.executable == null) {
            if (!executionCommand.isFailsafe) {
                for (String shellBinary : UnixShellEnvironment.LOGIN_SHELL_BINARIES) {
                    File shellFile = new File(defaultBinPath, shellBinary);
                    if (shellFile.canExecute()) {
                        executionCommand.executable = shellFile.getAbsolutePath();
                        break;
                    }
                }
            }

            if (executionCommand.executable == null) {
                // Fall back to system shell as last resort:
                // Do not start a login shell since ~/.profile may cause startup failure if its invalid.
                // /system/bin/sh is provided by mksh (not toybox) and does load .mkshrc but for android its set
                // to /system/etc/mkshrc even though its default is ~/.mkshrc.
                // So /system/etc/mkshrc must still be valid for failsafe session to start properly.
                // https://cs.android.com/android/platform/superproject/+/android-11.0.0_r3:external/mksh/src/main.c;l=663
                // https://cs.android.com/android/platform/superproject/+/android-11.0.0_r3:external/mksh/src/main.c;l=41
                // https://cs.android.com/android/platform/superproject/+/android-11.0.0_r3:external/mksh/Android.bp;l=114
                executionCommand.executable = "/system/bin/sh";
            } else {
                isLoginShell = true;
            }

        }

        // Setup command args
        String[] commandArgs = shellEnvironmentClient.setupShellCommandArguments(executionCommand.executable, executionCommand.arguments);

        executionCommand.executable = commandArgs[0];
        String processName = (isLoginShell ? "-" : "") + ShellUtils.getExecutableBasename(executionCommand.executable);

        String[] arguments = new String[commandArgs.length];
        arguments[0] = processName;
        if (commandArgs.length > 1) System.arraycopy(commandArgs, 1, arguments, 1, commandArgs.length - 1);

        executionCommand.arguments = arguments;

        if (executionCommand.commandLabel == null)
            executionCommand.commandLabel = processName;

        // Setup command environment
        HashMap<String, String> environment = shellEnvironmentClient.setupShellCommandEnvironment(currentPackageContext,
            executionCommand);
        if (additionalEnvironment != null)
            environment.putAll(additionalEnvironment);
        List<String> environmentList = ShellEnvironmentUtils.convertEnvironmentToEnviron(environment);
        Collections.sort(environmentList);
        String[] environmentArray = environmentList.toArray(new String[0]);

        if (!executionCommand.setState(ExecutionCommand.ExecutionState.EXECUTING)) {
            executionCommand.setStateFailed(Errno.ERRNO_FAILED.getCode(), currentPackageContext.getString(R.string.error_failed_to_execute_xodos_session_command, executionCommand.getCommandIdAndLabelLogString()));
            xodosSession.processxodosSessionResult(null, executionCommand);
            return null;
        }

        Logger.logDebugExtended(LOG_TAG, executionCommand.toString());
        Logger.logVerboseExtended(LOG_TAG, "\"" + executionCommand.getCommandIdAndLabelLogString() + "\" xodosSession Environment:\n" +
            Joiner.on("\n").join(environmentArray));

        Logger.logDebug(LOG_TAG, "Running \"" + executionCommand.getCommandIdAndLabelLogString() + "\" xodosSession");
        TerminalSession terminalSession = new TerminalSession(executionCommand.executable,
            executionCommand.workingDirectory, executionCommand.arguments, environmentArray,
            executionCommand.terminalTranscriptRows, terminalSessionClient);

        if (executionCommand.shellName != null) {
            terminalSession.mSessionName = executionCommand.shellName;
        }

        return new xodosSession(terminalSession, executionCommand, xodosSessionClient, setStdoutOnExit);
    }

    /**
     * Signal that this {@link xodosSession} has finished.  This should be called when
     * {@link TerminalSessionClient#onSessionFinished(TerminalSession)} callback is received by the caller.
     *
     * If the processes has finished, then sets {@link ResultData#stdout}, {@link ResultData#stderr}
     * and {@link ResultData#exitCode} for the {@link #mExecutionCommand} of the {@code xodosTask}
     * and then calls {@link #processxodosSessionResult(xodosSession, ExecutionCommand)} to process the result}.
     *
     */
    public void finish() {
        // If process is still running, then ignore the call
        if (mTerminalSession.isRunning()) return;

        int exitCode = mTerminalSession.getExitStatus();

        if (exitCode == 0)
            Logger.logDebug(LOG_TAG, "The \"" + mExecutionCommand.getCommandIdAndLabelLogString() + "\" xodosSession exited normally");
        else
            Logger.logDebug(LOG_TAG, "The \"" + mExecutionCommand.getCommandIdAndLabelLogString() + "\" xodosSession exited with code: " + exitCode);

        // If the execution command has already failed, like SIGKILL was sent, then don't continue
        if (mExecutionCommand.isStateFailed()) {
            Logger.logDebug(LOG_TAG, "Ignoring setting \"" + mExecutionCommand.getCommandIdAndLabelLogString() + "\" xodosSession state to ExecutionState.EXECUTED and processing results since it has already failed");
            return;
        }

        mExecutionCommand.resultData.exitCode = exitCode;

        if (this.mSetStdoutOnExit)
            mExecutionCommand.resultData.stdout.append(ShellUtils.getTerminalSessionTranscriptText(mTerminalSession, true, false));

        if (!mExecutionCommand.setState(ExecutionCommand.ExecutionState.EXECUTED))
            return;

        xodosSession.processxodosSessionResult(this, null);
    }

    /**
     * Kill this {@link xodosSession} by sending a {@link OsConstants#SIGILL} to its {@link #mTerminalSession}
     * if its still executing.
     *
     * @param context The {@link Context} for operations.
     * @param processResult If set to {@code true}, then the {@link #processxodosSessionResult(xodosSession, ExecutionCommand)}
     *                      will be called to process the failure.
     */
    public void killIfExecuting(@NonNull final Context context, boolean processResult) {
        // If execution command has already finished executing, then no need to process results or send SIGKILL
        if (mExecutionCommand.hasExecuted()) {
            Logger.logDebug(LOG_TAG, "Ignoring sending SIGKILL to \"" + mExecutionCommand.getCommandIdAndLabelLogString() + "\" xodosSession since it has already finished executing");
            return;
        }

        Logger.logDebug(LOG_TAG, "Send SIGKILL to \"" + mExecutionCommand.getCommandIdAndLabelLogString() + "\" xodosSession");
        if (mExecutionCommand.setStateFailed(Errno.ERRNO_FAILED.getCode(), context.getString(R.string.error_sending_sigkill_to_process))) {
            if (processResult) {
                mExecutionCommand.resultData.exitCode = 137; // SIGKILL

                // Get whatever output has been set till now in case its needed
                if (this.mSetStdoutOnExit)
                    mExecutionCommand.resultData.stdout.append(ShellUtils.getTerminalSessionTranscriptText(mTerminalSession, true, false));

                xodosSession.processxodosSessionResult(this, null);
            }
        }

        // Send SIGKILL to process
        mTerminalSession.finishIfRunning();
    }

    /**
     * Process the results of {@link xodosSession} or {@link ExecutionCommand}.
     *
     * Only one of {@code xodosSession} and {@code executionCommand} must be set.
     *
     * If the {@code xodosSession} and its {@link #mxodosSessionClient} are not {@code null},
     * then the {@link xodosSession.xodosSessionClient#onxodosSessionExited(xodosSession)}
     * callback will be called.
     *
     * @param xodosSession The {@link xodosSession}, which should be set if
     *                  {@link #execute(Context, ExecutionCommand, TerminalSessionClient, xodosSessionClient, IShellEnvironment, HashMap, boolean)}
     *                   successfully started the process.
     * @param executionCommand The {@link ExecutionCommand}, which should be set if
     *                          {@link #execute(Context, ExecutionCommand, TerminalSessionClient, xodosSessionClient, IShellEnvironment, HashMap, boolean)}
     *                          failed to start the process.
     */
    private static void processxodosSessionResult(final xodosSession xodosSession, ExecutionCommand executionCommand) {
        if (xodosSession != null)
            executionCommand = xodosSession.mExecutionCommand;

        if (executionCommand == null) return;

        if (executionCommand.shouldNotProcessResults()) {
            Logger.logDebug(LOG_TAG, "Ignoring duplicate call to process \"" + executionCommand.getCommandIdAndLabelLogString() + "\" xodosSession result");
            return;
        }

        Logger.logDebug(LOG_TAG, "Processing \"" + executionCommand.getCommandIdAndLabelLogString() + "\" xodosSession result");

        if (xodosSession != null && xodosSession.mxodosSessionClient != null) {
            xodosSession.mxodosSessionClient.onxodosSessionExited(xodosSession);
        } else {
            // If a callback is not set and execution command didn't fail, then we set success state now
            // Otherwise, the callback host can set it himself when its done with the xodosSession
            if (!executionCommand.isStateFailed())
                executionCommand.setState(ExecutionCommand.ExecutionState.SUCCESS);
        }
    }

    public TerminalSession getTerminalSession() {
        return mTerminalSession;
    }

    public ExecutionCommand getExecutionCommand() {
        return mExecutionCommand;
    }



    public interface xodosSessionClient {

        /**
         * Callback function for when {@link xodosSession} exits.
         *
         * @param xodosSession The {@link xodosSession} that exited.
         */
        void onxodosSessionExited(xodosSession xodosSession);

    }

}
