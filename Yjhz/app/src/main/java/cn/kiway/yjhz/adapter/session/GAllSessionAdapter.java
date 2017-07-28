package cn.kiway.yjhz.adapter.session;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import cn.kiway.yjhz.R;
import cn.kiway.yjhz.activity.BaseActivity;
import cn.kiway.yjhz.activity.simulator.GSessionActivity;
import cn.kiway.yjhz.activity.simulator.GSessionDetailActivity;
import cn.kiway.yjhz.model.VideoCateMode;
import cn.kiway.yjhz.utils.IConstant;

/**
 * Created by Administrator on 2017/7/4.
 */

public class GAllSessionAdapter extends ArrayAdapter implements View.OnClickListener {
    public List<VideoCateMode> list;// 显示在界面上的数据
    public int height;
    GAllSessionHodler hodler;

    private int mIndex; // 页数下标，标示第几页，从0开始
    private int mPargerSize;// 每页显示的最大的数量

    public GAllSessionAdapter(@NonNull Context context, List<VideoCateMode> list, int position, int num, int height) {
        super(context, -1);
        this.list = list;
        this.height = height;
        this.mIndex = position;
        this.mPargerSize = num;
    }

    @Override
    public int getCount() {
        return list.size() > (mIndex + 1) * mPargerSize ?
                mPargerSize : (list.size() - mIndex * mPargerSize);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            hodler = new GAllSessionHodler();
            view = LayoutInflater.from(getContext()).inflate(R.layout.gsession_list_item, null);
            hodler.img = (ImageView) view.findViewById(R.id.img);
            hodler.title = (TextView) view.findViewById(R.id.title);
            view.setTag(hodler);
        } else {
            hodler = (GAllSessionHodler) view.getTag();
        }
        final int pos = position + mIndex * mPargerSize;//假设mPageSiez
        ((BaseActivity) getContext()).imageLoader.displayImage(list.get(pos).getPreview(), hodler.img, (
                (BaseActivity) getContext()).displayImageOptions);
        hodler.img.setLayoutParams(new RelativeLayout.LayoutParams(height / 2 - 15, height / 2 - 15));
        view.setTag(R.id.bundle_params, position);
        view.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        int postion = Integer.parseInt(v.getTag(R.id.bundle_params).toString());
        VideoCateMode model = list.get(postion + mIndex * mPargerSize);
        Bundle bundle = new Bundle();
        bundle.putSerializable(IConstant.BUNDLE_PARAMS, model);
        bundle.putSerializable(IConstant.BUNDLE_PARAMS1, ((GSessionActivity) getContext()).classModel);
        Intent intent = new Intent(getContext(), GSessionDetailActivity.class);
        intent.putExtras(bundle);
        getContext().startActivity(intent);
    }

    class GAllSessionHodler {
        /**
         * 图片
         */
        ImageView img;
        TextView title;
    }
}
