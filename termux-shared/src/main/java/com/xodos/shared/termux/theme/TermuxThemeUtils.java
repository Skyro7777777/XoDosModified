package com.xodos.shared.xodos.theme;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.xodos.shared.xodos.settings.properties.xodosPropertyConstants;
import com.xodos.shared.xodos.settings.properties.xodosSharedProperties;
import com.xodos.shared.theme.NightMode;

public class xodosThemeUtils {

    /** Get the {@link xodosPropertyConstants#KEY_NIGHT_MODE} value from the properties file on disk
     * and set it to app wide night mode value. */
    public static void setAppNightMode(@NonNull Context context) {
        NightMode.setAppNightMode(xodosSharedProperties.getNightMode(context));
    }

    /** Set name as app wide night mode value. */
    public static void setAppNightMode(@Nullable String name) {
        NightMode.setAppNightMode(name);
    }

}
