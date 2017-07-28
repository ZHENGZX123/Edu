package cn.kiway.yjhz.wifimanager.MessageHander;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import cn.kiway.yjhz.activity.box.ImageViewActivity;
import cn.kiway.yjhz.activity.box.PptActivity;
import cn.kiway.yjhz.activity.box.VideoActivity;
import cn.kiway.yjhz.activity.simulator.GSessionActivity;
import cn.kiway.yjhz.utils.CommonUitl;
import cn.kiway.yjhz.utils.GlobeVariable;

/**
 * Created by Administrator on 2017/3/14.
 */

public class AccpectMessageHander extends Handler {
    public static VideoActivity audioActivity;// 视频
    private static PptActivity pptActivity;// ppt
    private static ImageViewActivity imageViewActivity;// 图片
    private static GSessionActivity activity;// 主界面

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch (msg.what) {
            case GlobeVariable.SHOW_WEBSOCKET_MESSAGE:
                String message = (String) msg.obj;
                Log.i("接受到的命令：：：：：：" , message);
                String array[] = message.split(":::");
                int s = Integer.parseInt(array[0]);
                switch (s) {
                    case GlobeVariable.PLAY_VIDEO:
                        if (audioActivity != null && !audioActivity.isFinishing()) {
                            String s1 = "";
                            if (array.length >= 4)
                                s1 = array[3];
                            audioActivity.accecpData(array[1], s1);
                        } else {
                            if (activity != null) {
                                if (array.length >= 4) {
                                    activity.Play(array[1] + ":::" + array[2] + ":::" + array[3], 1);
                                } else if (array.length == 3) {
                                    activity.Play(array[1] + ":::" + array[2], 1);
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
             * if (audioActivity != null) { if (array.length >= 2) {
			 * audioActivity.theLast(array[1]); } else {
			 * audioActivity.theLast(""); } }
			 */
                        break;
                    case GlobeVariable.THE_NEXT:
            /*
             * if (audioActivity != null) { if (array.length >= 2) {
			 * audioActivity.theNext(array[1]); } else {
			 * audioActivity.theNext(""); } }
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
                        if (audioActivity != null) {
                            audioActivity.finish();
                            audioActivity = null;
                        }
                        if (imageViewActivity != null) {
                            imageViewActivity.finish();
                            imageViewActivity = null;
                        }

                        if (activity != null) {
                            if (array.length >= 3) {
                                activity.Play(array[1] + ":::" + array[2] + ":::" + array[3], 2);
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
                        if (activity != null) {
                            activity.ConnectWifi(array[1], array[2], array[3]);
                        }
                        break;
                    case GlobeVariable.PLAY_IMG:
                        if (imageViewActivity != null) {
                            imageViewActivity.finish();
                        }
                        if (audioActivity != null) {
                            audioActivity.finish();
                            audioActivity = null;
                        }
                        if (pptActivity != null) {
                            pptActivity.finish();
                            pptActivity = null;
                        }
                        if (activity != null) {
                            if (array.length >= 3) {
                                activity.Play(array[1] + ":::" + array[2] + ":::" + array[3], 3);
                            } else {
                                activity.Play("", 3);
                            }
                        }
                        break;
                    case GlobeVariable.STRAT_APK:
                        if (array.length >= 3)
                            CommonUitl.stratApk(activity, array[1], array[2]);
                        else
                            CommonUitl.stratApk(activity, array[1]);
                        break;
                    case GlobeVariable.KILL_APK:
                        CommonUitl.killApk(activity, array[1]);
                        break;
                    case GlobeVariable.ACTION_KEY:
                        CommonUitl.sendActionKey(Integer.parseInt(array[1]));
                        break;
                    case GlobeVariable.MotionEvent:
                        CommonUitl.sendMotionEvent(Integer.parseInt(array[1]));
                        break;
                    default:
                        Log.i("无法识别的错误", "错误");
                        break;
                }
                break;
        }
    }

    public static void setActivity(GSessionActivity activity) {
        AccpectMessageHander.activity = activity;
    }

    public static void setAudioActivity(VideoActivity audioActivity) {
        AccpectMessageHander.audioActivity = audioActivity;
    }

    public static void setPptActivity(PptActivity pptActivity) {
        AccpectMessageHander.pptActivity = pptActivity;
    }

    public static void setImageViewActivity(ImageViewActivity imageViewActivity) {
        AccpectMessageHander.imageViewActivity = imageViewActivity;
    }
}
