package cn.kiway.yjhz.activity.box;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import cn.kiway.yjhz.PPTViewer;
import cn.kiway.yjhz.R;
import cn.kiway.yjhz.activity.BaseActivity;
import cn.kiway.yjhz.dialog.LoginDialog;
import cn.kiway.yjhz.utils.CommonUitl;
import cn.kiway.yjhz.utils.GlobeVariable;
import cn.kiway.yjhz.wifimanager.MessageHander.AccpectMessageHander;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PptActivity extends BaseActivity {
    PPTViewer pptViewer;
    Button button, button2;
    OkHttpClient mOkHttpClient = new OkHttpClient();
    LoginDialog loginDialog;
    String pptUrl = "";
    String curPptName;
    String downloadPPtPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AccpectMessageHander.setPptActivity(this);
        setContentView(R.layout.activity_ppt);
        pptViewer = (PPTViewer) findViewById(R.id.pptviewer);
        loginDialog = new LoginDialog(this);
        pptUrl = intent.getStringExtra(GlobeVariable.PLAY_URL);
        curPptName = intent.getStringExtra(GlobeVariable.PLAY_NAME);
        playPpt(curPptName);
        button = (Button) findViewById(R.id.button);
        button2 = (Button) findViewById(R.id.button2);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                pptViewer.next();
            }
        });
        button2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                pptViewer.prev();
            }
        });
    }

    // 播放ppt
    public void playPpt(String pptPath) {
        String aPptPath = GlobeVariable.File_Path + "/"
                + GlobeVariable.KWHZ_USERSEESION_PATH + "/" + pptPath;
        File file = new File(aPptPath);
        if (file.exists()) {
            pptViewer.loadPPT(this, aPptPath);
        } else {
            if (GlobeVariable.File_Path.equals("")) {
                downloadPPtPath = CommonUitl.createPPtFloder(Environment.getExternalStorageDirectory()
                        .getAbsolutePath());
            } else {
                downloadPPtPath = CommonUitl.createPPtFloder(GlobeVariable.File_Path + "/");
            }
            loginDialog.show();
            CommonUitl.createFloder();
            downloalPPT(pptUrl);
        }
    }

    // 下一页
    public void next() {
        runOnUiThread(new Runnable() {
            public void run() {
                pptViewer.next();
            }
        });
    }

    // 上一页
    public void pre() {
        runOnUiThread(new Runnable() {
            public void run() {
                pptViewer.prev();
            }
        });
    }

    ;

    @Override
    protected void onPause() {
        super.onPause();
    }

    void downloalPPT(String pptUrl) {
        Request request = new Request.Builder().url(pptUrl).build();
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                try {
                    is = response.body().byteStream();
                    long total = response.body().contentLength();
                    File file = new File(downloadPPtPath, curPptName);
                    fos = new FileOutputStream(file);
                    long sum = 0;
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                        sum += len;
                        int progress = (int) (sum * 1.0f / total * 100);
                        Log.d("h_bl", "progress=" + progress);
                        Message msg = mHandler.obtainMessage();
                        msg.what = 1;
                        msg.obj = progress + "%";
                        mHandler.sendMessage(msg);
                    }
                    fos.flush();
                    Log.d("h_bl", "文件下载成功");
                    Message msg = mHandler.obtainMessage();
                    msg.what = 2;
                    mHandler.sendMessage(msg);
                } catch (Exception e) {
                    Log.d("h_bl", "文件下载失败");
                    Message msg = mHandler.obtainMessage();
                    msg.what = 1;
                    msg.obj = "文件下载失败";
                    mHandler.sendMessage(msg);
                } finally {
                    try {
                        if (is != null)
                            is.close();
                    } catch (IOException e) {
                    }
                    try {
                        if (fos != null)
                            fos.close();
                    } catch (IOException e) {
                    }
                }
            }
        });
    }

    Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    loginDialog.setTitle(msg.obj.toString());
                    break;
                case 2:
                    loginDialog.close();
                    pptViewer.loadPPT(PptActivity.this, downloadPPtPath + "/" + curPptName);
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };
}
