package com.withiter.quhao.activity;

import java.util.List;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;

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

public class NearbyActivity extends QuhaoBaseActivity implements AMapLocationListener,OnPoiSearchListener{

	private LocationManagerProxy mAMapLocManager = null;
	
	private PoiSearch.Query query;
	
	private PoiSearch poiSearch;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.nearby_layout);
		super.onCreate(savedInstanceState);
		
		mAMapLocManager = LocationManagerProxy.getInstance(this);
		
		mAMapLocManager.requestLocationUpdates(LocationProviderProxy.AMapNetwork, 1000, 10, this);

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
	public void onClick(View v) {

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
						Log.e("TAG111", String.valueOf(poiItems.size()));
					}
				}
			} catch (AMapException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}//异步搜索
		}
	}


	@Override
	public void onPoiItemDetailSearched(PoiItemDetail arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPoiSearched(PoiResult arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

}
