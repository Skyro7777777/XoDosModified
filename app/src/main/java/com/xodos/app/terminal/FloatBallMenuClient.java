package com.xodos.app.terminal;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Toast;

import androidx.appcompat.content.res.AppCompatResources;

import com.xodos.R;
import com.xodos.app.xodosActivity;
import com.xodos.floatball.FloatBallManager;
import com.xodos.floatball.menu.FloatMenuCfg;
import com.xodos.floatball.menu.MenuItem;
import com.xodos.floatball.permission.FloatPermissionManager;
import com.xodos.floatball.utils.DensityUtil;
import com.xodos.floatball.widget.FloatBallCfg;
import com.xodos.x11.MainActivity;

public class FloatBallMenuClient {
    private FloatBallManager mFloatballManager;
    private FloatPermissionManager mFloatPermissionManager;
    private ActivityLifeCycleListener mActivityLifeCycleListener = new ActivityLifeCycleListener();
    private int resumed;
    private xodosActivity mxodosActivity;
    private boolean mAppNotOnFront = false;
    private boolean mShowKeyboard = false;
    private boolean mLockSlider = false;
    private boolean mShowTerminal = false;
    private boolean mShowPreference = false;

    private FloatBallMenuClient() {
    }

    public FloatBallMenuClient(xodosActivity xodosActivity) {
        mxodosActivity = xodosActivity;
    }

    public void onCreate() {
        init();
        mFloatballManager.show();
        //5 set float ball click handler
        if (mFloatballManager.getMenuItemSize() == 0) {
            toast(mxodosActivity.getString(R.string.add_menu_item));
        } else {
            mFloatballManager.setOnFloatBallClickListener(() -> {
                if (mAppNotOnFront) {
                    PackageManager packageManager = mxodosActivity.getPackageManager();
                    Intent intent = packageManager.getLaunchIntentForPackage("com.xodos");
                    if (intent != null) {
//                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        mxodosActivity.startActivity(intent);
                        toast(mxodosActivity.getString(R.string.raise_xodos_app));
                    }
                }
            });
        }
        //     6 if only float ball within app, register it to Application(out data, actually, it is enough within activity )
        mxodosActivity.getApplication().registerActivityLifecycleCallbacks(mActivityLifeCycleListener);
    }

    public void onAttachedToWindow() {
        try {
            mFloatballManager.show();
            mFloatballManager.onFloatBallClick();
        } catch (RuntimeException e) {
            e.printStackTrace();
            toast(mxodosActivity.getString(R.string.apply_display_over_other_app_permission));
        }

    }

    public void onDetachedFromWindow() {
        mFloatballManager.hide();
    }

    private void init() {
//      1 set position of float ball, set size, icon and drawable
        int ballSize = DensityUtil.dip2px(mxodosActivity, 40);
        Drawable ballIcon = AppCompatResources.getDrawable(mxodosActivity, R.drawable.icon_float_ball_shape);
//      different config below
//      FloatBallCfg ballCfg = new FloatBallCfg(ballSize, ballIcon);
//      FloatBallCfg ballCfg = new FloatBallCfg(ballSize, ballIcon, FloatBallCfg.Gravity.LEFT_CENTER,false);
//      FloatBallCfg ballCfg = new FloatBallCfg(ballSize, ballIcon, FloatBallCfg.Gravity.LEFT_BOTTOM, -100);
//      FloatBallCfg ballCfg = new FloatBallCfg(ballSize, ballIcon, FloatBallCfg.Gravity.RIGHT_TOP, 100);
        FloatBallCfg ballCfg = new FloatBallCfg(ballSize, ballIcon, FloatBallCfg.Gravity.RIGHT_CENTER);
//      set float ball weather hide
        ballCfg.setHideHalfLater(true);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mxodosActivity);
        boolean floatBallOverOtherApp = preferences.getBoolean("enableGlobalFloatBallMenu", false);
        Context ctx = mxodosActivity;
        if (floatBallOverOtherApp) {
            ctx = mxodosActivity.getApplicationContext();
        }
        //2 display float ball menu
        //2.1 init float ball menu config, every size of menu item and number of item
        int menuSize = DensityUtil.dip2px(mxodosActivity, 110);
        int menuItemSize = DensityUtil.dip2px(mxodosActivity, 20);
        FloatMenuCfg menuCfg = new FloatMenuCfg(menuSize, menuItemSize);
        //3 create float ball Manager
        mFloatballManager = new FloatBallManager(ctx, ballCfg, menuCfg);
        addFloatMenuItem();
        mFloatballManager.setFloatBallOverOtherApp(floatBallOverOtherApp);
        if (floatBallOverOtherApp) {
            setFloatPermission();
        }
    }

    private void setFloatPermission() {
        // set 'display over other app' permission of float bal menu
        //once permission, float ball never show
        mFloatPermissionManager = new FloatPermissionManager();
        mFloatballManager.setPermission(new FloatBallManager.IFloatBallPermission() {
            @Override
            public boolean onRequestFloatBallPermission() {
                requestFloatBallPermission(mxodosActivity);
                return true;
            }

            @Override
            public boolean hasFloatBallPermission(Context context) {
                return mFloatPermissionManager.checkPermission(context);
            }

            @Override
            public void requestFloatBallPermission(Activity activity) {
                mFloatPermissionManager.applyPermission(activity);
            }

        });
    }

    public void setTerminalShow(boolean showTerminal) {
        mShowTerminal = showTerminal;
    }

    public void setShowPreference(boolean showPreference) {
        mShowPreference = showPreference;
    }

    public class ActivityLifeCycleListener implements Application.ActivityLifecycleCallbacks {

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        }

        @Override
        public void onActivityStarted(Activity activity) {
        }

        @Override
        public void onActivityResumed(Activity activity) {
            ++resumed;
            setFloatBallVisible(true);
        }

        @Override
        public void onActivityPaused(Activity activity) {
            --resumed;
            if (!isApplicationInForeground()) {
                setFloatBallVisible(false);
            }
        }

        @Override
        public void onActivityStopped(Activity activity) {
        }

        @Override
        public void onActivityDestroyed(Activity activity) {
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        }
    }

    private void toast(String msg) {
        Toast.makeText(mxodosActivity, msg, Toast.LENGTH_SHORT).show();
    }

    private void addFloatMenuItem() {
        MenuItem terminalItem = new MenuItem(mxodosActivity.getDrawable(R.drawable.icon_menu_start_terminal_shape)) {
            @Override
            public void action() {
                boolean preState = mShowTerminal;
                mShowTerminal = !mShowTerminal;
                if (!preState) {
                    mxodosActivity.getMainContentView().setTerminalViewSwitchSlider(true);
                    toast(mxodosActivity.getString(R.string.open_terminal));
                } else {
                    mxodosActivity.getMainContentView().setTerminalViewSwitchSlider(false);
                    toast(mxodosActivity.getString(R.string.hide_terminal));
                }
                mFloatballManager.closeMenu();
            }
        };
        MenuItem stopItem = new MenuItem(mxodosActivity.getDrawable(R.drawable.icon_menu_kill_current_process_shape)) {
            @Override
            public void action() {
                mxodosActivity.stopDesktop();
                toast(mxodosActivity.getString(R.string.terminate_current_process));
                mFloatballManager.closeMenu();
            }
        };
        MenuItem gamePadItem = new MenuItem(mxodosActivity.getDrawable(R.drawable.icon_menu_game_pad_shape)) {
            @Override
            public void action() {
                mxodosActivity.showInputControlsDialog();
                mFloatballManager.closeMenu();
            }
        };
        MenuItem unLockLayoutItem = new MenuItem(mxodosActivity.getDrawable(R.drawable.icon_menu_unlock_layout_shape)) {
            @Override
            public void action() {
                if (mLockSlider) {
                    mDrawable = mxodosActivity.getDrawable(R.drawable.icon_menu_unlock_layout_open_shape);
                } else {
                    mDrawable = mxodosActivity.getDrawable(R.drawable.icon_menu_unlock_layout_shape);
                }
                mLockSlider = !mLockSlider;
                mxodosActivity.getMainContentView().releaseSlider(true);
                toast(mxodosActivity.getString(R.string.unlock_layout));
                mFloatballManager.closeMenu();
            }
        };
        MenuItem keyboardItem = new MenuItem(mxodosActivity.getDrawable(R.drawable.icon_menu_show_keyboard_shape)) {
            @Override
            public void action() {
                if (mShowKeyboard) {
                    mDrawable = mxodosActivity.getDrawable(R.drawable.icon_menu_show_keyboard_open_shape);
                } else {
                    mDrawable = mxodosActivity.getDrawable(R.drawable.icon_menu_show_keyboard_shape);
                }
                mShowKeyboard = !mShowKeyboard;
                MainActivity.toggleKeyboardVisibility(mxodosActivity);
                mFloatballManager.closeMenu();
            }
        };
        MenuItem taskManagerItem = new MenuItem(mxodosActivity.getDrawable(R.drawable.icon_menu_show_task_manager_shape)) {
            @Override
            public void action() {
                mxodosActivity.showProcessManagerDialog();
                toast(mxodosActivity.getString(com.xodos.x11.R.string.task_manager));
                mFloatballManager.closeMenu();
            }
        };
        MenuItem settingItem = new MenuItem(mxodosActivity.getDrawable(R.drawable.icon_menu_show_setting_shape)) {
            @Override
            public void action() {
                boolean preState = mShowPreference;
                mShowPreference = !mShowPreference;
                if (!preState) {
                    mxodosActivity.getMainContentView().setX11PreferenceSwitchSlider(true);
                    toast(mxodosActivity.getString(com.xodos.x11.R.string.open_x11_settings));
                } else {
                    mxodosActivity.getMainContentView().setX11PreferenceSwitchSlider(false);
                    toast(mxodosActivity.getString(com.xodos.x11.R.string.hide_x11_settings));
                }
                mFloatballManager.closeMenu();
            }
        };
        mFloatballManager.addMenuItem(terminalItem)
            .addMenuItem(stopItem)
            .addMenuItem(keyboardItem)
            .addMenuItem(gamePadItem)
            .addMenuItem(unLockLayoutItem)
            .addMenuItem(taskManagerItem)
            .addMenuItem(settingItem)
            .buildMenu();
    }

    private void setFloatBallVisible(boolean visible) {
        if (visible) {
//            mFloatballManager.show();
            mAppNotOnFront = false;
        } else {
//            mFloatballManager.hide();
            mAppNotOnFront = true;
        }
    }

    public boolean isApplicationInForeground() {
        return resumed > 0;
    }

    public void onDestroy() {
        onDetachedFromWindow();
        //unregister ActivityLifeCycle listener once register it, in case of memory leak
        mxodosActivity.getApplication().unregisterActivityLifecycleCallbacks(mActivityLifeCycleListener);
    }

    public boolean isGlobalFloatBallMenu() {
        return mFloatballManager.isFloatBallOverOtherApp();
    }
}
