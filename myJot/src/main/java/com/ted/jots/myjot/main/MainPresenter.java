package com.ted.jots.myjot.main;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.Toast;

import com.ted.jots.myjot.R;
import com.ted.jots.myjot.widget.WidgetForFourXFour;
import com.ted.jots.myjot.widget.WidgetForFourXOne;
import com.ted.jots.myjot.widget.WidgetForFourXThree;
import com.ted.jots.myjot.widget.WidgetForFourXTwo;
import com.ted.jots.myjot.widget.WidgetForOneXOne;

/**
 * Created by ted on 2016/12/23.
 * in com.ted.jots.myjot.main
 */

public class MainPresenter {

    public static void onShare(Context context, String content) {
        if (TextUtils.isEmpty(content)) return;
        Intent intent = new Intent();
        intent.setAction("android.intent.action.SEND");
        intent.putExtra("android.intent.extra.TEXT", content);
        intent.setType("text/plain");
        context.startActivity(intent);
    }

    public static void onShareApp(Context context) {
        String content = context.getString(R.string.share_app_description);
        Intent intent = new Intent();
        intent.setAction("android.intent.action.SEND");
        intent.putExtra("android.intent.extra.TEXT", content);
        intent.setType("text/plain");
        context.startActivity(intent);
    }

    public static void onTanksDeveloper(Activity activity) {
        Uri uri = Uri.parse("market://details?id=" + activity.getPackageName());
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        try {
            activity.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(activity, R.string.no_market_tips, Toast.LENGTH_SHORT).show();
        }
    }

    public static void updateMyWidget(Context context, String content) {
        String updateAction = "android.appwidget.action.APPWIDGET_SET_DATA";
        Intent intent1 = new Intent(context, WidgetForOneXOne.class);
        intent1.setAction(updateAction);
        intent1.putExtra("TEXT_STRING", content);
        context.sendBroadcast(intent1);
        Intent intent2 = new Intent(context, WidgetForFourXTwo.class);
        intent2.putExtra("TEXT_STRING", content);
        intent2.setAction(updateAction);
        context.sendBroadcast(intent2);
        Intent intent3 = new Intent(context, WidgetForFourXThree.class);
        intent3.putExtra("TEXT_STRING", content);
        intent3.setAction(updateAction);
        context.sendBroadcast(intent3);
        Intent intent4 = new Intent(context, WidgetForFourXFour.class);
        intent4.putExtra("TEXT_STRING", content);
        intent4.setAction(updateAction);
        context.sendBroadcast(intent4);
        Intent intent5 = new Intent(context, WidgetForFourXOne.class);
        intent5.putExtra("TEXT_STRING", content);
        intent5.setAction(updateAction);
        context.sendBroadcast(intent5);
    }
}
