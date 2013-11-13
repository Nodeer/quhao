package com.withiter.quhao.activity;

import java.util.List;

import com.withiter.quhao.R;
import com.withiter.quhao.util.tool.QuhaoConstant;
import com.withiter.quhao.util.tool.SharedprefUtil;
import com.withiter.quhao.vo.ReservationVO;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

public class PaiduiListActivity extends QuhaoBaseActivity {

	private List<ReservationVO> reservations;
	
	private TextView titleView;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.paidui_list_layout);
		super.onCreate(savedInstanceState);
		
		String queryCondition = this.getIntent().getStringExtra("queryCondition");
		String accountId = SharedprefUtil.get(this, QuhaoConstant.ACCOUNT_ID, "");
		
		titleView = (TextView) findViewById(R.id.title);
		
		btnBack.setOnClickListener(goBack(this));
		
	}
	
	@Override
	public void onClick(View v) {

	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return false;
	}

}
