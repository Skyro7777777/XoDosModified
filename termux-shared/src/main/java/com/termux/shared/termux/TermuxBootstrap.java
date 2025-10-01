package com.xodos.shared.xodos;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.xodos.shared.logger.Logger;
import com.xodos.shared.xodos.xodosConstants.xodos_APP;

public class xodosBootstrap {

    private static final String LOG_TAG = "xodosBootstrap";

    /** The field name used by xodos app to store package variant in
     * {@link xodos_APP#BUILD_CONFIG_CLASS_NAME} class. */
    public static final String BUILD_CONFIG_FIELD_xodos_PACKAGE_VARIANT = "xodos_PACKAGE_VARIANT";


    /** The {@link PackageManager} for the bootstrap in the app APK added in app/build.gradle. */
    public static PackageManager xodos_APP_PACKAGE_MANAGER;

    /** The {@link PackageVariant} for the bootstrap in the app APK added in app/build.gradle. */
    public static PackageVariant xodos_APP_PACKAGE_VARIANT;

    /** Set {@link #xodos_APP_PACKAGE_VARIANT} and {@link #xodos_APP_PACKAGE_MANAGER} from {@code packageVariantName} passed. */
    public static void setxodosPackageManagerAndVariant(@Nullable String packageVariantName) {
        xodos_APP_PACKAGE_VARIANT = PackageVariant.variantOf(packageVariantName);
        if (xodos_APP_PACKAGE_VARIANT == null) {
            throw new RuntimeException("Unsupported xodos_APP_PACKAGE_VARIANT \"" + packageVariantName + "\"");
        }

        Logger.logVerbose(LOG_TAG, "Set xodos_APP_PACKAGE_VARIANT to \"" + xodos_APP_PACKAGE_VARIANT + "\"");

        // Set packageManagerName to substring before first dash "-" in packageVariantName
        int index = packageVariantName.indexOf('-');
        String packageManagerName = (index == -1) ? null : packageVariantName.substring(0, index);
        xodos_APP_PACKAGE_MANAGER = PackageManager.managerOf(packageManagerName);
        if (xodos_APP_PACKAGE_MANAGER == null) {
            throw new RuntimeException("Unsupported xodos_APP_PACKAGE_MANAGER \"" + packageManagerName + "\" with variant \"" + packageVariantName + "\"");
        }

        Logger.logVerbose(LOG_TAG, "Set xodos_APP_PACKAGE_MANAGER to \"" + xodos_APP_PACKAGE_MANAGER + "\"");
    }

    /**
     * Set {@link #xodos_APP_PACKAGE_VARIANT} and {@link #xodos_APP_PACKAGE_MANAGER} with the
     * {@link #BUILD_CONFIG_FIELD_xodos_PACKAGE_VARIANT} field value from the
     * {@link xodos_APP#BUILD_CONFIG_CLASS_NAME} class of the xodos app APK installed on the device.
     * This can only be used by apps that share `sharedUserId` with the xodos app and can be used
     * by plugin apps.
     *
     * @param currentPackageContext The context of current package.
     */
    public static void setxodosPackageManagerAndVariantFromxodosApp(@NonNull Context currentPackageContext) {
        String packageVariantName = getxodosAppBuildConfigPackageVariantFromxodosApp(currentPackageContext);
        if (packageVariantName != null) {
            xodosBootstrap.setxodosPackageManagerAndVariant(packageVariantName);
        } else {
            Logger.logError(LOG_TAG, "Failed to set xodos_APP_PACKAGE_VARIANT and xodos_APP_PACKAGE_MANAGER from the xodos app");
        }
    }

    /**
     * Get {@link #BUILD_CONFIG_FIELD_xodos_PACKAGE_VARIANT} field value from the
     * {@link xodos_APP#BUILD_CONFIG_CLASS_NAME} class of the xodos app APK installed on the device.
     * This can only be used by apps that share `sharedUserId` with the xodos app.
     *
     * @param currentPackageContext The context of current package.
     * @return Returns the field value, otherwise {@code null} if an exception was raised or failed
     * to get xodos app package context.
     */
    public static String getxodosAppBuildConfigPackageVariantFromxodosApp(@NonNull Context currentPackageContext) {
        try {
            return (String) xodosUtils.getxodosAppAPKBuildConfigClassField(currentPackageContext, BUILD_CONFIG_FIELD_xodos_PACKAGE_VARIANT);
        } catch (Exception e) {
            Logger.logStackTraceWithMessage(LOG_TAG, "Failed to get \"" + BUILD_CONFIG_FIELD_xodos_PACKAGE_VARIANT + "\" value from \"" + xodos_APP.BUILD_CONFIG_CLASS_NAME + "\" class", e);
            return null;
        }
    }



    /** Is {@link PackageManager#APT} set as {@link #xodos_APP_PACKAGE_MANAGER}. */
    public static boolean isAppPackageManagerAPT() {
        return PackageManager.APT.equals(xodos_APP_PACKAGE_MANAGER);
    }

    ///** Is {@link PackageManager#TAPM} set as {@link #xodos_APP_PACKAGE_MANAGER}. */
    //public static boolean isAppPackageManagerTAPM() {
    //    return PackageManager.TAPM.equals(xodos_APP_PACKAGE_MANAGER);
    //}

    ///** Is {@link PackageManager#PACMAN} set as {@link #xodos_APP_PACKAGE_MANAGER}. */
    //public static boolean isAppPackageManagerPACMAN() {
    //    return PackageManager.PACMAN.equals(xodos_APP_PACKAGE_MANAGER);
    //}



    /** Is {@link PackageVariant#APT_ANDROID_7} set as {@link #xodos_APP_PACKAGE_VARIANT}. */
    public static boolean isAppPackageVariantAPTAndroid7() {
        return PackageVariant.APT_ANDROID_7.equals(xodos_APP_PACKAGE_VARIANT);
    }

    /** Is {@link PackageVariant#APT_ANDROID_5} set as {@link #xodos_APP_PACKAGE_VARIANT}. */
    public static boolean isAppPackageVariantAPTAndroid5() {
        return PackageVariant.APT_ANDROID_5.equals(xodos_APP_PACKAGE_VARIANT);
    }

    ///** Is {@link PackageVariant#TAPM_ANDROID_7} set as {@link #xodos_APP_PACKAGE_VARIANT}. */
    //public static boolean isAppPackageVariantTAPMAndroid7() {
    //    return PackageVariant.TAPM_ANDROID_7.equals(xodos_APP_PACKAGE_VARIANT);
    //}

    ///** Is {@link PackageVariant#PACMAN_ANDROID_7} set as {@link #xodos_APP_PACKAGE_VARIANT}. */
    //public static boolean isAppPackageVariantTPACMANAndroid7() {
    //    return PackageVariant.PACMAN_ANDROID_7.equals(xodos_APP_PACKAGE_VARIANT);
    //}



    /** xodos package manager. */
    public enum PackageManager {

        /**
         * Advanced Package Tool (APT) for managing debian deb package files.
         * https://wiki.debian.org/Apt
         * https://wiki.debian.org/deb
         */
        APT("apt");

        ///**
        // * xodos Android Package Manager (TAPM) for managing xodos apk package files.
        // * https://en.wikipedia.org/wiki/Apk_(file_format)
        // */
        //TAPM("tapm");

        ///**
        // * Package Manager (PACMAN) for managing arch linux pkg.tar package files.
        // * https://wiki.archlinux.org/title/pacman
        // * https://en.wikipedia.org/wiki/Arch_Linux#Pacman
        // */
        //PACMAN("pacman");

        private final String name;

        PackageManager(final String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public boolean equalsManager(String manager) {
            return manager != null && manager.equals(this.name);
        }

        /** Get {@link PackageManager} for {@code name} if found, otherwise {@code null}. */
        @Nullable
        public static PackageManager managerOf(String name) {
            if (name == null || name.isEmpty()) return null;
            for (PackageManager v : PackageManager.values()) {
                if (v.name.equals(name)) {
                    return v;
                }
            }
            return null;
        }

    }



    /** xodos package variant. The substring before first dash "-" must match one of the {@link PackageManager}. */
    public enum PackageVariant {

        /** {@link PackageManager#APT} variant for Android 7+. */
        APT_ANDROID_7("apt-android-7"),

        /** {@link PackageManager#APT} variant for Android 5+. */
        APT_ANDROID_5("apt-android-5");

        ///** {@link PackageManager#TAPM} variant for Android 7+. */
        //TAPM_ANDROID_7("tapm-android-7");

        ///** {@link PackageManager#PACMAN} variant for Android 7+. */
        //PACMAN_ANDROID_7("pacman-android-7");

        private final String name;

        PackageVariant(final String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public boolean equalsVariant(String variant) {
            return variant != null && variant.equals(this.name);
        }

        /** Get {@link PackageVariant} for {@code name} if found, otherwise {@code null}. */
        @Nullable
        public static PackageVariant variantOf(String name) {
            if (name == null || name.isEmpty()) return null;
            for (PackageVariant v : PackageVariant.values()) {
                if (v.name.equals(name)) {
                    return v;
                }
            }
            return null;
        }

    }

}
