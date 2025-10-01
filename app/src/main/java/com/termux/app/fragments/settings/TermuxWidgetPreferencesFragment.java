package com.xodos.app.fragments.settings;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Keep;
import androidx.preference.PreferenceDataStore;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.xodos.R;
import com.xodos.shared.xodos.settings.preferences.xodosWidgetAppSharedPreferences;

@Keep
public class xodosWidgetPreferencesFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        Context context = getContext();
        if (context == null) return;

        PreferenceManager preferenceManager = getPreferenceManager();
        preferenceManager.setPreferenceDataStore(xodosWidgetPreferencesDataStore.getInstance(context));

        setPreferencesFromResource(R.xml.xodos_widget_preferences, rootKey);
    }

}

class xodosWidgetPreferencesDataStore extends PreferenceDataStore {

    private final Context mContext;
    private final xodosWidgetAppSharedPreferences mPreferences;

    private static xodosWidgetPreferencesDataStore mInstance;

    private xodosWidgetPreferencesDataStore(Context context) {
        mContext = context;
        mPreferences = xodosWidgetAppSharedPreferences.build(context, true);
    }

    public static synchronized xodosWidgetPreferencesDataStore getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new xodosWidgetPreferencesDataStore(context);
        }
        return mInstance;
    }

}
