package com.ted.jots.myjot.widget;

import android.content.ComponentName;
import android.content.Context;
import android.widget.RemoteViews;

import com.ted.jots.myjot.R;

/**
 * Created by Ted on 2015/2/14.
 * WidgetForFourXOne
 */
public class WidgetForFourXOne extends BaseAppWidgetProvider {
    @Override
    public ComponentName getWidgetComponentName(Context context) {
        return new ComponentName(context, WidgetForFourXOne.class);
    }

    @Override
    public RemoteViews getWidgetRemoteViews(Context context) {
        return new RemoteViews(context.getPackageName(), R.layout.widgetlayout_fourxone);
    }

    @Override
    public int getClickViewId() {
        return R.id.widget_layout_4_1;
    }
}
