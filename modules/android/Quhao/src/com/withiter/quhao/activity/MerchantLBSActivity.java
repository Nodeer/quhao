package com.withiter.quhao.activity;

import java.util.ArrayList;
import java.util.List;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.withiter.quhao.R;
import com.withiter.quhao.util.QuhaoLog;
import com.withiter.quhao.util.StringUtils;
import com.withiter.quhao.util.http.CommonHTTPRequest;
import com.withiter.quhao.vo.MerchantLocation;

public class MerchantLBSActivity extends AppStoreActivity
{
	
	private static final String TAG = MerchantLBSActivity.class.getName();

	private Button btnBack;
	
	private TextView merchantNameView;
	
	private String merchantName;
	
	private String merchantId;
	
	private List<MerchantLocation> locations;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.merchant_lbs_map);
		
		
		this.merchantName = this.getIntent().getStringExtra("merchantName");
		this.merchantId = this.getIntent().getStringExtra("merchantId");
		
		merchantNameView = (TextView) findViewById(R.id.name);
		merchantNameView.setText(merchantName);
		init();
	}



	private void init()
	{
		btnBack = (Button) findViewById(R.id.back_btn);
		btnBack.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				MerchantLBSActivity.this.finish();
				
			}
		});
		
		
	}
	
	private Runnable getLocationRunnable = new Runnable()
	{
		
		@Override
		public void run()
		{
			try {
				QuhaoLog.v(TAG, "get categorys data form server begin");
				String buf = CommonHTTPRequest.get("getReservations?accountId=51e563feae4d165869fda38c&mid=51efe7d8ae4dca7b4c281754");
						//+ MerchantDetailActivity.this.merchantId);
				if (StringUtils.isNull(buf) && "[]".equals(buf)) {
				} else {
					//List<ReservationVO> rvos = ParseJson.getReservations(buf);
					locations = new ArrayList<MerchantLocation>();
					MerchantLocation location = new MerchantLocation("51e563feae4d165869fda38c", "name111", 31.678109, 31.678109, "address22");
					locations.add(location);
					getLocationUpdateHandler.obtainMessage(200, locations)
							.sendToTarget();
				}

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
			}
		}
	};
	
	private Handler getLocationUpdateHandler = new Handler(){

		@Override
		public void handleMessage(Message msg)
		{
			if (msg.what == 200) {
				super.handleMessage(msg);

				Drawable marker = getResources()
						.getDrawable(R.drawable.ic_address_big);
				marker.setBounds(0, 0, marker.getIntrinsicWidth(),
						marker.getIntrinsicHeight());
			}

		}
		
	};

	private void myLocation() {}

	@Override
	public void finish() {
		super.finish();
		//overridePendingTransition(R.anim.left_slide_in, R.anim.left_slide_out);
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
