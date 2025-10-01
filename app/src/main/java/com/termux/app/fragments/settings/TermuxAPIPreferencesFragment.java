package com.xodos.app.fragments.settings;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Keep;
import androidx.preference.PreferenceDataStore;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.xodos.R;
import com.xodos.shared.xodos.settings.preferences.xodosAPIAppSharedPreferences;

@Keep
public class xodosAPIPreferencesFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        Context context = getContext();
        if (context == null) return;

        PreferenceManager preferenceManager = getPreferenceManager();
        preferenceManager.setPreferenceDataStore(xodosAPIPreferencesDataStore.getInstance(context));

        setPreferencesFromResource(R.xml.xodos_api_preferences, rootKey);
    }

}

class xodosAPIPreferencesDataStore extends PreferenceDataStore {

    private final Context mContext;
    private final xodosAPIAppSharedPreferences mPreferences;

    private static xodosAPIPreferencesDataStore mInstance;

    private xodosAPIPreferencesDataStore(Context context) {
        mContext = context;
        mPreferences = xodosAPIAppSharedPreferences.build(context, true);
    }

    public static synchronized xodosAPIPreferencesDataStore getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new xodosAPIPreferencesDataStore(context);
        }
        return mInstance;
    }

}
