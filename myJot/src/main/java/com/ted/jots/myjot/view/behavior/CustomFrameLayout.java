package com.ted.jots.myjot.view.behavior;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * Created by ted on 2016/12/23.
 * in com.ted.jots.myjot.view.behavior
 */

@CoordinatorLayout.DefaultBehavior(MoveUpwardBehavior.class)
public class CustomFrameLayout extends FrameLayout {
    public CustomFrameLayout(Context context) {
        super(context);
    }

    public CustomFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
