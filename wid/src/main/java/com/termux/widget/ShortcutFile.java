package com.xodos.widget;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Build;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.google.common.base.Joiner;
import com.xodos.shared.data.DataUtils;
import com.xodos.shared.file.FileUtils;
import com.xodos.shared.file.filesystem.FileType;
import com.xodos.shared.logger.Logger;
import com.xodos.shared.shell.ShellUtils;
import com.xodos.shared.xodos.xodosConstants.xodos_APP.xodos_SERVICE;
import com.xodos.shared.xodos.xodosConstants.xodos_WIDGET.xodos_WIDGET_PROVIDER;
import com.xodos.shared.xodos.xodosConstants.xodos_WIDGET;
import com.xodos.shared.xodos.xodosConstants;
import com.xodos.shared.xodos.file.xodosFileUtils;
import com.xodos.shared.xodos.settings.preferences.xodosWidgetAppSharedPreferences;
import com.xodos.widget.utils.ShortcutUtils;

import java.io.File;

public final class ShortcutFile {

    private static final String LOG_TAG = "ShortcutFile";

    public final String mPath;
    public String mLabel;

    public ShortcutFile(@NonNull String path) {
        this(path, null);
    }

    public ShortcutFile(@NonNull File file) {
        this(file.getAbsolutePath(), null);
    }

    public ShortcutFile(@NonNull File file, int depth) {
        this(file.getAbsolutePath(),
                (depth > 0 && file.getParentFile() != null ? (file.getParentFile().getName() + "/") : "") + file.getName());
    }

    public ShortcutFile(@NonNull String path, @Nullable String defaultLabel) {
        mPath = path;
        mLabel = getLabelForShortcut(defaultLabel);
    }

    @NonNull
    public String getPath() {
        return mPath;
    }

    @NonNull
    public String getCanonicalPath() {
        return FileUtils.getCanonicalPath(getPath(), null);
    }

    @NonNull
    public String getUnExpandedPath() {
        return xodosFileUtils.getUnExpandedxodosPath(getCanonicalPath());
    }

    @NonNull
    public String getLabel() {
        return mLabel;
    }

    @NonNull
    public String getLabelForShortcut(@Nullable String defaultLabel) {
        if (!DataUtils.isNullOrEmpty(defaultLabel))
            return defaultLabel;
        else
            return ShellUtils.getExecutableBasename(mPath);
    }

    public Intent getExecutionIntent(Context context) {
        Uri scriptUri = new Uri.Builder().scheme(xodos_SERVICE.URI_SCHEME_SERVICE_EXECUTE).path(getPath()).build();
        Intent executionIntent = new Intent(context, xodosLaunchShortcutActivity.class);
        executionIntent.setAction(xodos_SERVICE.ACTION_SERVICE_EXECUTE); // Mandatory for pinned shortcuts
        executionIntent.setData(scriptUri);
     //   executionIntent.putExtra(xodosConstants.xodos_WIDGET.EXTRA_TOKEN_NAME, xodosWidgetAppSharedPreferences.getGeneratedToken(context));
        return executionIntent;
    }

    @RequiresApi(api = Build.VERSION_CODES.N_MR1)
    public ShortcutInfo getShortcutInfo(Context context, boolean showToastForIconUsed) {
        ShortcutInfo.Builder builder = new ShortcutInfo.Builder(context, getPath());
        builder.setIntent(getExecutionIntent(context));
        builder.setShortLabel(getLabel());

        // Set icon if existent.
        File shortcutIconFile = getIconFile(context, showToastForIconUsed);
        if (shortcutIconFile != null)
            builder.setIcon(Icon.createWithBitmap(((BitmapDrawable) Drawable.createFromPath(shortcutIconFile.getAbsolutePath())).getBitmap()));
        else
            builder.setIcon(Icon.createWithResource(context, R.drawable.ic_launcher));

        return builder.build();
    }

    public Intent getStaticShortcutIntent(Context context) {
        Intent intent = new Intent();
        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, getExecutionIntent(context));
        intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, getLabel());

        // Set icon if existent.
        File shortcutIconFile = getIconFile(context, true);
        if (shortcutIconFile != null)
            intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, ((BitmapDrawable) Drawable.createFromPath(shortcutIconFile.getAbsolutePath())).getBitmap());
        else
            intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, Intent.ShortcutIconResource.fromContext(context, R.drawable.ic_launcher));

        return intent;
    }

    public RemoteViews getListWidgetView(Context context) {
        // Position will always range from 0 to getCount() - 1.
        // Construct remote views item based on the item xml file and set text based on position.
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_item);
        remoteViews.setTextViewText(R.id.widget_item, getLabel());

        // Next, we set a fill-intent which will be used to fill-in the pending intent template
        // which is set on the collection view in xodosAppWidgetProvider.
        Intent fillInIntent = new Intent().putExtra(xodos_WIDGET_PROVIDER.EXTRA_FILE_CLICKED, getPath());
        remoteViews.setOnClickFillInIntent(R.id.widget_item_layout, fillInIntent);

        return remoteViews;
    }

    @Nullable
    private File getIconFile(Context context, boolean showToastForIconUsed) {
        String errmsg;
        String shortcutIconFilePath = xodosConstants.xodos_SHORTCUT_SCRIPT_ICONS_DIR_PATH +
                "/" + ShellUtils.getExecutableBasename(getPath()) + ".png";

        FileType fileType = FileUtils.getFileType(shortcutIconFilePath, true);
        //  Ensure file or symlink points to a regular file that exists
        if (fileType != FileType.REGULAR) {
            if (fileType != FileType.NO_EXIST) {
                errmsg = context.getString(R.string.error_icon_not_a_regular_file, fileType.getName()) +
                        "\n" + context.getString(R.string.msg_icon_absolute_path, shortcutIconFilePath);
                Logger.logErrorAndShowToast(context, LOG_TAG, errmsg);
            }
            return null;
        }

        // Do not allow shortcut icons files not under SHORTCUT_ICONS_FILES_ALLOWED_PATHS_LIST
        if (!FileUtils.isPathInDirPaths(shortcutIconFilePath, ShortcutUtils.SHORTCUT_ICONS_FILES_ALLOWED_PATHS_LIST, true)) {
            errmsg = context.getString(R.string.error_icon_not_under_shortcut_icons_directories,
                    Joiner.on(", ").skipNulls().join(xodosFileUtils.getUnExpandedxodosPaths(ShortcutUtils.SHORTCUT_ICONS_FILES_ALLOWED_PATHS_LIST))) +
                    "\n" + context.getString(R.string.msg_icon_absolute_path, shortcutIconFilePath);
            Logger.logErrorAndShowToast(context, LOG_TAG, errmsg);
            return null;
        }

        Logger.logInfo(LOG_TAG, "Using file at \"" + shortcutIconFilePath + "\" as shortcut icon file");
        if (showToastForIconUsed) {
            Logger.showToast(context, context.getString(R.string.msg_shortcut_icon_file_used, shortcutIconFilePath), true);
        }

        return new File(shortcutIconFilePath);
    }

}
