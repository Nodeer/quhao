package com.withiter.quhao.activity;

import com.withiter.quhao.R;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

public class PersonCenterActivity extends AppStoreActivity
{

	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		setContentView(R.layout.person_center_layout);
		super.onCreate(savedInstanceState);
		btnPerson.setOnClickListener(goPersonCenterListener(this));
		btnMarchent.setOnClickListener(getMarchentListListener(this));
	}

	@Override
	public void HttpClientCallBack(String buf)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(View v)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onTouch(View v, MotionEvent event)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void HttpCallBack(String buf)
	{
		// TODO Auto-generated method stub

	}

}
