package cn.kiway.activity.main;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.cookie.Cookie;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import cn.kiway.IConstant;
import cn.kiway.Yjpty.R;
import cn.kiway.activity.BaseNetWorkActicity;
import cn.kiway.dialog.IsNetWorkDialog;
import cn.kiway.utils.AppUtil;
import cn.kiway.utils.ViewUtil;

@SuppressWarnings("deprecation")
public class WebViewActivity extends BaseNetWorkActicity implements
		SwipeRefreshLayout.OnRefreshListener {
	WebView webView;
	String url;// 加载的url
	String title;// 标题的文字
	Map<String, String> map = new HashMap<>();// 请求时携带sessionid
	/**
	 * 刷新
	 * */
	SwipeRefreshLayout swipeLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (!AppUtil.isNetworkAvailable(this)) {// 判断是否有网
			newWorkdialog = new IsNetWorkDialog(context, this,
					resources.getString(R.string.dqsjmylrhlwqljhlwl),
					resources.getString(R.string.ljhlw));
			if (newWorkdialog != null && !newWorkdialog.isShowing()) {
				newWorkdialog.show();
				return;
			}
		}
		url = bundle.getString(IConstant.BUNDLE_PARAMS);
		title = bundle.getString(IConstant.BUNDLE_PARAMS1);
		try {
			if (app.getCookie() != null) {
				map.put("cookie", "JSESSIONID=" + app.getCookie().getValue());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			initView();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressLint({ "SetJavaScriptEnabled", "NewApi" })
	public void initView() throws Exception {
		setContentView(R.layout.activity_webview);
		swipeLayout = ViewUtil.findViewById(this, R.id.refresh_layout);
		swipeLayout.setOnRefreshListener(this);
		swipeLayout.setColorScheme(R.color._00cc99, R.color._12b7f5,
				R.color._00cc99, R.color._12b7f5);// 设置加载的时候的上方的颜色
		webView = ViewUtil.findViewById(this, R.id.webview);
		WebSettings webSettings = webView.getSettings();
		if (AppUtil.isNetworkAvailable(this)) {// 判断网络是否可用，可用不优先加载缓存，不可用加载本地缓存
			webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
		} else {
			webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
		}
		// 设置字符编码
		webSettings.setDefaultTextEncodingName("utf-8");
		webSettings.setJavaScriptEnabled(true);
		webSettings.setLoadsImagesAutomatically(true);
		webSettings.setLoadWithOverviewMode(true);
		webSettings.setDomStorageEnabled(true);
		webView.loadUrl(url, map);
		webView.setWebViewClient(new webViewClient());
		webView.setWebChromeClient(new webChromeClient());
		ViewUtil.setContent(this, R.id.title, title);
		findViewById(R.id.previos).setOnClickListener(this);
	}

	private class webViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			try {
				CookieSyncManager.createInstance(WebViewActivity.this);// 设置cookie
				CookieManager cookieManager = CookieManager.getInstance();
				Cookie sessionCookie = app.getCookie();
				if (sessionCookie != null) {
					String cookieString = sessionCookie.getName() + "="
							+ sessionCookie.getValue() + ";domain="
							+ sessionCookie.getDomain();
					cookieManager.setCookie(url, cookieString);
					CookieSyncManager.getInstance().sync();
				}
				view.loadUrl(url, map);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return true;
		}

		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {
			super.onReceivedError(view, errorCode, description, failingUrl);
			findViewById(R.id.refresh_layout).setVisibility(View.GONE);
			findViewById(R.id.no_data).setVisibility(View.VISIBLE);
		}
	}

	public class webChromeClient extends WebChromeClient {
		@Override
		public void onProgressChanged(WebView view, int newProgress) {
			if (newProgress == 100) {// 判断是否加载完成
				if (webView.getUrl() == null)// 当没有获取到当前加载的url时则返回，否则下方判断会报错
					return;
				swipeLayout.setRefreshing(false);// 停止刷新
			} else {
				if (!swipeLayout.isRefreshing())// 如果没有开启刷新的动作，则开启
					swipeLayout.setRefreshing(true);
			}
			super.onProgressChanged(view, newProgress);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {// 点击返回键的是否判断网页是否能返回
		if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
			webView.goBack();
			return true;
		}
		finish();
		return false;
	}

	@Override
	public void onRefresh() {// 刷新页面时重新加载当前的url
		webView.loadUrl(webView.getUrl(), map);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.previos:
			if (webView.canGoBack()) {
				webView.goBack();
			} else {
				finish();
			}
			break;
		}
	}
}
