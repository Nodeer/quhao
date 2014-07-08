package com.withiter.quhao.activity;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.withiter.quhao.QHClientApplication;
import com.withiter.quhao.R;
import com.withiter.quhao.adapter.MerchantChatRoomAdapter;
import com.withiter.quhao.task.GetChatPortTask;
import com.withiter.quhao.task.JsonPack;
import com.withiter.quhao.task.NearbyMerchantsTask;
import com.withiter.quhao.util.ActivityUtil;
import com.withiter.quhao.util.QuhaoLog;
import com.withiter.quhao.util.StringUtils;
import com.withiter.quhao.util.tool.ParseJson;
import com.withiter.quhao.util.tool.QuhaoConstant;
import com.withiter.quhao.view.refresh.PullToRefreshView;
import com.withiter.quhao.view.refresh.PullToRefreshView.OnFooterRefreshListener;
import com.withiter.quhao.view.refresh.PullToRefreshView.OnHeaderRefreshListener;
import com.withiter.quhao.vo.Merchant;

/**
 * 商家列表页面
 */
public class MerchantChatRoomsActivity extends QuhaoBaseActivity implements OnHeaderRefreshListener, OnFooterRefreshListener, OnItemClickListener, AMapLocationListener {

	private String LOGTAG = MerchantChatRoomsActivity.class.getName();
	protected ListView merchantListView;
	private List<Merchant> merchants;
	private MerchantChatRoomAdapter merchantChatRoomAdapter;
	private int page;
	private boolean isFirst = true;
	private boolean needToLoad = true;

	private ImageView selectedMerchantImgView;
	private LinearLayout chatLayout;
	private TextView selectedRenqi;
	private TextView selectedDistance;
	
	private DisplayImageOptions options;
	
	private Merchant selectedMerchant;
	
	private int selectedPosition;
	
	private PullToRefreshView mPullToRefreshView;
	
	private LinearLayout resultLayout;
	private LinearLayout noResultLayout;
	private TextView noResultView;
	private TextView locationResult;

	private boolean isFirstLocation = false;
	
	private AMapLocation firstLocation = null;
	
	private LocationManagerProxy mAMapLocationManager = null;
	
	private Handler locationHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg) {
			
			super.handleMessage(msg);
			
			if (firstLocation == null) {
				MerchantChatRoomsActivity.this.findViewById(R.id.loadingbar).setVisibility(View.GONE);
				MerchantChatRoomsActivity.this.findViewById(R.id.serverdata).setVisibility(View.VISIBLE);
				resultLayout.setVisibility(View.GONE);
				noResultLayout.setVisibility(View.VISIBLE);
				noResultView.setText(R.string.location_failed);
				locationResult.setText(R.string.re_location);
				locationResult.setVisibility(View.VISIBLE);
				if (ActivityUtil.isTopActivy(MerchantChatRoomsActivity.this, MerchantChatRoomsActivity.class.getName())) {
					Toast.makeText(MerchantChatRoomsActivity.this, "亲，定位失败，请检查网络状态！", Toast.LENGTH_SHORT).show();
				}
				
				stopLocation();// 销毁掉定位
			}
		}
		
	};
	
	/**
	 * 销毁定位
	 */
	private void stopLocation() {
		
		if (mAMapLocationManager != null) {
			mAMapLocationManager.removeUpdates(this);
			mAMapLocationManager.destory();
		}
		mAMapLocationManager = null;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.merchant_chat_rooms_layout);
		super.onCreate(savedInstanceState);

		this.merchants = new ArrayList<Merchant>();

		this.page = getIntent().getIntExtra("page", 1);
		QuhaoLog.i(LOGTAG, "init page is : " + this.page);

		btnBack.setOnClickListener(goBack(this, this.getClass().getName()));

		selectedMerchantImgView = (ImageView) this.findViewById(R.id.selected_merchant_img);
		chatLayout = (LinearLayout) this.findViewById(R.id.btn_chat);
		chatLayout.setOnClickListener(this);
		
		selectedRenqi = (TextView) this.findViewById(R.id.selected_renqi);
		selectedDistance = (TextView) this.findViewById(R.id.selected_distance);
		
		resultLayout = (LinearLayout) this.findViewById(R.id.result_layout);
		noResultLayout = (LinearLayout) this.findViewById(R.id.no_result_layout);
		noResultView = (TextView) this.findViewById(R.id.no_result_text);
		locationResult = (TextView) this.findViewById(R.id.location_result);
		locationResult.setOnClickListener(this);
		
		mPullToRefreshView = (PullToRefreshView) this.findViewById(R.id.main_pull_refresh_view);
		mPullToRefreshView.setOnHeaderRefreshListener(this);
		mPullToRefreshView.setOnFooterRefreshListener(this);
		mPullToRefreshView.setEnableFooterView(true);
		initView();
	}

	private Handler merchantsUpdateHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 200) {
				super.handleMessage(msg);

				// 默认isFirst是true.
				if (isFirst) {
					merchantChatRoomAdapter = new MerchantChatRoomAdapter(MerchantChatRoomsActivity.this, merchantListView, merchants);
					merchantListView.setAdapter(merchantChatRoomAdapter);
					isFirst = false;
				} else {
					merchantChatRoomAdapter.merchants = merchants;
				}

				merchantChatRoomAdapter.notifyDataSetChanged();
				mPullToRefreshView.onHeaderRefreshComplete();
				mPullToRefreshView.onFooterRefreshComplete();
				
				if (null == merchants ||merchants.isEmpty()) {
					Toast.makeText(MerchantChatRoomsActivity.this, R.string.no_result_4_chat_room, Toast.LENGTH_SHORT).show();
				}
				
				findViewById(R.id.loadingbar).setVisibility(View.GONE);
				findViewById(R.id.serverdata).setVisibility(View.VISIBLE);
				
				if (!needToLoad) {
					mPullToRefreshView.setEnableFooterView(false);
				} else {
					mPullToRefreshView.setEnableFooterView(true);
				}
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			}

		}

	};

	private void initView() {
		merchantListView = (ListView) findViewById(R.id.chats_list);
		merchantListView.setNextFocusDownId(R.id.chats_list);
		merchantListView.setOnItemClickListener(this);
		findViewById(R.id.loadingbar).setVisibility(View.VISIBLE);
		findViewById(R.id.serverdata).setVisibility(View.GONE);
//		rvoListView.setOnItemClickListener(itemClickListener);
	}

	private void getMerchants() {

		String accountId = QHClientApplication.getInstance().accountInfo.accountId;
		if(null == firstLocation)
		{
			Toast.makeText(MerchantChatRoomsActivity.this, "亲，现在没有定位信息，不能查看哦。", Toast.LENGTH_SHORT).show();
			return;
		}
		
		String url = "getNearMerchants?userX=" + firstLocation.getLongitude() + "&userY=" + firstLocation.getLatitude() + "&cityCode=" + firstLocation.getCityCode()
				+ "&page=" + page + "&maxDis=" + 2 + "&cateType=";
		final NearbyMerchantsTask task = new NearbyMerchantsTask(0, MerchantChatRoomsActivity.this, url);
		task.execute(new Runnable() {
			
			@Override
			public void run() {

				String result = task.result;
				List<Merchant> tempList = ParseJson.getMerchants(result);
				if (null == tempList || tempList.isEmpty() || tempList.size() < 20) {
					needToLoad = false;
				}
				if (null == merchants) {
					merchants = new ArrayList<Merchant>();
				}
				merchants.addAll(tempList);
				Log.e("wjzwjz ", " success merchant list size : " + merchants.size());
				merchantsUpdateHandler.obtainMessage(200, null).sendToTarget();
			}
		}, new Runnable() {
			
			@Override
			public void run() {
				
				merchants = new ArrayList<Merchant>();
				needToLoad = false;
				merchantsUpdateHandler.obtainMessage(200, merchants).sendToTarget();
				
			}
		});
	}

	@Override
	public void onClick(View v) {

		// 已经点过，直接返回
		if (isClick) {
			return;
		}

		// 设置已点击标志，避免快速重复点击
		isClick = true;
		// 解锁
		

		switch (v.getId()) {
		case R.id.location_result:
			this.findViewById(R.id.loadingbar).setVisibility(View.VISIBLE);
			this.findViewById(R.id.serverdata).setVisibility(View.GONE);
			resultLayout.setVisibility(View.VISIBLE);
			noResultLayout.setVisibility(View.GONE);
			locationResult.setVisibility(View.GONE);
			Thread requestLocation = new Thread(new Runnable() {

				@Override
				public void run() {
					Looper.prepare();
					try {
						stopLocation();
					
						mAMapLocationManager = LocationManagerProxy
								.getInstance(MerchantChatRoomsActivity.this);
						/*
						 * mAMapLocManager.setGpsEnable(false);//
						 * 1.0.2版本新增方法，设置true表示混合定位中包含gps定位，false表示纯网络定位，默认是true
						 */
						// Location SDK定位采用GPS和网络混合定位方式，时间最短是5000毫秒，否则无效
						mAMapLocationManager.requestLocationUpdates(
								LocationProviderProxy.AMapNetwork, 10000, 100,
								MerchantChatRoomsActivity.this);
//							locationHandler.removeCallbacks(locationRunnable);
						locationHandler.sendEmptyMessageDelayed(200, 60000);
//							locationHandler.postDelayed(locationRunnable, 60000);// 设置超过12秒还没有定位到就停止定位

					} catch (Exception e) {
						Log.e("wjzwjz", e.getMessage());
					}
					finally
					{
						Looper.loop();
					}
				}
			});
			requestLocation.start();
			unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			break;
		case R.id.btn_chat:
			unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			if (QHClientApplication.getInstance().isLogined) {
				
				if (selectedMerchant == null) {
					Toast.makeText(this, "亲，请选择一个商家。", Toast.LENGTH_SHORT).show();
					return;
				}
				
				final GetChatPortTask task = new GetChatPortTask(R.string.waitting, this, "chat?mid=" +selectedMerchant.id);
				task.execute(new Runnable() {
					
					@Override
					public void run() {
						JsonPack jsonPack = task.jsonPack;
						String port = jsonPack.getObj();
						if ("false".equals(port)) {
							Toast.makeText(MerchantChatRoomsActivity.this, "亲，房间人数已满，请稍后再来。", Toast.LENGTH_SHORT).show();
							return;
						}
						Intent intentChat = new Intent();
						//uid=uid1&image=image1&mid=mid1&user=11
						String image = QHClientApplication.getInstance().accountInfo.userImage;
						if(StringUtils.isNotNull(image) && image.contains(QuhaoConstant.HTTP_URL))
						{
							image = "/" + image.substring(QuhaoConstant.HTTP_URL.length());
						}
						if (QHClientApplication.getInstance().accountInfo == null) {
							Toast.makeText(MerchantChatRoomsActivity.this, "亲，账号登录过期了哦", Toast.LENGTH_SHORT).show();
							return;
						}
						intentChat.putExtra("uid", QHClientApplication.getInstance().accountInfo.accountId);
						intentChat.putExtra("image", image);
						intentChat.putExtra("mid", selectedMerchant.id);
						intentChat.putExtra("user", QHClientApplication.getInstance().accountInfo.phone);
						intentChat.putExtra("merchantName", selectedMerchant.name);
						intentChat.putExtra("port", port);
						intentChat.setClass(MerchantChatRoomsActivity.this, MerchantChatActivity.class);
						startActivity(intentChat);
					}
				},new Runnable() {
					
					@Override
					public void run() {
						Toast.makeText(MerchantChatRoomsActivity.this, "亲，房间人数已满，请稍后再来。", Toast.LENGTH_SHORT).show();
						return;
					}
				});
	
			} else {
				Intent intentChat = new Intent(MerchantChatRoomsActivity.this, LoginActivity.class);
				intentChat.putExtra("activityName", MerchantDetailActivity.class.getName());
				intentChat.putExtra("merchantId", selectedMerchant.id);
				intentChat.putExtra("notGetNumber", "true");
				intentChat.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intentChat);
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
	protected void onResume() {
		super.onResume();
		
		if (!ActivityUtil.isNetWorkAvailable(getApplicationContext())) {
			Toast.makeText(getApplicationContext(), R.string.network_error_info, Toast.LENGTH_SHORT).show();
			unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			return;
		}
		
		Thread requestLocation = new Thread(new Runnable() {

			@Override
			public void run() {
				Looper.prepare();
				try {
					stopLocation();
				
					mAMapLocationManager = LocationManagerProxy
							.getInstance(MerchantChatRoomsActivity.this);
					/*
					 * mAMapLocManager.setGpsEnable(false);//
					 * 1.0.2版本新增方法，设置true表示混合定位中包含gps定位，false表示纯网络定位，默认是true
					 */
					// Location SDK定位采用GPS和网络混合定位方式，时间最短是5000毫秒，否则无效
					mAMapLocationManager.requestLocationUpdates(
							LocationProviderProxy.AMapNetwork, 10000, 100,
							MerchantChatRoomsActivity.this);
//						locationHandler.removeCallbacks(locationRunnable);
//						locationHandler.postDelayed(locationRunnable, 60000);// 设置超过12秒还没有定位到就停止定位
					locationHandler.sendEmptyMessageDelayed(200, 60000);

				} catch (Exception e) {
					Log.e("wjzwjz", e.getMessage());
				}
				finally
				{
					Looper.loop();
				}
			}
		});
		requestLocation.start();
	}

	@Override
	public void onPause() {
		super.onPause();
		stopLocation();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		stopLocation();
	}
	
	@Override
	public void onFooterRefresh(PullToRefreshView view) {
		mPullToRefreshView.postDelayed(new Runnable() {

			@Override
			public void run() {
				MerchantChatRoomsActivity.this.page += 1;
				getMerchants();
			}
		}, 1000);
	}

	@Override
	public void onHeaderRefresh(PullToRefreshView view) {
		mPullToRefreshView.postDelayed(new Runnable() {

			@Override
			public void run() {
				MerchantChatRoomsActivity.this.page = 1;
				isFirst = true;
				needToLoad = true;

				// merchantsListView.setSelectionFromTop(0, 0);// 滑动到第一项
				MerchantChatRoomsActivity.this.merchants = new ArrayList<Merchant>();
				getMerchants();
			}
		}, 1000);

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		
		if (isClick) {
			return;
		}
		isClick = false;
		
		unlockHandler.sendEmptyMessage(UNLOCK_CLICK);
		if (merchants != null && !merchants.isEmpty()) {
			selectedPosition = arg2;
			selectedMerchant = merchants.get(arg2);
			topUpdateHandler.obtainMessage(200, selectedMerchant).sendToTarget();
		}
		
	}

	private Handler topUpdateHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg) {
			
			super.handleMessage(msg);
			
			if (msg.what == 200) {
				if (options == null) {
					options = new DisplayImageOptions.Builder()
					.showImageOnLoading(R.drawable.home_user_img_bg)
					.showImageForEmptyUri(R.drawable.home_user_img_bg)
					.showImageOnFail(R.drawable.home_user_img_bg)
					.cacheInMemory(true)
					.cacheOnDisk(true)
					.considerExifParams(true)
					.displayer(new RoundedBitmapDisplayer(80))
					.build();
				}
				ImageLoader.getInstance().displayImage(selectedMerchant.merchantImage, selectedMerchantImgView, options);
				
				selectedRenqi.setText("人气 " + 0);
				
				if(selectedMerchant.distance > 0)
				{
					if(selectedMerchant.distance>1000)
					{
						
						NumberFormat nf = NumberFormat.getNumberInstance();
				        nf.setMaximumFractionDigits(1);
						selectedDistance.setText(nf.format(selectedMerchant.distance/1000) + "km");
					}
					else
					{
						selectedDistance.setText(String.valueOf((int)selectedMerchant.distance) + "m");
					}
					
				}
				else
				{
					selectedDistance.setText("未定位");
				}
				
			}
			
		}
	};
	
	@Override
	public void onLocationChanged(Location location) {
		
		Log.e("", "wjzwjz onLocationChanged location : " + location);
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		Log.e("", "wjzwjz onStatusChanged provider : " + provider + " , status : " + status + " , extras:" + extras);
	}

	@Override
	public void onProviderEnabled(String provider) {
		Log.e("", "wjzwjz onProviderEnabled provider : " + provider );
	}

	@Override
	public void onProviderDisabled(String provider) {
		Log.e("", "wjzwjz onProviderDisabled provider : " + provider);
	}

	@Override
	public void onLocationChanged(AMapLocation location) {
		Log.e("", "wjzwjz onLocationChanged AMapLocation location : " + location);
		if (null != location) {
			
			QHClientApplication.getInstance().location = location;
			stopLocation();
			if(!isFirstLocation)
			{
				isFirstLocation = true;
				firstLocation = location;
				merchants = new ArrayList<Merchant>();
				getMerchants();
			}
			else
			{
				float distance = firstLocation.distanceTo(location);
				if(distance>100)
				{
					firstLocation = location;
					merchants = new ArrayList<Merchant>();
					getMerchants();
				}
				else
				{
					return;
				}
				
			}
			
		}
	}

}
