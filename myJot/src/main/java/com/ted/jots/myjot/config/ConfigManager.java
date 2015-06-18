package com.ted.jots.myjot.config;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;

import com.ted.jots.myjot.R;
import com.ted.jots.myjot.data.Constants;

/**
 * Created by Ted on 2015/2/15.
 */
public class ConfigManager {
    public static final int DEFAULT_ARGB_COLOR = 0xcc1a1a1a;
    public static final String NAME_FOR_SHARE_PRE = "sp_for_com_ted_my_jots";
    public static final String KEY_FOR_SP_FIRST_START = "com_ted_my_jots_first_start";
    public static final String KEY_FOR_SP_CONFIG_COLOR = "com_ted_my_jots_config_color";
    public static final String KEY_FOR_SP_CONFIG_MENU = "com_ted_my_jots_config_menu";
    public static final String KEY_FOR_SP_CONFIG_USE_APP_MENU = "key_for_sp_config_use_app_menu";
    private Context context;

    private SharedPreferences mSharedPreferences;

    public ConfigManager(Context context){
        this.context = context;
        mSharedPreferences = context.getSharedPreferences(ConfigManager.NAME_FOR_SHARE_PRE,Context.MODE_PRIVATE);
    }

    public boolean isFirstStart(){
        boolean result = mSharedPreferences.getBoolean(KEY_FOR_SP_FIRST_START,false);
        if(result){
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putBoolean(KEY_FOR_SP_FIRST_START,false);
            editor.commit();
        }
        return result;
    }

    public boolean isUseAppMenu(){
        boolean result = mSharedPreferences.getBoolean(KEY_FOR_SP_CONFIG_USE_APP_MENU,false);
        return result;
    }

    public void setUseAppMenu(boolean isUse){
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(KEY_FOR_SP_CONFIG_USE_APP_MENU, isUse);
        editor.commit();
    }

    public boolean isHasConfigMenu(){
        boolean result = mSharedPreferences.getBoolean(KEY_FOR_SP_CONFIG_MENU,false);
        return result;
    }

    public void setHasConfigMenu(){
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(KEY_FOR_SP_CONFIG_MENU, true);
        editor.commit();
    }

    public int getConfigAppBgColor(){
        int defaultColor = DEFAULT_ARGB_COLOR;
        int configColor = mSharedPreferences.getInt(KEY_FOR_SP_CONFIG_COLOR,-1);
        if(configColor == -1)configColor = defaultColor;
        return configColor;
    }

    public void setConfigAppBgColor(int argbColor){
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(KEY_FOR_SP_CONFIG_COLOR, argbColor);
        editor.commit();
    }

    public int getConfigWidgetBgColor(){
        int defaultColor = DEFAULT_ARGB_COLOR;
        int configColor = mSharedPreferences.getInt(KEY_FOR_SP_CONFIG_COLOR,-1);
        if(configColor == -1)configColor = defaultColor;
        return configColor;
    }

}
