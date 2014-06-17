package com.withiter.quhao.activity;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ListView;

import com.withiter.quhao.R;
import com.withiter.quhao.data.ReservationData;
import com.withiter.quhao.util.QuhaoLog;

public class MyNumberActivity extends QuhaoBaseActivity {

	private String LOGTAG = MyNumberActivity.class.getName();

	private ReservationData data;
	@Override
	public void finish() {
		super.finish();
		QuhaoLog.i(LOGTAG, LOGTAG + " finished");
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.my_number_layout);
		super.onCreate(savedInstanceState);

		btnBack.setOnClickListener(goBack(this, this.getClass().getName()));
		
		this.data = getIntent().getParcelableExtra("rvo");
		
	}

	@Override
	public void onClick(View v) {

	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return false;
	}
}
