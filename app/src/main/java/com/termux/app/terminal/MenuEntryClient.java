package com.xodos.app.terminal;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

import android.app.AlertDialog;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.xodos.R;
import com.xodos.app.xodosActivity;
import com.xodos.app.terminal.utils.ScreenUtils;

public class MenuEntryClient implements FileBrowser.FileSlectedAdapter {
    private xodosActivity mxodosActivity;
    private xodosTerminalSessionActivityClient mxodosTerminalSessionActivityClient;
    private PopupWindow mPop;
    private PopupWindow mConfigPopWindow;
    private View mPopWindowConfigContent;
    private LinearLayout mainContentView;
    private GridLayout mGrideLayout;
    private MenuEntry mMenutryEntry;
    private FileBrowser mFileBrowser;

    private MenuEntryClient() {
    }

    public MenuEntryClient(xodosActivity activity, xodosTerminalSessionActivityClient xodosTerminalSessionActivityClient) {
        this.mxodosActivity = activity;
        this.mxodosTerminalSessionActivityClient = xodosTerminalSessionActivityClient;
        mMenutryEntry = new MenuEntry();
        mMenutryEntry.loadMenuItems();
        setToolBoxView();
        setToolboxConfig();
    }

    private void showAddMenuItem() {
//        PopupWindow mConfigPopWindow = AppUtils.showPopupWindow(mainContentView, mPopWindowConfigContent, 200, 240);
        mConfigPopWindow = new PopupWindow(mxodosActivity);
        mConfigPopWindow.setContentView(mPopWindowConfigContent);
        int x = 0;
        int y = 120;
        if (mxodosActivity.getExtraKeysView().getVisibility() == View.VISIBLE) {
            y += mxodosActivity.getExtraKeysView().getHeight();
        }
        mConfigPopWindow.setFocusable(true);
        mConfigPopWindow.showAtLocation(mxodosActivity.findViewById(R.id.left_drawer), Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, x, y);
    }

    private void setToolBoxView() {
        mPop = new PopupWindow(mxodosActivity);
        mPop.setBackgroundDrawable(mxodosActivity.getDrawable(R.drawable.tool_box_background));
        int width = ScreenUtils.getScreenWidth(mxodosActivity);
        mPop.setWidth(width);
        mPop.setHeight(width);
        ScrollView mToolBoxView = new ScrollView(mxodosActivity);
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        param.gravity = Gravity.CENTER | Gravity.TOP;
        mToolBoxView.setLayoutParams(param);
        GridLayout gridLayout = new GridLayout(mxodosActivity);
        mGrideLayout = gridLayout;
//        int r = 3;
        int c = 4;

        gridLayout.setColumnCount(c);
//        gridLayout.setRowCount(r);
        gridLayout.setVerticalScrollBarEnabled(true);
        LinearLayout.LayoutParams grideParam = new LinearLayout.LayoutParams(width, width);
        grideParam.gravity = Gravity.CENTER_VERTICAL;
        gridLayout.setLayoutParams(grideParam);
        gridLayout.setPadding(50, 8, 50, 8);

        mGrideLayout.removeAllViews();
        updateMenuItems(width, gridLayout);

        LinearLayout linearLayout = new LinearLayout(mxodosActivity);
        linearLayout.setLayoutParams(param);
        mainContentView = new LinearLayout(mxodosActivity);
        LinearLayout.LayoutParams mainContentViewLayoutParam = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        mainContentViewLayoutParam.gravity = Gravity.CENTER;
        mainContentView.setOrientation(LinearLayout.VERTICAL);
        mainContentView.setLayoutParams(mainContentViewLayoutParam);
        LinearLayout closeLinearLayout = new LinearLayout(mxodosActivity);
        closeLinearLayout.setGravity(Gravity.RIGHT);
        LinearLayout.LayoutParams closeLinearLayoutLayoutParam = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        closeLinearLayoutLayoutParam.setMargins(0, 5, 5, 5);
        closeLinearLayout.setLayoutParams(closeLinearLayoutLayoutParam);
        ImageButton closeButton = new ImageButton(mxodosActivity);
        closeButton.setBackground(mxodosActivity.getDrawable(R.drawable.ic_close));
        closeButton.setOnClickListener(v -> {
            mPop.dismiss();
        });
        closeLinearLayout.addView(closeButton);

        linearLayout.addView(gridLayout);
        mToolBoxView.addView(linearLayout);
        mainContentView.addView(closeLinearLayout);
        mainContentView.addView(mToolBoxView);
        mPop.setContentView(mainContentView);
        mxodosActivity.findViewById(R.id.toggle_tool_box).setOnClickListener(v -> {
            int x = 0;
            int y = 120;
            if (mxodosActivity.getExtraKeysView().getVisibility() == View.VISIBLE) {
                y += mxodosActivity.getExtraKeysView().getHeight();
            }
            mPop.showAtLocation(mxodosActivity.findViewById(R.id.left_drawer), Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, x, y);
        });
        mPop.setOnDismissListener(()->{
            mMenutryEntry.saveMenuItems();
        });
    }

    private void updateMenuItems(int width, GridLayout gridLayout) {
        LinearLayout recover = createImageButton("script", "Reinstall", width / 5);
        gridLayout.addView(recover);

        recover.setOnClickListener(v -> {
            LinearLayout linearLayout = new LinearLayout(mxodosActivity);
            CheckBox dInputCheckBox = new CheckBox(mxodosActivity);
            dInputCheckBox.setText(mxodosActivity.getText(R.string.set_dinput));
            linearLayout.addView(dInputCheckBox);
            CheckBox xInputCheckoutBox = new CheckBox(mxodosActivity);
            xInputCheckoutBox.setText(mxodosActivity.getText(R.string.set_xinput));
            linearLayout.addView(xInputCheckoutBox);
            AlertDialog.Builder builder = new AlertDialog.Builder(mxodosActivity);
            builder.setView(linearLayout);
            builder.setMessage(mxodosActivity.getText(R.string.ready_set_mobox_env))
                .setPositiveButton(mxodosActivity.getText(R.string.yes), (dialog, id) -> {
                    int option1 = 0b0001; // dinput
                    int option2 = 0b0010; // xinput
                    int flags = 0;
                    if (dInputCheckBox.isChecked()) {
                        flags |= option1;
                    }
                    if (xInputCheckoutBox.isChecked()) {
                        flags |= option2;
                    }
                    Integer mode = null;
                    if (flags != 0) {
                        mode = flags;
                    }
                    mxodosActivity.reInstallCustomStartScript(mode);
                })
                .setNegativeButton(mxodosActivity.getText(R.string.cancel), (dialog, id) -> {

                });

            AlertDialog dialog = builder.create();
            dialog.setTitle(mxodosActivity.getText(R.string.select_gamepad_input_mode));
            dialog.show();

            mPop.dismiss();
        });
        for (int i = 0; i < mMenutryEntry.getStartItemList().size(); i++) {
            LinearLayout button = createImageButton(mMenutryEntry.getStartItemList().get(i).getType(), mMenutryEntry.getStartItemList().get(i).getFileName(), width / 5);
            button.setLongClickable(true);
            String command = mMenutryEntry.getStartItemList().get(i).getCommand();
            if (command == null) {
                command = mMenutryEntry.getStartItemList().get(i).getPath();
            }
            final String cmd = command + "\n";
            button.setOnClickListener(v -> {
                mxodosTerminalSessionActivityClient.getCurrentStoredSessionOrLast().write(cmd);
            });
            int idx = i;
            button.setOnLongClickListener(v -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(mxodosActivity);
                builder.setMessage(mxodosActivity.getText(R.string.remove_shortcut))
                    .setPositiveButton(mxodosActivity.getText(R.string.yes), (dialog, id) -> {
                        mMenutryEntry.getStartItemList().remove(idx);
                        mGrideLayout.removeAllViews();
                        updateMenuItems(ScreenUtils.getScreenWidth(mxodosActivity), mGrideLayout);
                    })
                    .setNegativeButton(mxodosActivity.getText(R.string.cancel), (dialog, id) -> {

                    });

                AlertDialog dialog = builder.create();
                dialog.setTitle(mxodosActivity.getText(R.string.remove));
                dialog.show();
                return true;
            });
            gridLayout.addView(button);
        }
        LinearLayout but = createImageButton("add", mxodosActivity.getString(com.xodos.x11.R.string.add), width / 5);
        but.setGravity(Gravity.CENTER_VERTICAL);
        but.setOrientation(LinearLayout.VERTICAL);
        gridLayout.addView(but);
        but.setOnClickListener(v -> {
            showAddMenuItem();
        });
    }

    private void setToolboxConfig() {
        mPopWindowConfigContent = mxodosActivity.getLayoutInflater().inflate(R.layout.menu_launch_item, null);
        LinearLayout mStartItemEntriesConfig = mPopWindowConfigContent.findViewById(R.id.LConfigStartItems);


        ImageButton mAddConfigButton = mPopWindowConfigContent.findViewById(R.id.BConfig_item);

        mFileBrowser = new FileBrowser(mxodosActivity, this);
        mFileBrowser.init();
        mAddConfigButton.setOnClickListener(v -> {
            mFileBrowser.showFileBrowser(mStartItemEntriesConfig);
        });
        EditText command = mPopWindowConfigContent.findViewById(R.id.ETCommand);
        EditText title = mPopWindowConfigContent.findViewById(R.id.ETTitle);
        Button okButton = mPopWindowConfigContent.findViewById(R.id.BTOK);
        okButton.setOnClickListener(v -> {
            if (command.getText().toString().isEmpty()||
            title.getText().toString().isEmpty()){
                Toast.makeText(mxodosActivity,R.string.invalid_config,Toast.LENGTH_LONG);
                return;
            }
            MenuEntry.Entry entry = new MenuEntry.Entry();
            entry.setPath(command.getText().toString());
            entry.setFileName(title.getText().toString());
            entry.setIconPath("default");
            entry.setTitlle(title.getText().toString());
            entry.setCommand(command.getText().toString());
            entry.setType("executable");
            mMenutryEntry.addMenuEntry(entry);
            mGrideLayout.removeAllViews();
            updateMenuItems(ScreenUtils.getScreenWidth(mxodosActivity), mGrideLayout);
            mConfigPopWindow.dismiss();
        });
    }

    private LinearLayout createImageButton(String type, String title, int size) {
        LinearLayout layout = new LinearLayout(mxodosActivity);
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(size < 10 ? WRAP_CONTENT : size, size < 10 ? WRAP_CONTENT : size);
        param.setMargins(10, 10, 10, 10);
        layout.setLayoutParams(param);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER);
        ImageView v = new ImageView(mxodosActivity);
        LinearLayout.LayoutParams vParam = new LinearLayout.LayoutParams(MATCH_PARENT, 0);
        vParam.weight = 3;
        vParam.gravity = Gravity.CENTER_VERTICAL;
        v.setLayoutParams(vParam);
        switch (type) {
            case "script": {
                v.setImageDrawable(mxodosActivity.getDrawable(R.drawable.ic_script_click));
                break;
            }
            case "executable": {
                v.setImageDrawable(mxodosActivity.getDrawable(R.drawable.ic_executable_click));
                break;
            }
            case "short_cut": {
                v.setImageDrawable(mxodosActivity.getDrawable(R.drawable.ic_shortcut_click));
                break;
            }
            case "terminal": {
                v.setImageDrawable(mxodosActivity.getDrawable(R.drawable.ic_terminal_click));
                break;
            }
            case "add": {
                v.setImageDrawable(mxodosActivity.getDrawable(R.drawable.ic_add_click));
                break;
            }
            default:
                v.setImageDrawable(mxodosActivity.getDrawable(R.drawable.ic_code_click));
        }
        TextView tv = new TextView(mxodosActivity);
        LinearLayout.LayoutParams tvParam = new LinearLayout.LayoutParams(MATCH_PARENT, 0);
        tvParam.weight = 1;
        tv.setGravity(Gravity.CENTER);
        tv.setLayoutParams(tvParam);
        tv.setText(title);
        layout.addView(v);
        layout.addView(tv);
        return layout;
    }

    @Override
    public void onFileSelected(FileInfo fileInfo) {
        EditText command = mPopWindowConfigContent.findViewById(R.id.ETCommand);
        EditText title = mPopWindowConfigContent.findViewById(R.id.ETTitle);
        command.setText(fileInfo.getPath());
        title.setText(fileInfo.getName());
    }
}
