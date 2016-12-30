package com.ted.jots.myjot.service;

import android.app.Service;
import android.content.*;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Vibrator;
import android.text.TextUtils;
import android.widget.Toast;
import com.ted.jots.myjot.listener.ShakeListener;
import com.ted.jots.myjot.R;
import com.ted.jots.myjot.data.Constants;
import com.ted.jots.myjot.data.DataManager;
import com.ted.jots.myjot.data.DataModel;
import com.ted.jots.myjot.main.*;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Ted on 2015/3/4.
 * WatchingService
 */
public class WatchingService extends Service implements ShakeListener.OnShakeListener,ClipboardManager.OnPrimaryClipChangedListener{
    public static final String RELOAD_DATA_ACTION = "com.ted.jots.myjot.reload_data";

    private ShakeListener mShakeListener;
    private AtomicBoolean mShakeCallOver;
    private Vibrator mShakeVibrator;
    private ClipboardManager mClipboardManager = null;
    private DataModel newDataModel;

    /**上一个便签插入是否结束*/
    private boolean mInsertFinish = true;

    private long mLastUpdateTime = 0l;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case Constants.CLOSE_SHAKE_MSG:
                    stopListenShake();
                    break;
            }
        }
    };


    @Override
    public void onCreate() {
        super.onCreate();
        mShakeListener = new ShakeListener(this);
        mShakeListener.setOnShakeListener(this);
        mShakeCallOver = new AtomicBoolean(true);
        mShakeVibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mClipboardManager = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
        mClipboardManager.addPrimaryClipChangedListener(this);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        mShakeListener.stop();
        mClipboardManager.removePrimaryClipChangedListener(this);
        super.onDestroy();
    }

    @Override
    public void onPrimaryClipChanged() {
        makeClipMemo();
        startListenShake();
    }

    @Override
    public void onShake() {
        if(mShakeCallOver.compareAndSet(true,false)){
            stopListenShake();
            doVibrate();
            onSaveClipMemo();
            mShakeCallOver.set(true);
        }
    }


    private void makeClipMemo(){
        if(!mInsertFinish){
            //LogUtil.e("上次插入事务尚未结束");
            return;
        }
        long nowTime = System.currentTimeMillis();
        if (nowTime - this.mLastUpdateTime < 700L)
        {
            //LogUtil.e("与上次间隔时间太短");
            return;
        }
        newDataModel = new DataModel();
        ClipDescription clipDescription;
        ClipData clipData;
        StringBuilder stringBuilder;
        clipDescription = this.mClipboardManager.getPrimaryClipDescription();
        if ((!clipDescription.hasMimeType("text/plain")) && (!clipDescription.hasMimeType("text/html")))
        {
            //LogUtil.e("剪切板里面的不是文本内容");
            return;
        }
        this.mLastUpdateTime = nowTime;
        clipData = this.mClipboardManager.getPrimaryClip();
        stringBuilder = new StringBuilder();
        int count = clipData.getItemCount();
        for(int i=0;i < count;i++){
            ClipData.Item item = clipData.getItemAt(i);
            if(null != item){
                stringBuilder.append(item.getText());
            }
        }
        newDataModel.setContent(stringBuilder.toString());
    }

    private void onSaveClipMemo(){
        if(null == newDataModel || TextUtils.isEmpty(newDataModel.getContent()))return;
        DataModel preDataModel = DataManager.readData(this);
        StringBuilder saveBuilder = new StringBuilder();
        String preContent = preDataModel.getContent();
        saveBuilder.append(preContent).append(TextUtils.isEmpty(preContent)?"":"\n").append(newDataModel.getContent());
        newDataModel.setContent(saveBuilder.toString());
        DataManager.writeData(this,newDataModel);
        Intent intent = new Intent(RELOAD_DATA_ACTION);
        Context context = getApplicationContext();
        Toast.makeText(context,context.getResources().getString(R.string.have_add_clip),Toast.LENGTH_LONG).show();
        sendBroadcast(intent);
        MainPresenter.updateMyWidget(this,newDataModel.getContent());
    }

    private void doVibrate(){
        if(null == mShakeVibrator){
            mShakeVibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
        }
        long [] pattern = {100,400};
        mShakeVibrator.vibrate(pattern, -1);
    }

    private void startListenShake(){
        if(null == mShakeListener){
            mShakeListener = new ShakeListener(this);
            mShakeListener.setOnShakeListener(this);
        }
        mShakeListener.start();
        mHandler.sendEmptyMessageDelayed(Constants.CLOSE_SHAKE_MSG,Constants.LISTEN_SHAKE_TIME);
    }

    private void stopListenShake(){
        if(null != mShakeListener){
            if(mShakeCallOver.compareAndSet(true,false)){
                mShakeListener.stop();
                mShakeCallOver.set(true);
            }
        }

    }

}

