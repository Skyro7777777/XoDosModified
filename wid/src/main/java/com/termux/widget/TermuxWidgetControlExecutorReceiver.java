package com.xodos.widget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class xodosWidgetControlExecutorReceiver extends BroadcastReceiver {

    private static final String LOG_TAG = "xodosWidgetControlExecutorReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        // Set log level for the receiver
        xodosWidgetApplication.setLogConfig(context, false);

        xodosWidgetProvider.handlexodosShortcutExecutionIntent(context, intent, LOG_TAG);
    }

}
