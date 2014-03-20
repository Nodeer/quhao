package com.withiter.quhao.activity;

import java.util.Calendar;
import java.util.List;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.withiter.quhao.QHClientApplication;
import com.withiter.quhao.R;
import com.withiter.quhao.adapter.ReservationAdapter;
import com.withiter.quhao.util.AsyncImageLoader;
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

public class MerchantDetailActivity extends QuhaoBaseActivity {

	private String LOGTAG = MerchantDetailActivity.class.getName();

	private String merchantId;
	private String mName;

	private final int UNLOCK_CLICK = 1000;
	private Merchant merchant;
	private Button btnGetNumber;
	private Button btnOpen;
	 private Button btnAttention;
	private LinearLayout info;
	private LinearLayout mapLayout;
	private TextView merchantName;
	private ImageView merchantImg;
	private TextView merchantAddress;
	private TextView merchantPhone;
	private TextView merchantBusinessTime;
	private TextView merchantDesc;
	private TextView merchantAverageCost;
	private TextView xingjiabi;
	private TextView kouwei;
	private TextView huanjing;
	private TextView fuwu;

	private LinearLayout currentQuHaoLayout;
	private LinearLayout critiqueLayout;
	private ListView reservationListView;
	private ReservationAdapter reservationAdapter;
	private List<ReservationVO> rvos;
	
	private LinearLayout paiduiConditionLayout;
	private TextView seatNoView;
	private TextView currentNumberView;
	private SelectSeatNo selectSeatNo;
	private String[] seatNos;
	private Haoma haoma;
	private int currentIndex = 0;
	private Paidui currentPaidui;
	private ProgressDialogUtil progressSeatNos;
	
	public static boolean backClicked = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.merchant_detail);
		super.onCreate(savedInstanceState);

		btnBack.setOnClickListener(goBack(this, this.getClass().getName()));

		this.merchantId = getIntent().getStringExtra("merchantId");
		
		btnGetNumber = (Button) findViewById(R.id.btn_GetNumber);
		btnGetNumber.setOnClickListener(getNumberClickListener());
		
		btnOpen = (Button) findViewById(R.id.btn_open);
		btnOpen.setOnClickListener(this);
		LayoutInflater inflater = LayoutInflater.from(this);
		info = (LinearLayout) inflater.inflate(R.layout.merchant_detail_info, null);
		LinearLayout scroll = (LinearLayout) findViewById(R.id.lite_list);
		LayoutParams layoutParams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);

		scroll.addView(info, layoutParams);

		this.mapLayout = (LinearLayout) findViewById(R.id.mapLayout);

		this.merchantImg = (ImageView) info.findViewById(R.id.merchantImg);
		this.merchantAddress = (TextView) info.findViewById(R.id.merchantAddress);
		this.merchantPhone = (TextView) info.findViewById(R.id.merchantPhone);
		
		this.merchantName = (TextView) info.findViewById(R.id.merchant_detail_merchantName);
		this.btnAttention = (Button) info.findViewById(R.id.btn_attention);
		
		btnAttention.setOnClickListener(this);
		
		if(!QHClientApplication.getInstance().isLogined)
		{
			btnAttention.setVisibility(View.GONE);
		}
		else
		{
			btnAttention.setVisibility(View.VISIBLE);
		}
		
		this.merchantPhone.setClickable(true);
		this.merchantPhone.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 取得输入的电话号码串
				String phoneNO = merchantPhone.getText().toString();
				// 如果输入不为空创建打电话的Intent
				if (StringUtils.isNotNull(phoneNO)) {
					Intent phoneIntent = new Intent("android.intent.action.CALL", Uri.parse("tel:" + phoneNO));
					startActivity(phoneIntent);
				} else {
					Toast.makeText(MerchantDetailActivity.this, "此商家还未添加联系方式", Toast.LENGTH_LONG).show();
				}
			}
		});

		this.merchantBusinessTime = (TextView) info.findViewById(R.id.merchantBusinessTime);
		this.merchantDesc = (TextView) info.findViewById(R.id.description);
		this.merchantAverageCost = (TextView) info.findViewById(R.id.merchant_details_AverageCost);
		this.xingjiabi = (TextView) info.findViewById(R.id.xingjiabi);
		this.kouwei = (TextView) info.findViewById(R.id.kouwei);
		this.fuwu = (TextView) info.findViewById(R.id.fuwu);
		this.huanjing = (TextView) info.findViewById(R.id.huanjing);

		currentQuHaoLayout = (LinearLayout) info.findViewById(R.id.currentQuHaoLayout);
		reservationListView = (ListView) info.findViewById(R.id.reservationListView);

		paiduiConditionLayout = (LinearLayout) info.findViewById(R.id.paidui_condition_layout);
		seatNoView = (TextView) info.findViewById(R.id.seatNo);
		currentNumberView = (TextView) info.findViewById(R.id.currentNumber);
		initView();
	}
	
	private Handler paiduiConditionLayoutHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 200) {
				super.handleMessage(msg);
				
				paiduiConditionLayout.setVisibility(View.VISIBLE);
			}

		}

	};
	
	private Handler currentQuHaoLayoutHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 200) {
				super.handleMessage(msg);
				currentQuHaoLayout.setVisibility(View.GONE);
			}

		}

	};

	/**
	 * 根据帐号ID，和merchant ID 获取当前的号码， 如果用户已经取号， 则显示当前号码， 如果没有取号，则不显示
	 */
	private void getCurrentNo() {
		Thread currentNoThread = new Thread(paiduiRunnable);
		currentNoThread.start();
	}

	private Runnable paiduiRunnable = new Runnable() {
		@Override
		public void run() {
			try {
				Looper.prepare();
				QuhaoLog.d(LOGTAG, "start to load paidui information from server side");
				String accountId = QHClientApplication.getInstance().accountInfo.accountId;
				String buf = CommonHTTPRequest.get("getReservations?accountId=" + accountId + "&mid=" + merchantId);
				if (StringUtils.isNull(buf) || "[]".equals(buf)) {
					
					paiduiConditionLayoutHandler.sendEmptyMessage(200);
					currentQuHaoLayoutHandler.sendEmptyMessage(200);
					Thread getSeatNosThread = new Thread(getSeatNosRunnable);
					getSeatNosThread.start();
				} else {
					rvos = ParseJson.getReservations(buf);
					reservationUpdateHandler.obtainMessage(200, rvos).sendToTarget();
				}

			} catch (Exception e) {
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				e.printStackTrace();
			} finally {
				progressDialogUtil.closeProgress();
				Looper.loop();
			}
		}
	};

	/**
	 * 
	 * 取号按钮的 click listener
	 * 
	 * @return 取号按钮的listener R.id.btn_GetNumber
	 */
	private OnClickListener getNumberClickListener() {
		OnClickListener listener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (QHClientApplication.getInstance().isLogined) {
					Intent intent = new Intent();
					intent.putExtra("merchantId", merchantId);
					intent.putExtra("merchantName", mName);
					intent.setClass(MerchantDetailActivity.this, GetNumberActivity.class);
					startActivity(intent);

				} else {
					Intent intent = new Intent(MerchantDetailActivity.this, LoginActivity.class);
					intent.putExtra("activityName", MerchantDetailActivity.class.getName());
					intent.putExtra("merchantId", MerchantDetailActivity.this.merchantId);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
				}
//				overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
			}
		};
		return listener;
	}

	private void initView() {
		if (isClick) {
			return;
		}
		isClick = true;
		
		Thread merchantThread = new Thread(merchantDetailRunnable);
		merchantThread.start();
		
	}

	private Runnable getSeatNosRunnable = new Runnable() {
		
		@Override
		public void run() {
			try {
				Looper.prepare();
				progressSeatNos = new ProgressDialogUtil(MerchantDetailActivity.this, R.string.empty, R.string.querying, false);
				progressSeatNos.showProgress();
				QuhaoLog.v(LOGTAG, "get seat numbers data form server begin, the merchantId is : " + merchantId);
				String buf = CommonHTTPRequest.get("quhao?id=" + merchantId);
				QuhaoLog.v(LOGTAG, "get seat numbers data form server begin, buf is :" + buf);
				// + GetNumberActivity.this.merchantId);
				if (StringUtils.isNull(buf)) {
					paiduiConditionLayoutHandler.sendEmptyMessage(200);
					progressSeatNos.closeProgress();
					unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				} else {
					haoma = ParseJson.getHaoma(buf);
					QuhaoLog.v(LOGTAG, "parse from json, haoma.paiduiList.size() is : " + haoma.paiduiList.size());
					seatNosUpdateHandler.obtainMessage(200, haoma).sendToTarget();
				}
			} catch (Exception e) {
				progressSeatNos.closeProgress();
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				e.printStackTrace();
			} finally {
				
				Looper.loop();
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
				paiduiConditionLayout.setVisibility(View.VISIBLE);
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
						QuhaoLog.v(LOGTAG, "currentIndex : " + currentIndex);
						QuhaoLog.v(LOGTAG, "haoma.paiduiList.size : " + haoma.paiduiList.size());
						currentPaidui = haoma.paiduiList.get(currentIndex);
						// currentNo =
						// String.valueOf(currentPaidui.currentNumber);
					}

					seatNoView.setText(currentPaidui.seatNo);
					currentNumberView.setText(String.valueOf(currentPaidui.currentNumber));
					seatNoView.setOnClickListener(MerchantDetailActivity.this);

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
					
				} else {
					// TODO : 没有位置时， 该怎么做， 应该返回到列表页面， 在酒店详细信息页面应该判断
					Toast.makeText(MerchantDetailActivity.this, "此酒店没有座位，请选择其他酒店。", Toast.LENGTH_LONG).show();
					paiduiConditionLayout.setVisibility(View.GONE);
				}
				
				progressSeatNos.closeProgress();
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
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
				QuhaoLog.v(LOGTAG, "get seat numbers data form server begin");
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
	
	private Runnable merchantDetailRunnable = new Runnable() {
		@Override
		public void run() {
			try {
				Looper.prepare();
				progressDialogUtil = new ProgressDialogUtil(MerchantDetailActivity.this, R.string.empty, R.string.querying, false);
				progressDialogUtil.showProgress();
				QuhaoLog.v(LOGTAG, "get merchant details form server begin");
				String accountId = SharedprefUtil.get(MerchantDetailActivity.this, QuhaoConstant.ACCOUNT_ID, "");
				QuhaoLog.v(LOGTAG, "MerchantDetailActivity.this.merchantId : " + merchantId + ",account ID : " + accountId);
				String buf = CommonHTTPRequest.get("merchantNew?id=" + merchantId + "&accountId=" + accountId);
				if (StringUtils.isNull(buf)) {
					progressDialogUtil.closeProgress();
					paiduiConditionLayoutHandler.sendEmptyMessage(200);
					currentQuHaoLayoutHandler.sendEmptyMessage(200);
					unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				} else {
					merchant = ParseJson.getMerchant(buf);
					merchantUpdateHandler.obtainMessage(200, merchant).sendToTarget();
				}

			} catch (Exception e) {
				progressDialogUtil.closeProgress();
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				e.printStackTrace();
			} finally {
				Looper.loop();
			}
		}
	};

	
	
	private Handler merchantUpdateHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 200) {
				super.handleMessage(msg);

				info.findViewById(R.id.loadingbar).setVisibility(View.GONE);
				info.findViewById(R.id.serverdata).setVisibility(View.VISIBLE);

				if (null != merchant) {

					Merchant m = merchant;
					
					// if merchant has no image, set no_logo as default
					if(StringUtils.isNull(m.merchantImage)){
						merchantImg.setImageResource(R.drawable.no_logo);
					}
					
					// get image from memory/SDCard/URL stream
					new Thread(new Runnable() {
						@Override
						public void run() {
							AsyncImageLoader asynImageLoader = new AsyncImageLoader();
							Drawable drawable = asynImageLoader.loadDrawable(merchant.merchantImage);
							if (drawable != null) {
								// update merchant image
								updateMerchantImageHandler.obtainMessage(0, drawable).sendToTarget();
							}
							else
							{
								drawable = getResources().getDrawable(R.drawable.no_logo);
								updateMerchantImageHandler.obtainMessage(0, drawable).sendToTarget();
							}
						}
					}).start();

					mName = m.name;
					merchantName.setText(m.name);
					merchantAddress.setText(m.address);
					merchantPhone.setText(m.phone);
					merchantBusinessTime.setText(m.openTime + "~" + m.closeTime);

					merchantDesc.setText(m.description);
					if (StringUtils.isNull(m.description)) {
						merchantDesc.setText(R.string.no_desc);
					}

					merchantAverageCost.setText(m.averageCost);
					xingjiabi.setText(String.valueOf(m.xingjiabi));
					kouwei.setText(String.valueOf(m.kouwei));
					fuwu.setText(String.valueOf(m.fuwu));
					huanjing.setText(String.valueOf(m.huanjing));

					// comment layout
					critiqueLayout = (LinearLayout) info.findViewById(R.id.critiqueLayout);
					QuhaoLog.i(LOGTAG, m.commentAverageCost);
					QuhaoLog.i(LOGTAG, m.commentContent);
					QuhaoLog.i(LOGTAG, m.commentDate);
					QuhaoLog.i(LOGTAG, m.commentFuwu);
					QuhaoLog.i(LOGTAG, m.commentHuanjing);
					QuhaoLog.i(LOGTAG, m.commentKouwei);
					QuhaoLog.i(LOGTAG, m.commentXingjiabi);

					TextView commentRenjun = (TextView) critiqueLayout.findViewById(R.id.comment_renjun);
					TextView commentFuwu = (TextView) critiqueLayout.findViewById(R.id.comment_fuwu);
					TextView commentHuanjing = (TextView) critiqueLayout.findViewById(R.id.comment_huanjing);
					TextView commentKouwei = (TextView) critiqueLayout.findViewById(R.id.comment_kouwei);
					TextView commentXingjiabi = (TextView) critiqueLayout.findViewById(R.id.comment_xingjiabi);
					TextView commentContent = (TextView) critiqueLayout.findViewById(R.id.comment_content);
					TextView commentDate = (TextView) info.findViewById(R.id.comment_date);

					commentRenjun.setText(getString(R.string.renjun) + "  " +m.commentAverageCost);
					commentFuwu.setText(getString(R.string.fuwu_4_space) + "  " + String.valueOf(m.commentFuwu));
					commentHuanjing.setText(getString(R.string.huanjing_4_space) + "  " + String.valueOf(m.commentHuanjing));
					commentKouwei.setText(getString(R.string.kouwei_4_space) + "  " + String.valueOf(m.commentKouwei));
					commentXingjiabi.setText(getString(R.string.xingjiabi) + "  " + String.valueOf(m.commentXingjiabi));
					commentContent.setText(m.commentContent);
					commentDate.setText(m.commentDate);

					mapLayout.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							Intent intent = new Intent(MerchantDetailActivity.this, MerchantLBSActivity.class);
							intent.putExtra("merchantId", merchantId);
							intent.putExtra("merchantName", merchant.name);
							startActivity(intent);
							overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
						}
					});

					critiqueLayout.setOnClickListener(MerchantDetailActivity.this);
					
					
					// check the merchant is enabled
					if (m.enable) {
						Calendar cal = Calendar.getInstance();
						int currentHour = cal.get(Calendar.HOUR_OF_DAY);
						int openHour = 25;
						
						if(StringUtils.isNotNull(m.openTime))
						{
							openHour = Integer.valueOf(m.openTime.substring(0, m.openTime.indexOf(":")));
						}
						
						int closeHour = 26;
						if(StringUtils.isNotNull(m.closeTime))
						{
							closeHour = Integer.valueOf(m.closeTime.substring(0, m.closeTime.indexOf(":")));
						}
						if(currentHour<openHour || currentHour>closeHour)
						{
							btnGetNumber.setVisibility(View.GONE);
						}
						else
						{
							btnGetNumber.setVisibility(View.VISIBLE);
						}
						
						btnOpen.setVisibility(View.GONE);
						
						QuhaoLog.d(LOGTAG, "check login state on MerchantDetailActivity, isLogined : " + QHClientApplication.getInstance().isLogined);
						if(!QHClientApplication.getInstance().isLogined)
						{
							btnAttention.setVisibility(View.GONE);
							paiduiConditionLayout.setVisibility(View.VISIBLE);
							currentQuHaoLayout.setVisibility(View.GONE);
							Thread getSeatNosThread = new Thread(getSeatNosRunnable);
							getSeatNosThread.start();
						}
						else
						{
							btnAttention.setVisibility(View.VISIBLE);
							if(m.isAttention)
							{
								btnAttention.setText(R.string.cancel_attention);
							}
							else
							{
								btnAttention.setText(R.string.attention);
							}
							currentQuHaoLayout.setVisibility(View.VISIBLE);
							getCurrentNo();
						}
					} else {
						btnGetNumber.setVisibility(View.GONE);
						btnOpen.setVisibility(View.VISIBLE);
						paiduiConditionLayout.setVisibility(View.GONE);
						currentQuHaoLayout.setVisibility(View.GONE);
						progressDialogUtil.closeProgress();
					}

				}
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			}
		}
	};

	private Handler updateMerchantImageHandler = new Handler(){
		public void handleMessage(Message msg) {
			if(msg.what == 0){
				
				merchantImg.setImageDrawable((Drawable) msg.obj);
			}
		};
	};
	
	private Handler reservationUpdateHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 200) {
				super.handleMessage(msg);
				
				currentQuHaoLayout.setVisibility(View.VISIBLE);
				LinearLayout.LayoutParams reservationsParams = (LayoutParams) reservationListView.getLayoutParams();

				// 设置自定义的layout

				reservationListView.setLayoutParams(reservationsParams);
				reservationListView.invalidate();

				btnGetNumber.setVisibility(View.GONE);

				reservationListView.setVisibility(View.VISIBLE);
				reservationAdapter = new ReservationAdapter(MerchantDetailActivity.this, reservationListView, rvos);
				reservationListView.setAdapter(reservationAdapter);
				reservationAdapter.notifyDataSetChanged();
				
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			}

		}

	};
	
	private Handler attentionHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 200) {
				super.handleMessage(msg);
				String buf = String.valueOf(msg.obj);
				if (StringUtils.isNull(buf)) {
					unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
					Toast.makeText(MerchantDetailActivity.this, R.string.committing_failed, Toast.LENGTH_LONG).show();
					if(merchant.isAttention)
					{
						btnAttention.setText(R.string.cancel_attention);
					}
					else
					{
						btnAttention.setText(R.string.attention);
					}
					
				} else {
					if("success".equals(buf))
					{
						unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
						Toast.makeText(MerchantDetailActivity.this, R.string.committing_success, Toast.LENGTH_LONG).show();
						if(merchant.isAttention)
						{
							btnAttention.setText(R.string.attention);
							merchant.isAttention = false;
						}
						else
						{
							btnAttention.setText(R.string.cancel_attention);
							merchant.isAttention = true;
						}
					}
					else
					{
						unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
						Toast.makeText(MerchantDetailActivity.this, R.string.committing_failed, Toast.LENGTH_LONG).show();
						if(merchant.isAttention)
						{
							btnAttention.setText(R.string.cancel_attention);
						}
						else
						{
							btnAttention.setText(R.string.attention);
						}
					}
					
				}
				progressDialogUtil.closeProgress();
			}

		}

	};

	@Override
	public void onClick(View v) {
		
		if(isClick)
		{
			return;
		}
		isClick = true;
		switch (v.getId()) {
		case R.id.critiqueLayout:
			if(StringUtils.isNotNull(this.merchant.commentContent))
			{
				QuhaoLog.d("", "the commentContent : " + this.merchant.commentContent);
				Intent intent = new Intent(this, CommentsMerchantActivity.class);
				intent.putExtra("merchantName", this.merchant.name);
				intent.putExtra("merchantId", this.merchant.id);
				
				if(null != rvos && rvos.size()>0)
				{
					intent.putExtra("rId", rvos.get(0).rId);
				}
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				startActivity(intent);
			}
			else
			{
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				Toast.makeText(this, "对不起，暂无评论。", Toast.LENGTH_LONG).show();
			}
			break;
		case R.id.seatNo:
			String str = String.valueOf(seatNoView.getText());
			for (int i = 0; i < seatNos.length; i++) {
				if (str == seatNos[i]) {
					currentIndex = i;
					currentPaidui = haoma.paiduiList.get(currentIndex);
					// currentNo = String.valueOf(currentPaidui.currentNumber);
				}
			}
			selectSeatNo = new SelectSeatNo(MerchantDetailActivity.this, seatNos, currentIndex);
			selectSeatNo.showAtLocation(MerchantDetailActivity.this.findViewById(R.id.root), Gravity.BOTTOM, 0, 0);
			unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			break;
		case R.id.btn_open:
			
			Thread thread = new Thread(new Runnable() {
				
				@Override
				public void run() {
					Looper.prepare();
					progressDialogUtil = new ProgressDialogUtil(MerchantDetailActivity.this, R.string.empty, R.string.waitting_for_commit, false);
					progressDialogUtil.showProgress();
					
					String accountId = SharedprefUtil.get(MerchantDetailActivity.this, QuhaoConstant.ACCOUNT_ID, "");
					String merchantId = merchant.id;
					
					try {
						QuhaoLog.v(LOGTAG, "commit open service, account id  : " + accountId + " , merchant ID : " + merchantId);
						String buf = CommonHTTPRequest.get("openService?mid=" + merchantId + "&accountId=" + accountId);
						if (StringUtils.isNull(buf)) {
							unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
							Toast.makeText(MerchantDetailActivity.this, R.string.committing_failed, Toast.LENGTH_LONG).show();
							
						} else {
							if("success".equals(buf))
							{
								unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
								Toast.makeText(MerchantDetailActivity.this, R.string.committing_success, Toast.LENGTH_LONG).show();
							}
							else
							{
								unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
								Toast.makeText(MerchantDetailActivity.this, R.string.committing_failed, Toast.LENGTH_LONG).show();
							}
							
						}
						progressDialogUtil.closeProgress();

					} catch (Exception e) {
						progressDialogUtil.closeProgress();
						Toast.makeText(MerchantDetailActivity.this, R.string.committing_failed, Toast.LENGTH_LONG).show();
						unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
						e.printStackTrace();
					} finally {
						Looper.loop();
					}
					
				}
			});
			thread.start();
			break;
			case R.id.btn_attention:
			Thread thread1 = new Thread(new Runnable() {
				
				@Override
				public void run() {
					Looper.prepare();
					progressDialogUtil = new ProgressDialogUtil(MerchantDetailActivity.this, R.string.empty, R.string.waitting_for_commit, false);
					progressDialogUtil.showProgress();
					
					String accountId = SharedprefUtil.get(MerchantDetailActivity.this, QuhaoConstant.ACCOUNT_ID, "");
					String merchantId = merchant.id;
					int flag = 0;
					if(!merchant.isAttention)
					{
						flag = 1;
					}
					
					try {
						QuhaoLog.v(LOGTAG, "pay attention to merchant, account id  : " + accountId + " , merchant ID : " + merchantId + ",flag : " + flag);
						String buf = CommonHTTPRequest.get("updateAttention?mid=" + merchantId + "&accountId=" + accountId + "&flag=" + flag);
						attentionHandler.obtainMessage(200, buf).sendToTarget();
						

					} catch (Exception e) {
						progressDialogUtil.closeProgress();
						Toast.makeText(MerchantDetailActivity.this, R.string.committing_failed, Toast.LENGTH_LONG).show();
						unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
						e.printStackTrace();
					} finally {
						Looper.loop();
					}
					
				}
			});
			thread1.start();
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
	public void finish() {
		if(null != progressDialogUtil)
		{
			progressDialogUtil.closeProgress();
		}
		if(null != progressSeatNos)
		{
			progressSeatNos.closeProgress();
		}
		super.finish();
		QuhaoLog.i(LOGTAG, LOGTAG + " finished");
	}

	@Override
	protected void onResume() {
		backClicked = false;
		initView();
		super.onResume();
	}

	@Override
	public void onPause() {
		if(null != progressDialogUtil)
		{
			progressDialogUtil.closeProgress();
		}
		if(null != progressSeatNos)
		{
			progressSeatNos.closeProgress();
		}
		super.onPause();
		QuhaoLog.i(LOGTAG, LOGTAG + " on pause");
		if (backClicked) {
			overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
		}
	}
	
	@Override
	protected void onStop() {
		
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		
		super.onDestroy();
	}
}
