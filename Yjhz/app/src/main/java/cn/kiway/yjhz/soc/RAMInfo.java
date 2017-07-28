package cn.kiway.yjhz.soc;

import android.annotation.SuppressLint;
import java.text.DecimalFormat;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStream;

/**
 * Created by arvin on 2017/2/20 0020.
 */

public class RAMInfo {

    private static String TAG = "kiway_RAMInfo";
    private static RAMInfo mRAMInfo;
    private ProcessBuilder cmd;
    private String cat = "/system/bin/cat";
    private String ramPath = "/proc/meminfo";

    private FileReader fr = null;
    private BufferedReader br = null;


    @SuppressLint("NewApi")
    public RAMInfo() {

    }

    public static RAMInfo getDefault() {
        return mRAMInfo == null ? mRAMInfo = new RAMInfo() : mRAMInfo;
    }

    public String getRamSize() {

        return readRamSize(ramPath);
    }

    @SuppressLint("NewApi")
    private String readRamSize(String path) {
        String result = null;
        float size =0.000f;

        try {
            String[] args = { cat, path };
            cmd = new ProcessBuilder(args);
            Process process = cmd.start();

            InputStream in = process.getInputStream();
            byte[] re = new byte[24];
            while (in.read(re) != -1) {
                result = result + new String(re);
            }
            in.close();
            result = result.split("\\s+")[1];

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (result != null) {
            Log.d(TAG, "result: " + result);
            DecimalFormat df = new DecimalFormat("###");
            size =  Float.valueOf(result);
            //从meminfo中读出的1G内存的数值.
            return (size >= 1024948) ? df.format(((size) / 1024) / 1024) + "G"
                    : (size == 0) ? " N/A" : df.format(((size) / 1024))
                    + "MB";
        }
        return null;
    }

}
