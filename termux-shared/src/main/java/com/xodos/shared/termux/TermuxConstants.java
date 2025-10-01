package com.xodos.shared.xodos;

import android.annotation.SuppressLint;

import com.xodos.shared.shell.command.ExecutionCommand;
import com.xodos.shared.shell.command.ExecutionCommand.Runner;

import java.io.File;
import java.util.Arrays;
import java.util.Formatter;
import java.util.List;

/*
 * Version: v0.52.0
 * SPDX-License-Identifier: MIT
 *
 * Changelog
 *
 * - 0.1.0 (2021-03-08)
 *      - Initial Release.
 *
 * - 0.2.0 (2021-03-11)
 *      - Added `_DIR` and `_FILE` substrings to paths.
 *      - Added `INTERNAL_PRIVATE_APP_DATA_DIR*`, `xodos_CACHE_DIR*`, `xodos_DATABASES_DIR*`,
 *          `xodos_SHARED_PREFERENCES_DIR*`, `xodos_BIN_PREFIX_DIR*`, `xodos_ETC_DIR*`,
 *          `xodos_INCLUDE_DIR*`, `xodos_LIB_DIR*`, `xodos_LIBEXEC_DIR*`, `xodos_SHARE_DIR*`,
 *          `xodos_TMP_DIR*`, `xodos_VAR_DIR*`, `xodos_STAGING_PREFIX_DIR*`,
 *          `xodos_STORAGE_HOME_DIR*`, `xodos_DEFAULT_PREFERENCES_FILE_BASENAME*`,
 *          `xodos_DEFAULT_PREFERENCES_FILE`.
 *      - Renamed `DATA_HOME_PATH` to `xodos_DATA_HOME_DIR_PATH`.
 *      - Renamed `CONFIG_HOME_PATH` to `xodos_CONFIG_HOME_DIR_PATH`.
 *      - Updated javadocs and spacing.
 *
 * - 0.3.0 (2021-03-12)
 *      - Remove `xodos_CACHE_DIR_PATH*`, `xodos_DATABASES_DIR_PATH*`,
 *          `xodos_SHARED_PREFERENCES_DIR_PATH*` since they may not be consistent on all devices.
 *      - Renamed `xodos_DEFAULT_PREFERENCES_FILE_BASENAME` to
 *          `xodos_DEFAULT_PREFERENCES_FILE_BASENAME_WITHOUT_EXTENSION`. This should be used for
 *           accessing shared preferences between xodos app and its plugins if ever needed by first
 *           getting shared package context with {@link Context.createPackageContext(String,int}).
 *
 * - 0.4.0 (2021-03-16)
 *      - Added `BROADCAST_xodos_OPENED`,
 *          `xodos_API_DEFAULT_PREFERENCES_FILE_BASENAME_WITHOUT_EXTENSION`
 *          `xodos_BOOT_DEFAULT_PREFERENCES_FILE_BASENAME_WITHOUT_EXTENSION`,
 *          `xodos_FLOAT_DEFAULT_PREFERENCES_FILE_BASENAME_WITHOUT_EXTENSION`,
 *          `xodos_STYLING_DEFAULT_PREFERENCES_FILE_BASENAME_WITHOUT_EXTENSION`,
 *          `xodos_TASKER_DEFAULT_PREFERENCES_FILE_BASENAME_WITHOUT_EXTENSION`,
 *          `xodos_WIDGET_DEFAULT_PREFERENCES_FILE_BASENAME_WITHOUT_EXTENSION`.
 *
 * - 0.5.0 (2021-03-16)
 *      - Renamed "xodos Plugin app" labels to "xodos:Tasker app".
 *
 * - 0.6.0 (2021-03-16)
 *      - Added `xodos_FILE_SHARE_URI_AUTHORITY`.
 *
 * - 0.7.0 (2021-03-17)
 *      - Fixed javadocs.
 *
 * - 0.8.0 (2021-03-18)
 *      - Fixed Intent extra types javadocs.
 *      - Added following to `xodos_SERVICE`:
 *          `EXTRA_PENDING_INTENT`, `EXTRA_RESULT_BUNDLE`,
 *          `EXTRA_STDOUT`, `EXTRA_STDERR`, `EXTRA_EXIT_CODE`,
 *          `EXTRA_ERR`, `EXTRA_ERRMSG`.
 *
 * - 0.9.0 (2021-03-18)
 *      - Fixed javadocs.
 *
 * - 0.10.0 (2021-03-19)
 *      - Added following to `xodos_SERVICE`:
 *          `EXTRA_SESSION_ACTION`,
 *          `VALUE_EXTRA_SESSION_ACTION_SWITCH_TO_NEW_SESSION_AND_OPEN_ACTIVITY`,
 *          `VALUE_EXTRA_SESSION_ACTION_KEEP_CURRENT_SESSION_AND_OPEN_ACTIVITY`,
 *          `VALUE_EXTRA_SESSION_ACTION_SWITCH_TO_NEW_SESSION_AND_DONT_OPEN_ACTIVITY`
 *          `VALUE_EXTRA_SESSION_ACTION_KEEP_CURRENT_SESSION_AND_DONT_OPEN_ACTIVITY`.
 *      - Added following to `RUN_COMMAND_SERVICE`:
 *          `EXTRA_SESSION_ACTION`.
 *
 * - 0.11.0 (2021-03-24)
 *      - Added following to `xodos_SERVICE`:
 *          `EXTRA_COMMAND_LABEL`, `EXTRA_COMMAND_DESCRIPTION`, `EXTRA_COMMAND_HELP`, `EXTRA_PLUGIN_API_HELP`.
 *      - Added following to `RUN_COMMAND_SERVICE`:
 *          `EXTRA_COMMAND_LABEL`, `EXTRA_COMMAND_DESCRIPTION`, `EXTRA_COMMAND_HELP`.
 *      - Updated `RESULT_BUNDLE` related extras with `PLUGIN_RESULT_BUNDLE` prefixes.
 *
 * - 0.12.0 (2021-03-25)
 *      - Added following to `xodos_SERVICE`:
 *          `EXTRA_PLUGIN_RESULT_BUNDLE_STDOUT_ORIGINAL_LENGTH`,
 *          `EXTRA_PLUGIN_RESULT_BUNDLE_STDERR_ORIGINAL_LENGTH`.
 *
 * - 0.13.0 (2021-03-25)
 *      - Added following to `RUN_COMMAND_SERVICE`:
 *          `EXTRA_PENDING_INTENT`.
 *
 * - 0.14.0 (2021-03-25)
 *      - Added `FDROID_PACKAGES_BASE_URL`,
 *          `xodos_GITHUB_ORGANIZATION_NAME`, `xodos_GITHUB_ORGANIZATION_URL`,
 *          `xodos_GITHUB_REPO_NAME`, `xodos_GITHUB_REPO_URL`, `xodos_FDROID_PACKAGE_URL`,
 *          `xodos_API_GITHUB_REPO_NAME`,`xodos_API_GITHUB_REPO_URL`, `xodos_API_FDROID_PACKAGE_URL`,
 *          `xodos_BOOT_GITHUB_REPO_NAME`, `xodos_BOOT_GITHUB_REPO_URL`, `xodos_BOOT_FDROID_PACKAGE_URL`,
 *          `xodos_FLOAT_GITHUB_REPO_NAME`, `xodos_FLOAT_GITHUB_REPO_URL`, `xodos_FLOAT_FDROID_PACKAGE_URL`,
 *          `xodos_STYLING_GITHUB_REPO_NAME`, `xodos_STYLING_GITHUB_REPO_URL`, `xodos_STYLING_FDROID_PACKAGE_URL`,
 *          `xodos_TASKER_GITHUB_REPO_NAME`, `xodos_TASKER_GITHUB_REPO_URL`, `xodos_TASKER_FDROID_PACKAGE_URL`,
 *          `xodos_WIDGET_GITHUB_REPO_NAME`, `xodos_WIDGET_GITHUB_REPO_URL` `xodos_WIDGET_FDROID_PACKAGE_URL`.
 *
 * - 0.15.0 (2021-04-06)
 *      - Fixed some variables that had `PREFIX_` substring missing in their name.
 *      - Added `xodos_CRASH_LOG_FILE_PATH`, `xodos_CRASH_LOG_BACKUP_FILE_PATH`,
 *          `xodos_GITHUB_ISSUES_REPO_URL`, `xodos_API_GITHUB_ISSUES_REPO_URL`,
 *          `xodos_BOOT_GITHUB_ISSUES_REPO_URL`, `xodos_FLOAT_GITHUB_ISSUES_REPO_URL`,
 *          `xodos_STYLING_GITHUB_ISSUES_REPO_URL`, `xodos_TASKER_GITHUB_ISSUES_REPO_URL`,
 *          `xodos_WIDGET_GITHUB_ISSUES_REPO_URL`,
 *          `xodos_GITHUB_WIKI_REPO_URL`, `xodos_PACKAGES_GITHUB_WIKI_REPO_URL`,
 *          `xodos_PACKAGES_GITHUB_REPO_NAME`, `xodos_PACKAGES_GITHUB_REPO_URL`, `xodos_PACKAGES_GITHUB_ISSUES_REPO_URL`,
 *          `xodos_GAME_PACKAGES_GITHUB_REPO_NAME`, `xodos_GAME_PACKAGES_GITHUB_REPO_URL`, `xodos_GAME_PACKAGES_GITHUB_ISSUES_REPO_URL`,
 *          `xodos_SCIENCE_PACKAGES_GITHUB_REPO_NAME`, `xodos_SCIENCE_PACKAGES_GITHUB_REPO_URL`, `xodos_SCIENCE_PACKAGES_GITHUB_ISSUES_REPO_URL`,
 *          `xodos_ROOT_PACKAGES_GITHUB_REPO_NAME`, `xodos_ROOT_PACKAGES_GITHUB_REPO_URL`, `xodos_ROOT_PACKAGES_GITHUB_ISSUES_REPO_URL`,
 *          `xodos_UNSTABLE_PACKAGES_GITHUB_REPO_NAME`, `xodos_UNSTABLE_PACKAGES_GITHUB_REPO_URL`, `xodos_UNSTABLE_PACKAGES_GITHUB_ISSUES_REPO_URL`,
 *          `xodos_DISPLAY_PACKAGES_GITHUB_REPO_NAME`, `xodos_DISPLAY_PACKAGES_GITHUB_REPO_URL`, `xodos_DISPLAY_PACKAGES_GITHUB_ISSUES_REPO_URL`.
 *      - Added following to `RUN_COMMAND_SERVICE`:
 *          `RUN_COMMAND_API_HELP_URL`.
 *
 * - 0.16.0 (2021-04-06)
 *      - Added `xodos_SUPPORT_EMAIL`, `xodos_SUPPORT_EMAIL_URL`, `xodos_SUPPORT_EMAIL_MAILTO_URL`,
 *          `xodos_REDDIT_SUBREDDIT`, `xodos_REDDIT_SUBREDDIT_URL`.
 *      - The `xodos_SUPPORT_EMAIL_URL` value must be fixed later when email has been set up.
 *
 * - 0.17.0 (2021-04-07)
 *      - Added `xodos_APP_NOTIFICATION_CHANNEL_ID`, `xodos_APP_NOTIFICATION_CHANNEL_NAME`, `xodos_APP_NOTIFICATION_ID`,
 *          `xodos_RUN_COMMAND_NOTIFICATION_CHANNEL_ID`, `xodos_RUN_COMMAND_NOTIFICATION_CHANNEL_NAME`, `xodos_RUN_COMMAND_NOTIFICATION_ID`,
 *          `xodos_PLUGIN_COMMAND_ERRORS_NOTIFICATION_CHANNEL_ID`, `xodos_PLUGIN_COMMAND_ERRORS_NOTIFICATION_CHANNEL_NAME`,
 *          `xodos_CRASH_REPORTS_NOTIFICATION_CHANNEL_ID`, `xodos_CRASH_REPORTS_NOTIFICATION_CHANNEL_NAME`.
 *      - Updated javadocs.
 *
 * - 0.18.0 (2021-04-11)
 *      - Updated `xodos_SUPPORT_EMAIL_URL` to a valid email.
 *      - Removed `xodos_SUPPORT_EMAIL`.
 *
 * - 0.19.0 (2021-04-12)
 *      - Added `xodos_ACTIVITY.ACTION_REQUEST_PERMISSIONS`.
 *      - Added `xodos_SERVICE.EXTRA_STDIN`.
 *      - Added `RUN_COMMAND_SERVICE.EXTRA_STDIN`.
 *      - Deprecated `xodos_ACTIVITY.EXTRA_RELOAD_STYLE`.
 *
 * - 0.20.0 (2021-05-13)
 *      - Added `xodos_WIKI`, `xodos_WIKI_URL`, `xodos_PLUGIN_APP_NAMES_LIST`, `xodos_PLUGIN_APP_PACKAGE_NAMES_LIST`.
 *      - Added `xodos_SETTINGS_ACTIVITY_NAME`.
 *
 * - 0.21.0 (2021-05-13)
 *      - Added `APK_RELEASE_FDROID`, `APK_RELEASE_FDROID_SIGNING_CERTIFICATE_SHA256_DIGEST`,
 *          `APK_RELEASE_GITHUB_DEBUG_BUILD`, `APK_RELEASE_GITHUB_DEBUG_BUILD_SIGNING_CERTIFICATE_SHA256_DIGEST`,
 *          `APK_RELEASE_GOOGLE_PLAYSTORE`, `APK_RELEASE_GOOGLE_PLAYSTORE_SIGNING_CERTIFICATE_SHA256_DIGEST`.
 *
 * - 0.22.0 (2021-05-13)
 *      - Added `xodos_DONATE_URL`.
 *
 * - 0.23.0 (2021-06-12)
 *      - Rename `INTERNAL_PRIVATE_APP_DATA_DIR_PATH` to `xodos_INTERNAL_PRIVATE_APP_DATA_DIR_PATH`.
 *
 * - 0.24.0 (2021-06-27)
 *      - Add `COMMA_NORMAL`, `COMMA_ALTERNATIVE`.
 *      - Added following to `xodos_APP.xodos_SERVICE`:
 *          `EXTRA_RESULT_DIRECTORY`, `EXTRA_RESULT_SINGLE_FILE`, `EXTRA_RESULT_FILE_BASENAME`,
 *          `EXTRA_RESULT_FILE_OUTPUT_FORMAT`, `EXTRA_RESULT_FILE_ERROR_FORMAT`, `EXTRA_RESULT_FILES_SUFFIX`.
 *      - Added following to `xodos_APP.RUN_COMMAND_SERVICE`:
 *          `EXTRA_RESULT_DIRECTORY`, `EXTRA_RESULT_SINGLE_FILE`, `EXTRA_RESULT_FILE_BASENAME`,
 *          `EXTRA_RESULT_FILE_OUTPUT_FORMAT`, `EXTRA_RESULT_FILE_ERROR_FORMAT`, `EXTRA_RESULT_FILES_SUFFIX`,
 *          `EXTRA_REPLACE_COMMA_ALTERNATIVE_CHARS_IN_ARGUMENTS`, `EXTRA_COMMA_ALTERNATIVE_CHARS_IN_ARGUMENTS`.
 *      - Added following to `RESULT_SENDER`:
 *           `FORMAT_SUCCESS_STDOUT`, `FORMAT_SUCCESS_STDOUT__EXIT_CODE`, `FORMAT_SUCCESS_STDOUT__STDERR__EXIT_CODE`
 *           `FORMAT_FAILED_ERR__ERRMSG__STDOUT__STDERR__EXIT_CODE`,
 *           `RESULT_FILE_ERR_PREFIX`, `RESULT_FILE_ERRMSG_PREFIX` `RESULT_FILE_STDOUT_PREFIX`,
 *           `RESULT_FILE_STDERR_PREFIX`, `RESULT_FILE_EXIT_CODE_PREFIX`.
 *
 * - 0.25.0 (2021-08-19)
 *      - Added following to `xodos_APP.xodos_SERVICE`:
 *          `EXTRA_BACKGROUND_CUSTOM_LOG_LEVEL`.
 *      - Added following to `xodos_APP.RUN_COMMAND_SERVICE`:
 *          `EXTRA_BACKGROUND_CUSTOM_LOG_LEVEL`.
 *
 * - 0.26.0 (2021-08-25)
 *      - Changed `xodos_ACTIVITY.ACTION_FAILSAFE_SESSION` to `xodos_ACTIVITY.EXTRA_FAILSAFE_SESSION`.
 *
 * - 0.27.0 (2021-09-02)
 *      - Added `xodos_FLOAT_APP_NOTIFICATION_CHANNEL_ID`, `xodos_FLOAT_APP_NOTIFICATION_CHANNEL_NAME`,
 *          `xodos_FLOAT_APP.xodos_FLOAT_SERVICE_NAME`.
 *      - Added following to `xodos_FLOAT_APP.xodos_FLOAT_SERVICE`:
 *          `ACTION_STOP_SERVICE`, `ACTION_SHOW`, `ACTION_HIDE`.
 *
 * - 0.28.0 (2021-09-02)
 *      - Added `xodos_FLOAT_PROPERTIES_PRIMARY_FILE*` and `xodos_FLOAT_PROPERTIES_SECONDARY_FILE*`.
 *
 * - 0.29.0 (2021-09-04)
 *      - Added `xodos_SHORTCUT_TASKS_SCRIPTS_DIR_BASENAME`, `xodos_SHORTCUT_SCRIPT_ICONS_DIR_BASENAME`,
 *          `xodos_SHORTCUT_SCRIPT_ICONS_DIR_PATH`, `xodos_SHORTCUT_SCRIPT_ICONS_DIR`.
 *      - Added following to `xodos_WIDGET.xodos_WIDGET_PROVIDER`:
 *          `ACTION_WIDGET_ITEM_CLICKED`, `ACTION_REFRESH_WIDGET`, `EXTRA_FILE_CLICKED`.
 *      - Changed naming convention of `xodos_FLOAT_APP.xodos_FLOAT_SERVICE.ACTION_*`.
 *      - Fixed wrong path set for `xodos_SHORTCUT_SCRIPTS_DIR_PATH`.
 *
 * - 0.30.0 (2021-09-08)
 *      - Changed `APK_RELEASE_GITHUB_DEBUG_BUILD`to `APK_RELEASE_GITHUB` and
 *          `APK_RELEASE_GITHUB_DEBUG_BUILD_SIGNING_CERTIFICATE_SHA256_DIGEST` to
 *          `APK_RELEASE_GITHUB_SIGNING_CERTIFICATE_SHA256_DIGEST`.
 *
 * - 0.31.0 (2021-09-09)
 *      - Added following to `xodos_APP.xodos_SERVICE`:
 *          `MIN_VALUE_EXTRA_SESSION_ACTION` and `MAX_VALUE_EXTRA_SESSION_ACTION`.
 *
 * - 0.32.0 (2021-09-23)
 *      - Added `xodos_API.xodos_API_ACTIVITY_NAME`, `xodos_TASKER.xodos_TASKER_ACTIVITY_NAME`
 *          and `xodos_WIDGET.xodos_WIDGET_ACTIVITY_NAME`.
 *
 * - 0.33.0 (2021-10-08)
 *      - Added `xodos_PROPERTIES_FILE_PATHS_LIST` and `xodos_FLOAT_PROPERTIES_FILE_PATHS_LIST`.
 *
 * - 0.34.0 (2021-10-26)
 *      - Move `RESULT_SENDER` to `com.xodos.shared.shell.command.ShellCommandConstants`.
 *
 * - 0.35.0 (2022-01-28)
 *      - Add `xodos_APP.xodos_ACTIVITY.EXTRA_RECREATE_ACTIVITY`.
 *
 * - 0.36.0 (2022-03-10)
 *      - Added `xodos_APP.xodos_SERVICE.EXTRA_RUNNER` and `xodos_APP.RUN_COMMAND_SERVICE.EXTRA_RUNNER`
 *
 * - 0.37.0 (2022-03-15)
 *  - Added `xodos_API_APT_*`.
 *
 * - 0.38.0 (2022-03-16)
 *      - Added `xodos_APP.xodos_ACTIVITY.ACTION_NOTIFY_APP_CRASH`.
 *
 * - 0.39.0 (2022-03-18)
 *      - Added `xodos_APP.xodos_SERVICE.EXTRA_SESSION_NAME`, `xodos_APP.RUN_COMMAND_SERVICE.EXTRA_SESSION_NAME`,
 *          `xodos_APP.xodos_SERVICE.EXTRA_SESSION_CREATE_MODE` and `xodos_APP.RUN_COMMAND_SERVICE.EXTRA_SESSION_CREATE_MODE`.
 *
 * - 0.40.0 (2022-04-17)
 *      - Added `xodos_APPS_DIR_PATH` and `xodos_APP.APPS_DIR_PATH`.
 *
 * - 0.41.0 (2022-04-17)
 *      - Added `xodos_APP.xodos_AM_SOCKET_FILE_PATH`.
 *
 * - 0.42.0 (2022-04-29)
 *      - Added `APK_RELEASE_xodos_DEVS` and `APK_RELEASE_xodos_DEVS_SIGNING_CERTIFICATE_SHA256_DIGEST`.
 *
 * - 0.43.0 (2022-05-29)
 *      - Changed `xodos_SUPPORT_EMAIL_URL` to support@xodos.dev.
 *
 * - 0.44.0 (2022-05-29)
 *      - Changed `xodos_APP.APPS_DIR_PATH` basename from `xodos-app` to `com.xodos`.
 *
 * - 0.45.0 (2022-06-01)
 *      - Added `xodos_APP.BUILD_CONFIG_CLASS_NAME`.
 *
 * - 0.46.0 (2022-06-03)
 *      - Rename `xodos_APP.xodos_SERVICE.EXTRA_SESSION_NAME` to `*.EXTRA_SHELL_NAME`,
 *          `xodos_APP.RUN_COMMAND_SERVICE.EXTRA_SESSION_NAME` to `*.EXTRA_SHELL_NAME`,
 *          `xodos_APP.xodos_SERVICE.EXTRA_SESSION_CREATE_MODE` to `*.EXTRA_SHELL_CREATE_MODE` and
 *          `xodos_APP.RUN_COMMAND_SERVICE.EXTRA_SESSION_CREATE_MODE` to `*.EXTRA_SHELL_CREATE_MODE`.
 *
 * - 0.47.0 (2022-06-04)
 *      - Added `xodos_SITE` and `xodos_SITE_URL`.
 *      - Changed `xodos_DONATE_URL`.
 *
 * - 0.48.0 (2022-06-04)
 *      - Removed `xodos_GAME_PACKAGES_GITHUB_*`, `xodos_SCIENCE_PACKAGES_GITHUB_*`,
 *          `xodos_ROOT_PACKAGES_GITHUB_*`, `xodos_UNSTABLE_PACKAGES_GITHUB_*`
 *
 * - 0.49.0 (2022-06-11)
 *      - Added `xodos_ENV_PREFIX_ROOT`.
 *
 * - 0.50.0 (2022-06-11)
 *      - Added `xodos_CONFIG_PREFIX_DIR_PATH`, `xodos_ENV_FILE_PATH` and `xodos_ENV_TEMP_FILE_PATH`.
 *
 * - 0.51.0 (2022-06-13)
 *      - Added `xodos_APP.FILE_SHARE_RECEIVER_ACTIVITY_CLASS_NAME` and `xodos_APP.FILE_VIEW_RECEIVER_ACTIVITY_CLASS_NAME`.
 *
 * - 0.52.0 (2022-06-18)
 *      - Added `xodos_PREFIX_DIR_IGNORED_SUB_FILES_PATHS_TO_CONSIDER_AS_EMPTY`.
 */

/**
 * A class that defines shared constants of the xodos app and its plugins.
 * This class will be hosted by xodos-shared lib and should be imported by other xodos plugin
 * apps as is instead of copying constants to random classes. The 3rd party apps can also import
 * it for interacting with xodos apps. If changes are made to this file, increment the version number
 * and add an entry in the Changelog section above.
 *
 * xodos app default package name is "com.xodos" and is used in {@link #xodos_PREFIX_DIR_PATH}.
 * The binaries compiled for xodos have {@link #xodos_PREFIX_DIR_PATH} hardcoded in them but it
 * can be changed during compilation.
 *
 * The {@link #xodos_PACKAGE_NAME} must be the same as the applicationId of xodos-app build.gradle
 * since its also used by {@link #xodos_FILES_DIR_PATH}.
 * If {@link #xodos_PACKAGE_NAME} is changed, then binaries, specially used in bootstrap need to be
 * compiled appropriately. Check https://github.com/xodos/xodos-packages/wiki/Building-packages
 * for more info.
 *
 * Ideally the only places where changes should be required if changing package name are the following:
 * - The {@link #xodos_PACKAGE_NAME} in {@link xodosConstants}.
 * - The "applicationId" in "build.gradle" of xodos-app. This is package name that android and app
 *      stores will use and is also the final package name stored in "AndroidManifest.xml".
 * - The "manifestPlaceholders" values for {@link #xodos_PACKAGE_NAME} and *_APP_NAME in
 *      "build.gradle" of xodos-app.
 * - The "ENTITY" values for {@link #xodos_PACKAGE_NAME} and *_APP_NAME in "strings.xml" of
 *      xodos-app and of xodos-shared.
 * - The "shortcut.xml" and "*_preferences.xml" files of xodos-app since dynamic variables don't
 *      work in it.
 * - Optionally the "package" in "AndroidManifest.xml" if modifying project structure of xodos-app.
 *      This is package name for java classes project structure and is prefixed if activity and service
 *      names use dot (.) notation. This is currently not advisable since this will break lot of
 *      stuff, including xodos-* packages.
 * - Optionally the *_PATH variables in {@link xodosConstants} containing the string "xodos".
 *
 * Check https://developer.android.com/studio/build/application-id for info on "package" in
 * "AndroidManifest.xml" and "applicationId" in "build.gradle".
 *
 * The {@link #xodos_PACKAGE_NAME} must be used in source code of xodos app and its plugins instead
 * of hardcoded "com.xodos" paths.
 */
public final class xodosConstants {


    /*
     * xodos organization variables.
     */

    /** xodos GitHub organization name */
    public static final String xodos_GITHUB_ORGANIZATION_NAME = "xodos"; // Default: "xodos"
    /** xodos GitHub organization url */
    public static final String xodos_GITHUB_ORGANIZATION_URL = "https://github.com" + "/" + xodos_GITHUB_ORGANIZATION_NAME; // Default: "https://github.com/xodos"

    /** F-Droid packages base url */
    public static final String FDROID_PACKAGES_BASE_URL = "https://f-droid.org/en/packages"; // Default: "https://f-droid.org/en/packages"





    /*
     * xodos and its plugin app and package names and urls.
     */

    /** xodos app name */
    public static final String xodos_APP_NAME = "xodos"; // Default: "xodos"
    /** xodos package name */
    public static final String xodos_PACKAGE_NAME = "com.xodos"; // Default: "com.xodos"
    /** xodos GitHub repo name */
    public static final String xodos_GITHUB_REPO_NAME = "xodos-app"; // Default: "xodos-app"
    /** xodos GitHub repo url */
    public static final String xodos_GITHUB_REPO_URL = xodos_GITHUB_ORGANIZATION_URL + "/" + xodos_GITHUB_REPO_NAME; // Default: "https://github.com/xodos/xodos-app"
    /** xodos GitHub issues repo url */
    public static final String xodos_GITHUB_ISSUES_REPO_URL = xodos_GITHUB_REPO_URL + "/issues"; // Default: "https://github.com/xodos/xodos-app/issues"
    /** xodos F-Droid package url */
    public static final String xodos_FDROID_PACKAGE_URL = FDROID_PACKAGES_BASE_URL + "/" + xodos_PACKAGE_NAME; // Default: "https://f-droid.org/en/packages/com.xodos"


    /** xodos:API app name */
    public static final String xodos_API_APP_NAME = "xodos:API"; // Default: "xodos:API"
    /** xodos:API app package name */
    public static final String xodos_API_PACKAGE_NAME = xodos_PACKAGE_NAME + ".api"; // Default: "com.xodos.api"
    /** xodos:API GitHub repo name */
    public static final String xodos_API_GITHUB_REPO_NAME = "xodos-api"; // Default: "xodos-api"
    /** xodos:API GitHub repo url */
    public static final String xodos_API_GITHUB_REPO_URL = xodos_GITHUB_ORGANIZATION_URL + "/" + xodos_API_GITHUB_REPO_NAME; // Default: "https://github.com/xodos/xodos-api"
    /** xodos:API GitHub issues repo url */
    public static final String xodos_API_GITHUB_ISSUES_REPO_URL = xodos_API_GITHUB_REPO_URL + "/issues"; // Default: "https://github.com/xodos/xodos-api/issues"
    /** xodos:API F-Droid package url */
    public static final String xodos_API_FDROID_PACKAGE_URL = FDROID_PACKAGES_BASE_URL + "/" + xodos_API_PACKAGE_NAME; // Default: "https://f-droid.org/en/packages/com.xodos.api"


    /** xodos:Boot app name */
    public static final String xodos_BOOT_APP_NAME = "xodos:Boot"; // Default: "xodos:Boot"
    /** xodos:Boot app package name */
    public static final String xodos_BOOT_PACKAGE_NAME = xodos_PACKAGE_NAME + ".boot"; // Default: "com.xodos.boot"
    /** xodos:Boot GitHub repo name */
    public static final String xodos_BOOT_GITHUB_REPO_NAME = "xodos-boot"; // Default: "xodos-boot"
    /** xodos:Boot GitHub repo url */
    public static final String xodos_BOOT_GITHUB_REPO_URL = xodos_GITHUB_ORGANIZATION_URL + "/" + xodos_BOOT_GITHUB_REPO_NAME; // Default: "https://github.com/xodos/xodos-boot"
    /** xodos:Boot GitHub issues repo url */
    public static final String xodos_BOOT_GITHUB_ISSUES_REPO_URL = xodos_BOOT_GITHUB_REPO_URL + "/issues"; // Default: "https://github.com/xodos/xodos-boot/issues"
    /** xodos:Boot F-Droid package url */
    public static final String xodos_BOOT_FDROID_PACKAGE_URL = FDROID_PACKAGES_BASE_URL + "/" + xodos_BOOT_PACKAGE_NAME; // Default: "https://f-droid.org/en/packages/com.xodos.boot"


    /** xodos:Float app name */
    public static final String xodos_FLOAT_APP_NAME = "xodos:Float"; // Default: "xodos:Float"
    /** xodos:Float app package name */
    public static final String xodos_FLOAT_PACKAGE_NAME = xodos_PACKAGE_NAME + ".window"; // Default: "com.xodos.window"
    /** xodos:Float GitHub repo name */
    public static final String xodos_FLOAT_GITHUB_REPO_NAME = "xodos-float"; // Default: "xodos-float"
    /** xodos:Float GitHub repo url */
    public static final String xodos_FLOAT_GITHUB_REPO_URL = xodos_GITHUB_ORGANIZATION_URL + "/" + xodos_FLOAT_GITHUB_REPO_NAME; // Default: "https://github.com/xodos/xodos-float"
    /** xodos:Float GitHub issues repo url */
    public static final String xodos_FLOAT_GITHUB_ISSUES_REPO_URL = xodos_FLOAT_GITHUB_REPO_URL + "/issues"; // Default: "https://github.com/xodos/xodos-float/issues"
    /** xodos:Float F-Droid package url */
    public static final String xodos_FLOAT_FDROID_PACKAGE_URL = FDROID_PACKAGES_BASE_URL + "/" + xodos_FLOAT_PACKAGE_NAME; // Default: "https://f-droid.org/en/packages/com.xodos.window"


    /** xodos:Styling app name */
    public static final String xodos_STYLING_APP_NAME = "xodos:Styling"; // Default: "xodos:Styling"
    /** xodos:Styling app package name */
    public static final String xodos_STYLING_PACKAGE_NAME = xodos_PACKAGE_NAME + ".styling"; // Default: "com.xodos.styling"
    /** xodos:Styling GitHub repo name */
    public static final String xodos_STYLING_GITHUB_REPO_NAME = "xodos-styling"; // Default: "xodos-styling"
    /** xodos:Styling GitHub repo url */
    public static final String xodos_STYLING_GITHUB_REPO_URL = xodos_GITHUB_ORGANIZATION_URL + "/" + xodos_STYLING_GITHUB_REPO_NAME; // Default: "https://github.com/xodos/xodos-styling"
    /** xodos:Styling GitHub issues repo url */
    public static final String xodos_STYLING_GITHUB_ISSUES_REPO_URL = xodos_STYLING_GITHUB_REPO_URL + "/issues"; // Default: "https://github.com/xodos/xodos-styling/issues"
    /** xodos:Styling F-Droid package url */
    public static final String xodos_STYLING_FDROID_PACKAGE_URL = FDROID_PACKAGES_BASE_URL + "/" + xodos_STYLING_PACKAGE_NAME; // Default: "https://f-droid.org/en/packages/com.xodos.styling"


    /** xodos:Tasker app name */
    public static final String xodos_TASKER_APP_NAME = "xodos:Tasker"; // Default: "xodos:Tasker"
    /** xodos:Tasker app package name */
    public static final String xodos_TASKER_PACKAGE_NAME = xodos_PACKAGE_NAME + ".tasker"; // Default: "com.xodos.tasker"
    /** xodos:Tasker GitHub repo name */
    public static final String xodos_TASKER_GITHUB_REPO_NAME = "xodos-tasker"; // Default: "xodos-tasker"
    /** xodos:Tasker GitHub repo url */
    public static final String xodos_TASKER_GITHUB_REPO_URL = xodos_GITHUB_ORGANIZATION_URL + "/" + xodos_TASKER_GITHUB_REPO_NAME; // Default: "https://github.com/xodos/xodos-tasker"
    /** xodos:Tasker GitHub issues repo url */
    public static final String xodos_TASKER_GITHUB_ISSUES_REPO_URL = xodos_TASKER_GITHUB_REPO_URL + "/issues"; // Default: "https://github.com/xodos/xodos-tasker/issues"
    /** xodos:Tasker F-Droid package url */
    public static final String xodos_TASKER_FDROID_PACKAGE_URL = FDROID_PACKAGES_BASE_URL + "/" + xodos_TASKER_PACKAGE_NAME; // Default: "https://f-droid.org/en/packages/com.xodos.tasker"


    /** xodos:Widget app name */
    public static final String xodos_WIDGET_APP_NAME = "xodos:Widget"; // Default: "xodos:Widget"
    /** xodos:Widget app package name */
    public static final String xodos_WIDGET_PACKAGE_NAME = xodos_PACKAGE_NAME + ".widget"; // Default: "com.xodos.widget"
    /** xodos:Widget GitHub repo name */
    public static final String xodos_WIDGET_GITHUB_REPO_NAME = "xodos-widget"; // Default: "xodos-widget"
    /** xodos:Widget GitHub repo url */
    public static final String xodos_WIDGET_GITHUB_REPO_URL = xodos_GITHUB_ORGANIZATION_URL + "/" + xodos_WIDGET_GITHUB_REPO_NAME; // Default: "https://github.com/xodos/xodos-widget"
    /** xodos:Widget GitHub issues repo url */
    public static final String xodos_WIDGET_GITHUB_ISSUES_REPO_URL = xodos_WIDGET_GITHUB_REPO_URL + "/issues"; // Default: "https://github.com/xodos/xodos-widget/issues"
    /** xodos:Widget F-Droid package url */
    public static final String xodos_WIDGET_FDROID_PACKAGE_URL = FDROID_PACKAGES_BASE_URL + "/" + xodos_WIDGET_PACKAGE_NAME; // Default: "https://f-droid.org/en/packages/com.xodos.widget"





    /*
     * xodos plugin apps lists.
     */

    public static final List<String> xodos_PLUGIN_APP_NAMES_LIST = Arrays.asList(
        xodos_API_APP_NAME,
        xodos_BOOT_APP_NAME,
        xodos_FLOAT_APP_NAME,
        xodos_STYLING_APP_NAME,
        xodos_TASKER_APP_NAME,
        xodos_WIDGET_APP_NAME);

    public static final List<String> xodos_PLUGIN_APP_PACKAGE_NAMES_LIST = Arrays.asList(
        xodos_API_PACKAGE_NAME,
        xodos_BOOT_PACKAGE_NAME,
        xodos_FLOAT_PACKAGE_NAME,
        xodos_STYLING_PACKAGE_NAME,
        xodos_TASKER_PACKAGE_NAME,
        xodos_WIDGET_PACKAGE_NAME);





    /*
     * xodos APK releases.
     */

    /** F-Droid APK release */
    public static final String APK_RELEASE_FDROID = "F-Droid"; // Default: "F-Droid"

    /** F-Droid APK release signing certificate SHA-256 digest */
    public static final String APK_RELEASE_FDROID_SIGNING_CERTIFICATE_SHA256_DIGEST = "228FB2CFE90831C1499EC3CCAF61E96E8E1CE70766B9474672CE427334D41C42"; // Default: "228FB2CFE90831C1499EC3CCAF61E96E8E1CE70766B9474672CE427334D41C42"

    /** GitHub APK release */
    public static final String APK_RELEASE_GITHUB = "Github"; // Default: "Github"

    /** GitHub APK release signing certificate SHA-256 digest */
    public static final String APK_RELEASE_GITHUB_SIGNING_CERTIFICATE_SHA256_DIGEST = "B6DA01480EEFD5FBF2CD3771B8D1021EC791304BDD6C4BF41D3FAABAD48EE5E1"; // Default: "B6DA01480EEFD5FBF2CD3771B8D1021EC791304BDD6C4BF41D3FAABAD48EE5E1"

    /** Google Play Store APK release */
    public static final String APK_RELEASE_GOOGLE_PLAYSTORE = "Google Play Store"; // Default: "Google Play Store"

    /** Google Play Store APK release signing certificate SHA-256 digest */
    public static final String APK_RELEASE_GOOGLE_PLAYSTORE_SIGNING_CERTIFICATE_SHA256_DIGEST = "738F0A30A04D3C8A1BE304AF18D0779BCF3EA88FB60808F657A3521861C2EBF9"; // Default: "738F0A30A04D3C8A1BE304AF18D0779BCF3EA88FB60808F657A3521861C2EBF9"

    /** xodos Devs APK release */
    public static final String APK_RELEASE_xodos_DEVS = "xodos Devs"; // Default: "xodos Devs"

    /** xodos Devs APK release signing certificate SHA-256 digest */
    public static final String APK_RELEASE_xodos_DEVS_SIGNING_CERTIFICATE_SHA256_DIGEST = "F7A038EB551F1BE8FDF388686B784ABAB4552A5D82DF423E3D8F1B5CBE1C69AE"; // Default: "F7A038EB551F1BE8FDF388686B784ABAB4552A5D82DF423E3D8F1B5CBE1C69AE"





    /*
     * xodos packages urls.
     */

    /** xodos Packages GitHub repo name */
    public static final String xodos_PACKAGES_GITHUB_REPO_NAME = "xodos-packages"; // Default: "xodos-packages"
    /** xodos Packages GitHub repo url */
    public static final String xodos_PACKAGES_GITHUB_REPO_URL = xodos_GITHUB_ORGANIZATION_URL + "/" + xodos_PACKAGES_GITHUB_REPO_NAME; // Default: "https://github.com/xodos/xodos-packages"
    /** xodos Packages GitHub issues repo url */
    public static final String xodos_PACKAGES_GITHUB_ISSUES_REPO_URL = xodos_PACKAGES_GITHUB_REPO_URL + "/issues"; // Default: "https://github.com/xodos/xodos-packages/issues"


    /** xodos API apt package name */
    public static final String xodos_API_APT_PACKAGE_NAME = "xodos-api"; // Default: "xodos-api"
    /** xodos API apt GitHub repo name */
    public static final String xodos_API_APT_GITHUB_REPO_NAME = "xodos-api-package"; // Default: "xodos-api-package"
    /** xodos API apt GitHub repo url */
    public static final String xodos_API_APT_GITHUB_REPO_URL = xodos_GITHUB_ORGANIZATION_URL + "/" + xodos_API_APT_GITHUB_REPO_NAME; // Default: "https://github.com/xodos/xodos-api-package"
    /** xodos API apt GitHub issues repo url */
    public static final String xodos_API_APT_GITHUB_ISSUES_REPO_URL = xodos_API_APT_GITHUB_REPO_URL + "/issues"; // Default: "https://github.com/xodos/xodos-api-package/issues"





    /*
     * xodos miscellaneous urls.
     */

    /** xodos Site */
    public static final String xodos_SITE = xodos_APP_NAME + " Site"; // Default: "xodos Site"

    /** xodos Site url */
    public static final String xodos_SITE_URL = "https://xodos.dev"; // Default: "https://xodos.dev"

    /** xodos Wiki */
    public static final String xodos_WIKI = xodos_APP_NAME + " Wiki"; // Default: "xodos Wiki"

    /** xodos Wiki url */
    public static final String xodos_WIKI_URL = "https://wiki.xodos.com"; // Default: "https://wiki.xodos.com"

    /** xodos GitHub wiki repo url */
    public static final String xodos_GITHUB_WIKI_REPO_URL = xodos_GITHUB_REPO_URL + "/wiki"; // Default: "https://github.com/xodos/xodos-app/wiki"

    /** xodos Packages wiki repo url */
    public static final String xodos_PACKAGES_GITHUB_WIKI_REPO_URL = xodos_PACKAGES_GITHUB_REPO_URL + "/wiki"; // Default: "https://github.com/xodos/xodos-packages/wiki"


    /** xodos support email url */
    public static final String xodos_SUPPORT_EMAIL_URL = "support@xodos.dev"; // Default: "support@xodos.dev"

    /** xodos support email mailto url */
    public static final String xodos_SUPPORT_EMAIL_MAILTO_URL = "mailto:" + xodos_SUPPORT_EMAIL_URL; // Default: "mailto:support@xodos.dev"


    /** xodos Reddit subreddit */
    public static final String xodos_REDDIT_SUBREDDIT = "r/xodos"; // Default: "r/xodos"

    /** xodos Reddit subreddit url */
    public static final String xodos_REDDIT_SUBREDDIT_URL = "https://www.reddit.com/r/xodos"; // Default: "https://www.reddit.com/r/xodos"


    /** xodos donate url */
    public static final String xodos_DONATE_URL = xodos_SITE_URL + "/donate"; // Default: "https://xodos.dev/donate"





    /*
     * xodos app core directory paths.
     */

    /** xodos app internal private app data directory path */
    @SuppressLint("SdCardPath")
    public static final String xodos_INTERNAL_PRIVATE_APP_DATA_DIR_PATH = "/data/data/" + xodos_PACKAGE_NAME; // Default: "/data/data/com.xodos"
    /** xodos app internal private app data directory */
    public static final File xodos_INTERNAL_PRIVATE_APP_DATA_DIR = new File(xodos_INTERNAL_PRIVATE_APP_DATA_DIR_PATH);



    /** xodos app Files directory path */
    public static final String xodos_FILES_DIR_PATH = xodos_INTERNAL_PRIVATE_APP_DATA_DIR_PATH + "/files"; // Default: "/data/data/com.xodos/files"
    /** xodos app Files directory */
    public static final File xodos_FILES_DIR = new File(xodos_FILES_DIR_PATH);



    /** xodos app $PREFIX directory path */
    public static final String xodos_PREFIX_DIR_PATH = xodos_FILES_DIR_PATH + "/usr"; // Default: "/data/data/com.xodos/files/usr"
    /** xodos app $PREFIX directory */
    public static final File xodos_PREFIX_DIR = new File(xodos_PREFIX_DIR_PATH);


    /** xodos app $PREFIX/bin directory path */
    public static final String xodos_BIN_PREFIX_DIR_PATH = xodos_PREFIX_DIR_PATH + "/bin"; // Default: "/data/data/com.xodos/files/usr/bin"
    /** xodos app $PREFIX/bin directory */
    public static final File xodos_BIN_PREFIX_DIR = new File(xodos_BIN_PREFIX_DIR_PATH);


    /** xodos app $PREFIX/etc directory path */
    public static final String xodos_ETC_PREFIX_DIR_PATH = xodos_PREFIX_DIR_PATH + "/etc"; // Default: "/data/data/com.xodos/files/usr/etc"
    /** xodos app $PREFIX/etc directory */
    public static final File xodos_ETC_PREFIX_DIR = new File(xodos_ETC_PREFIX_DIR_PATH);


    /** xodos app $PREFIX/include directory path */
    public static final String xodos_INCLUDE_PREFIX_DIR_PATH = xodos_PREFIX_DIR_PATH + "/include"; // Default: "/data/data/com.xodos/files/usr/include"
    /** xodos app $PREFIX/include directory */
    public static final File xodos_INCLUDE_PREFIX_DIR = new File(xodos_INCLUDE_PREFIX_DIR_PATH);


    /** xodos app $PREFIX/lib directory path */
    public static final String xodos_LIB_PREFIX_DIR_PATH = xodos_PREFIX_DIR_PATH + "/lib"; // Default: "/data/data/com.xodos/files/usr/lib"
    /** xodos app $PREFIX/lib directory */
    public static final File xodos_LIB_PREFIX_DIR = new File(xodos_LIB_PREFIX_DIR_PATH);


    /** xodos app $PREFIX/libexec directory path */
    public static final String xodos_LIBEXEC_PREFIX_DIR_PATH = xodos_PREFIX_DIR_PATH + "/libexec"; // Default: "/data/data/com.xodos/files/usr/libexec"
    /** xodos app $PREFIX/libexec directory */
    public static final File xodos_LIBEXEC_PREFIX_DIR = new File(xodos_LIBEXEC_PREFIX_DIR_PATH);


    /** xodos app $PREFIX/share directory path */
    public static final String xodos_SHARE_PREFIX_DIR_PATH = xodos_PREFIX_DIR_PATH + "/share"; // Default: "/data/data/com.xodos/files/usr/share"
    /** xodos app $PREFIX/share directory */
    public static final File xodos_SHARE_PREFIX_DIR = new File(xodos_SHARE_PREFIX_DIR_PATH);


    /** xodos app $PREFIX/tmp and $TMPDIR directory path */
    public static final String xodos_TMP_PREFIX_DIR_PATH = xodos_PREFIX_DIR_PATH + "/tmp"; // Default: "/data/data/com.xodos/files/usr/tmp"
    /** xodos app $PREFIX/tmp and $TMPDIR directory */
    public static final File xodos_TMP_PREFIX_DIR = new File(xodos_TMP_PREFIX_DIR_PATH);


    /** xodos app $PREFIX/var directory path */
    public static final String xodos_VAR_PREFIX_DIR_PATH = xodos_PREFIX_DIR_PATH + "/var"; // Default: "/data/data/com.xodos/files/usr/var"
    /** xodos app $PREFIX/var directory */
    public static final File xodos_VAR_PREFIX_DIR = new File(xodos_VAR_PREFIX_DIR_PATH);



    /** xodos app usr-staging directory path */
    public static final String xodos_STAGING_PREFIX_DIR_PATH = xodos_FILES_DIR_PATH + "/usr-staging"; // Default: "/data/data/com.xodos/files/usr-staging"
    /** xodos app usr-staging directory */
    public static final File xodos_STAGING_PREFIX_DIR = new File(xodos_STAGING_PREFIX_DIR_PATH);



    /** xodos app $HOME directory path */
    public static final String xodos_HOME_DIR_PATH = xodos_FILES_DIR_PATH + "/home"; // Default: "/data/data/com.xodos/files/home"
    /** xodos app $HOME directory */
    public static final File xodos_HOME_DIR = new File(xodos_HOME_DIR_PATH);


    /** xodos app config home directory path */
    public static final String xodos_CONFIG_HOME_DIR_PATH = xodos_HOME_DIR_PATH + "/.config/xodos"; // Default: "/data/data/com.xodos/files/home/.config/xodos"
    /** xodos app config home directory */
    public static final File xodos_CONFIG_HOME_DIR = new File(xodos_CONFIG_HOME_DIR_PATH);

    /** xodos app config $PREFIX directory path */
    public static final String xodos_CONFIG_PREFIX_DIR_PATH = xodos_ETC_PREFIX_DIR_PATH + "/xodos"; // Default: "/data/data/com.xodos/files/usr/etc/xodos"
    /** xodos app config $PREFIX directory */
    public static final File xodos_CONFIG_PREFIX_DIR = new File(xodos_CONFIG_PREFIX_DIR_PATH);


    /** xodos app data home directory path */
    public static final String xodos_DATA_HOME_DIR_PATH = xodos_HOME_DIR_PATH + "/.xodos"; // Default: "/data/data/com.xodos/files/home/.xodos"
    /** xodos app data home directory */
    public static final File xodos_DATA_HOME_DIR = new File(xodos_DATA_HOME_DIR_PATH);


    /** xodos app storage home directory path */
    public static final String xodos_STORAGE_HOME_DIR_PATH = xodos_HOME_DIR_PATH + "/storage"; // Default: "/data/data/com.xodos/files/home/storage"
    /** xodos app storage home directory */
    public static final File xodos_STORAGE_HOME_DIR = new File(xodos_STORAGE_HOME_DIR_PATH);



    /** xodos and plugin apps directory path */
    public static final String xodos_APPS_DIR_PATH = xodos_FILES_DIR_PATH + "/apps"; // Default: "/data/data/com.xodos/files/apps"
    /** xodos and plugin apps directory */
    public static final File xodos_APPS_DIR = new File(xodos_APPS_DIR_PATH);


    /** xodos app $PREFIX directory path ignored sub file paths to consider it empty */
    public static final List<String> xodos_PREFIX_DIR_IGNORED_SUB_FILES_PATHS_TO_CONSIDER_AS_EMPTY = Arrays.asList(
        xodosConstants.xodos_TMP_PREFIX_DIR_PATH, xodosConstants.xodos_ENV_TEMP_FILE_PATH, xodosConstants.xodos_ENV_FILE_PATH);



    /*
     * xodos app and plugin preferences and properties file paths.
     */

    /** xodos app default SharedPreferences file basename without extension */
    public static final String xodos_DEFAULT_PREFERENCES_FILE_BASENAME_WITHOUT_EXTENSION = xodos_PACKAGE_NAME + "_preferences"; // Default: "com.xodos_preferences"

    /** xodos:API app default SharedPreferences file basename without extension */
    public static final String xodos_API_DEFAULT_PREFERENCES_FILE_BASENAME_WITHOUT_EXTENSION = xodos_API_PACKAGE_NAME + "_preferences"; // Default: "com.xodos.api_preferences"

    /** xodos:Boot app default SharedPreferences file basename without extension */
    public static final String xodos_BOOT_DEFAULT_PREFERENCES_FILE_BASENAME_WITHOUT_EXTENSION = xodos_BOOT_PACKAGE_NAME + "_preferences"; // Default: "com.xodos.boot_preferences"

    /** xodos:Float app default SharedPreferences file basename without extension */
    public static final String xodos_FLOAT_DEFAULT_PREFERENCES_FILE_BASENAME_WITHOUT_EXTENSION = xodos_FLOAT_PACKAGE_NAME + "_preferences"; // Default: "com.xodos.window_preferences"

    /** xodos:Styling app default SharedPreferences file basename without extension */
    public static final String xodos_STYLING_DEFAULT_PREFERENCES_FILE_BASENAME_WITHOUT_EXTENSION = xodos_STYLING_PACKAGE_NAME + "_preferences"; // Default: "com.xodos.styling_preferences"

    /** xodos:Tasker app default SharedPreferences file basename without extension */
    public static final String xodos_TASKER_DEFAULT_PREFERENCES_FILE_BASENAME_WITHOUT_EXTENSION = xodos_TASKER_PACKAGE_NAME + "_preferences"; // Default: "com.xodos.tasker_preferences"

    /** xodos:Widget app default SharedPreferences file basename without extension */
    public static final String xodos_WIDGET_DEFAULT_PREFERENCES_FILE_BASENAME_WITHOUT_EXTENSION = xodos_WIDGET_PACKAGE_NAME + "_preferences"; // Default: "com.xodos.widget_preferences"



    /** xodos app properties primary file path */
    public static final String xodos_PROPERTIES_PRIMARY_FILE_PATH = xodos_DATA_HOME_DIR_PATH + "/xodos.properties"; // Default: "/data/data/com.xodos/files/home/.xodos/xodos.properties"
    /** xodos app properties primary file */
    public static final File xodos_PROPERTIES_PRIMARY_FILE = new File(xodos_PROPERTIES_PRIMARY_FILE_PATH);

    /** xodos app properties secondary file path */
    public static final String xodos_PROPERTIES_SECONDARY_FILE_PATH = xodos_CONFIG_HOME_DIR_PATH + "/xodos.properties"; // Default: "/data/data/com.xodos/files/home/.config/xodos/xodos.properties"
    /** xodos app properties secondary file */
    public static final File xodos_PROPERTIES_SECONDARY_FILE = new File(xodos_PROPERTIES_SECONDARY_FILE_PATH);

    /** xodos app properties file paths list. **DO NOT** allow these files to be modified by
     * {@link android.content.ContentProvider} exposed to external apps, since they may silently
     * modify the values for security properties like {@link #PROP_ALLOW_EXTERNAL_APPS} set by users
     * without their explicit consent. */
    public static final List<String> xodos_PROPERTIES_FILE_PATHS_LIST = Arrays.asList(
        xodos_PROPERTIES_PRIMARY_FILE_PATH,
        xodos_PROPERTIES_SECONDARY_FILE_PATH);



    /** xodos:Float app properties primary file path */
    public static final String xodos_FLOAT_PROPERTIES_PRIMARY_FILE_PATH = xodos_DATA_HOME_DIR_PATH + "/xodos.float.properties"; // Default: "/data/data/com.xodos/files/home/.xodos/xodos.float.properties"
    /** xodos:Float app properties primary file */
    public static final File xodos_FLOAT_PROPERTIES_PRIMARY_FILE = new File(xodos_FLOAT_PROPERTIES_PRIMARY_FILE_PATH);

    /** xodos:Float app properties secondary file path */
    public static final String xodos_FLOAT_PROPERTIES_SECONDARY_FILE_PATH = xodos_CONFIG_HOME_DIR_PATH + "/xodos.float.properties"; // Default: "/data/data/com.xodos/files/home/.config/xodos/xodos.float.properties"
    /** xodos:Float app properties secondary file */
    public static final File xodos_FLOAT_PROPERTIES_SECONDARY_FILE = new File(xodos_FLOAT_PROPERTIES_SECONDARY_FILE_PATH);

    /** xodos:Float app properties file paths list. **DO NOT** allow these files to be modified by
     * {@link android.content.ContentProvider} exposed to external apps, since they may silently
     * modify the values for security properties like {@link #PROP_ALLOW_EXTERNAL_APPS} set by users
     * without their explicit consent. */
    public static final List<String> xodos_FLOAT_PROPERTIES_FILE_PATHS_LIST = Arrays.asList(
        xodos_FLOAT_PROPERTIES_PRIMARY_FILE_PATH,
        xodos_FLOAT_PROPERTIES_SECONDARY_FILE_PATH);



    /** xodos app and xodos:Styling colors.properties file path */
    public static final String xodos_COLOR_PROPERTIES_FILE_PATH = xodos_DATA_HOME_DIR_PATH + "/colors.properties"; // Default: "/data/data/com.xodos/files/home/.xodos/colors.properties"
    /** xodos app and xodos:Styling colors.properties file */
    public static final File xodos_COLOR_PROPERTIES_FILE = new File(xodos_COLOR_PROPERTIES_FILE_PATH);

    /** xodos app and xodos:Styling font.ttf file path */
    public static final String xodos_FONT_FILE_PATH = xodos_DATA_HOME_DIR_PATH + "/font.ttf"; // Default: "/data/data/com.xodos/files/home/.xodos/font.ttf"
    /** xodos app and xodos:Styling font.ttf file */
    public static final File xodos_FONT_FILE = new File(xodos_FONT_FILE_PATH);


    /** xodos app and plugins crash log file path */
    public static final String xodos_CRASH_LOG_FILE_PATH = xodos_HOME_DIR_PATH + "/crash_log.md"; // Default: "/data/data/com.xodos/files/home/crash_log.md"

    /** xodos app and plugins crash log backup file path */
    public static final String xodos_CRASH_LOG_BACKUP_FILE_PATH = xodos_HOME_DIR_PATH + "/crash_log_backup.md"; // Default: "/data/data/com.xodos/files/home/crash_log_backup.md"


    /** xodos app environment file path */
    public static final String xodos_ENV_FILE_PATH = xodos_CONFIG_PREFIX_DIR_PATH + "/xodos.env"; // Default: "/data/data/com.xodos/files/usr/etc/xodos/xodos.env"

    /** xodos app environment temp file path */
    public static final String xodos_ENV_TEMP_FILE_PATH = xodos_CONFIG_PREFIX_DIR_PATH + "/xodos.env.tmp"; // Default: "/data/data/com.xodos/files/usr/etc/xodos/xodos.env.tmp"




    /*
     * xodos app plugin specific paths.
     */

    /** xodos app directory path to store scripts to be run at boot by xodos:Boot */
    public static final String xodos_BOOT_SCRIPTS_DIR_PATH = xodos_DATA_HOME_DIR_PATH + "/boot"; // Default: "/data/data/com.xodos/files/home/.xodos/boot"
    /** xodos app directory to store scripts to be run at boot by xodos:Boot */
    public static final File xodos_BOOT_SCRIPTS_DIR = new File(xodos_BOOT_SCRIPTS_DIR_PATH);


    /** xodos app directory path to store foreground scripts that can be run by the xodos launcher
     * widget provided by xodos:Widget */
    public static final String xodos_SHORTCUT_SCRIPTS_DIR_PATH = xodos_HOME_DIR_PATH + "/.shortcuts"; // Default: "/data/data/com.xodos/files/home/.shortcuts"
    /** xodos app directory to store foreground scripts that can be run by the xodos launcher widget provided by xodos:Widget */
    public static final File xodos_SHORTCUT_SCRIPTS_DIR = new File(xodos_SHORTCUT_SCRIPTS_DIR_PATH);


    /** xodos app directory basename that stores background scripts that can be run by the xodos
     * launcher widget provided by xodos:Widget */
    public static final String xodos_SHORTCUT_TASKS_SCRIPTS_DIR_BASENAME =  "tasks"; // Default: "tasks"
    /** xodos app directory path to store background scripts that can be run by the xodos launcher
     * widget provided by xodos:Widget */
    public static final String xodos_SHORTCUT_TASKS_SCRIPTS_DIR_PATH = xodos_SHORTCUT_SCRIPTS_DIR_PATH + "/" + xodos_SHORTCUT_TASKS_SCRIPTS_DIR_BASENAME; // Default: "/data/data/com.xodos/files/home/.shortcuts/tasks"
    /** xodos app directory to store background scripts that can be run by the xodos launcher widget provided by xodos:Widget */
    public static final File xodos_SHORTCUT_TASKS_SCRIPTS_DIR = new File(xodos_SHORTCUT_TASKS_SCRIPTS_DIR_PATH);


    /** xodos app directory basename that stores icons for the foreground and background scripts
     * that can be run by the xodos launcher widget provided by xodos:Widget */
    public static final String xodos_SHORTCUT_SCRIPT_ICONS_DIR_BASENAME =  "icons"; // Default: "icons"
    /** xodos app directory path to store icons for the foreground and background scripts that can
     * be run by the xodos launcher widget provided by xodos:Widget */
    public static final String xodos_SHORTCUT_SCRIPT_ICONS_DIR_PATH = xodos_SHORTCUT_SCRIPTS_DIR_PATH + "/" + xodos_SHORTCUT_SCRIPT_ICONS_DIR_BASENAME; // Default: "/data/data/com.xodos/files/home/.shortcuts/icons"
    /** xodos app directory to store icons for the foreground and background scripts that can be
     * run by the xodos launcher widget provided by xodos:Widget */
    public static final File xodos_SHORTCUT_SCRIPT_ICONS_DIR = new File(xodos_SHORTCUT_SCRIPT_ICONS_DIR_PATH);


    /** xodos app directory path to store scripts to be run by 3rd party twofortyfouram locale plugin
     * host apps like Tasker app via the xodos:Tasker plugin client */
    public static final String xodos_TASKER_SCRIPTS_DIR_PATH = xodos_DATA_HOME_DIR_PATH + "/tasker"; // Default: "/data/data/com.xodos/files/home/.xodos/tasker"
    /** xodos app directory to store scripts to be run by 3rd party twofortyfouram locale plugin host apps like Tasker app via the xodos:Tasker plugin client */
    public static final File xodos_TASKER_SCRIPTS_DIR = new File(xodos_TASKER_SCRIPTS_DIR_PATH);





    /*
     * xodos app and plugins notification variables.
     */

    /** xodos app notification channel id used by {@link xodos_APP.xodos_SERVICE} */
    public static final String xodos_APP_NOTIFICATION_CHANNEL_ID = "xodos_notification_channel";
    /** xodos app notification channel name used by {@link xodos_APP.xodos_SERVICE} */
    public static final String xodos_APP_NOTIFICATION_CHANNEL_NAME = xodosConstants.xodos_APP_NAME + " App";
    /** xodos app unique notification id used by {@link xodos_APP.xodos_SERVICE} */
    public static final int xodos_APP_NOTIFICATION_ID = 1337;

    /** xodos app notification channel id used by {@link xodos_APP.RUN_COMMAND_SERVICE} */
    public static final String xodos_RUN_COMMAND_NOTIFICATION_CHANNEL_ID = "xodos_run_command_notification_channel";
    /** xodos app notification channel name used by {@link xodos_APP.RUN_COMMAND_SERVICE} */
    public static final String xodos_RUN_COMMAND_NOTIFICATION_CHANNEL_NAME = xodosConstants.xodos_APP_NAME + " RunCommandService";
    /** xodos app unique notification id used by {@link xodos_APP.RUN_COMMAND_SERVICE} */
    public static final int xodos_RUN_COMMAND_NOTIFICATION_ID = 1338;

    /** xodos app notification channel id used for plugin command errors */
    public static final String xodos_PLUGIN_COMMAND_ERRORS_NOTIFICATION_CHANNEL_ID = "xodos_plugin_command_errors_notification_channel";
    /** xodos app notification channel name used for plugin command errors */
    public static final String xodos_PLUGIN_COMMAND_ERRORS_NOTIFICATION_CHANNEL_NAME = xodosConstants.xodos_APP_NAME + " Plugin Commands Errors";

    /** xodos app notification channel id used for crash reports */
    public static final String xodos_CRASH_REPORTS_NOTIFICATION_CHANNEL_ID = "xodos_crash_reports_notification_channel";
    /** xodos app notification channel name used for crash reports */
    public static final String xodos_CRASH_REPORTS_NOTIFICATION_CHANNEL_NAME = xodosConstants.xodos_APP_NAME + " Crash Reports";


    /** xodos app notification channel id used by {@link xodos_FLOAT_APP.xodos_FLOAT_SERVICE} */
    public static final String xodos_FLOAT_APP_NOTIFICATION_CHANNEL_ID = "xodos_float_notification_channel";
    /** xodos app notification channel name used by {@link xodos_FLOAT_APP.xodos_FLOAT_SERVICE} */
    public static final String xodos_FLOAT_APP_NOTIFICATION_CHANNEL_NAME = xodosConstants.xodos_FLOAT_APP_NAME + " App";
    /** xodos app unique notification id used by {@link xodos_APP.xodos_SERVICE} */
    public static final int xodos_FLOAT_APP_NOTIFICATION_ID = 1339;





    /*
     * xodos app and plugins miscellaneous variables.
     */

    /** Android OS permission declared by xodos app in AndroidManifest.xml which can be requested by
     * 3rd party apps to run various commands in xodos app context */
    public static final String PERMISSION_RUN_COMMAND = xodos_PACKAGE_NAME + ".permission.RUN_COMMAND"; // Default: "com.xodos.permission.RUN_COMMAND"

    /** xodos property defined in xodos.properties file as a secondary check to PERMISSION_RUN_COMMAND
     * to allow 3rd party apps to run various commands in xodos app context */
    public static final String PROP_ALLOW_EXTERNAL_APPS = "allow-external-apps"; // Default: "allow-external-apps"
    /** Default value for {@link #PROP_ALLOW_EXTERNAL_APPS} */
    public static final String PROP_DEFAULT_VALUE_ALLOW_EXTERNAL_APPS = "false"; // Default: "false"

    /** The broadcast action sent when xodos App opens */
    public static final String BROADCAST_xodos_OPENED = xodos_PACKAGE_NAME + ".app.OPENED";

    /** The Uri authority for xodos app file shares */
    public static final String xodos_FILE_SHARE_URI_AUTHORITY = xodos_PACKAGE_NAME + ".files"; // Default: "com.xodos.files"

    /** The normal comma character (U+002C, &comma;, &#44;, comma) */
    public static final String COMMA_NORMAL = ","; // Default: ","

    /** The alternate comma character (U+201A, &sbquo;, &#8218;, single low-9 quotation mark) that
     * may be used instead of {@link #COMMA_NORMAL} */
    public static final String COMMA_ALTERNATIVE = "‚"; // Default: "‚"

    /** Environment variable prefix root for the xodos app. */
    public static final String xodos_ENV_PREFIX_ROOT = "xodos";






    /**
     * xodos app constants.
     */
    public static final class xodos_APP {

        /** xodos apps directory path */
        public static final String APPS_DIR_PATH = xodos_APPS_DIR_PATH + "/" + xodos_PACKAGE_NAME; // Default: "/data/data/com.xodos/files/apps/com.xodos"

        /** xodos-am socket file path */
        public static final String xodos_AM_SOCKET_FILE_PATH = APPS_DIR_PATH + "/xodos-am/am.sock"; // Default: "/data/data/com.xodos/files/apps/com.xodos/xodos-am/am.sock"


        /** xodos app BuildConfig class name */
        public static final String BUILD_CONFIG_CLASS_NAME = xodos_PACKAGE_NAME + ".BuildConfig"; // Default: "com.xodos.BuildConfig"

        /** xodos app FileShareReceiverActivity class name */
        public static final String FILE_SHARE_RECEIVER_ACTIVITY_CLASS_NAME = xodos_PACKAGE_NAME + ".app.api.file.FileShareReceiverActivity"; // Default: "com.xodos.app.api.file.FileShareReceiverActivity"

        /** xodos app FileViewReceiverActivity class name */
        public static final String FILE_VIEW_RECEIVER_ACTIVITY_CLASS_NAME = xodos_PACKAGE_NAME + ".app.api.file.FileViewReceiverActivity"; // Default: "com.xodos.app.api.file.FileViewReceiverActivity"


        /** xodos app core activity name. */
        public static final String xodos_ACTIVITY_NAME = xodos_PACKAGE_NAME + ".app.xodosActivity"; // Default: "com.xodos.app.xodosActivity"

        /**
         * xodos app core activity.
         */
        public static final class xodos_ACTIVITY {

            /** Intent extra for if xodos failsafe session needs to be started and is used by {@link xodos_ACTIVITY} and {@link xodos_SERVICE#ACTION_STOP_SERVICE} */
            public static final String EXTRA_FAILSAFE_SESSION = xodosConstants.xodos_PACKAGE_NAME + ".app.failsafe_session"; // Default: "com.xodos.app.failsafe_session"


            /** Intent action to make xodos app notify user that a crash happened. */
            public static final String ACTION_NOTIFY_APP_CRASH = xodosConstants.xodos_PACKAGE_NAME + ".app.notify_app_crash"; // Default: "com.xodos.app.notify_app_crash"


            /** Intent action to make xodos reload its xodos session styling */
            public static final String ACTION_RELOAD_STYLE = xodosConstants.xodos_PACKAGE_NAME + ".app.reload_style"; // Default: "com.xodos.app.reload_style"
            /** Intent {@code String} extra for what to reload for the xodos_ACTIVITY.ACTION_RELOAD_STYLE intent. This has been deperecated. */
            @Deprecated
            public static final String EXTRA_RELOAD_STYLE = xodosConstants.xodos_PACKAGE_NAME + ".app.reload_style"; // Default: "com.xodos.app.reload_style"

            /**  Intent {@code boolean} extra for whether to recreate activity for the xodos_ACTIVITY.ACTION_RELOAD_STYLE intent. */
            public static final String EXTRA_RECREATE_ACTIVITY = xodos_APP.xodos_ACTIVITY_NAME + ".EXTRA_RECREATE_ACTIVITY"; // Default: "com.xodos.app.xodosActivity.EXTRA_RECREATE_ACTIVITY"


            /** Intent action to make xodos request storage permissions */
            public static final String ACTION_REQUEST_PERMISSIONS = xodosConstants.xodos_PACKAGE_NAME + ".app.request_storage_permissions"; // Default: "com.xodos.app.request_storage_permissions"
        }





        /** xodos app settings activity name. */
        public static final String xodos_SETTINGS_ACTIVITY_NAME = xodos_PACKAGE_NAME + ".app.activities.SettingsActivity"; // Default: "com.xodos.app.activities.SettingsActivity"





        /** xodos app core service name. */
        public static final String xodos_SERVICE_NAME = xodos_PACKAGE_NAME + ".app.xodosService"; // Default: "com.xodos.app.xodosService"

        /**
         * xodos app core service.
         */
        public static final class xodos_SERVICE {

            /** Intent action to stop xodos_SERVICE */
            public static final String ACTION_STOP_SERVICE = xodos_PACKAGE_NAME + ".service_stop"; // Default: "com.xodos.service_stop"


            /** Intent action to make xodos_SERVICE acquire a wakelock */
            public static final String ACTION_WAKE_LOCK = xodos_PACKAGE_NAME + ".service_wake_lock"; // Default: "com.xodos.service_wake_lock"


            /** Intent action to make xodos_SERVICE release wakelock */
            public static final String ACTION_WAKE_UNLOCK = xodos_PACKAGE_NAME + ".service_wake_unlock"; // Default: "com.xodos.service_wake_unlock"


            /** Intent action to execute command with xodos_SERVICE */
            public static final String ACTION_SERVICE_EXECUTE = xodos_PACKAGE_NAME + ".service_execute"; // Default: "com.xodos.service_execute"

            /** Uri scheme for paths sent via intent to xodos_SERVICE */
            public static final String URI_SCHEME_SERVICE_EXECUTE = xodos_PACKAGE_NAME + ".file"; // Default: "com.xodos.file"
            /** Intent {@code String[]} extra for arguments to the executable of the command for the xodos_SERVICE.ACTION_SERVICE_EXECUTE intent */
            public static final String EXTRA_ARGUMENTS = xodos_PACKAGE_NAME + ".execute.arguments"; // Default: "com.xodos.execute.arguments"
            /** Intent {@code String} extra for stdin of the command for the xodos_SERVICE.ACTION_SERVICE_EXECUTE intent */
            public static final String EXTRA_STDIN = xodos_PACKAGE_NAME + ".execute.stdin"; // Default: "com.xodos.execute.stdin"
            /** Intent {@code String} extra for command current working directory for the xodos_SERVICE.ACTION_SERVICE_EXECUTE intent */
            public static final String EXTRA_WORKDIR = xodos_PACKAGE_NAME + ".execute.cwd"; // Default: "com.xodos.execute.cwd"
            /** Intent {@code boolean} extra for whether to run command in background {@link Runner#APP_SHELL} or foreground {@link Runner#TERMINAL_SESSION} for the xodos_SERVICE.ACTION_SERVICE_EXECUTE intent */
            @Deprecated
            public static final String EXTRA_BACKGROUND = xodos_PACKAGE_NAME + ".execute.background"; // Default: "com.xodos.execute.background"
            /** Intent {@code String} extra for command the {@link Runner} for the xodos_SERVICE.ACTION_SERVICE_EXECUTE intent */
            public static final String EXTRA_RUNNER = xodos_PACKAGE_NAME + ".execute.runner"; // Default: "com.xodos.execute.runner"
            /** Intent {@code String} extra for custom log level for background commands defined by {@link com.xodos.shared.logger.Logger} for the xodos_SERVICE.ACTION_SERVICE_EXECUTE intent */
            public static final String EXTRA_BACKGROUND_CUSTOM_LOG_LEVEL = xodos_PACKAGE_NAME + ".execute.background_custom_log_level"; // Default: "com.xodos.execute.background_custom_log_level"
            /** Intent {@code String} extra for session action for {@link Runner#TERMINAL_SESSION} commands for the xodos_SERVICE.ACTION_SERVICE_EXECUTE intent */
            public static final String EXTRA_SESSION_ACTION = xodos_PACKAGE_NAME + ".execute.session_action"; // Default: "com.xodos.execute.session_action"
            /** Intent {@code String} extra for shell name for commands for the xodos_SERVICE.ACTION_SERVICE_EXECUTE intent */
            public static final String EXTRA_SHELL_NAME = xodos_PACKAGE_NAME + ".execute.shell_name"; // Default: "com.xodos.execute.shell_name"
            /** Intent {@code String} extra for the {@link ExecutionCommand.ShellCreateMode}  for the xodos_SERVICE.ACTION_SERVICE_EXECUTE intent. */
            public static final String EXTRA_SHELL_CREATE_MODE = xodos_PACKAGE_NAME + ".execute.shell_create_mode"; // Default: "com.xodos.execute.shell_create_mode"
            /** Intent {@code String} extra for label of the command for the xodos_SERVICE.ACTION_SERVICE_EXECUTE intent */
            public static final String EXTRA_COMMAND_LABEL = xodos_PACKAGE_NAME + ".execute.command_label"; // Default: "com.xodos.execute.command_label"
            /** Intent markdown {@code String} extra for description of the command for the xodos_SERVICE.ACTION_SERVICE_EXECUTE intent */
            public static final String EXTRA_COMMAND_DESCRIPTION = xodos_PACKAGE_NAME + ".execute.command_description"; // Default: "com.xodos.execute.command_description"
            /** Intent markdown {@code String} extra for help of the command for the xodos_SERVICE.ACTION_SERVICE_EXECUTE intent */
            public static final String EXTRA_COMMAND_HELP = xodos_PACKAGE_NAME + ".execute.command_help"; // Default: "com.xodos.execute.command_help"
            /** Intent markdown {@code String} extra for help of the plugin API for the xodos_SERVICE.ACTION_SERVICE_EXECUTE intent (Internal Use Only) */
            public static final String EXTRA_PLUGIN_API_HELP = xodos_PACKAGE_NAME + ".execute.plugin_api_help"; // Default: "com.xodos.execute.plugin_help"
            /** Intent {@code Parcelable} extra for the pending intent that should be sent with the
             * result of the execution command to the execute command caller for the xodos_SERVICE.ACTION_SERVICE_EXECUTE intent */
            public static final String EXTRA_PENDING_INTENT = "pendingIntent"; // Default: "pendingIntent"
            /** Intent {@code String} extra for the directory path in which to write the result of the
             * execution command for the execute command caller for the xodos_SERVICE.ACTION_SERVICE_EXECUTE intent */
            public static final String EXTRA_RESULT_DIRECTORY = xodos_PACKAGE_NAME + ".execute.result_directory"; // Default: "com.xodos.execute.result_directory"
            /** Intent {@code boolean} extra for whether the result should be written to a single file
             * or multiple files (err, errmsg, stdout, stderr, exit_code) in
             * {@link #EXTRA_RESULT_DIRECTORY} for the xodos_SERVICE.ACTION_SERVICE_EXECUTE intent */
            public static final String EXTRA_RESULT_SINGLE_FILE = xodos_PACKAGE_NAME + ".execute.result_single_file"; // Default: "com.xodos.execute.result_single_file"
            /** Intent {@code String} extra for the basename of the result file that should be created
             * in {@link #EXTRA_RESULT_DIRECTORY} if {@link #EXTRA_RESULT_SINGLE_FILE} is {@code true}
             * for the xodos_SERVICE.ACTION_SERVICE_EXECUTE intent */
            public static final String EXTRA_RESULT_FILE_BASENAME = xodos_PACKAGE_NAME + ".execute.result_file_basename"; // Default: "com.xodos.execute.result_file_basename"
            /** Intent {@code String} extra for the output {@link Formatter} format of the
             * {@link #EXTRA_RESULT_FILE_BASENAME} result file for the xodos_SERVICE.ACTION_SERVICE_EXECUTE intent */
            public static final String EXTRA_RESULT_FILE_OUTPUT_FORMAT = xodos_PACKAGE_NAME + ".execute.result_file_output_format"; // Default: "com.xodos.execute.result_file_output_format"
            /** Intent {@code String} extra for the error {@link Formatter} format of the
             * {@link #EXTRA_RESULT_FILE_BASENAME} result file for the xodos_SERVICE.ACTION_SERVICE_EXECUTE intent */
            public static final String EXTRA_RESULT_FILE_ERROR_FORMAT = xodos_PACKAGE_NAME + ".execute.result_file_error_format"; // Default: "com.xodos.execute.result_file_error_format"
            /** Intent {@code String} extra for the optional suffix of the result files that should
             * be created in {@link #EXTRA_RESULT_DIRECTORY} if {@link #EXTRA_RESULT_SINGLE_FILE} is
             * {@code false} for the xodos_SERVICE.ACTION_SERVICE_EXECUTE intent */
            public static final String EXTRA_RESULT_FILES_SUFFIX = xodos_PACKAGE_NAME + ".execute.result_files_suffix"; // Default: "com.xodos.execute.result_files_suffix"



            /**
             * The value for {@link #EXTRA_SESSION_ACTION} extra that will set the new session as
             * the current session and will start {@link xodos_ACTIVITY} if its not running to bring
             * the new session to foreground.
             */
            public static final int VALUE_EXTRA_SESSION_ACTION_SWITCH_TO_NEW_SESSION_AND_OPEN_ACTIVITY = 0;

            /**
             * The value for {@link #EXTRA_SESSION_ACTION} extra that will keep any existing session
             * as the current session and will start {@link xodos_ACTIVITY} if its not running to
             * bring the existing session to foreground. The new session will be added to the left
             * sidebar in the sessions list.
             */
            public static final int VALUE_EXTRA_SESSION_ACTION_KEEP_CURRENT_SESSION_AND_OPEN_ACTIVITY = 1;

            /**
             * The value for {@link #EXTRA_SESSION_ACTION} extra that will set the new session as
             * the current session but will not start {@link xodos_ACTIVITY} if its not running
             * and session(s) will be seen in xodos notification and can be clicked to bring new
             * session to foreground. If the {@link xodos_ACTIVITY} is already running, then this
             * will behave like {@link #VALUE_EXTRA_SESSION_ACTION_KEEP_CURRENT_SESSION_AND_OPEN_ACTIVITY}.
             */
            public static final int VALUE_EXTRA_SESSION_ACTION_SWITCH_TO_NEW_SESSION_AND_DONT_OPEN_ACTIVITY = 2;

            /**
             * The value for {@link #EXTRA_SESSION_ACTION} extra that will keep any existing session
             * as the current session but will not start {@link xodos_ACTIVITY} if its not running
             * and session(s) will be seen in xodos notification and can be clicked to bring
             * existing session to foreground. If the {@link xodos_ACTIVITY} is already running,
             * then this will behave like {@link #VALUE_EXTRA_SESSION_ACTION_KEEP_CURRENT_SESSION_AND_OPEN_ACTIVITY}.
             */
            public static final int VALUE_EXTRA_SESSION_ACTION_KEEP_CURRENT_SESSION_AND_DONT_OPEN_ACTIVITY = 3;

            /** The minimum allowed value for {@link #EXTRA_SESSION_ACTION}. */
            public static final int MIN_VALUE_EXTRA_SESSION_ACTION = VALUE_EXTRA_SESSION_ACTION_SWITCH_TO_NEW_SESSION_AND_OPEN_ACTIVITY;

            /** The maximum allowed value for {@link #EXTRA_SESSION_ACTION}. */
            public static final int MAX_VALUE_EXTRA_SESSION_ACTION = VALUE_EXTRA_SESSION_ACTION_KEEP_CURRENT_SESSION_AND_DONT_OPEN_ACTIVITY;


            /** Intent {@code Bundle} extra to store result of execute command that is sent back for the
             * xodos_SERVICE.ACTION_SERVICE_EXECUTE intent if the {@link #EXTRA_PENDING_INTENT} is not
             * {@code null} */
            public static final String EXTRA_PLUGIN_RESULT_BUNDLE = "result"; // Default: "result"
            /** Intent {@code String} extra for stdout value of execute command of the {@link #EXTRA_PLUGIN_RESULT_BUNDLE} */
            public static final String EXTRA_PLUGIN_RESULT_BUNDLE_STDOUT = "stdout"; // Default: "stdout"
            /** Intent {@code String} extra for original length of stdout value of execute command of the {@link #EXTRA_PLUGIN_RESULT_BUNDLE} */
            public static final String EXTRA_PLUGIN_RESULT_BUNDLE_STDOUT_ORIGINAL_LENGTH = "stdout_original_length"; // Default: "stdout_original_length"
            /** Intent {@code String} extra for stderr value of execute command of the {@link #EXTRA_PLUGIN_RESULT_BUNDLE} */
            public static final String EXTRA_PLUGIN_RESULT_BUNDLE_STDERR = "stderr"; // Default: "stderr"
            /** Intent {@code String} extra for original length of stderr value of execute command of the {@link #EXTRA_PLUGIN_RESULT_BUNDLE} */
            public static final String EXTRA_PLUGIN_RESULT_BUNDLE_STDERR_ORIGINAL_LENGTH = "stderr_original_length"; // Default: "stderr_original_length"
            /** Intent {@code int} extra for exit code value of execute command of the {@link #EXTRA_PLUGIN_RESULT_BUNDLE} */
            public static final String EXTRA_PLUGIN_RESULT_BUNDLE_EXIT_CODE = "exitCode"; // Default: "exitCode"
            /** Intent {@code int} extra for err value of execute command of the {@link #EXTRA_PLUGIN_RESULT_BUNDLE} */
            public static final String EXTRA_PLUGIN_RESULT_BUNDLE_ERR = "err"; // Default: "err"
            /** Intent {@code String} extra for errmsg value of execute command of the {@link #EXTRA_PLUGIN_RESULT_BUNDLE} */
            public static final String EXTRA_PLUGIN_RESULT_BUNDLE_ERRMSG = "errmsg"; // Default: "errmsg"

        }





        /** xodos app run command service name. */
        public static final String RUN_COMMAND_SERVICE_NAME = xodos_PACKAGE_NAME + ".app.RunCommandService"; // xodos app service to receive commands from 3rd party apps "com.xodos.app.RunCommandService"

        /**
         * xodos app run command service to receive commands sent by 3rd party apps.
         */
        public static final class RUN_COMMAND_SERVICE {

            /** xodos RUN_COMMAND Intent help url */
            public static final String RUN_COMMAND_API_HELP_URL = XODOS_GITHUB_WIKI_REPO_URL + "/RUN_COMMAND-Intent"; // Default: "https://github.com/xodos/xodos-emulator/wiki/RUN_COMMAND-Intent"


            /** Intent action to execute command with RUN_COMMAND_SERVICE */
            public static final String ACTION_RUN_COMMAND = xodos_PACKAGE_NAME + ".RUN_COMMAND"; // Default: "com.xodos.RUN_COMMAND"

            /** Intent {@code String} extra for absolute path of command for the RUN_COMMAND_SERVICE.ACTION_RUN_COMMAND intent */
            public static final String EXTRA_COMMAND_PATH = xodos_PACKAGE_NAME + ".RUN_COMMAND_PATH"; // Default: "com.xodos.RUN_COMMAND_PATH"
            /** Intent {@code String[]} extra for arguments to the executable of the command for the RUN_COMMAND_SERVICE.ACTION_RUN_COMMAND intent */
            public static final String EXTRA_ARGUMENTS = xodos_PACKAGE_NAME + ".RUN_COMMAND_ARGUMENTS"; // Default: "com.xodos.RUN_COMMAND_ARGUMENTS"
            /** Intent {@code boolean} extra for whether to replace comma alternative characters in arguments with comma characters for the RUN_COMMAND_SERVICE.ACTION_RUN_COMMAND intent */
            public static final String EXTRA_REPLACE_COMMA_ALTERNATIVE_CHARS_IN_ARGUMENTS = xodos_PACKAGE_NAME + ".RUN_COMMAND_REPLACE_COMMA_ALTERNATIVE_CHARS_IN_ARGUMENTS"; // Default: "com.xodos.RUN_COMMAND_REPLACE_COMMA_ALTERNATIVE_CHARS_IN_ARGUMENTS"
            /** Intent {@code String} extra for the comma alternative characters in arguments that should be replaced instead of the default {@link #COMMA_ALTERNATIVE} for the RUN_COMMAND_SERVICE.ACTION_RUN_COMMAND intent */
            public static final String EXTRA_COMMA_ALTERNATIVE_CHARS_IN_ARGUMENTS = xodos_PACKAGE_NAME + ".RUN_COMMAND_COMMA_ALTERNATIVE_CHARS_IN_ARGUMENTS"; // Default: "com.xodos.RUN_COMMAND_COMMA_ALTERNATIVE_CHARS_IN_ARGUMENTS"

            /** Intent {@code String} extra for stdin of the command for the RUN_COMMAND_SERVICE.ACTION_RUN_COMMAND intent */
            public static final String EXTRA_STDIN = xodos_PACKAGE_NAME + ".RUN_COMMAND_STDIN"; // Default: "com.xodos.RUN_COMMAND_STDIN"
            /** Intent {@code String} extra for current working directory of command for the RUN_COMMAND_SERVICE.ACTION_RUN_COMMAND intent */
            public static final String EXTRA_WORKDIR = xodos_PACKAGE_NAME + ".RUN_COMMAND_WORKDIR"; // Default: "com.xodos.RUN_COMMAND_WORKDIR"
            /** Intent {@code boolean} extra for whether to run command in background {@link Runner#APP_SHELL} or foreground {@link Runner#TERMINAL_SESSION} for the RUN_COMMAND_SERVICE.ACTION_RUN_COMMAND intent */
            @Deprecated
            public static final String EXTRA_BACKGROUND = xodos_PACKAGE_NAME + ".RUN_COMMAND_BACKGROUND"; // Default: "com.xodos.RUN_COMMAND_BACKGROUND"
            /** Intent {@code String} extra for command the {@link Runner} for the RUN_COMMAND_SERVICE.ACTION_RUN_COMMAND intent */
            public static final String EXTRA_RUNNER = xodos_PACKAGE_NAME + ".RUN_COMMAND_RUNNER"; // Default: "com.xodos.RUN_COMMAND_RUNNER"
            /** Intent {@code String} extra for custom log level for background commands defined by {@link com.xodos.shared.logger.Logger} for the RUN_COMMAND_SERVICE.ACTION_RUN_COMMAND intent */
            public static final String EXTRA_BACKGROUND_CUSTOM_LOG_LEVEL = xodos_PACKAGE_NAME + ".RUN_COMMAND_BACKGROUND_CUSTOM_LOG_LEVEL"; // Default: "com.xodos.RUN_COMMAND_BACKGROUND_CUSTOM_LOG_LEVEL"
            /** Intent {@code String} extra for session action of {@link Runner#TERMINAL_SESSION} commands for the RUN_COMMAND_SERVICE.ACTION_RUN_COMMAND intent */
            public static final String EXTRA_SESSION_ACTION = xodos_PACKAGE_NAME + ".RUN_COMMAND_SESSION_ACTION"; // Default: "com.xodos.RUN_COMMAND_SESSION_ACTION"
            /** Intent {@code String} extra for shell name of commands for the RUN_COMMAND_SERVICE.ACTION_RUN_COMMAND intent */
            public static final String EXTRA_SHELL_NAME = xodos_PACKAGE_NAME + ".RUN_COMMAND_SHELL_NAME"; // Default: "com.xodos.RUN_COMMAND_SHELL_NAME"
            /** Intent {@code String} extra for the {@link ExecutionCommand.ShellCreateMode}  for the RUN_COMMAND_SERVICE.ACTION_RUN_COMMAND intent. */
            public static final String EXTRA_SHELL_CREATE_MODE = xodos_PACKAGE_NAME + ".RUN_COMMAND_SHELL_CREATE_MODE"; // Default: "com.xodos.RUN_COMMAND_SHELL_CREATE_MODE"
            /** Intent {@code String} extra for label of the command for the RUN_COMMAND_SERVICE.ACTION_RUN_COMMAND intent */
            public static final String EXTRA_COMMAND_LABEL = xodos_PACKAGE_NAME + ".RUN_COMMAND_COMMAND_LABEL"; // Default: "com.xodos.RUN_COMMAND_COMMAND_LABEL"
            /** Intent markdown {@code String} extra for description of the command for the RUN_COMMAND_SERVICE.ACTION_RUN_COMMAND intent */
            public static final String EXTRA_COMMAND_DESCRIPTION = xodos_PACKAGE_NAME + ".RUN_COMMAND_COMMAND_DESCRIPTION"; // Default: "com.xodos.RUN_COMMAND_COMMAND_DESCRIPTION"
            /** Intent markdown {@code String} extra for help of the command for the RUN_COMMAND_SERVICE.ACTION_RUN_COMMAND intent */
            public static final String EXTRA_COMMAND_HELP = xodos_PACKAGE_NAME + ".RUN_COMMAND_COMMAND_HELP"; // Default: "com.xodos.RUN_COMMAND_COMMAND_HELP"
            /** Intent {@code Parcelable} extra for the pending intent that should be sent with the result of the execution command to the execute command caller for the RUN_COMMAND_SERVICE.ACTION_RUN_COMMAND intent */
            public static final String EXTRA_PENDING_INTENT = xodos_PACKAGE_NAME + ".RUN_COMMAND_PENDING_INTENT"; // Default: "com.xodos.RUN_COMMAND_PENDING_INTENT"
            /** Intent {@code String} extra for the directory path in which to write the result of
             * the execution command for the execute command caller for the RUN_COMMAND_SERVICE.ACTION_RUN_COMMAND intent */
            public static final String EXTRA_RESULT_DIRECTORY = xodos_PACKAGE_NAME + ".RUN_COMMAND_RESULT_DIRECTORY"; // Default: "com.xodos.RUN_COMMAND_RESULT_DIRECTORY"
            /** Intent {@code boolean} extra for whether the result should be written to a single file
             * or multiple files (err, errmsg, stdout, stderr, exit_code) in
             * {@link #EXTRA_RESULT_DIRECTORY} for the RUN_COMMAND_SERVICE.ACTION_RUN_COMMAND intent */
            public static final String EXTRA_RESULT_SINGLE_FILE = xodos_PACKAGE_NAME + ".RUN_COMMAND_RESULT_SINGLE_FILE"; // Default: "com.xodos.RUN_COMMAND_RESULT_SINGLE_FILE"
            /** Intent {@code String} extra for the basename of the result file that should be created
             * in {@link #EXTRA_RESULT_DIRECTORY} if {@link #EXTRA_RESULT_SINGLE_FILE} is {@code true}
             * for the RUN_COMMAND_SERVICE.ACTION_RUN_COMMAND intent */
            public static final String EXTRA_RESULT_FILE_BASENAME = xodos_PACKAGE_NAME + ".RUN_COMMAND_RESULT_FILE_BASENAME"; // Default: "com.xodos.RUN_COMMAND_RESULT_FILE_BASENAME"
            /** Intent {@code String} extra for the output {@link Formatter} format of the
             * {@link #EXTRA_RESULT_FILE_BASENAME} result file for the RUN_COMMAND_SERVICE.ACTION_RUN_COMMAND intent */
            public static final String EXTRA_RESULT_FILE_OUTPUT_FORMAT = xodos_PACKAGE_NAME + ".RUN_COMMAND_RESULT_FILE_OUTPUT_FORMAT"; // Default: "com.xodos.RUN_COMMAND_RESULT_FILE_OUTPUT_FORMAT"
            /** Intent {@code String} extra for the error {@link Formatter} format of the
             * {@link #EXTRA_RESULT_FILE_BASENAME} result file for the RUN_COMMAND_SERVICE.ACTION_RUN_COMMAND intent */
            public static final String EXTRA_RESULT_FILE_ERROR_FORMAT = xodos_PACKAGE_NAME + ".RUN_COMMAND_RESULT_FILE_ERROR_FORMAT"; // Default: "com.xodos.RUN_COMMAND_RESULT_FILE_ERROR_FORMAT"
            /** Intent {@code String} extra for the optional suffix of the result files that should be
             * created in {@link #EXTRA_RESULT_DIRECTORY} if {@link #EXTRA_RESULT_SINGLE_FILE} is
             * {@code false} for the RUN_COMMAND_SERVICE.ACTION_RUN_COMMAND intent */
            public static final String EXTRA_RESULT_FILES_SUFFIX = xodos_PACKAGE_NAME + ".RUN_COMMAND_RESULT_FILES_SUFFIX"; // Default: "com.xodos.RUN_COMMAND_RESULT_FILES_SUFFIX"

        }
    }


    /**
     * xodos:API app constants.
     */
    public static final class xodos_API {

        /** xodos:API app core activity name. */
        public static final String xodos_API_ACTIVITY_NAME = xodos_API_PACKAGE_NAME + ".activities.xodosAPIActivity"; // Default: "com.xodos.tasker.activities.xodosAPIActivity"

    }





    /**
     * xodos:Float app constants.
     */
    public static final class xodos_FLOAT_APP {

        /** xodos:Float app core service name. */
        public static final String xodos_FLOAT_SERVICE_NAME = xodos_FLOAT_PACKAGE_NAME + ".xodosFloatService"; // Default: "com.xodos.window.xodosFloatService"

        /**
         * xodos:Float app core service.
         */
        public static final class xodos_FLOAT_SERVICE {

            /** Intent action to stop xodos_FLOAT_SERVICE. */
            public static final String ACTION_STOP_SERVICE = xodos_FLOAT_PACKAGE_NAME + ".ACTION_STOP_SERVICE"; // Default: "com.xodos.float.ACTION_STOP_SERVICE"

            /** Intent action to show float window. */
            public static final String ACTION_SHOW = xodos_FLOAT_PACKAGE_NAME + ".ACTION_SHOW"; // Default: "com.xodos.float.ACTION_SHOW"

            /** Intent action to hide float window. */
            public static final String ACTION_HIDE = xodos_FLOAT_PACKAGE_NAME + ".ACTION_HIDE"; // Default: "com.xodos.float.ACTION_HIDE"

        }

    }





    /**
     * xodos:Styling app constants.
     */
    public static final class xodos_STYLING {

        /** xodos:Styling app core activity name. */
        public static final String xodos_STYLING_ACTIVITY_NAME = xodos_STYLING_PACKAGE_NAME + ".xodosStyleActivity"; // Default: "com.xodos.styling.xodosStyleActivity"

    }





    /**
     * xodos:Tasker app constants.
     */
    public static final class xodos_TASKER {

        /** xodos:Tasker app core activity name. */
        public static final String xodos_TASKER_ACTIVITY_NAME = xodos_TASKER_PACKAGE_NAME + ".activities.xodosTaskerActivity"; // Default: "com.xodos.tasker.activities.xodosTaskerActivity"

    }





    /**
     * xodos:Widget app constants.
     */
    public static final class xodos_WIDGET {

        /** xodos:Widget app core activity name. */
        public static final String xodos_WIDGET_ACTIVITY_NAME = xodos_WIDGET_PACKAGE_NAME + ".activities.xodosWidgetActivity"; // Default: "com.xodos.widget.activities.xodosWidgetActivity"


        /**  Intent {@code String} extra for the token of the xodos:Widget app shortcuts. */
        public static final String EXTRA_TOKEN_NAME = xodos_PACKAGE_NAME + ".shortcut.token"; // Default: "com.xodos.shortcut.token"

        /**
         * xodos:Widget app {@link android.appwidget.AppWidgetProvider} class.
         */
        public static final class xodos_WIDGET_PROVIDER {

            /** Intent action for if an item is clicked in the widget. */
            public static final String ACTION_WIDGET_ITEM_CLICKED = xodos_WIDGET_PACKAGE_NAME + ".ACTION_WIDGET_ITEM_CLICKED"; // Default: "com.xodos.widget.ACTION_WIDGET_ITEM_CLICKED"


            /** Intent action to refresh files in the widget. */
            public static final String ACTION_REFRESH_WIDGET = xodos_WIDGET_PACKAGE_NAME + ".ACTION_REFRESH_WIDGET"; // Default: "com.xodos.widget.ACTION_REFRESH_WIDGET"


            /**  Intent {@code String} extra for the file clicked for the xodos_WIDGET_PROVIDER.ACTION_WIDGET_ITEM_CLICKED intent. */
            public static final String EXTRA_FILE_CLICKED = xodos_WIDGET_PACKAGE_NAME + ".EXTRA_FILE_CLICKED"; // Default: "com.xodos.widget.EXTRA_FILE_CLICKED"

        }

    }

}
