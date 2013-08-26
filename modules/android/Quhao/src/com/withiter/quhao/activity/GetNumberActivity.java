package com.withiter.quhao.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.withiter.quhao.R;
import com.withiter.quhao.util.http.CommonHTTPRequest;
import com.withiter.quhao.util.tool.CommonTool;
import com.withiter.quhao.util.tool.ParseJson;
import com.withiter.quhao.view.SelectSeatNo;
import com.withiter.quhao.vo.Merchant;

/***
 * 取号activity
 * 
 * @author Wang Jie Ze
 * 
 */
public class GetNumberActivity extends AppStoreActivity {

	private static final String LOG_TAG = GetNumberActivity.class.getName();
	
	/**
	 * 传递过来的merchant ID
	 */
	private String merchantId;
	
	/**
	 * 根据传递的merchant ID 从服务器查询的数据
	 */
	private Merchant merchant;
	
	/**
	 * merchant 名字组件
	 */
	private TextView merchantName;
	
	private TextView seatNo;
	
	private Button btnSeatNo;
	
	private SelectSeatNo selectSeatNo;
	
	/**
	 * 根据merchant显示在界面上的handler
	 */
	private Handler merchantUpdateHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 200) {
				super.handleMessage(msg);

				if (null != GetNumberActivity.this.merchant) {
					merchantName.setText(GetNumberActivity.this.merchant.name);
				}
				GetNumberActivity.this.findViewById(R.id.loadingbar).setVisibility(View.GONE);
				GetNumberActivity.this.findViewById(R.id.merchantNameLayout).setVisibility(View.VISIBLE);
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			}

		}

	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.get_number);
		super.onCreate(savedInstanceState);

		merchantId = getIntent().getStringExtra("merchantId");
		
		// 设置回退
		btnBack.setOnClickListener(goBack(this));
		
		//获取merchant名称组件
		merchantName = (TextView) findViewById(R.id.merchantName);
		seatNo = (TextView) findViewById(R.id.seatNo);
		btnSeatNo = (Button) findViewById(R.id.btn_seatNo);
		btnSeatNo.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				selectSeatNo = new SelectSeatNo(GetNumberActivity.this);
				selectSeatNo.showAtLocation(GetNumberActivity.this.findViewById(R.id.root), Gravity.BOTTOM, 0, 0);
				
			}
		});
		getMerchantInfo();
		
	}

	/***
	 * 获取merchant信息
	 */
	private void getMerchantInfo()
	{
		Thread merchantThread = new Thread(merchantDetailRunnable);
		merchantThread.start();
		
	}

	/***
	 * 获取merchant信息的线程
	 */
	private Runnable merchantDetailRunnable = new Runnable()
	{
		
		@Override
		public void run()
		{
			try {
				Log.v(LOG_TAG, "get merchant data form server begin");
				String buf = CommonHTTPRequest.get("merchant?id="
						+ GetNumberActivity.this.merchantId);
				if(CommonTool.isNull(buf))
				{
					unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				}
				else
				{
					merchant = ParseJson.getMerchant(buf);

					merchantUpdateHandler.obtainMessage(200, merchant)
							.sendToTarget();
				}
				

			} catch (Exception e) {
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				e.printStackTrace();
			}
		}
	};
	
	@Override
	public void onClick(View v) {
		

	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		
		return false;
	}

}
