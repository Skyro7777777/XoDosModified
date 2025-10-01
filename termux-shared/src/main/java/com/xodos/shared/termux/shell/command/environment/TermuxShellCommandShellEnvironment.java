package com.xodos.shared.xodos.shell.command.environment;

import android.content.Context;

import androidx.annotation.NonNull;

import com.xodos.shared.shell.command.ExecutionCommand;
import com.xodos.shared.shell.command.environment.ShellCommandShellEnvironment;
import com.xodos.shared.shell.command.environment.ShellEnvironmentUtils;
import com.xodos.shared.xodos.settings.preferences.xodosAppSharedPreferences;
import com.xodos.shared.xodos.shell.xodosShellManager;

import java.util.HashMap;

/**
 * Environment for xodos {@link ExecutionCommand}.
 */
public class xodosShellCommandShellEnvironment extends ShellCommandShellEnvironment {

    /** Get shell environment containing info for xodos {@link ExecutionCommand}. */
    @NonNull
    @Override
    public HashMap<String, String> getEnvironment(@NonNull Context currentPackageContext,
                                                  @NonNull ExecutionCommand executionCommand) {
        HashMap<String, String> environment = super.getEnvironment(currentPackageContext, executionCommand);

        xodosAppSharedPreferences preferences = xodosAppSharedPreferences.build(currentPackageContext);
        if (preferences == null) return environment;

        if (ExecutionCommand.Runner.APP_SHELL.equalsRunner(executionCommand.runner)) {
            ShellEnvironmentUtils.putToEnvIfSet(environment, ENV_SHELL_CMD__APP_SHELL_NUMBER_SINCE_BOOT,
                String.valueOf(preferences.getAndIncrementAppShellNumberSinceBoot()));
            ShellEnvironmentUtils.putToEnvIfSet(environment, ENV_SHELL_CMD__APP_SHELL_NUMBER_SINCE_APP_START,
                String.valueOf(xodosShellManager.getAndIncrementAppShellNumberSinceAppStart()));

        } else if (ExecutionCommand.Runner.TERMINAL_SESSION.equalsRunner(executionCommand.runner)) {
            ShellEnvironmentUtils.putToEnvIfSet(environment, ENV_SHELL_CMD__TERMINAL_SESSION_NUMBER_SINCE_BOOT,
                String.valueOf(preferences.getAndIncrementTerminalSessionNumberSinceBoot()));
            ShellEnvironmentUtils.putToEnvIfSet(environment, ENV_SHELL_CMD__TERMINAL_SESSION_NUMBER_SINCE_APP_START,
                String.valueOf(xodosShellManager.getAndIncrementTerminalSessionNumberSinceAppStart()));
        } else {
            return environment;
        }

        return environment;
    }

}
