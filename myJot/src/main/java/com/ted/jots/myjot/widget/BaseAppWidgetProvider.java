package com.ted.jots.myjot.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import com.ted.jots.myjot.R;
import com.ted.jots.myjot.config.ConfigManager;
import com.ted.jots.myjot.data.DataManager;
import com.ted.jots.myjot.main.MainActivity;

/**
 * Created by ted on 2016/12/30.
 * in com.ted.jots.myjot.widget
 */

public abstract class BaseAppWidgetProvider extends AppWidgetProvider {
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (!"android.appwidget.action.APPWIDGET_SET_DATA".equals(intent.getAction())) return;
        if (null == getWidgetComponentName(context) || null == getWidgetRemoteViews(context))
            return;
        int[] widgetIds = AppWidgetManager.getInstance(context).getAppWidgetIds(getWidgetComponentName(context));
        for (int widgetId : widgetIds) {
            try {
                String str = intent.getExtras().getString("TEXT_STRING");
                RemoteViews remoteViews = getWidgetRemoteViews(context);
                remoteViews.setTextViewText(R.id.widget_update, str);
                remoteViews.setInt(getClickViewId(), "setBackgroundColor", ConfigManager.getConfigWidgetBgColor(context));
                remoteViews.setTextColor(R.id.widget_update, ConfigManager.getConfigAppTxtColor(context));
                remoteViews.setOnClickPendingIntent(getClickViewId(),
                        PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT));
                AppWidgetManager.getInstance(context).updateAppWidget(widgetId, remoteViews);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("ted", "update widget error @" + widgetId);
            }
        }
    }

    public abstract ComponentName getWidgetComponentName(Context context);

    public abstract RemoteViews getWidgetRemoteViews(Context context);

    public abstract int getClickViewId();

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        int[] widgetIds = appWidgetManager.getAppWidgetIds(getWidgetComponentName(context));
        for (int widgetId : widgetIds) {
            try {
                RemoteViews remoteViews = getWidgetRemoteViews(context);
                remoteViews.setTextViewText(R.id.widget_update, DataManager.readData(context).getContent());
                remoteViews.setInt(getClickViewId(), "setBackgroundColor", ConfigManager.getConfigWidgetBgColor(context));
                remoteViews.setTextColor(R.id.widget_update, ConfigManager.getConfigAppTxtColor(context));
                remoteViews.setOnClickPendingIntent(getClickViewId(),
                        PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT));
                AppWidgetManager.getInstance(context).updateAppWidget(widgetId, remoteViews);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("ted", "update widget error @" + widgetId);
            }

        }
    }
}
