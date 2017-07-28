package cn.kiway.fragment.studio;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import cn.kiway.Yjptj.R;
import cn.kiway.fragment.BaseFragment;
import cn.kiway.utils.ViewUtil;

@SuppressLint("SetJavaScriptEnabled")
public class WebFragment extends BaseFragment {
    int type;
    WebView webView;
    String url;

    public WebFragment() {
        super();
    }

    public static WebFragment newInstance(int type) {

        Bundle args = new Bundle();
        args.putInt("type", type);
        WebFragment fragment = new WebFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
            this.type = getArguments().getInt("type");
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = ViewUtil.inflate(activity, R.layout.activity_webview);
        if (type == 1) {
            url = "http://192.168.8.201:8080/yjpt_jzd/video.html";
        } else {
            url = "http://baidu.com";
        }
        try {
            initView();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }

    void initView() throws Exception {
        webView = ViewUtil.findViewById(view, R.id.webview);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setBuiltInZoomControls(true);
        webView.loadUrl(url);
        webView.setWebViewClient(new webViewClient());
        view.findViewById(R.id.relat).setVisibility(View.GONE);
    }

    private class webViewClient extends WebViewClient {
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    @Override
    public void loadData() throws Exception {

    }

}
