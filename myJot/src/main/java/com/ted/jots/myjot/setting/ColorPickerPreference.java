package com.ted.jots.myjot.setting;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.preference.DialogPreference;
import android.util.AttributeSet;

import com.ted.jots.myjot.R;

/**
 * Created by ted on 2016/12/27.
 * in com.ted.jots.myjot.setting
 */

public class ColorPickerPreference extends DialogPreference {

    private int mColor;

    public ColorPickerPreference(Context context) {
        this(context, null);
    }

    public ColorPickerPreference(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.preferenceStyle);
    }

    public ColorPickerPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, defStyleAttr);

    }

    public ColorPickerPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public int getColor() {
        return mColor;
    }

    public void setColor(int color) {
        mColor = color;
        persistInt(color);
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInt(index, 0);
    }

    @Override
    public int getDialogLayoutResource() {
        return R.layout.color_picker_layout;
    }


    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        setColor(restorePersistedValue ? getPersistedInt(mColor) : (int) defaultValue);
    }
}
