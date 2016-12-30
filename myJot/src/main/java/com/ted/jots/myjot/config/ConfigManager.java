package com.ted.jots.myjot.config;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;
import android.text.TextUtils;

import com.ted.jots.myjot.R;

/**
 * Created by Ted on 2015/2/15.
 * ConfigManager
 */
public class ConfigManager {
    public static final int DEFAULT_ARGB_COLOR = 0xcc1a1a1a;
    public static final int DEFAULT_TEXT_COLOR = 0xffffffff;
    public static final int DEFAULT_HINT_COLOR = 0xff3ba3f8;
    private static final String KEY_FOR_SP_FIRST_START = "com_ted_my_jots_first_start";


    public static SharedPreferences getSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
        //return context.getSharedPreferences(ConfigManager.NAME_FOR_SHARE_PRE, Context.MODE_PRIVATE);
    }

    public static boolean isFirstStart(Context context) {
        boolean result = getSharedPreferences(context).getBoolean(KEY_FOR_SP_FIRST_START, false);
        if (result) {
            SharedPreferences.Editor editor = getSharedPreferences(context).edit();
            editor.putBoolean(KEY_FOR_SP_FIRST_START, false);
            editor.apply();
        }
        return result;
    }


    public static int getConfigAppBgColor(Context context) {
        int defaultColor = DEFAULT_ARGB_COLOR;
        int configColor = getSharedPreferences(context).getInt(context.getString(R.string.edit_page_color_key), -1);
        if (configColor == -1) configColor = defaultColor;
        return configColor;
    }

    public static int getConfigAppTxtColor(Context context) {
        int defaultColor = DEFAULT_TEXT_COLOR;
        int configColor = getSharedPreferences(context).getInt(context.getString(R.string.font_color_key), -1);
        if (configColor == -1) configColor = defaultColor;
        return configColor;
    }

//    public static void setConfigAppBgColor(Context context, int argbColor) {
//        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
//        editor.putInt(context.getString(R.string.edit_page_color_key), argbColor);
//        editor.apply();
//    }

    public static int getConfigWidgetBgColor(Context context) {
        return getConfigAppBgColor(context);
    }

    public static boolean getSupportListenClipboard(Context context) {
        return getSharedPreferences(context).getBoolean(context.getString(R.string.is_support_clipboard_key), true);
    }

    public static long getConfigListenClipboardTime(Context context) {
        String value = getSharedPreferences(context).getString(context.getString(R.string.listen_clipboard_time_key), "1");
        if (TextUtils.isEmpty(value)) value = "1";
        int second = 15;
        switch (value) {
            case "0":
                second = 5;
                break;
            case "1":
                second = 15;
                break;
            case "2":
                second = 30;
                break;
            case "3":
                second = 120;
                break;
            default:
                break;
        }
        return 1000L * second;
    }

}
