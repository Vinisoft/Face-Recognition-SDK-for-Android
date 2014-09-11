package com.vinisoft.facesdk.custom;

import android.annotation.SuppressLint;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;

import com.vinisoft.facesdk.utils.LogUtils;

public class CameraManager {
	private static CameraManager instance;

	private boolean isFrontCamera = true;
	private Camera camera = null;

	private CameraManager() {
		freeCamera();
		camera = openFrontCamera();
	}

	public static CameraManager getInstance() {
		if (instance == null) {
			instance = new CameraManager();
		}

		if (instance.camera == null) {
			instance.camera = instance.openFrontCamera();
		}

		return instance;
	}

	public void freeCamera() {
		if (camera != null) {
			camera.stopPreview();
			camera.release();
			camera = null;
		}
	}

	public Camera getCamera() {
		return camera;
	}

	public boolean isFrontCamera() {
		return isFrontCamera;
	}

	@SuppressLint("NewApi")
	private Camera openFrontCamera() {
		Camera cam = null;
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.GINGERBREAD) {
			try {
				cam = Camera.open(CameraInfo.CAMERA_FACING_FRONT);
				isFrontCamera = true;
				return cam;
			} catch (Exception e) {
				isFrontCamera = false;
				LogUtils.exception(e);
			}

		} else {
			try {
				cam = Camera.open();
				CameraInfo camInfo = new CameraInfo();
				Camera.getCameraInfo(0, camInfo);
				if (camInfo.facing == CameraInfo.CAMERA_FACING_FRONT) {
					isFrontCamera = true;
				} else {
					isFrontCamera = false;
				}
				return cam;
			} catch (Exception e) {
				LogUtils.exception(e);
			}
		}

		return cam;
	}
}
