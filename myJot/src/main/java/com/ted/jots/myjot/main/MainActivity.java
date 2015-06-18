package com.ted.jots.myjot.main;

import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.gc.materialdesign.views.ButtonFloat;
import com.gc.materialdesign.views.CheckBox;
import com.gc.materialdesign.views.Switch;
import com.gc.materialdesign.widgets.SnackBar;
import com.ted.jots.myjot.R;
import com.ted.jots.myjot.config.ConfigManager;
import com.ted.jots.myjot.data.DataManager;
import com.ted.jots.myjot.data.DataModel;
import com.ted.jots.myjot.service.WatchingService;
import com.ted.jots.myjot.utils.CheckDoubleClick;
import com.ted.jots.myjot.utils.SystemUtil;
import com.ted.jots.myjot.view.colorpickerview.view.ColorPanelView;
import com.ted.jots.myjot.view.colorpickerview.view.ColorPickerView;


public class MainActivity extends FragmentActivity {
    private DataManager mDataManager;
    private ConfigManager mConfigManager;
    private EditText mInputEditText;
    private ButtonFloat mJotButton;
    private ButtonFloat mMenuButton;
    private DataModel mOldData;
    private RelativeLayout mRootLayout;
    private RelativeLayout mMainLayout;
    private PopupWindow mMainMenu;
    private View mMainMenuView;
    private ColorPickerView mColorPickerView;
    private ColorPanelView mOldColorPanelView;
    private ColorPanelView mNewColorPanelView;
    private CheckBox  mMenuConfigCheckBox;
    private Switch mMenuConfigSwitchView;

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
        updateAppMenu();
        showMenuConfigDialog();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateMyWidget();
    }

    @Override
    protected void onPause() {
        writeData();
        updateMyWidget();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mBroadcastReceiver);
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getRepeatCount() > 0) return true;
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            showMenuWindow();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (CheckDoubleClick.isFastDoubleClick()) return;
            switch (view.getId()) {
                case R.id.jot_jot_btn_float:
                    SystemUtil.HideSoftInput(MainActivity.this);
                    finish();
                    break;
                case R.id.root_layout:
                    SystemUtil.HideSoftInput(MainActivity.this);
                    finish();
                    break;
                case R.id.menu_btn_float:
                    showMenuWindow();
                    break;
                case R.id.main_menu_item_1:
                    onShareData();
                    mMainMenu.dismiss();
                    break;
                case R.id.main_menu_item_2:
                    clearData();
                    mMainMenu.dismiss();
                    break;
                case R.id.main_menu_item_3:
                    showCoursesDialog();
                    mMainMenu.dismiss();
                    break;
                case R.id.main_menu_item_4:
                    showDeveloperDialog();
                    mMainMenu.dismiss();
                    break;
                case R.id.main_menu_item_set_app:
                    showColorPickerDialog();
                    mMainMenu.dismiss();
                    break;
                default:
                    break;
            }
        }
    };

    private void onShareData() {
        if (TextUtils.isEmpty(MainActivity.this.mInputEditText.getText().toString())) return;
        Intent intent = new Intent();
        intent.setAction("android.intent.action.SEND");
        intent.putExtra("android.intent.extra.TEXT", MainActivity.this.mInputEditText.getText().toString());
        intent.setType("text/plain");
        MainActivity.this.startActivity(intent);
    }


    private MaterialDialog.ButtonCallback mDeveloperCallback = new MaterialDialog.ButtonCallback() {
        @Override
        public void onPositive(MaterialDialog dialog) {
            super.onPositive(dialog);
            if (CheckDoubleClick.isFastDoubleClick()) return;
            Uri uri = Uri.parse("market://details?id=" + MainActivity.this.getPackageName());
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            try {
                MainActivity.this.startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(MainActivity.this, "你还未安装应用市场", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onNegative(MaterialDialog dialog) {
            super.onNegative(dialog);
        }
    };

    private MaterialDialog.ButtonCallback mConfigMenuCallback = new MaterialDialog.ButtonCallback() {
        @Override
        public void onPositive(MaterialDialog dialog) {
            super.onPositive(dialog);
            if(null != mMenuConfigCheckBox && mMenuConfigCheckBox.isCheck())mConfigManager.setHasConfigMenu();
            if(null != mMenuConfigSwitchView)mConfigManager.setUseAppMenu(mMenuConfigSwitchView.isCheck());
            updateAppMenu();
        }

        @Override
        public void onNegative(MaterialDialog dialog) {
            super.onNegative(dialog);
        }
    };


    private MaterialDialog.ButtonCallback mSelectColorCallback = new MaterialDialog.ButtonCallback() {
        @Override
        public void onPositive(MaterialDialog dialog) {
            super.onPositive(dialog);
            int argbColor = mColorPickerView.getColor();
            mConfigManager.setConfigAppBgColor(argbColor);
            setMainLayoutBg();
            updateMyWidget();
        }

        @Override
        public void onNegative(MaterialDialog dialog) {
            super.onNegative(dialog);
            mConfigManager.setConfigAppBgColor(-1);
            setMainLayoutBg();
            updateMyWidget();
        }
    };

    private View.OnClickListener snackOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (null == mOldData) return;
            mInputEditText.setText(mOldData.getContent());
            mInputEditText.setSelection(mOldData.getContent().length());
            updateMyWidget();
        }
    };

    private void initBase() {
        mDataManager = new DataManager(this);
        mConfigManager = new ConfigManager(this);
        mOldData = new DataModel();
        mBroadcastReceiver = new ServiceBroadcastReceiver();
        mMainMenuView = View.inflate(this, R.layout.popwindow_guide, null);
    }

    private void initViews() {
        mRootLayout = (RelativeLayout) findViewById(R.id.root_layout);
        mMainLayout = (RelativeLayout) findViewById(R.id.main_bg_layout);
        mInputEditText = (EditText) findViewById(R.id.jot_input_edit_txt);
        mJotButton = (ButtonFloat) findViewById(R.id.jot_jot_btn_float);
        mMenuButton = (ButtonFloat) findViewById(R.id.menu_btn_float);
        mJotButton.setOnClickListener(onClickListener);
        mMenuButton.setOnClickListener(onClickListener);
        mRootLayout.setOnClickListener(onClickListener);
        mMainMenuView.findViewById(R.id.main_menu_item_1).setOnClickListener(onClickListener);
        mMainMenuView.findViewById(R.id.main_menu_item_2).setOnClickListener(onClickListener);
        mMainMenuView.findViewById(R.id.main_menu_item_3).setOnClickListener(onClickListener);
        mMainMenuView.findViewById(R.id.main_menu_item_4).setOnClickListener(onClickListener);
        mMainMenuView.findViewById(R.id.main_menu_item_set_app).setOnClickListener(onClickListener);
        setMainLayoutBg();
    }

    private void initService() {
        startService(new Intent(this, WatchingService.class));
        IntentFilter filter = new IntentFilter(WatchingService.RELOAD_DATA_ACTION);
        registerReceiver(mBroadcastReceiver, filter);
    }

    private void setMainLayoutBg() {
        int bgColor = mConfigManager.getConfigAppBgColor();
        mMainLayout.setBackgroundColor(bgColor);
    }

    private void clearData() {
        String oldData = mInputEditText.getText().toString();
        mOldData.setContent(oldData);
        mInputEditText.setText("");
        writeData();
        updateMyWidget();
        showSnackBar();
    }

    private void readData() {
        DataModel dataModel = mDataManager.readData();
        String content = dataModel.getContent();
        if (!TextUtils.isEmpty(content)) {
            mInputEditText.setText(content);
            mInputEditText.setSelection(content.length());
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        } else {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
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
        mDataManager.writeData(dataModel);
    }


    private void showSnackBar() {
        SnackBar snackbar = new SnackBar(this, getResources().getString(R.string.you_just_clear_all_data),
                getResources().getString(R.string.not_clear), snackOnClickListener);
        snackbar.show();
    }


    private void showDeveloperDialog() {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(this);
        builder.title(R.string.about_developer)
                .content(R.string.developer_description)
                .positiveText(R.string.praise_developer)
                .callback(mDeveloperCallback)
                .negativeText(R.string.close)
                .show();
    }

    private void showColorPickerDialog() {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(this);
        builder.title(R.string.app_set_color_title)
                .customView(R.layout.color_picker_layout, true)
                .negativeText(R.string.app_set_color_set_default)
                .positiveText(R.string.app_set_color_set_color)
                .callback(mSelectColorCallback);
        MaterialDialog dialog = builder.build();
        initColorPickerView(dialog.getView());
        dialog.show();
    }

    private void showMenuConfigDialog() {
        if (mConfigManager.isHasConfigMenu()) return;
        MaterialDialog.Builder builder = new MaterialDialog.Builder(this);
        builder.title(R.string.app_set_menu_title)
                .customView(R.layout.user_guide_layout_layout, true)
                .callback(mConfigMenuCallback)
                .positiveText(R.string.ok);
        MaterialDialog dialog = builder.build();
        mMenuConfigCheckBox = (CheckBox) dialog.getView().findViewById(R.id.menu_set_check_box);
        mMenuConfigSwitchView = (Switch) dialog.getView().findViewById(R.id.menu_set_switch_view);
        mMenuConfigSwitchView.setChecked(mConfigManager.isUseAppMenu());
        dialog.show();
    }

    private void showCoursesDialog() {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(this);
        Resources res = this.getResources();
        String courseContext = res.getString(R.string.courses_1) + res.getString(R.string.courses_2)
                + res.getString(R.string.courses_3);
        builder.title(R.string.courses_title)
                .content(courseContext)
                .positiveText(R.string.praise_developer)
                .callback(mDeveloperCallback)
                .negativeText(R.string.close)
                .show();
    }

    private void showMenuWindow() {
        if (null == mMainMenu) {
            mMainMenu = new PopupWindow(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            mMainMenu.setBackgroundDrawable(getResources().getDrawable(R.color.transparent));
            mMainMenu.setOutsideTouchable(true);
            mMainMenu.setFocusable(true);
            mMainMenu.setAnimationStyle(R.style.AnimationPopupWindow);
            mMainMenu.setContentView(mMainMenuView);
        }
        if (mMainMenu.isShowing()) {
            mMainMenu.dismiss();
            return;
        }
        boolean hasData = !TextUtils.isEmpty(mInputEditText.getText().toString());
        mMainMenuView.findViewById(R.id.main_menu_item_1).setVisibility(hasData ? View.VISIBLE : View.GONE);
        mMainMenuView.findViewById(R.id.main_menu_item_2).setVisibility(hasData ? View.VISIBLE : View.GONE);
        mMainMenu.showAtLocation(mMainLayout, Gravity.BOTTOM | Gravity.LEFT, 0, 0);
    }


    private void updateAppMenu(){
        boolean useAppMenu = mConfigManager.isUseAppMenu();
        mMenuButton.setVisibility(useAppMenu?View.VISIBLE:View.GONE);
    }


    public void updateMyWidget() {
        String updateAction = "android.appwidget.action.APPWIDGET_SET_DATA";
        Intent intent1 = new Intent(this, WidgetForOneXOne.class);
        intent1.setAction(updateAction);
        intent1.putExtra("TEXT_STRING", this.mInputEditText.getText().toString());
        sendBroadcast(intent1);
        Intent intent2 = new Intent(this, WidgetForFourXTwo.class);
        intent2.putExtra("TEXT_STRING", this.mInputEditText.getText().toString());
        intent2.setAction(updateAction);
        sendBroadcast(intent2);
        Intent intent3 = new Intent(this, WidgetForFourXThree.class);
        intent3.putExtra("TEXT_STRING", this.mInputEditText.getText().toString());
        intent3.setAction(updateAction);
        sendBroadcast(intent3);
        Intent intent4 = new Intent(this, WidgetForFourXFour.class);
        intent4.putExtra("TEXT_STRING", this.mInputEditText.getText().toString());
        intent4.setAction(updateAction);
        sendBroadcast(intent4);
        Intent intent5 = new Intent(this, WidgetForFourXOne.class);
        intent5.putExtra("TEXT_STRING", this.mInputEditText.getText().toString());
        intent5.setAction(updateAction);
        sendBroadcast(intent5);
    }

    private class ServiceBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            readData();
        }
    }

    private void initColorPickerView(View colorPickerView) {
        int initialColor = mConfigManager.getConfigAppBgColor();
        if (null != colorPickerView) {
            mColorPickerView = (ColorPickerView) colorPickerView.findViewById(R.id.color_picker_view);
            mOldColorPanelView = (ColorPanelView) colorPickerView.findViewById(R.id.color_panel_old);
            mNewColorPanelView = (ColorPanelView) colorPickerView.findViewById(R.id.color_panel_new);
            mColorPickerView.setOnColorChangedListener(onColorChangedListener);
            mColorPickerView.setColor(initialColor, true);
            mOldColorPanelView.setColor(initialColor);
        }
    }

    private ColorPickerView.OnColorChangedListener onColorChangedListener = new ColorPickerView.OnColorChangedListener() {
        @Override
        public void onColorChanged(int newColor) {
            mNewColorPanelView.setColor(mColorPickerView.getColor());
        }
    };
}
