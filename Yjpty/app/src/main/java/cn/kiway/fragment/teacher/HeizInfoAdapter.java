package cn.kiway.fragment.teacher;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import cn.kiway.IConstant;
import cn.kiway.Yjpty.R;
import cn.kiway.activity.BaseActivity;
import cn.kiway.activity.MipcaCaptureActivity;
import cn.kiway.activity.main.teaching.TeachingPlansActivity;
import cn.kiway.activity.main.teaching.netty.NettyClientBootstrap;
import cn.kiway.activity.main.teaching.netty.PushClient;
import cn.kiway.model.ClassModel;
import cn.kiway.model.HeziStautsModel;
import cn.kiway.utils.SharedPreferencesUtil;
import cn.kiway.utils.StringUtil;
import cn.kiway.utils.ViewUtil;

public class HeizInfoAdapter extends ArrayAdapter<HeziStautsModel> implements
        OnClickListener {
    public List<HeziStautsModel> list;
    HeziInfoModel holder;
    BaseActivity activity;
    List<ClassModel> classModels;

    public HeizInfoAdapter(Context context, List<HeziStautsModel> list,
                           List<ClassModel> classModels) {
        super(context, -1);
        activity = (BaseActivity) context;
        this.list = list;
        this.classModels = classModels;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            holder = new HeziInfoModel();
            view = ViewUtil.inflate(activity, R.layout.heziinfo_list_item);
            holder.className = ViewUtil.findViewById(view, R.id.class_name);
            holder.classGrade = ViewUtil.findViewById(view, R.id.class_grade);
            holder.heziCode = ViewUtil.findViewById(view, R.id.hezi_id);
            holder.heziInfo = ViewUtil.findViewById(view, R.id.hezi_info);
            view.setTag(holder);
        } else {
            holder = (HeziInfoModel) view.getTag();
        }
        HeziStautsModel model = list.get(position);
        ViewUtil.setContent(holder.className, model.getClassName());
        switch (model.getGrade()) {
            case 1:
                ViewUtil.setContent(holder.classGrade, R.string.ddb);
                break;
            case 2:
                ViewUtil.setContent(holder.classGrade, R.string.db);
                break;
            case 3:
                ViewUtil.setContent(holder.classGrade, R.string.zb);
                break;
            case 4:
                ViewUtil.setContent(holder.classGrade, R.string.xb);
                break;
        }
        ViewUtil.setContent(holder.heziCode, model.getHeziCode());
        switch (model.getHeziType()) {
            case 1:
                ViewUtil.setContent(holder.heziInfo, "点击上课");
                holder.heziInfo.setBackgroundResource(R.drawable.green_val);
                break;
            case 2:
                ViewUtil.setContent(holder.heziInfo, "扫码上课");
                holder.heziInfo.setBackgroundResource(R.drawable.gray_val);
                break;
        }
        view.setTag(R.id.bundle_params, position);
        view.setOnClickListener(this);
        return view;
    }

    class HeziInfoModel {
        TextView className;
        TextView classGrade;
        TextView heziCode;
        TextView heziInfo;
    }

    @Override
    public void onClick(View v) {
        int position = StringUtil
                .toInt(v.getTag(R.id.bundle_params).toString());
        HeziStautsModel model = list.get(position);
        Bundle bundle = new Bundle();
        activity.app.setClassModel(classModels.get(position));
        switch (model.getHeziType()) {
            case 1:
                if (model.getHeziResoures().indexOf(model.getString()) < 0) {
                    String string;
                    if (model.getHeziResoures().equals("没有资源文件")) {
                        string = "盒子没有资源,无法上课";
                    } else {
                        string = "盒子资源为" + model.getHeziResoures()
                                + "与所选班级不对应，无法上课";
                    }
                    ViewUtil.showMessage(activity, string);
                    return;
                }
                activity.app.setSessionIp(model.getHeziIP());
                NettyClientBootstrap.host = model.getHeziIP();
                PushClient.create();
                if (PushClient.isOpen()) {
                    PushClient.close();
                }
                SharedPreferencesUtil.save(activity,
                        IConstant.WIFI_NEME + classModels.get(position).getId(), "");
                PushClient.start();
                bundle.putBoolean(IConstant.BUNDLE_PARAMS, true);// 1为上课 2看备课
                activity.startActivity(TeachingPlansActivity.class, bundle);
                activity.finish();
                break;
            case 2:
                bundle.putInt(IConstant.BUNDLE_PARAMS, 1);
                activity.startActivity(MipcaCaptureActivity.class, bundle);
                activity.finish();
                break;
        }
    }
}
