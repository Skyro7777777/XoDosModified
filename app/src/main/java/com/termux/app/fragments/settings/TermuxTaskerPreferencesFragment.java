package com.xodos.app.fragments.settings;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Keep;
import androidx.preference.PreferenceDataStore;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.xodos.R;
import com.xodos.shared.xodos.settings.preferences.xodosTaskerAppSharedPreferences;

@Keep
public class xodosTaskerPreferencesFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        Context context = getContext();
        if (context == null) return;

        PreferenceManager preferenceManager = getPreferenceManager();
        preferenceManager.setPreferenceDataStore(xodosTaskerPreferencesDataStore.getInstance(context));

        setPreferencesFromResource(R.xml.xodos_tasker_preferences, rootKey);
    }

}

class xodosTaskerPreferencesDataStore extends PreferenceDataStore {

    private final Context mContext;
    private final xodosTaskerAppSharedPreferences mPreferences;

    private static xodosTaskerPreferencesDataStore mInstance;

    private xodosTaskerPreferencesDataStore(Context context) {
        mContext = context;
        mPreferences = xodosTaskerAppSharedPreferences.build(context, true);
    }

    public static synchronized xodosTaskerPreferencesDataStore getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new xodosTaskerPreferencesDataStore(context);
        }
        return mInstance;
    }

}
