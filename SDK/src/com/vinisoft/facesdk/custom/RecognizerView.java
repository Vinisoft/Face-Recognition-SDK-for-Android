package com.vinisoft.facesdk.custom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.vinisoft.facesdk.demo.R;
import com.vinisoft.facesdk.utils.DetectListener;

public class RecognizerView extends RelativeLayout {

	private LayoutInflater mInflater;
	private View mLockView;
	private CameraView preview;
	private Context ctx;

	public RecognizerView(Context context) {
		super(context);
		this.ctx = context;
		init(context);
	}

	public RecognizerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.ctx = context;
		init(context);
	}

	@SuppressLint("SimpleDateFormat")
	private void init(Context context) {

		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mLockView = mInflater.inflate(R.layout.activity_lock, null);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		mLockView.setLayoutParams(params);
		addView(mLockView);
		initView(mLockView);

	}

	private void initView(View mView) {
		preview = (CameraView) mView.findViewById(R.id.surface);
		if ((ctx != null) && (ctx instanceof DetectListener)) {
			preview.setListener((DetectListener) ctx);
		} else {
			Toast.makeText(
					getContext(),
					"ERROR: Your recognizer activity must implement DetectListener interface!",
					Toast.LENGTH_LONG).show();

		}
		preview.setMode(CameraView.MODE_LOCKSCREEN);
		initViewStatus();
	}

	private void initViewStatus() {
		preview.setVisibility(View.VISIBLE);
	}

	public CameraView getCameraView() {
		return preview;
	}

	public void prepareResumeView() {
		preview.setVisibility(View.VISIBLE);
		preview.setVisibility(View.VISIBLE);
	}

}
