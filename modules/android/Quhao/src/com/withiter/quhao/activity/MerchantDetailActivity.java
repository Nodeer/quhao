package com.withiter.quhao.activity;

import java.util.List;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
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
import android.widget.Toast;

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
import com.withiter.quhao.vo.Merchant;
import com.withiter.quhao.vo.ReservationVO;

public class MerchantDetailActivity extends QuhaoBaseActivity {

	private String LOGTAG = MerchantDetailActivity.class.getName();

	private String merchantId;
	private String mName;

	private final int UNLOCK_CLICK = 1000;
	private ProgressDialogUtil progress;
	private Merchant merchant;
	private Button btnGetNumber;
	// private Button btnLogin;
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

	private LinearLayout currentNoLayout;
	private LinearLayout critiqueLayout;
	private ListView reservationListView;
	private ReservationAdapter reservationAdapter;

	public static boolean backClicked = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.merchant_detail);
		super.onCreate(savedInstanceState);

		this.merchantId = getIntent().getStringExtra("merchantId");
		this.merchantName = (TextView) findViewById(R.id.merchant_detail_merchantName);
		btnBack.setOnClickListener(goBack(this, this.getClass().getName()));
		btnGetNumber = (Button) findViewById(R.id.btn_GetNumber);

		btnGetNumber.setOnClickListener(getNumberClickListener());
		LayoutInflater inflater = LayoutInflater.from(this);
		info = (LinearLayout) inflater.inflate(R.layout.merchant_detail_info, null);
		LinearLayout scroll = (LinearLayout) findViewById(R.id.lite_list);
		LayoutParams layoutParams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);

		scroll.addView(info, layoutParams);

		this.mapLayout = (LinearLayout) findViewById(R.id.mapLayout);

		this.merchantImg = (ImageView) info.findViewById(R.id.merchantImg);
		this.merchantAddress = (TextView) info.findViewById(R.id.merchantAddress);
		this.merchantPhone = (TextView) info.findViewById(R.id.merchantPhone);

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

		currentNoLayout = (LinearLayout) info.findViewById(R.id.currentNoLayout);
		reservationListView = (ListView) info.findViewById(R.id.reservationListView);

		initView();
	}

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
				QuhaoLog.v(LOGTAG, "get categorys data form server begin");
				String accountId = SharedprefUtil.get(MerchantDetailActivity.this, QuhaoConstant.ACCOUNT_ID, "false");
				String buf = CommonHTTPRequest.get("getReservations?accountId=" + accountId + "&mid=" + merchantId);
				if (StringUtils.isNull(buf) || "[]".equals(buf)) {
					unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				} else {
					List<ReservationVO> rvos = ParseJson.getReservations(buf);
					reservationUpdateHandler.obtainMessage(200, rvos).sendToTarget();
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

				// TODO add login check
				String isLogin = SharedprefUtil.get(MerchantDetailActivity.this, QuhaoConstant.IS_LOGIN, "false");
				if ("true".equalsIgnoreCase(isLogin)) {
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
				overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
			}
		};
		return listener;
	}

	private void initView() {
		if (isClick) {
			return;
		}
		isClick = true;
		progress = new ProgressDialogUtil(this, R.string.empty, R.string.querying, false);
		progress.showProgress();
		Thread merchantThread = new Thread(merchantDetailRunnable);
		merchantThread.start();

	}

	private Runnable merchantDetailRunnable = new Runnable() {
		@Override
		public void run() {
			try {
				QuhaoLog.v(LOGTAG, "get merchant details form server begin");
				QuhaoLog.v(LOGTAG, "MerchantDetailActivity.this.merchantId : " + merchantId);
				String buf = CommonHTTPRequest.get("merchant?id=" + merchantId);
				if (StringUtils.isNull(buf)) {
					unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				} else {
					merchant = ParseJson.getMerchant(buf);
					merchantUpdateHandler.obtainMessage(200, merchant).sendToTarget();
				}

			} catch (Exception e) {
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				e.printStackTrace();
			} finally {
				progress.closeProgress();
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
					AsyncImageLoader asynImageLoader = new AsyncImageLoader();
					Drawable drawable = asynImageLoader.loadDrawable(merchant.merchantImage);
					if (drawable != null) {
						merchantImg.setImageDrawable(drawable);
					}

					// check the merchant is enabled and autoLogin is true
					if (m.enable) {
						btnGetNumber.setVisibility(View.VISIBLE);
					} else {
						btnGetNumber.setVisibility(View.GONE);
					}

					merchantName.setText(m.name);
					mName = m.name;
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

					commentRenjun.setText(commentRenjun.getText() + m.commentAverageCost);
					commentFuwu.setText(commentFuwu.getText() + String.valueOf(m.commentFuwu));
					commentHuanjing.setText(commentHuanjing.getText() + String.valueOf(m.commentHuanjing));
					commentKouwei.setText(commentKouwei.getText() + String.valueOf(m.commentKouwei));
					commentXingjiabi.setText(commentXingjiabi.getText() + String.valueOf(m.commentXingjiabi));
					commentContent.setText(m.commentContent);
					commentDate.setText(m.commentDate);

					mapLayout.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							Intent intent = new Intent(MerchantDetailActivity.this, MerchantLBSActivity.class);
							intent.putExtra("merchantId", merchantId);
							intent.putExtra("merchantName", merchant.name);
							startActivity(intent);
							overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
						}
					});

					String isLogin = SharedprefUtil.get(MerchantDetailActivity.this, QuhaoConstant.IS_LOGIN, "false");
					if ("true".equalsIgnoreCase(isLogin)) {
						getCurrentNo();
					}
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
				LinearLayout.LayoutParams reservationsParams = (LayoutParams) reservationListView.getLayoutParams();

				// 设置自定义的layout

				reservationListView.setLayoutParams(reservationsParams);
				reservationListView.invalidate();

				btnGetNumber.setVisibility(View.GONE);

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
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.critiqueLayout:
			if(StringUtils.isNotNull(this.merchant.commentContent))
			{
				QuhaoLog.d("", "the commentContent : " + this.merchant.commentContent);
				Intent intent = new Intent(this, CommentsActivity.class);
				intent.putExtra("merchantName", this.merchant.name);
				intent.putExtra("merchantId", this.merchant.id);
				startActivity(intent);
			}
			else
			{
				Toast.makeText(this, "对不起，暂无评论。", Toast.LENGTH_LONG).show();
			}
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
		super.finish();
		QuhaoLog.i(LOGTAG, LOGTAG + " finished");
	}

	@Override
	protected void onResume() {
		backClicked = false;
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
		QuhaoLog.i(LOGTAG, LOGTAG + " on pause");
		if (backClicked) {
			overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
		}
	}

}
