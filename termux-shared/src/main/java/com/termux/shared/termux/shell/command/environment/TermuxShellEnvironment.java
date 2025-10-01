package com.xodos.shared.xodos.shell.command.environment;

import android.content.Context;

import androidx.annotation.NonNull;

import com.xodos.shared.errors.Error;
import com.xodos.shared.file.FileUtils;
import com.xodos.shared.logger.Logger;
import com.xodos.shared.shell.command.ExecutionCommand;
import com.xodos.shared.shell.command.environment.AndroidShellEnvironment;
import com.xodos.shared.shell.command.environment.ShellEnvironmentUtils;
import com.xodos.shared.shell.command.environment.ShellCommandShellEnvironment;
import com.xodos.shared.xodos.xodosBootstrap;
import com.xodos.shared.xodos.xodosConstants;
import com.xodos.shared.xodos.shell.xodosShellUtils;

import java.nio.charset.Charset;
import java.util.HashMap;

/**
 * Environment for xodos.
 */
public class xodosShellEnvironment extends AndroidShellEnvironment {

    private static final String LOG_TAG = "xodosShellEnvironment";

    /** Environment variable for the xodos {@link xodosConstants#xodos_PREFIX_DIR_PATH}. */
    public static final String ENV_PREFIX = "PREFIX";

    public xodosShellEnvironment() {
        super();
        shellCommandShellEnvironment = new xodosShellCommandShellEnvironment();
    }


    /** Init {@link xodosShellEnvironment} constants and caches. */
    public synchronized static void init(@NonNull Context currentPackageContext) {
        xodosAppShellEnvironment.setxodosAppEnvironment(currentPackageContext);
    }

    /** Init {@link xodosShellEnvironment} constants and caches. */
    public synchronized static void writeEnvironmentToFile(@NonNull Context currentPackageContext) {
        HashMap<String, String> environmentMap = new xodosShellEnvironment().getEnvironment(currentPackageContext, false);
        String environmentString = ShellEnvironmentUtils.convertEnvironmentToDotEnvFile(environmentMap);

        // Write environment string to temp file and then move to final location since otherwise
        // writing may happen while file is being sourced/read
        Error error = FileUtils.writeTextToFile("xodos.env.tmp", xodosConstants.xodos_ENV_TEMP_FILE_PATH,
            Charset.defaultCharset(), environmentString, false);
        if (error != null) {
            Logger.logErrorExtended(LOG_TAG, error.toString());
            return;
        }

        error = FileUtils.moveRegularFile("xodos.env.tmp", xodosConstants.xodos_ENV_TEMP_FILE_PATH, xodosConstants.xodos_ENV_FILE_PATH, true);
        if (error != null) {
            Logger.logErrorExtended(LOG_TAG, error.toString());
        }
    }

    /** Get shell environment for xodos. */
    @NonNull
    @Override
    public HashMap<String, String> getEnvironment(@NonNull Context currentPackageContext, boolean isFailSafe) {

        // xodos environment builds upon the Android environment
        HashMap<String, String> environment = super.getEnvironment(currentPackageContext, isFailSafe);

        HashMap<String, String> xodosAppEnvironment = xodosAppShellEnvironment.getEnvironment(currentPackageContext);
        if (xodosAppEnvironment != null)
            environment.putAll(xodosAppEnvironment);

        HashMap<String, String> xodosApiAppEnvironment = xodosAPIShellEnvironment.getEnvironment(currentPackageContext);
        if (xodosApiAppEnvironment != null)
            environment.putAll(xodosApiAppEnvironment);

        environment.put(ENV_HOME, xodosConstants.xodos_HOME_DIR_PATH);
        environment.put(ENV_PREFIX, xodosConstants.xodos_PREFIX_DIR_PATH);

        // If failsafe is not enabled, then we keep default PATH and TMPDIR so that system binaries can be used
        if (!isFailSafe) {
            environment.put(ENV_TMPDIR, xodosConstants.xodos_TMP_PREFIX_DIR_PATH);
            if (xodosBootstrap.isAppPackageVariantAPTAndroid5()) {
                // xodos in android 5/6 era shipped busybox binaries in applets directory
                environment.put(ENV_PATH, xodosConstants.xodos_BIN_PREFIX_DIR_PATH + ":" + xodosConstants.xodos_BIN_PREFIX_DIR_PATH + "/applets");
                environment.put(ENV_LD_LIBRARY_PATH, xodosConstants.xodos_LIB_PREFIX_DIR_PATH);
            } else {
                // xodos binaries on Android 7+ rely on DT_RUNPATH, so LD_LIBRARY_PATH should be unset by default
                environment.put(ENV_PATH, xodosConstants.xodos_BIN_PREFIX_DIR_PATH);
                environment.remove(ENV_LD_LIBRARY_PATH);
            }
        }

        return environment;
    }


    @NonNull
    @Override
    public String getDefaultWorkingDirectoryPath() {
        return xodosConstants.xodos_HOME_DIR_PATH;
    }

    @NonNull
    @Override
    public String getDefaultBinPath() {
        return xodosConstants.xodos_BIN_PREFIX_DIR_PATH;
    }

    @NonNull
    @Override
    public String[] setupShellCommandArguments(@NonNull String executable, String[] arguments) {
        return xodosShellUtils.setupShellCommandArguments(executable, arguments);
    }

}
