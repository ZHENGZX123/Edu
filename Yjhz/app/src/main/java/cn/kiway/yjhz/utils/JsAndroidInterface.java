package cn.kiway.yjhz.utils;

import android.content.Context;
import android.webkit.JavascriptInterface;

import cn.kiway.yjhz.activity.simulator.GSessionActivity;


/**
 * Created by Administrator on 2017/5/27.
 */

public class JsAndroidInterface {
    Context context;

    public JsAndroidInterface(Context context) {
        this.context = context;
    }

    @JavascriptInterface
    public void Login(String desJS) {
        try {
            String des=new DES("yjpt").decrypt(desJS);//  &
         ((GSessionActivity) context).login(des.split("&")[0],des.split("&")[1]);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @JavascriptInterface
    public void UserInfo(String desJS) {
        try {
            ((GSessionActivity) context).UserInfo(desJS.split("&")[0],desJS.split("&")[1]);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @JavascriptInterface
    public void BoxCode(String code) {
        try {
            ((GSessionActivity) context).BoxCode(code);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @JavascriptInterface
public void Cancle(){
    ((GSessionActivity) context).Cancle();
}

}
