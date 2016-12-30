package com.ted.jots.myjot.setting;

import android.os.Bundle;
import android.support.v7.preference.DialogPreference;
import android.support.v7.preference.PreferenceDialogFragmentCompat;
import android.view.View;

import com.ted.jots.myjot.R;
import com.ted.jots.myjot.config.ConfigManager;
import com.ted.jots.myjot.view.colorpickerview.view.ColorPanelView;
import com.ted.jots.myjot.view.colorpickerview.view.ColorPickerView;

/**
 * Created by ted on 2016/12/27.
 * in com.ted.jots.myjot.setting
 */

public class ColorPickerPreDlgFragCompat extends PreferenceDialogFragmentCompat {
    private ColorPanelView mNewColorPanelView;
    private ColorPickerView mColorPickerView;

    public static ColorPickerPreDlgFragCompat newInstance(String key) {
        final ColorPickerPreDlgFragCompat
                fragment = new ColorPickerPreDlgFragCompat();
        final Bundle b = new Bundle();
        b.putString(ARG_KEY, key);
        fragment.setArguments(b);
        return fragment;
    }

    private ColorPickerView.OnColorChangedListener onColorChangedListener = new ColorPickerView.OnColorChangedListener() {
        @Override
        public void onColorChanged(int newColor) {
            mNewColorPanelView.setColor(mColorPickerView.getColor());
        }
    };

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
        mColorPickerView = (ColorPickerView) view.findViewById(R.id.color_picker_view);
        ColorPanelView preColorPanelView = (ColorPanelView) view.findViewById(R.id.color_panel_old);
        mNewColorPanelView = (ColorPanelView) view.findViewById(R.id.color_panel_new);
        if (null == mColorPickerView || null == preColorPanelView || null == mNewColorPanelView) {
            throw new IllegalStateException("Dialog view must contain a ColorPickerView or ColorPanelView");
        }
        mColorPickerView.setOnColorChangedListener(onColorChangedListener);

        boolean isFontColor = getString(R.string.font_color_key).equals(getArguments().getString(ARG_KEY));
        mColorPickerView.setAlphaSliderVisible(!isFontColor);
        int color = -1;
        DialogPreference preference = getPreference();
        if (preference instanceof ColorPickerPreference) {
            color = ((ColorPickerPreference) preference).getColor();
        }
        if (color == 0) color = getDefaultColor(isFontColor);
        mColorPickerView.setColor(color, true);
        preColorPanelView.setColor(color);
    }

    @Override
    public void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            int color = mColorPickerView.getColor();
            DialogPreference preference = getPreference();
            if (preference instanceof ColorPickerPreference) {
                ((ColorPickerPreference) preference).setColor(color);
            }
        }
    }

    private int getDefaultColor(boolean isFont) {
        return isFont ? ConfigManager.getConfigAppTxtColor(getActivity())
                : ConfigManager.getConfigAppBgColor(getActivity());
    }

}
