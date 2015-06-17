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
                return;
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
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(subject).append(body);
                newDataModel.setContent(stringBuilder.toString());
                onSaveShareMemo(newDataModel);
            }else finish();
        }
    }

    private void onSaveShareMemo(DataModel newDataModel){
        if(null == newDataModel || TextUtils.isEmpty(newDataModel.getContent()))return;
        DataManager dataManager = new DataManager(getApplicationContext());
        DataModel preDataModel = dataManager.readData();
        StringBuilder saveBuilder = new StringBuilder();
        String preContent = preDataModel.getContent();
        saveBuilder.append(preContent).append(TextUtils.isEmpty(preContent)?"":"\n").append(newDataModel.getContent());
        newDataModel.setContent(saveBuilder.toString());
        dataManager.writeData(newDataModel);
        Context context = getApplicationContext();
        Toast.makeText(context, context.getResources().getString(R.string.have_add_share), Toast.LENGTH_LONG).show();
        Intent intent = new Intent(WatchingService.RELOAD_DATA_ACTION);
        sendBroadcast(intent);
        updateMyWidget(newDataModel);
        finish();
    }

    public void updateMyWidget(DataModel newDataModel) {
        String updateAction = "android.appwidget.action.APPWIDGET_SET_DATA";
        Intent intent1 = new Intent(this, WidgetForOneXOne.class);
        intent1.setAction(updateAction);
        intent1.putExtra("TEXT_STRING", newDataModel.getContent());
        sendBroadcast(intent1);
        Intent intent2 = new Intent(this, WidgetForFourXTwo.class);
        intent2.putExtra("TEXT_STRING", newDataModel.getContent());
        intent2.setAction(updateAction);
        sendBroadcast(intent2);
        Intent intent3 = new Intent(this, WidgetForFourXThree.class);
        intent3.putExtra("TEXT_STRING", newDataModel.getContent());
        intent3.setAction(updateAction);
        sendBroadcast(intent3);
        Intent intent4 = new Intent(this, WidgetForFourXFour.class);
        intent4.putExtra("TEXT_STRING", newDataModel.getContent());
        intent4.setAction(updateAction);
        sendBroadcast(intent4);
        Intent intent5 = new Intent(this, WidgetForFourXOne.class);
        intent5.putExtra("TEXT_STRING", newDataModel.getContent());
        intent5.setAction(updateAction);
        sendBroadcast(intent5);
    }
}
