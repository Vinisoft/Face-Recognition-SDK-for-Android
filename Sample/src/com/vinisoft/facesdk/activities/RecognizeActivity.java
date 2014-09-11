package com.vinisoft.facesdk.activities;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.vinisoft.facesdk.custom.CameraManager;
import com.vinisoft.facesdk.custom.RecognizerView;
import com.vinisoft.facesdk.utils.DetectListener;
import com.vinisoft.facesdk.utils.MainUtils;

/**
 * IMPORTANT NOTE<br>
 * 1)The recognizer activity must implement interface {@link DetectListener} to
 * handle result when success.<br>
 * 
 * 2)Remember copy all files in asset folder of Sample Project to your Project.
 * 
 * * TODO this SDK is valid to end of each year. If SDK out of date, please
 * download new version and update your application or contact with SDK
 * provider.
 * */

public class RecognizeActivity extends Activity implements DetectListener {
	private RecognizerView mLock = null;
	private WindowManager.LayoutParams params = null;
	private Handler handler;
	private Runnable run;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		params = new WindowManager.LayoutParams(
				WindowManager.LayoutParams.TYPE_SYSTEM_ERROR,
				WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
				PixelFormat.TRANSLUCENT);
		params.screenOrientation = Configuration.ORIENTATION_PORTRAIT;
		mLock = new RecognizerView(this);
		handler = new Handler();
		run = new Runnable() {

			@Override
			public void run() {
				mLock.prepareResumeView();
				mLock.getCameraView().invalidate();
				mLock.invalidate();
			}
		};
		if (CameraManager.getInstance().getCamera() == null) {
			MainUtils.getDialogCameraError(getApplicationContext()).show();
		}

		mLock.getCameraView().setVisibility(View.INVISIBLE);
		mLock.getCameraView().onResume();
		handler.postDelayed(run, 10);

		setContentView(mLock, params);

	}

	@Override
	public void onDestroy() {

		super.onDestroy();
		if (mLock != null) {
			mLock.getCameraView().onPause();
			mLock = null;
		}
	}

	@Override
	public void onRecognizeSuccess() {
		Toast.makeText(RecognizeActivity.this, "Recognize success",
				Toast.LENGTH_SHORT).show();

		// TODO continue with your action here.

	}
}
