package com.ted.jots.myjot.main;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;
import com.ted.jots.myjot.R;
import com.ted.jots.myjot.data.DataManager;
import com.ted.jots.myjot.data.DataModel;
import com.ted.jots.myjot.service.WatchingService;

/**
 * Created by Ted on 2015/3/5.
 * ReceiveDataActivity
 */
public class ReceiveDataActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        if ("android.intent.action.SEND".equals(intent.getAction())){
            onReceiveData(intent);
        }
        super.onCreate(savedInstanceState);
    }

    private void onReceiveData(Intent intent){
        if(Build.VERSION.SDK_INT > 15){
            ClipData clipData = intent.getClipData();
            ClipDescription clipDescription;
            clipDescription = clipData.getDescription();
            if ((!clipDescription.hasMimeType("text/plain")) && (!clipDescription.hasMimeType("text/html"))){
                /**剪切板里面的不是文本内容*/
                finish();
            }else {
                DataModel newDataModel = new DataModel();
                StringBuilder stringBuilder = new StringBuilder();
                int count = clipData.getItemCount();
                for(int i=0;i < count;i++){
                    ClipData.Item item = clipData.getItemAt(i);
                    if(null != item){
                        stringBuilder.append(item.getText());
                    }
                }
                newDataModel.setContent(stringBuilder.toString());
                onSaveShareMemo(newDataModel);
            }
        }else {
            String type = intent.getType();
            if(TextUtils.isEmpty(type))return;
            if(type.contains("text/plain")){
                String subject = intent.getStringExtra(android.content.Intent.EXTRA_SUBJECT);
                String body = intent.getStringExtra(android.content.Intent.EXTRA_TEXT);
                DataModel newDataModel = new DataModel();
                newDataModel.setContent(subject + body);
                onSaveShareMemo(newDataModel);
            }else finish();
        }
    }

    private void onSaveShareMemo(DataModel newDataModel){
        if(null == newDataModel || TextUtils.isEmpty(newDataModel.getContent()))return;
        DataModel preDataModel = DataManager.readData(this);
        StringBuilder saveBuilder = new StringBuilder();
        String preContent = preDataModel.getContent();
        saveBuilder.append(preContent).append(TextUtils.isEmpty(preContent)?"":"\n").append(newDataModel.getContent());
        newDataModel.setContent(saveBuilder.toString());
        DataManager.writeData(this,newDataModel);
        Context context = getApplicationContext();
        Toast.makeText(context, context.getResources().getString(R.string.have_add_share), Toast.LENGTH_LONG).show();
        Intent intent = new Intent(WatchingService.RELOAD_DATA_ACTION);
        sendBroadcast(intent);
        updateMyWidget(newDataModel);
        finish();
    }

    public void updateMyWidget(DataModel newDataModel) {
        MainPresenter.updateMyWidget(this,newDataModel.getContent());
    }
}
