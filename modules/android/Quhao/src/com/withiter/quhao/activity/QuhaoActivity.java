package com.withiter.quhao.activity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources.NotFoundException;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;

import com.withiter.quhao.R;
import com.withiter.quhao.util.QuhaoLog;
import com.withiter.quhao.util.tool.PhoneTool;
import com.withiter.quhao.util.tool.QuhaoConstant;

public abstract class QuhaoActivity extends Activity {

	private final static String TAG = QuhaoActivity.class.getName();
	private static boolean inited = false;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try {
			super.onCreate(savedInstanceState);
			QuhaoLog.i(TAG, "QuhaoActivity onCreate invoked");
			QuhaoLog.i(TAG, "QuhaoActivity already inited : " + inited);
			if (!inited) {
				initConfig();
				inited = true;
			}

		} catch (NotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@SuppressLint("NewApi")
	private void initConfig() throws IOException {
		// Get the value of test from AndroidManifest.xml
		try {
			ApplicationInfo appInfo = this.getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
			boolean msg = appInfo.metaData.getBoolean("test");
			QuhaoConstant.test = msg;
			QuhaoLog.i(TAG, "current deployment is test mode : " + msg);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		// Get the screen size
		Display display = getWindowManager().getDefaultDisplay();

		int currentapiVersion = android.os.Build.VERSION.SDK_INT;
		Point size = new Point();
		int width = 0;
		int height = 0;
		if (currentapiVersion >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
			// Do something for API 17 only (4.2)
			// getRealSize()
			display.getRealSize(size);
			width = size.x;
			height = size.y;
		} else if (currentapiVersion >= android.os.Build.VERSION_CODES.HONEYCOMB_MR2) {
			// Do something for API 13 and above , but below API 17 (API 17 will
			// trigger the above block
			// getSize()
			display.getSize(size);
			width = size.x;
			height = size.y;
		} else {
			// do something for phones running an SDK before Android 3.2 (API
			// 13)
			// getWidth(), getHeight()
			width = display.getWidth();
			height = display.getHeight();
		}
		PhoneTool.setScreenWidth(width);
		PhoneTool.setScreenHeight(height);
		QuhaoLog.i(TAG, "device's screen width is: " + width);
		QuhaoLog.i(TAG, "device's screen height is: " + height);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		super.onPause();
	}
}
