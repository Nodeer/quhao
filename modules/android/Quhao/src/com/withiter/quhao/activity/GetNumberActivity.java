package com.withiter.quhao.activity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.withiter.quhao.R;
import com.withiter.quhao.util.QuhaoLog;
import com.withiter.quhao.util.http.CommonHTTPRequest;
import com.withiter.quhao.util.tool.CommonTool;
import com.withiter.quhao.util.tool.ParseJson;
import com.withiter.quhao.view.SelectSeatNo;
import com.withiter.quhao.vo.Haoma;
import com.withiter.quhao.vo.Merchant;
import com.withiter.quhao.vo.Paidui;

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
	private TextView merchantNameView;
	private TextView seatNoView;
	private TextView currentNumberView;
	private Button btnSeatNo;
	private SelectSeatNo selectSeatNo;
	private String[] seatNos;
	private Haoma haoma;
	private int currentIndex = 0;

	private Paidui currentPaidui;
	
	/**
	 * 根据merchant显示在界面上的handler
	 */
	private Handler merchantUpdateHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 200) {
				super.handleMessage(msg);

				if (null != GetNumberActivity.this.merchant) {
					merchantNameView.setText(GetNumberActivity.this.merchant.name);
				}
				GetNumberActivity.this.findViewById(R.id.loadingbar)
						.setVisibility(View.GONE);
				GetNumberActivity.this.findViewById(R.id.merchantNameLayout)
						.setVisibility(View.VISIBLE);
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			}
		}
	};

	/**
	 * 根据seat numbers 显示在界面上的handler
	 */
	private Handler seatNosUpdateHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 200) {
				super.handleMessage(msg);
				if (null != haoma) {
					
					seatNos = new String[haoma.paiduiList.size()];
					for (int j = 0; j < haoma.paiduiList.size(); j++)
					{
						seatNos[j] = haoma.paiduiList.get(j).seatNo;
					}
					
					btnSeatNo.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							String str = String.valueOf(seatNoView.getText());
							for (int i = 0; i < seatNos.length; i++)
							{
								if(str == seatNos[i])
								{
									currentIndex = i;
									currentPaidui = haoma.paiduiList.get(currentIndex);
								}
							}
							selectSeatNo = new SelectSeatNo(GetNumberActivity.this,seatNos, currentIndex);
							selectSeatNo.showAtLocation(
									GetNumberActivity.this.findViewById(R.id.root),
									Gravity.BOTTOM, 0, 0);
						}
					});
					
					seatNoView.addTextChangedListener(new TextWatcher()
					{
						
						@Override
						public void onTextChanged(CharSequence s, int start, int before, int count)
						{
							
							
						}
						
						@Override
						public void beforeTextChanged(CharSequence s, int start, int count,
								int after)
						{
							
							
						}
						
						@Override
						public void afterTextChanged(Editable s)
						{
							String str = String.valueOf(seatNoView.getText());
							
							for (int j = 0; j < seatNos.length; j++)
							{
								if(seatNos[j].equals(str))
								{
									currentIndex = j;
									currentPaidui = haoma.paiduiList.get(currentIndex);
									currentNumberView.setText(String.valueOf(currentPaidui.currentNumber));
								}
							}
							
							
						}
					});
				}

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

		// 获取merchant名称组件
		merchantNameView = (TextView) findViewById(R.id.merchantName);
		seatNoView = (TextView) findViewById(R.id.seatNo);
		currentNumberView = (TextView) findViewById(R.id.currentNumber);
		btnSeatNo = (Button) findViewById(R.id.btn_seatNo);
		
		getMerchantInfo();
		getSeatNos();
	}

	/**
	 * 
	 * get seat numbers by merchant ID from server
	 * 
	 */
	private void getSeatNos() {
		Thread merchantThread = new Thread(getSeatNosRunnable);
		merchantThread.start();

	}

	/***
	 * 获取merchant信息的线程
	 */
	private Runnable getSeatNosRunnable = new Runnable() {

		@Override
		public void run() {
			try {
				QuhaoLog.v(LOG_TAG, "get seat numbers data form server begin");
				String buf = CommonHTTPRequest
						.get("quhao?id=51efe7d8ae4dca7b4c281754");
				// + GetNumberActivity.this.merchantId);
				if (CommonTool.isNull(buf)) {
					unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				} else {
					haoma = ParseJson.getHaoma(buf);

					seatNosUpdateHandler.obtainMessage(200, haoma)
							.sendToTarget();
				}
			} catch (Exception e) {
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				e.printStackTrace();
			}
		}
	};

	/***
	 * 获取merchant信息
	 */
	private void getMerchantInfo() {
		Thread merchantThread = new Thread(merchantDetailRunnable);
		merchantThread.start();

	}

	/***
	 * 获取merchant信息的线程
	 */
	private Runnable merchantDetailRunnable = new Runnable() {

		@Override
		public void run() {
			try {
				QuhaoLog.v(LOG_TAG, "get merchant data form server begin");
				String buf = CommonHTTPRequest.get("merchant?id="
						+ GetNumberActivity.this.merchantId);
				if (CommonTool.isNull(buf)) {
					unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				} else {
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
