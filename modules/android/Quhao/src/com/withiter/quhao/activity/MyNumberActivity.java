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
	
	private TextView successTipView;
	
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
		this.successTipView = (TextView) this.findViewById(R.id.success_tip_msg);
		
		seatNoView.setText(data.getSeatNumber());
		myNoView.setText(data.getMyNumber());
		beforeYouView.setText(data.getBeforeYou());
		nextNoView.setText(data.getCurrentNumber());
		
		if(Integer.parseInt(data.getBeforeYou())<5)
		{
//			String html="<html><head><title></title></head><body>恭喜，<font color=\"red\">取号成功！</font>在你前面排队的不多于5桌，为了避免排队号码过期，请抓紧时间前往商家。"  
//	                +"</body></html>";  
//	          
//			successTipView.setMovementMethod(ScrollingMovementMethod.getInstance());//滚动  
//			successTipView.setText(Html.fromHtml(html));      
			successTipView.setText("恭喜，取号成功！在你前面排队的不多于5桌，为了避免排队号码过期，请抓紧时间前往商家。");
		}
		else
		{
//			String html="<html><head><title></title></head><body>恭喜，<font color=\"#aabb00\">取号成功！</font>当你的排号前还剩5位时，我们会用短信通知到你，继续享受你的免排队时间吧。"  
//	                +"</body></html>";  
//	          
//			successTipView.setMovementMethod(ScrollingMovementMethod.getInstance());//滚动  
//			successTipView.setText(Html.fromHtml(html));     
			
			successTipView.setText("恭喜，取号成功！当你的排号前还剩5位时，我们会用短信通知到你，继续享受你的免排队时间吧。");
		}
	}

	@Override
	public void onClick(View v) {

	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return false;
	}
}
