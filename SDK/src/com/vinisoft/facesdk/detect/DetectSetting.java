package com.vinisoft.facesdk.detect;

import android.content.Context;

public class DetectSetting {
	public static Context mContext;
	public static final int MAX_IMAGE = 10;
	public static final int MY_ID = 1111;
	public static String PATH_ROOT;
	public static String PATH_LIST_PERSON;

	public static String PATH_XML_LBPFACE;
	public static String PATH_XML_FACE;
	public static String PATH_XML_NOSE;
	public static String PATH_XML_EYE1;
	public static String PATH_XML_EYE2;

	public static String PATH_DATA_FACE;
	public static String PATH_DATA_NOSE;

	public static String PATH_LIST_FACE;
	public static String PATH_LIST_NOSE;

	public static String PATH_TEMP_FACE;
	public static String PATH_TEMP_NOSE;
	public static String PATH_TEMP_SAVE;

	public static String PATH_IMG;
	public static String PATH_IMG_FACE;
	public static String PATH_IMG_NOSE;
	public static String PATH_CONFIG;

	public static String STRING_DATA_FACE;
	public static String STRING_DATA_NOSE;

	public static void init(Context context) {
		mContext = context;
		PATH_ROOT = mContext.getCacheDir().getPath() + "/";
		//PATH_ROOT = mContext.getExternalCacheDir().getPath() + "/";

		PATH_LIST_PERSON = PATH_ROOT + "ListPerson.txt";

		PATH_XML_LBPFACE = PATH_ROOT + "cascade/lbpcascade_frontalface.xml";
		PATH_XML_FACE = PATH_ROOT + "cascade/haarcascade_frontalface_alt.xml";
		PATH_XML_NOSE = PATH_ROOT + "cascade/haarcascade_mcs_nose.xml";
		PATH_XML_EYE1 = PATH_ROOT + "cascade/haarcascade_eye_tree_eyeglasses.xml";
		PATH_XML_EYE2 = PATH_ROOT + "cascade/haarcascade_eye.xml";

		PATH_DATA_FACE = PATH_ROOT + "datasave/FaceSave.yml";
		PATH_DATA_NOSE = PATH_ROOT + "datasave/NoseSave.yml";

		PATH_LIST_FACE = PATH_ROOT + "ListFace.txt";
		PATH_LIST_NOSE = PATH_ROOT + "ListNose.txt";

		PATH_TEMP_FACE = PATH_ROOT + "temp/Face.jpg";
		PATH_TEMP_NOSE = PATH_ROOT + "temp/Nose.jpg";
		PATH_TEMP_SAVE = PATH_ROOT + "temp/save.jpg";

		PATH_IMG = PATH_ROOT + "s%d/";
		PATH_IMG_FACE = PATH_IMG + "face%d.jpg";
		PATH_IMG_NOSE = PATH_IMG + "nose%d.jpg";
		PATH_CONFIG = PATH_IMG + "config.txt";

		STRING_DATA_FACE = PATH_IMG + "face%d.jpg;%d";
		STRING_DATA_NOSE = PATH_IMG + "nose%d.jpg;%d";
	}
}
