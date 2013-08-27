package com.withiter.quhao.activity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.app.Activity;
import android.content.res.Resources.NotFoundException;
import android.os.Bundle;

import com.withiter.quhao.R;
import com.withiter.quhao.util.log.QuhaoLog;
import com.withiter.quhao.util.tool.QuhaoConstant;

public abstract class QuhaoActivity extends Activity {

	private final static String TAG = QuhaoActivity.class.getName();

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
			QuhaoLog.i(TAG,
					"start to init configurations from application.properties");
			InputStream input = getResources().openRawResource(
					R.raw.application);
			BufferedReader read = new BufferedReader(new InputStreamReader(
					input));
			String line = "";
			while ((line = read.readLine()) != null) {
				if (line.contains("app.server")) {
					QuhaoConstant.HTTP_URL = line.split("=")[1];
					QuhaoLog.i(TAG, "server url is : " + QuhaoConstant.HTTP_URL);
				}
			}
		} catch (NotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
