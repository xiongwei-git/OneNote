package com.ted.jots.myjot.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import com.github.clans.fab.FloatingActionMenu;
import com.ted.jots.myjot.R;
import com.ted.jots.myjot.config.ConfigManager;
import com.ted.jots.myjot.data.DataManager;
import com.ted.jots.myjot.data.DataModel;
import com.ted.jots.myjot.service.WatchingService;
import com.ted.jots.myjot.setting.SettingActivity;
import com.ted.jots.myjot.utils.CheckDoubleClick;
import com.ted.jots.myjot.utils.SystemUtil;

import static com.ted.jots.myjot.config.ConfigManager.DEFAULT_HINT_COLOR;
import static com.ted.jots.myjot.config.ConfigManager.DEFAULT_TEXT_COLOR;


public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_TO_SETTING_PAGE = 0x12;

    private EditText mInputEditText;
    private DataModel mOldData;
    private NestedScrollView mMainLayout;
    private FloatingActionMenu mMenuBtn;
    private MainMenuHelper mMainMainMenuHelper;

    private int mMenuItemType = -1;
    private Handler mUiHandler = new Handler();
    /**
     * 针对监听服务的广播接收器
     */
    private ServiceBroadcastReceiver mBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initBase();
        initViews();
        readData();
        initService();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MainPresenter.updateMyWidget(this, getInputContent());
    }

    @Override
    protected void onPause() {
        writeData();
        MainPresenter.updateMyWidget(this, getInputContent());
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mBroadcastReceiver);
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_TO_SETTING_PAGE) {
            onConfigColor();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getRepeatCount() > 0) return true;
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mMenuBtn.isOpened()) {
                mMenuBtn.toggle(true);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private MainMenuHelper.MainMenuClickListener mMenuItemClickListener = new MainMenuHelper.MainMenuClickListener() {
        @Override
        public void onMenuClick(int type) {
            mMenuItemType = type;
            if (mMenuBtn.isOpened()) mMenuBtn.toggle(true);
        }
    };


    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (CheckDoubleClick.isFastDoubleClick()) return;
            switch (view.getId()) {
                case R.id.root_layout:
                    SystemUtil.HideSoftInput(MainActivity.this);
                    finish();
                    break;
                default:
                    break;
            }
        }
    };

    private View.OnClickListener mMenuClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            onClickMenuBtn();
        }
    };

    private View.OnLongClickListener mMenuLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View view) {
            if (mMenuBtn.isOpened()) return true;
            mMenuBtn.toggle(true);
            return true;
        }
    };

    private FloatingActionMenu.OnMenuToggleListener mMenuToggleListener = new FloatingActionMenu.OnMenuToggleListener() {
        @Override
        public void onMenuToggle(boolean opened) {
            Drawable drawable = MainActivity.this.getResources().getDrawable(opened ? R.drawable.close : R.drawable.pin);
            mMenuBtn.getMenuIconView().setImageDrawable(drawable);
            //点击菜单条目时要等菜单关闭之后再执行行为
            if (mMenuItemType < 0 || opened) return;
            if (mMenuItemType == MainMenuHelper.MENU_TYPE_SHARE) {
                onShareData();
            } else if (mMenuItemType == MainMenuHelper.MENU_TYPE_SETTING) {
                startActivityForResult(new Intent(MainActivity.this, SettingActivity.class), REQUEST_TO_SETTING_PAGE);
            } else if (mMenuItemType == MainMenuHelper.MENU_TYPE_CLEAR) {
                clearData();
            }
            mMenuItemType = -1;
        }
    };

    private void onShareData() {
        String content = mInputEditText.getText().toString();
        if (TextUtils.isEmpty(content)) {
            showNoDataSnackBar();
            return;
        }
        MainPresenter.onShare(this, content);
    }


    private View.OnClickListener snackOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (null == mOldData) return;
            mInputEditText.setText(mOldData.getContent());
            mInputEditText.setSelection(mOldData.getContent().length());
            MainPresenter.updateMyWidget(MainActivity.this, getInputContent());
        }
    };

    private void initBase() {
        mOldData = new DataModel();
        mBroadcastReceiver = new ServiceBroadcastReceiver();
        mMainMainMenuHelper = new MainMenuHelper(mMenuItemClickListener);
    }

    private void initViews() {
        View mRootLayout = findViewById(R.id.root_layout);
        mMainLayout = (NestedScrollView) findViewById(R.id.main_bg_layout);
        mInputEditText = (EditText) findViewById(R.id.jot_input_edit_txt);
        mRootLayout.setOnClickListener(onClickListener);
        mMenuBtn = (FloatingActionMenu) findViewById(R.id.main_menu_btn);
        mMenuBtn.setClosedOnTouchOutside(true);
        mMenuBtn.hideMenuButton(false);
        mMenuBtn.setOnMenuButtonClickListener(mMenuClickListener);
        mMenuBtn.setOnMenuButtonLongClickListener(mMenuLongClickListener);
        mMenuBtn.setOnMenuToggleListener(mMenuToggleListener);
        mMainMainMenuHelper.initBasicMenu(mMenuBtn);
        mUiHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mMenuBtn.showMenuButton(true);
            }
        }, 600);
        onConfigColor();
    }

    private void onClickMenuBtn() {
        if (mMenuBtn.isOpened()) mMenuBtn.toggle(true);
        else {
            if (TextUtils.isEmpty(getInputContent())) {
                showNoDataSnackBar();
                return;
            }
            SystemUtil.HideSoftInput(MainActivity.this);
            finish();
        }
    }

    private void initService() {
        startService(new Intent(this, WatchingService.class));
        IntentFilter filter = new IntentFilter(WatchingService.RELOAD_DATA_ACTION);
        registerReceiver(mBroadcastReceiver, filter);
    }

    private void onConfigColor() {
        int bgColor = ConfigManager.getConfigAppBgColor(this);
        mMainLayout.setBackgroundColor(bgColor);
        mInputEditText.setHintTextColor(bgColor == DEFAULT_HINT_COLOR ?
                DEFAULT_TEXT_COLOR : DEFAULT_HINT_COLOR);
        mInputEditText.setTextColor(ConfigManager.getConfigAppTxtColor(this));
    }

    private void clearData() {
        if (TextUtils.isEmpty(getInputContent())) {
            showNoDataSnackBar();
            return;
        }
        String oldData = mInputEditText.getText().toString();
        mOldData.setContent(oldData);
        mInputEditText.setText("");
        writeData();
        MainPresenter.updateMyWidget(this, getInputContent());
        Snackbar.make(mMainLayout, R.string.you_just_clear_all_data, Snackbar.LENGTH_SHORT)
                .setAction(R.string.not_clear, snackOnClickListener).show();
    }

    private void readData() {
        DataModel dataModel = DataManager.readData(this);
        String content = dataModel.getContent();
        if (!TextUtils.isEmpty(content)) {
            mInputEditText.setText(content);
            mInputEditText.setSelection(content.length());
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        } else {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_UNSPECIFIED);
            mInputEditText.setText("");
            mInputEditText.setFocusable(true);
            mInputEditText.setFocusableInTouchMode(true);
            mInputEditText.requestFocus();
        }
    }

    private void writeData() {
        String content = mInputEditText.getText().toString();
        DataModel dataModel = new DataModel();
        dataModel.setContent(content);
        DataManager.writeData(this, dataModel);
    }


    private void showNoDataSnackBar() {
        Snackbar.make(mMainLayout, R.string.no_data_tips, Snackbar.LENGTH_SHORT).show();
    }


    private String getInputContent() {
        return mInputEditText.getText().toString();
    }

    private class ServiceBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            readData();
        }
    }
}
