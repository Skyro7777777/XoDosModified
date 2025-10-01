package com.xodos.app.terminal.utils;

import static com.xodos.shared.xodos.xodosConstants.xodos_FILES_DIR_PATH;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import com.xodos.app.xodosService;
import com.xodos.shared.xodos.xodosConstants;

import java.util.ArrayList;

public class CommandUtils {
    public static void  exec(Activity activity, String cmd, ArrayList<String> args){
        String fullCmd = xodos_FILES_DIR_PATH+"/usr/bin/"+cmd;
        Intent executeIntent = new Intent(xodosConstants.xodos_APP.xodos_SERVICE.ACTION_SERVICE_EXECUTE);
        executeIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        executeIntent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        Uri uri = Uri.parse(fullCmd);
        executeIntent.setData(uri);
        if (args!=null){
            executeIntent.putStringArrayListExtra(xodosConstants.xodos_APP.xodos_SERVICE.EXTRA_ARGUMENTS, args);
        }
        executeIntent.putExtra(xodosConstants.xodos_APP.xodos_SERVICE.EXTRA_BACKGROUND,true);
        executeIntent.putExtra(xodosConstants.xodos_APP.xodos_SERVICE.EXTRA_WORKDIR, xodos_FILES_DIR_PATH+"/usr/bin/");
        executeIntent.setClass(activity, xodosService.class);
        activity.startService(executeIntent);
    }
    public static void  execInPath(Activity activity, String cmd, ArrayList<String> args,String path){
        String fullCmd = xodos_FILES_DIR_PATH+path+cmd;
        Intent executeIntent = new Intent(xodosConstants.xodos_APP.xodos_SERVICE.ACTION_SERVICE_EXECUTE);
        executeIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        executeIntent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        Uri uri = Uri.parse(fullCmd);
        executeIntent.setData(uri);
        if (args!=null){
            executeIntent.putStringArrayListExtra(xodosConstants.xodos_APP.xodos_SERVICE.EXTRA_ARGUMENTS, args);
        }
        executeIntent.putExtra(xodosConstants.xodos_APP.xodos_SERVICE.EXTRA_BACKGROUND,true);
        executeIntent.putExtra(xodosConstants.xodos_APP.xodos_SERVICE.EXTRA_WORKDIR, xodos_FILES_DIR_PATH+path);
        executeIntent.setClass(activity, xodosService.class);
        activity.startService(executeIntent);
    }
}
