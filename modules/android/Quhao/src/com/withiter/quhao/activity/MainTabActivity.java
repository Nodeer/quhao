package com.withiter.quhao.activity;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.withiter.quhao.R;

public class MainTabActivity extends FragmentActivity implements AMapLocationListener{
	
	/**
	 * FragmentTabHost对象
	 */
	private FragmentTabHost mTabHost;
	
	private LayoutInflater inflater;
	
	/**
	 * 定义数组来存放Fragment界面
	 */
	private Class[] fragments = {HomeFragment.class,NearbyFragment.class,PersonCenterFragment.class,MoreFragment.class};
	
	/**
	 * 定义数组来存放按钮图片
	 */
	private int[] menuImgs = {R.drawable.menu_home,R.drawable.menu_nearby,R.drawable.menu_person_center,R.drawable.menu_more};
	
	/**
	 * Tab选项卡的文字
	 */
	private String menuTags[] = {"home", "nearby", "personCenter", "more"};
	
	/**
	 * Tab选项卡的文字
	 */
	private String menuTextViews[] = {"商家列表", "周边美食", "个人中心", "更多"};
	
	private LocationManagerProxy mAMapLocationManager; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_tab_layout);
		Log.e("wjzwjz", "MainTabActivity onCreate");
		initView();
	}

	
	@Override
	public void onStart() {
		super.onStart();
		Log.e("wjzwjz", "MainTabActivity onStart");
	}
	
	@Override
	protected void onRestart() {
		super.onRestart();
		Log.e("wjzwjz", "MainTabActivity onRestart");
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		Log.e("wjzwjz", "MainTabActivity onResume");
	}
	
	@Override
	public void onPause() {
		super.onPause();
		Log.e("wjzwjz", "MainTabActivity onPause");
	}
	
	@Override
	public void onStop() {
		super.onStop();
		Log.e("wjzwjz", "MainTabActivity onStop");
	}
	
	
	@Override
	public void onDestroy() {
		if (mAMapLocationManager != null) {
			mAMapLocationManager.removeUpdates(this);
			mAMapLocationManager.destory();
		}
		mAMapLocationManager = null;
		
		super.onDestroy();
		
		Log.e("wjzwjz", "MainTabActivity onDestroy");
	}
	
	/**
	 * 初始化组件
	 */
	private void initView() {
		
//		if (mAMapLocationManager == null) {  
//            mAMapLocationManager = LocationManagerProxy.getInstance(this);  
//            /* 
//             * mAMapLocManager.setGpsEnable(false);// 
//             * 1.0.2版本新增方法，设置true表示混合定位中包含gps定位，false表示纯网络定位，默认是true 
//             */  
//            // Location SDK定位采用GPS和网络混合定位方式，时间最短是5000毫秒，否则无效  
//            mAMapLocationManager.requestLocationUpdates(  
//                    LocationProviderProxy.AMapNetwork, 5000, 10, this);  
//        }
		
		//实例化布局对象
		inflater = LayoutInflater.from(this);
		
		//实例化TabHost对象， 得到TabHost
		mTabHost = (FragmentTabHost) this.findViewById(R.id.tabhost);
		
		mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);	
		int count = fragments.length;
		
		for (int i = 0; i < count; i++) {
			//为每一个Tab按钮设置图标、文字和内容
			TabSpec tabSpec = mTabHost.newTabSpec(menuTags[i]).setIndicator(getTabItemView(i));
			
			mTabHost.addTab(tabSpec, fragments[i], null);
			
			mTabHost.getTabWidget().getChildAt(i).setBackgroundResource(R.drawable.imagebutton_background);
		}
		
		mTabHost.setCurrentTab(0);
		mTabHost.setOnTabChangedListener(new OnTabChangeListener() {
			
			@Override
			public void onTabChanged(String tabId) {
				
				Log.e("wjzwjz", "tab id : " + tabId);
				
			}
		});
//		TabWidget tabWidget = (TabWidget)findViewById(R.id.tabs);
//        tabWidget.setBackgroundResource(R.drawable.ic_action_search);
	}

	private View getTabItemView(int i) {
		
		View view = inflater.inflate(R.layout.tab_item_view, null);
		ImageView imgView = (ImageView) view.findViewById(R.id.imageview);
		imgView.setImageResource(menuImgs[i]);
		
		TextView textView = (TextView) view.findViewById(R.id.textview);
		textView.setText(menuTextViews[i]);
		
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
		
	}
	
}
