package com.withiter.quhao.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.baidu.mapapi.GeoPoint;
import com.baidu.mapapi.LocationListener;
import com.baidu.mapapi.MKLocationManager;
import com.baidu.mapapi.MapActivity;
import com.baidu.mapapi.MapView;
import com.baidu.mapapi.MyLocationOverlay;
import com.baidu.mobstat.StatService;
import com.withiter.quhao.R;
import com.withiter.quhao.util.QuhaoLog;
import com.withiter.quhao.util.StringUtils;
import com.withiter.quhao.util.baidu.BaiduMapUtil;
import com.withiter.quhao.util.baidu.MerchantItemizedOverlay;
import com.withiter.quhao.util.http.CommonHTTPRequest;
import com.withiter.quhao.vo.MerchantLocation;

public class MerchantLBSActivity extends MapActivity
{
	
	private static final String TAG = MerchantLBSActivity.class.getName();

	private Button btnBack;
	
	private TextView merchantNameView;
	
	private MapView mapView;
	
	private LocationListener mLocationListener;
	
	private String merchantName;
	
	private String merchantId;
	
	private List<MerchantLocation> locations;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.merchant_lbs_map);
		
		
		this.merchantName = this.getIntent().getStringExtra("merchantName");
		this.merchantId = this.getIntent().getStringExtra("merchantId");
		
		merchantNameView = (TextView) findViewById(R.id.name);
		merchantNameView.setText(merchantName);
		init();
	}



	private void init()
	{
		btnBack = (Button) findViewById(R.id.back_btn);
		btnBack.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				MerchantLBSActivity.this.finish();
				
			}
		});
		
		mapView = (MapView) findViewById(R.id.mapView);
		super.initMapActivity(BaiduMapUtil.getManager());
		
		// 设置在缩放动画过程中也显示overlay,默认为不绘制
		mapView.setDrawOverlayWhenZooming(true);
		
		WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
		int width = wm.getDefaultDisplay().getWidth();// 屏幕宽度
		if (width <= 640) {
			mapView.getController().setZoom(17);
		} else {
			mapView.getController().setZoom(20);
		}
		
		GeoPoint pt = new GeoPoint((int) (31.678109 * 1e6),
				(int) (120.761869 * 1e6));
		
		mapView.getController().setCenter(pt);
		
		mLocationListener = new LocationListener()
		{
			
			@Override
			public void onLocationChanged(Location location)
			{
				if(null != location)
				{
					// 将当前位置转换成地理坐标点
					GeoPoint pt = new GeoPoint((int) (location.getLatitude() * 1e6),(int) (location.getLongitude() * 1e6));
					// 将当前位置设置为地图的中心
					mapView.getController().animateTo(pt);
					BaiduMapUtil.getManager().getLocationManager()
							.removeUpdates(mLocationListener);
				}
				
			}
		};
		
		myLocation();
		
		Thread thread = new Thread(getLocationRunnable);
		thread.start();
		
	}
	
	private Runnable getLocationRunnable = new Runnable()
	{
		
		@Override
		public void run()
		{
			try {
				QuhaoLog.v(TAG, "get categorys data form server begin");
				String buf = CommonHTTPRequest.get("getReservations?accountId=51e563feae4d165869fda38c&mid=51efe7d8ae4dca7b4c281754");
						//+ MerchantDetailActivity.this.merchantId);
				if (StringUtils.isNull(buf) && "[]".equals(buf)) {
				} else {
					//List<ReservationVO> rvos = ParseJson.getReservations(buf);
					locations = new ArrayList<MerchantLocation>();
					MerchantLocation location = new MerchantLocation("51e563feae4d165869fda38c", "name111", 31.678109, 31.678109, "address22");
					locations.add(location);
					getLocationUpdateHandler.obtainMessage(200, locations)
							.sendToTarget();
				}

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
			}
		}
	};
	
	private Handler getLocationUpdateHandler = new Handler(){

		@Override
		public void handleMessage(Message msg)
		{
			if (msg.what == 200) {
				super.handleMessage(msg);

				Drawable marker = getResources()
						.getDrawable(R.drawable.ic_address_big);
				marker.setBounds(0, 0, marker.getIntrinsicWidth(),
						marker.getIntrinsicHeight());
				MerchantItemizedOverlay overitem = new MerchantItemizedOverlay(marker,
						MerchantLBSActivity.this, locations, mapView);
				mapView.getOverlays().add(overitem);
			}

		}
		
	};

	private void myLocation() {
		// 初始化Location模块
		MKLocationManager mLocationManager = BaiduMapUtil.getManager()
				.getLocationManager();
		// 通过enableProvider和disableProvider方法，选择定位的Provider
		mLocationManager.enableProvider(MKLocationManager.MK_NETWORK_PROVIDER);
		mLocationManager.enableProvider(MKLocationManager.MK_GPS_PROVIDER);
		// 添加定位图层
		MyLocationOverlay mylocTest = new MyLocationOverlay(this, mapView);
		mylocTest.enableMyLocation(); // 启用定位
		// mMapView.getController().setCenter(mylocTest.getMyLocation());
		mapView.getOverlays().add(mylocTest);
	}

	@Override
	protected boolean isRouteDisplayed()
	{
		return false;
	}

	@Override
	protected void onPause() {
		BaiduMapUtil.getManager().getLocationManager()
				.removeUpdates(mLocationListener);
		BaiduMapUtil.stop();
		super.onPause();
		StatService.onPause(this);
	}

	@Override
	protected void onResume() {
		BaiduMapUtil.getManager().getLocationManager()
				.requestLocationUpdates(mLocationListener);
		BaiduMapUtil.start();
		super.onResume();
		StatService.onResume(this);
	}

	@Override
	public void finish() {
		super.finish();
		//overridePendingTransition(R.anim.left_slide_in, R.anim.left_slide_out);
	}
}
