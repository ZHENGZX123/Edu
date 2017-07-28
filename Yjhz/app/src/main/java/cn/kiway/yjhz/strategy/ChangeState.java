package cn.kiway.yjhz.strategy;

import android.util.Log;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;

/**
 * Created by arvin on 2017/3/1 0001.
 */

public class ChangeState {

    private static final String TAG = "TAG_ChangeState";

    public ChangeState(){

    }

    public static boolean hasRooted() {
        Process process = null;
        DataOutputStream out = null;
        try {
            process =  Runtime.getRuntime().exec("su");
            out = new DataOutputStream(process.getOutputStream());
            out.writeBytes("\n");
            out.writeBytes("exit\n");
            out.flush();
            process.waitFor();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            Log.d(TAG, "Unexpected error - Here is what I know: "+e.getMessage());
            return false;
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.d(TAG, "Unexpected error - Here is what I know: "+e.getMessage());
        } finally {
            try {
                if (out != null)
                    out.close();
                process.destroy();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    public static boolean isRoot(){
        boolean bool = false;

        try{
            if ((!new File("/system/bin/su").exists()) &&
                    (!new File("/system/xbin/su").exists())){
                bool = false;
            } else {
                bool = true;
            }
            Log.d(TAG, "bool = " + bool);
        } catch (Exception e) {

        }
        return bool;
    }
}
