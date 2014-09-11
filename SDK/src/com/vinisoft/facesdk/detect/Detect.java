package com.vinisoft.facesdk.detect;

import android.content.Context;

import com.vinisoft.facesdk.utils.FileUtils;
import com.vinisoft.facesdk.utils.LogUtils;

public class Detect {
	static {
		System.loadLibrary("opencv_java");
		System.loadLibrary("Detect");
	}

	public static Detect instance;

	public static Detect getInstance(Context context) {
		if (instance == null) {
			instance = new Detect(context);
		}
		return instance;
	}

	private Detect(Context context) {

		FileUtils.assertToFile(context, "lbpcascade_frontalface.xml", DetectSetting.PATH_XML_LBPFACE);
		FileUtils.assertToFile(context, "haarcascade_frontalface_alt.xml", DetectSetting.PATH_XML_FACE);
		FileUtils.assertToFile(context, "haarcascade_mcs_nose.xml", DetectSetting.PATH_XML_NOSE);
		FileUtils.assertToFile(context, "haarcascade_eye_tree_eyeglasses.xml", DetectSetting.PATH_XML_EYE1);
		FileUtils.assertToFile(context, "haarcascade_eye.xml", DetectSetting.PATH_XML_EYE2);

		FileUtils.writeFile(DetectSetting.PATH_TEMP_FACE, "", false);
		FileUtils.writeFile(DetectSetting.PATH_DATA_NOSE, "", false);
		setting(DetectSetting.class);
	}

	public FaceDetectInfo doRecognition(boolean isFrontCamera, int width, int height, byte[] data) {
		FaceDetectInfo info = null;
		try {
			info = doRecognitionNative(isFrontCamera, width, height, data);
		} catch (Exception e) {
			LogUtils.exception(e);
		}
		return info;
	}

	public native void setting(Class setting);

	public native FaceDetectInfo doRecognitionNative(boolean isFrontCamera, int width, int height, byte[] data);

	public native int doTrainning();

	public native int getFace(boolean isFrontCamera, int width, int height, byte[] data);

	public native void update(int id, int imagePos);

	public native void setThreshold(int threshold); // Threshold of face

	public native void free();
}
