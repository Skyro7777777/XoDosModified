package com.xodos.app.fragments.settings;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Keep;
import androidx.preference.PreferenceDataStore;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.xodos.R;
import com.xodos.shared.xodos.settings.preferences.xodosAppSharedPreferences;

@Keep
public class xodosPreferencesFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        Context context = getContext();
        if (context == null) return;

        PreferenceManager preferenceManager = getPreferenceManager();
        preferenceManager.setPreferenceDataStore(xodosPreferencesDataStore.getInstance(context));

        setPreferencesFromResource(R.xml.xodos_preferences, rootKey);
    }

}

class xodosPreferencesDataStore extends PreferenceDataStore {

    private final Context mContext;
    private final xodosAppSharedPreferences mPreferences;

    private static xodosPreferencesDataStore mInstance;

    private xodosPreferencesDataStore(Context context) {
        mContext = context;
        mPreferences = xodosAppSharedPreferences.build(context, true);
    }

    public static synchronized xodosPreferencesDataStore getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new xodosPreferencesDataStore(context);
        }
        return mInstance;
    }

}
