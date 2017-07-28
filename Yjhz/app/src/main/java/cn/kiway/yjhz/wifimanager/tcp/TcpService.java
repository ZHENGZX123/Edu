package cn.kiway.yjhz.wifimanager.tcp;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import cn.kiway.yjhz.MainActivity;
import cn.kiway.yjhz.activity.box.ImageViewActivity;
import cn.kiway.yjhz.activity.box.PptActivity;
import cn.kiway.yjhz.activity.box.VideoActivity;
import cn.kiway.yjhz.utils.CommonUitl;
import cn.kiway.yjhz.utils.GlobeVariable;

public class TcpService implements Runnable {
    private Thread receiveTCPThread;// tcp线程
    private static MainActivity activity;// 主界面
    private static TcpService instance;// tcpservice
    private static VideoActivity audioActivity;// 视频
    private static PptActivity pptActivity;// ppt
    private static ImageViewActivity imageViewActivity;// 图片
    private boolean isThreadRunning = true;// 是否跑线程
    public static ServerSocket server;// socket服务
    private Socket client = null;
    static BufferedReader bufferedReaderServer = null;
    static PrintWriter printWriterServer = null;

    public static TcpService getInstance(Context context) {
        activity = (MainActivity) context;
        instance = new TcpService();
        return instance;
    }

    @Override
    public void run() {
        try {
            if (server == null)
                server = new ServerSocket(30000);
            // Toast.makeText(activity, "", Toast.LENGTH_LONG).show();
            System.out.println("TCP线程启动：：：：：：：：：：：");
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        while (isThreadRunning) {
            try {
                client = server.accept();
                new Thread(new ServerThread(client)).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static VideoActivity getAudioActivity() {
        return audioActivity;
    }

    public static void setAudioActivity(VideoActivity audioActivity) {
        TcpService.audioActivity = audioActivity;
    }

    public static PptActivity getPptActivity() {
        return pptActivity;
    }

    public static void setPptActivity(PptActivity pptActivity) {
        TcpService.pptActivity = pptActivity;
    }

    public static void setImageViewActivity(ImageViewActivity imageViewActivity) {
        TcpService.imageViewActivity = imageViewActivity;
    }

    public static ImageViewActivity getImageViewActivity() {
        return imageViewActivity;
    }

    /**
     * 开始监听线程
     **/
    public void startTCPSocketThread() {
        if (receiveTCPThread == null) {
            receiveTCPThread = new Thread(this);
            receiveTCPThread.start();
        }
        isThreadRunning = true;
        Log.i("TAG", "TCP 线程启动成功");
    }

    private class ServerThread extends Thread {
        @SuppressWarnings("unused")
        private Socket s = null;

        public ServerThread(Socket s) {
            this.s = s;
            try {
                bufferedReaderServer = new BufferedReader(
                        new InputStreamReader(s.getInputStream()));
                printWriterServer = new PrintWriter(s.getOutputStream(), true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run() {
            char[] buffer = new char[256];
            int count = 0;
            while (isThreadRunning) {
                try {
                    if ((count = bufferedReaderServer.read(buffer)) > 0) {
                        printWriterServer.print("1");// 发送客户端
                        printWriterServer.flush();
                        if (getInfoBuff(buffer, count) != null) {
                            String array[] = getInfoBuff(buffer, count).split(
                                    ":::");
                            Log.d("接收到的消息命令", getInfoBuff(buffer, count));
                            int s = Integer.parseInt(array[0]);
                            switch (s) {
                                case GlobeVariable.PLAY_VIDEO:
                                    if (audioActivity != null
                                            && !audioActivity.isFinishing()) {
                                        audioActivity
                                                .getObbVideoPath(array[1]);
                                    } else {
                                        if (activity != null) {
                                            if (array.length >= 3) {
                                                activity.Play(array[1] + ":::"
                                                        + array[2], 1);
                                            } else {
                                                activity.Play("", 1);
                                            }
                                        }
                                    }
                                    break;
                                case GlobeVariable.INITIAL_SCREEN:
                                    if (audioActivity != null) {
                                        audioActivity.finish();
                                        audioActivity = null;
                                    }
                                    if (pptActivity != null) {
                                        pptActivity.finish();
                                        pptActivity = null;
                                    }
                                    if (imageViewActivity != null) {
                                        imageViewActivity.finish();
                                        imageViewActivity = null;
                                    }
                                    if (activity != null) {
                                        activity.initialScreen();
                                    }
                                    break;

                                case GlobeVariable.PAUSE_PLAY_VIDEO:
                                    if (audioActivity != null) {
                                        audioActivity.pauseVideo();
                                    }
                                    break;
                                case GlobeVariable.RESTART_PLAY_VIDEO:
                                    if (audioActivity != null) {
                                        audioActivity.resumeVideo();
                                    }
                                    break;
                                case GlobeVariable.ADD_VOLUME:
                                    CommonUitl.addVolume(activity);
                                    break;
                                case GlobeVariable.DECREASE_VOLUME:
                                    CommonUitl.decreaseVolume(activity);
                                    break;
                                case GlobeVariable.STOP_PLAY_VIDEO:
                                    if (audioActivity != null) {
                                        audioActivity.finish();
                                    }
                                    break;
                                case GlobeVariable.ADD_STEP:
                                    if (audioActivity != null) {
                                        // audioActivity.goHead();
                                    }
                                    break;
                                case GlobeVariable.DECREASE_STEP:
                                    if (audioActivity != null) {
                                        // audioActivity.retreat();
                                    }
                                    break;
                                case GlobeVariable.THE_LAST:
                                /*
								 * if (audioActivity != null) { if (array.length
								 * >= 2) { audioActivity.theLast(array[1]); }
								 * else { audioActivity.theLast(""); } }
								 */
                                    break;
                                case GlobeVariable.THE_NEXT:
								/*
								 * if (audioActivity != null) { if (array.length
								 * >= 2) { audioActivity.theNext(array[1]); }
								 * else { audioActivity.theNext(""); } }
								 */
                                    break;

                                case GlobeVariable.BRIGHT:
                                    if (audioActivity != null) {
                                        audioActivity.setShineScreen();
                                    }
                                    break;
                                case GlobeVariable.DRAK:
                                    if (audioActivity != null) {
                                        audioActivity.setDarkScreen();
                                    }
                                    break;
                                case GlobeVariable.CLASSCONTENT:
                                    // if (activity != null) {
                                    // activity.setClassContent(array[1]);
                                    // }
                                    break;
                                case GlobeVariable.USERINFO:
                                    // if (activity != null) {
                                    // activity.setUserInfo(array[1]);
                                    // }
                                    break;
                                case GlobeVariable.PLAY_PPT:
                                    if (pptActivity != null) {
                                        pptActivity.finish();
                                    }
                                    if (activity != null) {
                                        if (array.length >= 3) {
                                            activity.Play(array[1] + ":::"
                                                    + array[2], 2);
                                        } else {
                                            activity.Play("", 2);
                                        }
                                    }
                                    break;
                                case GlobeVariable.PPT_NEXT:
                                    if (pptActivity != null) {
                                        pptActivity.next();
                                    }
                                    break;
                                case GlobeVariable.PPT_LASR:
                                    if (pptActivity != null) {
                                        pptActivity.pre();
                                    }
                                    break;
                                case GlobeVariable.GET_WIFI_INFO:
                                    printWriterServer.print("1");// 发送客户端
                                    printWriterServer.flush();
                                    if (activity != null) {
                                        activity.ConnectWifi(array[1], array[2],
                                                array[3]);
                                    }
                                    break;
                                case GlobeVariable.PLAY_IMG:
                                    if (imageViewActivity != null) {
                                        imageViewActivity.finish();
                                    }
                                    if (activity != null) {
                                        if (array.length >= 3) {
                                            activity.Play(array[1] + ":::"
                                                    + array[2], 3);
                                        } else {
                                            activity.Play("", 3);
                                        }
                                    }
                                    break;
                                default:
                                    Log.i("无法识别的错误", "错误");
                                    break;
                            }
                        }
                    }
                } catch (Exception e) {
                    return;
                }
            }
        }
    }

    private String getInfoBuff(char[] buff, int count) {
        char[] temp = new char[count];
        for (int i = 0; i < count; i++) {
            temp[i] = buff[i];
        }
        return new String(temp);
    }
}
