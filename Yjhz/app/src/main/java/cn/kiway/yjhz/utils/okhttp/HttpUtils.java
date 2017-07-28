package cn.kiway.yjhz.utils.okhttp;

import java.util.HashMap;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by Administrator on 2017/6/28.
 */

public class HttpUtils {


    /**
     * post请求参数
     *
     * @param url 请求地址
     * @param map 请求参数集合
     */
    public static Request post(String url, HashMap<String, String> map) {
        /**
         * 创建请求的参数body
         */
        FormBody.Builder builder = new FormBody.Builder();
        /**
         * 遍历key
         */
        if (null != map) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                System.out.println("Key = " + entry.getKey() + ", Value = "
                        + entry.getValue());
                builder.add(entry.getKey(), entry.getValue().toString());
            }
        }
        RequestBody body = builder.build();
        return new Request.Builder().url(url).post(body).build();
    }

    /**
     * post请求参数
     *
     * @param url 请求地址
     * @param map 请求参数集合
     * @param tag 加入携带数据
     */
    public static Request post(String url, HashMap<String, String> map, Object tag) {
        /**
         * 创建请求的参数body
         */
        FormBody.Builder builder = new FormBody.Builder();
        /**
         * 遍历key
         */
        if (null != map) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                System.out.println("Key = " + entry.getKey() + ", Value = "
                        + entry.getValue());
                builder.add(entry.getKey(), entry.getValue().toString());
            }
        }
        RequestBody body = builder.build();
        return new Request.Builder().url(url).post(body).tag(tag).build();
    }

    /**
     * post请求参数
     *
     * @param url        请求地址
     * @param map        请求参数集合
     * @param httpHeader 请求头
     */
    public static Request post(String url, HashMap<String, String> map, HashMap<String, String> httpHeader) {
        /**
         * 创建请求的参数body
         */
        FormBody.Builder builder = new FormBody.Builder();
        /**
         * 遍历key
         */
        if (null != map) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                System.out.println("Key = " + entry.getKey() + ", Value = "
                        + entry.getValue());
                builder.add(entry.getKey(), entry.getValue().toString());
            }
        }
        Request.Builder request = new Request.Builder();
        if (null != httpHeader) {
            for (Map.Entry<String, String> entry : httpHeader.entrySet()) {
                System.out.println("httpHeader = " + entry.getKey() + ", httpHeader = "
                        + entry.getValue());
                request.addHeader(entry.getKey(), entry.getValue());
            }
        }
        RequestBody body = builder.build();
        request.url(url);
        request.post(body);
        return request.build();
    }

    /**
     * get 请求
     *
     * @param url 请求地址
     */
    public static Request get(String url) {
        return new Request.Builder()
                .url(url)
                .build();
    }

    /**
     * get 请求
     *
     * @param url 请求地址
     * @param tag 加入携带参数
     */
    public static Request get(String url, Object tag) {
        return new Request.Builder()
                .url(url).tag(tag)
                .build();
    }

    /**
     * get 请求
     *
     * @param url 请求地址
     */
    public static Request get(String url, String session) {
        return new Request.Builder()
                .url(url).addHeader("Cookie", session)
                .build();
    }
}
