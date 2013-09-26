package com.withiter.quhao.activity;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

import com.withiter.quhao.R;
import com.withiter.quhao.adapter.ReservationAdapter;
import com.withiter.quhao.util.QuhaoLog;
import com.withiter.quhao.util.StringUtils;
import com.withiter.quhao.util.http.CommonHTTPRequest;
import com.withiter.quhao.util.tool.ParseJson;
import com.withiter.quhao.util.tool.ProgressDialogUtil;
import com.withiter.quhao.vo.Merchant;
import com.withiter.quhao.vo.ReservationVO;

public class MerchantDetailActivity extends AppStoreActivity {
	private String LOGTAG = MerchantDetailActivity.class.getName();
	private String merchantId;
	private final int UNLOCK_CLICK = 1000;
	private ProgressDialogUtil progress;
	private Merchant merchant;
	private Button btnGetNumber;
	private LinearLayout info;
	private LinearLayout mapLayout;
	private TextView merchantName;
	private ImageView merchantImg;
	private TextView merchantAddress;
	private TextView merchantPhone;
	private TextView merchantTags;
	private TextView merchantDesc;
	private TextView merchantAverageCost;
	private TextView xingjiabi;
	private TextView kouwei;
	private TextView huanjing;
	private TextView fuwu;
	
	private LinearLayout currentNoLayout;
	private LinearLayout critiqueLayout;
	private ListView reservationListView;
	private ReservationAdapter reservationAdapter;

	private Handler merchantUpdateHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 200) {
				super.handleMessage(msg);

				info.findViewById(R.id.loadingbar).setVisibility(View.GONE);
				info.findViewById(R.id.serverdata).setVisibility(View.VISIBLE);

				if (null != MerchantDetailActivity.this.merchant) {
					MerchantDetailActivity.this.merchantName
							.setText(MerchantDetailActivity.this.merchant.name);
					// MerchantDetailActivity.this.merchantImg = (ImageView)
					// info.findViewById(R.id.merchantImg);
					MerchantDetailActivity.this.merchantAddress
							.setText(MerchantDetailActivity.this.merchant.address);
					MerchantDetailActivity.this.merchantPhone
							.setText(MerchantDetailActivity.this.merchant.phone);
					
					MerchantDetailActivity.this.merchantTags
					.setText(MerchantDetailActivity.this.merchant.tags);
					if(StringUtils.isNull(MerchantDetailActivity.this.merchant.tags))
					{
						MerchantDetailActivity.this.merchantTags
						.setText(R.string.no_tags);
					}
					
					MerchantDetailActivity.this.merchantDesc
							.setText(MerchantDetailActivity.this.merchant.description);
					if(StringUtils.isNull(MerchantDetailActivity.this.merchant.description))
					{
						MerchantDetailActivity.this.merchantDesc
						.setText(R.string.no_desc);
					}
					
					MerchantDetailActivity.this.merchantAverageCost
							.setText(MerchantDetailActivity.this.merchant.averageCost);
					MerchantDetailActivity.this.xingjiabi
							.setText(String
									.valueOf(MerchantDetailActivity.this.merchant.xingjiabi));
					MerchantDetailActivity.this.kouwei
							.setText(String
									.valueOf(MerchantDetailActivity.this.merchant.kouwei));
					MerchantDetailActivity.this.fuwu
							.setText(String
									.valueOf(MerchantDetailActivity.this.merchant.fuwu));
					MerchantDetailActivity.this.huanjing
							.setText(String
									.valueOf(MerchantDetailActivity.this.merchant.huanjing));
					critiqueLayout = (LinearLayout) info.findViewById(R.id.critiqueLayout);
					critiqueLayout.setOnClickListener(MerchantDetailActivity.this);
				}
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			}

		}

	};

	private Handler reservationUpdateHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 200) {
				super.handleMessage(msg);

				
				currentNoLayout.setVisibility(View.VISIBLE);
				LinearLayout.LayoutParams reservationsParams = (LayoutParams) reservationListView
						.getLayoutParams();

				// 设置自定义的layout
				
				reservationListView.setLayoutParams(reservationsParams);
				reservationListView.invalidate();
				
				//btnGetNumber.setVisibility(View.GONE); TODO:
				
				reservationListView.setVisibility(View.VISIBLE);
				List<ReservationVO> rvos = (List<ReservationVO>) msg.obj;
				reservationAdapter = new ReservationAdapter(MerchantDetailActivity.this, reservationListView, rvos);
				reservationListView.setAdapter(reservationAdapter);
				reservationAdapter.notifyDataSetChanged();
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			}

		}

	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.merchant_detail);
		super.onCreate(savedInstanceState);

		this.merchantId = getIntent().getStringExtra("merchantId");

		btnGetNumber = (Button) findViewById(R.id.btn_GetNumber);

		btnGetNumber.setOnClickListener(getNumberClickListener());

		LayoutInflater inflater = LayoutInflater.from(this);
		info = (LinearLayout) inflater.inflate(R.layout.poiinfo, null);
		LinearLayout scroll = (LinearLayout) findViewById(R.id.lite_list);

		LayoutParams layoutParams = new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT);

		scroll.addView(info, layoutParams);

		this.mapLayout = (LinearLayout) findViewById(R.id.mapLayout);
		mapLayout.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent(MerchantDetailActivity.this, MerchantLBSActivity.class);
				intent.putExtra("merchantId",
						MerchantDetailActivity.this.merchantId);
				intent.putExtra("merchantName",
						"jiu dian 1");
				startActivity(intent);
				overridePendingTransition(R.anim.main_enter,
						R.anim.main_exit);

			}
		});
		this.merchantName = (TextView) findViewById(R.id.merchantName);
		this.merchantImg = (ImageView) info.findViewById(R.id.merchantImg);
		this.merchantAddress = (TextView) info
				.findViewById(R.id.merchantAddress);
		this.merchantPhone = (TextView) info.findViewById(R.id.merchantPhone);
		this.merchantTags = (TextView) info.findViewById(R.id.merchantTags);
		this.merchantDesc = (TextView) info.findViewById(R.id.description);
		this.merchantAverageCost = (TextView) info
				.findViewById(R.id.merchantAverageCost);
		this.xingjiabi = (TextView) info.findViewById(R.id.xingjiabi);
		this.kouwei = (TextView) info.findViewById(R.id.kouwei);
		this.fuwu = (TextView) info.findViewById(R.id.fuwu);
		this.huanjing = (TextView) info.findViewById(R.id.huanjing);
		currentNoLayout = (LinearLayout) info.findViewById(R.id.currentNoLayout);
		reservationListView = (ListView) info.findViewById(R.id.reservationListView);
		
		//if(QHClientApplication.getInstance().isLogined)
		if(true)
		{
			getCurrentNo();
		}
		
		btnBack.setOnClickListener(goBack(this));
		initView();
	}

	/**
	 * 根据帐号ID，和merchant ID 获取当前的号码， 如果用户已经取号， 则显示当前号码，
	 * 如果没有取号，则不显示
	 */
	private void getCurrentNo()
	{
		Thread currentNoThread = new Thread(paiduiRunnable);
		currentNoThread.start();
	}

	private Runnable paiduiRunnable = new Runnable() {
		@Override
		public void run() {
			try {
				QuhaoLog.v(LOGTAG, "get categorys data form server begin");
				String buf = CommonHTTPRequest.get("getReservations?accountId=51e563feae4d165869fda38c&mid=51efe7d8ae4dca7b4c281754");
						//+ MerchantDetailActivity.this.merchantId);
				if (StringUtils.isNull(buf) || "[]".equals(buf)) {
					unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				} else {
					List<ReservationVO> rvos = ParseJson.getReservations(buf);
					reservationUpdateHandler.obtainMessage(200, rvos)
							.sendToTarget();
				}

			} catch (Exception e) {
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				e.printStackTrace();
			} finally {
				progress.closeProgress();
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
				// if (QHClientApplication.getInstance().isLogined) {
				if (true) {
					Intent intent = new Intent();
					intent.putExtra("merchantId",
							MerchantDetailActivity.this.merchantId);
					intent.setClass(MerchantDetailActivity.this,
							GetNumberActivity.class);
					startActivity(intent);

				} else {
					Intent intent = new Intent(MerchantDetailActivity.this,
							LoginActivity.class);
					intent.putExtra("activityName",
							MerchantDetailActivity.class.getName());
					intent.putExtra("merchantId",
							MerchantDetailActivity.this.merchantId);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
				}
				overridePendingTransition(R.anim.main_enter, R.anim.main_exit);
			}
		};
		return listener;
	}

	private void initView() {
		if (isClick) {
			return;
		}
		isClick = true;

		progress = new ProgressDialogUtil(this, R.string.empty,
				R.string.querying, false);
		progress.showProgress();
		Thread merchantThread = new Thread(merchantDetailRunnable);
		merchantThread.start();

	}

	private Runnable merchantDetailRunnable = new Runnable() {
		@Override
		public void run() {
			try {
				QuhaoLog.v(LOGTAG, "get categorys data form server begin");
				String buf = CommonHTTPRequest.get("merchant?id="
						+ MerchantDetailActivity.this.merchantId);
				if (StringUtils.isNull(buf)) {
					unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				} else {
					merchant = ParseJson.getMerchant(buf);

					merchantUpdateHandler.obtainMessage(200, merchant)
							.sendToTarget();
				}

			} catch (Exception e) {
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				e.printStackTrace();
			} finally {
				progress.closeProgress();
			}
		}
	};

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.critiqueLayout:
			
			Intent intent = new Intent(this, CritiquesActivity.class);
			intent.putExtra("merchantName", this.merchant.name);
			intent.putExtra("merchantId", this.merchant.id);
			startActivity(intent);
			break;
		default:
			break;
		}
		
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return false;
	}
}
