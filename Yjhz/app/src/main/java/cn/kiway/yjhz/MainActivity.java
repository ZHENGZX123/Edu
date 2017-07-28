package cn.kiway.yjhz;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.WriterException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.kiway.mqtt.service.ClientService;
import cn.kiway.mqtt.util.SystemProp;
import cn.kiway.remote.monitor.app.ApksControlCenter;
import cn.kiway.yjhz.activity.BaseActivity;
import cn.kiway.yjhz.activity.box.ImageViewActivity;
import cn.kiway.yjhz.activity.box.PptActivity;
import cn.kiway.yjhz.activity.box.VideoActivity;
import cn.kiway.yjhz.activity.simulator.ClassListActivity;
import cn.kiway.yjhz.activity.simulator.GSessionActivity;
import cn.kiway.yjhz.adapter.AdvViewPagerAdapter;
import cn.kiway.yjhz.dialog.LoginDialog;
import cn.kiway.yjhz.qrcode.QRCodeUtils;
import cn.kiway.yjhz.tfcard.TFCard;
import cn.kiway.yjhz.utils.CommonUitl;
import cn.kiway.yjhz.utils.GlobeVariable;
import cn.kiway.yjhz.utils.IConstant;
import cn.kiway.yjhz.utils.Logger;
import cn.kiway.yjhz.utils.SharedPreferencesUtil;
import cn.kiway.yjhz.utils.SilentInstall;
import cn.kiway.yjhz.utils.okhttp.HttpRequestUrl;
import cn.kiway.yjhz.utils.okhttp.HttpUtils;
import cn.kiway.yjhz.utils.writeErrorLog;
import cn.kiway.yjhz.wifimanager.MessageHander.AccpectMessageHander;
import cn.kiway.yjhz.wifimanager.WIFIHotSpot;
import cn.kiway.yjhz.wifimanager.WifiAdmin;
import cn.kiway.yjhz.wifimanager.netty.PushServer;
import cn.kiway.yjhz.wifimanager.udp.BroadCastUdp;
import cn.kiway.yjhz.wifimanager.websocket.WebsocketControlCenter;
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static cn.kiway.yjhz.utils.okhttp.HttpRequestUrl.GET_CUSE_BASE;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    public static final String TAG = "kiway_MainActiviry";
    public static String curWIFISID;// wifi SSID
    public static String WIFI_PWD = "12345678";// wifi密码
    private ImageView imageview1;// 二维码图片
    int connectWifiNumber;//wifi连接次数
    private WifiManager wifiManager;// wifi管理
    private WIFIHotSpot wifiHotManager = new WIFIHotSpot();// wifi热点
    private TextView yjhzTag;// wifi名字
    private static TextView heiziSdInfo;// sd卡信息
    private static boolean hasSDCard = false;// 是否有sd卡
    WifiAdmin admin;// wifi连接
    protected String wifiInfo;// 从内存获取的wifi信息
    public static MainActivity mainActivity;
    public static boolean isHot = false;// 是否为wifi热点
    public LoginDialog dialog;// 连接wifi时候的dialog
    static List<String> sdpaths = TFCard.getExtSDCardPaths();// TF卡数据路径
    ViewPager viewPager; // 广告viewPager
    AdvViewPagerAdapter adapter;// 广告适配器
    List<String> photoList = new ArrayList<String>();// 广告列表
    LinearLayout layout;// 键盘
    boolean hasMeasured = false;// 只获取一次键盘的宽高
    String MAC;// mac地址
    BroadCastUdp broadCastUdp;
    private boolean isBindClientFlag = false; //ClientService绑定标记
    private boolean isBindMqttFlag = false; //MqttService绑定标记
    private SystemProp mSystemProp;
    private WebsocketControlCenter mWebsocketControlCenter;
    AccpectMessageHander accpectMessageHander = new AccpectMessageHander();//websocket与tcp协议信息接收协议的
    public Dialog webViewDialog;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CommonUitl.setTimeZone(this);//设置时区
        mainActivity = this;
        //AccpectMessageHander.setActivity(this);
        admin = new WifiAdmin(this);//获取WiFi对象
        wifiInfo = SharedPreferencesUtil.getString(this,
                GlobeVariable.WIFI_INFO);//获取保存的wifi信息
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        dialog = new LoginDialog(this);
        iniView();
        findSDPath();//获取sd路径
        ChechSd();//检查sdk
        try {
            setData();
            loadData();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (CommonUitl.isWifiActive(this)) {//如果当前网络环境是wifi则直接用
            curWIFISID = CommonUitl.getConnectWifiSsid(this);//获取当前的WiFiSSID
            WIFI_PWD = "";
            isHot = false;
            initialScreen();//更新界面二维码
            SharedPreferencesUtil.save(this, GlobeVariable.WIFI_INFO,//保存当前的wifi
                    curWIFISID + ":::" + "1" + ":::" + "2");
            broadCastUdp = new BroadCastUdp(MAC + ":::" + heiziSdInfo.getText());//启动广播
            broadCastUdp.start();
        } else {
            if (!wifiInfo.equals("")
                    && // 判断是否有wifi以及是否能被搜索到，没有自建热点
                    admin.canScannable(wifiInfo.split(":::")[0])
                    && wifiInfo.split(":::").length > 2) {//有保存的WiFi
                WIFIHotSpot.closeWIFIHotspot(wifiManager);//关闭热点
                yjhzAppication.setwifiName(wifiInfo.split(":::")[0]);//设置当期连接wifi
                yjhzAppication.setwifiPs(wifiInfo.split(":::")[1]);
                yjhzAppication.setwifiTp(Integer.parseInt(wifiInfo.split(":::")[2]));
                admin.openWifi();//打开wifi
                admin.connectConfiguratedWifi(wifiInfo.split(":::")[0]);
                dialog.setTitle("初始化中，请稍后");
                dialog.show();
            } else {//没有保存的wifi和当前没有连接wifi 启动热点
                isHot = true;
                openWifiHotSpot();
            }
        }
        //测试模拟器 可以删除
        if (CommonUitl.getLocalEthernetMacAddress() == null) {//mac地址为空，默认为模拟器
            teaching();
        } else {
           // teaching();
        }
    }

    /**
     * 设置界面数据
     */
    public void initialScreen() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                saveQRCodeUtils(imageview1);
                if (isHot) {//如果是热点设置红颜色
                    setTextStyle(R.drawable.ic_device_signal_wifi_red,
                            R.color._f41100, yjhzTag, curWIFISID);
                } else {//不是热点
                    yjhzTag.setText(curWIFISID);
                    IConstant.executorService.execute(new Runnable() {
                        public void run() {
                            if (CommonUitl.ping()) {//ping的使我们网站，能ping白色，不能黄色
                                setTextStyle(
                                        R.drawable.ic_device_signal_wifi_white,
                                        R.color._ffffff, yjhzTag, curWIFISID);
                            } else {
                                setTextStyle(
                                        R.drawable.ic_device_signal_wifi_yellow,
                                        R.color._ff9800, yjhzTag, curWIFISID);
                            }
                        }
                    });
                }
                findSDPath();//获取sd卡路径，网络变化后重新更新下
            }
        });
    }

    /**
     * 显示二维码图片
     *
     * @param imageview
     */
    private void saveQRCodeUtils(ImageView imageview) {
        if (WIFI_PWD != null && curWIFISID != null && wifiManager != null) {
            try {
                PushServer.start(accpectMessageHander);//开始tcp协议
                String qrCode = "http://"
                        + CommonUitl.getLocalIpAddress(wifiManager,
                        MainActivity.this) + "/dl?ref=box&" + "&ssid="
                        + curWIFISID + "&pwd=" + WIFI_PWD + "&cid=" + MAC
                        + "&resoures=" + heiziSdInfo.getText() + "&ishot="
                        + isHot;//二维码的图片信息
                imageview.setImageBitmap(QRCodeUtils.createQRCode(qrCode, 150));//设置二维码
            } catch (WriterException e1) {
                e1.printStackTrace();
            }
        }
    }

    /**
     * 打开热点
     */
    private void openWifiHotSpot() {
        String tmpWifiId = CommonUitl.getWifiSID(this);//获取热点名字
        if ((tmpWifiId != null) && (!"".equalsIgnoreCase(tmpWifiId.trim()))) {//热点名字不为空
            curWIFISID = tmpWifiId;
        } else {//热点名字为空则创建一个并保存
            curWIFISID = CommonUitl.getWifiHot(this);
            CommonUitl.saveWifiSID(curWIFISID);
        }
        Log.i("TAG", WIFI_PWD + "");
        isHot = true;
        if (curWIFISID != null && WIFI_PWD != null) {//开启热点
            wifiHotManager.setWifiApEnabled(true, wifiManager, curWIFISID,
                    WIFI_PWD);
        }
        initialScreen();//开启热点后更新二维码
    }

    /**
     * 初始化视图
     */
    public void iniView() {
        imageview1 = (ImageView) findViewById(R.id.imageView1);
        imageview1.setOnClickListener(this);
        yjhzTag = (TextView) findViewById(R.id.wifi_name_title);
        heiziSdInfo = (TextView) findViewById(R.id.sd_title);
        MAC = CommonUitl.getLocalEthernetMacAddress();//获取mac地址
        CommonUitl.setContent(this, R.id.hezi_title, MAC);//设置界面上的mac地址
        CommonUitl.setContent(this, R.id.xignq_title, CommonUitl.getWeek()
                + "\n" + CommonUitl.getDate());//设置时间
        CommonUitl.setContent(this, R.id.version, CommonUitl.getVersionName(this));//设置当期版本号
        handler.sendEmptyMessageDelayed(GlobeVariable.update_time, 1000);//启动hander更新界面上的时间
        viewPager = (ViewPager) findViewById(R.id.viewpager);//广告viewPager
        photoList.add("drawable://" + R.drawable.yjpt);//设置广告默认图
        adapter = new AdvViewPagerAdapter(getSupportFragmentManager(),
                photoList);//初始化广告
        viewPager.setAdapter(adapter);
        try {
            getKeyBoradHW();//中间广告图大小
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 播放
     *
     * @param url  播放地址
     * @param type 1视频 2ppt 3图片
     */
    public void Play(String url, int type) {//从发送命令那边调用，至于为啥写在这我也不知道
        SharedPreferencesUtil.save(this, GlobeVariable.GRADE,
                url.split(":::")[1]);//保存命令过来的年级，从而从文件分年级去找
        if (GlobeVariable.File_Path.equals("")
                || GlobeVariable.File_Path == null || !hasSDCard)//如果卡路径为空。重新获取下
            findSDPath();
        if (!hasSDCard) {//没有插入sd卡不允许进入
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    Toast.makeText(MainActivity.this, "请插入SD卡",
                            Toast.LENGTH_SHORT).show();
                }
            });
            return;
        }
        if (GlobeVariable.File_Path.equals("")
                || GlobeVariable.File_Path == null)// 如果为空的话，重新获取
            GlobeVariable.File_Path = CommonUitl.getSdPath(this, sdpaths);
        if ((GlobeVariable.File_Path.equals("")//这里判断了这么多路径为空，当初为啥加，好像是有bug才加的
                || GlobeVariable.File_Path == null) && url.split(":::").length < 3) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this, "没有找到该资源",
                            Toast.LENGTH_SHORT).show();
                }
            });
            return;
        }
        Intent intent = null;
        switch (type) {//根据文件类别，来启动
            case 1:
                intent = new Intent(MainActivity.this, VideoActivity.class);
                break;
            case 2:
                intent = new Intent(MainActivity.this, PptActivity.class);
                break;
            case 3:
                intent = new Intent(MainActivity.this, ImageViewActivity.class);
                break;
        }
        if (intent != null) {
            intent.putExtra(GlobeVariable.PLAY_NAME, url.split(":::")[0]);
            if (url.split(":::").length >= 3)
                intent.putExtra(GlobeVariable.PLAY_URL, url.split(":::")[2]);
            startActivity(intent);
        }
    }

    @SuppressLint("HandlerLeak")
    public Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case GlobeVariable.update_time://更新时间
                    handler.sendEmptyMessageDelayed(GlobeVariable.update_time, 1000);
                    CommonUitl.setContent(MainActivity.this, R.id.xignq_time,
                            CommonUitl.getFormattedDate().substring(11, 16));
                    break;
                case GlobeVariable.connectWifi://连接wifi
                    if (dialog != null && !dialog.isShowing()) {
                        dialog.setTitle("连接中，请稍后");
                        dialog.show();
                    }
                    WIFIHotSpot.closeWIFIHotspot(wifiManager);//关闭热点，打开wifi启动连接
                    admin.openWifi();
                    handler.sendEmptyMessageDelayed(2000, 1000);// 做延迟，因为立刻开启wifi就连会导致没有连接的动作
                    break;
                case GlobeVariable.viewPager://广告轮播
                    if ((int) viewPager.getCurrentItem() + 1 == adapter.getCount()) {
                        viewPager.setCurrentItem(0);
                    } else
                        viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                    handler.sendEmptyMessageDelayed(GlobeVariable.viewPager, 20000);
                    break;
                case 2000:
                    if (connectWifiNumber > WifiAdmin.IS_OPENED) {//判断连接次数
                        isHot = true;//连接次数大于3次，不连接了，启动热点
                        openWifiHotSpot();//为啥加这个，注意是这个盒子wifi有问题，有时候调起连接，并没有连接的动作
                        dialog.close();
                        Toast.makeText(MainActivity.this, "连接不上wifi,请重试",
                                Toast.LENGTH_SHORT).show();
                        handler.removeMessages(2000);
                        return;
                    }
                    if (admin.getWifitate() != WifiAdmin.IS_OPENED)
                        admin.openWifi();//开启连接wifi
                    connectWifiNumber = connectWifiNumber + 1;//次数加一
                    admin.addNetwork(admin.CreateWifiInfo(
                            yjhzAppication.getWifiName(),
                            yjhzAppication.getwifiPs(), yjhzAppication.getWifiTp()));
                    handler.sendEmptyMessageDelayed(2000, 30000);//30秒后判断是否连接成
                    break;
                case GlobeVariable.downLoadApk://检查更新下载apl
                    dialog.setTitle(msg.obj.toString());
                    if (!dialog.isShowing())
                        dialog.show();
                    break;
                case GlobeVariable.downLoadApkSuccess://下载apk成功后启动静默安装
                    dialog.setTitle("安装中,请稍后");
                    if (!dialog.isShowing())
                        dialog.show();
                    final String s = msg.obj.toString();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            SilentInstall installHelper = new SilentInstall();
                            final boolean result = installHelper
                                    .install(s);
                            handler.sendEmptyMessage(GlobeVariable.closeDialog);
                            if (result) {//判断静默安装是否成
                                CommonUitl.stratApk(MainActivity.this, CommonUitl.DownLoadApkPackName);
                            } else {
                                Toast.makeText(getApplicationContext(), "启动失败",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).start();
                    break;
                case GlobeVariable.downLoadApkFial://下载失败
                    dialog.close();
                    Toast.makeText(MainActivity.this, "启动失败", Toast.LENGTH_SHORT).show();
                    break;
                case GlobeVariable.closeDialog:
                    dialog.close();
                    break;
            }
        }

        ;
    };


    /**
     * 添加公网成功后
     */
    public void setWifiName() {
        handler.removeMessages(2000);
        curWIFISID = CommonUitl.getConnectWifiSsid(this);//获取WiFiSSID
        WIFI_PWD = "";//不显示wifi 密码
        initialScreen();//更新数据
        isHot = false;//不是热点
        broadCastUdp = new BroadCastUdp(MAC + ":::" + heiziSdInfo.getText());
        broadCastUdp.start();//启动广播
        try {
            loadData();//加载广告
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (dialog != null)//关闭
            dialog.close();
        System.out.println("开始广播：：：：：");
    }

    /**
     * 连接到公用的wifi
     */
    public void ConnectWifi(String SSID, String PS, String TYPE) {//在接收数据时调用
        connectWifiNumber = 0;
        yjhzAppication.setwifiPs(PS);
        yjhzAppication.setwifiTp(Integer.parseInt(TYPE));
        yjhzAppication.setwifiName(SSID);
        isHot = false;
        handler.sendEmptyMessageDelayed(GlobeVariable.connectWifi, 1000);
    }

    /**
     * 检查是否有SD卡,且资源是否正确 小班资源名 SmallClass 中班资源名 MiddleClass 大班资源名 LargeClass
     * 大大资源名 HighClass
     */
    private void findSDPath() {
        runOnUiThread(new Runnable() {
            @SuppressLint("ResourceAsColor")
            public void run() {
                if (sdpaths != null && sdpaths.size() > 0) {
                    hasSDCard = true;
                    for (int i = 0; i < sdpaths.size(); i++) {//从获取sd卡路径中来判断所有的
                        if (!CommonUitl.isHasResource(mainActivity,//路径中是否有资源来
                                sdpaths.get(i))) {//切断sd卡的路径
                            GlobeVariable.File_Path = "";
                        } else {
                            Log.e("tag", "::::::::::" + GlobeVariable.File_Path);
                            GlobeVariable.File_Path = sdpaths.get(i);
                            break;
                        }
                    }
                } else {
                    hasSDCard = false;
                }
                ChechSd();//检查sd卡
            }
        });
    }

    void ChechSd() {
        runOnUiThread(new Runnable() {
            public void run() {
                if (!hasSDCard) {// 获取sd卡里面的资源
//                    setTextStyle(R.drawable.ic_hardware_sim_card_no,
//                            R.color._f41100, heiziSdInfo, "请插入SD卡");
                } else {//根据卡的路径来更新界面
                    String string = CommonUitl.checkSdResource(
                            MainActivity.this, sdpaths);
                    if (string.equals("没有资源文件")) {
//                        setTextStyle(R.drawable.ic_hardware_sim_card_no,
//                                R.color._f41100, heiziSdInfo, string);
                    } else {
                        setTextStyle(R.drawable.ic_hardware_sim_card_has,
                                R.color._ffffff, heiziSdInfo, string);
                    }
                }
            }
        });
        if (broadCastUdp != null)//启动广播
            broadCastUdp.setMessageData(MAC + ":::" + heiziSdInfo.getText());
    }

    ;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
            Logger.log("**************"+keyCode);
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            finish();
            System.exit(0);
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_MENU && event.getRepeatCount() == 0) {
            teaching();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {//注销广播
        super.onDestroy();
        if (BroadCastUdp.udpSocket != null) {
            BroadCastUdp.udpSocket.close();
        }
        BroadCastUdp.isRun = false;
    }

    /**
     * 移除SD卡调用
     */
    public void outputSdCard() {
        hasSDCard = false;
        mainActivity.runOnUiThread(new Runnable() {
            @SuppressLint("ResourceAsColor")
            public void run() {
//                setTextStyle(R.drawable.ic_hardware_sim_card_no,
//                        R.color._f41100, heiziSdInfo, "请插入SD卡");
                if (broadCastUdp != null)
                    broadCastUdp.setMessageData(MAC + ":::"
                            + heiziSdInfo.getText());
            }
        });
    }

    /**
     * 插入Sd卡调用
     */
    public void inputSdCard() {
        try {
            findSDPath();
            ChechSd();
        } catch (Exception e) {
            writeErrorLog.writeErrorLogger(e);
        }
    }

    /**
     * 监听SD卡的拔插
     */
    public void startListen() {//监听sd卡广播只能放到activity
        IntentFilter intentFilter = new IntentFilter(
                Intent.ACTION_MEDIA_MOUNTED);
        intentFilter.setPriority(1000);
        intentFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
        intentFilter.addAction(Intent.ACTION_MEDIA_EJECT);
        intentFilter.addDataScheme("file");
        registerReceiver(broadcastRec, intentFilter);
    }
    //监听sd广播，不过没啥用，移除能监听，但是插入因为系统原因不能读取到卡的路径，会显示没有资源
    private final BroadcastReceiver broadcastRec = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_MEDIA_MOUNTED)) {//插入广播
                inputSdCard();
            } else if (action.equals(Intent.ACTION_MEDIA_UNMOUNTED)) {//移除广播
                outputSdCard();
            }
            saveQRCodeUtils(imageview1);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        startListen();//sd监听
        try {
            setData();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (mSystemProp == null)
            mSystemProp = new SystemProp(this);
        bindClientService();
        if (mWebsocketControlCenter == null)//websocket广播
            mWebsocketControlCenter = new WebsocketControlCenter(30001, accpectMessageHander);
    }


    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(broadcastRec);// 取消注册
    }


    void setData() throws Exception {
        photoList.clear();
        JSONObject data = mCache.getAsJSONObject(GlobeVariable.ADV_URL);
        if (data != null) {//广告
            JSONArray jsonArray = data.optJSONArray("imgs");
            if (jsonArray != null) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    final String picUrl = CommonUitl.imgUrl(jsonArray
                            .optString(i));
                    String videoPath = CommonUitl.downloadPhoto(CommonUitl
                            .MD5(jsonArray.optString(i)));
                    final File f = new File(videoPath + ".jpg");
                    IConstant.executorService.execute(new Runnable() {
                        @Override
                        public void run() {
                            if (!f.exists())
                                CommonUitl.downloadFile(f, picUrl,
                                        MainActivity.this);
                        }
                    });
                    if (f.exists())
                        photoList.add("file://" + videoPath + ".jpg");
                }
            }
        }
        if (photoList.size() == 0)
            photoList.add("drawable://" + R.drawable.kwadv);
        adapter = new AdvViewPagerAdapter(getSupportFragmentManager(),
                photoList);
        runOnUiThread(new Runnable() {
            public void run() {
                adapter.notifyDataSetChanged();
                viewPager.setAdapter(adapter);
                handler.removeMessages(GlobeVariable.viewPager);
                handler.sendEmptyMessageDelayed(GlobeVariable.viewPager, 20000);
            }
        });
    }

    /**
     * 加载广告图片
     */
    void loadData() throws Exception {
        // 表单数据
        FormBody.Builder builder = new FormBody.Builder();
        builder.add("type", "BOX_AD");
        RequestBody formBody = builder.build();
        Request request = new Request.Builder().url(GlobeVariable.ADV_URL)
                .post(formBody).build();
        yjhzAppication.mHttpClient.newCall(request).enqueue(this);
        yjhzAppication.mHttpClient.newCall(HttpUtils.get(GET_CUSE_BASE)).enqueue(this);
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        super.onResponse(call, response);
        try {
            if (call.request().url().toString().equals(HttpRequestUrl.LOGIN_URL)) {
                JSONObject data = new JSONObject(response.body().string());
                Logger.log(data);
                if (data.optInt("StatusCode") == 200) {
                    webViewDialog.dismiss();
                    startActivity(new Intent(this, ClassListActivity.class));
                }
            } else if (call.request().url().toString().equals(GET_CUSE_BASE)) {
                JSONObject data = new JSONObject(response.body().string());
                mCache.put(GET_CUSE_BASE, data);
            } else {
                try {
                    JSONObject data = new JSONObject(response.body().string());
                    if (call.request().url().toString().equals(GlobeVariable.ADV_URL)) {
                        mCache.put(GlobeVariable.ADV_URL, data);
                        setData();
                    }
                } catch (JSONException e1) {
                    e1.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private ClientService mClientService;

    /* 绑定service监听*/
    ServiceConnection mClientServiceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName name, IBinder binder) {
            Log.d(TAG, "onServiceConnected");
            mClientService = ((ClientService.ClientBinder) binder).getService();
            mClientService.start();
        }

        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected");
        }
    };

    /**
     * 绑定服务
     */
    public void bindClientService() {
        Log.d(TAG, "bindClientService");
        Intent bindIntent = new Intent(this, ClientService.class);
        startService(bindIntent);
        if (!isBindClientFlag) {
            isBindClientFlag = bindService(bindIntent, mClientServiceConnection,
                    Context.BIND_AUTO_CREATE);
            Log.d(TAG, "bindClientService, binding success: " + isBindClientFlag);
        }
    }

    /**
     * 解除绑定
     */
    public void unBindClientService() {
        if (isBindClientFlag) {
            unbindService(mClientServiceConnection);
            Intent bindIntent = new Intent(this, ClientService.class);
            stopService(bindIntent);
            mClientService.stopService(bindIntent);
        }
    }


    int i = 0;

    @SuppressLint("NewApi")
    @Override
    public void onClick(View v) {
        int id = v.getId();
        Log.d(TAG, "onClick,ID: " + id);
        switch (id) {
            case R.id.imageView1://二维码图片
                mClientService.sendMessage(new SystemProp(this).getSystemPropJson());
                int count = ApksControlCenter.getInstance().getLaunchCount("cn.kiway.yjhz",
                        "cn.kiway.yjhz.MainActivity");
                Log.d(TAG, "幼教平台启动次数: " + count);
                long time = ApksControlCenter.getInstance().getUseTime("cn.kiway.yjhz",
                        "cn.kiway.yjhz.MainActivity");
                Log.d(TAG, "幼教平台启动时长: " + time);
                String file = TFCard.getExternalStorageDirectory();
                String size = TFCard.readTotalSDCard(file);
                Log.d(TAG, "external file: " + file + "; Size: " + size);
                file = TFCard.getSecondaryStorageDirectory(MainActivity.this);
                size = TFCard.readAvailableSDCard(file);
                Log.d(TAG, "Secondary size: " + file + "; Size: " + size);
                break;
        }
    }

    //    //-------------------------我是分割线--------------------------
//    //专用模拟器的，可以删除
    public void ShowWeb(View view) {
        if (getSharedPreferences("kiway", 0).getBoolean("login", true)) {
            teaching();
        } else {
            teaching();
        }
    }



    private void teaching() {
        startActivity(new Intent(this,GSessionActivity.class));
    }

    //扫码登录返回
    public void login(String userName, String password) {
        webViewDialog.dismiss();
        Log.e("*************", userName);
        Log.e("*************", password);
        //保存帐号密码
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("userName", userName);
        map.put("password", password);
        map.put("type", "1");
        yjhzAppication.mHttpClient.newCall(HttpUtils.post(HttpRequestUrl.LOGIN_URL, map)).enqueue(this);

    }
    //设置颜色值
    public static void setTextStyle(final int drawable, final int color,
                                    final TextView view, final String text) {
        mainActivity.runOnUiThread(new Runnable() {
            public void run() {
                try {
                    if (!text.equals(""))
                        view.setText(text);
                    CommonUitl.setTextFontColor(view,
                            mainActivity.getResources(), color);
                    CommonUitl.setArroundDrawable(mainActivity, view, drawable,
                            -1, -1, -1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 中间布局的大小
     */
    void getKeyBoradHW() throws Exception {
        layout = (LinearLayout) findViewById(R.id.layout);
        ViewTreeObserver vto = layout.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @SuppressLint("NewApi")
            public boolean onPreDraw() {
                if (hasMeasured == false) {
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            layout.getMeasuredHeight() * 16 / 9,
                            LayoutParams.MATCH_PARENT);
                    params.setMargins(5, 0, 0, 0);
                    viewPager.setLayoutParams(params);
                    hasMeasured = true;
                }
                return true;
            }
        });
    }

}
