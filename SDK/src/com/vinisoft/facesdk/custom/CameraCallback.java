package com.vinisoft.facesdk.custom;

import android.content.Context;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.os.AsyncTask;
import android.os.Handler;

import com.vinisoft.facesdk.detect.Detect;
import com.vinisoft.facesdk.detect.DetectSetting;
import com.vinisoft.facesdk.detect.FaceDetectInfo;
import com.vinisoft.facesdk.utils.FileUtils;
import com.vinisoft.facesdk.utils.LogUtils;

public class CameraCallback implements PreviewCallback {
	public boolean getIdDone = true;
	public boolean getFaceDone = true;

	private byte[] m_previewBuffer = null;

	private CameraView mCameraView;
	private Size previewSize;
	private int bufferSize;
	private int bitsPerPixel;
	private PixelFormat pfInfo;

	private Context context = null;
	private Detect detect;
	private FaceDetectInfo info;
	private Size mCameraSize = null;
	private Handler mHandler = new Handler();
	private Runnable run = new Runnable() {

		@Override
		public void run() {
			getIdDone = true;
		}
	};

	public interface UpdateOnPreview {
		public void onUpdate();
	}

	public FaceDetectInfo getInfo() {
		return info;
	}

	public void resetInfo() {
		if (info != null) {
			info.id = -1;
		}
		getIdDone = false;
		mHandler.postDelayed(run, 3000);
		FileUtils.deleteRecursive(DetectSetting.PATH_TEMP_SAVE);
	}

	public CameraCallback(CameraView mCameraView, Context ctx) {
		this.mCameraView = mCameraView;
		this.context = ctx;
		this.detect = Detect.getInstance(context);
		this.info = null;
		this.getIdDone = true;
		this.getFaceDone = true;
	}

	public void setCameraCallback(Camera camera) {
		try {
			if (camera != null) {
				previewSize = camera.getParameters().getPreviewSize();
				pfInfo = new PixelFormat();
				PixelFormat.getPixelFormatInfo(camera.getParameters()
						.getPreviewFormat(), pfInfo);
				bitsPerPixel = pfInfo.bitsPerPixel;
				bufferSize = (previewSize.width) * (previewSize.height)
						* (int) Math.ceil(bitsPerPixel / 8.);
				m_previewBuffer = null;
				m_previewBuffer = new byte[bufferSize];
				camera.addCallbackBuffer(m_previewBuffer);
				camera.setPreviewCallbackWithBuffer(this);
			}
		} catch (Throwable ex) {
			if (ex instanceof OutOfMemoryError) {
				System.gc();
			}
			return;
		}
	}

	@Override
	public void onPreviewFrame(final byte[] data, final Camera camera) {
		if (camera == null || data == null || data.length <= 0) {
			return;
		}

		setCameraCallback(camera);

		if (mCameraView.getMode() == CameraView.MODE_LOCKSCREEN) {

			if (getIdDone) {
				onPreviewGetID(data, camera);
			}
		} else if (mCameraView.getMode() == CameraView.MODE_TRAINING) {

			if (getFaceDone) {
				onPreviewGetFace(data, camera);
			}
		}
	}

	// =====================================================================
	private void onPreviewGetID(final byte[] data, final Camera camera) {
		new AsyncTask<Void, Void, FaceDetectInfo>() {
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				getIdDone = false;
			}

			@Override
			protected FaceDetectInfo doInBackground(Void... params) {
				try {
					if (mCameraSize == null) {
						mCameraSize = camera.getParameters().getPreviewSize();
					}
					info = detect.doRecognition(CameraManager.getInstance()
							.isFrontCamera(), mCameraSize.width,
							mCameraSize.height, data);
					return info;
				} catch (Exception e) {
					LogUtils.exception(e);
				}

				return null;
			}

			@Override
			protected void onPostExecute(FaceDetectInfo result) {
				super.onPostExecute(result);
				getIdDone = true;
				if (mCameraView.getOnUpdate() != null) {
					mCameraView.getOnUpdate().onUpdate();
				}
			}

		}.execute();
	}

	private void onPreviewGetFace(final byte[] data, final Camera c) {
		new AsyncTask<Void, Void, Integer>() {
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				getFaceDone = false;
			}

			@Override
			protected Integer doInBackground(Void... params) {
				Size size = c.getParameters().getPreviewSize();

				try {
					return detect.getFace(CameraManager.getInstance()
							.isFrontCamera(), size.width, size.height, data);

				} catch (Exception e) {
					LogUtils.exception(e);
				}

				return -1;
			}

			@Override
			protected void onPostExecute(Integer result) {
				super.onPostExecute(result);
				getFaceDone = true;
				if (result == 1) {
					if (mCameraView.getOnUpdate() != null) {
						mCameraView.getOnUpdate().onUpdate();
					}
				}
				LogUtils.d("Result: " + result);
			}

		}.execute();
	}
}
