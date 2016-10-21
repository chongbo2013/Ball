package com.moxiu.ball;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by x002 on 2016/10/21.
 */

public class TempViewPage extends ViewPager {
    private boolean canScroll=true;
    public TempViewPage(Context context) {
        super(context);
    }

    public TempViewPage(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if(!canScroll){
            return false;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if(!canScroll){
            return false;
        }
        return super.onTouchEvent(ev);
    }
}
