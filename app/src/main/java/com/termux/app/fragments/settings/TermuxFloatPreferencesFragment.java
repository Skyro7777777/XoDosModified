package com.xodos.app.fragments.settings;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Keep;
import androidx.preference.PreferenceDataStore;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.xodos.R;
import com.xodos.shared.xodos.settings.preferences.xodosFloatAppSharedPreferences;

@Keep
public class xodosFloatPreferencesFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        Context context = getContext();
        if (context == null) return;

        PreferenceManager preferenceManager = getPreferenceManager();
        preferenceManager.setPreferenceDataStore(xodosFloatPreferencesDataStore.getInstance(context));

        setPreferencesFromResource(R.xml.xodos_float_preferences, rootKey);
    }

}

class xodosFloatPreferencesDataStore extends PreferenceDataStore {

    private final Context mContext;
    private final xodosFloatAppSharedPreferences mPreferences;

    private static xodosFloatPreferencesDataStore mInstance;

    private xodosFloatPreferencesDataStore(Context context) {
        mContext = context;
        mPreferences = xodosFloatAppSharedPreferences.build(context, true);
    }

    public static synchronized xodosFloatPreferencesDataStore getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new xodosFloatPreferencesDataStore(context);
        }
        return mInstance;
    }

}
