package com.withiter.quhao.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
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
import com.withiter.quhao.adapter.MerchantNearByAdapter;
import com.withiter.quhao.task.AllCategoriesTask;
import com.withiter.quhao.task.NearbyMerchantsTask;
import com.withiter.quhao.util.ActivityUtil;
import com.withiter.quhao.util.StringUtils;
import com.withiter.quhao.util.tool.ParseJson;
import com.withiter.quhao.view.expandtab.ViewLeft;
import com.withiter.quhao.view.expandtab.ViewRight;
import com.withiter.quhao.view.refresh.PullToRefreshView;
import com.withiter.quhao.view.refresh.PullToRefreshView.OnFooterRefreshListener;
import com.withiter.quhao.view.refresh.PullToRefreshView.OnHeaderRefreshListener;
import com.withiter.quhao.vo.Category;
import com.withiter.quhao.vo.Merchant;

public class NearbyFragment extends Fragment implements AMapLocationListener, OnItemClickListener, OnClickListener, 
	OnHeaderRefreshListener, OnFooterRefreshListener {

	private LocationManagerProxy mAMapLocationManager = null;
	
	private Handler locationHandler = new Handler();
	
	private int page = 1;
	private ListView merchantsListView;
	private MerchantNearByAdapter nearByAdapter;
	private boolean isFirstLoad = true;
	private boolean needToLoad = true;
	private List<Merchant> merchantList;

	private boolean isFirstLocation = false;

	private AMapLocation firstLocation = null;

//	private ExpandTabView expandTabView;
	private ArrayList<View> mViewArray = new ArrayList<View>();
	private ViewLeft viewLeft;
	
	private ViewRight viewRight;

	private String searchDistence;

	private List<String> distanceItems;

	private List<String> distanceItemsValue;
	
	private String categoryType = "";
	
	private List<String> categoryTypes;
	
	private List<String> categoryNames;

	private boolean isClick;

	private static final int UNLOCK_CLICK = 1000;

	private View contentView;

	private ViewGroup group;

	private PullToRefreshView mPullToRefreshView;
	
	private LinearLayout resultLayout;
	private LinearLayout noResultLayout;
	private TextView noResultView;
	private TextView locationResult;
	
	private List<Category> categorys;
	
	private long time1;
	
	private LinearLayout categoryLayout;
	
	private LinearLayout queueLayout;
	
	private TextView categoryNameView;
	
	private TextView queueNameView;
	
	private PopupWindow popupWindow1;
	
	private PopupWindow popupWindow2;
	
	private int displayWidth;
	private int displayHeight;
	
	protected Handler unlockHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == UNLOCK_CLICK) {
				isClick = false;
			}
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Log.e("wjzwjz", "NearByFragment onCreateView");
		if (!ActivityUtil.isNetWorkAvailable(getActivity())) {
			Toast.makeText(getActivity(), R.string.network_error_info, Toast.LENGTH_SHORT).show();
		}
		if(contentView != null)
		{
			ViewGroup vg = (ViewGroup) contentView.getParent();
			vg.removeView(contentView);
			return contentView;
		}
		
		this.group = container;
		page = 1;
		isFirstLoad = true;
		needToLoad = true;
		isFirstLocation = false;
		firstLocation = null;
		contentView = inflater.inflate(R.layout.nearby_fragment_layout, container, false);
		merchantsListView = (ListView) contentView.findViewById(R.id.merchantsListView);

		resultLayout = (LinearLayout) contentView.findViewById(R.id.result_layout);
		noResultLayout = (LinearLayout) contentView.findViewById(R.id.no_result_layout);
		noResultView = (TextView) contentView.findViewById(R.id.no_result_text);
		locationResult = (TextView) contentView.findViewById(R.id.location_result);
		locationResult.setOnClickListener(this);
		mPullToRefreshView = (PullToRefreshView) contentView.findViewById(R.id.main_pull_refresh_view);
		mPullToRefreshView.setEnableFooterView(true);
		mPullToRefreshView.setOnHeaderRefreshListener(this);
		mPullToRefreshView.setOnFooterRefreshListener(this);

		merchantsListView.setNextFocusDownId(R.id.merchantsListView);

		categoryLayout = (LinearLayout) contentView.findViewById(R.id.category_layout);
		queueLayout = (LinearLayout) contentView.findViewById(R.id.queue_layout);
		categoryNameView = (TextView) contentView.findViewById(R.id.categoryName);
		queueNameView = (TextView) contentView.findViewById(R.id.queueName);
		categoryLayout.setOnClickListener(this);
		queueLayout.setOnClickListener(this);
		displayWidth = getActivity().getWindowManager().getDefaultDisplay().getWidth();
		displayHeight = getActivity().getWindowManager().getDefaultDisplay().getHeight();
//		initExpandView();
//		expandTabView = (ExpandTabView) contentView.findViewById(R.id.expandtab_view);
		
		contentView.findViewById(R.id.loadingbar).setVisibility(View.VISIBLE);
		contentView.findViewById(R.id.serverdata).setVisibility(View.GONE);
		resultLayout.setVisibility(View.VISIBLE);
		noResultLayout.setVisibility(View.GONE);
		locationResult.setVisibility(View.GONE);
		return contentView;
	}

	private Runnable locationRunnable = new Runnable() {
		
		@Override
		public void run() {
			if (firstLocation == null) {
				Toast.makeText(getActivity(), "亲，定位失败，请检查网络状态！", Toast.LENGTH_SHORT).show();
				contentView.findViewById(R.id.loadingbar).setVisibility(View.GONE);
				contentView.findViewById(R.id.serverdata).setVisibility(View.VISIBLE);
				resultLayout.setVisibility(View.GONE);
				noResultLayout.setVisibility(View.VISIBLE);
				noResultView.setText(R.string.location_failed);
				locationResult.setText(R.string.re_location);
				locationResult.setVisibility(View.VISIBLE);
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
	
	/**
	 * get all categories from server and display them
	 */
	public void getCategoriesFromServerAndDisplay(String cityCode) {
		
		if (null != categorys && !categorys.isEmpty()) {
			return;
		}
		final AllCategoriesTask task = new AllCategoriesTask(0, getActivity(), "allCategories?cityCode=" + cityCode);
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
	
	private Handler categorysUpdateHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 200) {
				super.handleMessage(msg);
				if (categorys != null && !categorys.isEmpty()) {
					initExpandView();
				}
				else {
					Toast.makeText(getActivity(), "亲，该城市暂未开通，请选择其他城市。", Toast.LENGTH_SHORT).show();
				}
				
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			}
		}
	};
	
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
		
		if (StringUtils.isNull(categoryType)) {
			categoryType = "-1";
			categoryNameView.setText("全部分类");
		}
//		expandTabView = (ExpandTabView) contentView.findViewById(R.id.expandtab_view);
		viewLeft = new ViewLeft(contentView.getContext(), categoryNames, categoryTypes, categoryType);
		
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
		
		if (StringUtils.isNull(searchDistence)) {
			searchDistence = "-1";
			queueNameView.setText("全城");
		}
		
		viewRight = new ViewRight(contentView.getContext(), distanceItems, distanceItemsValue, searchDistence);
		
		mViewArray = new ArrayList<View>();
//		mViewArray.add(viewLeft);
//		mViewArray.add(viewRight);
		
//		expandTabView.setValue(mTextArray, mViewArray,imgArray);
//		expandTabView.setTitle(viewLeft.getShowText(), 0);
//		expandTabView.setTitle(viewRight.getShowText(), 1);
		final RelativeLayout viewLeftLayout = new RelativeLayout(this.getActivity());
		int maxHeight = (int) (displayHeight * 0.5);
		RelativeLayout.LayoutParams viewLeftParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, maxHeight);
		viewLeftParams.leftMargin = 10;
		viewLeftParams.rightMargin = 10;
		viewLeftLayout.addView(viewLeft, viewLeftParams);
		if(viewLeftLayout.getParent()!=null) {
			ViewGroup vg = (ViewGroup) viewLeftLayout.getParent();
			vg.removeView(viewLeftLayout);
		}
		viewLeftLayout.setBackgroundColor(getActivity().getResources().getColor(R.color.popup_main_background));
		viewLeftLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				onPressBack();
			}
		});
		mViewArray.add(viewLeftLayout);
		
		final RelativeLayout viewRightLayout = new RelativeLayout(this.getActivity());
		RelativeLayout.LayoutParams viewRightLP = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, maxHeight);
		viewRightLP.leftMargin = 10;
		viewRightLP.rightMargin = 10;
		viewRightLayout.addView(viewRight, viewRightLP);
		if(viewRightLayout.getParent()!=null) {
			ViewGroup vg = (ViewGroup) viewRightLayout.getParent();
			vg.removeView(viewRightLayout);
		}
		viewRightLayout.setBackgroundColor(getActivity().getResources().getColor(R.color.popup_main_background));
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
	
		/*
		categoryNames = new ArrayList<String>();
		categoryTypes = new ArrayList<String>();
		categoryNames.add("默认排序");
		categoryTypes.add("-1");
		if (categorys!=null && !categorys.isEmpty()) {
			for (int i = 0; i < categorys.size(); i++) {
				categoryNames.add(categorys.get(i).cateName);
				categoryTypes.add(categorys.get(i).categoryType);
			}
		}
		
		categoryType = "";
		
		viewLeft = new ViewLeft(getActivity(), categoryNames, categoryTypes, categoryType);
		viewLeft.setShowText("菜系");
		
		if (searchDistence == 0) {
			searchDistence = 3;
//			distanceItems = new String[] { "1千米", "3千米", "5千米", "10千米", "全城" };// 显示字段
//			distanceItemsValue = new String[] { "1", "3", "5", "10", "-1" };// 显示字段
		}
		
		distanceItems = new ArrayList<String>();
		distanceItems.add("1千米");
		distanceItems.add("3千米");
		distanceItems.add("5千米");
		distanceItems.add("10千米");
		distanceItems.add("全城");
		
		distanceItemsValue = new ArrayList<String>();
		distanceItemsValue.add("1");
		distanceItemsValue.add("3");
		distanceItemsValue.add("5");
		distanceItemsValue.add("10");
		distanceItemsValue.add("-1");
		

		expandTabView = (ExpandTabView) contentView.findViewById(R.id.expandtab_view);
		viewLeft = new ViewLeft(contentView.getContext(), distanceItems, distanceItemsValue, String.valueOf(searchDistence));
		viewLeft.setShowText("距离");
		mViewArray = new ArrayList<View>();
		mViewArray.add(viewLeft);
		ArrayList<String> mTextArray = new ArrayList<String>();
		mTextArray.add("距离");
		
		ArrayList<Integer> imgArray = new ArrayList<Integer>();
		imgArray.add(R.drawable.ic_expand_queue);
		
		expandTabView.setValue(mTextArray, mViewArray,imgArray);
		expandTabView.setTitle(viewLeft.getShowText(), 0);

		viewLeft.setOnSelectListener(new ViewLeft.OnSelectListener() {
			@Override
			public void getValue(String distance, String showText) {
				onRefresh(viewLeft, showText);
			}
		});*/
	}

	private void onRefresh(int position, String showText) {
		
//		expandTabView.onPressBack();
//		int position = getPositon(view);
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

	protected Handler updateMerchantsHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			super.handleMessage(msg);

			if (msg.what == 200) {

				if (isFirstLoad) {

					nearByAdapter = new MerchantNearByAdapter(getActivity(), merchantsListView, merchantList);
					merchantsListView.setAdapter(nearByAdapter);
					isFirstLoad = false;
				} else {
					nearByAdapter.merchants = merchantList;
				}
				nearByAdapter.notifyDataSetChanged();
				// merchantsListView.setOnScrollListener(NearbyFragment.this);
				merchantsListView.setOnItemClickListener(NearbyFragment.this);
				contentView.findViewById(R.id.loadingbar).setVisibility(View.GONE);
				contentView.findViewById(R.id.serverdata).setVisibility(View.VISIBLE);
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
				
				mPullToRefreshView.onHeaderRefreshComplete();
				mPullToRefreshView.onFooterRefreshComplete();
				if (!needToLoad) {
					mPullToRefreshView.setEnableFooterView(false);
				} else {
					mPullToRefreshView.setEnableFooterView(true);
				}
				
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			}
		}

	};

	@Override
	public void onPause() {
		Log.e("wjzwjz", "NearByFragment onPause");
		super.onPause();
		if (mAMapLocationManager != null) {
			mAMapLocationManager.removeUpdates(this);
			locationHandler.removeCallbacks(locationRunnable);
		}
	}

	@Override
	public void onDestroyView() {
		Log.e("wjzwjz", "NearByFragment onDestroyView");
		if (mAMapLocationManager != null) {
			mAMapLocationManager.removeUpdates(this);
			mAMapLocationManager.destory();
			locationHandler.removeCallbacks(locationRunnable);
		}
		mAMapLocationManager = null;
		super.onDestroyView();
	}

	@Override
	public void onAttach(Activity activity) {
		Log.e("wjzwjz", "NearByFragment onAttach");
		super.onAttach(activity);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		Log.e("wjzwjz", "NearByFragment onActivityCreated");
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onViewStateRestored(Bundle savedInstanceState) {
		Log.e("wjzwjz", "NearByFragment onViewStateRestored");
		super.onViewStateRestored(savedInstanceState);
	}

	@Override
	public void onStart() {
		Log.e("wjzwjz", "NearByFragment onStart");
		super.onStart();
	}

	@Override
	public void onStop() {
		Log.e("wjzwjz", "NearByFragment onStop");
		super.onStop();
	}

	@Override
	public void onDetach() {
		Log.e("wjzwjz", "NearByFragment onDetach");
		super.onDetach();
	}

	@Override
	public void onDestroy() {
		Log.e("wjzwjz", "NearByFragment onDestroy");
		if (mAMapLocationManager != null) {
			mAMapLocationManager.removeUpdates(this);
			mAMapLocationManager.destory();
			locationHandler.removeCallbacks(locationRunnable);
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

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

		// 已经点过，直接返回
		if (isClick) {
			return;
		}

		// 设置已点击标志，避免快速重复点击
		isClick = true;
		// 解锁
		try {
			if (null != merchantList && !merchantList.isEmpty() && null != merchantList.get(position) && StringUtils.isNotNull(merchantList.get(position).id)) {
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				Intent intent = new Intent();
				intent.setClass(getActivity(), MerchantDetailActivity.class);
				intent.putExtra("merchantId", merchantList.get(position).id);
				getActivity().startActivity(intent);

			} else {
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				AlertDialog.Builder builder = new Builder(getActivity());
				builder.setTitle("温馨提示");
				builder.setMessage("对不起，该商家未在取号系统注册。");
				builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				builder.create().show();
			}

		} catch (Exception e) {
			unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			AlertDialog.Builder builder = new Builder(getActivity());
			builder.setTitle("温馨提示");
			builder.setMessage("对不起，网络异常。");
			builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
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
	public void onLocationChanged(AMapLocation location) {
		Log.e("wjzwjz", "NearByFragment onLocationChanged : " + (System.currentTimeMillis()-time1));
		if (null != location) {
			getCategoriesFromServerAndDisplay(location.getCityCode());
			QHClientApplication.getInstance().location = location;
			if (!isFirstLocation) {
				isFirstLocation = true;
				firstLocation = location;
				
				merchantList = new ArrayList<Merchant>();
				queryNearbyMerchants();
			} else {
				float distance = firstLocation.distanceTo(location);
				if (distance > 100) {
					firstLocation = location;
					merchantList = new ArrayList<Merchant>();
					
					queryNearbyMerchants();
				} else {
					return;
				}

			}

		}
	}

	private void queryNearbyMerchants() {
		if(null == firstLocation)
		{
			Toast.makeText(getActivity(), "亲，现在没有定位信息，不能查看哦。", Toast.LENGTH_SHORT).show();
			return;
		}
		
		String type = categoryType;
		if ("-1".equals(categoryType)) {
			type = "";
		}
		
		String url = "getNearMerchants?userX=" + firstLocation.getLongitude() + "&userY=" + firstLocation.getLatitude() + "&cityCode=" + firstLocation.getCityCode()
				+ "&page=" + page + "&maxDis=" + searchDistence + "&cateType=" + type;
		final NearbyMerchantsTask task = new NearbyMerchantsTask(0, getActivity(), url);
		task.execute(new Runnable() {

			@Override
			public void run() {

				String result = task.result;
				List<Merchant> tempList = ParseJson.getMerchants(result);
				if (null == tempList || tempList.isEmpty() || tempList.size() < 20) {
					needToLoad = false;
				}
				if (null == merchantList) {
					merchantList = new ArrayList<Merchant>();
				}
				merchantList.addAll(tempList);
				Log.e("wjzwjz ", " success merchant list size : " + merchantList.size());
				updateMerchantsHandler.obtainMessage(200, null).sendToTarget();
			}
		}, new Runnable() {

			@Override
			public void run() {
				Log.e("wjzwjz ", " error merchant list size : " + merchantList.size());
				needToLoad = false;
				updateMerchantsHandler.obtainMessage(200, null).sendToTarget();
			}
		});
	}

	@Override
	public void onResume() {
		Log.e("wjzwjz", "NearByFragment onResume");
		super.onResume();
		// contentView.findViewById(R.id.loadingbar).setVisibility(View.VISIBLE);
		// contentView.findViewById(R.id.serverdata).setVisibility(View.GONE);
		
		Thread requestLocation = new Thread(new Runnable() {

			@Override
			public void run() {
				Looper.prepare();
				try {
					if (mAMapLocationManager == null) {

						mAMapLocationManager = LocationManagerProxy
								.getInstance(getActivity());
						/*
						 * mAMapLocManager.setGpsEnable(false);//
						 * 1.0.2版本新增方法，设置true表示混合定位中包含gps定位，false表示纯网络定位，默认是true
						 */
						// Location SDK定位采用GPS和网络混合定位方式，时间最短是5000毫秒，否则无效
						time1 = System.currentTimeMillis();
						mAMapLocationManager.requestLocationUpdates(
								LocationProviderProxy.AMapNetwork, 10000, 100,
								NearbyFragment.this);
						Log.e("wjzwjz", "nearby location manager : " + (System.currentTimeMillis()-time1));
						locationHandler.removeCallbacks(locationRunnable);
						locationHandler.postDelayed(locationRunnable, 60000);// 设置超过12秒还没有定位到就停止定位
					} else {
						/*
						 * mAMapLocManager.setGpsEnable(false);//
						 * 1.0.2版本新增方法，设置true表示混合定位中包含gps定位，false表示纯网络定位，默认是true
						 */
						// Location SDK定位采用GPS和网络混合定位方式，时间最短是5000毫秒，否则无效
						mAMapLocationManager.requestLocationUpdates(
								LocationProviderProxy.AMapNetwork, 10000, 100,
								NearbyFragment.this);
						locationHandler.removeCallbacks(locationRunnable);
						locationHandler.postDelayed(locationRunnable, 60000);// 设置超过12秒还没有定位到就停止定位

					}

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
//		
//		if (mAMapLocationManager == null) {
//			mAMapLocationManager = LocationManagerProxy.getInstance(getActivity());
//			/*
//			 * mAMapLocManager.setGpsEnable(false);//
//			 * 1.0.2版本新增方法，设置true表示混合定位中包含gps定位，false表示纯网络定位，默认是true
//			 */
//			// Location SDK定位采用GPS和网络混合定位方式，时间最短是5000毫秒，否则无效
//			mAMapLocationManager.requestLocationUpdates(LocationProviderProxy.AMapNetwork, 10000, 100, this);
//			locationHandler.postDelayed(locationRunnable, 60000);// 设置超过12秒还没有定位到就停止定位
//			
//		}
		// buildTask();
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
		case R.id.location_result:
			contentView.findViewById(R.id.loadingbar).setVisibility(View.VISIBLE);
			contentView.findViewById(R.id.serverdata).setVisibility(View.GONE);
			resultLayout.setVisibility(View.VISIBLE);
			noResultLayout.setVisibility(View.GONE);
			locationResult.setVisibility(View.GONE);
			
			Thread requestLocation = new Thread(new Runnable() {

				@Override
				public void run() {
					Looper.prepare();
					try {
						if (mAMapLocationManager == null) {

							mAMapLocationManager = LocationManagerProxy
									.getInstance(getActivity());
							/*
							 * mAMapLocManager.setGpsEnable(false);//
							 * 1.0.2版本新增方法，设置true表示混合定位中包含gps定位，false表示纯网络定位，默认是true
							 */
							// Location SDK定位采用GPS和网络混合定位方式，时间最短是5000毫秒，否则无效
							mAMapLocationManager.requestLocationUpdates(
									LocationProviderProxy.AMapNetwork, 10000, 100,
									NearbyFragment.this);
							locationHandler.removeCallbacks(locationRunnable);
							locationHandler.postDelayed(locationRunnable, 60000);// 设置超过12秒还没有定位到就停止定位
						} else {
							/*
							 * mAMapLocManager.setGpsEnable(false);//
							 * 1.0.2版本新增方法，设置true表示混合定位中包含gps定位，false表示纯网络定位，默认是true
							 */
							// Location SDK定位采用GPS和网络混合定位方式，时间最短是5000毫秒，否则无效
							mAMapLocationManager.requestLocationUpdates(
									LocationProviderProxy.AMapNetwork, 10000, 100,
									NearbyFragment.this);
							locationHandler.removeCallbacks(locationRunnable);
							locationHandler.postDelayed(locationRunnable, 60000);// 设置超过12秒还没有定位到就停止定位

						}

					} catch (Exception e) {
						
					}
					finally
					{
						Looper.loop();
					}
				}
			});
			requestLocation.start();
			
			
//			if (mAMapLocationManager == null) {
//				mAMapLocationManager = LocationManagerProxy.getInstance(getActivity());
//				/*
//				 * mAMapLocManager.setGpsEnable(false);//
//				 * 1.0.2版本新增方法，设置true表示混合定位中包含gps定位，false表示纯网络定位，默认是true
//				 */
//				// Location SDK定位采用GPS和网络混合定位方式，时间最短是5000毫秒，否则无效
//				mAMapLocationManager.requestLocationUpdates(LocationProviderProxy.AMapNetwork, 10000, 100, this);
//				locationHandler.removeCallbacks(locationRunnable);
//				locationHandler.postDelayed(locationRunnable, 60000);
//				
//			}
//			else
//			{
//				mAMapLocationManager.requestLocationUpdates(LocationProviderProxy.AMapNetwork, 10000, 100, this);
//				locationHandler.removeCallbacks(locationRunnable);
//				locationHandler.postDelayed(locationRunnable, 60000);
//			}
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
			if(view.getParent()!=null) {
				group.removeView(view);
			}
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
			if(view2.getParent()!=null) {
			
				group.removeView(view2);
			}
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
	public void onHeaderRefresh(PullToRefreshView view) {
		page = 1;
		// isFirstLoad = true;
		needToLoad = true;
		// isFirstLocation = false;
		// firstLocation = null;

		merchantList = new ArrayList<Merchant>();
		queryNearbyMerchants();

	}

	@Override
	public void onFooterRefresh(PullToRefreshView view) {
		page++;
		queryNearbyMerchants();
		// mPullToRefreshView.postDelayed(new Runnable() {
		//
		// @Override
		// public void run() {
		// // 处理下拉刷新最新数据
		// // merchantList = new ArrayList<Merchant>();
		//
		//
		// }
		// }, 1000);

	}

}