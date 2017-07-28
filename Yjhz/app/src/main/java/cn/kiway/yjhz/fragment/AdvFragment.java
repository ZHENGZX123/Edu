package cn.kiway.yjhz.fragment;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.util.ArrayList;
import java.util.List;

import cn.kiway.yjhz.R;

public class AdvFragment extends Fragment {
    List<String> list;
    int position;
    View view;
    ImageView imageView;
    /**
     * 加载图像
     */
    private ImageLoader imageLoader;
    /**
     * 图像加载配置参数
     */
    private DisplayImageOptions options;

//    public AdvFragment(int position, List<String> list) {
//        super();
//        this.list = list;
//        this.position = position;
//    }

    public static AdvFragment newInstance(int position, List<String> list) {
        Bundle bundle = new Bundle();
        bundle.putInt("positon", position);
        bundle.putStringArrayList("list", (ArrayList<String>) list);
        AdvFragment fragment = new AdvFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.position = getArguments().getInt("positon");
            this.list = (List<String>) getArguments().getStringArrayList("list");
        }
    }

    public AdvFragment() {
        super();
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = LayoutInflater.from(getActivity()).inflate(
                R.layout.activity_img, null);
        imageView = (ImageView) view.findViewById(R.id.img);
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(getActivity()));
        options = new DisplayImageOptions.Builder()
                .bitmapConfig(Bitmap.Config.RGB_565).cacheOnDisk(true)
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                .resetViewBeforeLoading(true).cacheInMemory(true)
                .considerExifParams(true).cacheOnDisc(true).build();
        if (list != null && list.size() > 0)
            imageLoader.displayImage(list.get(position), imageView, options);
        return view;
    }

}
