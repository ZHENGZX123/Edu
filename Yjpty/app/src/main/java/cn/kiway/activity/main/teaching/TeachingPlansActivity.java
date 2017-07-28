package cn.kiway.activity.main.teaching;

import android.annotation.SuppressLint;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import cn.kiway.IConstant;
import cn.kiway.IUrContant;
import cn.kiway.Yjpty.R;
import cn.kiway.activity.BaseNetWorkActicity;
import cn.kiway.activity.MainActivity;
import cn.kiway.activity.main.WebViewActivity;
import cn.kiway.dialog.ClearDataDialog;
import cn.kiway.dialog.ClearDataDialog.ClearDataCallBack;
import cn.kiway.fragment.BaseFragment;
import cn.kiway.fragment.teacher.SessionDbFragment;
import cn.kiway.fragment.teacher.SessionTable2Fragment;
import cn.kiway.utils.SharedPreferencesUtil;
import cn.kiway.utils.ViewUtil;

public class TeachingPlansActivity extends BaseNetWorkActicity implements
        OnCheckedChangeListener, ClearDataCallBack {
    RadioGroup rg;
    BaseFragment sessionTableBaseFragment, sessionDbBaseFragment;
    int page = 0;
    ClearDataDialog dialog;
    boolean isAttendClass;
    boolean isRunUdp = true;
    WifiManager.MulticastLock lock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_plan);
        isAttendClass = bundle.getBoolean(IConstant.BUNDLE_PARAMS);// 是否为上课动作
        try {
            if (isAttendClass) {// 是否为上课动作
                View v = ViewUtil.findViewById(this, R.id.previos_class);// 初始化退出的dialog
                dialog = new ClearDataDialog(this, this,
                        resources.getString(R.string.exit_session), v);
                String wifiName = SharedPreferencesUtil.getString(this,
                        IConstant.WIFI_NEME + app.getClassModel().getId());
                if (!Boolean.parseBoolean(wifiName.split(":::")[1])) {// 不是wifi热点启动监听广播
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            initView();
            loadData();
            setData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("isAttendClass", isAttendClass);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState.containsKey("isAttendClass"))
            isAttendClass = savedInstanceState.getBoolean("isAttendClass");
    }

    @Override
    public void initView() throws Exception {
        rg = ViewUtil.findViewById(this, R.id.rg);
        rg.setOnCheckedChangeListener(this);
        // 进来默认课程表
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        hideFragment(transaction, sessionTableBaseFragment,
                sessionDbBaseFragment);
        if (sessionTableBaseFragment == null) {
            sessionTableBaseFragment =  SessionTable2Fragment.newInstance(isAttendClass);
            transaction.add(R.id.fragment, sessionTableBaseFragment,
                    "sessionTableBaseFragment");
        } else if (sessionTableBaseFragment.isAdded()
                && sessionTableBaseFragment.isHidden()) {
            transaction.show(sessionTableBaseFragment);
        }
        transaction.commit();
        findViewById(R.id.see_plans).setOnClickListener(this);
        findViewById(R.id.previos_class).setOnClickListener(this);
    }

    @SuppressLint("CommitTransaction")
    @Override
    public void onCheckedChanged(RadioGroup rg, int checkId) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        int pg = 0;
        switch (checkId) {
            case R.id.rb1:
                pg = 0;
                break;
            case R.id.rb2:
                pg = 1;
                break;
            case R.id.rb3:
                pg = 2;
                break;
        }
        if (page > pg) {// 切换动画
            transaction.setCustomAnimations(R.anim.silde_in_left,
                    R.anim.silde_out_right);
        } else {
            transaction.setCustomAnimations(R.anim.silde_in_right,
                    R.anim.silde_out_left);
        }
        page = pg;
        hideFragment(transaction, sessionTableBaseFragment,
                sessionDbBaseFragment);
        switch (checkId) {
            case R.id.rb1:// 课程表
                if (sessionTableBaseFragment == null) {
                    sessionTableBaseFragment = SessionTable2Fragment.newInstance(
                            isAttendClass);
                    transaction.add(R.id.fragment, sessionTableBaseFragment,
                            "sessionTableBaseFragment");
                } else if (sessionTableBaseFragment.isAdded()
                        && sessionTableBaseFragment.isHidden()) {
                    transaction.show(sessionTableBaseFragment);
                }
                break;
            case R.id.rb2:// 课程库
                if (sessionDbBaseFragment == null) {
                    sessionDbBaseFragment = SessionDbFragment.newInstance(isAttendClass);
                    transaction.add(R.id.fragment, sessionDbBaseFragment,
                            "sessionDbBaseFragment");
                } else if (sessionDbBaseFragment.isAdded()
                        && sessionDbBaseFragment.isHidden()) {
                    transaction.show(sessionDbBaseFragment);
                }
                break;
        }
        transaction.commit();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.see_plans:// 课程计划表
                Bundle bundle = new Bundle();
                bundle.putString(IConstant.BUNDLE_PARAMS,
                        IUrContant.SESSION_PLAN_URL + "?classId="
                                + app.getClassModel().getId());
                bundle.putString(IConstant.BUNDLE_PARAMS1,
                        resources.getString(R.string.class_plans));
                startActivity(WebViewActivity.class, bundle);
                break;
            case R.id.previos_class:
                if (!isAttendClass)// 如果不是在上课则不发送请求
                {
                    finish();
                } else {
                    if (dialog != null && !dialog.isShowing()) {// 退出上课的dialog
                        dialog.show();
                    }
                }
                break;
        }
    }

    @Override
    public void clearDataCallBack(View vx) throws Exception {
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!isAttendClass)// 如果不是在上课则不发送请求
            {
                startActivity(MainActivity.class);
                finish();
            } else {
                if (dialog != null && !dialog.isShowing()) {// 退出上课的dialog
                    dialog.show();
                }
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
