package cn.kiway.fragment.common;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.io.ByteArrayInputStream;

import cn.kiway.IUrContant;
import cn.kiway.Yjptj.R;
import cn.kiway.adapter.message.MessageAdapter;
import cn.kiway.dialog.picture.SavePictureDialog;
import cn.kiway.fragment.BaseFragment;
import cn.kiway.http.BaseHttpConnectPool;
import cn.kiway.utils.StringUtil;
import cn.kiway.utils.ViewUtil;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher.OnPhotoTapListener;

/**
 * 图像浏览帧
 *
 * @author ZAO
 */
public class ViewPhotoFragment extends BaseFragment implements
        OnPhotoTapListener, ImageLoadingListener, OnLongClickListener {
    /**
     * 图像地址
     */
    String url;
    SavePictureDialog savePictureDialog;
    boolean bool;
    PhotoView photoView;


    public static ViewPhotoFragment newInstance(String url, Boolean bool) {
        Bundle args = new Bundle();
        args.putString("url", url);
        args.putBoolean("bool", bool);
        ViewPhotoFragment fragment = new ViewPhotoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.bool = getArguments().getBoolean("bool");
            this.url = getArguments().getString("url");
        }
    }

    public ViewPhotoFragment() {
        super();
    }

    @Override
    @Nullable
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = ViewUtil.inflate(activity, R.layout.fragment_view_photo);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        savePictureDialog = new SavePictureDialog(activity);
        if (bool) {
            activity.imageLoader.loadImage(StringUtil.imgUrl(activity, url),
                    this);
            view.setOnLongClickListener(this);
            if (StringUtil.imgUrl(activity, url).indexOf("file://") < 0) {
                BaseHttpConnectPool.loodingDialog.show();
            }

        } else {
            if ((MessageAdapter.byteData != null)) {
                photoView = (PhotoView) view.findViewById(R.id.iv_photo);
                Bitmap btp = BitmapFactory
                        .decodeStream(new ByteArrayInputStream(
                                MessageAdapter.byteData));
                photoView.setImageBitmap(btp);
            }
        }
        ((PhotoView) view).setOnPhotoTapListener(this);
    }

    @Override
    public void loadData() throws Exception {
    }

    @Override
    public void onPhotoTap(View view, float x, float y) {
        activity.finish();
    }

    @Override
    public void onLoadingCancelled(String arg0, View arg1) {
    }

    @Override
    public void onLoadingComplete(String str, View arg1, Bitmap arg2) {
        ((PhotoView) view).setImageBitmap(arg2);
        // activity.imageLoader.displayImage(StringUtil.imgUrl(activity, url),
        // (PhotoView) view, activity.fadeOptions);
        if (BaseHttpConnectPool.loodingDialog != null) {
            BaseHttpConnectPool.loodingDialog.mLoadingDialog.dismiss();
        }
    }

    @Override
    public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
    }

    @Override
    public void onLoadingStarted(String arg0, View arg1) {
    }

    @Override
    public boolean onLongClick(View arg0) {
        savePictureDialog.setPicUrl((IUrContant.BASE_URL + "/" + url).replace(
                "\\", "/"));
        if (savePictureDialog != null && !savePictureDialog.isShowing())
            savePictureDialog.show();
        return false;
    }
}
