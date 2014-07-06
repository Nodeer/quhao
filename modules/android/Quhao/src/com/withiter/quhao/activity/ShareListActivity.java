package com.withiter.quhao.activity;

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
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.withiter.quhao.QHClientApplication;
import com.withiter.quhao.R;
import com.withiter.quhao.adapter.ShareListAdapter;
import com.withiter.quhao.task.GetShareListTask;
import com.withiter.quhao.task.JsonPack;
import com.withiter.quhao.util.ActivityUtil;
import com.withiter.quhao.util.QuhaoLog;
import com.withiter.quhao.util.tool.ParseJson;
import com.withiter.quhao.view.refresh.PullToRefreshView;
import com.withiter.quhao.view.refresh.PullToRefreshView.OnFooterRefreshListener;
import com.withiter.quhao.view.refresh.PullToRefreshView.OnHeaderRefreshListener;
import com.withiter.quhao.vo.ShareVO;

public class ShareListActivity extends QuhaoBaseActivity  implements AMapLocationListener, OnClickListener,
	OnHeaderRefreshListener, OnFooterRefreshListener{

	private String LOGTAG = ShareListActivity.class.getName();
	protected ListView shareListView;
	private List<ShareVO> shareList;
	private ShareListAdapter shareListAdapter;
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
	
	private LocationManagerProxy mAMapLocationManager = null;
	
	private Button btnCreateShare;
	
	private Handler locationHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg) {
			
			super.handleMessage(msg);
			
			if (firstLocation == null) {
				ShareListActivity.this.findViewById(R.id.loadingbar).setVisibility(View.GONE);
				ShareListActivity.this.findViewById(R.id.serverdata).setVisibility(View.VISIBLE);
				resultLayout.setVisibility(View.GONE);
				noResultLayout.setVisibility(View.VISIBLE);
				noResultView.setText(R.string.location_failed);
				locationResult.setText(R.string.re_location);
				locationResult.setVisibility(View.VISIBLE);
				if (ActivityUtil.isTopActivy(ShareListActivity.this, ShareListActivity.class.getName())) {
					Toast.makeText(ShareListActivity.this, "亲，定位失败，请检查网络状态！", Toast.LENGTH_SHORT).show();
				}
				
				stopLocation();// 销毁掉定位
			}
		}
		
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.share_list_layout);
		super.onCreate(savedInstanceState);

		this.shareList = new ArrayList<ShareVO>();

		this.page = getIntent().getIntExtra("page", 1);
		isFirstLoad = true;
		needToLoad = true;
		isFirstLocation = false;
		firstLocation = null;
		
		QuhaoLog.i(LOGTAG, "init page is : " + this.page);

		btnBack.setOnClickListener(goBack(this, this.getClass().getName()));

		btnCreateShare = (Button) this.findViewById(R.id.btn_create_share);
		btnCreateShare.setOnClickListener(this);
		
		resultLayout = (LinearLayout) this.findViewById(R.id.result_layout);
		noResultLayout = (LinearLayout) this.findViewById(R.id.no_result_layout);
		noResultView = (TextView) this.findViewById(R.id.no_result_text);
		locationResult = (TextView) this.findViewById(R.id.location_result);
		locationResult.setOnClickListener(this);
		
		mPullToRefreshView = (PullToRefreshView) this.findViewById(R.id.main_pull_refresh_view);
		mPullToRefreshView.setEnableFooterView(false);
		mPullToRefreshView.setOnHeaderRefreshListener(this);
		mPullToRefreshView.setOnFooterRefreshListener(this);
		
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
		
		Log.e("", "wjzwjz stopLocation location : " + mAMapLocationManager);
		if (mAMapLocationManager != null) {
			mAMapLocationManager.removeUpdates(this);
			mAMapLocationManager.destory();
			mAMapLocationManager = null;
		}
		
	}
	
	private void initView() {
		shareListView = (ListView) findViewById(R.id.sharesListView);
		shareListView.setNextFocusDownId(R.id.sharesListView);
		shareListView.setVisibility(View.GONE);
//		expandTabView = (ExpandTabView) this.findViewById(R.id.expandtab_view);
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
								.getInstance(ShareListActivity.this);
						/*
						 * mAMapLocManager.setGpsEnable(false);//
						 * 1.0.2版本新增方法，设置true表示混合定位中包含gps定位，false表示纯网络定位，默认是true
						 */
						// Location SDK定位采用GPS和网络混合定位方式，时间最短是5000毫秒，否则无效
						mAMapLocationManager.requestLocationUpdates(
								LocationProviderProxy.AMapNetwork, 10000, 100,
								ShareListActivity.this);
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
		case R.id.btn_create_share:
			unlockHandler.sendEmptyMessage(UNLOCK_CLICK);
			Intent intent = new Intent();
			intent.setClass(this, CreateShareActivity.class);
			startActivity(intent);
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
							.getInstance(ShareListActivity.this);
					/*
					 * mAMapLocManager.setGpsEnable(false);//
					 * 1.0.2版本新增方法，设置true表示混合定位中包含gps定位，false表示纯网络定位，默认是true
					 */
					// Location SDK定位采用GPS和网络混合定位方式，时间最短是5000毫秒，否则无效
					mAMapLocationManager.requestLocationUpdates(
							LocationProviderProxy.AMapNetwork, 10000, 100,
							ShareListActivity.this);
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
				ShareListActivity.this.page += 1;
				queryShares();
			}
		}, 1000);
	}

	@Override
	public void onHeaderRefresh(PullToRefreshView view) {
		mPullToRefreshView.postDelayed(new Runnable() {

			@Override
			public void run() {
				ShareListActivity.this.page = 1;
				isFirstLoad = true;
				needToLoad = true;

				ShareListActivity.this.shareList = new ArrayList<ShareVO>();
				queryShares();
			}
		}, 1000);

	}

	@Override
	public void onPause() {
		super.onPause();
		stopLocation();
	}
	
	@Override
	public void onDestroy() {
		Log.e("wjzwjz", "NearbyFragment onDestroy");
		if (mAMapLocationManager != null) {
			mAMapLocationManager.removeUpdates(this);
			mAMapLocationManager.destory();
			mAMapLocationManager = null;
//			locationHandler.removeCallbacks(locationRunnable);
		}
		super.onDestroy();
	}
	
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

	protected Handler updateSharesHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			super.handleMessage(msg);

			if (msg.what == 200) {

				shareListView.setVisibility(View.VISIBLE);
				if (isFirstLoad) {

					shareListAdapter = new ShareListAdapter(
							ShareListActivity.this, shareListView, shareList,animateFirstListener);
					shareListView.setAdapter(shareListAdapter);
					isFirstLoad = false;
				} else {
					shareListAdapter.shares = shareList;
				}
				shareListAdapter.notifyDataSetChanged();
//				merchantsListView.setOnScrollListener(NearbyFragment.this);
				ShareListActivity.this.findViewById(R.id.loadingbar).setVisibility(View.GONE);
				ShareListActivity.this.findViewById(R.id.serverdata).setVisibility(View.VISIBLE);
				if(null != shareList && !shareList.isEmpty())
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
	
	private void queryShares() {
		
		String url = "";
		if(null != firstLocation)
		{
			url = "share/getNearShare?userX=" + firstLocation.getLongitude() + "&userY=" + firstLocation.getLatitude() + "&cityCode=" + firstLocation.getCityCode() + 
					"&page=" + page + "&maxDis=5";
		}
		else
		{
			url = "share/getShare?cityCode=" + QHClientApplication.getInstance().defaultCity.cityCode + "&page=" + page;
		}
		
		final GetShareListTask task = new GetShareListTask(0, this, url);
		task.execute(new Runnable() {
			
			@Override
			public void run() {
				
				JsonPack result = task.jsonPack;
				List<ShareVO> tempList = ParseJson.getShareVOs(result.getObj());
				if (null == tempList || tempList.isEmpty() || tempList.size()<=60) {
					needToLoad = false;
				}
				if(null == shareList)
				{
					shareList = new ArrayList<ShareVO>();
				}
				shareList.addAll(tempList);
				updateSharesHandler.obtainMessage(200, null).sendToTarget();
			}
		},new Runnable() {
			
			@Override
			public void run() {
				needToLoad = false;
				updateSharesHandler.obtainMessage(200, null).sendToTarget();
			}
		});
	}
	
	@Override
	public void onLocationChanged(AMapLocation location) {
		Log.e("", "wjzwjz onLocationChanged AMapLocation location : " + location);
		if (null != location) {
			
			stopLocation();
			QHClientApplication.getInstance().location = location;
			
			if(!isFirstLocation)
			{
				isFirstLocation = true;
				firstLocation = location;
				shareList = new ArrayList<ShareVO>();
				queryShares();
			}
			else
			{
				float distance = firstLocation.distanceTo(location);
				if(distance>100)
				{
					firstLocation = location;
					shareList = new ArrayList<ShareVO>();
					queryShares();
				}
				else
				{
					return;
				}
				
			}
			
		}
	}

	public void onBackPressed() {
		finish();
	}

}
