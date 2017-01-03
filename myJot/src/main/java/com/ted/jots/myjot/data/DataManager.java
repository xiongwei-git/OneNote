package com.ted.jots.myjot.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.ted.jots.myjot.config.ConfigManager;

/**
 * Created by Ted on 2015/2/14.
 * DataManager
 */
public class DataManager {

    private static final String KEY_FOR_SP_CONTENT = "com_ted_my_jots_content";

    public static DataModel readData(Context context) {
        DataModel model = new DataModel();
        String data = getLastVersionData(context);
        if (TextUtils.isEmpty(data))
            data = ConfigManager.getSharedPreferences(context).getString(KEY_FOR_SP_CONTENT, "");
        model.setContent(data);
        return model;
    }


    public static void writeData(Context context, DataModel model) {
        if (null == model) return;
        String date = model.getContent();
        if (TextUtils.isEmpty(date)) date = "";
        SharedPreferences.Editor editor = ConfigManager.getSharedPreferences(context).edit();
        editor.putString(KEY_FOR_SP_CONTENT, date);
        editor.apply();
    }

    private static String getLastVersionData(Context context) {
        SharedPreferences sp = context.getSharedPreferences("sp_for_com_ted_my_jots", Context.MODE_PRIVATE);
        if (sp.getBoolean("hasReadLastVersion", false)) {
            return "";
        }
        sp.edit().putBoolean("hasReadLastVersion", true).apply();
        return sp.getString(KEY_FOR_SP_CONTENT, "");
    }

}
