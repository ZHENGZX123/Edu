package cn.kiway.yjhz.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.provider.Settings;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import cn.kiway.yjhz.MainActivity;

public class SilentInstall {

    /**
     * 执行具体的静默安装逻辑，需要手机ROOT。
     *
     * @param apkPath 要安装的apk文件的路径
     * @return 安装成功返回true，安装失败返回false。
     */
    public boolean install(String apkPath) {
        openInstallApk(MainActivity.mainActivity);
        boolean result = false;
        DataOutputStream dataOutputStream = null;
        BufferedReader errorStream = null;
        try {
            // 申请su权限
            Process process = Runtime.getRuntime().exec("su");
            dataOutputStream = new DataOutputStream(process.getOutputStream());
            // 执行pm install命令
            String command = "pm install -r " + apkPath + "\n";
            dataOutputStream.write(command.getBytes(Charset.forName("utf-8")));
            dataOutputStream.flush();
            dataOutputStream.writeBytes("exit\n");
            dataOutputStream.flush();
            process.waitFor();
            errorStream = new BufferedReader(new InputStreamReader(
                    process.getErrorStream()));
            String msg = "";
            String line;
            // 读取命令的执行结果
            while ((line = errorStream.readLine()) != null) {
                msg += line;
            }
            Log.d("TAG", "install msg is " + msg);
            // 如果执行结果中包含Failure字样就认为是安装失败，否则就认为安装成功
            if (!msg.contains("Failure")) {
                result = true;
            }
        } catch (Exception e) {
            Log.e("TAG", e.getMessage(), e);
        } finally {
            try {
                if (dataOutputStream != null) {
                    dataOutputStream.close();
                }
                if (errorStream != null) {
                    errorStream.close();
                }
            } catch (IOException e) {
                Log.e("TAG", e.getMessage(), e);
            }
        }
        return result;
    }

    /**
     * 开启允许安装来自未知来源的应用
     */
    public static void openInstallApk(Context context) {
        ContentValues values = new ContentValues();
        values.put("value", 1);
        Cursor cursor = null;
        try {
            int value = 0;
            cursor = context.getContentResolver().query(Settings.Secure.CONTENT_URI, new String[]{"value",},
                    "name=?",
                    new String[]{Settings.Secure.INSTALL_NON_MARKET_APPS}, null);
            if (cursor != null && cursor.moveToNext()) {
                value = cursor.getInt(cursor.getColumnIndex("value"));
            }
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
            if (0 == value) {
                int i = context.getContentResolver().update(Settings.Secure.CONTENT_URI, values, "name=?",
                        new String[]{Settings.Secure.INSTALL_NON_MARKET_APPS});
                if (i > 0) {
                    Log.e("", "success");
                } else {
                    Log.e("", "fail");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
    }
}