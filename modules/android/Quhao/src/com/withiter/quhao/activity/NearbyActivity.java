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
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiItemDetail;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.amap.api.services.poisearch.PoiSearch.OnPoiSearchListener;
import com.amap.api.services.poisearch.PoiSearch.SearchBound;
import com.withiter.quhao.R;
import com.withiter.quhao.adapter.MerchantNearByAdapter;
import com.withiter.quhao.task.NearbySearchMerchantTask;
import com.withiter.quhao.util.StringUtils;
import com.withiter.quhao.view.expandtab.ExpandTabView;
import com.withiter.quhao.view.expandtab.ViewLeft;

public class NearbyActivity extends QuhaoBaseActivity implements
		AMapLocationListener, OnPoiSearchListener, OnScrollListener,
		OnItemClickListener {

	private LocationManagerProxy mAMapLocationManager = null;
	private PoiSearch.Query query;
	private PoiSearch poiSearch;
	private PoiResult poiResult; // poi返回的结果
	private int page = 0;
	private ListView merchantsListView;
	private MerchantNearByAdapter nearByAdapter;
	private View moreView;
	private Button bt;
	private ProgressBar pg;
	private int lastVisibleIndex;
	private boolean isFirstLoad = true;
	private boolean needToLoad = true;
	private List<PoiItem> poiItems;

	private boolean isFirstLocation = false;
	
	private AMapLocation firstLocation = null;
	
	private ExpandTabView expandTabView;
	private ArrayList<View> mViewArray = new ArrayList<View>();
	private ViewLeft viewLeft;
	
	private int searchDistence;
	
	private List<String> distanceItems;
	
	private List<String> distanceItemsValue;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.nearby_layout);
		super.onCreate(savedInstanceState);

		// bind menu button function
		btnCategory.setOnClickListener(goCategory(this));
		btnNearby.setOnClickListener(goNearby(this));
		btnPerson.setOnClickListener(goPersonCenter(this));
		btnMore.setOnClickListener(goMore(this));

		merchantsListView = (ListView) this
				.findViewById(R.id.merchantsListView);
		moreView = getLayoutInflater().inflate(R.layout.moredata, null);
		bt = (Button) moreView.findViewById(R.id.bt_load);
		pg = (ProgressBar) moreView.findViewById(R.id.pg);
		bt.setOnClickListener(this);
		merchantsListView.addFooterView(moreView);
		merchantsListView.setNextFocusDownId(R.id.merchantsListView);
		
		initExpandView();
		
		// TODO add default view here
		if (!networkOK) {
			unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			Builder dialog = new AlertDialog.Builder(NearbyActivity.this);
			dialog.setTitle("温馨提示").setMessage("Wifi/蜂窝网络未打开，或者网络情况不是很好哟").setPositiveButton("确定", null);
			dialog.show();
			
			return;
		}
		
//		setPoiSearch();
//		buildTask();
//		 Thread queryMerchantsThread = new Thread(new Runnable() {
//		
//		 @Override
//		 public void run() {
//		 try
//		 {
//		 Looper.prepare();
//		 queryMerchants();
//		
//		 }catch (Exception e) {
//		
//		 }
//		 finally
//		 {
//		 Looper.loop();
//		 }
//		
//		
//		 }
//		 });
//		 queryMerchantsThread.start();
	}

	private void buildTask() {
		
		/*
		final NearbyMerchantsTask task = new NearbyMerchantsTask(R.string.waitting, this, poiSearch);
		task.execute(new Runnable() {
			
			@Override
			public void run() {
				poiResult = task.poiResult;
				poiItems = new ArrayList<PoiItem>();
				if (null != poiResult && null != poiResult.getQuery()) {
					if(poiResult.getQuery().equals(query))
					{
						List<PoiItem> poiItemTemps = poiResult.getPois();// 取得第一页的poiitem数据，页数从数字0开始
						if (null != poiItemTemps && poiItemTemps.size() > 0) {
							if (poiItemTemps.size() < 10) {
								needToLoad = false;
							}
							if(null == poiItems)
							{
								poiItems = new ArrayList<PoiItem>();
							}
							poiItems.clear();
							poiItems.addAll(poiItemTemps);
							Log.e("TAG111",
									"poi items size : " + poiItemTemps.size() + " , page : " + page);
							updatePoiItemsHandler.obtainMessage(200, null)
									.sendToTarget();
							
						} else {
							needToLoad = false;
						}
					}
					else
					{
						needToLoad = false;
					}
					
					
				} else {
					needToLoad = false;
				}
				
			}
		},new Runnable() {
			
			@Override
			public void run() {
				Builder dialog = new AlertDialog.Builder(NearbyActivity.this);
				dialog.setTitle("温馨提示").setMessage("Wifi/蜂窝网络未打开，或者网络情况不是很好哟").setPositiveButton("确定", null);
				dialog.show();
				
			}
		});
		*/
	}

	private void setPoiSearch() {
		page = 0;
		query = new PoiSearch.Query("", "餐厅", "021");
		query.setPageSize(10);// 设置每页最多返回多少条poiitem
		query.setPageNum(page);// 设置查第一页
		query.setLimitDiscount(false);
		query.setLimitGroupbuy(false);
		poiSearch = new PoiSearch(this, query);
		poiSearch.setOnPoiSearchListener(this);
//		 double lat = location.getLatitude();
//		 double lon = location.getLongitude();
//		LatLonPoint lp = new LatLonPoint(firstLocation.getLatitude(), firstLocation.getLongitude());
		LatLonPoint lp = new LatLonPoint(31.235048, 121.474794);
		poiSearch.setBound(new SearchBound(lp, searchDistence));// 设置搜索区域为以lp点为圆心，其周围1000米范围
	}

	private void initExpandView() {
		
		if(searchDistence == 0)
		{
			searchDistence = 1000;
//			distanceItems = new String[] { "100米", "200米", "3000米", "4000米", "50000米" };//显示字段
			distanceItems = new ArrayList<String>();
			distanceItems.add("100米");
			distanceItems.add("200米");
			distanceItems.add("3000米");
			distanceItems.add("4000米");
			distanceItems.add("5000米");
			
			distanceItemsValue = new ArrayList<String>();
			distanceItemsValue.add("100");
			distanceItemsValue.add("200");
			distanceItemsValue.add("3000");
			distanceItemsValue.add("4000");
			distanceItemsValue.add("5000");
//			distanceItemsValue = new String[] { "100", "200", "3000", "4000", "50000" };//显示字段
		}
		
		expandTabView = (ExpandTabView) this.findViewById(R.id.expandtab_view);
		viewLeft = new ViewLeft(this,distanceItems,distanceItemsValue,String.valueOf(searchDistence));
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
		
		for (int i = 0; i < distanceItems.size(); i++) {
			if(showText.equals(distanceItems.get(i)))
			{
				searchDistence = Integer.valueOf(distanceItemsValue.get(i)); 
				break;
			}
		}
//		setPoiSearch();
//		buildTask();
		merchantsListView.setSelectionFromTop(0, 0);// 滑动到第一项
		setPoiSearch();
		Thread queryMerchantsThread = new Thread(queryRunnable);
		queryMerchantsThread.start();
		
		Toast.makeText(NearbyActivity.this, showText, Toast.LENGTH_SHORT).show();

	}
	
	private int getPositon(View tView) {
		for (int i = 0; i < mViewArray.size(); i++) {
			if (mViewArray.get(i) == tView) {
				return i;
			}
		}
		return -1;
	}

	protected Handler updatePoiItemsHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			super.handleMessage(msg);

			if (msg.what == 200) {

				/*
				if (isFirstLoad) {

					nearByAdapter = new MerchantNearByAdapter(
							NearbyActivity.this, merchantsListView, poiItems);
					merchantsListView.setAdapter(nearByAdapter);
					isFirstLoad = false;
				} else {
					nearByAdapter.merchants = poiItems;
				}
				nearByAdapter.notifyDataSetChanged();
				bt.setVisibility(View.VISIBLE);
				pg.setVisibility(View.GONE);
				merchantsListView.setOnScrollListener(NearbyActivity.this);
				merchantsListView.setOnItemClickListener(NearbyActivity.this);*/
				findViewById(R.id.loadingbar).setVisibility(View.GONE);
				findViewById(R.id.serverdata).setVisibility(View.VISIBLE);
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			}
		}

	};

	/*
	private void queryMerchants() {

		setPoiSearch();
		try {
			poiItems = new ArrayList<PoiItem>();
			long start = System.currentTimeMillis();

			poiResult = poiSearch.searchPOI();
			long end = System.currentTimeMillis();
			
			Log.e("wjzwjz : ", "the date : " + (end-start));
			if (null != poiResult && null != poiResult.getQuery()) {
				List<PoiItem> poiItemTemps = poiResult.getPois();// 取得第一页的poiitem数据，页数从数字0开始
				if (null != poiItemTemps && poiItemTemps.size() > 0) {
					if (poiItemTemps.size() < 10) {
						needToLoad = false;
					}
					poiItems = new ArrayList<PoiItem>();
					poiItems.addAll(poiItemTemps);
					updatePoiItemsHandler.obtainMessage(200, null)
							.sendToTarget();
					Log.e("TAG111", poiItemTemps.toString());
					Log.e("TAG222", "first query page count : " + poiResult.getPageCount() + " , searchDistence : " + searchDistence);
				} else {
					needToLoad = false;
				}

			} else {
				needToLoad = false;
			}
		} catch (AMapException e) {
			needToLoad = false;
			e.printStackTrace();
		}// 异步搜索
	}*/

	/**
	 * 点击下一页poi搜索
	 */
	public void nextSearch() {
		if (query != null && poiSearch != null && poiResult != null) {
			if (poiResult.getPageCount() - 1 > page) {
				page++;
				query.setPageNum(page);// 设置查后一页
				/*
				final NearbyMerchantsTask task = new NearbyMerchantsTask(R.string.waitting, this, poiSearch);
				task.execute(new Runnable() {
					
					@Override
					public void run() {
						poiResult = task.poiResult;
						if (null != poiResult && null != poiResult.getQuery()) {
							if(poiResult.getQuery().equals(query))
							{
								List<PoiItem> poiItemTemps = poiResult.getPois();// 取得第一页的poiitem数据，页数从数字0开始
								if (null != poiItemTemps && poiItemTemps.size() > 0) {
									if (poiItemTemps.size() < 10) {
										needToLoad = false;
									}
									if(null == poiItems)
									{
										poiItems = new ArrayList<PoiItem>();
									}
									poiItems.addAll(poiItemTemps);
									Log.e("TAG111", String.valueOf(poiItemTemps.size()));
									updatePoiItemsHandler.obtainMessage(200, null)
											.sendToTarget();
									
								} else {
									needToLoad = false;
								}
							}
							else
							{
								needToLoad = false;
							}
							
							
						} else {
							needToLoad = false;
						}
						
					}
				},new Runnable() {
					
					@Override
					public void run() {
						Builder dialog = new AlertDialog.Builder(NearbyActivity.this);
						dialog.setTitle("温馨提示").setMessage("Wifi/蜂窝网络未打开，或者网络情况不是很好哟").setPositiveButton("确定", null);
						dialog.show();
						
					}
				});
				*/
				
				PoiResult result;
				try {
					result = poiSearch.searchPOI();
					if (null != result && null != result.getQuery()) {
						if (result.getQuery().equals(query)) {
							poiResult = result;
							List<PoiItem> poiItemTemps = result.getPois();// 取得第一页的poiitem数据，页数从数字0开始
							if (null != poiItemTemps && poiItemTemps.size() > 0) {
								if (poiItemTemps.size() < 10) {
									needToLoad = false;
								}
								if (null == poiItems) {
									poiItems = new ArrayList<PoiItem>();
								}
								poiItems.addAll(poiItemTemps);
								Log.e("TAG111",
										"poi items size : " + poiItemTemps.size() + " , page : " + page);
								Log.e("TAG222", "next search query page count : " + poiResult.getPageCount() + " , searchDistence : " + searchDistence);
								updatePoiItemsHandler.obtainMessage(200, null)
										.sendToTarget();
								
							} else {
								needToLoad = false;
							}
						} else {
							needToLoad = false;
						}

					} else {
						needToLoad = false;
					}
				} catch (AMapException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				/**/
				/*
				 * try { poiResult = poiSearch.searchPOI(); if (null !=
				 * poiResult && null != poiResult.getQuery()) {
				 * List<PoiItem> poiItemTemps = poiResult.getPois();//
				 * 取得第一页的poiitem数据，页数从数字0开始 if (null != poiItemTemps &&
				 * poiItemTemps.size() > 0) { if (poiItemTemps.size() < 10)
				 * { needToLoad = false; } poiItems.addAll(poiItemTemps);
				 * updatePoiItemsHandler.obtainMessage(200, null)
				 * .sendToTarget(); Log.e("TAG111",
				 * poiItemTemps.toString()); } else { needToLoad = false; }
				 * 
				 * } else { needToLoad = false; } } catch (AMapException e)
				 * { needToLoad = false; e.printStackTrace(); }
				 */
			} else {
				needToLoad = false;
				Toast.makeText(this, "No result", Toast.LENGTH_SHORT).show();
			}
		} else {
			needToLoad = false;
			Toast.makeText(this, "No result", Toast.LENGTH_SHORT).show();
		}

	}
	
	@Override
	protected void onPause() {
		super.onPause();
		if (mAMapLocationManager != null) {
			mAMapLocationManager.removeUpdates(this);
		}
	}

	@Override
	protected void onDestroy() {
		if (mAMapLocationManager != null) {
			mAMapLocationManager.removeUpdates(this);
			mAMapLocationManager.destory();
		}
		mAMapLocationManager = null;
		super.onDestroy();
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return false;
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
	public void onLocationChanged(AMapLocation location) {

		if (null != location) {
			if(!isFirstLocation)
			{
				isFirstLocation = true;
				firstLocation = location;
//				setPoiSearch();
//				buildTask();
//				query = new PoiSearch.Query("", "餐厅", location.getCityCode());
//				query.setLimitDiscount(false);
//				query.setLimitGroupbuy(false);
//				poiSearch = new PoiSearch(this, query);
//				poiSearch.setOnPoiSearchListener(this);
//				double lat = location.getLatitude();
//				double lon = location.getLongitude();
//				LatLonPoint lp = new LatLonPoint(lat, lon);
//				poiSearch.setBound(new SearchBound(lp, searchDistence));// 设置搜索区域为以lp点为圆心，其周围1000米范围
////				poiSearch.searchPOIAsyn();
//				new Thread(queryRunnable).start();
				setPoiSearch();
				Thread queryMerchantsThread = new Thread(queryRunnable);
				queryMerchantsThread.start();
				/*
				Thread queryMerchantsThread = new Thread(new Runnable() {
					
					 @Override
					 public void run() {
					 try
					 {
					 Looper.prepare();
					 queryMerchants();
					
					 }catch (Exception e) {
					
					 }
					 finally
					 {
					 Looper.loop();
					 }
					
					
					 }
					 });
					 queryMerchantsThread.start();*/
			}
			else
			{
				float distance = firstLocation.distanceTo(location);
				if(distance>100)
				{
					firstLocation = location;
//					setPoiSearch();
//					buildTask();
//					progressDialogUtil = new ProgressDialogUtil(this, R.string.empty, R.string.waitting, false);
//					progressDialogUtil.showProgress();
//					if(null == poiItems)
//					{
//						poiItems = new ArrayList<PoiItem>();
//					}
//					poiItems.clear();
//					query = new PoiSearch.Query("", "餐厅", location.getCityCode());
//					query.setLimitDiscount(false);
//					query.setLimitGroupbuy(false);
//					poiSearch = new PoiSearch(this, query);
//					poiSearch.setOnPoiSearchListener(this);
//					double lat = location.getLatitude();
//					double lon = location.getLongitude();
//					LatLonPoint lp = new LatLonPoint(lat, lon);
//					poiSearch.setBound(new SearchBound(lp, searchDistence));// 设置搜索区域为以lp点为圆心，其周围1000米范围
////					poiSearch.searchPOIAsyn();
//					new Thread(queryRunnable).start();
					setPoiSearch();
					Thread queryMerchantsThread = new Thread(queryRunnable);
					queryMerchantsThread.start();
				}
				else
				{
					return;
				}
				
			}
			
			/*
			query = new PoiSearch.Query("", "餐厅", location.getCityCode());
			query.setLimitDiscount(false);
			query.setLimitGroupbuy(false);
			poiSearch = new PoiSearch(this, query);
			poiSearch.setOnPoiSearchListener(this);
			double lat = location.getLatitude();
			double lon = location.getLongitude();
			LatLonPoint lp = new LatLonPoint(lat, lon);
			poiSearch.setBound(new SearchBound(lp, 1000));// 设置搜索区域为以lp点为圆心，其周围1000米范围
			*/
			
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		findViewById(R.id.loadingbar).setVisibility(View.VISIBLE);
		findViewById(R.id.serverdata).setVisibility(View.GONE);
		
		page = 0;
		isFirstLoad = true;
		needToLoad = true;
		isFirstLocation = false;
		firstLocation = null;
		
//		mAMapLocationManager = LocationManagerProxy.getInstance(this);
//		mAMapLocationManager.requestLocationUpdates(
//				LocationProviderProxy.AMapNetwork, 5000, 10, this);
		
		setPoiSearch();
//		buildTask();
		Thread queryMerchantsThread = new Thread(queryRunnable);
		queryMerchantsThread.start();
	};
		
	// 使用线程访问网络，否则APP会挂掉
	private Runnable queryRunnable = new Runnable(){
		@Override
		public void run() {
			try {
				Looper.prepare();
				//TODO : 需要改为定位后的location
				LatLonPoint lp = new LatLonPoint(31.235048, 121.474794);
				poiSearch.setBound(new SearchBound(lp, searchDistence));// 设置搜索区域为以lp点为圆心，其周围1000米范围
				PoiResult result = poiSearch.searchPOI();
				if (null != result && null != result.getQuery()) {
					if(result.getQuery().equals(query))
					{
						poiResult = result;
						List<PoiItem> poiItemTemps = result.getPois();// 取得第一页的poiitem数据，页数从数字0开始
						if (null != poiItemTemps && poiItemTemps.size() > 0) {
							if (poiItemTemps.size() < 10) {
								needToLoad = false;
							}
							if(null == poiItems)
							{
								poiItems = new ArrayList<PoiItem>();
							}
							poiItems.clear();
							poiItems.addAll(poiItemTemps);
							Log.e("TAG111", poiItemTemps.toString());
							Log.e("TAG222", "first query page count : " + poiResult.getPageCount() + " , searchDistence : " + searchDistence);
							Log.e("TAG111",
									"poi items size : " + poiItemTemps.size() + " , page : " + page);
							updatePoiItemsHandler.obtainMessage(200, null)
									.sendToTarget();
							
						} else {
							needToLoad = false;
						}
					}
					else
					{
						needToLoad = false;
					}
					
					
				} else {
					needToLoad = false;
				}
			} catch (AMapException e) {
				e.printStackTrace();
			}// 异步搜索
			finally
			{
				Looper.loop();
			}
		}
	};
	
	@Override
	public void onPoiItemDetailSearched(PoiItemDetail arg0, int arg1) {

	}

	@Override
	public void onPoiSearched(PoiResult result, int rCode) {
		if(rCode == 0)
		{
			if (null != result && null != result.getQuery()) {
				if(result.getQuery().equals(query))
				{
					poiResult = result;
					if(null == poiItems)
					{
						poiItems = new ArrayList<PoiItem>();
					}
					List<PoiItem> poiItemTemps = result.getPois();// 取得第一页的poiitem数据，页数从数字0开始
					if (poiItemTemps.size() < 10) {
						needToLoad = false;
					}
					poiItems.addAll(poiItemTemps);
					poiItems = poiResult.getPois();
					Log.e("TAG111",
							"poi items size : " + poiItemTemps.size() + " , page : " + page);
					updatePoiItemsHandler.obtainMessage(200, null).sendToTarget();
					
				} else {
					needToLoad = false;
				}
				
			} else {
				needToLoad = false;
			}
			
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (scrollState == OnScrollListener.SCROLL_STATE_IDLE
				&& lastVisibleIndex == nearByAdapter.getCount()) {
			pg.setVisibility(View.VISIBLE);
			bt.setVisibility(View.GONE);
//			nextSearch();
			
			Thread queryMerchantsThread = new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						Looper.prepare();
						nextSearch();

					} catch (Exception e) {

					} finally {
						Looper.loop();
					}

				}
			});
			queryMerchantsThread.start();
			
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// check hit the bottom of current loaded data
		lastVisibleIndex = firstVisibleItem + visibleItemCount - 1;
		if (!needToLoad) {
			merchantsListView.removeFooterView(moreView);
		}
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
		unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);

		switch (v.getId()) {
		case R.id.back_btn:
			onBackPressed();
			this.finish();
			break;
		case R.id.bt_load:
			pg.setVisibility(View.VISIBLE);
			bt.setVisibility(View.GONE);
//			nextSearch();
			
			Thread queryMerchantsThread = new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						Looper.prepare();
						nextSearch();

					} catch (Exception e) {

					} finally {
						Looper.loop();
					}

				}
			});
			queryMerchantsThread.start();
			
			break;
		default:
			break;
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
			String poiId = poiItems.get(position).getPoiId();
			if (StringUtils.isNotNull(poiId)) {
				final NearbySearchMerchantTask task = new NearbySearchMerchantTask(R.string.waitting, NearbyActivity.this, "queryMerchantByPoiId?poiId=" + poiId);
				task.execute(new Runnable() {
					String buf = task.result;
					@Override
					public void run() {
						unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
						Intent intent = new Intent();
						intent.setClass(NearbyActivity.this, MerchantDetailActivity.class);
						intent.putExtra("merchantId", buf);
						NearbyActivity.this.startActivity(intent);
						overridePendingTransition(R.anim.in_from_right,
								R.anim.out_to_left);
						
					}
				},new Runnable() {
					
					@Override
					public void run() {
						unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
						AlertDialog.Builder builder = new Builder(NearbyActivity.this);
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
				});
//				String buf = CommonHTTPRequest
//						.get("queryMerchantByPoiId?poiId=" + poiId);
				
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
