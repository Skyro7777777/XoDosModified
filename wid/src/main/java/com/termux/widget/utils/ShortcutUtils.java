package com.xodos.widget.utils;

import android.content.Context;
import android.content.pm.ShortcutManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.xodos.shared.file.FileUtils;
import com.xodos.shared.logger.Logger;
import com.xodos.shared.xodos.xodosConstants;
import com.xodos.shared.xodos.xodosUtils;
import com.xodos.widget.NaturalOrderComparator;
import com.xodos.widget.R;
import com.xodos.widget.ShortcutFile;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.List;

public class ShortcutUtils {

    public static final int xodos_SHORTCUTS_SCRIPTS_DIR_MAX_SEARCH_DEPTH = 5;

    /* Allowed paths under which shortcut files can exist. */
    public static final List<String> SHORTCUT_FILES_ALLOWED_PATHS_LIST = Arrays.asList(
            xodosConstants.xodos_SHORTCUT_SCRIPTS_DIR_PATH,
            xodosConstants.xodos_DATA_HOME_DIR_PATH);

    /* Allowed paths under which shortcut icons files can exist. */
    public static final List<String> SHORTCUT_ICONS_FILES_ALLOWED_PATHS_LIST = Arrays.asList(
            xodosConstants.xodos_SHORTCUT_SCRIPT_ICONS_DIR_PATH,
            xodosConstants.xodos_DATA_HOME_DIR_PATH);

    public static final FileFilter SHORTCUT_FILES_FILTER = new FileFilter() {
        public boolean accept(File file) {
            if (file.getName().startsWith(".")) {
                // Do not show hidden files starting with a dot.
                return false;
            } else if (!FileUtils.fileExists(file.getAbsolutePath(), true)) {
                // Do not show broken symlinks
                return false;
            } else if (!FileUtils.isPathInDirPaths(file.getAbsolutePath(), SHORTCUT_FILES_ALLOWED_PATHS_LIST, true)) {
                // Do not show files that are not under SHORTCUT_FILES_ALLOWED_PATHS_LIST
                return false;
            } else if (xodosConstants.xodos_SHORTCUT_SCRIPTS_DIR.equals(file.getParentFile()) &&
                    file.getName().equals(xodosConstants.xodos_SHORTCUT_SCRIPT_ICONS_DIR_BASENAME)) {
                // Do not show files under xodos_SHORTCUT_SCRIPT_ICONS_DIR_PATH
                return false;
            }
            return true;
        }
    };

    public static void enumerateShortcutFiles(List<ShortcutFile> files, boolean sorted) {
        enumerateShortcutFiles(files, xodosConstants.xodos_SHORTCUT_SCRIPTS_DIR, sorted, 0);
    }

    public static void enumerateShortcutFiles(List<ShortcutFile> files, File dir, boolean sorted) {
        enumerateShortcutFiles(files, dir, sorted, 0);
    }

    public static void enumerateShortcutFiles(List<ShortcutFile> files, File dir, boolean sorted, int depth) {
        if (depth > xodos_SHORTCUTS_SCRIPTS_DIR_MAX_SEARCH_DEPTH) return;

        File[] current_files = dir.listFiles(SHORTCUT_FILES_FILTER);

        if (current_files == null) return;

        if (sorted) {
            Arrays.sort(current_files, (lhs, rhs) -> {
                if (lhs.isDirectory() != rhs.isDirectory()) {
                    return lhs.isDirectory() ? 1 : -1;
                }
                return NaturalOrderComparator.compare(lhs.getName(), rhs.getName());
            });
        }

        for (File file : current_files) {
            if (file.isDirectory()) {
                enumerateShortcutFiles(files, file, sorted, depth + 1);
            } else {
                files.add(new ShortcutFile(file, depth));
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N_MR1)
    public static ShortcutManager getShortcutManager(@NonNull Context context, @NonNull String logTag, boolean showErrorToast) {
        ShortcutManager shortcutManager = (ShortcutManager) context.getSystemService(Context.SHORTCUT_SERVICE);
        if (shortcutManager == null)  {
            Logger.logErrorAndShowToast(showErrorToast ? context : null, logTag, context.getString(R.string.error_failed_to_get_shortcut_manager));
            return null;
        }
        return shortcutManager;
    }

    public static boolean isxodosAppAccessible(@NonNull Context context, @NonNull String logTag, boolean showErrorToast) {
        String errmsg = xodosUtils.isxodosAppAccessible(context);
        if (errmsg != null) {
            Logger.logErrorAndShowToast(showErrorToast ? context : null, logTag, errmsg);
            return false;
        }
        return true;
    }

}
