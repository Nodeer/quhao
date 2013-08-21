package com.withiter.quhao.activity;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;

import com.withiter.quhao.R;

public class PersonCenterActivity extends AppStoreActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.person_center_layout);
		super.onCreate(savedInstanceState);

		btnPerson.setOnClickListener(goPersonCenterListener(this));
		btnMarchent.setOnClickListener(getMarchentListListener(this));
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
