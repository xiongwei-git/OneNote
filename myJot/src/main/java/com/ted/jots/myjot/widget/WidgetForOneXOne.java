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
 * Created by Ted on 2015/2/14.
 */
public class WidgetForOneXOne extends AppWidgetProvider{

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        int[] widgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, WidgetForOneXOne.class));
        for (int widgetId : widgetIds) {
            try {
                RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widgetlayout_onexone);
                remoteViews.setTextViewText(R.id.widget_update, DataManager.readData(context).getContent());
                remoteViews.setTextColor(R.id.widget_update, ConfigManager.getConfigAppTxtColor(context));
                remoteViews.setInt(R.id.widget_layout, "setBackgroundColor", ConfigManager.getConfigWidgetBgColor(context));
                remoteViews.setOnClickPendingIntent(R.id.widget_layout, PendingIntent.getActivity(context, 0,
                        new Intent(context, MainActivity.class), PendingIntent.FLAG_CANCEL_CURRENT));
                ComponentName componentName = new ComponentName(context, WidgetForOneXOne.class);
                AppWidgetManager.getInstance(context).updateAppWidget(componentName, remoteViews);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("ted","update widget error @" + widgetId);
            }

        }
    }


    @Override
    public void onReceive(Context context, Intent intent)
    {
        super.onReceive(context, intent);
        if (null == intent.getAction()
                || !intent.getAction().equalsIgnoreCase("android.appwidget.action.APPWIDGET_SET_DATA"))return;
        ComponentName componentName = new ComponentName(context, WidgetForOneXOne.class);
        int[] widgetIds = AppWidgetManager.getInstance(context).getAppWidgetIds(componentName);
        int num = widgetIds.length;
        for (int i = 0; i < num; i++) {
            try {
                String str = intent.getExtras().getString("TEXT_STRING");
                RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widgetlayout_onexone);
                remoteViews.setTextViewText(R.id.widget_update, str);
                remoteViews.setInt(R.id.widget_layout,"setBackgroundColor",ConfigManager.getConfigWidgetBgColor(context));
                remoteViews.setOnClickPendingIntent(R.id.widget_layout, PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), PendingIntent.FLAG_CANCEL_CURRENT));
                AppWidgetManager.getInstance(context).updateAppWidget(componentName, remoteViews);
            }catch (Exception e){
                try {
                    RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widgetlayout_onexone);
                    remoteViews.setTextViewText(R.id.widget_update, DataManager.readData(context).getContent());
                    remoteViews.setInt(R.id.widget_layout,"setBackgroundColor",ConfigManager.getConfigWidgetBgColor(context));
                    remoteViews.setOnClickPendingIntent(R.id.widget_layout, PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), PendingIntent.FLAG_CANCEL_CURRENT));
                    AppWidgetManager.getInstance(context).updateAppWidget(componentName, remoteViews);
                } catch (Exception ex) {

                }
            }
        }
    }

}
