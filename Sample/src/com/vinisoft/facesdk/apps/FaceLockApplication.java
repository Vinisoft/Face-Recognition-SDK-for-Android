package com.vinisoft.facesdk.apps;

import java.lang.Thread.UncaughtExceptionHandler;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;

import com.vinisoft.facesdk.custom.CameraManager;
import com.vinisoft.facesdk.detect.Detect;
import com.vinisoft.facesdk.detect.DetectSetting;
import com.vinisoft.facesdk.utils.LogUtils;
import com.vinisoft.facesdk.utils.MainUtils;

/**
 * IMPORTANT NOTE<br>
 * 1)Remember copy all files in asset folder of Sample Project to your Project.
 * 
 * TODO this SDK is valid to end of each year. If SDK out of date, please
 * download new version and update your application or contact with SDK
 * provider.
 * */
public class FaceLockApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();

		LogUtils.setEnable(true);

		if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
			init(getApplicationContext());
		} else {
			onTerminate();
		}

		Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {

			@Override
			public void uncaughtException(Thread thread, Throwable ex) {
				LogUtils.setTag("UncaughtExceptionHandler");
				if (thread.getId() == getMainLooper().getThread().getId()) {
					LogUtils.throwable(ex);
					CameraManager.getInstance().freeCamera();
					System.gc();
					System.exit(0);
				}
			}
		});
	}

	// ====================================================================
	public static void init(Context mContext) {

		DetectSetting.init(mContext);

		String value = MainUtils.getString("prefLevel", "100", mContext);
		int threshold = value == null ? 100 : Integer.parseInt(value);
		Detect.getInstance(mContext).setThreshold(threshold);
	}

}
