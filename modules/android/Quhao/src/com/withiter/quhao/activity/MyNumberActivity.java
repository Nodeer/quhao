package com.withiter.quhao.activity;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.withiter.quhao.R;
import com.withiter.quhao.data.ReservationData;

public class MyNumberActivity extends QuhaoBaseActivity {

	private ReservationData data;
	
	private TextView seatNoView;
	
	private TextView myNoView;
	
	private TextView beforeYouView; 
	
	private TextView nextNoView;
	
	@Override
	public void finish() {
		super.finish();
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
		
		this.seatNoView = (TextView) this.findViewById(R.id.rvo_seat_no);
		this.myNoView = (TextView) this.findViewById(R.id.rvo_my_number);
		this.beforeYouView = (TextView) this.findViewById(R.id.rvo_before_you);
		this.nextNoView = (TextView) this.findViewById(R.id.rvo_next_number);
		
		seatNoView.setText(data.getSeatNumber());
		myNoView.setText(data.getMyNumber());
		beforeYouView.setText(data.getBeforeYou());
		nextNoView.setText(data.getCurrentNumber());
	}

	@Override
	public void onClick(View v) {

	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return false;
	}
}
