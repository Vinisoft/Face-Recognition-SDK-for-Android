package com.vinisoft.facesdk.activities;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;

import com.vinisoft.facesdk.demo.R;

/**
 * IMPORTANT NOTE<br>
 * 1)Remember copy all files in asset folder of Sample Project to your Project.
 * 
 * TODO this SDK is valid to end of each year. If SDK out of date, please
 * download new version and update your application or contact with SDK
 * provider.
 * */
public class AboutDialog extends Dialog {

	public AboutDialog(Context context) {
		super(context);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_about);
		setCanceledOnTouchOutside(false);

	}

}
