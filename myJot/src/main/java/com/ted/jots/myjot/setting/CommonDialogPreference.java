package com.ted.jots.myjot.setting;

import android.content.Context;
import android.support.v7.preference.DialogPreference;
import android.util.AttributeSet;

/**
 * Created by ted on 2016/12/27.
 * in com.ted.jots.myjot.setting
 */

public class CommonDialogPreference extends DialogPreference {

    public CommonDialogPreference(Context context) {
        super(context);
    }

    public CommonDialogPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CommonDialogPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CommonDialogPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
}
