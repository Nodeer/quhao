package com.withiter.quhao.activity;

import com.withiter.quhao.R;

import android.os.Bundle;
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.get_number);
		super.onCreate(savedInstanceState);
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
