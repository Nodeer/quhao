package com.withiter.quhao.activity;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.withiter.quhao.R;
import com.withiter.quhao.util.ActivityUtil;
import com.withiter.quhao.util.QuhaoLog;

public class AboutUsActivity extends QuhaoBaseActivity {
	
	public static boolean backClicked = false;
	private String LOGTAG = AboutUsActivity.class.getName();

	@Override
	public void finish() {
		super.finish();
		QuhaoLog.i(LOGTAG, LOGTAG + " finished");
	}

	@Override
	protected void onResume() {
		backClicked = false;
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
		QuhaoLog.i(LOGTAG, LOGTAG + " on pause");
		if (backClicked) {
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.more_about_us);
		super.onCreate(savedInstanceState);
		btnBack.setOnClickListener(goBack(this,this.getClass().getName()));
		TextView version = (TextView) findViewById(R.id.about_us_version);
		version.setText(ActivityUtil.getVersionName(this));

	}

	@Override
	public void onClick(View v) {

	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return false;
	}

}
