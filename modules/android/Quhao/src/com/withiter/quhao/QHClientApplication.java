package com.withiter.quhao;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Set;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources.NotFoundException;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

import com.amap.api.location.AMapLocation;
import com.withiter.quhao.domain.AccountInfo;
import com.withiter.quhao.domain.CityInfo;
import com.withiter.quhao.util.StringUtils;
import com.withiter.quhao.util.encrypt.DesUtils;
import com.withiter.quhao.util.http.CommonHTTPRequest;
import com.withiter.quhao.util.tool.ParseJson;
import com.withiter.quhao.util.tool.PhoneTool;
import com.withiter.quhao.util.tool.QuhaoConstant;
import com.withiter.quhao.util.tool.SharedprefUtil;
import com.withiter.quhao.vo.LoginInfo;

public class QHClientApplication extends Application {

	/**
	 * 创建全局变量 全局变量一般都比较倾向于创建一个单独的数据类文件，并使用static静态变量
	 * 这里使用了在Application中添加数据的方法实现全局变量
	 */
	private WindowManager.LayoutParams wmParams = new WindowManager.LayoutParams();

	public WindowManager.LayoutParams getMywmParams() {
		return wmParams;
	}

	private static final String TAG = QHClientApplication.class.getName();
	public boolean isLogined = false; // Global attribute, indicates the login
										// status
	public String phone = ""; // Global attribute, indicates current login phone
								// number
	public AccountInfo accountInfo = null; // Global attribute, indicates
											// current login account

	public boolean isAuto = false;

	/**
	 * 读取用户设置的时候在2G/3G下浏览图片 true:
	 */
	public boolean is2G3GRead = false;

	/**
	 * 是否是WIFI连接
	 */
	public boolean isWifiOpen = false;

	/**
	 * 是否可以加载图片
	 */
	public boolean canLoadImg = false;

	/**
	 * 默认城市
	 */
	public CityInfo defaultCity;

	public long time1;

	public long time2;

	/**
	 * 当前位置信息
	 */
	// public LatLonPoint lp = new LatLonPoint(31.235048, 121.474794);
	public AMapLocation location;
	public static Context mContext;
	private static QHClientApplication instance;

	public static QHClientApplication getInstance() {
		if (instance == null) {
			throw new IllegalStateException("Not yet initialized");
		}
		return instance;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		// TODO set debug mode to false
		JPushInterface.setDebugMode(true);
		JPushInterface.init(this);
		JPushInterface.reportNotificationOpened(mContext, null);
		
		// 注册push消息打开动作
//		JPushReceiver mMessageReceiver = new JPushReceiver();
//		IntentFilter filter = new IntentFilter();
//		filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
//		filter.addAction(JPushInterface.ACTION_NOTIFICATION_OPENED);
//		registerReceiver(mMessageReceiver,filter);

		// QuhaoLog.i(TAG, "onCreate method is called");
		QHClientApplication.mContext = this;
		// isLogined = false;
		instance = this;
		initServerConfig();
		Thread accountThread = new Thread(new Runnable() {

			@Override
			public void run() {
				initAccountConfig();
			}
		});
		accountThread.start();

		/*
		 * Thread facesThread = new Thread(new Runnable() {
		 * 
		 * @Override public void run() {
		 * FaceConversionUtil.getInstace().getFileText
		 * (QHClientApplication.mContext); } }); facesThread.start();
		 */
		initSDCardConfig();
	}

	public QHClientApplication() {
		super();
	}

	/**
	 * Initial the server configuration
	 */
	private void initServerConfig() {
		try {

			/*
			 * String is2G3GRead = SharedprefUtil.get(this,
			 * QuhaoConstant.IS_2G3G_READ, "true");
			 * 
			 * if ("true".equals(is2G3GRead)) { this.is2G3GRead = true; } else {
			 * this.is2G3GRead = false; }
			 * 
			 * this.isWifiOpen = ActivityUtil.isWifiOpen(this);
			 * 
			 * if (this.is2G3GRead || (!this.is2G3GRead && this.isWifiOpen)) {
			 * this.canLoadImg = true; } else { this.canLoadImg = false; }
			 */
			String canLoadImgStr = SharedprefUtil.get(this, QuhaoConstant.IS_LOAD_IMG, "false");
			if ("true".equals(canLoadImgStr)) {
				this.canLoadImg = true;
			}

			// 初始化城市
			String cityCode = SharedprefUtil.get(this, QuhaoConstant.CITY_CODE, "021");
			String cityName = SharedprefUtil.get(this, QuhaoConstant.CITY_NAME, "上海");
			String cityPinyin = SharedprefUtil.get(this, QuhaoConstant.CITY_PINYIN, "shanghai");
			this.defaultCity = new CityInfo(cityCode, cityName, cityPinyin);

			try {
				ApplicationInfo appInfo = this.getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
				boolean msg = appInfo.metaData.getBoolean("test");
				QuhaoConstant.test = msg;
				// QuhaoLog.i(TAG, "current deployment is test mode : " + msg);
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}

			// QuhaoLog.i(TAG,
			// "start to init configurations from application.properties");
			InputStream input = getResources().openRawResource(R.raw.application);
			BufferedReader read = new BufferedReader(new InputStreamReader(input));
			String line = "";
			while ((line = read.readLine()) != null) {
				if (line.startsWith("#")) {
					continue;
				}
				if (line.contains("app.server")) {
					QuhaoConstant.HTTP_URL = line.split("=")[1];
					// QuhaoLog.i(TAG, "server url is : " +
					// QuhaoConstant.HTTP_URL);
				}
			}
			input.close();
		} catch (NotFoundException e) {
			e.printStackTrace();
			// QuhaoLog.e(TAG, e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			// QuhaoLog.e(TAG, e.getMessage());
		}
	}

	/**
	 * Initial the account configuration
	 */
	private void initAccountConfig() {

		if (!PhoneTool.isNetworkAvailable(this)) {
			// QuhaoLog.w(TAG, "Network is not available!");
			return;
		}

		// QuhaoLog.i(TAG, "Initial the account configuration");
		SharedprefUtil.put(this, QuhaoConstant.IS_LOGIN, "false");

		String phone = SharedprefUtil.get(this, QuhaoConstant.PHONE, "");
		String isAutoLogin = SharedprefUtil.get(this, QuhaoConstant.IS_AUTO_LOGIN, "");
		String password = SharedprefUtil.get(this, QuhaoConstant.PASSWORD, "");

		// SharedPreferences sharedPreferences =
		// PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		// String password = sharedPreferences.getString(QuhaoConstant.PASSWORD,
		// "");
		// QuhaoLog.d("cross, get password from sp", password);
		String isExitedLastTime = SharedprefUtil.get(this, QuhaoConstant.IS_EXITED_LASTTIME, "true");
		if (StringUtils.isNull(isAutoLogin) || "false".equals(isAutoLogin) || StringUtils.isNull(phone) || StringUtils.isNull(password) || "true".equals(isExitedLastTime)) {
			return;
		}

		String decryptPassword = new DesUtils().decrypt(password);
		String url = "login?phone=" + phone + "&password=" + decryptPassword;
		try {
			Looper.prepare();
			String result = CommonHTTPRequest.post(url);
			if (StringUtils.isNull(result)) {
				// SharedprefUtil.put(this, QuhaoConstant.IS_LOGIN, "false");
				this.isLogined = false;
				Handler handler = new Handler();
				handler.post(new Runnable() {

					@Override
					public void run() {

						Toast.makeText(QHClientApplication.this, "自动登陆失败，请进入个人中心登陆", Toast.LENGTH_SHORT).show();

					}
				});
				return;
			}

			LoginInfo loginInfo = ParseJson.getLoginInfo(result);
			AccountInfo account = new AccountInfo();
			account.build(loginInfo);
			// QuhaoLog.d(TAG, account.msg);

			if ("fail".equals(account.msg)) {
				this.isLogined = false;
				// SharedprefUtil.put(this, QuhaoConstant.IS_LOGIN, "false");
				Handler handler = new Handler();
				handler.post(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(QHClientApplication.this, "自动登陆失败，请进入个人中心登陆", Toast.LENGTH_SHORT).show();
					}
				});
				return;
			}

			if ("success".equals(account.msg)) {
				SharedprefUtil.put(this, QuhaoConstant.ACCOUNT_ID, loginInfo.accountId);
				SharedprefUtil.put(this, QuhaoConstant.PHONE, phone);
				String encryptPassword = new DesUtils().encrypt(decryptPassword);
				SharedprefUtil.put(this, QuhaoConstant.PASSWORD, encryptPassword);
				SharedprefUtil.put(this, QuhaoConstant.IS_AUTO_LOGIN, isAutoLogin);
				this.accountInfo = account;
				this.phone = phone;
				this.isLogined = true;

				// 设置jpush alias
				Log.d(TAG, "Set alias in auto login.");
				JPushInterface.setAliasAndTags(getApplicationContext(), phone, null, mAliasCallback);
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
			// QuhaoLog.e(TAG, e);
			this.isLogined = false;
			Handler handler = new Handler();
			handler.post(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(QHClientApplication.this, "自动登陆失败，请进入个人中心登陆", Toast.LENGTH_SHORT).show();
				}
			});
		} finally {
			Looper.loop();
		}
	}

	/**
	 * Initial the SD configuration
	 */
	public void initSDCardConfig() {
		if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			return;
		}

		File sdcardDir = Environment.getExternalStorageDirectory();
		String path = sdcardDir.getPath() + "/quhao";
		File path1 = new File(path);
		if (!path1.exists()) {
			path1.mkdirs();
		}
	}
	
	/**
	 * Jpush alias call back
	 */
	public static final TagAliasCallback mAliasCallback = new TagAliasCallback() {
		@Override
		public void gotResult(int code, String alias, Set<String> tags) {
			String logs;
			switch (code) {
			case 0:
				logs = "Set tag and alias success";
				Log.i(TAG, logs);
				break;
			case 6002:
				Log.i(TAG, "Failed to set alias and tags due to timeout or No network");
				break;

			default:
				logs = "Failed with errorCode = " + code;
				Log.e(TAG, logs);
			}
		}
	};
}
