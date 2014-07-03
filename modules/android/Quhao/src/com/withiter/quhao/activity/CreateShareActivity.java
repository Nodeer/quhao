package com.withiter.quhao.activity;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.withiter.quhao.QHClientApplication;
import com.withiter.quhao.R;
import com.withiter.quhao.util.ActivityUtil;

public class CreateShareActivity extends QuhaoBaseActivity implements AMapLocationListener {

	/**
	 * 查询输入框
	 */
	private EditText contentEdit;
	private LocationManagerProxy mAMapLocationManager;
	private TextView locationView;
	private AMapLocation location;
	
	private Button reLocateBtn;
	
	private ImageView shareImgView;
	
	private String content;
	
	private String locationStr;


	/**
	 * 去上传文件
	 */
	protected static final int TO_UPLOAD_FILE = 1;  
	/**
	 * 上传文件响应
	 */
	protected static final int UPLOAD_FILE_DONE = 2;  //
	/**
	 * 选择文件
	 */
	public static final int TO_SELECT_PHOTO = 3;
	/**
	 * 上传初始化
	 */
	private static final int UPLOAD_INIT_PROCESS = 4;
	/**
	 * 上传中
	 */
	private static final int UPLOAD_IN_PROCESS = 5;
	
	private String picPath = null;
	
	private Handler locationHandler = new Handler()
	{

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			if (location == null) {
//				Toast.makeText(CitySelectActivity.this, "亲，定位失败，请检查网络状态！", Toast.LENGTH_SHORT).show();
				locationView.setText("定位失败，请手动定位");
				reLocationBtnHandler.obtainMessage(200, "true").sendToTarget();
				stopLocation();// 销毁掉定位
			}
		}
		
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.create_share_layout);
		super.onCreate(savedInstanceState);

		btnBack.setOnClickListener(goBack(this));
		
		shareImgView = (ImageView) this.findViewById(R.id.share_img);
		shareImgView.setOnClickListener(this);
		reLocateBtn = (Button) this.findViewById(R.id.relocate);
		reLocateBtn.setOnClickListener(this);
		
		locationView = (TextView) this.findViewById(R.id.location);
		locationView.setText("正在定位中...");

		contentEdit = (EditText) this.findViewById(R.id.content_edit);
		
		initView();

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
	
	private void initView() {
		
	}


	@Override
	protected void onResume() {
//		initView();
		Thread requestLocation = new Thread(new Runnable() {

			@Override
			public void run() {
				Looper.prepare();
				try {
					
					if (ActivityUtil.isNetWorkAvailable(CreateShareActivity.this)) {
						
						stopLocation();

						mAMapLocationManager = LocationManagerProxy
								.getInstance(CreateShareActivity.this);
						/*
						 * mAMapLocManager.setGpsEnable(false);//
						 * 1.0.2版本新增方法，设置true表示混合定位中包含gps定位，false表示纯网络定位，默认是true
						 */
						// Location SDK定位采用GPS和网络混合定位方式，时间最短是5000毫秒，否则无效
						mAMapLocationManager.requestLocationUpdates(
								LocationProviderProxy.AMapNetwork, 10000, 100,
								CreateShareActivity.this);
//							locationHandler.postDelayed(locationRunnable, 60000);// 设置超过12秒还没有定位到就停止定位
						locationHandler.sendEmptyMessageDelayed(200, 60000);
					} else {
//						locateMsg.setText("网络未开启...");
						locateMsgHandler.obtainMessage(200, "网络未开启...").sendToTarget();
						reLocationBtnHandler.obtainMessage(200, "true").sendToTarget();
					}

				} catch (Exception e) {
					locateMsgHandler.obtainMessage(200, "定位失败，请手动选择城市").sendToTarget();
					reLocationBtnHandler.obtainMessage(200, "true").sendToTarget();
				}
				finally
				{
					Looper.loop();
				}
			}
		});
		requestLocation.start();
		
		super.onResume();
	};

	protected Handler locateMsgHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == 200) {
				String message = (String) msg.obj;
				if (locationView != null) {
					locationView.setText(message);
				}
			}
		}
	};
	
	protected Handler reLocationBtnHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == 200) {
				String message = (String) msg.obj;
				if ("true".equals(message)) {
					reLocateBtn.setEnabled(true);
					reLocateBtn.setBackgroundResource(R.drawable.btn_background_red);
				}
				else
				{
					reLocateBtn.setEnabled(false);
					reLocateBtn.setBackgroundResource(R.drawable.btn_background_red_disable);
				}
			}
		}
	};
	
	@Override
	public void onClick(View v) {
		if (isClick) {
			return;
		}
		isClick = true;

		switch (v.getId()) {
		case R.id.cancel_btn:
			unlockHandler.sendEmptyMessage(UNLOCK_CLICK);
			this.finish();
			break;
		case R.id.relocate:
			unlockHandler.sendEmptyMessage(UNLOCK_CLICK);
			
			Thread requestLocation = new Thread(new Runnable() {

				@Override
				public void run() {
					Looper.prepare();
					try {
						
						if (ActivityUtil.isNetWorkAvailable(CreateShareActivity.this)) {
							
							stopLocation();

							mAMapLocationManager = LocationManagerProxy
									.getInstance(CreateShareActivity.this);
							/*
							 * mAMapLocManager.setGpsEnable(false);//
							 * 1.0.2版本新增方法，设置true表示混合定位中包含gps定位，false表示纯网络定位，默认是true
							 */
							// Location SDK定位采用GPS和网络混合定位方式，时间最短是5000毫秒，否则无效
							mAMapLocationManager.requestLocationUpdates(
									LocationProviderProxy.AMapNetwork, 10000, 100,
									CreateShareActivity.this);
//							locationHandler.postDelayed(locationRunnable, 60000);// 设置超过12秒还没有定位到就停止定位
							locationHandler.sendEmptyMessageDelayed(200, 60000);
							locateMsgHandler.obtainMessage(200, "请稍等...").sendToTarget();
							reLocationBtnHandler.obtainMessage(200, "false").sendToTarget();
							
						} else {
//							locateMsg.setText("网络未开启...");
							locateMsgHandler.obtainMessage(200, "网络未开启...").sendToTarget();
							reLocationBtnHandler.obtainMessage(200, "true").sendToTarget();
						}

					} catch (Exception e) {
						locateMsgHandler.obtainMessage(200, "定位失败，请手动定位").sendToTarget();
						reLocationBtnHandler.obtainMessage(200, "true").sendToTarget();
					}
					finally
					{
						Looper.loop();
					}
				}
			});
			requestLocation.start();
			break;
		default:
			unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			break;
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return false;
	}



	@Override
	public void onPause() {
		if (mAMapLocationManager != null) {
			mAMapLocationManager.removeUpdates(this);
			mAMapLocationManager.destory();
//			locationHandler.removeCallbacks(locationRunnable);
		}
		super.onPause();
	}

	@Override
	public void onStop() {
		super.onStop();
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
			stopLocation();
			String cityName = location.getCity().replace("市", "");
//			locateMsg.setText("定位城市：" + cityName);
			locateMsgHandler.obtainMessage(200, "定位城市：" + cityName).sendToTarget();
			reLocationBtnHandler.obtainMessage(200, "true").sendToTarget();
		} else {
			stopLocation();
//			locateMsg.setText("定位失败...");
			locateMsgHandler.obtainMessage(200, "定位失败，请手动选择城市").sendToTarget();
			reLocationBtnHandler.obtainMessage(200, "true").sendToTarget();
		}
	}
	
	@Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
        	
            // 获得当前得到焦点的View，一般情况下就是EditText（特殊情况就是轨迹求或者实体案件会移动焦点）
            View v = getCurrentFocus();

            if (isShouldHideInput(v, ev)) {
                hideSoftInput(v.getWindowToken());
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时没必要隐藏
     * 
     * @param v
     * @param event
     * @return
     */
    private boolean isShouldHideInput(View v, MotionEvent event) {
    	
        if (v != null && (v instanceof EditText)) {
            int[] l = { 0, 0 };
            v.getLocationInWindow(l);
            int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left
                    + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击EditText的事件，忽略它。
                return false;
            } else {
                return true;
            }
        }
        // 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditView上，和用户用轨迹球选择其他的焦点
        return false;
    }

    /**
     * 多种隐藏软件盘方法的其中一种
     * 
     * @param token
     */
    private void hideSoftInput(IBinder token) {
        if (token != null) {
            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(token,
                    InputMethodManager.HIDE_NOT_ALWAYS);
            
//            if(im.isActive()){
//            	im.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS);
//			}
        }
    }
}
