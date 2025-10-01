package com.xodos.shared.xodos.settings.properties;

import android.content.Context;

import androidx.annotation.NonNull;

import com.xodos.shared.xodos.xodosConstants;

public class xodosAppSharedProperties extends xodosSharedProperties {

    private static xodosAppSharedProperties properties;


    private xodosAppSharedProperties(@NonNull Context context) {
        super(context, xodosConstants.xodos_APP_NAME,
            xodosConstants.xodos_PROPERTIES_FILE_PATHS_LIST, xodosPropertyConstants.xodos_APP_PROPERTIES_LIST,
            new xodosSharedProperties.SharedPropertiesParserClient());
    }

    /**
     * Initialize the {@link #properties} and load properties from disk.
     *
     * @param context The {@link Context} for operations.
     * @return Returns the {@link xodosAppSharedProperties}.
     */
    public static xodosAppSharedProperties init(@NonNull Context context) {
        if (properties == null)
            properties = new xodosAppSharedProperties(context);

        return properties;
    }

    /**
     * Get the {@link #properties}.
     *
     * @return Returns the {@link xodosAppSharedProperties}.
     */
    public static xodosAppSharedProperties getProperties() {
        return properties;
    }

}
