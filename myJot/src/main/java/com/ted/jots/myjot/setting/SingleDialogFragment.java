package com.ted.jots.myjot.setting;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.preference.Preference;

import com.ted.jots.myjot.R;

/**
 * Created by ted on 2016/12/27.
 * in com.ted.jots.myjot.setting
 */

public class SingleDialogFragment extends DialogFragment {
    //private String mDialogPreferenceKey;

    public static SingleDialogFragment newIns(Preference preference) {
        SingleDialogFragment ins = new SingleDialogFragment();
        Bundle args = new Bundle();
        CommonDialogPreference dialogPreference = (CommonDialogPreference) preference;
        args.putString("title", dialogPreference.getDialogTitle().toString());
        args.putString("msg", dialogPreference.getDialogMessage().toString());
        args.putString("neutral", dialogPreference.getPositiveButtonText().toString());
        args.putString("key", dialogPreference.getKey());
        ins.setArguments(args);
        return ins;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getArguments().getString("title", ""));
        builder.setMessage(getArguments().getString("msg", ""));
        builder.setNeutralButton(getArguments().getString("neutral", getString(R.string.i_see)), null);
        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        //mDialogPreferenceKey = getArguments().getString("key", "");
    }
}
