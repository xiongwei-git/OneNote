package com.ted.jots.myjot.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.inputmethod.InputConnectionWrapper;
import android.view.inputmethod.InputMethodManager;

/**
 * 系统功能调用工具类
 * 
 * @author ted
 */
public final class SystemUtil {
    private final static String VIDEO_ERROR_TAG = "video_error";
    private final static String VIDEO_OUTPUT_TAG = "video_out";
	
	/**
	 * 调用系统拨号功能，跳到拨号界面
	 * 
	 * @param context
	 * @param phoneNumber
	 *            电话号码
	 */
	public static void callPhone(Context context, String phoneNumber){
		Intent intent = new Intent(Intent.ACTION_DIAL,Uri.parse("tel:"+phoneNumber));
		context.startActivity(intent);
	}
	
	/**
	 * 显示或隐藏软键盘， 若软键盘处于显示状态，则执行隐藏； 若软键盘处于隐藏状态,则执行显示
	 *
	 */
	public static void showOrHideSoftInput(Activity activity){
		InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
	}

	/**
	 * @Title: HideSoftInput
	 * @Description:关闭输入法
	 * @param @param activity
	 * @return void
	 * @throws
	 */
	public static void HideSoftInput(Activity activity) {
		if(null == activity || activity.getCurrentFocus() == null){
			return;
		}
		InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }
	
	/**
	 * 获取软键盘的打开状态
	 * 
	 * @param activity
	 * @return true,打开状态；false,关闭状态
	 */
	public static boolean isSoftInputActive(Activity activity){
		InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
		return imm.isActive();// isOpen若返回true，则表示输入法打开
	}

}
