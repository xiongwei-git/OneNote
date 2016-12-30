package com.ted.jots.myjot.utils;

import android.app.Activity;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;

/**
 * 系统功能调用工具类
 * 
 * @author ted
 */
public final class SystemUtil {


	public static void HideSoftInput(Activity activity) {
		if(null == activity || activity.getCurrentFocus() == null){
			return;
		}
		InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }


}
