package com.withiter.quhao.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.withiter.quhao.QHClientApplication;
import com.withiter.quhao.R;
import com.withiter.quhao.adapter.MerchantNoQueueAdapter;
import com.withiter.quhao.task.AllCategoriesTask;
import com.withiter.quhao.task.JsonPack;
import com.withiter.quhao.task.QueryNoQueueMerchantsTask;
import com.withiter.quhao.util.ActivityUtil;
import com.withiter.quhao.util.QuhaoLog;
import com.withiter.quhao.util.StringUtils;
import com.withiter.quhao.util.tool.ParseJson;
import com.withiter.quhao.view.expandtab.ViewLeft;
import com.withiter.quhao.view.expandtab.ViewRight;
import com.withiter.quhao.view.refresh.PullToRefreshView;
import com.withiter.quhao.view.refresh.PullToRefreshView.OnFooterRefreshListener;
import com.withiter.quhao.view.refresh.PullToRefreshView.OnHeaderRefreshListener;
import com.withiter.quhao.vo.Category;
import com.withiter.quhao.vo.Merchant;

/**
 * 不需要排队商家列表页面
 */
public class NoQueueMerchantListActivity extends QuhaoBaseActivity implements AMapLocationListener, OnClickListener,
	OnHeaderRefreshListener, OnFooterRefreshListener, OnItemClickListener {

	private String LOGTAG = NoQueueMerchantListActivity.class.getName();
	protected ListView merchantsListView;
	private List<Merchant> merchantList;
	private MerchantNoQueueAdapter merchantNoQueueAdapter;
	private final int UNLOCK_CLICK = 1000;
	private int page;
	private boolean isFirstLoad = true;
	private boolean needToLoad = true;
	public static boolean backClicked = false;
	
	private boolean isFirstLocation = false;
	
	private AMapLocation firstLocation = null;
	

	private PullToRefreshView mPullToRefreshView;
	
	private LinearLayout resultLayout;
	private LinearLayout noResultLayout;
	private TextView noResultView;
	private TextView locationResult;
	
//	private ExpandTabView expandTabView;
	private ArrayList<View> mViewArray = new ArrayList<View>();
	private ViewLeft viewLeft;
	
	private ViewRight viewRight;
	
	private String searchDistence = "-1";
	
	private List<String> distanceItems;
	
	private List<String> distanceItemsValue;
	
	private LocationManagerProxy mAMapLocationManager = null;
	
	private String categoryType = "-1";
	
	private List<String> categoryTypes;
	
	private List<String> categoryNames;
	
	private List<Category> categorys;
	
	private LinearLayout categoryLayout;
	
	private LinearLayout queueLayout;
	
	private TextView categoryNameView;
	
	private TextView queueNameView;
	
	private PopupWindow popupWindow1;
	
	private PopupWindow popupWindow2;
	
	private int displayWidth;
	private int displayHeight;
	
	private Handler locationHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg) {
			
			super.handleMessage(msg);
			
			if (firstLocation == null) {
				NoQueueMerchantListActivity.this.findViewById(R.id.loadingbar).setVisibility(View.GONE);
				NoQueueMerchantListActivity.this.findViewById(R.id.serverdata).setVisibility(View.VISIBLE);
				resultLayout.setVisibility(View.GONE);
				noResultLayout.setVisibility(View.VISIBLE);
				noResultView.setText(R.string.location_failed);
				locationResult.setText(R.string.re_location);
				locationResult.setVisibility(View.VISIBLE);
				if (ActivityUtil.isTopActivy(NoQueueMerchantListActivity.this, NoQueueMerchantListActivity.class.getName())) {
					Toast.makeText(NoQueueMerchantListActivity.this, "亲，定位失败，请检查网络状态！", Toast.LENGTH_SHORT).show();
				}
				
				stopLocation();// 销毁掉定位
			}
		}
		
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.no_queue_merchant_list_layout);
		super.onCreate(savedInstanceState);

		this.merchantList = new ArrayList<Merchant>();

		this.page = getIntent().getIntExtra("page", 1);
		isFirstLoad = true;
		needToLoad = true;
		isFirstLocation = false;
		firstLocation = null;
		
		QuhaoLog.i(LOGTAG, "init page is : " + this.page);

		btnBack.setOnClickListener(goBack(this, this.getClass().getName()));

		resultLayout = (LinearLayout) this.findViewById(R.id.result_layout);
		noResultLayout = (LinearLayout) this.findViewById(R.id.no_result_layout);
		noResultView = (TextView) this.findViewById(R.id.no_result_text);
		locationResult = (TextView) this.findViewById(R.id.location_result);
		locationResult.setOnClickListener(this);
		
		mPullToRefreshView = (PullToRefreshView) this.findViewById(R.id.main_pull_refresh_view);
		mPullToRefreshView.setEnableFooterView(false);
		mPullToRefreshView.setOnHeaderRefreshListener(this);
		mPullToRefreshView.setOnFooterRefreshListener(this);
		
		categoryLayout = (LinearLayout) this.findViewById(R.id.category_layout);
		queueLayout = (LinearLayout) this.findViewById(R.id.queue_layout);
		categoryNameView = (TextView) this.findViewById(R.id.categoryName);
		queueNameView = (TextView) this.findViewById(R.id.queueName);
		categoryLayout.setOnClickListener(this);
		queueLayout.setOnClickListener(this);
		displayWidth = this.getWindowManager().getDefaultDisplay().getWidth();
		displayHeight = this.getWindowManager().getDefaultDisplay().getHeight();
		
		this.findViewById(R.id.loadingbar).setVisibility(View.VISIBLE);
		this.findViewById(R.id.serverdata).setVisibility(View.GONE);
		resultLayout.setVisibility(View.VISIBLE);
		noResultLayout.setVisibility(View.GONE);
		locationResult.setVisibility(View.GONE);
		initView();
	}

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
	
	private AdapterView.OnItemClickListener merchantItemClickListener = new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			Merchant merchant = merchantList.get(position);
			Intent intent = new Intent();
			intent.putExtra("merchantId", merchant.id);
			intent.setClass(NoQueueMerchantListActivity.this, MerchantDetailActivity.class);
			startActivity(intent);
		}
	};

	private void initView() {
		merchantsListView = (ListView) findViewById(R.id.merchantsListView);
		merchantsListView.setNextFocusDownId(R.id.merchantsListView);
		merchantsListView.setVisibility(View.GONE);
		merchantsListView.setOnItemClickListener(merchantItemClickListener);
//		expandTabView = (ExpandTabView) this.findViewById(R.id.expandtab_view);
	}
	
	private void initExpandView() {

		categoryNames = new ArrayList<String>();
		categoryTypes = new ArrayList<String>();
		categoryNames.add("全部分类");
		categoryTypes.add("-1");
		if (categorys!=null && !categorys.isEmpty()) {
			for (int i = 0; i < categorys.size(); i++) {
				categoryNames.add(categorys.get(i).cateName);
				categoryTypes.add(categorys.get(i).categoryType);
			}
		}
		
		if (StringUtils.isNull(categoryType) || "-1".equals(categoryType)) {
			categoryType = "-1";
			categoryNameView.setText("全部分类");
		}
//		expandTabView = (ExpandTabView) findViewById(R.id.expandtab_view);
		viewLeft = new ViewLeft(this, categoryNames, categoryTypes, categoryType);
		
		distanceItems = new ArrayList<String>();
		distanceItems.add("全城");
		distanceItems.add("1千米");
		distanceItems.add("3千米");
		distanceItems.add("5千米");
		distanceItems.add("10千米");
		
		distanceItemsValue = new ArrayList<String>();
		distanceItemsValue.add("-1");
		distanceItemsValue.add("1");
		distanceItemsValue.add("3");
		distanceItemsValue.add("5");
		distanceItemsValue.add("10");
		
		if (StringUtils.isNull(searchDistence) || "-1".equals(searchDistence)) {
			searchDistence = "-1";
			queueNameView.setText("全城");
		}
		
		viewRight = new ViewRight(this, distanceItems, distanceItemsValue, String.valueOf(searchDistence));
		
		mViewArray = new ArrayList<View>();

		final RelativeLayout viewLeftLayout = new RelativeLayout(this);
		int maxHeight = (int) (displayHeight * 0.5);
		RelativeLayout.LayoutParams viewLeftParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, maxHeight);
		viewLeftParams.leftMargin = 10;
		viewLeftParams.rightMargin = 10;
		viewLeftLayout.addView(viewLeft, viewLeftParams);
		if(viewLeftLayout.getParent()!=null) {
			ViewGroup vg = (ViewGroup) viewLeftLayout.getParent();
			vg.removeView(viewLeftLayout);
		}
		viewLeftLayout.setBackgroundColor(this.getResources().getColor(R.color.popup_main_background));
		viewLeftLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				onPressBack();
			}
		});
		mViewArray.add(viewLeftLayout);
		
		final RelativeLayout viewRightLayout = new RelativeLayout(this);
		RelativeLayout.LayoutParams viewRightLP = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, maxHeight);
		viewRightLP.leftMargin = 10;
		viewRightLP.rightMargin = 10;
		viewRightLayout.addView(viewRight, viewRightLP);
		if(viewRightLayout.getParent()!=null) {
			ViewGroup vg = (ViewGroup) viewRightLayout.getParent();
			vg.removeView(viewRightLayout);
		}
		viewRightLayout.setBackgroundColor(this.getResources().getColor(R.color.popup_main_background));
		viewRightLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				onPressBack();
			}
		});
		mViewArray.add(viewRightLayout);
		
		viewLeft.setOnSelectListener(new ViewLeft.OnSelectListener() {
			@Override
			public void getValue(String distance, String showText) {
				onRefresh(0, showText);
			}
		});
		
		viewRight.setOnSelectListener(new ViewRight.OnSelectListener() {
			@Override
			public void getValue(String distance, String showText) {
				onRefresh(1, showText);
			}
		});
	
	}
	
	private void onRefresh(int position, String showText) {
		
		if (position >= 0 && position == 0) {
			categoryNameView.setText(showText);
		}
		
		if (position >= 0 && position == 1) {
			queueNameView.setText(showText);
		}

		if (0 == position) {
			categoryType = categoryTypes.get(categoryNames.indexOf(showText));
			if (null != popupWindow1 && popupWindow1.isShowing()) {
				popupWindow1.dismiss();
			}
		}
		else if(1 == position) {
			if (null != popupWindow2 && popupWindow2.isShowing()) {
				popupWindow2.dismiss();
			}
			
			searchDistence = distanceItemsValue.get(distanceItems.indexOf(showText));
		}
		
		page = 1;
		needToLoad = true;

		merchantList = new ArrayList<Merchant>();
		resultLayout.setVisibility(View.VISIBLE);
		noResultLayout.setVisibility(View.GONE);
		locationResult.setVisibility(View.GONE);
		mPullToRefreshView.headerRefreshing();

	}

	private int getPositon(View tView) {
		for (int i = 0; i < mViewArray.size(); i++) {
			if (mViewArray.get(i) == tView) {
				return i;
			}
		}
		return -1;
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
								.getInstance(NoQueueMerchantListActivity.this);
						/*
						 * mAMapLocManager.setGpsEnable(false);//
						 * 1.0.2版本新增方法，设置true表示混合定位中包含gps定位，false表示纯网络定位，默认是true
						 */
						// Location SDK定位采用GPS和网络混合定位方式，时间最短是5000毫秒，否则无效
						mAMapLocationManager.requestLocationUpdates(
								LocationProviderProxy.AMapNetwork, 10000, 100,
								NoQueueMerchantListActivity.this);
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
		case R.id.category_layout:
			if (mViewArray == null || mViewArray.isEmpty()) {
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 0);
				return;
			}
			
			if (popupWindow2 != null && popupWindow2.isShowing()) {
				popupWindow2.dismiss();
			}
			View view = mViewArray.get(0);
			if (popupWindow1 == null) {
				
				popupWindow1 = new PopupWindow(view, displayWidth, displayHeight);
				popupWindow1.setAnimationStyle(R.style.PopupWindowAnimation);
				popupWindow1.setFocusable(false);
				popupWindow1.setOutsideTouchable(true);
			}
			
			if (!popupWindow1.isShowing()) {
				popupWindow1.showAsDropDown(categoryLayout);
//				showPopup(selectPosition);
			} else {
//				popupWindow.setOnDismissListener(this);
				popupWindow1.dismiss();
//				popupWindow.
//				hideView();
			}
			
			unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 0);
			break;
		case R.id.queue_layout:
			if (mViewArray == null || mViewArray.isEmpty()) {
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 0);
				return;
			}
			if (popupWindow1 != null && popupWindow1.isShowing()) {
				popupWindow1.dismiss();
			}
			
			View view2 = mViewArray.get(1);
			if (popupWindow2 == null) {
				
				popupWindow2 = new PopupWindow(view2, displayWidth, displayHeight);
				popupWindow2.setAnimationStyle(R.style.PopupWindowAnimation);
				popupWindow2.setFocusable(false);
				popupWindow2.setOutsideTouchable(true);
			}
			
			if (!popupWindow2.isShowing()) {
				popupWindow2.showAsDropDown(queueLayout);
//				showPopup(selectPosition);
			} else {
//				popupWindow.setOnDismissListener(this);
				popupWindow2.dismiss();
//				popupWindow.
//				hideView();
			}
			
			unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 0);
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
		backClicked = false;
		super.onResume();
//		if (mAMapLocationManager == null) {  
//            mAMapLocationManager = LocationManagerProxy.getInstance(this);  
//            /* 
//             * mAMapLocManager.setGpsEnable(false);// 
//             * 1.0.2版本新增方法，设置true表示混合定位中包含gps定位，false表示纯网络定位，默认是true 
//             */  
//            // Location SDK定位采用GPS和网络混合定位方式，时间最短是5000毫秒，否则无效  
//            mAMapLocationManager.requestLocationUpdates(  
//                    LocationProviderProxy.AMapNetwork, 10000, 100, this);
//            
//            locationHandler.postDelayed(locationRunnable , 60000);// 设置超过12秒还没有定位到就停止定位
//            
//        }
		
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
							.getInstance(NoQueueMerchantListActivity.this);
					/*
					 * mAMapLocManager.setGpsEnable(false);//
					 * 1.0.2版本新增方法，设置true表示混合定位中包含gps定位，false表示纯网络定位，默认是true
					 */
					// Location SDK定位采用GPS和网络混合定位方式，时间最短是5000毫秒，否则无效
					mAMapLocationManager.requestLocationUpdates(
							LocationProviderProxy.AMapNetwork, 10000, 100,
							NoQueueMerchantListActivity.this);
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
	public void onFooterRefresh(PullToRefreshView view) {
		mPullToRefreshView.postDelayed(new Runnable() {

			@Override
			public void run() {
				NoQueueMerchantListActivity.this.page += 1;
				queryNoQueueMerchants();
			}
		}, 1000);
	}

	@Override
	public void onHeaderRefresh(PullToRefreshView view) {
		mPullToRefreshView.postDelayed(new Runnable() {

			@Override
			public void run() {
				NoQueueMerchantListActivity.this.page = 1;
				isFirstLoad = true;
				needToLoad = true;

				// merchantsListView.setSelectionFromTop(0, 0);// 滑动到第一项
				NoQueueMerchantListActivity.this.merchantList = new ArrayList<Merchant>();
				queryNoQueueMerchants();
			}
		}, 1000);

	}

	@Override
	public void onPause() {
		super.onPause();
		if (mAMapLocationManager != null) {
			mAMapLocationManager.removeUpdates(this);
//			locationHandler.removeCallbacks(locationRunnable);
		}
	}
	
	@Override
	public void onDestroy() {
		Log.e("wjzwjz", "NearbyFragment onDestroy");
		if (mAMapLocationManager != null) {
			mAMapLocationManager.removeUpdates(this);
			mAMapLocationManager.destory();
//			locationHandler.removeCallbacks(locationRunnable);
		}
		mAMapLocationManager = null;
		super.onDestroy();
	}
	
	@Override
	public void onLocationChanged(Location location) {
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		
	}

	protected Handler updateMerchantsHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			super.handleMessage(msg);

			if (msg.what == 200) {

				merchantsListView.setVisibility(View.VISIBLE);
				if (isFirstLoad) {

					merchantNoQueueAdapter = new MerchantNoQueueAdapter(
							NoQueueMerchantListActivity.this, merchantsListView, merchantList);
					merchantsListView.setAdapter(merchantNoQueueAdapter);
					isFirstLoad = false;
				} else {
					merchantNoQueueAdapter.merchants = merchantList;
				}
				merchantNoQueueAdapter.notifyDataSetChanged();
//				merchantsListView.setOnScrollListener(NearbyFragment.this);
				NoQueueMerchantListActivity.this.findViewById(R.id.loadingbar).setVisibility(View.GONE);
				NoQueueMerchantListActivity.this.findViewById(R.id.serverdata).setVisibility(View.VISIBLE);
				if(null != merchantList && !merchantList.isEmpty())
				{
					resultLayout.setVisibility(View.VISIBLE);
					noResultLayout.setVisibility(View.GONE);
				}
				else
				{
					resultLayout.setVisibility(View.GONE);
					noResultLayout.setVisibility(View.VISIBLE);
					noResultView.setText(R.string.no_result);
					locationResult.setVisibility(View.GONE);
				}
				
				merchantsListView.setOnItemClickListener(NoQueueMerchantListActivity.this);
				mPullToRefreshView.onHeaderRefreshComplete();
				mPullToRefreshView.onFooterRefreshComplete();
				if(!needToLoad)
				{
					mPullToRefreshView.setEnableFooterView(false);
				}
				else
				{
					mPullToRefreshView.setEnableFooterView(true);
				}
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			}
		}

	};
	
	private void queryNoQueueMerchants() {
		
		if(null == firstLocation)
		{
			Toast.makeText(this, "亲，现在没有定位信息，不能查看哦。", Toast.LENGTH_SHORT).show();
			return;
		}
		
		String type = categoryType;
		if ("-1".equals(categoryType)) {
			type = "";
		}
		
		String url = "getNearNoQueueMerchants?userX=" + firstLocation.getLongitude() + "&userY=" + firstLocation.getLatitude() + "&cityCode=" + firstLocation.getCityCode() + 
				"&page=" + page + "&maxDis=" + searchDistence + "&cateType=" + type;;
		final QueryNoQueueMerchantsTask task = new QueryNoQueueMerchantsTask(0, this, url);
		task.execute(new Runnable() {
			
			@Override
			public void run() {
				
				JsonPack result = task.jsonPack;
				List<Merchant> tempList = ParseJson.getMerchants(result.getObj());
				if (null == tempList || tempList.isEmpty() || tempList.size()<20) {
					needToLoad = false;
				}
				if(null == merchantList)
				{
					merchantList = new ArrayList<Merchant>();
				}
				merchantList.addAll(tempList);
				updateMerchantsHandler.obtainMessage(200, null).sendToTarget();
			}
		},new Runnable() {
			
			@Override
			public void run() {
				needToLoad = false;
				updateMerchantsHandler.obtainMessage(200, null).sendToTarget();
			}
		});
	}
	
	private Handler categorysUpdateHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 200) {
				super.handleMessage(msg);
				if (categorys != null && !categorys.isEmpty()) {
					initExpandView();
				}
				else {
					Toast.makeText(NoQueueMerchantListActivity.this, "亲，该城市暂未开通，请选择其他城市。", Toast.LENGTH_SHORT).show();
				}
				
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			}
		}
	};
	
	/**
	 * get all categories from server and display them
	 */
	public void getCategoriesFromServerAndDisplay(String cityCode) {
		
		if (null != categorys && !categorys.isEmpty()) {
			return;
		}
		final AllCategoriesTask task = new AllCategoriesTask(0, this, "allCategories?cityCode=" + cityCode);
		task.execute(new Runnable() {
			@Override
			public void run() {
				String result = task.result;
				if (null == categorys) {
					categorys = new ArrayList<Category>();
				}
				categorys.clear();
				categorys.addAll(ParseJson.getCategorys(result));
				QHClientApplication.getInstance().categorys = categorys;
				categorysUpdateHandler.obtainMessage(200, categorys).sendToTarget();
			}
		}, new Runnable() {

			@Override
			public void run() {
				String result = task.result;
				if (null == categorys) {
					categorys = new ArrayList<Category>();
				}
				categorys.clear();
				categorys.addAll(ParseJson.getCategorys(result));
				categorysUpdateHandler.obtainMessage(200, categorys).sendToTarget();
			}
		});

	}
	
	@Override
	public void onLocationChanged(AMapLocation location) {

		if (null != location) {
			
			QHClientApplication.getInstance().location = location;
			getCategoriesFromServerAndDisplay(location.getCityCode());
			
			if(!isFirstLocation)
			{
				isFirstLocation = true;
				firstLocation = location;
				merchantList = new ArrayList<Merchant>();
				queryNoQueueMerchants();
			}
			else
			{
				float distance = firstLocation.distanceTo(location);
				if(distance>100)
				{
					firstLocation = location;
					merchantList = new ArrayList<Merchant>();
					queryNoQueueMerchants();
				}
				else
				{
					return;
				}
				
			}
			
		}
	}

	/**
	 * 如果菜单成展开状态，则让菜单收回去
	 */
	public boolean onPressBack() {
		if (popupWindow1 != null && popupWindow1.isShowing()) {
			popupWindow1.dismiss();
			return true;
		}
		else if(popupWindow2 != null && popupWindow2.isShowing())
		{
			popupWindow2.dismiss();
			return true;
		}else {
			return false;
		}

	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {

		// 已经点过，直接返回
		if (isClick) {
			return;
		}

		// 设置已点击标志，避免快速重复点击
		isClick = true;
		// 解锁
		try {
			if (null != merchantList && !merchantList.isEmpty() && null != merchantList.get(position) 
					&& StringUtils.isNotNull(merchantList.get(position).id) && merchantList.get(position).enable) {
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				Intent intent = new Intent();
				intent.setClass(this, MerchantDetailActivity.class);
				intent.putExtra("merchantId", merchantList.get(position).id);
				startActivity(intent);
				
			} else {
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				AlertDialog.Builder builder = new Builder(this);
				builder.setTitle("温馨提示");
				builder.setMessage("对不起，该商家未在取号系统注册。");
				builder.setPositiveButton("确认",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						});
				builder.create().show();
			}
			
		} catch (Exception e) {
			unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			AlertDialog.Builder builder = new Builder(this);
			builder.setTitle("温馨提示");
			builder.setMessage("对不起，网络异常。");
			builder.setPositiveButton("确认",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
			builder.create().show();
			e.printStackTrace();
		}

	}
	
	@Override
	public void onBackPressed() {
		
		if (null != popupWindow1 && popupWindow1.isShowing()) {
			popupWindow1.dismiss();
			return;
		}
		else if(null != popupWindow2 && popupWindow2.isShowing())
		{
			popupWindow2.dismiss();
			return;
		}
		else
		{
			finish();
		}
	}

}
