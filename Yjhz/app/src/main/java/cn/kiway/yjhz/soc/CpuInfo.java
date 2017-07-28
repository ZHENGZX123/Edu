package cn.kiway.yjhz.soc;

import android.annotation.SuppressLint;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;

/**
 * Created by arvin on 2017/2/20 0020.
 */

public class CpuInfo {
    private static String TAG = "kiway_CpuInfo";
    private static CpuInfo mCpuInfo;
    private ProcessBuilder cmd;
    private String cat = "/system/bin/cat";
    private String maxPath = "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq";
    private String minPath = "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_min_freq";
    private String curPath = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq";
    private String na = "N/A";
    private String Ghz = "GHz";
    private int mp = 1000 * 1000;

    private FileReader fr = null;
    private BufferedReader br = null;

    private DecimalFormat df;


    @SuppressLint("NewApi")
    public CpuInfo() {
        df = new DecimalFormat("###.0");
    }


    public static CpuInfo getDefault() {
        return mCpuInfo == null ? mCpuInfo = new CpuInfo() : mCpuInfo;
    }

    public String getCpuMaxFrep() {

        return getReadCpuInfo(maxPath);
    }
    public String getCpuMinFrep() {
        return getReadCpuInfo(minPath);
    }
    public String getCpuCurFrep() {
        return getReadCpuInfo(curPath);
    }
    @SuppressLint("NewApi")
    private String getReadCpuInfo(String path) {
        float cur = 0.0f;
        String result = "";
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
            result = result.split("\\s+")[0];
        } catch (IOException e) {
            e.printStackTrace();
            cur = -1;
        } finally {
            try {
                if (fr != null) {
                    fr.close();
                }
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(result != null) {
            Log.d(TAG, "result: " + result);
            cur = Float.valueOf(result);
        }
        return cur <= 0 ? na : df.format(cur / mp) + Ghz;
    }
}
