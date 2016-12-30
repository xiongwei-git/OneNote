package com.ted.jots.myjot.setting;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.preference.Preference;

/**
 * Created by ted on 2016/12/27.
 * in com.ted.jots.myjot.setting
 */

public class CommonDialogFragment extends DialogFragment {
    private String mDialogPreferenceKey;

    public static CommonDialogFragment newIns(Preference preference) {
        CommonDialogFragment ins = new CommonDialogFragment();
        Bundle args = new Bundle();
        CommonDialogPreference dialogPreference = (CommonDialogPreference) preference;
        args.putString("title", dialogPreference.getDialogTitle().toString());
        args.putString("msg", dialogPreference.getDialogMessage().toString());
        args.putString("positive", dialogPreference.getPositiveButtonText().toString());
        args.putString("negative", dialogPreference.getNegativeButtonText().toString());
        args.putString("key", dialogPreference.getKey());
        ins.setArguments(args);
        return ins;
    }

    private DialogInterface.OnClickListener mPositiveOnClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            if (null != getTargetFragment() && getTargetFragment() instanceof SettingFragment) {
                ((SettingFragment) getTargetFragment()).onDialogCall(mDialogPreferenceKey, true);
            }
        }
    };

    private DialogInterface.OnClickListener mNegativeOnClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            if (null != getTargetFragment() && getTargetFragment() instanceof SettingFragment) {
                ((SettingFragment) getTargetFragment()).onDialogCall(mDialogPreferenceKey, false);
            }
        }
    };

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getArguments().getString("title", ""));
        builder.setMessage(getArguments().getString("msg", ""));
        builder.setNegativeButton(getArguments().getString("negative", "取消"), mNegativeOnClickListener);
        builder.setPositiveButton(getArguments().getString("positive", "确定"), mPositiveOnClickListener);
        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mDialogPreferenceKey = getArguments().getString("key", "");
    }
}
