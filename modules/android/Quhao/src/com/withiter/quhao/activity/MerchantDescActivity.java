package com.withiter.quhao.activity;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.withiter.quhao.R;
import com.withiter.quhao.util.StringUtils;

public class MerchantDescActivity extends QuhaoBaseActivity {

	public static boolean backClicked = false;

	private String desc;
	
	private TextView descView;
	
	
	@Override
	public void finish() {
		super.finish();
	}

	@Override
	protected void onResume() {
		backClicked = false;
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.merchant_desc_layout);
		super.onCreate(savedInstanceState);

		desc = getIntent().getStringExtra("merchantDesc");
		descView = (TextView) this.findViewById(R.id.desc);
		
		if(StringUtils.isNotNull(desc))
		{
			descView.setText(desc);
		}
		
		btnBack.setOnClickListener(goBack(this, this.getClass().getName()));
		
		findViewById(R.id.loadingbar).setVisibility(View.GONE);
		findViewById(R.id.scrollViewLayout).setVisibility(View.VISIBLE);
		
	}

	@Override
	public void onClick(View v) {

	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return false;
	}
}
