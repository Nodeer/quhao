package com.withiter.quhao.activity;

import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.withiter.quhao.QHClientApplication;
import com.withiter.quhao.R;
import com.withiter.quhao.util.ActivityUtil;

public class MainTabActivityS extends FragmentActivity implements AMapLocationListener, OnClickListener {

	/**
	 * FragmentTabHost对象
	 */
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
	
	private Button btnMenu;
	
	private TextView topTitle;
	
	private TextView citySelectView;
	
	private Button searchBtn;
	
	private LinearLayout homeLayout;
	
	private LinearLayout nearbyLayout;
	
	private LinearLayout personLayout;
	
	private LinearLayout moreLayout;
	
	protected final int UNLOCK_CLICK = 1000;
	
	protected boolean isClick = false;
	
	protected Handler unlockHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == UNLOCK_CLICK) {
				
				isClick = false;
			}
		}
	};
	
	private Handler locationHandler = new Handler()
	{

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (location == null) {
				if (ActivityUtil.isTopActivy(MainTabActivityS.this, MainTabActivityS.class.getName())) {
					Toast.makeText(MainTabActivityS.this, "亲，定位失败，请检查网络状态！", Toast.LENGTH_SHORT).show();
				}
				stopLocation();// 销毁掉定位
			}
		}
		
	};
	private AMapLocation location;
	
	private SlidingMenu slidingMenu;
	
	private Fragment mContent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_tab_layout_s);
		
		initView(savedInstanceState);
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
	}

	@Override
	protected void onResume() {
		super.onResume();
		Thread requestLocation = new Thread(new Runnable() {

			@Override
			public void run() {
				Looper.prepare();
				try {
					
					stopLocation();
					

					mAMapLocationManager = LocationManagerProxy
							.getInstance(MainTabActivityS.this);
					/*
					 * mAMapLocManager.setGpsEnable(false);//
					 * 1.0.2版本新增方法，设置true表示混合定位中包含gps定位，false表示纯网络定位，默认是true
					 */
					// Location SDK定位采用GPS和网络混合定位方式，时间最短是5000毫秒，否则无效
					mAMapLocationManager.requestLocationUpdates(
							LocationProviderProxy.AMapNetwork, 10000, 100,
							MainTabActivityS.this);
//						locationHandler.postDelayed(locationRunnable, 60000);// 设置超过12秒还没有定位到就停止定位
					locationHandler.sendEmptyMessageDelayed(200, 60000);

				} catch (Exception e) {
					e.printStackTrace();
				}
				finally {
					Looper.loop();
				}
			}
		});
		requestLocation.start();
	}

	@Override
	public void onPause() {
		stopLocation();
		super.onPause();
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	@Override
	public void onDestroy() {
		stopLocation();
		super.onDestroy();

	}

	/**
	 * 初始化组件
	 */
	private void initView(Bundle savedInstanceState) {

		btnMenu = (Button) this.findViewById(R.id.btn_menu);
		btnMenu.setOnClickListener(this);
		topTitle = (TextView) this.findViewById(R.id.top_title);
		topTitle.setOnClickListener(this);
		citySelectView = (TextView) this.findViewById(R.id.city_select);
		citySelectView.setOnClickListener(this);
		searchBtn = (Button) this.findViewById(R.id.edit_search_c);
		searchBtn.setOnClickListener(this);
		slidingMenu = new SlidingMenu(this);
		slidingMenu.setMode(SlidingMenu.LEFT);
		slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		slidingMenu.setShadowWidthRes(R.dimen.shadow_width);
//        menu.setShadowDrawable(R.drawable.shadow);
		slidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		slidingMenu.setFadeDegree(0.35f);
		slidingMenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        slidingMenu.setMenu(R.layout.sliding_menu_main);
        
        homeLayout = (LinearLayout) slidingMenu.findViewById(R.id.home_layout);
        homeLayout.setOnClickListener(this);
        
        nearbyLayout = (LinearLayout) slidingMenu.findViewById(R.id.nearby_layout);
        nearbyLayout.setOnClickListener(this);
        
        personLayout = (LinearLayout) slidingMenu.findViewById(R.id.person_layout);
        personLayout.setOnClickListener(this);
        
        moreLayout = (LinearLayout) slidingMenu.findViewById(R.id.more_layout);
        moreLayout.setOnClickListener(this);
        
        if (savedInstanceState != null)
        {
        	mContent = getSupportFragmentManager().getFragment(savedInstanceState, "mContent");
        }
			
		if (mContent == null)
		{
			mContent = new HomeFragment();	
		}
		
		getSupportFragmentManager()
		.beginTransaction()
		.replace(R.id.content_frame, mContent)
		.commitAllowingStateLoss();
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
//				locationHandler.removeCallbacks(locationRunnable);
			}
			mAMapLocationManager = null;
		}
	}

	@Override
	public void finish() {
		stopLocation();
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

	@Override
	public void onClick(View v) {
		
		if(isClick)
		{
			return;
		}
		isClick = true;
		switch (v.getId()) {
		case R.id.home_layout:
			unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			if (mContent instanceof HomeFragment) {
				return;
			}
			mContent = new HomeFragment();
			getSupportFragmentManager()
			.beginTransaction()
			.replace(R.id.content_frame, mContent)
			.commitAllowingStateLoss();
			slidingMenu.showContent();
			break;
		case R.id.nearby_layout:
			unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			if (mContent instanceof NearbyFragment) {
				return;
			}
			mContent = new NearbyFragment();
			getSupportFragmentManager()
			.beginTransaction()
			.replace(R.id.content_frame, mContent)
			.commitAllowingStateLoss();
			slidingMenu.showContent();
			break;case R.id.person_layout:
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				if (mContent instanceof PersonCenterFragment) {
					return;
				}
				mContent = new PersonCenterFragment();
				getSupportFragmentManager()
				.beginTransaction()
				.replace(R.id.content_frame, mContent)
				.commitAllowingStateLoss();
				slidingMenu.showContent();
				break;
			case R.id.more_layout:
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				if (mContent instanceof MoreFragment) {
					return;
				}
				mContent = new MoreFragment();
				getSupportFragmentManager()
				.beginTransaction()
				.replace(R.id.content_frame, mContent)
				.commitAllowingStateLoss();
				slidingMenu.showContent();
				break;
		case R.id.btn_menu:
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				if (!slidingMenu.isMenuShowing()) {
					slidingMenu.showContent();
				}
				break;
		default:
			unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			break;
		}

	}
}
