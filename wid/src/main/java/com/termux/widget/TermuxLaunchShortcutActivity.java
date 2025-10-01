package com.xodos.widget;

import android.app.Activity;

/**
 * An activity to launch a shortcut. We want to a launch a service directly, but a shortcut
 * cannot be used to launch a service, only activities, so have to go through this activity.
 */
public class xodosLaunchShortcutActivity extends Activity {

	private static final String LOG_TAG = "xodosLaunchShortcutActivity";

	@Override
	protected void onResume() {
		super.onResume();

		// Set log level for the app
		xodosWidgetApplication.setLogConfig(this, false);

		xodosWidgetProvider.handlexodosShortcutExecutionIntent(this, getIntent(), LOG_TAG);
		finish();
	}

}
