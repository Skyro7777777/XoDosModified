package com.xodos.shared.xodos.shell.command.environment;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.xodos.shared.android.PackageUtils;
import com.xodos.shared.android.SELinuxUtils;
import com.xodos.shared.data.DataUtils;
import com.xodos.shared.shell.command.environment.ShellEnvironmentUtils;
import com.xodos.shared.xodos.xodosBootstrap;
import com.xodos.shared.xodos.xodosConstants;
import com.xodos.shared.xodos.xodosUtils;
import com.xodos.shared.xodos.shell.am.xodosAmSocketServer;

import java.util.HashMap;

/**
 * Environment for {@link xodosConstants#xodos_PACKAGE_NAME} app.
 */
public class xodosAppShellEnvironment {

    /** xodos app environment variables. */
    public static HashMap<String, String> xodosAppEnvironment;

    /** Environment variable for the xodos app version. */
    public static final String ENV_xodos_VERSION = xodosConstants.xodos_ENV_PREFIX_ROOT + "_VERSION";

    /** Environment variable prefix for the xodos app. */
    public static final String xodos_APP_ENV_PREFIX = xodosConstants.xodos_ENV_PREFIX_ROOT + "_APP__";

    /** Environment variable for the xodos app version name. */
    public static final String ENV_xodos_APP__VERSION_NAME = xodos_APP_ENV_PREFIX + "VERSION_NAME";
    /** Environment variable for the xodos app version code. */
    public static final String ENV_xodos_APP__VERSION_CODE = xodos_APP_ENV_PREFIX + "VERSION_CODE";
    /** Environment variable for the xodos app package name. */
    public static final String ENV_xodos_APP__PACKAGE_NAME = xodos_APP_ENV_PREFIX + "PACKAGE_NAME";
    /** Environment variable for the xodos app process id. */
    public static final String ENV_xodos_APP__PID = xodos_APP_ENV_PREFIX + "PID";
    /** Environment variable for the xodos app uid. */
    public static final String ENV_xodos_APP__UID = xodos_APP_ENV_PREFIX + "UID";
    /** Environment variable for the xodos app targetSdkVersion. */
    public static final String ENV_xodos_APP__TARGET_SDK = xodos_APP_ENV_PREFIX + "TARGET_SDK";
    /** Environment variable for the xodos app is debuggable apk build. */
    public static final String ENV_xodos_APP__IS_DEBUGGABLE_BUILD = xodos_APP_ENV_PREFIX + "IS_DEBUGGABLE_BUILD";
    /** Environment variable for the xodos app {@link xodosConstants} APK_RELEASE_*. */
    public static final String ENV_xodos_APP__APK_RELEASE = xodos_APP_ENV_PREFIX + "APK_RELEASE";
    /** Environment variable for the xodos app install path. */
    public static final String ENV_xodos_APP__APK_PATH = xodos_APP_ENV_PREFIX + "APK_PATH";
    /** Environment variable for the xodos app is installed on external/portable storage. */
    public static final String ENV_xodos_APP__IS_INSTALLED_ON_EXTERNAL_STORAGE = xodos_APP_ENV_PREFIX + "IS_INSTALLED_ON_EXTERNAL_STORAGE";

    /** Environment variable for the xodos app process selinux context. */
    public static final String ENV_xodos_APP__SE_PROCESS_CONTEXT = xodos_APP_ENV_PREFIX + "SE_PROCESS_CONTEXT";
    /** Environment variable for the xodos app data files selinux context. */
    public static final String ENV_xodos_APP__SE_FILE_CONTEXT = xodos_APP_ENV_PREFIX + "SE_FILE_CONTEXT";
    /** Environment variable for the xodos app seInfo tag found in selinux policy used to set app process and app data files selinux context. */
    public static final String ENV_xodos_APP__SE_INFO = xodos_APP_ENV_PREFIX + "SE_INFO";
    /** Environment variable for the xodos app user id. */
    public static final String ENV_xodos_APP__USER_ID = xodos_APP_ENV_PREFIX + "USER_ID";
    /** Environment variable for the xodos app profile owner. */
    public static final String ENV_xodos_APP__PROFILE_OWNER = xodos_APP_ENV_PREFIX + "PROFILE_OWNER";

    /** Environment variable for the xodos app {@link xodosBootstrap#xodos_APP_PACKAGE_MANAGER}. */
    public static final String ENV_xodos_APP__PACKAGE_MANAGER = xodos_APP_ENV_PREFIX + "PACKAGE_MANAGER";
    /** Environment variable for the xodos app {@link xodosBootstrap#xodos_APP_PACKAGE_VARIANT}. */
    public static final String ENV_xodos_APP__PACKAGE_VARIANT = xodos_APP_ENV_PREFIX + "PACKAGE_VARIANT";
    /** Environment variable for the xodos app files directory. */
    public static final String ENV_xodos_APP__FILES_DIR = xodos_APP_ENV_PREFIX + "FILES_DIR";


    /** Environment variable for the xodos app {@link xodosAmSocketServer#getxodosAppAMSocketServerEnabled(Context)}. */
    public static final String ENV_xodos_APP__AM_SOCKET_SERVER_ENABLED = xodos_APP_ENV_PREFIX + "AM_SOCKET_SERVER_ENABLED";



    /** Get shell environment for xodos app. */
    @Nullable
    public static HashMap<String, String> getEnvironment(@NonNull Context currentPackageContext) {
        setxodosAppEnvironment(currentPackageContext);
        return xodosAppEnvironment;
    }

    /** Set xodos app environment variables in {@link #xodosAppEnvironment}. */
    public synchronized static void setxodosAppEnvironment(@NonNull Context currentPackageContext) {
        boolean isxodosApp = xodosConstants.xodos_PACKAGE_NAME.equals(currentPackageContext.getPackageName());

        // If current package context is of xodos app and its environment is already set, then no need to set again since it won't change
        // Other apps should always set environment again since xodos app may be installed/updated/deleted in background
        if (xodosAppEnvironment != null && isxodosApp)
            return;

        xodosAppEnvironment = null;

        String packageName = xodosConstants.xodos_PACKAGE_NAME;
        PackageInfo packageInfo = PackageUtils.getPackageInfoForPackage(currentPackageContext, packageName);
        if (packageInfo == null) return;
        ApplicationInfo applicationInfo = PackageUtils.getApplicationInfoForPackage(currentPackageContext, packageName);
        if (applicationInfo == null || !applicationInfo.enabled) return;

        HashMap<String, String> environment = new HashMap<>();

        ShellEnvironmentUtils.putToEnvIfSet(environment, ENV_xodos_VERSION, PackageUtils.getVersionNameForPackage(packageInfo));
        ShellEnvironmentUtils.putToEnvIfSet(environment, ENV_xodos_APP__VERSION_NAME, PackageUtils.getVersionNameForPackage(packageInfo));
        ShellEnvironmentUtils.putToEnvIfSet(environment, ENV_xodos_APP__VERSION_CODE, String.valueOf(PackageUtils.getVersionCodeForPackage(packageInfo)));

        ShellEnvironmentUtils.putToEnvIfSet(environment, ENV_xodos_APP__PACKAGE_NAME, packageName);
        ShellEnvironmentUtils.putToEnvIfSet(environment, ENV_xodos_APP__PID, xodosUtils.getxodosAppPID(currentPackageContext));
        ShellEnvironmentUtils.putToEnvIfSet(environment, ENV_xodos_APP__UID, String.valueOf(PackageUtils.getUidForPackage(applicationInfo)));
        ShellEnvironmentUtils.putToEnvIfSet(environment, ENV_xodos_APP__TARGET_SDK, String.valueOf(PackageUtils.getTargetSDKForPackage(applicationInfo)));
        ShellEnvironmentUtils.putToEnvIfSet(environment, ENV_xodos_APP__IS_DEBUGGABLE_BUILD, PackageUtils.isAppForPackageADebuggableBuild(applicationInfo));
        ShellEnvironmentUtils.putToEnvIfSet(environment, ENV_xodos_APP__APK_PATH, PackageUtils.getBaseAPKPathForPackage(applicationInfo));
        ShellEnvironmentUtils.putToEnvIfSet(environment, ENV_xodos_APP__IS_INSTALLED_ON_EXTERNAL_STORAGE, PackageUtils.isAppInstalledOnExternalStorage(applicationInfo));

        putxodosAPKSignature(currentPackageContext, environment);

        Context xodosPackageContext = xodosUtils.getxodosPackageContext(currentPackageContext);
        if (xodosPackageContext != null) {
            // An app that does not have the same sharedUserId as xodos app will not be able to get
            // get xodos context's classloader to get BuildConfig.xodos_PACKAGE_VARIANT via reflection.
            // Check xodosBootstrap.setxodosPackageManagerAndVariantFromxodosApp()
            if (xodosBootstrap.xodos_APP_PACKAGE_MANAGER != null)
                environment.put(ENV_xodos_APP__PACKAGE_MANAGER, xodosBootstrap.xodos_APP_PACKAGE_MANAGER.getName());
            if (xodosBootstrap.xodos_APP_PACKAGE_VARIANT != null)
                environment.put(ENV_xodos_APP__PACKAGE_VARIANT, xodosBootstrap.xodos_APP_PACKAGE_VARIANT.getName());

            // Will not be set for plugins
            ShellEnvironmentUtils.putToEnvIfSet(environment, ENV_xodos_APP__AM_SOCKET_SERVER_ENABLED,
                xodosAmSocketServer.getxodosAppAMSocketServerEnabled(currentPackageContext));

            String filesDirPath = currentPackageContext.getFilesDir().getAbsolutePath();
            ShellEnvironmentUtils.putToEnvIfSet(environment, ENV_xodos_APP__FILES_DIR, filesDirPath);

            ShellEnvironmentUtils.putToEnvIfSet(environment, ENV_xodos_APP__SE_PROCESS_CONTEXT, SELinuxUtils.getContext());
            ShellEnvironmentUtils.putToEnvIfSet(environment, ENV_xodos_APP__SE_FILE_CONTEXT, SELinuxUtils.getFileContext(filesDirPath));

            String seInfoUser = PackageUtils.getApplicationInfoSeInfoUserForPackage(applicationInfo);
            ShellEnvironmentUtils.putToEnvIfSet(environment, ENV_xodos_APP__SE_INFO, PackageUtils.getApplicationInfoSeInfoForPackage(applicationInfo) +
                (DataUtils.isNullOrEmpty(seInfoUser) ? "" : seInfoUser));

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                ShellEnvironmentUtils.putToEnvIfSet(environment, ENV_xodos_APP__USER_ID, String.valueOf(PackageUtils.getUserIdForPackage(currentPackageContext)));
            ShellEnvironmentUtils.putToEnvIfSet(environment, ENV_xodos_APP__PROFILE_OWNER, PackageUtils.getProfileOwnerPackageNameForUser(currentPackageContext));
        }

        xodosAppEnvironment = environment;
    }

    /** Put {@link #ENV_xodos_APP__APK_RELEASE} in {@code environment}. */
    public static void putxodosAPKSignature(@NonNull Context currentPackageContext,
                                             @NonNull HashMap<String, String> environment) {
        String signingCertificateSHA256Digest = PackageUtils.getSigningCertificateSHA256DigestForPackage(currentPackageContext,
            xodosConstants.xodos_PACKAGE_NAME);
        if (signingCertificateSHA256Digest != null) {
            ShellEnvironmentUtils.putToEnvIfSet(environment, ENV_xodos_APP__APK_RELEASE,
                xodosUtils.getAPKRelease(signingCertificateSHA256Digest).replaceAll("[^a-zA-Z]", "_").toUpperCase());
        }
    }

    /** Update {@link #ENV_xodos_APP__AM_SOCKET_SERVER_ENABLED} value in {@code environment}. */
    public synchronized static void updatexodosAppAMSocketServerEnabled(@NonNull Context currentPackageContext) {
        if (xodosAppEnvironment == null) return;
        xodosAppEnvironment.remove(ENV_xodos_APP__AM_SOCKET_SERVER_ENABLED);
        ShellEnvironmentUtils.putToEnvIfSet(xodosAppEnvironment, ENV_xodos_APP__AM_SOCKET_SERVER_ENABLED,
            xodosAmSocketServer.getxodosAppAMSocketServerEnabled(currentPackageContext));
    }

}
