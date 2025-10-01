package com.xodos.x11.utils;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.KeyEvent;
import android.view.KeyCharacterMap;

import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.xodos.x11.extrakeys.xodosExtraKeysView;
import com.xodos.x11.MainActivity;
import com.xodos.x11.R;

public class X11ToolbarViewPager {
    public static class PageAdapter extends PagerAdapter {

        final MainActivity mActivity;
        private final View.OnKeyListener mEventListener;

        public PageAdapter(MainActivity activity, View.OnKeyListener listen) {
            this.mActivity = activity;
            this.mEventListener = listen;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @SuppressLint("ClickableViewAccessibility")
        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup collection, int position) {
            LayoutInflater inflater = LayoutInflater.from(mActivity);
            View layout;
            if (position == 0) {
                layout = inflater.inflate(R.layout.display_view_terminal_toolbar_extra_keys, collection, false);
                xodosExtraKeysView xodosExtraKeysView = (xodosExtraKeysView) layout;
                mActivity.mExtraKeys = new xodosX11ExtraKeys(mEventListener, mActivity, xodosExtraKeysView);
                int mTerminalToolbarDefaultHeight = mActivity.getDisplayTerminalToolbarViewPager().getLayoutParams().height;
                int height = mTerminalToolbarDefaultHeight *
                        ((mActivity.mExtraKeys.getExtraKeysInfo() == null) ? 0 : mActivity.mExtraKeys.getExtraKeysInfo().getMatrix().length);
                xodosExtraKeysView.reload(mActivity.mExtraKeys.getExtraKeysInfo(), height);
                xodosExtraKeysView.setExtraKeysViewClient(mActivity.mExtraKeys);
                xodosExtraKeysView.setOnHoverListener((v, e) -> true);
                xodosExtraKeysView.setOnGenericMotionListener((v, e) -> true);
            } else {
                layout = inflater.inflate(R.layout.display_view_terminal_toolbar_text_input, collection, false);
                final EditText editText = layout.findViewById(R.id.display_terminal_toolbar_text_input);
                final Button back = layout.findViewById(R.id.display_terminal_toolbar_back_button);

                editText.setOnEditorActionListener((v, actionId, event) -> {
                    String textToSend = editText.getText().toString();
                    if (textToSend.length() == 0) textToSend = "\r";
                    KeyEvent e = new KeyEvent(0, textToSend, KeyCharacterMap.VIRTUAL_KEYBOARD, 0);
                    mEventListener.onKey(mActivity.getLorieView(), 0, e);

                    editText.setText("");
                    return true;
                });

                editText.setOnCapturedPointerListener((v2, e2) -> {
                    v2.releasePointerCapture();
                    return false;
                });

                back.setOnClickListener(v -> mActivity.getDisplayTerminalToolbarViewPager().setCurrentItem(0, true));
                back.setTextColor(0xFFFFFFFF);
                back.setPadding(0, 0, 0, 0);
                back.setBackground(new ColorDrawable(Color.BLACK) {
                    public boolean isStateful() {
                        return true;
                    }
                    public boolean hasFocusStateSpecified() {
                        return true;
                    }
                });
                back.setOnTouchListener((view, event) -> {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            view.setBackgroundColor(0xFF7F7F7F);
                            break;

                        case MotionEvent.ACTION_UP:
                        case MotionEvent.ACTION_CANCEL:
                            view.setBackgroundColor(0x00000000);
                            break;
                    }
                    return false;
                });
            }
            collection.addView(layout);
            return layout;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup collection, int position, @NonNull Object view) {
            collection.removeView((View) view);
        }

    }

    public static class OnPageChangeListener extends ViewPager.SimpleOnPageChangeListener {

        final MainActivity act;
        final ViewPager mTerminalToolbarViewPager;

        public OnPageChangeListener(MainActivity activity, ViewPager viewPager) {
            this.act = activity;
            this.mTerminalToolbarViewPager = viewPager;
        }

        @Override
        public void onPageSelected(int position) {
            if (position == 0) {
                act.getLorieView().requestFocus();
            } else {
                final EditText editText = mTerminalToolbarViewPager.findViewById(R.id.display_terminal_toolbar_text_input);
                if (editText != null) editText.requestFocus();
            }
        }
    }
}
