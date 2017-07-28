package cn.kiway.yjhz.utils.views.viewPager;

import android.animation.TimeInterpolator;
import android.support.v4.view.ViewPager;
import android.view.View;

import static java.lang.Math.pow;

/**
 * @author wupanjie
 */

public class StereoPagerTransformer implements ViewPager.PageTransformer {
    private static final float MAX_ROTATE_Y = 90;

    private static final TimeInterpolator sInterpolator = new TimeInterpolator() {
        @Override
        public float getInterpolation(float input) {
            if (input < 0.7) {
                return input * (float) pow(0.7, 3) * MAX_ROTATE_Y;
            } else {
                return (float) pow(input, 4) * MAX_ROTATE_Y;
            }
        }
    };

    private final float pageWidth;

    public StereoPagerTransformer(float pageWidth) {
        this.pageWidth = pageWidth;
    }

    public void transformPage(View view, float position) {
        raised3D(view,position);
    }

    /**
     * 动画效果2  凸起的3D效果
     */
    public void raised3D(View view,float position){
        if(position>=-1&&position<=1){
            view.setPivotX(position<0?view.getWidth():0);//设置要旋转的Y轴的位置
            view.setRotationY(90*position);//开始设置属性动画值
        }
    }
    public void raise3DX(View view,float position){
        view.setPivotY(view.getHeight() / 3);
        if (position < -1) { // [-Infinity,-1)
            // This page is way off-screen to the left.
            view.setPivotX(0);
            view.setRotationY(90);
        } else if (position <= 0) { // [-1,0]
            view.setPivotX(pageWidth);
            view.setRotationY(-sInterpolator.getInterpolation(-position));
        } else if (position <= 1) { // (0,1]
            view.setPivotX(0);
            view.setRotationY(sInterpolator.getInterpolation(position));
        } else { // (1,+Infinity]
            // This page is way off-screen to the right.
            view.setPivotX(0);
            view.setRotationY(90);
        }
    }
}
