package com.withiter.quhao.activity;

import java.io.File;

import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.withiter.quhao.QHClientApplication;
import com.withiter.quhao.R;
import com.withiter.quhao.domain.AccountInfo;
import com.withiter.quhao.util.ActivityUtil;
import com.withiter.quhao.util.StringUtils;
import com.withiter.quhao.util.tool.FileUtil;
import com.withiter.quhao.util.tool.ImageUtil;
import com.withiter.quhao.util.tool.QuhaoConstant;
import com.withiter.quhao.util.tool.SharedprefUtil;

public class MainTabActivityS extends FragmentActivity implements AMapLocationListener, OnClickListener {

	private long exitTime = 0;

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
	
	private RelativeLayout userInfoLayout;
	
	private RelativeLayout userLoginLayout;
	
	private ImageView userImage;
	
	private TextView nickname;
	
	private TextView jifen;
	
	private Button btnLogin;
	
	private TextView btnRegister; 
	
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
		refreshUI();
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
		searchBtn = (Button) this.findViewById(R.id.edit_search);
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
        
        userInfoLayout = (RelativeLayout) slidingMenu.findViewById(R.id.menu_person_info);
        
        userLoginLayout = (RelativeLayout) slidingMenu.findViewById(R.id.menu_person_info_login);
    	
        userImage = (ImageView) slidingMenu.findViewById(R.id.menu_user_image);
    	
        nickname = (TextView) slidingMenu.findViewById(R.id.menu_nickName);
        
        jifen = (TextView) slidingMenu.findViewById(R.id.menu_jifen);
        
        btnLogin = (Button) slidingMenu.findViewById(R.id.menu_login);
        btnLogin.setOnClickListener(this);
        btnRegister = (TextView) slidingMenu.findViewById(R.id.menu_register);
        btnRegister.setOnClickListener(this);
        if (savedInstanceState != null)
        {
        	mContent = getSupportFragmentManager().getFragment(savedInstanceState, "mContent");
        }
			
		if (mContent == null)
		{
			mContent = new HomeFragmentNew();	
		}
		
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		
		ft.replace(R.id.content_frame, mContent,"home");
		ft.addToBackStack(null);
		ft.commitAllowingStateLoss();
	}
	
	public void refreshUI() {
		// if haven't login, prompt the login dialog
		// no need to check auto login from SharedPreference
		// because when APP start up, the action had been performed
		if (QHClientApplication.getInstance().isLogined) {
			AccountInfo account = QHClientApplication.getInstance().accountInfo;
			if (account != null) {
				userLoginLayout.setVisibility(View.GONE);
				userInfoLayout.setVisibility(View.VISIBLE);
				updateUIData(account);
			} else {

				nickname.setText(R.string.noname);

				jifen.setText("0");

				userImage.setImageResource(R.drawable.person_avatar);
				userLoginLayout.setVisibility(View.VISIBLE);
				userInfoLayout.setVisibility(View.GONE);
			}
		}
		else
		{
			nickname.setText(R.string.noname);

			jifen.setText("0");

			userImage.setImageResource(R.drawable.person_avatar);
			
			userInfoLayout.setVisibility(View.VISIBLE);
			userLoginLayout.setVisibility(View.GONE);
		}

	}
	
	// update UI according to the account object
		private void updateUIData(AccountInfo account) {
			nickname.setText(account.nickName);
			if(StringUtils.isNull(account.nickName))
			{
				nickname.setText(R.string.noname);
			}

			jifen.setText(account.jifen);

			Bitmap bitmap = null;
			String fileName = "";
			if(StringUtils.isNotNull(account.userImage))
			{
				if (FileUtil.hasSdcard()) {
					
					String[] strs = account.userImage.split("fileName=");
					if(strs != null && strs.length>1)
					{
						fileName = account.userImage.split("fileName=")[1];
						String localFileName = SharedprefUtil.get(this, QuhaoConstant.USER_IMAGE, "");
						if(localFileName.equals(fileName))
						{
							File f = new File(Environment.getExternalStorageDirectory() + "/" + 
									QuhaoConstant.IMAGES_SD_URL + "/" + fileName);
							File folder = f.getParentFile();
							if (!folder.exists()) {
								folder.mkdirs();
							}
							
							if(f.exists()){
								bitmap = ImageUtil.decodeFile(f.getPath(),-1,128*128);
								if (null != bitmap) {
									userImage.setImageBitmap(bitmap);
								}
							}
						}
						else
						{
							File f = new File(Environment.getExternalStorageDirectory() + "/" + 
									QuhaoConstant.IMAGES_SD_URL + "/" + localFileName);
							File folder = f.getParentFile();
							if (!folder.exists()) {
								folder.mkdirs();
							}
							
							if(f.exists()){
								f.delete();
							}
						}
					}
					
				}
			}
			
			if(null == bitmap)
			{
				if(StringUtils.isNotNull(fileName))
				{
					SharedprefUtil.put(this, QuhaoConstant.USER_IMAGE, fileName);
					ImageLoader.getInstance().displayImage(account.userImage, userImage);
//					AsynImageLoader.getInstance().showImageAsyn(avatar, 0, account.userImage, R.drawable.person_avatar);
				}
				
			}
			
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
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		switch (v.getId()) {
		case R.id.home_layout:
			unlockHandler.sendEmptyMessage(UNLOCK_CLICK);
			if (mContent instanceof HomeFragmentNew) {
				slidingMenu.showContent();
				return;
			}
			
			mContent = getSupportFragmentManager().findFragmentByTag("home");
			if (null == mContent) {
				mContent = new HomeFragmentNew();
			}
			
			ft.replace(R.id.content_frame, mContent,"home");
			ft.addToBackStack(null);
//			getSupportFragmentManager().popBackStack(arg0, arg1)
			ft.commitAllowingStateLoss();
			slidingMenu.showContent();
			break;
		case R.id.nearby_layout:
			unlockHandler.sendEmptyMessage(UNLOCK_CLICK);
			if (mContent instanceof NearbyFragment) {
				slidingMenu.showContent();
				return;
			}
			
			mContent = getSupportFragmentManager().findFragmentByTag("nearby");
			if (null == mContent) {
				mContent = new NearbyFragment();
			}
			
			ft.replace(R.id.content_frame, mContent,"nearby");
			ft.addToBackStack(null);
			ft.commitAllowingStateLoss();
			slidingMenu.showContent();
			break;case R.id.person_layout:
				unlockHandler.sendEmptyMessage(UNLOCK_CLICK);
				if (mContent instanceof PersonCenterFragment) {
					slidingMenu.showContent();
					return;
				}
				
				mContent = getSupportFragmentManager().findFragmentByTag("person");
				if (null == mContent) {
					mContent = new PersonCenterFragment();
				}
				
				ft.replace(R.id.content_frame, mContent,"person");
				ft.addToBackStack(null);
				ft.commitAllowingStateLoss();
				slidingMenu.showContent();
				break;
			case R.id.more_layout:
				unlockHandler.sendEmptyMessage(UNLOCK_CLICK);
				if (mContent instanceof MoreFragment) {
					slidingMenu.showContent();
					return;
				}
				
				mContent = getSupportFragmentManager().findFragmentByTag("more");
				if (null == mContent) {
					mContent = new MoreFragment();
				}
				
				ft.replace(R.id.content_frame, mContent,"more");
				ft.addToBackStack(null);
				ft.commitAllowingStateLoss();
				slidingMenu.showContent();
				break;
		case R.id.btn_menu:
			unlockHandler.sendEmptyMessage(UNLOCK_CLICK);
				if (!slidingMenu.isMenuShowing()) {
					slidingMenu.showMenu();
				}
				break;
		case R.id.city_select:
			unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			Intent intent = new Intent();
			intent.setClass(this, CitySelectActivity.class);
			startActivity(intent);
			break;
		case R.id.edit_search:
			unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			Intent intent2 = new Intent(this, MerchantsSearchActivity.class);
			startActivity(intent2);
			break;
		default:
			unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			break;
		}

	}
}
