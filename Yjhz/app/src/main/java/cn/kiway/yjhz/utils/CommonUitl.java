package cn.kiway.yjhz.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.format.Time;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import cn.kiway.yjhz.activity.BaseActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static cn.kiway.yjhz.MainActivity.mainActivity;

@SuppressLint("SimpleDateFormat")
public class CommonUitl {

    public static String getUUID() {

        UUID uuid = UUID.randomUUID();

        String s = uuid.toString().replace("-", "");

        return s;

    }

    public static String[] chars = new String[]{"a", "b", "c", "d", "e", "f",
            "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s",
            "t", "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "I",
            "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
            "W", "X", "Y", "Z"};

    public static String getShortUUID() {
        StringBuffer shortBuffer = new StringBuffer();
        String uuid = UUID.randomUUID().toString().replace("-", "");
        for (int i = 0; i < 8; i++) {
            String str = uuid.substring(i * 4, i * 4 + 4);
            int x = Integer.parseInt(str, 16);
            shortBuffer.append(chars[x % 0x3E]);
        }
        return shortBuffer.toString();

    }

    /**
     * 获取wifi热点名字
     */
    public static String getWifiHot(Context context) {
        @SuppressWarnings("deprecation")
        String ANDROID_ID = Settings.System.getString(
                context.getContentResolver(), Settings.System.ANDROID_ID);
        return "kiway" + ANDROID_ID.substring(0, 6);
    }

    /**
     * 获取设备的id
     */
    @SuppressWarnings("deprecation")
    public static String getAndroidID(Context context) {
        return Settings.System.getString(context.getContentResolver(),
                Settings.System.ANDROID_ID);
    }

    /**
     * 验证是否在wifi环境下
     */
    @SuppressWarnings("deprecation")
    public static boolean isWifiActive(Context icontext) {
        Context context = icontext.getApplicationContext();
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] info;
        if (connectivity != null) {
            info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getTypeName().equals("WIFI")
                            && info[i].isConnected()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 获取版本号
     *
     * @return 当前应用的版本号
     */
    public static int getVersion(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(),
                    0);
            int version = info.versionCode;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 获取当前网络名字
     */
    public static String getConnectWifiSsid(Context context) {
        WifiManager wifiManager = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if (wifiInfo.getSSID() == null)
            return "";
        return wifiInfo.getSSID().replace("\"", "");// 手机适配//在魅族手机获取的名字带有双引号，这边去除
    }

    /**
     * 获取网络地址
     */
    public static String getlocalip(Context context) {
        WifiManager wifiManager = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        // Log.d(Tag, "int ip "+ipAddress);
        if (ipAddress == 0)
            return null;
        return ((ipAddress & 0xff) + "." + (ipAddress >> 8 & 0xff) + "."
                + (ipAddress >> 16 & 0xff) + "." + (ipAddress >> 24 & 0xff));
    }

    /**
     * 获取当前网络ip地址
     *
     * @param context
     * @return
     */
    public static String getLocalIpAddress(WifiManager wifiManager,
                                           Context context) {
        try {
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int i = wifiInfo.getIpAddress();
            StringBuilder sb = new StringBuilder();
            sb.append(i & 0xFF).append(".");
            sb.append((i >> 8) & 0xFF).append(".");
            sb.append((i >> 16) & 0xFF).append(".");
            sb.append((i >> 24) & 0xFF);
            return sb.toString();
        } catch (Exception ex) {
            return " 获取IP出错鸟!!!!请保证是WIFI,或者请重新打开网络!\n" + ex.getMessage();
        }
    }

    /**
     * 获取wifi热点名字
     */

    public static String getWifiSID(Context context) {
        InputStream input;
        try {
            AssetManager assetManager = context.getAssets();
            input = assetManager.open("config.txt");
            int size = input.available();
            byte[] buffer = new byte[size];
            input.read(buffer);
            input.close();
            String text = new String(buffer);
            return text;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 保存热点名字
     */
    public static void saveWifiSID(String tmpWifiId) {
        String path = "config.txt";
        try {
            FileOutputStream fos = new FileOutputStream(new File("/data/"
                    + path));
            if (tmpWifiId != null) {
                fos.write(tmpWifiId.getBytes());
            }
            fos.close();
        } catch (Exception ex) {
        }
    }

    /**
     * 是否有资源 小班资源名 SmallClass 中班资源名 MiddleClass 大班资源名 LargeClass 大大资源名 HighClass
     */
    public static boolean isHasResource(Context context, String sdpath) {
        String resource = null;
        String grade = SharedPreferencesUtil.getString(context,
                GlobeVariable.GRADE);
        if (grade.equals("")) {
            resource = "";
        } else if (grade.equals("1")) {
            resource = "/HighClass";
        } else if (grade.equals("2")) {
            resource = "/LargeClass";
        } else if (grade.equals("3")) {
            resource = "/MiddleClass";
        } else if (grade.equals("4")) {
            resource = "/SmallClass";
        }
        if (sdpath.equals("") || sdpath == null)
            return false;
        File file = new File(sdpath + "/" + GlobeVariable.KWHZ_COURSE_PATH
                + resource);
        if (file.exists())
            return true;
        else
            return false;
    }

    /**
     * 没有相应资源的提示
     */
    public static String NoResourceText(Context context) {
        String grade = SharedPreferencesUtil.getString(context,
                GlobeVariable.GRADE);
        if (grade.equals("1")) {
            return "请插入大大班的资源";
        } else if (grade.equals("2")) {
            return "请插入大班的资源";
        } else if (grade.equals("3")) {
            return "请插入中班的资源";
        } else if (grade.equals("4")) {
            return "请插入小班的资源";
        }
        return "";
    }

    /**
     * 有班级的时候的提示
     */
    public static String getResourceName(Context context) {
        String grade = SharedPreferencesUtil.getString(context,
                GlobeVariable.GRADE);
        if (grade.equals("1")) {
            return "HighClass";
        } else if (grade.equals("2")) {
            return "LargeClass";
        } else if (grade.equals("3")) {
            return "MiddleClass";
        } else if (grade.equals("4")) {
            return "SmallClass";
        }
        return "";
    }

    /**
     * 获取Sd路径
     */
    public static String getSdPath(Context context, List<String> sdpaths) {
        String grade = SharedPreferencesUtil.getString(context,
                GlobeVariable.GRADE);
        if (grade.equals("")) {
            GlobeVariable.File_Path = "";
            return "";
        }
        String string = "";
        for (int i = 0; i < sdpaths.size(); i++) {
            if (!CommonUitl.isHasResource(context, sdpaths.get(i))) {
                string = "";
            } else {
                string = sdpaths.get(i);
                break;
            }
        }
        return string;
    }

    /**
     * 计算今天星期几
     */
    public static String getWeek() {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date(System.currentTimeMillis()));
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
        switch (dayOfWeek) {
            case 1:
                return "星期日";
            case 2:
                return "星期一";
            case 3:
                return "星期二";
            case 4:
                return "星期三";
            case 5:
                return "星期四";
            case 6:
                return "星期五";
            case 7:
                return "星期六";
        }
        return "";
    }

    public static String getDateField(long time, int filed) {
        String s = null;
        Date date = new Date(time);
        SimpleDateFormat sdf;
        try {
            switch (filed) {
                case 0:
                    sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
                    s = sdf.format(date);
                    break;
                case 1:
                    sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    s = sdf.format(date);
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }

    // 获取日期
    public static String getDate() {
        String date = getFormattedDate();
        return date.substring(0, 11);
    }

    @SuppressWarnings("deprecation")
    public static String getFormattedDate() {
        Time time = new Time();
        time.setToNow();
        DateFormat.getDateInstance();
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return df.format(c.getTime());
    }

    /**
     * 设置系统时区
     */
    public static void setTimeZone(Context context) {
        AlarmManager timeZone = (AlarmManager) context
                .getSystemService(context.ALARM_SERVICE);
        timeZone.setTimeZone("Asia/Taipei");
    }


    /**
     * 设置文本
     */
    public static void setContent(TextView view, String text) {
        if (view != null)
            view.setText(text);
    }

    public static void setContent(BaseActivity context, int dId, String text) {
        TextView view = findViewById(context, dId);
        setContent(view, text);
    }

    /**
     * 获取视图控件
     *
     * @param activity 当前视图
     * @param id       控件ID
     */
    @SuppressWarnings("unchecked")
    public static <T> T findViewById(Activity activity, int id) {
        return (T) activity.findViewById(id);

    }

    public static String getClassGrade(Context context) {
        String classGrade = "";
        int classGradeInt = SharedPreferencesUtil.getInteger(context,
                GlobeVariable.GRADE);
        switch (classGradeInt) {
            case 0:
                classGrade = "未绑定年级";
                break;
            case 1:
                classGrade = "大大班";
                break;
            case 2:
                classGrade = "大班";
                break;
            case 3:
                classGrade = "中班";
                break;
            case 4:
                classGrade = "小班";
                break;
        }
        return classGrade;
    }

    /**
     * 设置文字颜色
     */
    @SuppressWarnings("deprecation")
    public static void setTextFontColor(TextView view, Resources resources,
                                        int corlor) throws Exception {
        SpannableStringBuilder builder = new SpannableStringBuilder(
                view.getText());
        int corlorValue = corlor;
        if (resources != null)
            corlorValue = resources.getColor(corlor);
        builder.setSpan(new ForegroundColorSpan(corlorValue), 0, view.length(),
                Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        view.setText(builder);
    }

    /**
     * 设置控件周围的图形
     */
    public static void setArroundDrawable(BaseActivity activity, TextView view,
                                          int rLeft, int rTop, int rRight, int rBottom) {
        Resources resources = activity.getResources();
        Drawable left = null, top = null, right = null, bottom = null;
        if (rLeft != -1) {
            left = resources.getDrawable(rLeft);
        }
        if (rTop != -1) {
            top = resources.getDrawable(rTop);
        }
        if (rRight != -1) {
            right = resources.getDrawable(rRight);
        }
        if (rBottom != -1) {
            bottom = resources.getDrawable(rBottom);
        }
        view.setCompoundDrawablesWithIntrinsicBounds(left, top, right, bottom);
    }

    /**
     * @return
     * @author sichard
     * @category 判断是否有外网连接（普通方法不能判断外网的网络是否连接，比如连接上局域网）
     */
    public static final boolean ping() {
        String result = null;
        try {
            String ip = "www.yuertong.com";// ping 的地址，可以换成任何一种可靠的外网
            Process p = Runtime.getRuntime().exec("ping -c 3 -w 100 " + ip);// ping网址3次
            // 读取ping的内容，可以不加
            InputStream input = p.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(input));
            StringBuffer stringBuffer = new StringBuffer();
            String content = "";
            while ((content = in.readLine()) != null) {
                stringBuffer.append(content);
            }
            Log.d("------ping-----",
                    "result content : " + stringBuffer.toString());
            // ping的状态
            int status = p.waitFor();
            if (status == 0) {
                result = "success";
                return true;
            } else {
                result = "failed";
            }
        } catch (IOException e) {
            result = "IOException";
        } catch (InterruptedException e) {
            result = "InterruptedException";
        } finally {
            Log.d("----result---", "result = " + result);
        }
        return false;
    }

    /**
     * 检查卡里的资源
     */
    public static String checkSdResource(Context context, List<String> sdpaths) {
        String string = "";
        String sdpath = "";
        for (int i = 0; i < sdpaths.size(); i++) {
            if (!new File(sdpaths.get(i) + "/" + GlobeVariable.KWHZ_COURSE_PATH)
                    .exists()) {
                sdpath = "";
            } else {
                sdpath = sdpaths.get(i);
                break;
            }
        }
        if (!sdpath.equals("")) {
            if (new File(sdpath + "/" + GlobeVariable.KWHZ_COURSE_PATH
                    + "/SmallClass").exists())
                string = string + "小班";
            if (new File(sdpath + "/" + GlobeVariable.KWHZ_COURSE_PATH
                    + "/MiddleClass").exists())
                string = string + " 中班";
            if (new File(sdpath + "/" + GlobeVariable.KWHZ_COURSE_PATH
                    + "/LargeClass").exists())
                string = string + " 大班";
            if (new File(sdpath + "/" + GlobeVariable.KWHZ_COURSE_PATH
                    + "/HighClass").exists())
                string = string + " 大大班";
        }
        if (string.equals(""))
            string = "没有资源文件";
        return string;
    }

    /**
     * 文件下载
     */
    public static void downloadFile(File f, String path, BaseActivity activity) {
        if (!f.exists()) {
            HttpURLConnection con = null;
            FileOutputStream fos = null;
            BufferedOutputStream bos = null;
            BufferedInputStream bis = null;
            try {
                URL url = new URL(path);
                con = (HttpURLConnection) url.openConnection();
                con.setConnectTimeout(5 * 1000);
                con.setReadTimeout(15 * 1000);
                if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    bis = new BufferedInputStream(con.getInputStream());
                    fos = new FileOutputStream(f);
                    bos = new BufferedOutputStream(fos);
                    byte[] b = new byte[1024];
                    int length;
                    while ((length = bis.read(b)) != -1) {
                        bos.write(b, 0, length);
                        bos.flush();
                    }
                } else {

                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (bos != null)
                        bos.close();
                    if (fos != null)
                        fos.close();
                    if (bis != null)
                        bis.close();
                    if (con != null)
                        con.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 下载相片
     */
    public static String downloadPhoto(Object object) {
        try {
            createFloder();
            File file = new File(Environment.getExternalStorageDirectory(),
                    IConstant.Yjhz);
            file = new File(file, IConstant.photo);
            file = new File(file, object.toString());
            return file.getAbsolutePath();
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * 创建软件的私有文件夹
     */
    public static void createFloder() {
        File file = new File(Environment.getExternalStorageDirectory(),
                IConstant.Yjhz);
        if (!file.exists())
            file.mkdirs();
        File f = new File(file, IConstant.photo);
        if (!f.exists())
            f.mkdirs();
    }

    /**
     * 创建软件的私有文件夹
     */
    public static String createPPtFloder(String path) {
        File file = new File(path,
                GlobeVariable.KWHZ_PPT_PATH);
        if (!file.exists())
            file.mkdirs();
        File f = new File(file, GlobeVariable.KWHZ_PPT_CPATH);
        if (!f.exists())
            f.mkdirs();
        return f.getAbsolutePath();
    }

    /**
     * 创建软件的私有文件夹
     */
    public static String createaPKFloder(String path) {
        File file = new File(path,
                GlobeVariable.KWHZ_PPT_PATH);
        if (!file.exists())
            file.mkdirs();
        File f = new File(file, GlobeVariable.KWHZ_APK_CPATH);
        if (!f.exists())
            f.mkdirs();
        return f.getAbsolutePath();
    }

    /**
     * MD5加密
     */
    public final static String MD5(String s) {
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'A', 'B', 'C', 'D', 'E', 'F'};
        try {
            byte[] btInput = s.getBytes();
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            mdInst.update(btInput);
            // 获得密文
            byte[] md = mdInst.digest();
            // 把密文转换成十六进制的字符串形式
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }

            return new String(str);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 拼接图像的Url 由于获取的地址不完全得拼接基础地址
     */
    public static String imgUrl(String url) {
        String s;
        if (url == null)
            return "";
        if (url.equals("null"))
            return "";
        s = "http://www.yuertong.com/yjpt" + url.replace("\\", "/");
        return s;
    }

    @SuppressLint("NewApi")
    public static String getLocalEthernetMacAddress() {
        String mac = null;
        try {
            @SuppressWarnings("rawtypes")
            Enumeration localEnumeration = NetworkInterface
                    .getNetworkInterfaces();
            while (localEnumeration.hasMoreElements()) {
                NetworkInterface localNetworkInterface = (NetworkInterface) localEnumeration
                        .nextElement();
                String interfaceName = localNetworkInterface.getDisplayName();
                if (interfaceName == null) {
                    continue;
                }
                if (interfaceName.equals("eth0")) {
                    mac = convertToMac(localNetworkInterface
                            .getHardwareAddress());
                    if (mac != null && mac.startsWith("0:")) {
                        mac = "0" + mac;
                    }
                    break;
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return mac;
    }

    @SuppressLint("DefaultLocale")
    private static String convertToMac(byte[] mac) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < mac.length; i++) {
            byte b = mac[i];
            int value = 0;
            if (b >= 0 && b <= 16) {
                value = b;
                sb.append("0" + Integer.toHexString(value));
            } else if (b > 16) {
                value = b;
                sb.append(Integer.toHexString(value));
            } else {
                value = 256 + b;
                sb.append(Integer.toHexString(value));
            }
        }
        return sb.toString().toUpperCase();
    }


    /**
     * 清空我们的文件
     */
    public static void deleteFiles(String filePath, boolean isDeleteFloder)
            throws Exception {
        File file = new File(filePath);
        if (!file.exists())
            return;
        if (file.isFile()) {
            file.delete();
        } else {
            File[] files = file.listFiles();
            for (File f : files) {
                deleteFiles(f.getAbsolutePath(), isDeleteFloder);
            }
            if (isDeleteFloder)
                file.delete();
        }
    }

    public static String getVersionName(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(),
                    0);
            String version = info.versionName;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return "没有获取到版本号";
        }
    }

    /**
     * 启动第三方应用
     */
    public static void stratApk(Context context, String packName, String DownLoadUrl) {
        if (isDownLoadApk) {
            isDownLoadApk = true;
            Toast.makeText(context, "正在下载应用，请勿操作", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!isAppInstalled(context, packName)) {//判断是否已经安装了
            if (DownLoadUrl.equals("") || DownLoadUrl.equals("null")) {//判断下载地址是否为空
                Toast.makeText(context, "下载地址为空，无法下载安装该应用", Toast.LENGTH_SHORT).show();
                return;
            }
            DownLoadApkPackName = packName;
            Toast.makeText(context, "未安装该应用,启动下载", Toast.LENGTH_SHORT).show();
            downloalApk(DownLoadUrl, CommonUitl.createaPKFloder(Environment
                    .getExternalStorageDirectory()
                    .getAbsolutePath()), packName + ".apk",
                    mainActivity.handler);
            return;
        }
        stratApk(context, packName);
    }

    /**
     * 启动第三方应用
     */
    public static void stratApk(Context context, String packName) {
        if (!isAppInstalled(context, packName)) {//判断是否已经安装了
            Toast.makeText(context, "启动错误", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(
                packName);
        if (launchIntent != null) {
            context.startActivity(launchIntent);
        }
    }

    /**
     * 结束第三方进程
     */
    public static void killApk(Context context, final String packName) {
        ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        Method method = null;
        try {
            method = Class.forName("android.app.ActivityManager").getMethod("forceStopPackage", String.class);
            method.invoke(mActivityManager, packName);  //packageName是需要强制停止的应用程序包名
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * 模拟按键
     */
    public static void sendActionKey(final int KeyEvent) {
        IConstant.executorService.execute(new Runnable() {
            @Override
            public void run() {
                Instrumentation inst = new Instrumentation();
                inst.sendKeyDownUpSync(KeyEvent);
            }
        });
    }

    /**
     * 模拟鼠标按键
     */
    public static void sendMotionEvent(final int motionEvent) {
        IConstant.executorService.execute(new Runnable() {
            @Override
            public void run() {
                Instrumentation inst = new Instrumentation();
                inst.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(),
                        motionEvent, 10, 10, 0));
                if (MotionEvent.ACTION_DOWN == motionEvent)
                    inst.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(),
                            MotionEvent.ACTION_UP, 10, 10, 0));
            }
        });
    }

    /**
     * 增加音量
     */
    public static void addVolume(Context context) {
        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        am.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                AudioManager.ADJUST_RAISE, AudioManager.FLAG_PLAY_SOUND
                        | AudioManager.FLAG_SHOW_UI);
    }

    /**
     * 减少音量
     */
    public static void decreaseVolume(Context context) {
        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        am.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                AudioManager.ADJUST_LOWER, AudioManager.FLAG_PLAY_SOUND
                        | AudioManager.FLAG_SHOW_UI);
    }

    public static boolean isAppInstalled(Context context, String packagename) {
        PackageInfo packageInfo;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(packagename, 0);
        } catch (PackageManager.NameNotFoundException e) {
            packageInfo = null;
            e.printStackTrace();
        }
        if (packageInfo == null) {
            return false;
        } else {
            return true;
        }
    }

    private static boolean isDownLoadApk = false;
    public static String DownLoadApkPackName;

    public static void downloalApk(String downloadUrl, final String downloadPath, final String curName, final Handler
            handler) {
        isDownLoadApk = true;
        createFloder();
        OkHttpClient mOkHttpClient = new OkHttpClient();
        Request request = null;
        try {
            request = new Request.Builder().url(downloadUrl).build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (request == null) {
            isDownLoadApk = false;
            Message msg = handler.obtainMessage();
            msg.what = GlobeVariable.downLoadApkFial;
            msg.obj = "文件下载失败";
            handler.sendMessage(msg);
            return;
        }
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                isDownLoadApk = false;
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                isDownLoadApk = true;
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                try {
                    is = response.body().byteStream();
                    long total = response.body().contentLength();
                    File file = new File(downloadPath, curName);
                    file.delete();
                    fos = new FileOutputStream(file);
                    long sum = 0;
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                        sum += len;
                        int progress = (int) (sum * 1.0f / total * 100);
                        Log.d("h_bl", "progress=" + progress);
                        Message msg = handler.obtainMessage();
                        msg.what = GlobeVariable.downLoadApk;
                        msg.obj = "下载应用中,请勿操作\n" + progress + "%";
                        handler.sendMessage(msg);
                    }
                    fos.flush();
                    Log.d("h_bl", "文件下载成功");
                    Message msg = handler.obtainMessage();
                    msg.what = GlobeVariable.downLoadApkSuccess;
                    msg.obj = downloadPath + "/" + curName;
                    handler.sendMessage(msg);
                } catch (Exception e) {
                    Log.d("h_bl", "文件下载失败");
                    Message msg = handler.obtainMessage();
                    msg.what = GlobeVariable.downLoadApkFial;
                    msg.obj = "文件下载失败";
                    handler.sendMessage(msg);
                } finally {
                    isDownLoadApk = false;
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
    public static ArrayList<String> listZi(){
        ArrayList<String> list=new ArrayList<>();
        list.add("A");list.add("B");list.add("C");list.add("D");list.add("E");
        list.add("F");list.add("G");list.add("H");list.add("I");list.add("J");
        list.add("K");list.add("L");list.add("M");list.add("N");list.add("O");
        list.add("P");list.add("Q");list.add("R");list.add("S");list.add("T");
        list.add("U");list.add("V");list.add("W");list.add("X");list.add("Y");
        list.add("Z");list.add("123");list.add("");list.add("");list.add("←");
        return list;
    }
    public static ArrayList<String> listShu(){
        ArrayList<String> list=new ArrayList<>();
        list.add("0");list.add("1");list.add("2");list.add("3");list.add("4");
        list.add("5");list.add("6");list.add("7");list.add("8");list.add("9");
        list.add("ABC");list.add("");list.add("");list.add("");list.add("←");
        return list;
    }
}