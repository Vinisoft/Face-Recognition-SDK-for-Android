package com.vinisoft.facesdk.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.vinisoft.facesdk.custom.CameraCallback.UpdateOnPreview;
import com.vinisoft.facesdk.custom.CameraManager;
import com.vinisoft.facesdk.custom.CameraView;
import com.vinisoft.facesdk.demo.R;
import com.vinisoft.facesdk.detect.Detect;
import com.vinisoft.facesdk.detect.DetectSetting;
import com.vinisoft.facesdk.detect.PersonUtils;
import com.vinisoft.facesdk.utils.FileUtils;
import com.vinisoft.facesdk.utils.LogUtils;
import com.vinisoft.facesdk.utils.MainUtils;

/**
 * The activity display training screen. The screen will display camera to take
 * picture and train for smart phone. You can use it by default or customize it
 * by yourself if you want.
 * 
 * TODO this SDK is valid to end of each year. If SDK out of date, please
 * download new version and update your application or contact with SDK
 * provider.
 * 
 * */
public class TraningActivity extends Activity implements OnClickListener {
	private int numberImage = 0;
	private boolean hasFace = false;
	private boolean addingFace = false;
	private Detect detect = null;
	private PersonUtils personUtil;

	private CameraView preview = null;
	private TextView txtFace = null;
	private Button btnStartTraining = null;

	private ViewGroup parentPreview;

	private Handler handler;
	private Runnable run;

	private Runnable afterClear = null;
	private Runnable afterAddFace = null;
	private Runnable afterTraining = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_train);

		preview = (CameraView) findViewById(R.id.surface);
		preview.setMode(CameraView.MODE_TRAINING);
		preview.setOnUpdate(new UpdateOnPreview() {

			@Override
			public void onUpdate() {
				try {
					hasFace = false;

					if (preview.getMode() == CameraView.MODE_LOCKSCREEN) {
						preview.invalidate();
					} else if (preview.getMode() == CameraView.MODE_TRAINING) {

						final Bitmap faceBitmap = BitmapFactory
								.decodeFile(DetectSetting.PATH_TEMP_FACE);
						if (faceBitmap != null) {
							hasFace = true;

							if (addingFace) {
								addFace();
								preview.invalidateTraining(numberImage);
							}
						}
					}
				} catch (Exception e) {
					LogUtils.exception(e);
				}
			}
		});

		detect = Detect.getInstance(this);
		personUtil = new PersonUtils(this);
		numberImage = personUtil.getPersonNumberImage(DetectSetting.MY_ID);

		initLayout();
		txtFace.setText("");
		txtFace.setVisibility(View.INVISIBLE);
		parentPreview = (ViewGroup) preview.getParent();

		handler = new Handler();
		run = new Runnable() {

			@Override
			public void run() {
				preview.setVisibility(View.VISIBLE);
				preview.invalidateTraining(numberImage);
			}
		};
	}

	@Override
	public void onResume() {
		super.onResume();

		if (CameraManager.getInstance().getCamera() == null) {
			MainUtils.getDialogCameraError(this).show();
			return;
		}

		preview.setMode(CameraView.MODE_TRAINING);
		preview.setVisibility(View.INVISIBLE);
		preview.onResume();

		try {
			parentPreview.addView(preview, 0);
		} catch (Exception e) {
			LogUtils.exception(e);
		}

		handler.postDelayed(run, 1);

		if (numberImage < MainUtils.MAX_FACE_DATA || !MainUtils.isTrained()) {
			txtFace.setText(R.string.training_not_complete);
			txtFace.setVisibility(View.VISIBLE);
		} else {
			txtFace.setText("");
			txtFace.setVisibility(View.INVISIBLE);
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		if (CameraManager.getInstance().getCamera() == null) {
			return;
		}

		preview.onPause();
		preview.setVisibility(View.INVISIBLE);
		parentPreview.removeView(preview);
	}

	private void initLayout() {
		txtFace = (TextView) findViewById(R.id.idText);
		btnStartTraining = (Button) findViewById(R.id.btnStartTraining);
		btnStartTraining.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btnStartTraining) {
			btnStartTraining.setEnabled(false);
			doStartTraining();
		}

	}

	private void doStartTraining() {

		if (afterClear == null) {
			afterClear = new Runnable() {

				@Override
				public void run() {
					addingFace = true;
				}
			};
		}

		if (afterAddFace == null) {
			afterAddFace = new Runnable() {

				@Override
				public void run() {
					trainingData(afterTraining);
				}
			};
		}

		if (afterTraining == null) {
			afterTraining = new Runnable() {

				@Override
				public void run() {
					if (numberImage < MainUtils.MAX_FACE_DATA
							|| !MainUtils.isTrained()) {
						txtFace.setText(R.string.training_not_complete);
						txtFace.setVisibility(View.VISIBLE);
					} else {
						txtFace.setText("");
						txtFace.setVisibility(View.INVISIBLE);
					}
				}
			};
		}

		txtFace.setText("");
		txtFace.setVisibility(View.INVISIBLE);
		addingFace = false;
		clearFaceData(afterClear);
	}

	private void addFace() {
		try {

			if (hasFace == true) {
				hasFace = false;
				int nextPos = numberImage + 1;
				numberImage = personUtil.addPersonImage(DetectSetting.MY_ID,
						nextPos);
				detect.update(DetectSetting.MY_ID, nextPos);

				if (numberImage >= MainUtils.MAX_FACE_DATA) {
					addingFace = false;
					if (afterAddFace != null) {
						afterAddFace.run();
					}
				}
			}

		} catch (Exception e) {
			LogUtils.exception(e);
		}
	}

	private void trainingData(final Runnable afterDone) {
		new AsyncTask<Void, Void, Integer>() {
			ProgressDialog dialog = null;

			@Override
			protected void onPreExecute() {
				dialog = new ProgressDialog(TraningActivity.this);
				dialog.setCancelable(false);
				dialog.setCanceledOnTouchOutside(false);
				dialog.setMessage("Trainning data...");
				dialog.show();
				super.onPreExecute();
			}

			@Override
			protected Integer doInBackground(Void... params) {
				return detect.doTrainning();
			}

			@Override
			protected void onPostExecute(Integer result) {
				btnStartTraining.setEnabled(true);
				if (result == -100) {
					Toast.makeText(getApplicationContext(),
							"SDK out of date, please contact with provider",
							Toast.LENGTH_LONG).show();
					return;
				}
				if (result > 0) {
					Toast.makeText(TraningActivity.this,
							R.string.training_success, Toast.LENGTH_LONG)
							.show();
					Editor edit = MainUtils.getSetting(getApplicationContext())
							.edit();
					edit.putBoolean("prefEnable", true);
					edit.commit();
				} else {
					Toast.makeText(TraningActivity.this,
							R.string.training_faild, Toast.LENGTH_LONG).show();
					Editor edit = MainUtils.getSetting(getApplicationContext())
							.edit();
					edit.putBoolean("prefEnable", false);
					edit.commit();
				}

				if (dialog != null && dialog.isShowing()) {
					dialog.dismiss();
					dialog = null;
				}
				if (afterDone != null) {
					runOnUiThread(afterDone);
				}
				super.onPostExecute(result);
				TraningActivity.this.finish();
			}
		}.execute();
	}

	private void clearFaceData(final Runnable afterDone) {
		new AsyncTask<Void, Void, Void>() {
			ProgressDialog dialog = null;

			@Override
			protected void onPreExecute() {
				dialog = new ProgressDialog(TraningActivity.this);
				dialog.setCancelable(false);
				dialog.setCanceledOnTouchOutside(false);
				dialog.setMessage(getResources().getString(R.string.clearing));
				dialog.show();
				super.onPreExecute();
			}

			@Override
			protected Void doInBackground(Void... params) {
				FileUtils.deleteRecursive(DetectSetting.PATH_LIST_PERSON);
				FileUtils.deleteRecursive(DetectSetting.PATH_DATA_FACE);
				FileUtils.deleteRecursive(DetectSetting.PATH_DATA_NOSE);
				FileUtils.deleteRecursive(DetectSetting.PATH_LIST_FACE);
				FileUtils.deleteRecursive(DetectSetting.PATH_LIST_NOSE);
				FileUtils.deleteRecursive(DetectSetting.PATH_TEMP_FACE);
				FileUtils.deleteRecursive(DetectSetting.PATH_TEMP_NOSE);
				FileUtils.deleteRecursive(DetectSetting.PATH_TEMP_SAVE);

				String pathImg = String.format(DetectSetting.PATH_IMG,
						DetectSetting.MY_ID);
				FileUtils.deleteRecursive(pathImg);

				numberImage = personUtil
						.getPersonNumberImage(DetectSetting.MY_ID);
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {

				if (dialog != null && dialog.isShowing()) {
					dialog.dismiss();
					dialog = null;
				}

				preview.invalidateTraining(numberImage);
				if (afterDone != null) {
					afterDone.run();
				}
				super.onPostExecute(result);
			}
		}.execute();
	}
}
