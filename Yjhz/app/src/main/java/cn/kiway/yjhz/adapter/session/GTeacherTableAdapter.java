package cn.kiway.yjhz.adapter.session;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import cn.kiway.yjhz.R;
import cn.kiway.yjhz.activity.BaseActivity;
import cn.kiway.yjhz.activity.simulator.VideoListActivity;
import cn.kiway.yjhz.model.ClassModel;
import cn.kiway.yjhz.model.VideoModel;
import cn.kiway.yjhz.utils.CommonUitl;
import cn.kiway.yjhz.utils.IConstant;
import cn.kiway.yjhz.utils.Logger;
import cn.kiway.yjhz.utils.okhttp.HttpRequestUrl;
import cn.kiway.yjhz.utils.okhttp.HttpUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class GTeacherTableAdapter extends ArrayAdapter<VideoModel>
        implements OnClickListener, Callback {
    BaseActivity activity;
    TeacherTableHolder holder;
    public List<VideoModel> list;// 显示在界面上的数据
    /**
     * 是否为看视频
     */
    boolean view;
    boolean isTeacherSession;
    ClassModel classModel;
    public int wight;
    private int mIndex; // 页数下标，标示第几页，从0开始
    private int mPargerSize;// 每页显示的最大的数量
    int height;

    public GTeacherTableAdapter(Context context, List<VideoModel>
            list, boolean isTeacherSession, ClassModel classModel,int wight,int height,int position,int num) {
        super(context, -1);
        this.activity = (BaseActivity) context;
        this.list = list;
        this.classModel = classModel;
        this.isTeacherSession = isTeacherSession;
        this.wight=wight;
        this.height=height;
        this.mIndex=position;
        this.mPargerSize=num;
    }

    @Override
    public int getCount() {
        return list.size() > (mIndex + 1) * mPargerSize ?
                mPargerSize : (list.size() - mIndex*mPargerSize);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(activity).inflate(R.layout.gsession_detail_list_item, null);
            holder = new TeacherTableHolder();
            holder.pic = (ImageView) view.findViewById(R.id.img);
            holder.videoName = (TextView) view.findViewById(R.id.title);
            holder.isPlay = (TextView) view.findViewById(R.id.isplay);
            holder.videoTotal= (TextView) view.findViewById(R.id.total);
            view.setTag(holder);
        } else {
            holder = (TeacherTableHolder) view.getTag();
        }
        int pos = position + mIndex * mPargerSize;//假设mPageSiez
        Logger.log("*********"+pos+"*********"+mIndex+"*********"+mPargerSize);
        VideoModel model = list.get(pos);
        holder.videoName.setText(model.getName());// 名字
        if (model.getReadCount() > 0)
            holder.isPlay.setVisibility(View.VISIBLE);
        else
            holder.isPlay.setVisibility(View.GONE);
        // 视频图像
        activity.imageLoader.displayImage(model.getPreview(), holder.pic, activity.displayImageOptions);
        holder.videoTotal.setText(model.getTeachingAim());
        view.setLayoutParams(new RelativeLayout.LayoutParams(wight/2-10,height/3-5));
        view.setTag(R.id.bundle_params, position);
        view.setOnClickListener(this);
        return view;
    }


    static class TeacherTableHolder {
        /**
         * 视频图片
         */
        ImageView pic;
        /**
         * 视频名称
         */
        TextView videoName,videoTotal;
        TextView isPlay;
    }

    VideoModel model;
    String RequsetUrl, RequesetType;

    @Override
    public void onClick(View v) {
        int position = Integer.parseInt(v.getTag(R.id.bundle_params).toString());
        final int pos = position + mIndex * mPargerSize;//假设mPageSiez
        model = list.get(pos);
        if (model.isKiectSession()){
            CommonUitl.stratApk(activity, model.getKinectPackageName());
            return;
        }
//        switch (v.getId()) {
//            case R.id.look:// 看教案
//                view = false;
//                RequesetType = "&opType=1";
//                break;
//            case R.id.view:// 看视频
                list.get(position).setReadCount(list.get(position).getReadCount() + 1);
                notifyDataSetChanged();
                RequesetType = "&opType=2";
//                view = true;
//                break;
//        }
        if (isTeacherSession)
            RequesetType = RequesetType + "&sectionType=teacher";
        else
            RequesetType = RequesetType + "&sectionType=null";
        RequsetUrl = HttpRequestUrl.GET_ONE_COURSE.replace
                ("{sectionId}", model.getId() + "") + classModel.getId() + RequesetType;
        activity.yjhzAppication.mHttpClient.newCall(HttpUtils.get(RequsetUrl, activity.yjhzAppication.session))
                .enqueue(this);
    }

    @Override
    public void onFailure(Call call, IOException e) {

    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        Logger.log(call.request().url().toString());
        if (call.request().url().toString().equals(RequsetUrl)) {
            try {
                JSONObject data = new JSONObject(response.body().string());
                Logger.log(data);
                if (data.optInt("StatusCode") == 200) {
                    activity.mCache.put(HttpRequestUrl.GET_ONE_COURSE + model.getId(), data);
                    String jsonTest = new GsonBuilder().create().toJson(model, VideoModel.class);//保存最近播放记录
                    activity.mCache.put(classModel.getId(),activity.mCache.getAsJSONArray(classModel.getId()).put(new JSONObject(jsonTest)));
                    Intent intent = new Intent(activity, VideoListActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(IConstant.BUNDLE_PARAMS, model);
                    intent.putExtras(bundle);
                    activity.startActivity(intent);
                } else if (data.optInt("StatusCode") == 500) {
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
//    private MyFilter mFilter;
//    @Override
//    public Filter getFilter() {
//        if (null == mFilter) {
//            mFilter = new MyFilter();
//        }
//        return mFilter;
//    }
//    // 自定义Filter类
//    class MyFilter extends Filter {
//
//        @SuppressLint("DefaultLocale")
//        @Override
//        // 该方法在子线程中执行
//        // 自定义过滤规则
//        protected FilterResults performFiltering(CharSequence constraint) {
//            FilterResults results = new FilterResults();
//            List<VideoModel> newValues = new ArrayList<VideoModel>();
//            String filterString = constraint.toString().trim()
//                    .toLowerCase();
//            // 如果搜索框内容为空，就恢复原始数据
//
//            if (TextUtils.isEmpty(filterString)) {
//                newValues = listData;
//            } else {
//                // 过滤出新数据
//                for (VideoModel str : listData) {
//                    if ((-1 != str.getName().toLowerCase()
//                            .indexOf(filterString))
//                            || (-1 != str.getSessionName()
//                            .toLowerCase()
//                            .indexOf(filterString))
//                            || (-1 != str.getTypeName().toLowerCase()
//                            .indexOf(filterString))||(-1 != str.getPingYin().toLowerCase()
//                            .indexOf(filterString.toLowerCase()))) {
//                        newValues.add(str);
//                    }
//                }
//            }
//            results.values = newValues;
//            results.count = newValues.size();
//            return results;
//        }
//
//        @SuppressWarnings("unchecked")
//        @Override
//        protected void publishResults(CharSequence constraint,
//                                      FilterResults results) {
//            list = (List<VideoModel>) results.values;
//            if (results.count > 0) {
//                notifyDataSetChanged(); // 通知数据发生了改变
//            } else {
//                notifyDataSetInvalidated(); // 通知数据失效
//            }
//        }
//    }
}

