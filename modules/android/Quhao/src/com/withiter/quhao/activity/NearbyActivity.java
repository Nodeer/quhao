package com.withiter.quhao.activity;

import com.withiter.quhao.R;

import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;

public class NearbyActivity extends AppStoreActivity {

	private static final String TAG = NearbyActivity.class.getName();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
//		setContentView(R.layout.merchants_search);
		super.onCreate(savedInstanceState);
		Log.i(TAG, "NearbyActivity is displayed");
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
