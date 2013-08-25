package com.withiter.quhao.activity;

import com.withiter.quhao.R;

import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;

/***
 * 取号activity
 * 
 * @author ASUS
 * 
 */
public class GetNumberActivity extends AppStoreActivity {

	private static final String LOG_TAG = GetNumberActivity.class.getName();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.get_number);
		super.onCreate(savedInstanceState);


		String merchantId = getIntent().getStringExtra("merchantId");
		btnBack.setOnClickListener(goBack(this));
		
		Log.d(LOG_TAG, merchantId);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		return false;
	}

}
