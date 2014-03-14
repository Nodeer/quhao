package com.withiter.quhao.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.withiter.quhao.R;
import com.withiter.quhao.util.QuhaoLog;
import com.withiter.quhao.util.StringUtils;
import com.withiter.quhao.util.http.CommonHTTPRequest;
import com.withiter.quhao.util.tool.ParseJson;
import com.withiter.quhao.util.tool.ProgressDialogUtil;
import com.withiter.quhao.util.tool.QuhaoConstant;
import com.withiter.quhao.util.tool.SharedprefUtil;
import com.withiter.quhao.view.SelectSeatNo;
import com.withiter.quhao.vo.Haoma;
import com.withiter.quhao.vo.Merchant;
import com.withiter.quhao.vo.Paidui;
import com.withiter.quhao.vo.ReservationVO;

/**
 * 取号activity
 * 
 * @author Wang Jie Ze
 */
public class GetNumberActivity extends QuhaoBaseActivity {

	private static final String TAG = GetNumberActivity.class.getName();

	/**
	 * 传递过来的merchant ID
	 */
	private String merchantId;

	/**
	 * 传递过来的merchant name
	 */
	private String merchantName;

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
	private Button btnGetNo;
	private TextView beforeYouView;
	private TextView myNumberView;
	private LinearLayout beforeYouLayout;
	private LinearLayout btnGetNumberLayout;
	private LinearLayout myNumberLayout;
	private SelectSeatNo selectSeatNo;
	private String[] seatNos;
	private Haoma haoma;
	private int currentIndex = 0;

	private Paidui currentPaidui;
	private ProgressDialogUtil progress;
	private ReservationVO reservation;

	public static boolean backClicked = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.get_number);
		super.onCreate(savedInstanceState);

		progress = new ProgressDialogUtil(this, R.string.empty, R.string.querying, false);
		merchantId = getIntent().getStringExtra("merchantId");
		merchantName = getIntent().getStringExtra("merchantName");

		// 设置回退
		btnBack.setOnClickListener(goBack(this, this.getClass().getName()));

		// 获取merchant名称组件
		merchantNameView = (TextView) findViewById(R.id.merchantName);
		seatNoView = (TextView) findViewById(R.id.seatNo);
		currentNumberView = (TextView) findViewById(R.id.currentNumber);
		beforeYouView = (TextView) findViewById(R.id.beforeYou);

		beforeYouLayout = (LinearLayout) findViewById(R.id.beforeYouLayout);
		myNumberLayout = (LinearLayout) findViewById(R.id.myNoLayout);
		myNumberView = (TextView) findViewById(R.id.myNumber);
		btnGetNumberLayout = (LinearLayout) findViewById(R.id.btn_GetNumberLayout);
		btnGetNo = (Button) findViewById(R.id.btn_GetNumber);

		
		QuhaoLog.d(TAG, "merchantName:" + merchantName);
		merchantNameView.setText(merchantName);
		GetNumberActivity.this.findViewById(R.id.loadingbar).setVisibility(View.GONE);
		GetNumberActivity.this.findViewById(R.id.getNumberLayout).setVisibility(View.VISIBLE);

		getMerchantInfo();
		getSeatNos();
	}

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
				GetNumberActivity.this.findViewById(R.id.loadingbar).setVisibility(View.GONE);
				GetNumberActivity.this.findViewById(R.id.merchantNameLayout).setVisibility(View.VISIBLE);
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			}
		}
	};

	/**
	 * 根据merchant显示在界面上的handler
	 */
	private Handler currentNoUpdateHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 200) {
				super.handleMessage(msg);

				String currentNo = (String) msg.obj;
				currentNumberView.setText(currentNo);
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);

			}
		}
	};

	/**
	 * 根据merchant显示在界面上的handler
	 */
	private Handler getNoUpdateHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 200) {
				super.handleMessage(msg);

				seatNoView.setCompoundDrawables(null, null, null, null);
				seatNoView.setClickable(false);
				seatNoView.setText(reservation.seatNumber);
				currentNumberView.setText(reservation.currentNumber);
				btnGetNumberLayout.setVisibility(View.GONE);
				myNumberLayout.setVisibility(View.VISIBLE);
				myNumberView.setText(reservation.myNumber);
				beforeYouLayout.setVisibility(View.VISIBLE);
				LinearLayout nahaoSuccessTipLayout = (LinearLayout)findViewById(R.id.nahaoSuccessTipLayout);
				nahaoSuccessTipLayout.setVisibility(View.VISIBLE);
				TextView nahaosuccessTipView = (TextView) findViewById(R.id.nahao_success_tip);
				if(Integer.parseInt(reservation.beforeYou)<5)
				{
					nahaosuccessTipView.setText(R.string.nahao_success_tip_5_less);
				}
				else
				{
					nahaosuccessTipView.setText(R.string.nahao_success_tip_5_more);
				}
				
				
				beforeYouView.setText(reservation.beforeYou);
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
				if (null != haoma && null != haoma.paiduiList && haoma.paiduiList.size() > 0) {

					seatNos = new String[haoma.paiduiList.size()];
					for (int j = 0; j < haoma.paiduiList.size(); j++) {
						seatNos[j] = haoma.paiduiList.get(j).seatNo;
					}

					if (null != currentPaidui) {
						for (int i = 0; i < haoma.paiduiList.size(); i++) {
							if (currentPaidui.seatNo.equals(haoma.paiduiList.get(i).seatNo)) {
								currentIndex = i;
								currentPaidui = haoma.paiduiList.get(i);
								// currentNo = String
								// .valueOf(currentPaidui.currentNumber);
								break;
							}
						}
					} else {
						// currentIndex = 0;
						QuhaoLog.v(TAG, "currentIndex : " + currentIndex);
						QuhaoLog.v(TAG, "haoma.paiduiList.size : " + haoma.paiduiList.size());
						currentPaidui = haoma.paiduiList.get(currentIndex);
						// currentNo =
						// String.valueOf(currentPaidui.currentNumber);
					}

					seatNoView.setText(currentPaidui.seatNo);
					currentNumberView.setText(String.valueOf(currentPaidui.currentNumber));
					seatNoView.setOnClickListener(GetNumberActivity.this);

					seatNoView.addTextChangedListener(new TextWatcher() {
						@Override
						public void onTextChanged(CharSequence s, int start, int before, int count) {
						}

						@Override
						public void beforeTextChanged(CharSequence s, int start, int count, int after) {
						}

						@Override
						public void afterTextChanged(Editable s) {
							String str = String.valueOf(seatNoView.getText());

							for (int j = 0; j < seatNos.length; j++) {
								if (seatNos[j].equals(str)) {
									currentIndex = j;
									currentPaidui = haoma.paiduiList.get(currentIndex);
									// currentNo = String
									// .valueOf(currentPaidui.currentNumber);
									Thread getCurrentNoThread = new Thread(getCurrentNoRunnable);
									getCurrentNoThread.start();
									// getSeatNos();
									// currentNumberView.setText(String.valueOf(currentPaidui.currentNumber));
								}
							}

						}
					});
					btnGetNo.setOnClickListener(GetNumberActivity.this);
				} else {
					// TODO : 没有位置时， 该怎么做， 应该返回到列表页面， 在酒店详细信息页面应该判断
					Toast.makeText(GetNumberActivity.this, "此酒店没有座位了，请选择其他酒店。", Toast.LENGTH_LONG).show();
					GetNumberActivity.this.finish();
				}

				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			}
		}
	};

	/***
	 * 获取merchant信息的线程
	 */
	private Runnable getNoRunnable = new Runnable() {

		@Override
		public void run() {
			try {
				Looper.prepare();
				QuhaoLog.v(TAG, "get seat numbers data form server begin");
				String accountId = SharedprefUtil.get(GetNumberActivity.this, QuhaoConstant.ACCOUNT_ID, "");
				String buf = CommonHTTPRequest.get("nahao?accountId=" + accountId + "&mid=" + merchantId + "&seatNumber=" + currentPaidui.seatNo);
				// + GetNumberActivity.this.merchantId);
				if (StringUtils.isNull(buf)) {
					Toast.makeText(GetNumberActivity.this, "当前网络异常，请重新拿号。", Toast.LENGTH_LONG).show();
					unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				} else {
					// String currentNo = buf;
					reservation = ParseJson.getReservation(buf);
					if ("NO_MORE_JIFEN".equalsIgnoreCase(reservation.tipValue)) {
						Toast.makeText(GetNumberActivity.this, "您没有更多的积分了..", Toast.LENGTH_LONG).show();
						GetNumberActivity.this.finish();
						return;
					}
					else
					{
						getNoUpdateHandler.obtainMessage(200, reservation).sendToTarget();
					}
				}
			} catch (Exception e) {
				Toast.makeText(GetNumberActivity.this, "当前网络异常，请重新拿号。", Toast.LENGTH_LONG).show();
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				e.printStackTrace();
			} finally {
				progress.closeProgress();
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				Looper.loop();
			}
		}
	};

	/***
	 * 获取merchant信息的线程
	 */
	private Runnable getCurrentNoRunnable = new Runnable() {

		@Override
		public void run() {
			try {
				Looper.prepare();
				QuhaoLog.v(TAG, "get seat numbers data form server begin");
				String buf = CommonHTTPRequest.get("getCurrentNo?id=" + merchantId + "&seatNo=" + currentPaidui.seatNo); 
				// + GetNumberActivity.this.merchantId);
				if (StringUtils.isNull(buf)) {
					unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				} else {
					// String currentNo = buf;

					currentNoUpdateHandler.obtainMessage(200, buf).sendToTarget();
				}
			} catch (Exception e) {
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				e.printStackTrace();
			}
			finally
			{
				Looper.loop();
			}
		}
	};

	/**
	 * 
	 * get seat numbers by merchant ID from server
	 * 
	 */
	private void getSeatNos() {
		progress.showProgress();
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
				Looper.prepare();
				QuhaoLog.v(TAG, "get seat numbers data form server begin, the merchantId is : " + merchantId);
				String buf = CommonHTTPRequest.get("quhao?id=" + merchantId);
				QuhaoLog.v(TAG, "get seat numbers data form server begin, buf is :" + buf);
				// + GetNumberActivity.this.merchantId);
				if (StringUtils.isNull(buf)) {

					unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				} else {
					haoma = ParseJson.getHaoma(buf);
					QuhaoLog.v(TAG, "parse from json, haoma.paiduiList.size() is : " + haoma.paiduiList.size());
					seatNosUpdateHandler.obtainMessage(200, haoma).sendToTarget();
				}
			} catch (Exception e) {

				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				e.printStackTrace();
			} finally {
				progress.closeProgress();
				Looper.loop();
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
				Looper.prepare();
				QuhaoLog.v(TAG, "get merchant data form server begin");
				String buf = CommonHTTPRequest.get("merchant?id=" + GetNumberActivity.this.merchantId);
				if (StringUtils.isNull(buf)) {
					unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				} else {
					merchant = ParseJson.getMerchant(buf);

					merchantUpdateHandler.obtainMessage(200, merchant).sendToTarget();
				}

			} catch (Exception e) {
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				e.printStackTrace();
			}
			finally
			{
				Looper.loop();
			}
		}
	};

	@Override
	public void onClick(View v) {
		// 已经点过，直接返回
		if (isClick) {
			return;
		}

		// 设置已点击标志，避免快速重复点击
		isClick = true;
		switch (v.getId()) {
		case R.id.seatNo:
			String str = String.valueOf(seatNoView.getText());
			for (int i = 0; i < seatNos.length; i++) {
				if (str == seatNos[i]) {
					currentIndex = i;
					currentPaidui = haoma.paiduiList.get(currentIndex);
					// currentNo = String.valueOf(currentPaidui.currentNumber);
				}
			}
			selectSeatNo = new SelectSeatNo(GetNumberActivity.this, seatNos, currentIndex);
			selectSeatNo.showAtLocation(GetNumberActivity.this.findViewById(R.id.root), Gravity.BOTTOM, 0, 0);
			unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			break;

		case R.id.btn_GetNumber:
			progress.showProgress();
			Thread getNoThread = new Thread(getNoRunnable);
			getNoThread.start();
			break;
		default:
			break;
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return false;
	}

	@Override
	protected void onResume() {
		backClicked = false;
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.i(TAG, "backClicked: " + backClicked);
		if (backClicked) {
			overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
		}
	}
}
