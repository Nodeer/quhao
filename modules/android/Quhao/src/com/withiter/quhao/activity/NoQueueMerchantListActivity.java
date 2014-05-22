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
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.withiter.quhao.QHClientApplication;
import com.withiter.quhao.R;
import com.withiter.quhao.adapter.MerchantNoQueueAdapter;
import com.withiter.quhao.task.QueryNoQueueMerchantsTask;
import com.withiter.quhao.util.QuhaoLog;
import com.withiter.quhao.util.StringUtils;
import com.withiter.quhao.util.tool.ParseJson;
import com.withiter.quhao.view.expandtab.ExpandTabView;
import com.withiter.quhao.view.expandtab.ViewLeft;
import com.withiter.quhao.view.refresh.PullToRefreshView;
import com.withiter.quhao.view.refresh.PullToRefreshView.OnFooterRefreshListener;
import com.withiter.quhao.view.refresh.PullToRefreshView.OnHeaderRefreshListener;
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
	
	private ExpandTabView expandTabView;
	private ArrayList<View> mViewArray = new ArrayList<View>();
	private ViewLeft viewLeft;
	
	private int searchDistence;
	
	private String[] distanceItems;
	
	private String[] distanceItemsValue;
	
	private LocationManagerProxy mAMapLocationManager = null;
	private Handler locationHandler = new Handler();

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

		if (mAMapLocationManager == null) {  
            mAMapLocationManager = LocationManagerProxy.getInstance(this);  
            /* 
             * mAMapLocManager.setGpsEnable(false);// 
             * 1.0.2版本新增方法，设置true表示混合定位中包含gps定位，false表示纯网络定位，默认是true 
             */  
            // Location SDK定位采用GPS和网络混合定位方式，时间最短是5000毫秒，否则无效  
            mAMapLocationManager.requestLocationUpdates(  
                    LocationProviderProxy.AMapNetwork, 10000, 100, this);
            
            locationHandler .postDelayed(new Runnable() {
				
				@Override
				public void run() {
					if (firstLocation == null) {
						Toast.makeText(NoQueueMerchantListActivity.this, "亲，定位失败，请检查网络状态！", Toast.LENGTH_SHORT).show();
						NoQueueMerchantListActivity.this.findViewById(R.id.loadingbar).setVisibility(View.GONE);
						NoQueueMerchantListActivity.this.findViewById(R.id.serverdata).setVisibility(View.VISIBLE);
						stopLocation();// 销毁掉定位
					}
				}
			}, 60000);// 设置超过12秒还没有定位到就停止定位
            
        }
		
		mPullToRefreshView = (PullToRefreshView) this.findViewById(R.id.main_pull_refresh_view);
		mPullToRefreshView.setOnHeaderRefreshListener(this);
		mPullToRefreshView.setOnFooterRefreshListener(this);
		mPullToRefreshView.setEnableFooterView(true);
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
			overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
		}
	};

	private void initView() {
		merchantsListView = (ListView) findViewById(R.id.merchantsListView);
		merchantsListView.setNextFocusDownId(R.id.merchantsListView);
		merchantsListView.setVisibility(View.GONE);
		merchantsListView.setOnItemClickListener(merchantItemClickListener);
		
		initExpandView();
	}
	
	private void initExpandView() {
		
		if(searchDistence == 0)
		{
			searchDistence = 1;
			distanceItems = new String[] { "1千米", "3千米", "5千米", "10千米", "全城" };// 显示字段
			distanceItemsValue = new String[] { "1", "3", "5", "10", "-1" };// 显示字段
		}
		
		expandTabView = (ExpandTabView) this.findViewById(R.id.expandtab_view);
		viewLeft = new ViewLeft(this,distanceItems,distanceItemsValue,String.valueOf(searchDistence));
		
		mViewArray = new ArrayList<View>();
		mViewArray.add(viewLeft);
		ArrayList<String> mTextArray = new ArrayList<String>();
		mTextArray.add("距离");
		expandTabView.setValue(mTextArray, mViewArray);
		expandTabView.setTitle(viewLeft.getShowText(), 0);
		
		viewLeft.setOnSelectListener(new ViewLeft.OnSelectListener() {

			@Override
			public void getValue(String distance, String showText) {
				onRefresh(viewLeft, showText);
			}
		});
	}
	
	private void onRefresh(View view, String showText) {
		
		expandTabView.onPressBack();
		int position = getPositon(view);
		if (position >= 0 && !expandTabView.getTitle(position).equals(showText)) {
			expandTabView.setTitle(showText, position);
		}
		
		for (int i = 0; i < distanceItems.length; i++) {
			if(showText.equals(distanceItems[i]))
			{
				searchDistence = Integer.valueOf(distanceItemsValue[i]); 
				break;
			}
		}
//		setPoiSearch();
//		buildTask();
		merchantsListView.setSelectionFromTop(0, 0);// 滑动到第一项
		page = 1;
//		isFirstLoad = true;
		needToLoad = true;
//		isFirstLocation = false;
//		firstLocation = null;
		
		
		merchantList = new ArrayList<Merchant>();
		queryNoQueueMerchants();
		
//		Toast.makeText(NoQueueMerchantListActivity.this, showText, Toast.LENGTH_SHORT).show();

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
		}
		if (backClicked) {
			overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
		}
	}
	
	@Override
	public void onDestroy() {
		Log.e("wjzwjz", "NearbyFragment onDestroy");
		if (mAMapLocationManager != null) {
			mAMapLocationManager.removeUpdates(this);
			mAMapLocationManager.destory();
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
				merchantsListView.setOnItemClickListener(NoQueueMerchantListActivity.this);
				NoQueueMerchantListActivity.this.findViewById(R.id.loadingbar).setVisibility(View.GONE);
				NoQueueMerchantListActivity.this.findViewById(R.id.serverdata).setVisibility(View.VISIBLE);
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
			Toast.makeText(this, "亲，现在没有定位信息，不能查看哦。", Toast.LENGTH_LONG).show();
			return;
		}
		String url = "getNearNoQueueMerchants?userX=" + firstLocation.getLongitude() + "&userY=" + firstLocation.getLatitude() + "&cityCode=" + firstLocation.getCityCode() + 
				"&page=" + page + "&maxDis=" + searchDistence;
		final QueryNoQueueMerchantsTask task = new QueryNoQueueMerchantsTask(R.string.waitting, this, url);
		task.execute(new Runnable() {
			
			@Override
			public void run() {
				
				String result = task.result;
				List<Merchant> tempList = ParseJson.getMerchants(result);
				if (null == tempList || tempList.isEmpty() || tempList.size()<20) {
					needToLoad = false;
				}
				if(null == merchantList)
				{
					merchantList = new ArrayList<Merchant>();
				}
				merchantList.addAll(tempList);
				Log.e("wjzwjz ", " success merchant list size : " + merchantList.size());
				updateMerchantsHandler.obtainMessage(200, null).sendToTarget();
			}
		},new Runnable() {
			
			@Override
			public void run() {
				Log.e("wjzwjz ", " error merchant list size : " + merchantList.size());
				needToLoad = false;
				updateMerchantsHandler.obtainMessage(200, null).sendToTarget();
			}
		});
	}
	
	@Override
	public void onLocationChanged(AMapLocation location) {

		if (null != location) {
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
				overridePendingTransition(R.anim.in_from_right,
						R.anim.out_to_left);
				
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

}
