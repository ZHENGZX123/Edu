package cn.kiway.remote.monitor;

import android.os.SystemClock;

/**
 * Created by arvin on 2017/3/8 0008.
 */

public class BootStatus {

    private static BootStatus mBootStatus;

    public BootStatus() {

    }

    public BootStatus getInstance() {
        return mBootStatus == null ? mBootStatus = new BootStatus() : mBootStatus;
    }

    public String getUptimeMillis() {
        return formatDur(SystemClock.uptimeMillis());
    }

    public String getElapsedRealTime() {
        return formatDur(SystemClock.elapsedRealtime());
    }

    public String formatDur(Long mss) {
        //long days = mss / (1000 * 60 * 60 * 24);
        //long hours = (mss % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
        long hours = mss / (1000 * 60 * 60);
        long minutes = (mss % (1000 * 60 * 60)) / (1000 * 60);
        long seconds = (mss % (1000 * 60)) / 1000;

        return hours + ":" + minutes + ":" + seconds;
    }


}
