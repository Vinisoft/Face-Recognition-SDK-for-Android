package com.vinisoft.facesdk.activities;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

/**
 * IMPORTANT NOTE<br>
 * 1)Remember copy all files in asset folder of Sample Project to your Project.
 * 
 * TODO this SDK is valid to end of each year. If SDK out of date, please
 * download new version and update your application or contact with SDK
 * provider.
 * */
public class LicenseActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		final WebView webv = new WebView(this);

		webv.loadUrl("file:///android_asset/contents_use_policy.html");
		setContentView(webv);
	}

}
