package com.withiter.quhao.activity;

import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.withiter.quhao.QHClientApplication;
import com.withiter.quhao.R;

public class MainTabActivity extends FragmentActivity implements AMapLocationListener {

	/**
	 * FragmentTabHost对象
	 */
	private FragmentTabHost mTabHost;
	private LayoutInflater inflater;
	private long exitTime = 0;

	/**
	 * 定义数组来存放Fragment界面
	 */
	private Class[] fragments = { HomeFragment.class, NearbyFragment.class, PersonCenterFragment.class, MoreFragment.class };

	/**
	 * 定义数组来存放按钮图片
	 */
	private int[] menuImgs = { R.drawable.menu_home, R.drawable.menu_nearby, R.drawable.menu_person_center, R.drawable.menu_more };

	/**
	 * Tab选项卡的文字
	 */
	private String menuTags[] = { "home", "nearby", "personCenter", "more" };

	/**
	 * Tab选项卡的文字
	 */
	private LocationManagerProxy mAMapLocationManager;
	private Handler locationHandler = new Handler();
	private AMapLocation location;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_tab_layout);
		initView();
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
	}

	private Runnable locationRunnable = new Runnable() {
		@Override
		public void run() {
			if (location == null) {
				Toast.makeText(MainTabActivity.this, "亲，定位失败，请检查网络状态！", Toast.LENGTH_SHORT).show();
				stopLocation();// 销毁掉定位
			}
		}
	};
	
	@Override
	protected void onResume() {
		super.onResume();
		Thread requestLocation = new Thread(new Runnable() {

			@Override
			public void run() {
				Looper.prepare();
				try {
					if (mAMapLocationManager == null) {

						mAMapLocationManager = LocationManagerProxy
								.getInstance(MainTabActivity.this);
						/*
						 * mAMapLocManager.setGpsEnable(false);//
						 * 1.0.2版本新增方法，设置true表示混合定位中包含gps定位，false表示纯网络定位，默认是true
						 */
						// Location SDK定位采用GPS和网络混合定位方式，时间最短是5000毫秒，否则无效
						mAMapLocationManager.requestLocationUpdates(
								LocationProviderProxy.AMapNetwork, 10000, 100,
								MainTabActivity.this);
						locationHandler.postDelayed(locationRunnable, 60000);// 设置超过12秒还没有定位到就停止定位
					} else {
						/*
						 * mAMapLocManager.setGpsEnable(false);//
						 * 1.0.2版本新增方法，设置true表示混合定位中包含gps定位，false表示纯网络定位，默认是true
						 */
						// Location SDK定位采用GPS和网络混合定位方式，时间最短是5000毫秒，否则无效
						mAMapLocationManager.requestLocationUpdates(
								LocationProviderProxy.AMapNetwork, 10000, 100,
								MainTabActivity.this);
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
	}

	@Override
	public void onPause() {
		if (mAMapLocationManager != null) {
			mAMapLocationManager.removeUpdates(this);
			locationHandler.removeCallbacks(locationRunnable);
		}
		super.onPause();
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	@Override
	public void onDestroy() {
		if (mAMapLocationManager != null) {
			mAMapLocationManager.removeUpdates(this);
			mAMapLocationManager.destory();
			locationHandler.removeCallbacks(locationRunnable);
		}
		mAMapLocationManager = null;
		super.onDestroy();

	}

	/**
	 * 初始化组件
	 */
	private void initView() {

		// 实例化布局对象
		inflater = LayoutInflater.from(this);

		// 实例化TabHost对象， 得到TabHost
		mTabHost = (FragmentTabHost) this.findViewById(R.id.tabhost);
		mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);

		for (int i = 0; i < fragments.length; i++) {
			// 为每一个Tab按钮设置图标、文字和内容
			TabSpec tabSpec = mTabHost.newTabSpec(menuTags[i]).setIndicator(getTabItemView(i));
			mTabHost.addTab(tabSpec, fragments[i], null);
		}
		mTabHost.setCurrentTab(0);
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
	
	private View getTabItemView(int i) {
		View view = inflater.inflate(R.layout.tab_item_view, null);
		
		// 初始化menu图片
		ImageView imgView = (ImageView) view.findViewById(R.id.imageview);
		imgView.setImageResource(menuImgs[i]);
		return view;
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
			this.location = location;
			QHClientApplication.getInstance().location = location;
			if (mAMapLocationManager != null) {
				mAMapLocationManager.removeUpdates(this);
				mAMapLocationManager.destory();
				locationHandler.removeCallbacks(locationRunnable);
			}
			mAMapLocationManager = null;
		}
	}

	@Override
	public void finish() {
		if (mAMapLocationManager != null) {
			mAMapLocationManager.removeUpdates(this);
			mAMapLocationManager.destory();
			locationHandler.removeCallbacks(locationRunnable);
		}
		mAMapLocationManager = null;
		super.finish();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
			if (System.currentTimeMillis() - exitTime > 2000) {
				Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
				exitTime = System.currentTimeMillis();
			} else {
				finish();
				System.exit(0);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
