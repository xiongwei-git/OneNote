package com.ted.jots.myjot.main;

import android.content.Context;
import android.view.View;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.ted.jots.myjot.R;
import com.ted.jots.myjot.utils.CheckDoubleClick;

/**
 * Created by ted on 2016/12/22.
 * in com.ted.jots.myjot.main
 */

public class MainMenuHelper implements View.OnClickListener {
    public static final int MENU_TYPE_CLEAR = 0;
    public static final int MENU_TYPE_SHARE = 1;
    public static final int MENU_TYPE_SETTING = 2;


//    @IntDef({MENU_TYPE_CLEAR,MENU_TYPE_SETTING,MENU_TYPE_SHARE})
//    @Retention(RetentionPolicy.SOURCE)
//    private @interface MenuType{}

    private MainMenuClickListener menuClickListener;

    @Override
    public void onClick(View view) {
        if (CheckDoubleClick.isFastDoubleClick()) return;
        Object tag = view.getTag();
        if (null != tag && tag instanceof Integer) {
            int type = (Integer) tag;
            if (null != menuClickListener) {
                menuClickListener.onMenuClick(type);
            }
        }
    }

    public MainMenuHelper(MainMenuClickListener listener) {
        this.menuClickListener = listener;
    }

    public void initBasicMenu(FloatingActionMenu menu) {
        if (menu.getChildCount() > 2) return;
        menu.addMenuButton(getClearButton(menu.getContext()));
        menu.addMenuButton(getShareButton(menu.getContext()));
        menu.addMenuButton(getSettingButton(menu.getContext()));
    }

    private FloatingActionButton getSettingButton(Context context) {
        final FloatingActionButton settingBtn = new FloatingActionButton(context);
        settingBtn.setButtonSize(FloatingActionButton.SIZE_MINI);
        settingBtn.setLabelText(context.getString(R.string.app_name));
        settingBtn.setColorNormalResId(R.color.app_logo_color_light);
        settingBtn.setColorPressedResId(R.color.app_logo_color);
        settingBtn.setImageResource(R.drawable.set);
        settingBtn.setTag(MENU_TYPE_SETTING);
        settingBtn.setLabelText("设置应用");
        settingBtn.setOnClickListener(this);
        return settingBtn;
    }

    private FloatingActionButton getShareButton(Context context) {
        final FloatingActionButton shareBtn = new FloatingActionButton(context);
        shareBtn.setButtonSize(FloatingActionButton.SIZE_MINI);
        shareBtn.setLabelText(context.getString(R.string.app_name));
        shareBtn.setImageResource(R.drawable.share);
        shareBtn.setTag(MENU_TYPE_SHARE);
        shareBtn.setLabelText("分享内容");
        shareBtn.setColorNormalResId(R.color.app_logo_color_light);
        shareBtn.setColorPressedResId(R.color.app_logo_color);
        shareBtn.setOnClickListener(this);
        return shareBtn;
    }

    private FloatingActionButton getClearButton(Context context) {
        final FloatingActionButton clearBtn = new FloatingActionButton(context);
        clearBtn.setButtonSize(FloatingActionButton.SIZE_MINI);
        clearBtn.setLabelText(context.getString(R.string.app_name));
        clearBtn.setImageResource(R.drawable.delete);
        clearBtn.setTag(MENU_TYPE_CLEAR);
        clearBtn.setLabelText("清空内容");
        clearBtn.setColorNormalResId(R.color.app_logo_color_light);
        clearBtn.setColorPressedResId(R.color.app_logo_color);
        clearBtn.setOnClickListener(this);
        return clearBtn;
    }


    public interface MainMenuClickListener {
        void onMenuClick(int type);
    }
}
