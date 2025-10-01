package com.xodos.shared.xodos.settings.preferences;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.xodos.shared.logger.Logger;
import com.xodos.shared.android.PackageUtils;
import com.xodos.shared.settings.preferences.AppSharedPreferences;
import com.xodos.shared.settings.preferences.SharedPreferenceUtils;
import com.xodos.shared.xodos.xodosUtils;
import com.xodos.shared.xodos.settings.preferences.xodosPreferenceConstants.xodos_API_APP;
import com.xodos.shared.xodos.xodosConstants;

public class xodosAPIAppSharedPreferences extends AppSharedPreferences {

    private static final String LOG_TAG = "xodosAPIAppSharedPreferences";

    private xodosAPIAppSharedPreferences(@NonNull Context context) {
        super(context,
            SharedPreferenceUtils.getPrivateSharedPreferences(context,
                xodosConstants.xodos_API_DEFAULT_PREFERENCES_FILE_BASENAME_WITHOUT_EXTENSION),
            SharedPreferenceUtils.getPrivateAndMultiProcessSharedPreferences(context,
                xodosConstants.xodos_API_DEFAULT_PREFERENCES_FILE_BASENAME_WITHOUT_EXTENSION));
    }

    /**
     * Get {@link xodosAPIAppSharedPreferences}.
     *
     * @param context The {@link Context} to use to get the {@link Context} of the
     *                {@link xodosConstants#xodos_API_PACKAGE_NAME}.
     * @return Returns the {@link xodosAPIAppSharedPreferences}. This will {@code null} if an exception is raised.
     */
    @Nullable
    public static xodosAPIAppSharedPreferences build(@NonNull final Context context) {
        Context xodosAPIPackageContext = PackageUtils.getContextForPackage(context, xodosConstants.xodos_API_PACKAGE_NAME);
        if (xodosAPIPackageContext == null)
            return null;
        else
            return new xodosAPIAppSharedPreferences(xodosAPIPackageContext);
    }

    /**
     * Get {@link xodosAPIAppSharedPreferences}.
     *
     * @param context The {@link Context} to use to get the {@link Context} of the
     *                {@link xodosConstants#xodos_API_PACKAGE_NAME}.
     * @param exitAppOnError If {@code true} and failed to get package context, then a dialog will
     *                       be shown which when dismissed will exit the app.
     * @return Returns the {@link xodosAPIAppSharedPreferences}. This will {@code null} if an exception is raised.
     */
    public static xodosAPIAppSharedPreferences build(@NonNull final Context context, final boolean exitAppOnError) {
        Context xodosAPIPackageContext = xodosUtils.getContextForPackageOrExitApp(context, xodosConstants.xodos_API_PACKAGE_NAME, exitAppOnError);
        if (xodosAPIPackageContext == null)
            return null;
        else
            return new xodosAPIAppSharedPreferences(xodosAPIPackageContext);
    }



    public int getLogLevel(boolean readFromFile) {
        if (readFromFile)
            return SharedPreferenceUtils.getInt(mMultiProcessSharedPreferences, xodos_API_APP.KEY_LOG_LEVEL, Logger.DEFAULT_LOG_LEVEL);
        else
            return SharedPreferenceUtils.getInt(mSharedPreferences, xodos_API_APP.KEY_LOG_LEVEL, Logger.DEFAULT_LOG_LEVEL);
    }

    public void setLogLevel(Context context, int logLevel, boolean commitToFile) {
        logLevel = Logger.setLogLevel(context, logLevel);
        SharedPreferenceUtils.setInt(mSharedPreferences, xodos_API_APP.KEY_LOG_LEVEL, logLevel, commitToFile);
    }


    public int getLastPendingIntentRequestCode() {
        return SharedPreferenceUtils.getInt(mSharedPreferences, xodos_API_APP.KEY_LAST_PENDING_INTENT_REQUEST_CODE, xodos_API_APP.DEFAULT_VALUE_KEY_LAST_PENDING_INTENT_REQUEST_CODE);
    }

    public void setLastPendingIntentRequestCode(int lastPendingIntentRequestCode) {
        SharedPreferenceUtils.setInt(mSharedPreferences, xodos_API_APP.KEY_LAST_PENDING_INTENT_REQUEST_CODE, lastPendingIntentRequestCode, true);
    }

}
