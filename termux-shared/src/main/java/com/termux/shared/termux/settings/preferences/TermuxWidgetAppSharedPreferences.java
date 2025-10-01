package com.xodos.shared.xodos.settings.preferences;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.xodos.shared.logger.Logger;
import com.xodos.shared.android.PackageUtils;
import com.xodos.shared.settings.preferences.AppSharedPreferences;
import com.xodos.shared.settings.preferences.SharedPreferenceUtils;
import com.xodos.shared.xodos.xodosUtils;
import com.xodos.shared.xodos.settings.preferences.xodosPreferenceConstants.xodos_WIDGET_APP;
import com.xodos.shared.xodos.xodosConstants;

import java.util.UUID;

public class xodosWidgetAppSharedPreferences extends AppSharedPreferences {

    private static final String LOG_TAG = "xodosWidgetAppSharedPreferences";

    private xodosWidgetAppSharedPreferences(@NonNull Context context) {
        super(context,
            SharedPreferenceUtils.getPrivateSharedPreferences(context,
                xodosConstants.xodos_WIDGET_DEFAULT_PREFERENCES_FILE_BASENAME_WITHOUT_EXTENSION),
            SharedPreferenceUtils.getPrivateAndMultiProcessSharedPreferences(context,
                xodosConstants.xodos_WIDGET_DEFAULT_PREFERENCES_FILE_BASENAME_WITHOUT_EXTENSION));
    }

    /**
     * Get {@link xodosWidgetAppSharedPreferences}.
     *
     * @param context The {@link Context} to use to get the {@link Context} of the
     *                {@link xodosConstants#xodos_WIDGET_PACKAGE_NAME}.
     * @return Returns the {@link xodosWidgetAppSharedPreferences}. This will {@code null} if an exception is raised.
     */
    @Nullable
    public static xodosWidgetAppSharedPreferences build(@NonNull final Context context) {
        Context xodosWidgetPackageContext = PackageUtils.getContextForPackage(context, xodosConstants.xodos_WIDGET_PACKAGE_NAME);
        if (xodosWidgetPackageContext == null)
            return null;
        else
            return new xodosWidgetAppSharedPreferences(xodosWidgetPackageContext);
    }

    /**
     * Get the {@link xodosWidgetAppSharedPreferences}.
     *
     * @param context The {@link Context} to use to get the {@link Context} of the
     *                {@link xodosConstants#xodos_WIDGET_PACKAGE_NAME}.
     * @param exitAppOnError If {@code true} and failed to get package context, then a dialog will
     *                       be shown which when dismissed will exit the app.
     * @return Returns the {@link xodosWidgetAppSharedPreferences}. This will {@code null} if an exception is raised.
     */
    public static xodosWidgetAppSharedPreferences build(@NonNull final Context context, final boolean exitAppOnError) {
        Context xodosWidgetPackageContext = xodosUtils.getContextForPackageOrExitApp(context, xodosConstants.xodos_WIDGET_PACKAGE_NAME, exitAppOnError);
        if (xodosWidgetPackageContext == null)
            return null;
        else
            return new xodosWidgetAppSharedPreferences(xodosWidgetPackageContext);
    }



    public static String getGeneratedToken(@NonNull Context context) {
        xodosWidgetAppSharedPreferences preferences = xodosWidgetAppSharedPreferences.build(context, true);
        if (preferences == null) return null;
        return preferences.getGeneratedToken();
    }

    public String getGeneratedToken() {
        String token =  SharedPreferenceUtils.getString(mSharedPreferences, xodos_WIDGET_APP.KEY_TOKEN, null, true);
        if (token == null) {
            token = UUID.randomUUID().toString();
            SharedPreferenceUtils.setString(mSharedPreferences, xodos_WIDGET_APP.KEY_TOKEN, token, true);
        }
        return token;
    }



    public int getLogLevel(boolean readFromFile) {
        if (readFromFile)
            return SharedPreferenceUtils.getInt(mMultiProcessSharedPreferences, xodos_WIDGET_APP.KEY_LOG_LEVEL, Logger.DEFAULT_LOG_LEVEL);
        else
            return SharedPreferenceUtils.getInt(mSharedPreferences, xodos_WIDGET_APP.KEY_LOG_LEVEL, Logger.DEFAULT_LOG_LEVEL);
    }

    public void setLogLevel(Context context, int logLevel, boolean commitToFile) {
        logLevel = Logger.setLogLevel(context, logLevel);
        SharedPreferenceUtils.setInt(mSharedPreferences, xodos_WIDGET_APP.KEY_LOG_LEVEL, logLevel, commitToFile);
    }

}
