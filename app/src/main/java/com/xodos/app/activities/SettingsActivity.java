package com.xodos.app.activities;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.xodos.R;
import com.xodos.shared.activities.ReportActivity;
import com.xodos.shared.file.FileUtils;
import com.xodos.shared.models.ReportInfo;
import com.xodos.app.models.UserAction;
import com.xodos.shared.interact.ShareUtils;
import com.xodos.shared.android.PackageUtils;
import com.xodos.shared.xodos.settings.preferences.xodosAPIAppSharedPreferences;
import com.xodos.shared.xodos.settings.preferences.xodosFloatAppSharedPreferences;
import com.xodos.shared.xodos.settings.preferences.xodosTaskerAppSharedPreferences;
import com.xodos.shared.xodos.settings.preferences.xodosWidgetAppSharedPreferences;
import com.xodos.shared.android.AndroidUtils;
import com.xodos.shared.xodos.xodosConstants;
import com.xodos.shared.xodos.xodosUtils;
import com.xodos.shared.activity.media.AppCompatActivityUtils;
import com.xodos.shared.theme.NightMode;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppCompatActivityUtils.setNightMode(this, NightMode.getAppNightMode().getName(), true);

        setContentView(R.layout.activity_settings);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new RootPreferencesFragment())
                .commit();
        }

        AppCompatActivityUtils.setToolbar(this, com.xodos.shared.R.id.toolbar);
        AppCompatActivityUtils.setShowBackButtonInActionBar(this, true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public static class RootPreferencesFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            Context context = getContext();
            if (context == null) return;

            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            new Thread() {
                @Override
                public void run() {
                    configurexodosAPIPreference(context);
                    configurexodosFloatPreference(context);
                    configurexodosTaskerPreference(context);
                    configurexodosWidgetPreference(context);
                    configureAboutPreference(context);
                    configureDonatePreference(context);
                }
            }.start();
        }

        private void configurexodosAPIPreference(@NonNull Context context) {
            Preference xodosAPIPreference = findPreference("xodos_api");
            if (xodosAPIPreference != null) {
                xodosAPIAppSharedPreferences preferences = xodosAPIAppSharedPreferences.build(context, false);
                // If failed to get app preferences, then likely app is not installed, so do not show its preference
                xodosAPIPreference.setVisible(preferences != null);
            }
        }

        private void configurexodosFloatPreference(@NonNull Context context) {
            Preference xodosFloatPreference = findPreference("xodos_float");
            if (xodosFloatPreference != null) {
                xodosFloatAppSharedPreferences preferences = xodosFloatAppSharedPreferences.build(context, false);
                // If failed to get app preferences, then likely app is not installed, so do not show its preference
                xodosFloatPreference.setVisible(preferences != null);
            }
        }

        private void configurexodosTaskerPreference(@NonNull Context context) {
            Preference xodosTaskerPreference = findPreference("xodos_tasker");
            if (xodosTaskerPreference != null) {
                xodosTaskerAppSharedPreferences preferences = xodosTaskerAppSharedPreferences.build(context, false);
                // If failed to get app preferences, then likely app is not installed, so do not show its preference
                xodosTaskerPreference.setVisible(preferences != null);
            }
        }

        private void configurexodosWidgetPreference(@NonNull Context context) {
            Preference xodosWidgetPreference = findPreference("xodos_widget");
            if (xodosWidgetPreference != null) {
                xodosWidgetAppSharedPreferences preferences = xodosWidgetAppSharedPreferences.build(context, false);
                // If failed to get app preferences, then likely app is not installed, so do not show its preference
                xodosWidgetPreference.setVisible(preferences != null);
            }
        }

        private void configureAboutPreference(@NonNull Context context) {
            Preference aboutPreference = findPreference("about");
            if (aboutPreference != null) {
                aboutPreference.setOnPreferenceClickListener(preference -> {
                    new Thread() {
                        @Override
                        public void run() {
                            String title = "About";

                            StringBuilder aboutString = new StringBuilder();
                            aboutString.append(xodosUtils.getAppInfoMarkdownString(context, xodosUtils.AppInfoMode.xodos_AND_PLUGIN_PACKAGES));
                            aboutString.append("\n\n").append(AndroidUtils.getDeviceInfoMarkdownString(context, true));
                            aboutString.append("\n\n").append(xodosUtils.getImportantLinksMarkdownString(context));

                            String userActionName = UserAction.ABOUT.getName();

                            ReportInfo reportInfo = new ReportInfo(userActionName,
                                xodosConstants.xodos_APP.xodos_SETTINGS_ACTIVITY_NAME, title);
                            reportInfo.setReportString(aboutString.toString());
                            reportInfo.setReportSaveFileLabelAndPath(userActionName,
                                Environment.getExternalStorageDirectory() + "/" +
                                    FileUtils.sanitizeFileName(xodosConstants.xodos_APP_NAME + "-" + userActionName + ".log", true, true));

                            ReportActivity.startReportActivity(context, reportInfo);
                        }
                    }.start();

                    return true;
                });
            }
        }

        private void configureDonatePreference(@NonNull Context context) {
            Preference donatePreference = findPreference("donate");
            if (donatePreference != null) {
                String signingCertificateSHA256Digest = PackageUtils.getSigningCertificateSHA256DigestForPackage(context);
                if (signingCertificateSHA256Digest != null) {
                    // If APK is a Google Playstore release, then do not show the donation link
                    // since xodos isn't exempted from the playstore policy donation links restriction
                    // Check Fund solicitations: https://pay.google.com/intl/en_in/about/policy/
                    String apkRelease = xodosUtils.getAPKRelease(signingCertificateSHA256Digest);
                    if (apkRelease == null || apkRelease.equals(xodosConstants.APK_RELEASE_GOOGLE_PLAYSTORE_SIGNING_CERTIFICATE_SHA256_DIGEST)) {
                        donatePreference.setVisible(false);
                        return;
                    } else {
                        donatePreference.setVisible(true);
                    }
                }

                donatePreference.setOnPreferenceClickListener(preference -> {
                    ShareUtils.openUrl(context, xodosConstants.xodos_DONATE_URL);
                    return true;
                });
            }
        }
    }

}
