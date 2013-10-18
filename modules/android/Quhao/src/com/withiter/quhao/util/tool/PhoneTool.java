package com.withiter.quhao.util.tool;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.withiter.quhao.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.Display;
import android.widget.Toast;

public class PhoneTool {
	
	private static int screenHeight;
	private static int screenwidth;

	/**
	 * Get the device height
	 * @param cx
	 * @return
	 */
	public static int getDeviceWidth(Context cx) {
		DisplayMetrics dm = new DisplayMetrics();
		dm = cx.getApplicationContext().getResources().getDisplayMetrics();
		return dm.widthPixels;
	}

	/**
	 * Get the device width
	 * @param cx
	 * @return
	 */
	public static int getDeviceHeight(Context cx) {
		DisplayMetrics dm = new DisplayMetrics();
		dm = cx.getApplicationContext().getResources().getDisplayMetrics();
		return dm.heightPixels;
	}

	public static void setScreenHeight(int height){
		screenHeight = height;
	}
	public static void setScreenWidth(int width){
		screenwidth = width;
	}
	
	public static int getScreenHeight(){
		return screenHeight;
	}
	public static int getScreenWidth(){
		return screenwidth;
	}
	
	// SD卡的判断，如果没有SD卡，则对任务进行操作前给用户提示
	public static Boolean isSDCardisAvailable() {
		String state = Environment.getExternalStorageState();
		if (state.equals(Environment.MEDIA_MOUNTED))
			return true;
		return false;
	}

	// 檢查網絡
	public static boolean isNetworkAvailable(Context cx) {
		ConnectivityManager cm = (ConnectivityManager) cx
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cm.getActiveNetworkInfo();
		return (info != null && info.isConnected());
	}

	public static void warningDialog(Context cx, int msgid) {
		new AlertDialog.Builder(cx)
				.setTitle(R.string.warning)
				.setMessage(msgid)
				// .setIcon(android.R.drawable.)
				.setPositiveButton(R.string.btn_ok,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								dialog.dismiss();
							}
						}).show();
	}

	public static String getFileNameByPath(String path) {
		String[] names = path.split("/");
		String name;
		if (names.length == 0)
			name = path;
		name = names[names.length - 1];
		String[] n = name.split("\\.");
		return n[0];
	}

	public static void hintDialog(Context cx, String msg) {
		System.out.println("hintDialog: " + msg);
		Toast.makeText(cx, msg, Toast.LENGTH_SHORT).show();
	}

	public static boolean isNameAdressFormat(String email) {
		boolean isExist = false;

		Pattern p = Pattern.compile("\\w+@(\\w+.)+[a-z]{2,3}");
		Matcher m = p.matcher(email);
		boolean b = m.matches();
		if (b) {
			System.out.println("有效邮件地址");
			isExist = true;
		} else {
			System.out.println("无效邮件地址");
		}
		return isExist;
	}

}
