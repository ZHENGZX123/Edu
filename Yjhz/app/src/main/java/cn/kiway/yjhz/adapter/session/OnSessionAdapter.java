package cn.kiway.yjhz.adapter.session;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.kiway.yjhz.R;
import cn.kiway.yjhz.activity.BaseActivity;
import cn.kiway.yjhz.model.OnClassModel;

/**
 * Created by Administrator on 2017/7/5.
 */

public class OnSessionAdapter extends ArrayAdapter {
   public List<OnClassModel> PlayList;//当前播放视频的列表
    public boolean isZhuke;//是否为主课列表
    OnSessionHolder holder;
    public OnSessionAdapter(@NonNull Context context,List<OnClassModel> PlayList,boolean isZhuke) {
        super(context, -1);
        this.PlayList=PlayList;
        this.isZhuke=isZhuke;
    }

    @Override
    public int getCount() {
        return PlayList.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view=convertView;
        if (view==null){
            view= LayoutInflater.from(getContext()).inflate(R.layout.onsession_list_item,null);
            holder=new OnSessionHolder();
            holder.Img= (ImageView) view.findViewById(R.id.img);
            holder.ZhuKeTV= (TextView) view.findViewById(R.id.name_z);
            holder.WeiKeTV= (TextView) view.findViewById(R.id.name);
            view.setTag(holder);
        }else {
            holder= (OnSessionHolder) view.getTag();
        }
        if (isZhuke){
            view.findViewById(R.id.layout).setVisibility(View.GONE);
            holder.ZhuKeTV.setVisibility(View.VISIBLE);
            holder.ZhuKeTV.setText(PlayList.get(position).getContent());
        }else {
            view.findViewById(R.id.layout).setVisibility(View.VISIBLE);
            holder.ZhuKeTV.setVisibility(View.GONE);
            holder.WeiKeTV.setText(PlayList.get(position).getContent());
            ((BaseActivity)getContext()).imageLoader.displayImage("drawable://"+R.drawable.yjpty_landi,holder.Img, ((BaseActivity)getContext()).displayImageOptions);
        }
        return view;
    }
    class OnSessionHolder{
        /**
         * 主课名字
         * */
        TextView ZhuKeTV;
        /**
         * 微课名字
         * */
        TextView WeiKeTV;
        /**
         * 微课图像
         * */
        ImageView Img;
    }
}
