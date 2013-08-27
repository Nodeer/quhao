package com.withiter.quhao.activity;

import com.withiter.quhao.util.log.QuhaoLog;

import android.app.Activity;
import android.os.Bundle;

public abstract class QuhaoActivity extends Activity {

	private final static String TAG = QuhaoActivity.class.getName();
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		QuhaoLog.i(TAG, "QuhaoActivity onCreate invoked");
		QuhaoLog.i(TAG, "start to init configurations from application.properties");
		
	}
	
}
