package cn.kiway.yjhz.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import cn.kiway.yjhz.R;
import cn.kiway.yjhz.YjhzAppication;
import cn.kiway.yjhz.utils.ACache;
import cn.kiway.yjhz.utils.SharedPreferencesUtil;
import cn.kiway.yjhz.utils.okhttp.HttpRequestUrl;
import cn.kiway.yjhz.utils.okhttp.HttpUtils;
import io.vov.vitamio.Vitamio;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.Response;

public class BaseActivity extends FragmentActivity implements Callback {
    public YjhzAppication yjhzAppication;
    public Intent intent;
    /**
     * 缓存
     */
    public ACache mCache;
    /**
     * 加载图像
     */
    public ImageLoader imageLoader;
    /**
     * 图像加载配置参数
     */
    public DisplayImageOptions options, displayImageOptions;
    public FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 取得启动该Activity的Intent对象
        intent = getIntent();
        yjhzAppication = (YjhzAppication) getApplication();
        yjhzAppication.init();
        yjhzAppication.addActivity(this);
        mCache = ACache.get(this);
        Vitamio.isInitialized(this);
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(this));
        options = new DisplayImageOptions.Builder()
                .bitmapConfig(Bitmap.Config.RGB_565).cacheOnDisk(true)
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                .resetViewBeforeLoading(true).cacheInMemory(true)
                .considerExifParams(true).cacheOnDisc(true).build();
        displayImageOptions = new DisplayImageOptions.Builder()
                .bitmapConfig(Bitmap.Config.RGB_565).cacheOnDisk(true)
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED).showImageForEmptyUri(R.drawable.yjpty_landi)
                .resetViewBeforeLoading(true).cacheInMemory(true).showImageOnFail(R.drawable.yjpty_landi)
                .considerExifParams(true).cacheOnDisc(true).build();
        fragmentManager = getSupportFragmentManager();
    }

    @Override
    public void onFailure(Call call, IOException ioException) {

    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        try {
            if (call.request().url().toString().equals(HttpRequestUrl.LOGIN_URL)) {
                Headers headers = response.headers();
                List<String> cookies = headers.values("Set-Cookie");
                String sessionInfo = cookies.get(0);
                yjhzAppication.session = sessionInfo.substring(0, sessionInfo.indexOf(";"));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 隐藏Fragment
     *
     * @param transaction
     * @param fragments
     */
    public void hideFragment(FragmentTransaction transaction,
                             Fragment... fragments) {
        if (transaction == null)
            return;
        if (fragments == null || fragments.length == 0)
            return;
        for (Fragment fragment : fragments) {
            if (fragment != null && fragment.isVisible())
                transaction.hide(fragment);
        }
    }
    //扫码登录返回
    public void login() {
        yjhzAppication.mHttpClient.newCall(HttpUtils.get(HttpRequestUrl.LOGOUT_URL)).enqueue(this);
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("userName", SharedPreferencesUtil.getString(this,"userName"));
        map.put("password", SharedPreferencesUtil.getString(this,"password"));
        map.put("type", "1");
        yjhzAppication.mHttpClient.newCall(HttpUtils.post(HttpRequestUrl.LOGIN_URL, map)).enqueue(this);
    }
}
