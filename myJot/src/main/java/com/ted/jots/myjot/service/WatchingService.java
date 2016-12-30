package com.ted.jots.myjot.service;

import android.app.Service;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Vibrator;
import android.text.TextUtils;
import android.widget.Toast;

import com.ted.jots.myjot.R;
import com.ted.jots.myjot.config.ConfigManager;
import com.ted.jots.myjot.data.DataManager;
import com.ted.jots.myjot.data.DataModel;
import com.ted.jots.myjot.listener.MobileShakeListener;
import com.ted.jots.myjot.main.MainPresenter;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Ted on 2015/3/4.
 * WatchingService
 */
public class WatchingService extends Service implements MobileShakeListener.OnShakeListener, ClipboardManager.OnPrimaryClipChangedListener {
    public static final String RELOAD_DATA_ACTION = "com.ted.jots.myjot.reload_data";
    public static final int CLOSE_SHAKE_MSG = 0x1;

    private MobileShakeListener mMobileShakeListener;
    private AtomicBoolean mShakeCallOver;
    private ClipboardManager mClipboardManager = null;
    private DataModel newDataModel;

    private long mLastUpdateTime = 0L;

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case CLOSE_SHAKE_MSG:
                    stopListenShake();
                    break;
            }
            return false;
        }
    });


    @Override
    public void onCreate() {
        super.onCreate();
        mShakeCallOver = new AtomicBoolean(true);
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mClipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        mClipboardManager.addPrimaryClipChangedListener(this);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (null != mMobileShakeListener)
            mMobileShakeListener.stop();
        if (null != mClipboardManager)
            mClipboardManager.removePrimaryClipChangedListener(this);
        super.onDestroy();
    }

    @Override
    public void onPrimaryClipChanged() {
        if (ConfigManager.getSupportListenClipboard(this)) {
            makeClipMemo();
            startListenShake();
        }
    }

    @Override
    public void onShake() {
        if (mShakeCallOver.compareAndSet(true, false)) {
            stopListenShake();
            doVibrate();
            onSaveClipMemo();
            mShakeCallOver.set(true);
        }
    }


    private void makeClipMemo() {
        /*上一个便签插入是否结束*/
//        boolean insertFinish = true;
//        if (!insertFinish) {
//            //LogUtil.e("上次插入事务尚未结束");
//            return;
//        }
        long nowTime = System.currentTimeMillis();
        if (nowTime - this.mLastUpdateTime < 1000L) {
            //LogUtil.e("与上次间隔时间太短");
            return;
        }
        newDataModel = new DataModel();
        ClipDescription clipDescription;
        ClipData clipData;
        StringBuilder stringBuilder;
        clipDescription = this.mClipboardManager.getPrimaryClipDescription();
        if ((!clipDescription.hasMimeType("text/plain")) && (!clipDescription.hasMimeType("text/html"))) {
            //LogUtil.e("剪切板里面的不是文本内容");
            return;
        }
        this.mLastUpdateTime = nowTime;
        clipData = this.mClipboardManager.getPrimaryClip();
        stringBuilder = new StringBuilder();
        int count = clipData.getItemCount();
        for (int i = 0; i < count; i++) {
            ClipData.Item item = clipData.getItemAt(i);
            if (null != item) {
                stringBuilder.append(item.getText());
            }
        }
        newDataModel.setContent(stringBuilder.toString());
    }

    private void onSaveClipMemo() {
        if (null == newDataModel || TextUtils.isEmpty(newDataModel.getContent())) return;
        DataModel preDataModel = DataManager.readData(this);
        StringBuilder saveBuilder = new StringBuilder();
        String preContent = preDataModel.getContent();
        saveBuilder.append(preContent).append(TextUtils.isEmpty(preContent) ? "" : "\n").append(newDataModel.getContent());
        newDataModel.setContent(saveBuilder.toString());
        DataManager.writeData(this, newDataModel);
        Intent intent = new Intent(RELOAD_DATA_ACTION);
        Context context = getApplicationContext();
        Toast.makeText(context, context.getResources().getString(R.string.have_add_clip), Toast.LENGTH_LONG).show();
        sendBroadcast(intent);
        MainPresenter.updateMyWidget(this, newDataModel.getContent());
    }

    private void doVibrate() {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {100, 400};
        vibrator.vibrate(pattern, -1);
    }

    private void startListenShake() {
        if (null == mMobileShakeListener) {
            mMobileShakeListener = new MobileShakeListener(this);
            mMobileShakeListener.setOnShakeListener(this);
        }
        mMobileShakeListener.start();
        mHandler.sendEmptyMessageDelayed(CLOSE_SHAKE_MSG, ConfigManager.getConfigListenClipboardTime(this));
    }

    private void stopListenShake() {
        if (null != mMobileShakeListener) {
            if (mShakeCallOver.compareAndSet(true, false)) {
                mMobileShakeListener.stop();
                mShakeCallOver.set(true);
            }
        }

    }

}

