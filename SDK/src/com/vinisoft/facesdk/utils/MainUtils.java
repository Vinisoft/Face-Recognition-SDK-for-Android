package com.vinisoft.facesdk.utils;

import java.io.File;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Build;
import android.preference.PreferenceManager;
import android.view.Display;
import android.view.WindowManager;

import com.vinisoft.facesdk.custom.CameraManager;
import com.vinisoft.facesdk.detect.DetectSetting;

public class MainUtils {

	/** The {@link SharedPreferences} to contain data setting for SDK. */
	private static SharedPreferences setting = null;

	public static SharedPreferences getSetting(Context ctx) {
		if (setting == null) {
			setting = PreferenceManager.getDefaultSharedPreferences(ctx);
		}

		return setting;
	}

	public static final int MAX_FACE_DATA = 20;

	public static AlertDialog getDialogCameraError(final Context mContext) {
		try {
			CameraManager.getInstance().freeCamera();
		} catch (Exception e) {
			LogUtils.exception(e);
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setTitle("Error");
		builder.setMessage("Can't connect to Camera, please restart your device!");
		builder.setCancelable(false);
		builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (mContext instanceof Activity) {
					((Activity) mContext).finish();
				}
				dialog.dismiss();
				System.gc();
				System.exit(0);
			}
		});

		AlertDialog alert = builder.create();
		alert.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ERROR);
		return alert;
	}

	public static boolean isTrained() {
		File fileTrained = new File(DetectSetting.PATH_DATA_FACE);
		return fileTrained.exists();
	}

	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	public static Point getScreenSize(Context mContext) {
		WindowManager wm = (WindowManager) mContext
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		Point size = new Point();

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			display.getSize(size);
			return size;
		} else {
			size.x = display.getWidth();
			size.y = display.getHeight();
			return size;
		}
	}

	public static String getString(String key, String defaultValue, Context ctx) {
		if (setting == null) {
			setting = PreferenceManager.getDefaultSharedPreferences(ctx);
		}
		return setting.getString(key, defaultValue);
	}
}
