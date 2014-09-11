package com.vinisoft.facesdk.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.text.Html;
import android.util.Log;
import android.widget.Toast;

import com.vinisoft.facesdk.demo.R;
import com.vinisoft.facesdk.detect.Detect;
import com.vinisoft.facesdk.utils.MainUtils;

/**
 * IMPORTANT NOTE<br>
 * 1)Remember copy all files in asset folder of Sample Project to your Project.
 * 
 * TODO this SDK is valid to end of each year. If SDK out of date, please
 * download new version and update your application or contact with SDK
 * provider.
 * */
public class SettingActivity extends PreferenceActivity implements
		OnPreferenceChangeListener, OnPreferenceClickListener {

	private boolean isTrainOK = false;

	private Detect detect;

	private Preference prefTraining;
	private Preference prefTest;
	private Preference prefAbout;
	private ListPreference prefLevel;
	private Preference prefLicense;
	private Preference prefVote;
	private AboutDialog mDialog;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_setting);
		addPreferencesFromResource(R.xml.setting);

		detect = Detect.getInstance(this);

		init();
	}

	@Override
	protected void onResume() {
		initData();
		super.onResume();
	}

	@SuppressWarnings("deprecation")
	private void init() {
		//

		prefTraining = (Preference) findPreference("prefTraining");
		prefTest = (Preference) findPreference("prefTest");
		prefAbout = (Preference) findPreference("prefAbout");
		prefLevel = (ListPreference) findPreference("prefLevel");
		prefLicense = (Preference) findPreference("prefLicense");
		prefVote = (Preference) findPreference("prefVote");

		prefTraining.setOnPreferenceClickListener(this);
		prefTest.setOnPreferenceClickListener(this);
		prefLevel.setOnPreferenceChangeListener(this);
		prefAbout.setOnPreferenceClickListener(this);
		prefLicense.setOnPreferenceClickListener(this);
		prefVote.setOnPreferenceClickListener(this);

		prefVote.setTitle(Html.fromHtml(getResources().getString(
				R.string.pref_vote_title)));

		mDialog = new AboutDialog(this);
	}

	private void initData() {
		isTrainOK = MainUtils.isTrained();

		if (isTrainOK) {
			prefTraining.setSummary(Html.fromHtml(getResources().getString(
					R.string.pref_summary_complete)));
		}

		setThreshold(prefLevel.getValue());

	}

	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		String key = preference.getKey();
		if (key.equals(prefLevel.getKey())) {
			setThreshold((String) newValue);
			return true;

		}
		return false;
	}

	@Override
	public boolean onPreferenceClick(Preference preference) {
		String key = preference.getKey();

		if (key.equals(prefTraining.getKey())) {

			Intent i = new Intent(this, TraningActivity.class);
			startActivity(i);

			return true;

		} else if (key.equals(prefAbout.getKey())) {

			mDialog.show();

		} else if (key.equals(prefTest.getKey())) {

			if (isTrainOK) {
				Intent i = new Intent(this, RecognizeActivity.class);
				startActivity(i);

			} else {
				Toast.makeText(getApplicationContext(),
						"Please train before recognize", Toast.LENGTH_LONG)
						.show();
			}

		}

		else if (key.equals(prefLicense.getKey())) {
			Intent license = new Intent(this, LicenseActivity.class);
			startActivity(license);

		} else if (key.equals(prefVote.getKey())) {

			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(Uri
					.parse("market://details?id=com.vinisoft.opencv.applock.facelock"));
			startActivity(intent);
		}

		return false;
	}

	private void setThreshold(String value) {
		int threshold = Integer.parseInt(value);
		detect.setThreshold(threshold);
	}

	@Override
	protected void onPause() {
		try {
			initData();
		} catch (Exception e) {
			Log.e("Setting ", "error reinit data");
		}
		super.onPause();
	}
}