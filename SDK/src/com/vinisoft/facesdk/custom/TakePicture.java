package com.vinisoft.facesdk.custom;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;

public class TakePicture implements Camera.PictureCallback {
	private Activity mActivity;
	private Bitmap bmp;
	private static Bitmap mutableBitmap;
	private File imageFileName = null;
	private File imageFileFolder = null;
	private MediaScannerConnection msConn = null;
	private TakePictureTask mTask = null;

	private class TakePictureTask extends AsyncTask<byte[], Void, Void> {
		ProgressDialog dialog;
		Camera camera;

		public TakePictureTask(Camera camera) {
			this.camera = camera;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog = ProgressDialog.show(mActivity, "", "Saving Photo");
		}

		@Override
		protected Void doInBackground(byte[]... params) {
			onPictureTake(params[0], camera);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			dialog.dismiss();
			camera.startPreview();
		}
	}

	public TakePicture(Activity mActivity, Camera camera) {
		this.mActivity = mActivity;
		this.mTask = new TakePictureTask(camera);
	}

	@Override
	public void onPictureTaken(final byte[] data, final Camera camera) {
		mTask.execute(data);
	}

	private void onPictureTake(byte[] data, Camera camera) {

		bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
		mutableBitmap = bmp.copy(Bitmap.Config.ARGB_8888, true);
		savePhoto(mutableBitmap);
	}

	public void savePhoto(Bitmap bmp) {
		imageFileFolder = new File(Environment.getExternalStorageDirectory(), "Data");
		imageFileFolder.mkdir();
		FileOutputStream out = null;
		Calendar c = Calendar.getInstance();
		String date = fromInt(c.get(Calendar.MONTH)) + fromInt(c.get(Calendar.DAY_OF_MONTH)) + fromInt(c.get(Calendar.YEAR)) + fromInt(c.get(Calendar.HOUR_OF_DAY)) + fromInt(c.get(Calendar.MINUTE)) + fromInt(c.get(Calendar.SECOND));
		imageFileName = new File(imageFileFolder, date.toString() + ".jpg");
		try {
			out = new FileOutputStream(imageFileName);
			bmp.compress(Bitmap.CompressFormat.JPEG, 100, out);
			out.flush();
			out.close();
			scanPhoto(imageFileName.toString());
			out = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String fromInt(int val) {
		return String.valueOf(val);
	}

	public void scanPhoto(final String imageFileName) {
		msConn = new MediaScannerConnection(mActivity, new MediaScannerConnectionClient() {
			public void onMediaScannerConnected() {
				msConn.scanFile(imageFileName, null);
			}

			public void onScanCompleted(String path, Uri uri) {
				msConn.disconnect();
			}
		});
		msConn.connect();
	}
}
