package com.withiter.quhao.activity;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;

import com.withiter.quhao.R;
import com.withiter.quhao.util.QuhaoLog;

public class NearbyActivity extends AppStoreActivity {

	private static final String TAG = NearbyActivity.class.getName();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.nearby_layout);
		super.onCreate(savedInstanceState);
		QuhaoLog.i(TAG, "NearbyActivity is displayed");
		
		btnCategory.setOnClickListener(goCategory(this));
		btnNearby.setOnClickListener(goNearby(this));
		btnPerson.setOnClickListener(goPersonCenter(this));
		btnMore.setOnClickListener(goMore(this));
		
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
