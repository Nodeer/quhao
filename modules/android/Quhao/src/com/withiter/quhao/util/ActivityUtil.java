package com.withiter.quhao.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import com.withiter.quhao.R;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;


public class ActivityUtil {
	private static final String TAG = "ActivityUtil";

	/**
	 * 窗体跳转
	 * 
	 * @param old
	 * @param cls
	 */
	public static void jump(Context old, Class<?> cls, Bundle mBundle) {
		jump(old, cls, mBundle, false);
	}

	/**
	 * 窗体跳转
	 * 
	 * @param old
	 * @param cls
	 */
	public static void jump(Context old, Class<?> cls, Bundle mBundle,
			boolean clearTop) {
		Intent intent = new Intent(old, cls);

		if (mBundle != null) {
			intent.putExtras(mBundle);
		}

		if (clearTop) {
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		}
		old.startActivity(intent);
		// 仿Iphone切换效果
		((Activity) old).overridePendingTransition(R.anim.zoomin,
				R.anim.zoomout);
		// 原来的切换效果
		// ((Activity) old).overridePendingTransition(R.anim.right_slide_in,
		// R.anim.right_slide_out);
	}

	/**
	 * 窗体跳转
	 * 
	 * @param old
	 * @param cls
	 */
	public static void jumpForResult(Context old, Class<?> cls,
			int requestCode, Bundle mBundle) {
		Intent intent = new Intent();
		intent.setClass(old, cls);
		if (mBundle != null) {
			intent.putExtras(mBundle);
		}

		Activity activity = (Activity) old;

		activity.startActivityForResult(intent, requestCode);
		((Activity) old).overridePendingTransition(R.anim.right_slide_in,
				R.anim.right_slide_out);
	}

	/**
	 * 窗体跳转
	 * 
	 * @param old
	 * @param cls
	 */
	public static void jumpForResult(Context old, Class<?> cls, int requestCode) {
		jumpForResult(old, cls, requestCode, null);
	}

	public static void jump(Context old, Class<?> cls) {
		jump(old, cls, null);
	}

	/**
	 * 通过地址跳转到网
	 * 
	 * @param activity
	 * @param url
	 */
	public static void jumbToWeb(Activity activity, String url) {
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
		activity.startActivity(intent);
		activity.overridePendingTransition(R.anim.right_slide_in,
				R.anim.right_slide_out);
	}

	public static void runInUIThread(Context context, final Toast toast) {
		final Activity activity = (Activity) context;
		activity.runOnUiThread(new Runnable() {
			public void run() {
				toast.show();
			}
		});
	}

	public static boolean isTopActivy(Activity activity,String activityName)
	{
		if (null == activity || StringUtils.isNull(activityName)) {
			return false;
		}
		ActivityManager am = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
		ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
		Log.e("wjzwjz", "activity Name : " + activityName + " , cn. className : " + cn.getClassName());
		if (cn != null && activityName.equals(cn.getClassName())) {
			return true;
		}
		return false;
	}
	
	public static Display getWindowDisplay(Context context) {
		return ((WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
	}

	/**
	 * 获得手机型号
	 */
	public static String getDeviceType() {
		return Build.MODEL;
	}

	/**
	 * 获得版本号
	 * 
	 * @param ctx
	 * @return
	 */
	public static int getVersionCode(Context ctx) {
		PackageManager manager = ctx.getPackageManager();
		try {
			PackageInfo info = manager.getPackageInfo(ctx.getPackageName(), 0);
			int code = info.versionCode; // 版本号
			// Log.d(TAG, "versionCode="+code+", pkg="+info.packageName);
			return code;
		} catch (NameNotFoundException e) {
			Log.e(TAG, e.getMessage(), e);
			return 0;
		}
	}

	/**
	 * 获得版本名称
	 * 
	 * @param ctx
	 * @return
	 */
	public static String getVersionName(Context ctx) {
		PackageManager manager = ctx.getPackageManager();
		try {
			PackageInfo info = manager.getPackageInfo(ctx.getPackageName(), 0);
			return info.versionName;
		} catch (NameNotFoundException e) {
			Log.e(TAG, e.getMessage(), e);
			return "";
		}
	}

	public static float getPX(Context context, int dipValue) {
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30,
				context.getResources().getDisplayMetrics());
	}

	/**
	 * 检测是否连接了网络
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isNetWorkAvailable(Context context) {

		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		if (connectivity != null) {
			NetworkInfo[] infoArray = connectivity.getAllNetworkInfo();
			if (infoArray != null) {
				for (NetworkInfo info : infoArray) {
					if (info.getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * 拨打电话
	 */
	public static void call(Context context, String phoneNo) {
		// 已经绑定电话的场合
		String number = "tel:" + phoneNo;
		try {
			// Intent callIntent = new Intent(Intent.ACTION_CALL);
			Intent callIntent = new Intent(Intent.ACTION_DIAL);
			callIntent.setData(Uri.parse(number));
			context.startActivity(callIntent);
		} catch (ActivityNotFoundException e) {
		}
	}

	/**
	 * 去系统设置界面
	 */
	public static void gotoSysSetting(Context context) {
		try {
			Intent settingsIntent = new Intent(
					android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			context.startActivity(settingsIntent);
		} catch (ActivityNotFoundException e) {
		}
	}

	/**
	 * 获得手机Ip
	 * 
	 * @return
	 */
	public static String getLocalIpAddress() {
		try {
			Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces();
			for (; en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses();
				for (; enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException e) {
		}
		return "";
	}

	public static boolean existSDcard() {
		if (android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 通过uri获得文件名
	 * 
	 * @param contentUri
	 * @return
	 */
	public static String getRealPathFromURI(Activity activity, Uri uri) {
		String[] proj = { MediaStore.Images.Media.DATA };
		Cursor cursor = activity.managedQuery(uri, proj, null, null, null);
		int column_index = cursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}

	/**
	 * 获得手机分辨率
	 */
	public static DisplayMetrics getWindowsPixels(Activity activity) {
		DisplayMetrics dm = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		// 获得手机的宽带和高度像素单位为px
		return dm;
	}

	/**
	 * 判断是否是联通wcdma
	 */
	public static boolean isWcdma(Activity activity) {
		// 获得手机SIMType
		TelephonyManager tm = (TelephonyManager) activity
				.getSystemService(Context.TELEPHONY_SERVICE);
		int nType = tm.getNetworkType();
		int pType = tm.getPhoneType();
		String nOperator = tm.getNetworkOperator();

		if (nOperator.equals("46001")) {
			// 联通的场合
			if (pType == TelephonyManager.PHONE_TYPE_GSM) {
				// gsm的场合
				if (nType == TelephonyManager.NETWORK_TYPE_HSDPA
						|| nType == TelephonyManager.NETWORK_TYPE_HSUPA
						|| nType == TelephonyManager.NETWORK_TYPE_HSPA) {
					// WCDMA的场合
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 获取IMSI信息
	 */
	public static String getIMSI(Context context) {
		TelephonyManager telManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		String imsi = telManager.getSubscriberId();
		return imsi;
	}

	/**
	 * 判断WIFI是否打开
	 */
	public static boolean isWifiOpen(Context activity) {
		// 获得手机SIMType
		WifiManager wm = (WifiManager) activity
				.getSystemService(Context.WIFI_SERVICE);
		return wm.isWifiEnabled();
	}

	/**
	 * 调用拍照程序拍摄图片，返回图片对应的Uri，应处理onActivityResult
	 * ContentResolver的insert方法会默认创建一张空图片，如取消了拍摄，应根据方法返回的Uri删除图片
	 * 
	 * @param activity
	 * @param requestCode
	 * @param fileName
	 * @return
	 */
	public static Uri captureImage(Activity activity, int requestCode,
			String fileName, String desc) throws Exception {
		// 设置文件参数
		ContentValues values = new ContentValues();
		values.put(MediaStore.Images.Media.TITLE, fileName);
		values.put(MediaStore.Images.Media.DESCRIPTION, desc);
		// 获得uri
		Uri imageUri = activity.getContentResolver().insert(
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE, null);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
		intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
		activity.startActivityForResult(intent, requestCode);
		return imageUri;
	}

	/**
	 * 从本地选取图片，应处理onActivityResult，示例： protected void onActivityResult(int
	 * requestCode, int resultCode, Intent data) { //获得图片的真实地址 String path =
	 * getPathByUri(this, data.getData()); }
	 * 
	 * @param activity
	 * @param requestCode
	 */
	public static void pickImage(Activity activity, int requestCode)
			throws Exception {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
		intent.setType("image/*");
		// intent.putExtra("return-data", true);
		activity.startActivityForResult(intent, requestCode);
	}

	public static boolean isGPSOn(Context context)
	{
		LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		
		boolean flag = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
		
		return flag;
	}
}
