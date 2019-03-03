package com.laimaiyao.pager;

import android.support.v4.view.ViewPager;
import android.view.View;
/**
 * 要实现动画效果的核心就在 setPageTransformer(true, new VerticalPageTransformer());
 * ，大家都知道这个是设置Viewpager 设置动画的方法 ，
 * 那么要实现这个如果用系统自带的切换动画效果的话 肯定是不符合Viewpager 竖屏滑动的效果的。
 * 所以自定义PageTransformer 就是必然的趋势了
 */

public class VerticalPageTransformer implements ViewPager.PageTransformer{

    private  float MIN_SCALE = 0.5f;

    @Override
    public void transformPage(View view, float position) {
        if (position < -1) { // [-Infinity,-1)
            // This page is way off-screen to the left.
            view.setAlpha(0);

        }else if (position <= 0) { // [-1,1]
            view.setAlpha(1 + position);
            view.setTranslationX(-view.getWidth() * position);
            view.setPivotY( view.getMeasuredHeight());
            view.setPivotX( view.getMeasuredWidth()/2);
            view.setTranslationY( position * view.getHeight());
            float scaleFactor = MIN_SCALE + (1 - MIN_SCALE)
                    * (1 - Math.abs(position));
            view.setScaleX(scaleFactor);
            view.setScaleY(scaleFactor);

        }
        else if (position <= 1) { // [-1,1]
            view.setAlpha(1);
            view.setTranslationX(-view.getWidth() * position);
            view.setTranslationY( position * view.getHeight());
            float scaleFactor = MIN_SCALE + (1 - MIN_SCALE)
                    * (1 - Math.abs(position));

        } else { // (1,+Infinity]
            view.setAlpha(0);
            view.setTranslationY( position * view.getHeight());
        }
    }

}

