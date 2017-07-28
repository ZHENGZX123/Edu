package cn.kiway.yjhz.activity.box;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONArray;

import java.io.File;

import cn.kiway.yjhz.R;
import cn.kiway.yjhz.activity.BaseActivity;
import cn.kiway.yjhz.utils.GlobeVariable;
import cn.kiway.yjhz.wifimanager.MessageHander.AccpectMessageHander;

public class ImageViewActivity extends BaseActivity {
    ImageView imageView;

    JSONArray array;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AccpectMessageHander.setImageViewActivity(this);
        setContentView(R.layout.activity_img);
        imageView = (ImageView) findViewById(R.id.img);

        String imgName = intent.getStringExtra(GlobeVariable.PLAY_NAME);
        showImg(imgName);
    }

    void showImg(String imgName) {
        String aPptPath = GlobeVariable.File_Path + "/"
                + GlobeVariable.KWHZ_USERSEESION_PATH + "/" + imgName;
        File file = new File(aPptPath);
        if (!file.exists()) {
            if (intent.getStringExtra(GlobeVariable.PLAY_URL) != null && !intent.getStringExtra
                    (GlobeVariable.PLAY_URL).equals("null")) {
                imageLoader.displayImage(intent.getStringExtra(GlobeVariable.PLAY_URL), imageView, options);
            } else {
                Toast.makeText(this, "没有该文件", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        imageLoader.displayImage("file://" + aPptPath, imageView, options);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mCache.getAsJSONArray(GlobeVariable.statisticsModel) == null) {
            array = new JSONArray();
        } else {
            array = mCache.getAsJSONArray(GlobeVariable.statisticsModel);
        }
    }
}
