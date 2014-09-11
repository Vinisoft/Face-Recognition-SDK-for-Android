package com.vinisoft.facesdk.custom;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import com.vinisoft.facesdk.custom.CameraCallback.UpdateOnPreview;
import com.vinisoft.facesdk.detect.FaceDetectInfo;
import com.vinisoft.facesdk.utils.DetectListener;
import com.vinisoft.facesdk.utils.LogUtils;
import com.vinisoft.facesdk.utils.MainUtils;

public class CameraView extends SurfaceView implements SurfaceHolder.Callback {
	public static final int MODE_LOCKSCREEN = 1;
	public static final int MODE_TRAINING = 2;
	private int mode = MODE_LOCKSCREEN;

	private int iTrainingCount = 0;
	private int count = 0;
	private SurfaceHolder previewHolder = null;
	private Camera camera = null;
	private CameraCallback cameraCallback;
	private Paint paint;
	private UpdateOnPreview mOnUpdate;
	private TrainingLayer mLayer;
	private DetectListener listener;

	public DetectListener getListener() {
		return listener;
	}

	public void setListener(DetectListener listener) {
		this.listener = listener;
	}

	private static List<CameraView> all;

	private static void add(CameraView cp) {
		if (all == null) {
			all = new ArrayList<CameraView>();
		}

		if (!all.contains(cp)) {
			all.add(cp);
		}
	}

	private void remove(CameraView cp) {
		if (all != null) {
			if (all.contains(cp)) {
				all.remove(cp);
			}

			if (all.size() == 0) {
				CameraManager.getInstance().freeCamera();
				camera.release();
				camera = null;
			}
		}
	}

	// ==================================================================

	public CameraView(Context context) {
		super(context);
		init();
	}

	public CameraView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public CameraView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	// ==================================================================
	@SuppressWarnings("deprecation")
	public void init() {
		setWillNotDraw(false);
		Point screenSize = MainUtils.getScreenSize(getContext());

		camera = CameraManager.getInstance().getCamera();

		previewHolder = getHolder();
		previewHolder.addCallback(this);
		previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		previewHolder.setFixedSize(screenSize.x, screenSize.y);

		if (cameraCallback == null) {
			cameraCallback = new CameraCallback(this, getContext());
		}

		if (mOnUpdate == null) {
			mOnUpdate = new UpdateOnPreview() {

				@Override
				public void onUpdate() {
					CameraView.this.invalidate();
				}
			};
		}

		if (paint == null) {
			paint = new Paint(Paint.ANTI_ALIAS_FLAG);
			paint.setDither(true);
			paint.setColor(Color.parseColor("#1F9DB5"));
			paint.setStyle(Paint.Style.FILL);
			paint.setStrokeJoin(Paint.Join.ROUND);
			paint.setStrokeCap(Paint.Cap.ROUND);
			paint.setStrokeWidth(2);
			paint.setTextSize(30);
		}

		add(this);
	}

	public void onResume() {
		init();
		surfaceCreated(previewHolder);
		camera.startPreview();
	}

	public void onPause() {
		if (camera != null) {
			camera.stopPreview();
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		if (mode == CameraView.MODE_LOCKSCREEN) {
			onLockScreenDraw(canvas);
		} else if (mode == CameraView.MODE_TRAINING) {
			onTrainScreenDraw(canvas);
		}
	}

	private void onLockScreenDraw(Canvas canvas) {

		if (camera == null || cameraCallback == null)
			return;

		FaceDetectInfo info = cameraCallback.getInfo();

		if (info != null) {
			if (info.id == -100) {
				Toast.makeText(getContext(),
						"SDK out of date, please contact with provider",
						Toast.LENGTH_LONG).show();
				return;
			}
			if (info.id > 0) {

				/**
				 * TODO If recognize success, application jump to here, you
				 * should put your action after recognize here.
				 * */
				LogUtils.e("Unlock by: " + info.id);
				if ((listener != null) && (listener instanceof DetectListener)) {
					listener.onRecognizeSuccess();
				}
				cameraCallback.resetInfo();
			}
			paint.setColor(Color.GREEN);
		} else {
			paint.setColor(Color.RED);
		}

		if (++count >= 2) {
			canvas.drawOval(new RectF(20, 20, 30, 30), paint);
			if (count >= 3) {
				count = 0;
			}
		}
	}

	private void onTrainScreenDraw(Canvas canvas) {
		mLayer = new TrainingLayer(getWidth(), getHeight(),
				MainUtils.MAX_FACE_DATA);

		mLayer.drawBorderPath(canvas);
		mLayer.drawCircleArc(canvas, iTrainingCount);
	}

	public void invalidateTraining(int iTrainingCount) {
		if (iTrainingCount <= 0) {
			iTrainingCount = 0;
		} else if (iTrainingCount > MainUtils.MAX_FACE_DATA) {
			iTrainingCount = MainUtils.MAX_FACE_DATA;
		}

		this.iTrainingCount = iTrainingCount;
		invalidate();
	}

	// ==================================================================

	public int getMode() {
		return mode;
	}

	public void setMode(int mode) {
		this.mode = mode;
		cameraCallback.getIdDone = true;
		cameraCallback.getFaceDone = true;
	}

	public UpdateOnPreview getOnUpdate() {
		return mOnUpdate;
	}

	public void setOnUpdate(UpdateOnPreview mOnUpdate) {
		if (this.mOnUpdate != null) {
			this.mOnUpdate = null;
		}
		this.mOnUpdate = mOnUpdate;
	}

	private Camera.Size getBestPreviewSize(int width, int height,
			Camera.Parameters parameters) {
		Camera.Size result = null;
		for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
			if (size.width <= width && size.height <= height) {
				if (result == null) {
					result = size;
				} else {
					int resultArea = result.width * result.height;
					int newArea = size.width * size.height;
					if (newArea > resultArea) {
						result = size;
					}
				}
			}
		}
		return (result);
	}

	// ==================================================================
	public void restart() {
		surfaceCreated(previewHolder);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (camera == null || holder.getSurface() == null) {
			return;
		}

		try {
			camera.stopPreview();
			camera.setPreviewDisplay(holder);
			camera.setDisplayOrientation(90);
			camera.startPreview();
		} catch (Throwable t) {
			LogUtils.throwable(t);
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		if (holder.getSurface() == null) {
			return;
		}

		try {
			camera.stopPreview();
		} catch (Exception e) {
			LogUtils.exception(e);
		}

		try {
			Camera.Parameters parameters = camera.getParameters();
			Camera.Size size = getBestPreviewSize(width, height, parameters);

			if (size != null) {
				parameters.setPreviewSize(size.width, size.height);
				camera.setParameters(parameters);
				camera.setPreviewDisplay(holder);
				cameraCallback.setCameraCallback(camera);
				camera.startPreview();
			}
		} catch (Exception e) {
			LogUtils.exception(e);
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		try {
			camera.stopPreview();
			getHolder().removeCallback(this);
			camera.setPreviewCallback(null);
			camera.setPreviewDisplay(null);
			camera.setDisplayOrientation(0);
		} catch (Throwable t) {
			LogUtils.throwable(t);
		}
		remove(this);
	}

	// =================================================================
}
