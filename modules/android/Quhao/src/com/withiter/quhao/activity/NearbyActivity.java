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
import com.amap.api.location.LocationProviderProxy;
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
import com.withiter.quhao.util.QuhaoLog;
import com.withiter.quhao.util.StringUtils;
import com.withiter.quhao.util.http.CommonHTTPRequest;
import com.withiter.quhao.util.tool.ParseJson;
import com.withiter.quhao.util.tool.ProgressDialogUtil;

public class NearbyActivity extends QuhaoBaseActivity implements AMapLocationListener,OnPoiSearchListener,OnScrollListener,OnItemClickListener{

	private LocationManagerProxy mAMapLocManager = null;
	
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
		
		mAMapLocManager = LocationManagerProxy.getInstance(this);
		
		mAMapLocManager.requestLocationUpdates(LocationProviderProxy.AMapNetwork, 1000, 10, this);

		merchantsListView = (ListView) this.findViewById(R.id.merchantsListView);
		
		moreView = getLayoutInflater().inflate(R.layout.moredata, null);
		bt = (Button) moreView.findViewById(R.id.bt_load);
		pg = (ProgressBar) moreView.findViewById(R.id.pg);
		bt.setOnClickListener(this);
		merchantsListView.addFooterView(moreView);
		merchantsListView.setNextFocusDownId(R.id.merchantsListView);
		queryMerchants();
	}

	protected Handler updatePoiItemsHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			super.handleMessage(msg);

			if (msg.what == 200) {

				if (isFirstLoad) {

					nearByAdapter = new MerchantNearByAdapter(NearbyActivity.this, merchantsListView, poiItems);
					merchantsListView.setAdapter(nearByAdapter);
					isFirstLoad = false;
				} else {
					nearByAdapter.merchants = poiItems;
				}
				nearByAdapter.notifyDataSetChanged();
				bt.setVisibility(View.VISIBLE);
				pg.setVisibility(View.GONE);
				merchantsListView.setOnScrollListener(NearbyActivity.this);
				merchantsListView.setOnItemClickListener(NearbyActivity.this);
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			}
		}

	};
	
	private void queryMerchants() {
		
		query = new PoiSearch.Query("", "餐厅", "021");
		query.setPageSize(10);// 设置每页最多返回多少条poiitem
		query.setPageNum(page);// 设置查第一页
		query.setLimitDiscount(false);
		query.setLimitGroupbuy(false);
		poiSearch = new PoiSearch(this, query);
		poiSearch.setOnPoiSearchListener(this);
//		double lat = location.getLatitude();
//		double lon = location.getLongitude();
		LatLonPoint lp = new LatLonPoint(31.192172, 121.443025);
		poiSearch.setBound(new SearchBound(
				lp, 1000));//设置搜索区域为以lp点为圆心，其周围1000米范围
		try {
			poiItems = new ArrayList<PoiItem>();
			poiResult = poiSearch.searchPOI();
			if(null != poiResult && null != poiResult.getQuery())
			{
				List<PoiItem> poiItemTemps = poiResult.getPois();// 取得第一页的poiitem数据，页数从数字0开始
				if(null != poiItemTemps && poiItemTemps.size()>0)
				{
					if(poiItemTemps.size()<10)
					{
						needToLoad = false;
					}
					poiItems.addAll(poiItemTemps);
					updatePoiItemsHandler.obtainMessage(200, null).sendToTarget();
					Log.e("TAG111", poiItemTemps.toString());
				}
				else
				{
					needToLoad = false;
				}
				
			}
			else
			{
				needToLoad = false;
			}
		} catch (AMapException e) {
			needToLoad = false;
			e.printStackTrace();
		}//异步搜索
	}
	
	/**
	 * 点击下一页poi搜索
	 */
	public void nextSearch() {
		if(query!=null&&poiSearch!=null&&poiResult!=null){
			if (poiResult.getPageCount() - 1 > page) {
				page++;
				query.setPageNum(page);// 设置查后一页
				try {
					poiResult = poiSearch.searchPOI();
					if(null != poiResult && null != poiResult.getQuery())
					{
						List<PoiItem> poiItemTemps = poiResult.getPois();// 取得第一页的poiitem数据，页数从数字0开始
						if(null != poiItemTemps && poiItemTemps.size()>0)
						{
							if(poiItemTemps.size()<10)
							{
								needToLoad = false;
							}
							poiItems.addAll(poiItemTemps);
							updatePoiItemsHandler.obtainMessage(200, null).sendToTarget();
							Log.e("TAG111", poiItemTemps.toString());
						}
						else
						{
							needToLoad = false;
						}
						
					}
					else
					{
						needToLoad = false;
					}
				} catch (AMapException e) {
					needToLoad = false;
					e.printStackTrace();
				}
			} else {
				needToLoad = false;
				Toast.makeText(this, "No result", Toast.LENGTH_LONG).show();
			}
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		if (mAMapLocManager != null) {
			mAMapLocManager.removeUpdates(this);
		}
	}

	@Override
	protected void onDestroy() {
		if (mAMapLocManager != null) {
			mAMapLocManager.removeUpdates(this);
			mAMapLocManager.destory();
		}
		mAMapLocManager = null;
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
		
		if(null != location)
		{
			query = new PoiSearch.Query("", "餐厅", location.getCityCode());
			query.setLimitDiscount(false);
			query.setLimitGroupbuy(false);
			poiSearch = new PoiSearch(this, query);
			poiSearch.setOnPoiSearchListener(this);
			double lat = location.getLatitude();
			double lon = location.getLongitude();
			LatLonPoint lp = new LatLonPoint(lat, lon);
			poiSearch.setBound(new SearchBound(
					lp, 1000));//设置搜索区域为以lp点为圆心，其周围1000米范围
			try {
				PoiResult result = poiSearch.searchPOI();
				if(null != result && null != result.getQuery())
				{
					List<PoiItem> poiItems = result.getPois();// 取得第一页的poiitem数据，页数从数字0开始
					if(null != poiItems && poiItems.size()>0)
					{
						if(poiItems.size()<10)
						{
							needToLoad = false;
						}
						Log.e("TAG111", String.valueOf(poiItems.size()));
					}
					else
					{
						needToLoad = false;
					}
					
				}
				else
				{
					needToLoad = false;
				}
			} catch (AMapException e) {
				e.printStackTrace();
			}//异步搜索
		}
	}


	@Override
	public void onPoiItemDetailSearched(PoiItemDetail arg0, int arg1) {
		
	}

	@Override
	public void onPoiSearched(PoiResult arg0, int arg1) {
		
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (scrollState == OnScrollListener.SCROLL_STATE_IDLE
				&& lastVisibleIndex == nearByAdapter.getCount()) {
			pg.setVisibility(View.VISIBLE);
			bt.setVisibility(View.GONE);
			nextSearch();
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// check hit the bottom of current loaded data
		lastVisibleIndex = firstVisibleItem + visibleItemCount -1;
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
			nextSearch();
			
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
		unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
		progressDialogUtil = new ProgressDialogUtil(this, R.string.empty, R.string.waitting, false);
		progressDialogUtil.showProgress();
		
		try {
			String poiId = poiItems.get(position).getPoiId();
			if(StringUtils.isNotNull(poiId))
			{
				String buf = CommonHTTPRequest.get("queryMerchantByPoiId?poiId=" + poiId);
				if (StringUtils.isNull(buf) || "[]".equals(buf)) {
					progressDialogUtil.closeProgress();
					AlertDialog.Builder builder = new Builder(this);
					builder.setMessage("对不起，该商家未在取号系统注册。");
					builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
					builder.create().show();
				} else {
					progressDialogUtil.closeProgress();
					Intent intent = new Intent();
					intent.setClass(this, MerchantDetailActivity.class);
					intent.putExtra("merchantId", buf);
					this.startActivity(intent);
					overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
				}
			}
			else
			{
				progressDialogUtil.closeProgress();
				AlertDialog.Builder builder = new Builder(this);
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
			progressDialogUtil.closeProgress();
			unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			AlertDialog.Builder builder = new Builder(this);
			builder.setMessage("对不起，网络异常。");
			builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			builder.create().show();
			e.printStackTrace();
		} finally {
			
		}
		
	}
}
