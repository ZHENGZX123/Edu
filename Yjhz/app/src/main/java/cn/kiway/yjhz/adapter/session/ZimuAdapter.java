package cn.kiway.yjhz.adapter.session;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import cn.kiway.yjhz.R;

/**
 * Created by Administrator on 2017/7/5.
 */

public class ZimuAdapter extends BaseAdapter {
    public ArrayList<String> list;
    ZimuHolder holder;
    Context context;

    public ZimuAdapter(Context context, ArrayList<String> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View getView(int i, View con, ViewGroup viewGroup) {
        View view = con;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.girdview_p_item, null);
            holder = new ZimuHolder();
            holder.textView = (TextView) view.findViewById(R.id.text);
            holder.imageView = (ImageView) view.findViewById(R.id.img);
            view.setTag(holder);
        } else {
            holder = (ZimuHolder) view.getTag();
        }
        if (i + 1 == list.size()) {
            holder.textView.setVisibility(View.GONE);
            holder.imageView.setVisibility(View.VISIBLE);
        } else {
            holder.textView.setVisibility(View.VISIBLE);
            holder.imageView.setVisibility(View.GONE);
            holder.textView.setText(list.get(i));
        }

        return view;
    }

    class ZimuHolder {
        TextView textView;
        ImageView imageView;
    }
}
