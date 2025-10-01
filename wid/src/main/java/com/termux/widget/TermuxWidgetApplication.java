package com.xodos.widget;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.xodos.shared.logger.Logger;
import com.xodos.shared.xodos.xodosConstants;
import com.xodos.shared.xodos.crash.xodosCrashUtils;
import com.xodos.shared.xodos.settings.preferences.xodosWidgetAppSharedPreferences;
import com.xodos.shared.xodos.theme.xodosThemeUtils;

public class xodosWidgetApplication extends Application {

    public static final String LOG_TAG = "xodosWidgetApplication";

    public void onCreate() {
        super.onCreate();

        Log.i(LOG_TAG, "AppInit");

        Context context = getApplicationContext();

        // Set crash handler for the app
        xodosCrashUtils.setCrashHandler(context);

        // Set log config for the app
        setLogConfig(context, true);

        // Set NightMode.APP_NIGHT_MODE
        xodosThemeUtils.setAppNightMode(context);
    }

    public static void setLogConfig(Context context, boolean commitToFile) {
        Logger.setDefaultLogTag(xodosConstants.xodos_WIDGET_APP_NAME.replaceAll("[: ]", ""));

        // Load the log level from shared preferences and set it to the {@link Logger.CURRENT_LOG_LEVEL}
        xodosWidgetAppSharedPreferences preferences = xodosWidgetAppSharedPreferences.build(context);
        if (preferences == null) return;
        preferences.setLogLevel(null, preferences.getLogLevel(true), commitToFile);
    }

}
