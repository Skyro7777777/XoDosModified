package com.xodos.shared.xodos.shell.command.environment;

import android.content.Context;
import android.content.pm.PackageInfo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.xodos.shared.android.PackageUtils;
import com.xodos.shared.shell.command.environment.ShellEnvironmentUtils;
import com.xodos.shared.xodos.xodosConstants;
import com.xodos.shared.xodos.xodosUtils;

import java.util.HashMap;

/**
 * Environment for {@link xodosConstants#xodos_API_PACKAGE_NAME} app.
 */
public class xodosAPIShellEnvironment {

    /** Environment variable prefix for the xodos:API app. */
    public static final String xodos_API_APP_ENV_PREFIX = xodosConstants.xodos_ENV_PREFIX_ROOT + "_API_APP__";

    /** Environment variable for the xodos:API app version. */
    public static final String ENV_xodos_API_APP__VERSION_NAME = xodos_API_APP_ENV_PREFIX + "VERSION_NAME";

    /** Get shell environment for xodos:API app. */
    @Nullable
    public static HashMap<String, String> getEnvironment(@NonNull Context currentPackageContext) {
        if (xodosUtils.isxodosAPIAppInstalled(currentPackageContext) != null) return null;

        String packageName = xodosConstants.xodos_API_PACKAGE_NAME;
        PackageInfo packageInfo = PackageUtils.getPackageInfoForPackage(currentPackageContext, packageName);
        if (packageInfo == null) return null;

        HashMap<String, String> environment = new HashMap<>();

        ShellEnvironmentUtils.putToEnvIfSet(environment, ENV_xodos_API_APP__VERSION_NAME, PackageUtils.getVersionNameForPackage(packageInfo));

        return environment;
    }

}
