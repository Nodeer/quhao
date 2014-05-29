package com.withiter.quhao.activity;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
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
import com.withiter.quhao.adapter.PaiduiConditionAdapter;
import com.withiter.quhao.adapter.ReservationAdapter;
import com.withiter.quhao.task.GetPaiduiListTask;
import com.withiter.quhao.task.JsonPack;
import com.withiter.quhao.util.ActivityUtil;
import com.withiter.quhao.util.QuhaoLog;
import com.withiter.quhao.util.StringUtils;
import com.withiter.quhao.util.http.CommonHTTPRequest;
import com.withiter.quhao.util.tool.AsynImageLoader;
import com.withiter.quhao.util.tool.ParseJson;
import com.withiter.quhao.util.tool.ProgressDialogUtil;
import com.withiter.quhao.util.tool.QuhaoConstant;
import com.withiter.quhao.util.tool.SharedprefUtil;
import com.withiter.quhao.vo.Haoma;
import com.withiter.quhao.vo.Merchant;
import com.withiter.quhao.vo.MerchantDetailVO;
import com.withiter.quhao.vo.ReservationVO;

public class MerchantDetailActivity extends QuhaoBaseActivity {

	private String LOGTAG = MerchantDetailActivity.class.getName();

	private String merchantId;
	private String mName;

	private final int UNLOCK_CLICK = 1000;
	private Merchant merchant;
	private MerchantDetailVO merchantDetail;
	private Button btnGetNumber;
	private Button btnOpen;
	private TextView openNumView;
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
	private LinearLayout descLayout;
	private ListView reservationListView;
	private TextView reservationListEmpty;
	private ReservationAdapter reservationAdapter;
	private List<ReservationVO> rvos;
	
	private LinearLayout paiduiConditionLayout;
	private Haoma haoma;
	
	private PaiduiConditionAdapter paiduiAdapter;
	
	private ListView paiduiListView;
	
	private TextView paiduiListEmpty;
	
	private Button refershPaiduiBtn;
	
	public static boolean backClicked = false;

	private LinearLayout youhuiLayout;
	
	private TextView youhuiView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.merchant_detail);
		super.onCreate(savedInstanceState);

		btnBack.setOnClickListener(goBack(this, this.getClass().getName()));

		this.merchantId = getIntent().getStringExtra("merchantId");
		
		btnGetNumber = (Button) findViewById(R.id.btn_GetNumber);
		btnGetNumber.setOnClickListener(this);
		
		LayoutInflater inflater = LayoutInflater.from(this);
		info = (LinearLayout) inflater.inflate(R.layout.merchant_detail_info, null);
		LinearLayout scroll = (LinearLayout) findViewById(R.id.lite_list);
		LayoutParams layoutParams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);

		scroll.addView(info, layoutParams);

		this.mapLayout = (LinearLayout) findViewById(R.id.mapLayout);
		mapLayout.setOnClickListener(this);
		this.merchantImg = (ImageView) info.findViewById(R.id.merchantImg);
		this.merchantAddress = (TextView) info.findViewById(R.id.merchantAddress);
		this.merchantPhone = (TextView) info.findViewById(R.id.merchantPhone);
		
		this.merchantName = (TextView) info.findViewById(R.id.merchant_detail_merchantName);
		this.btnAttention = (Button) info.findViewById(R.id.btn_attention);
		
		btnAttention.setOnClickListener(this);
		
		btnOpen = (Button) info.findViewById(R.id.btn_open);
		btnOpen.setOnClickListener(this);
		
		openNumView = (TextView) info.findViewById(R.id.open_number);
		
		this.merchantPhone.setClickable(true);
		this.merchantPhone.setOnClickListener(this);

		this.merchantBusinessTime = (TextView) info.findViewById(R.id.merchantBusinessTime);
		this.descLayout = (LinearLayout) info.findViewById(R.id.desc_layout);
		descLayout.setOnClickListener(this);
		this.merchantDesc = (TextView) info.findViewById(R.id.description);
		this.merchantAverageCost = (TextView) info.findViewById(R.id.merchant_details_AverageCost);
		this.xingjiabi = (TextView) info.findViewById(R.id.xingjiabi);
		this.kouwei = (TextView) info.findViewById(R.id.kouwei);
		this.fuwu = (TextView) info.findViewById(R.id.fuwu);
		this.huanjing = (TextView) info.findViewById(R.id.huanjing);

		currentQuHaoLayout = (LinearLayout) info.findViewById(R.id.currentQuHaoLayout);
		reservationListView = (ListView) info.findViewById(R.id.reservationListView);
		reservationListEmpty = (TextView) info.findViewById(R.id.reservation_list_empty);
		reservationListView.setEmptyView(reservationListEmpty);
		paiduiConditionLayout = (LinearLayout) info.findViewById(R.id.paidui_condition_layout);
		paiduiListView = (ListView) info.findViewById(R.id.paidui_condition_list);
		
		paiduiListEmpty = (TextView) info.findViewById(R.id.paidui_list_empty);
		paiduiListView.setEmptyView(paiduiListEmpty);
		
		refershPaiduiBtn = (Button) info.findViewById(R.id.btn_refresh_paidui);
		refershPaiduiBtn.setOnClickListener(this);
		
		//添加优惠信息栏
		youhuiLayout = (LinearLayout) info.findViewById(R.id.youhui_layout);
		youhuiLayout.setOnClickListener(this);
		youhuiView = (TextView) info.findViewById(R.id.youhui);
		info.findViewById(R.id.loadingbar).setVisibility(View.VISIBLE);
		info.findViewById(R.id.serverdata).setVisibility(View.GONE);
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

	private Runnable merchantDetailRunnable = new Runnable() {
		@Override
		public void run() {
			try {
				Looper.prepare();
				QuhaoLog.v(LOGTAG, "get merchant details form server begin");
				String accountId = SharedprefUtil.get(MerchantDetailActivity.this, QuhaoConstant.ACCOUNT_ID, "");
				QuhaoLog.v(LOGTAG, "MerchantDetailActivity.this.merchantId : " + merchantId + ",account ID : " + accountId);
				if (!ActivityUtil.isNetWorkAvailable(getApplicationContext())) {
					Toast.makeText(getApplicationContext(), R.string.network_error_info, Toast.LENGTH_SHORT).show();
					unlockHandler.sendEmptyMessage(UNLOCK_CLICK);
					info.findViewById(R.id.loadingbar).setVisibility(View.GONE);
					info.findViewById(R.id.serverdata).setVisibility(View.VISIBLE);
					return;
				}
				String buf = CommonHTTPRequest.get("querytMerchantDetail?merchantId=" + merchantId + "&accountId=" + accountId + "&isLogined=" + String.valueOf(QHClientApplication.getInstance().isLogined));
				if (StringUtils.isNull(buf)) {
					info.findViewById(R.id.loadingbar).setVisibility(View.GONE);
					info.findViewById(R.id.serverdata).setVisibility(View.VISIBLE);
					paiduiConditionLayoutHandler.sendEmptyMessage(200);
					currentQuHaoLayoutHandler.sendEmptyMessage(200);
					unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				} else {
					merchantDetail = ParseJson.getMerchantDetail(buf);
					merchantUpdateHandler.obtainMessage(200, merchantDetail).sendToTarget();
				}

			} catch (Exception e) {
				//TODO: wjzwjz 系统异常时，怎么处理
				info.findViewById(R.id.loadingbar).setVisibility(View.GONE);
				info.findViewById(R.id.serverdata).setVisibility(View.VISIBLE);
				paiduiConditionLayoutHandler.sendEmptyMessage(200);
				currentQuHaoLayoutHandler.sendEmptyMessage(200);
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
				if(null != merchantDetail)
				{
					
					merchant = merchantDetail.merchant;
					if (null != merchant) {
	
						Merchant m = merchant;
						
						// if merchant has no image, set no_logo as default
						if(StringUtils.isNull(m.merchantImage)){
							merchantImg.setImageResource(R.drawable.no_logo);
						}
						
						// get image from memory/SDCard/URL stream
						AsynImageLoader.getInstance().showImageAsyn(merchantImg, 0,merchant.merchantImage, R.drawable.no_logo);
						/*
						new Thread(new Runnable() {
							@Override
							public void run() {
								AsynImageLoader.getInstance().showImageAsyn(merchantImg, merchant.merchantImage, R.drawable.no_logo);
								
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
						*/
						mName = m.name;
						merchantName.setText(m.name);
						merchantAddress.setText(m.address);
						if(StringUtils.isNull(m.address))
						{
							merchantAddress.setText("暂无地址");
						}
						merchantPhone.setText(m.phone);
						if (StringUtils.isNull(m.phone)) {
							merchantPhone.setText("暂无");
						}
						merchantBusinessTime.setText(m.openTime + "~" + m.closeTime);
	
						if(StringUtils.isNull(m.openTime) || StringUtils.isNull(m.closeTime))
						{
							merchantBusinessTime.setText("暂无");
						}
						merchantDesc.setText(m.description);
						
						openNumView.setText(String.valueOf(m.openNum));
						
						if (StringUtils.isNull(m.description)) {
							merchantDesc.setText(R.string.no_desc);
						}
	
						if (m.youhuiExist) {
							youhuiView.setText(R.string.check_youhui_list_info);
						}
						else
						{
							youhuiView.setText(R.string.no_youhui_info);
						}
						
						merchantAverageCost.setText(m.averageCost);
						xingjiabi.setText(String.valueOf(m.xingjiabi));
						kouwei.setText(String.valueOf(m.kouwei));
						fuwu.setText(String.valueOf(m.fuwu));
						huanjing.setText(String.valueOf(m.huanjing));
	
						// comment layout
						critiqueLayout = (LinearLayout) info.findViewById(R.id.critiqueLayout);
	
						TextView commentContent = (TextView) critiqueLayout.findViewById(R.id.comment_content);
						TextView commentDate = (TextView) info.findViewById(R.id.comment_date);
	
						commentContent.setText(m.commentContent);
						commentDate.setText(m.commentDate);
	
						critiqueLayout.setOnClickListener(MerchantDetailActivity.this);
						
						btnAttention.setVisibility(View.VISIBLE);
						if(m.isAttention)
						{
							btnAttention.setText(R.string.cancel_attention);
						}
						else
						{
							btnAttention.setText(R.string.attention);
						}
						
						// check the merchant is enabled
						if (m.enable) {
							
							btnOpen.setVisibility(View.GONE);
							if(m.online)
							{
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
							}
							else
							{
								btnGetNumber.setVisibility(View.GONE);
							}
							
							btnOpen.setVisibility(View.GONE);
							openNumView.setVisibility(View.GONE);
							QuhaoLog.d(LOGTAG, "check login state on MerchantDetailActivity, isLogined : " + QHClientApplication.getInstance().isLogined);
							handlerPaidui();
							if(QHClientApplication.getInstance().isLogined)
							{
								currentQuHaoLayout.setVisibility(View.VISIBLE);
								
								reservationListView.setVisibility(View.VISIBLE);
								rvos = merchantDetail.rvos;
								if(null != rvos && !rvos.isEmpty())
								{
									LinearLayout.LayoutParams reservationsParams = (LayoutParams) reservationListView.getLayoutParams();

									// 设置自定义的layout

									reservationListView.setLayoutParams(reservationsParams);
									reservationListView.invalidate();

									btnGetNumber.setVisibility(View.GONE);

									reservationAdapter = new ReservationAdapter(MerchantDetailActivity.this, reservationListView, rvos);
									reservationListView.setAdapter(reservationAdapter);
									reservationAdapter.notifyDataSetChanged();
									reservationListView.getEmptyView().setVisibility(View.GONE);
									unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
								}
								else
								{
									reservationListView.getEmptyView().setVisibility(View.VISIBLE);
								}
								
							}
							else
							{
								reservationListView.getEmptyView().setVisibility(View.GONE);
								reservationListView.setVisibility(View.GONE);
								currentQuHaoLayout.setVisibility(View.GONE);
							}
						} else {
							btnGetNumber.setVisibility(View.GONE);
							btnOpen.setVisibility(View.VISIBLE);
							openNumView.setVisibility(View.VISIBLE);
							paiduiConditionLayout.setVisibility(View.GONE);
							currentQuHaoLayout.setVisibility(View.GONE);
							
						}

					}
					else
					{
						btnGetNumber.setVisibility(View.GONE);
						btnOpen.setVisibility(View.GONE);
						openNumView.setVisibility(View.GONE);
						paiduiConditionLayout.setVisibility(View.GONE);
						btnAttention.setVisibility(View.GONE);
						currentQuHaoLayout.setVisibility(View.GONE);
					}
				}
				else
				{
					btnGetNumber.setVisibility(View.GONE);
					btnOpen.setVisibility(View.GONE);
					openNumView.setVisibility(View.GONE);
					paiduiConditionLayout.setVisibility(View.GONE);
					btnAttention.setVisibility(View.GONE);
					currentQuHaoLayout.setVisibility(View.GONE);
				}
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			}
		}

	};
	
	protected void handlerPaidui() {
//		btnAttention.setVisibility(View.GONE);
//		currentQuHaoLayout.setVisibility(View.GONE);
		
		paiduiConditionLayout.setVisibility(View.VISIBLE);
		
		haoma = merchantDetail.haoma;
		if (null != haoma && null != haoma.paiduiList && haoma.paiduiList.size() > 0) {

			paiduiListView.getEmptyView().setVisibility(View.GONE);
			paiduiAdapter = new PaiduiConditionAdapter(MerchantDetailActivity.this, paiduiListView, haoma.paiduiList);
			paiduiListView.setAdapter(paiduiAdapter);
			
			int totalHeight = 0;    
	        for (int i = 0, len = paiduiAdapter.getCount(); i < len; i++) { //listAdapter.getCount()返回数据项的数目    
		        View listItem = paiduiAdapter.getView(i, null, paiduiListView);    
		        listItem.measure(0, 0); //计算子项View 的宽高    
		        totalHeight += listItem.getMeasuredHeight(); //统计所有子项的总高度    
	        }
	            
	        android.view.ViewGroup.LayoutParams params = paiduiListView.getLayoutParams();   
	        params.height = totalHeight + (paiduiListView.getDividerHeight() * (paiduiListView.getCount() - 1));    
	        paiduiListView.setLayoutParams(params);
			paiduiAdapter.notifyDataSetChanged();
			
		} else {
			// TODO : 没有位置时， 该怎么做， 应该返回到列表页面， 在酒店详细信息页面应该判断
//			Toast.makeText(MerchantDetailActivity.this, "此酒店没有座位，请选择其他酒店。", Toast.LENGTH_SHORT).show();
//			btnGetNumber.setVisibility(View.INVISIBLE);
			paiduiConditionLayout.setVisibility(View.VISIBLE);
			paiduiListView.getEmptyView().setVisibility(View.VISIBLE);
		}
	}
	
	private Handler updatePaiduiListHandler = new Handler()
	{

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if(msg.what == 0){
				handlerPaidui();
			}
		}
		
	};

	private Handler openServiceHandler = new Handler(){
		public void handleMessage(Message msg) {
			if(msg.what == 0){
				String num = (String) msg.obj;;
				openNumView.setText(num);
			}
		};
	};
	
	private Handler attentionHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 200) {
				super.handleMessage(msg);
				String buf = String.valueOf(msg.obj);
				if (StringUtils.isNull(buf)) {
					unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
					Toast.makeText(MerchantDetailActivity.this, R.string.committing_failed, Toast.LENGTH_SHORT).show();
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
//						Toast.makeText(MerchantDetailActivity.this, R.string.committing_success, Toast.LENGTH_SHORT).show();
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
						Toast.makeText(MerchantDetailActivity.this, R.string.committing_failed, Toast.LENGTH_SHORT).show();
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
		case R.id.youhui_layout:
			if(this.merchant.youhuiExist)
			{
				QuhaoLog.d("", "the commentContent : " + this.merchant.commentContent);
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				Intent intent = new Intent(this, YouhuiListActivity.class);
				intent.putExtra("merchantId", this.merchant.id);
				startActivity(intent);
			}
			else
			{
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			}
			break;
		case R.id.critiqueLayout:
			if(StringUtils.isNotNull(this.merchant.commentContent) && !"暂无评论".equals(this.merchant.commentContent))
			{
				QuhaoLog.d("", "the commentContent : " + this.merchant.commentContent);
				Intent intent = new Intent(this, CommentsMerchantActivity.class);
				intent.putExtra("merchantName", this.merchant.name);
				intent.putExtra("merchantId", this.merchant.id);
				
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				startActivity(intent);
			}
			else
			{
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
//				Toast.makeText(this, "对不起，暂无评论。", Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.desc_layout:
			if(StringUtils.isNotNull(this.merchant.description))
			{
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				QuhaoLog.d("", "the commentContent : " + this.merchant.commentContent);
				Intent intent = new Intent(this, MerchantDescActivity.class);
				intent.putExtra("merchantDesc", this.merchant.description);
				startActivity(intent);
			}
			else
			{
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
//				Toast.makeText(this, "对不起，暂无描述。", Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.btn_refresh_paidui:
			if(null != merchant && StringUtils.isNotNull(merchant.id))
			{
				final GetPaiduiListTask task = new GetPaiduiListTask(R.string.waitting, this, "quhao?id=" + merchant.id);
				task.execute(new Runnable() {
					
					@Override
					public void run() {
						
						JsonPack jsonPack = task.jsonPack;
						Haoma haomaTemp = ParseJson.getHaoma(jsonPack.getObj());
						haoma = haomaTemp;
						merchantDetail.haoma = haomaTemp;
						
						updatePaiduiListHandler.obtainMessage(0, null).sendToTarget();
						
					}
				}, new Runnable() {
					
					@Override
					public void run() {
						
						JsonPack jsonPack = task.jsonPack;
						
						Map<String, Object> toastParams = new HashMap<String, Object>();
						toastParams.put("activity", MerchantDetailActivity.this);
						toastParams.put("text", jsonPack.getMsg());
						toastParams.put("toastLength", Toast.LENGTH_SHORT);
						toastStringHandler.obtainMessage(1000, toastParams).sendToTarget();
						
					}
				});
			}
			
			unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			break;
		case R.id.btn_open:
			if(QHClientApplication.getInstance().isLogined)
			{
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
							if (!ActivityUtil.isNetWorkAvailable(getApplicationContext())) {
								Toast.makeText(getApplicationContext(), R.string.network_error_info, Toast.LENGTH_SHORT).show();
								unlockHandler.sendEmptyMessage(UNLOCK_CLICK);
								progressDialogUtil.closeProgress();
								return;
							}
							String buf = CommonHTTPRequest.get("openService?mid=" + merchantId + "&accountId=" + accountId);
							if (StringUtils.isNull(buf)) {
								unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
								Toast.makeText(MerchantDetailActivity.this, R.string.committing_failed, Toast.LENGTH_SHORT).show();
								
							} else {
								if("error".equals(buf))
								{
									unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
									Toast.makeText(MerchantDetailActivity.this, R.string.committing_failed, Toast.LENGTH_SHORT).show();
								}
								else
								{
									unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
									openServiceHandler.obtainMessage(0, buf).sendToTarget();
//									Toast.makeText(MerchantDetailActivity.this, R.string.committing_success, Toast.LENGTH_SHORT).show();
								}
								
							}
							progressDialogUtil.closeProgress();

						} catch (Exception e) {
							progressDialogUtil.closeProgress();
							Toast.makeText(MerchantDetailActivity.this, R.string.committing_failed, Toast.LENGTH_SHORT).show();
							unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
							e.printStackTrace();
						} finally {
							Looper.loop();
						}
						
					}
				});
				thread.start();
			}
			else
			{
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				Intent intent = new Intent(MerchantDetailActivity.this, LoginActivity.class);
				intent.putExtra("activityName", MerchantDetailActivity.class.getName());
				intent.putExtra("notGetNumber", "true");
				intent.putExtra("merchantId", MerchantDetailActivity.this.merchantId);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
			
			break;
			case R.id.btn_attention:
				if(QHClientApplication.getInstance().isLogined)
				{
					Thread thread1 = new Thread(new Runnable() {
						
						@Override
						public void run() {
							Looper.prepare();
							progressDialogUtil = new ProgressDialogUtil(MerchantDetailActivity.this, R.string.empty, R.string.waitting_for_commit, false);
							progressDialogUtil.showProgress();
							
							String accountId = SharedprefUtil.get(MerchantDetailActivity.this, QuhaoConstant.ACCOUNT_ID, "");
							String merchantId = merchant.id;
							int flag = 0;
							if(merchant.isAttention)
							{
								flag = 1;
							}
							
							try {
								QuhaoLog.v(LOGTAG, "pay attention to merchant, account id  : " + accountId + " , merchant ID : " + merchantId + ",flag : " + flag);
								if (!ActivityUtil.isNetWorkAvailable(getApplicationContext())) {
									Toast.makeText(getApplicationContext(), R.string.network_error_info, Toast.LENGTH_SHORT).show();
									unlockHandler.sendEmptyMessage(UNLOCK_CLICK);
									progressDialogUtil.closeProgress();
									return;
								}
								String buf = CommonHTTPRequest.get("updateAttention?mid=" + merchantId + "&accountId=" + accountId + "&flag=" + flag);
								attentionHandler.obtainMessage(200, buf).sendToTarget();
								

							} catch (Exception e) {
								progressDialogUtil.closeProgress();
								Toast.makeText(MerchantDetailActivity.this, R.string.committing_failed, Toast.LENGTH_SHORT).show();
								unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
								e.printStackTrace();
							} finally {
								Looper.loop();
							}
							
						}
					});
					thread1.start();
				}
				else
				{
					unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
					Intent intent = new Intent(MerchantDetailActivity.this, LoginActivity.class);
					intent.putExtra("activityName", MerchantDetailActivity.class.getName());
					intent.putExtra("notGetNumber", "true");
					intent.putExtra("merchantId", MerchantDetailActivity.this.merchantId);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
				}
			break;
			case R.id.mapLayout:
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				Intent intent = new Intent(MerchantDetailActivity.this, MerchantLBSActivity.class);
				intent.putExtra("merchantId", merchantId);
				intent.putExtra("merchantName", merchant.name);
				startActivity(intent);
				break;
			case R.id.merchantPhone:
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				// 取得输入的电话号码串
				String phoneNO = merchant.phone;
				// 如果输入不为空创建打电话的Intent
				if (StringUtils.isNotNull(phoneNO)) {
					Intent phoneIntent = new Intent("android.intent.action.CALL", Uri.parse("tel:" + phoneNO));
					startActivity(phoneIntent);
				} else {
					Toast.makeText(MerchantDetailActivity.this, "此商家还未添加联系方式", Toast.LENGTH_SHORT).show();
				}
				break;
			case R.id.btn_GetNumber:
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				if (QHClientApplication.getInstance().isLogined) {
					if (null == merchantDetail.haoma 
						|| null == merchantDetail.haoma.paiduiList || merchantDetail.haoma.paiduiList.isEmpty()) {
						Toast.makeText(this, "现在暂时不能取号哦。", Toast.LENGTH_SHORT).show();
						return;
					}
					Intent intentGetNumber = new Intent();
					intentGetNumber.putExtra("merchantId", merchantId);
					intentGetNumber.putExtra("merchantName", mName);
					intentGetNumber.setClass(MerchantDetailActivity.this, GetNumberActivity.class);
					startActivity(intentGetNumber);
		
				} else {
					Intent intentGetNumber = new Intent(MerchantDetailActivity.this, LoginActivity.class);
					intentGetNumber.putExtra("activityName", MerchantDetailActivity.class.getName());
					intentGetNumber.putExtra("merchantId", MerchantDetailActivity.this.merchantId);
					intentGetNumber.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intentGetNumber);
				}
				break;
		default:
			unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
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
		super.finish();
		QuhaoLog.i(LOGTAG, LOGTAG + " finished");
	}

	@Override
	protected void onResume() {
		backClicked = false;
		
		Thread merchantThread = new Thread(merchantDetailRunnable);
		merchantThread.start();
		super.onResume();
	}

	@Override
	public void onPause() {
		if(null != progressDialogUtil)
		{
			progressDialogUtil.closeProgress();
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
