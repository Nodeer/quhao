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
import com.withiter.quhao.util.tool.SharedprefUtil;

public abstract class QuhaoActivity extends Activity {

	private final static String TAG = QuhaoActivity.class.getName();
	
	// move below to QuhaoBaseActivity class
//	public static String uid = "";
//	public static boolean autoLogin = false;
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
			QuhaoLog.i(TAG, "QuhaoActivity inited : " + inited);
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
		
		int currentapiVersion = android.os.Build.VERSION.SDK_INT;
		Point size = new Point();
		int width = 0;
		int height = 0;
		if (currentapiVersion >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1){
		    //Do something for API 17 only (4.2)
		    //getRealSize()
			display.getRealSize(size);
			width = size.x;
			height = size.y;
		} else if (currentapiVersion >= android.os.Build.VERSION_CODES.HONEYCOMB_MR2){
		    // Do something for API 13 and above , but below API 17 (API 17 will trigger the above block
		    //getSize()
			display.getSize(size);
			width = size.x;
			height = size.y;
		} else{
		    // do something for phones running an SDK before Android 3.2 (API 13)
		    //getWidth(), getHeight()
			width = display.getWidth();
			height = display.getHeight();
		}
		PhoneTool.setScreenWidth(width);
		PhoneTool.setScreenHeight(height);
		QuhaoLog.i(TAG, "device's screen width is: " + width);
		QuhaoLog.i(TAG, "device's screen height is: " + height);
	}

	private void initLogin() {
		SharedPreferences settings = getSharedPreferences(
				QuhaoConstant.SHARED_PREFERENCES, 0);
		String uidStr = settings.getString("uid", "");
		String autoLogin = settings.getString("autoLogin", "false");
		if (uidStr.length() > 0) {
			QuhaoBaseActivity.uid = uidStr;
			QuhaoBaseActivity.autoLogin = Boolean.valueOf(autoLogin);
			if(Boolean.valueOf(autoLogin)){
				// TODO verify the uid and password
//				String isLogin = SharedprefUtil.get(this,QuhaoConstant.IS_LOGIN, "false");
			}
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
