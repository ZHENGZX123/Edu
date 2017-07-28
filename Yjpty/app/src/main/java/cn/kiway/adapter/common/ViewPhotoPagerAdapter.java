package cn.kiway.adapter.common;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

import cn.kiway.activity.BaseActivity;
import cn.kiway.fragment.ViewPhotoFragment;
import cn.kiway.model.SelectPictureModel;

/**
 * 图片浏览分页适配器
 *
 * @author YI
 */
public class ViewPhotoPagerAdapter extends FragmentPagerAdapter {
    /**
     * 图像列表
     */
    List<SelectPictureModel> models;
    List<String> urls;
    BaseActivity activity;
    boolean bool;

    public ViewPhotoPagerAdapter(FragmentManager fm, List<String> urls,
                                 Boolean bool, BaseActivity activity) {
        super(fm);
        this.urls = urls;
        this.activity = activity;
        this.bool = bool;
    }

    @Override
    public Fragment getItem(int position) {
        if (bool) {
            return ViewPhotoFragment.newInstance(urls.get(position), bool);
        } else {
            return ViewPhotoFragment.newInstance(null, bool);
        }
    }

    @Override
    public int getCount() {
        if (bool)
            return urls.size();
        else
            return 1;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

}
