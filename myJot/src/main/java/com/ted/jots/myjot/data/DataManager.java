package com.ted.jots.myjot.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import com.ted.jots.myjot.R;
import com.ted.jots.myjot.config.ConfigManager;

import java.util.ArrayList;

/**
 * Created by Ted on 2015/2/14.
 */
public class DataManager {

    public static final String KEY_FOR_SP_CONTENT = "com_ted_my_jots_content";

    private SharedPreferences mSharedPreferences;
    private Context mContext;

    public DataManager(Context context){
        mContext = context;
        mSharedPreferences = context.getSharedPreferences(ConfigManager.NAME_FOR_SHARE_PRE,Context.MODE_PRIVATE);
    }

    public DataModel readData(){
        DataModel model = new DataModel();
        String data = mSharedPreferences.getString(KEY_FOR_SP_CONTENT,"");
        model.setContent(data);
        return model;
    }


    public void writeData(DataModel model){
        if(null == model)return;
        String date = model.getContent();
        if(TextUtils.isEmpty(date))date = "";
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(KEY_FOR_SP_CONTENT,date);
        editor.commit();
    }

}
