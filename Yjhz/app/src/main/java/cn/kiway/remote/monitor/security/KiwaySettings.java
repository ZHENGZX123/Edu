package cn.kiway.remote.monitor.security;

import java.io.File;

/**
 * Created by arvin on 2017/3/10 0010.
 */

public class KiwaySettings {

    private String gesture = "/data/system/gesture.key";
    private String password = "/data/system/password.key";

    public KiwaySettings(){

    }


    public void restartPasswd(){
        File file = new File(gesture);
        if(file != null && file.exists() && file.canWrite()&& file.canRead()){
            file.delete();
        }

        file = new File(password);
        if(file != null && file.exists() && file.canWrite()&& file.canRead()){
            file.delete();
        }
    }

    public static void restoreFactory(){

    }


}
