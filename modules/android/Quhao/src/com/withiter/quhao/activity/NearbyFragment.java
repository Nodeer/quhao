package com.withiter.quhao.activity;

import java.util.ArrayList;
import java.util.Date;
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
import android.widget.ListView;
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
import com.withiter.quhao.util.ActivityUtil;
import com.withiter.quhao.util.StringUtils;
import com.withiter.quhao.view.expandtab.ExpandTabView;
import com.withiter.quhao.view.expandtab.ViewLeft;
import com.withiter.quhao.view.refresh.PullToRefreshView;
import com.withiter.quhao.view.refresh.PullToRefreshView.OnFooterRefreshListener;
import com.withiter.quhao.view.refresh.PullToRefreshView.OnHeaderRefreshListener;

public class NearbyFragment extends Fragment implements AMapLocationListener,
		OnPoiSearchListener, OnItemClickListener, OnClickListener,OnHeaderRefreshListener, OnFooterRefreshListener {

	private LocationManagerProxy mAMapLocationManager = null;
	private PoiSearch.Query query;
	private PoiSearch poiSearch;
	private PoiResult poiResult; // poi返回的结果
	private int page = 0;
	private ListView merchantsListView;
	private MerchantNearByAdapter nearByAdapter;
	private boolean isFirstLoad = true;
	private boolean needToLoad = true;
	private List<PoiItem> poiItems;

	private boolean isFirstLocation = false;
	
	private AMapLocation firstLocation = null;
	
	private ExpandTabView expandTabView;
	private ArrayList<View> mViewArray = new ArrayList<View>();
	private ViewLeft viewLeft;
	
	private int searchDistence;
	
	private String[] distanceItems;
	
	private String[] distanceItemsValue;
	
	private boolean isClick;
	
	private static final int UNLOCK_CLICK = 1000;
	
	private View contentView;
	
	private ViewGroup group;
	
	private PullToRefreshView mPullToRefreshView;
	
	protected Handler unlockHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == UNLOCK_CLICK) {
				
				isClick = false;
			}
		}
	};
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.e("wjzwjz", "NearbyFragment onCreateView");
		this.group = container;
		contentView = inflater.inflate(R.layout.nearby_fragment_layout, container,false);
		merchantsListView = (ListView) contentView
				.findViewById(R.id.merchantsListView);
		
		mPullToRefreshView = (PullToRefreshView) contentView.findViewById(R.id.main_pull_refresh_view);
		mPullToRefreshView.setEnableFooterView(true);
		mPullToRefreshView.setOnHeaderRefreshListener(this);
		mPullToRefreshView.setOnFooterRefreshListener(this);
		
		merchantsListView.setNextFocusDownId(R.id.merchantsListView);
		
		initExpandView();
		
		// TODO add default view here
		if (!ActivityUtil.isNetWorkAvailable(getActivity())) {
			Builder dialog = new AlertDialog.Builder(getActivity());
			dialog.setTitle("温馨提示").setMessage("Wifi/蜂窝网络未打开，或者网络情况不是很好哟").setPositiveButton("确定", null);
			dialog.show();
			
		}
		return contentView;
	}

	private void setPoiSearch() {
		page = 0;
		query = new PoiSearch.Query("", "餐厅", "021");
		query.setPageSize(10);// 设置每页最多返回多少条poiitem
		query.setPageNum(page);// 设置查第一页
		query.setLimitDiscount(false);
		query.setLimitGroupbuy(false);
		poiSearch = new PoiSearch(getActivity(), query);
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
			distanceItems = new String[] { "1000米", "2000米", "3000米", "4000米", "5000米" };//显示字段
			distanceItemsValue = new String[] { "1000", "2000", "3000", "4000", "5000" };//显示字段
		}
		
		expandTabView = (ExpandTabView) contentView.findViewById(R.id.expandtab_view);
		viewLeft = new ViewLeft(contentView.getContext(),distanceItems,distanceItemsValue,String.valueOf(searchDistence));
		
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
		setPoiSearch();
		Thread queryMerchantsThread = new Thread(queryRunnable);
		queryMerchantsThread.start();
		
		Toast.makeText(getActivity(), showText, Toast.LENGTH_SHORT).show();

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

				if (isFirstLoad) {

					nearByAdapter = new MerchantNearByAdapter(
							getActivity(), merchantsListView, poiItems);
					merchantsListView.setAdapter(nearByAdapter);
					isFirstLoad = false;
				} else {
					nearByAdapter.merchants = poiItems;
				}
				nearByAdapter.notifyDataSetChanged();
//				merchantsListView.setOnScrollListener(NearbyFragment.this);
				merchantsListView.setOnItemClickListener(NearbyFragment.this);
				contentView.findViewById(R.id.loadingbar).setVisibility(View.GONE);
				contentView.findViewById(R.id.serverdata).setVisibility(View.VISIBLE);
				mPullToRefreshView.onHeaderRefreshComplete();
				mPullToRefreshView.onFooterRefreshComplete();
				if(!needToLoad)
				{
					mPullToRefreshView.setEnableFooterView(false);
				}
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			}
		}

	};
	
	/**
	 * 点击下一页poi搜索
	 */
	public void nextSearch() {
		if (query != null && poiSearch != null && poiResult != null) {
			if (poiResult.getPageCount() - 1 > page) {
				page++;
				query.setPageNum(page);// 设置查后一页
				
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
					e.printStackTrace();
				}
			} else {
				needToLoad = false;
				Toast.makeText(getActivity(), "No result", Toast.LENGTH_LONG).show();
			}
		} else {
			needToLoad = false;
			Toast.makeText(getActivity(), "No result", Toast.LENGTH_LONG).show();
		}

	}
	
	@Override
	public void onPause() {
		super.onPause();
		if (mAMapLocationManager != null) {
			mAMapLocationManager.removeUpdates(this);
		}
	}

	@Override
	public void onDestroyView() {
		Log.e("wjzwjz", "NearbyFragment onDestroyView" + this.getId());
		if (mAMapLocationManager != null) {
			mAMapLocationManager.removeUpdates(this);
			mAMapLocationManager.destory();
		}
		mAMapLocationManager = null;
		Log.e("wjzwjz", "NearbyFragment end onDestroyView" + this.getId());
		super.onDestroyView();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		Log.e("wjzwjz", "NearbyFragment onAttach");
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Log.e("wjzwjz", "NearbyFragment onActivityCreated");
	}
	
	@Override
	public void onViewStateRestored(Bundle savedInstanceState) {
		super.onViewStateRestored(savedInstanceState);
		Log.e("wjzwjz", "NearbyFragment onViewStateRestored");
	}
	
	@Override
	public void onStart() {
		super.onStart();
		Log.e("wjzwjz", "NearbyFragment onStart");
	}
	
	@Override
	public void onStop() {
		super.onStop();
		Log.e("wjzwjz", "NearbyFragment onStop");
	}
	
	@Override
	public void onDetach() {
		super.onDetach();
		Log.e("wjzwjz", "NearbyFragment onDetach");
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
				final NearbySearchMerchantTask task = new NearbySearchMerchantTask(R.string.waitting, getActivity(), "queryMerchantByPoiId?poiId=" + poiId);
				task.execute(new Runnable() {
					
					@Override
					public void run() {
						String buf = task.result;
						unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
						Intent intent = new Intent();
						intent.setClass(getActivity(), MerchantDetailActivity.class);
						intent.putExtra("merchantId", buf);
						getActivity().startActivity(intent);
						getActivity().overridePendingTransition(R.anim.in_from_right,
								R.anim.out_to_left);
						
					}
				},new Runnable() {
					
					@Override
					public void run() {
						unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
						AlertDialog.Builder builder = new Builder(getActivity());
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
				AlertDialog.Builder builder = new Builder(getActivity());
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
			AlertDialog.Builder builder = new Builder(getActivity());
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

	/*
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
	*/
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
	public void onLocationChanged(AMapLocation location) {

		if (null != location) {
			if(!isFirstLocation)
			{
				isFirstLocation = true;
				firstLocation = location;
				setPoiSearch();
				Thread queryMerchantsThread = new Thread(queryRunnable);
				queryMerchantsThread.start();
			}
			else
			{
				float distance = firstLocation.distanceTo(location);
				if(distance>100)
				{
					firstLocation = location;
					setPoiSearch();
					Thread queryMerchantsThread = new Thread(queryRunnable);
					queryMerchantsThread.start();
				}
				else
				{
					return;
				}
				
			}
			
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		contentView.findViewById(R.id.loadingbar).setVisibility(View.VISIBLE);
		contentView.findViewById(R.id.serverdata).setVisibility(View.GONE);
		
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
		default:
			break;
		}
	}
	
	@Override
	public void onHeaderRefresh(PullToRefreshView view) {
		mPullToRefreshView.postDelayed(new Runnable() {

			@Override
			public void run() {

				contentView.findViewById(R.id.loadingbar).setVisibility(View.VISIBLE);
				contentView.findViewById(R.id.serverdata).setVisibility(View.GONE);
				
				page = 0;
				isFirstLoad = true;
				needToLoad = true;
				isFirstLocation = false;
				firstLocation = null;
				
				merchantsListView.setSelectionFromTop(0, 0);// 滑动到第一项
				
//				mAMapLocationManager = LocationManagerProxy.getInstance(this);
//				mAMapLocationManager.requestLocationUpdates(
//						LocationProviderProxy.AMapNetwork, 5000, 10, this);
				
				setPoiSearch();
//				buildTask();
				Thread queryMerchantsThread = new Thread(queryRunnable);
				queryMerchantsThread.start();
				
//				mPullToRefreshView.onHeaderRefreshComplete("更新于:"+new Date().toLocaleString());
				
//				mPullToRefreshView.onHeaderRefreshComplete();
			}
		}, 1000);

	}
	
	@Override
	public void onFooterRefresh(PullToRefreshView view) {
		mPullToRefreshView.postDelayed(new Runnable() {

			@Override
			public void run() {
				// 处理下拉刷新最新数据
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
		}, 1000);

	}
	
}