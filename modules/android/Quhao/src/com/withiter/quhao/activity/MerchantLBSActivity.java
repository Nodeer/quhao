package com.withiter.quhao.activity;

import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.InfoWindowAdapter;
import com.amap.api.maps.AMap.OnInfoWindowClickListener;
import com.amap.api.maps.AMap.OnMapLoadedListener;
import com.amap.api.maps.AMap.OnMarkerClickListener;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.withiter.quhao.QHClientApplication;
import com.withiter.quhao.R;
import com.withiter.quhao.data.MerchantData;
import com.withiter.quhao.util.StringUtils;

public class MerchantLBSActivity extends QuhaoBaseActivity implements OnMarkerClickListener, OnMapLoadedListener, 
	OnInfoWindowClickListener, InfoWindowAdapter,LocationSource, AMapLocationListener {

	private Button btnBack;

	private Button btnGuideRoute;
	
	private TextView merchantNameView;

	private MerchantData merchant;

	private MapView mMapView;

	private AMap mAMap;

	private CameraUpdate update = null;
	
	private OnLocationChangedListener mListener;
	
	private LocationManagerProxy mAMapLocationManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.merchant_lbs_map);

		
//		this.merchantName = this.getIntent().getStringExtra("merchantName");
//		this.merchantId = this.getIntent().getStringExtra("merchantId");
		long time1 = System.currentTimeMillis();
		Log.e("wjzwjz", "quhaoclient : " + (time1-QHClientApplication.getInstance().time1));
		merchant = getIntent().getParcelableExtra("merchant");
		
		merchantNameView = (TextView) findViewById(R.id.name);
		merchantNameView.setText(merchant.getName());
		if(StringUtils.isNotNull(merchant.getName()) && merchant.getName().length()>10)
		{
			merchantNameView.setText(merchant.getName().substring(0, 10) + "...");
		}

		btnGuideRoute = (Button) this.findViewById(R.id.guide_route);
		btnGuideRoute.setOnClickListener(this);
		long time2 = System.currentTimeMillis();
		Log.e("wjzwjz", "time2-time1:" + (time2-time1));
		mMapView = (MapView) findViewById(R.id.mapView);
		mMapView.onCreate(savedInstanceState);
		// markerText = (TextView) findViewById(R.id.mark_listenter_text);
		init();
	}

	private void init() {
		btnBack = (Button) findViewById(R.id.back_btn);
		btnBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				MerchantLBSActivity.this.finish();
			}
		});
		long time3 = System.currentTimeMillis();
		if (mAMap == null) {
			mAMap = mMapView.getMap();
			mAMap.setOnMapLoadedListener(this);// 设置amap加载成功事件监听器
			mAMap.setOnMarkerClickListener(this);// 设置点击marker事件监听器
			mAMap.setOnInfoWindowClickListener(this);// 设置点击infoWindow事件监听器
			mAMap.setInfoWindowAdapter(this);// 设置自定义InfoWindow样式
			// 自定义系统定位小蓝点
			MyLocationStyle myLocationStyle = new MyLocationStyle();
			myLocationStyle.myLocationIcon(BitmapDescriptorFactory
					.fromResource(R.drawable.location_marker));// 设置小蓝点的图标
			myLocationStyle.strokeColor(Color.BLACK);// 设置圆形的边框颜色
			// myLocationStyle.radiusFillColor(color)//设置圆形的填充颜色
			// myLocationStyle.anchor(int,int)//设置小蓝点的锚点
			myLocationStyle.strokeWidth(0.1f);// 设置圆形的边框粗细
			mAMap.setMyLocationStyle(myLocationStyle);
			mAMap.setMyLocationRotateAngle(180);
			mAMap.setLocationSource(this);// 设置定位监听
			mAMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
//			mAMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
		}
		long time4 = System.currentTimeMillis();
		Log.e("wjzwjz", "time4-time3 : " + (time4-time3));
		if(null!=merchant)
		{
			merchantNameView.setText(merchant.getName());
			if(StringUtils.isNotNull(merchant.getName()) && merchant.getName().length()>10)
			{
				merchantNameView.setText(merchant.getName().substring(0, 10) + "...");
			}
			LatLng latLng = new LatLng(merchant.getLat(), merchant.getLng());
			MarkerOptions options = new MarkerOptions();
			options.position(latLng).title(merchant.getName()).snippet(merchant.getAddress()).icon(BitmapDescriptorFactory.fromResource(R.drawable.menu_nearby_img)).perspective(true);

			mAMap.addMarker(options);
			update = CameraUpdateFactory.newLatLngZoom(latLng,15);

			if (null != update) {
				mAMap.moveCamera(update);
			}
		}
		long time5 = System.currentTimeMillis();
		Log.e("wjzwjz", "time5-time4:" + (time5-time4));

	}

	@Override
	public void finish() {
		super.finish();
	}

	/**
	 * 把一个xml布局文件转化成view
	 */
	/*
	 * public View getView(String title, String text) { View view =
	 * getLayoutInflater().inflate(R.layout.marker, null); TextView text_title =
	 * (TextView) view.findViewById(R.id.marker_title); TextView text_text =
	 * (TextView) view.findViewById(R.id.marker_text);
	 * text_title.setText(title); text_text.setText(text); return view; }
	 */

	/**
	 * 把一个view转化成bitmap对象
	 */
	/*
	 * public static Bitmap getViewBitmap(View view) {
	 * view.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
	 * MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)); view.layout(0,
	 * 0, view.getMeasuredWidth(), view.getMeasuredHeight());
	 * view.buildDrawingCache(); Bitmap bitmap = view.getDrawingCache(); return
	 * bitmap; }
	 */

	@Override
	public void onClick(View v) {

		if (isClick) {
			return;
		}
		
		isClick = true;
		
		switch(v.getId())
		{
			case R.id.guide_route:
				unlockHandler.sendEmptyMessage(UNLOCK_CLICK);
				Intent intent = new Intent();
				
				Bundle mBundle = new Bundle();
				mBundle.putParcelable("merchant", merchant);
				intent.putExtras(mBundle);
				
				intent.setClass(this, MerchantRouteActivity.class);
				startActivity(intent);
				break;
			default:
				unlockHandler.sendEmptyMessage(UNLOCK_CLICK);
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
		mMapView.onResume();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {

		super.onSaveInstanceState(outState);
		mMapView.onSaveInstanceState(outState);
	}

	@Override
	protected void onDestroy() {

		super.onDestroy();
		mMapView.onDestroy();
	}

	@Override
	protected void onPause() {

		super.onPause();
		mMapView.onPause();
		deactivate();
	}

	@Override
	public void onMapLoaded() {
		// 设置所有maker显示在View中
		if (null != update) {
			mAMap.moveCamera(update);
		}
	}

	/**
	 * 对marker标注点点击响应事件
	 */
	@Override
	public boolean onMarkerClick(Marker marker) {
		// markerText.setText("你点击的是" + marker.getTitle());
		return false;

	}

	/**
	 * 监听自定义infowindow窗口的infocontents事件回调
	 */
	@Override
	public View getInfoContents(Marker marker) {
		View infoContent = getLayoutInflater().inflate(R.layout.custom_info_contents, null);
		render(marker, infoContent);
		return infoContent;
	}

	/**
	 * 自定义infowinfow窗口
	 */
	public void render(Marker marker, View view) {
		String title = marker.getTitle();
		TextView titleUi = ((TextView) view.findViewById(R.id.title));
		if (title != null) {
			SpannableString titleText = new SpannableString(title);
			titleText.setSpan(new ForegroundColorSpan(Color.RED), 0, titleText.length(), 0);
			titleUi.setText(titleText);

		} else {
			titleUi.setText("");
		}
		String snippet = marker.getSnippet();
		TextView snippetUi = ((TextView) view.findViewById(R.id.snippet));
		if (snippet != null) {
			SpannableString snippetText = new SpannableString(snippet);
			snippetText.setSpan(new ForegroundColorSpan(Color.GREEN), 0, snippetText.length(), 0);
			snippetUi.setText(snippetText);
		} else {
			snippetUi.setText("");
		}
	}

	@Override
	public View getInfoWindow(Marker marker) {
		View infoWindow = getLayoutInflater().inflate(R.layout.custom_info_window, null);
		render(marker, infoWindow);
		return infoWindow;
	}

	@Override
	public void onInfoWindowClick(Marker marker) {
//		Toast.makeText(this, "你点击了infoWindow窗口" + marker.getTitle(), Toast.LENGTH_SHORT).show();
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
	public void onLocationChanged(AMapLocation aLocation) {
		if (mListener != null && aLocation != null) {
			QHClientApplication.getInstance().location = aLocation;
			mListener.onLocationChanged(aLocation);// 显示系统小蓝点
			float bearing = mAMap.getCameraPosition().bearing;
			mAMap.setMyLocationRotateAngle(bearing);// 设置小蓝点旋转角度
			
		}
	}

	@Override
	public void activate(OnLocationChangedListener listener) {
		mListener = listener;
//		if (mAMapLocationManager == null) {
//			mAMapLocationManager = LocationManagerProxy.getInstance(this);
//			/*
//			 * mAMapLocManager.setGpsEnable(false);
//			 * 1.0.2版本新增方法，设置true表示混合定位中包含gps定位，false表示纯网络定位，默认是true Location
//			 * API定位采用GPS和网络混合定位方式
//			 * ，第一个参数是定位provider，第二个参数时间最短是5000毫秒，第三个参数距离间隔单位是米，第四个参数是定位监听者
//			 */
//			mAMapLocationManager.requestLocationUpdates(
//					LocationProviderProxy.AMapNetwork, 10000, 100, this);
//		}
		
		Thread requestLocation = new Thread(new Runnable() {

			@Override
			public void run() {
				Looper.prepare();
				try {
					if (mAMapLocationManager == null) {

						mAMapLocationManager = LocationManagerProxy
								.getInstance(MerchantLBSActivity.this);
						/*
						 * mAMapLocManager.setGpsEnable(false);//
						 * 1.0.2版本新增方法，设置true表示混合定位中包含gps定位，false表示纯网络定位，默认是true
						 */
						// Location SDK定位采用GPS和网络混合定位方式，时间最短是5000毫秒，否则无效
						mAMapLocationManager.requestLocationUpdates(
								LocationProviderProxy.AMapNetwork, 10000, 100,
								MerchantLBSActivity.this);
					} else {
						/*
						 * mAMapLocManager.setGpsEnable(false);//
						 * 1.0.2版本新增方法，设置true表示混合定位中包含gps定位，false表示纯网络定位，默认是true
						 */
						// Location SDK定位采用GPS和网络混合定位方式，时间最短是5000毫秒，否则无效
						mAMapLocationManager.requestLocationUpdates(
								LocationProviderProxy.AMapNetwork, 10000, 100,
								MerchantLBSActivity.this);

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
	}

	@Override
	public void deactivate() {
		mListener = null;
		if (mAMapLocationManager != null) {
			mAMapLocationManager.removeUpdates(this);
			mAMapLocationManager.destory();
		}
		mAMapLocationManager = null;
	}
}
