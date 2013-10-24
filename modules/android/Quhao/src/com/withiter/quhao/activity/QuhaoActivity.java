package com.withiter.quhao.activity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
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
	public static String uid = "";
	public static boolean autoLogin = false;
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
			if (!inited) {
				initAll();
				inited = true;
			}
			
		} catch (NotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void initAll() throws IOException {
		initConfig();
		initLogin();
	}

	@SuppressLint("NewApi")
	private void initConfig() throws IOException {
		QuhaoLog.i(TAG,
				"start to init configurations from application.properties");
		InputStream input = getResources().openRawResource(R.raw.application);
		BufferedReader read = new BufferedReader(new InputStreamReader(input));
		String line = "";
		while ((line = read.readLine()) != null) {
			if (line.contains("app.server")) {
				QuhaoConstant.HTTP_URL = line.split("=")[1];
				QuhaoLog.i(TAG, "server url is : " + QuhaoConstant.HTTP_URL);
			}
		}

		// Get the value of test from AndroidManifest.xml
		try {
			ApplicationInfo appInfo = this.getPackageManager().getApplicationInfo(
					getPackageName(), PackageManager.GET_META_DATA);
			boolean msg = appInfo.metaData.getBoolean("test");
			QuhaoConstant.test = msg;
			QuhaoLog.i(TAG, "current deployment is test mode : " + msg);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		
		// Get the screen size
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int width = size.x;
		int height = size.y;
		PhoneTool.setScreenWidth(width);
		PhoneTool.setScreenHeight(height);
	}

	private void initLogin() {
		SharedPreferences settings = getSharedPreferences(
				QuhaoConstant.SHARED_PREFERENCES, 0);
		String uidStr = settings.getString("uid", "");
		String autoLogin = settings.getString("autoLogin", "false");
		if (uidStr.length() > 0) {
			QuhaoActivity.uid = uidStr;
			QuhaoActivity.autoLogin = Boolean.valueOf(autoLogin);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();

		// SharedPreferences prefs = getSharedPreferences("X", MODE_PRIVATE);
		// Editor editor = prefs.edit();
		// editor.putString("lastActivity", getClass().getName());
		// editor.commit();
	}
}
