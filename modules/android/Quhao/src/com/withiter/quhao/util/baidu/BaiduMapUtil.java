package com.withiter.quhao.util.baidu;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKEvent;
import com.baidu.mapapi.MKGeneralListener;
import com.withiter.quhao.QHClientApplication;

public class BaiduMapUtil
{
	private static BMapManager mBMapMan = null;
	// 百度key
	private static String mStrKey = "F170F9535C7AF64736FC48C5F39E02BAC129C641";
	static boolean m_bKeyRight = true; // 授权Key正确，验证通过

	public static void init(Context context) {
		mBMapMan = new BMapManager(context);
		mBMapMan.init(mStrKey, new MyGeneralListener());
		mBMapMan.getLocationManager().setNotifyInternal(10, 5);
	}

	// 常用事件监听，用来处理通常的网络错误，授权验证错误等
	static class MyGeneralListener implements MKGeneralListener {
		@Override
		public void onGetNetworkState(int iError) {
			Log.d("MyGeneralListener", "onGetNetworkState error is " + iError);
			Toast.makeText(QHClientApplication.getInstance(), "您的网络出错啦！", Toast.LENGTH_LONG).show();
		}

		@Override
		public void onGetPermissionState(int iError) {
			Log.d("MyGeneralListener", "onGetPermissionState error is "
					+ iError);
			if (iError == MKEvent.ERROR_PERMISSION_DENIED) {
				// 授权Key错误：
				Toast.makeText(QHClientApplication.getInstance(), "授权Key不正确！", Toast.LENGTH_LONG).show();
				m_bKeyRight = false;
			}
		}
	}
	
	public static BMapManager getManager(){
		if (mBMapMan == null) {
			mBMapMan = new BMapManager(QHClientApplication.getInstance());
			mBMapMan.init(mStrKey, new MyGeneralListener());
		}
		
		return mBMapMan;
	}
	
	/**
	 * 开启
	 */
	public static void start(){
		mBMapMan.start();
	}
	
	/**
	 * 关闭
	 */
	public static void stop(){
		mBMapMan.stop();
	}
	
	/**
	 * 销毁管理
	 */
	public static void destroy(){
		if (mBMapMan != null) {
			mBMapMan.destroy();
			mBMapMan = null;
		}
	}
}
