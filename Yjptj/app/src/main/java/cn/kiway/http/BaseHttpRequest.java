package cn.kiway.http;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Message;
import android.util.Log;
import cn.kiway.activity.BaseActivity;
import cn.kiway.utils.Logger;

/**
 * 网络请求
 * 
 * @author Zao
 */
public class BaseHttpRequest extends Thread {
	/**
	 * HTTP请求地址
	 */
	String requestUrl;
	/**
	 * HTTP请求客户端
	 */
	HttpClient httpClient = new DefaultHttpClient();
	/**
	 * HTTP POST请求
	 */
	HttpPost httpPost;
	/**
	 * HTTP 请求回调
	 */
	BaseHttpHandler handler;
	/**
	 * 是否继续执行回调操作
	 */
	boolean requesting;
	/**
	 * HTTP 请求参数
	 */
	Object params;
	/**
	 * 同时请求同一个地址的第几次请求
	 */
	int which;
	/**
	 * HTTP请求的附带参数
	 */
	Map<String, Object> map;
	/**
	 * 是否展现等待进度
	 */
	boolean isShowLoad;
	/**
	 * HTTP 请求标记
	 */
	String requestTag;
	/**
	 * 应用标题
	 * */
	private String app = "yjptj";
	/**
	 * 获得的cookie
	 * **/
	CookieStore cookieStore;

	public boolean isRequesting() {
		return requesting;
	}

	public void setRequesting(boolean requesting) {
		this.requesting = requesting;
	}

	public boolean isShowLoad() {
		return isShowLoad;
	}

	@SuppressWarnings("unused")
	private BaseHttpRequest() {
	}

	/**
	 * @param requestUrl
	 *            请求地址
	 * @param params
	 *            请求参数 支持(protobuf,Map<String,String>,JSONObject)
	 * @param handler
	 *            请求回调函数
	 * @param which
	 *            第几次请求
	 * @param map
	 *            请求的附带参数
	 * @param isShowLoad
	 *            是否展现等待进度
	 */
	public BaseHttpRequest(String requestUrl, Object params,
			BaseHttpHandler handler, int which, Map<String, Object> map,
			String requestTag, boolean isShowLoad) throws Exception {
		this.requestUrl = requestUrl;
		this.params = params;
		this.handler = handler;
		this.which = which;
		this.map = map;
		this.requestTag = requestTag;
		this.isShowLoad = isShowLoad;
		httpClient.getParams().setParameter(
				CoreConnectionPNames.CONNECTION_TIMEOUT, 20 * 1000);
		httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,
				20 * 1000);
		httpPost = new HttpPost(requestUrl);
		httpPost.setHeader("Content-Type",
				"application/x-www-form-urlencoded; charset=utf-8");
		if (params instanceof Map) {// Map<String,String>
			List<BasicNameValuePair> list = new ArrayList<>();
			@SuppressWarnings({ "unchecked", "rawtypes" })
			Set<Map.Entry<String, String>> set = ((Map) params).entrySet();
			for (Map.Entry<String, String> entry : set) {
				list.add(new BasicNameValuePair(entry.getKey(), entry
						.getValue()));
			}
			httpPost.setEntity(new UrlEncodedFormEntity(list, HTTP.UTF_8));
		} else if (params instanceof JSONObject) {// JSONObject
			httpPost.setEntity(new StringEntity(((JSONObject) params)
					.toString()));
		}
		addCookies();// 添加头部
		setRequesting(true);
	}

	private void excute() throws Exception {
		HttpResponse httpResponse = null;
		try {
			httpResponse = httpClient.execute(httpPost);
		} catch (Exception e) {
			e.printStackTrace();
			if (handler != null && isRequesting())
				handler.sendEmptyMessage(HttpResponseMsgType.RESPONSE_ERR);
		}
		Logger.log("request url :" + requestUrl);
		Logger.log("request params :" + params);
		if (httpResponse != null && httpResponse.getStatusLine() != null)
			Logger.log("response code :"
					+ httpResponse.getStatusLine().getStatusCode());
		if (httpResponse != null
				&& httpResponse.getStatusLine() != null
				&& httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			HttpEntity httpEntity = httpResponse.getEntity();
			saveCookies();// 保存获取的cookies
			if (httpEntity != null) {
				byte[] b = EntityUtils.toByteArray(httpEntity);
				Logger.log(new String(b));
				Log.e("---",new String(b));
				if (handler != null && isRequesting()) {
					try {
						Message message = new Message();
						message.what = HttpResponseMsgType.RESPONSE_SUCCESS;
						HttpResponseModel model = new HttpResponseModel(
								requestUrl, b, which, map);
						message.obj = model;
						handler.sendMessage(message);
					} catch (Exception e) {
						e.printStackTrace();
						handler.sendEmptyMessage(HttpResponseMsgType.RESPONSE_ERROR);
					}
				}
			} else {
				if (handler != null && isRequesting())
					handler.sendEmptyMessage(HttpResponseMsgType.RESPONSE_ERROR);
			}
		} else {
			if (handler != null && isRequesting())
				handler.sendEmptyMessage(HttpResponseMsgType.RESPONSE_ERROR);
		}
		httpPost.abort();
	}

	@Override
	public void run() {
		super.run();
		BaseHttpConnectPool.handler
				.sendEmptyMessage(BaseHttpConnectPool.OPEN_SUG);
		try {
			excute();
		} catch (Exception e) {
			e.printStackTrace();
		}
		BaseHttpConnectPool.httpRequests.remove(requestTag);
		BaseHttpConnectPool.handler
				.sendEmptyMessage(BaseHttpConnectPool.CLOSE_SUG);
	}

	/**
	 * 将返回的cookie保存起来
	 * 
	 * @param resp
	 * @throws JSONException
	 */
	@SuppressWarnings("unchecked")
	public void saveCookies() throws JSONException {
		cookieStore = ((AbstractHttpClient) httpClient).getCookieStore();
		List<Cookie> cookies = cookieStore.getCookies();
		try {
			@SuppressWarnings("rawtypes")
			Map appinfo = Global.apps.get(app);
			List<Map<String, String>> jsonArray = null;
			/*
			 * if (appinfo.containsKey("cookieStr")) { jsonArray =
			 * (List<Map<String, String>>) appinfo .get("cookieStr"); } else {
			 */
			jsonArray = new ArrayList<Map<String, String>>();
			for (Cookie cookie : cookies) {
				Map<String, String> obj = new HashMap<String, String>();
				obj.put("name", cookie.getName());
				obj.put("value", cookie.getValue());
				obj.put("path", cookie.getPath());
				obj.put("domain", cookie.getDomain());
				obj.put("expiryDate", cookie.getExpiryDate() == null ? "" : ""
						+ cookie.getExpiryDate());
				Log.d(cookie.getName(),
						cookie.getDomain() + "====" + cookie.getValue()
								+ "====" + cookie.getPath() + "====="
								+ cookie.getExpiryDate());
				jsonArray.add(obj);
				BaseActivity.baseActivityInsantnce.app.setCookie(cookie);
			}
			// }
			// 持久化cookie
			appinfo.put("cookieStr", jsonArray);
			Global.apps.put(app, appinfo);
			Logger.log("获取到cokie" + jsonArray);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 给http头部增加Cookie
	 * 
	 * @param request
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void addCookies() {
		try {
			Map appinfo = Global.apps.get(app);
			cookieStore = ((AbstractHttpClient) this.httpClient)
					.getCookieStore();
			if (appinfo == null || !appinfo.containsKey("cookieStr")) {
				return;
			}
			List<Map<String, String>> jsonArray = null;
			try {
				jsonArray = (List<Map<String, String>>) appinfo
						.get("cookieStr");
			} catch (Exception e) {
				appinfo.remove("cookieStr");
				Global.apps.put(app, appinfo);
			}
			for (int i = 0; i < jsonArray.size(); i++) {
				Map<String, String> obj = jsonArray.get(i);
				String name = obj.get("name");
				String value = obj.get("value");
				String path = obj.get("path");
				String domain = obj.get("domain");
				String expiryDate = obj.get("expiryDate");
				BasicClientCookie cookie = new BasicClientCookie(name, value);
				cookie.setDomain(domain);
				cookie.setPath(path);
				if (expiryDate != null && !expiryDate.equals("")) {
					SimpleDateFormat format = new SimpleDateFormat(
							"EEE MMM dd HH:mm:ss 'GMT' yyyy", Locale.US);
					Date date = format.parse(expiryDate);
					cookie.setExpiryDate(date);
				}
				cookieStore.addCookie(cookie);
			}
			cookieStore.clearExpired(new Date());
			((AbstractHttpClient) httpClient).setCookieStore(cookieStore);
			Logger.log(app + "添加http头部的" + cookieStore.toString());
		} catch (Exception e) {
		}
	}
}
