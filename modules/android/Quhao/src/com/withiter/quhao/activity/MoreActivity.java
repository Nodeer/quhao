package com.withiter.quhao.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;

public class MoreActivity extends AppStoreActivity {

	private static final String TAG = MoreActivity.class.getName();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "MoreActivity is displayed");
		requestWindowFeature(Window.FEATURE_NO_TITLE);
//		setContentView(R.layout.merchants_search);
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {
		// TODO Auto-generated method stub
		return false;
	}
}
