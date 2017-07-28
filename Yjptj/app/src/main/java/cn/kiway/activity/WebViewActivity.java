package cn.kiway.activity;

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
import cn.kiway.Yjptj.R;
import cn.kiway.dialog.IsNetWorkDialog;
import cn.kiway.utils.AppUtil;
import cn.kiway.utils.ViewUtil;

public class WebViewActivity extends BaseNetWorkActicity implements
		SwipeRefreshLayout.OnRefreshListener {
	WebView webView;
	String url;
	String title;
	Map<String, String> map = new HashMap<>();
	/**
	 * 刷新
	 * */
	SwipeRefreshLayout swipeLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (!AppUtil.isNetworkAvailable(this)) {
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
		if (app.getCookie() != null) {
			map.put("cookie", "JSESSIONID=" + app.getCookie().getValue());
		}
		try {
			initView();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("deprecation")
	@SuppressLint("SetJavaScriptEnabled")
	void initView() throws Exception {
		setContentView(R.layout.activity_webview);
		swipeLayout = ViewUtil.findViewById(this, R.id.refresh_layout);
		swipeLayout.setOnRefreshListener(this);
		swipeLayout.setColorScheme(R.color._00cc99, R.color._12b7f5,
				R.color._00cc99, R.color._12b7f5);
		webView = ViewUtil.findViewById(this, R.id.webview);
		WebSettings webSettings = webView.getSettings();
		if (AppUtil.isNetworkAvailable(this)) {// 判断网络是否可用，可用不优先加载缓存，不可用加载本地缓存
			webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
		} else {
			webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
		}
		webSettings.setJavaScriptEnabled(true);
		webSettings.setAllowFileAccess(true);
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
			if (newProgress == 100) {
				if (webView.getUrl() == null)
					return;
				swipeLayout.setRefreshing(false);
			} else {
				if (!swipeLayout.isRefreshing())
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
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
			webView.goBack();
			return true;
		}
		finish();
		return false;
	}

	@Override
	public void onRefresh() {
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
