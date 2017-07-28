package cn.kiway.remote.monitor.wifi;

/**
 * Created by arvin on 2017/3/6 0006.
 */

public class KiWifiInfo {


    private String ssid;
    private String passwd;
    private String school;
    private String area;


    public KiWifiInfo(String ssid, String passwd, String school, String area) {
        this.ssid = ssid;
        this.passwd = passwd;
        this.school = school;
        this.area = school;

    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }


    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }


    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }


}
