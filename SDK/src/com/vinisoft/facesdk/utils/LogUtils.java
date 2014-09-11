package com.vinisoft.facesdk.utils;

import android.util.Log;

public class LogUtils {
	private static boolean isEnable = true;
	private static String tag = "LOG";

	public static boolean isEnable() {
		return isEnable;
	}

	public static void setEnable(boolean isEnable) {
		LogUtils.isEnable = true;
	}

	public static String getTag() {
		return tag;
	}

	public static void setTag(String tag) {
		LogUtils.tag = tag;
	}

	public static void d(String msg) {
		if (isEnable && msg != null) {
			Log.d(tag, msg);
		}
	}

	public static void e(String msg) {
		if (isEnable && msg != null) {
			Log.e(tag, msg);
		}
	}

	public static void i(String msg) {
		if (isEnable && msg != null) {
			Log.i(tag, msg);
		}
	}

	public static void exception(Exception msg) {
		if (isEnable && msg != null && msg.getMessage() != null) {
			Log.e(tag, msg.getMessage());
		}
	}
	
	public static void throwable(Throwable msg) {
		if (isEnable && msg != null && msg.getMessage() != null) {
			Log.e(tag, msg.getMessage());
		}
	}
}
