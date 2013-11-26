package com.withiter.quhao;

import java.io.File;

import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.view.WindowManager;
import android.widget.Toast;

import com.withiter.quhao.domain.AccountInfo;
import com.withiter.quhao.util.QuhaoLog;
import com.withiter.quhao.util.StringUtils;
import com.withiter.quhao.util.db.AccountInfoHelper;
import com.withiter.quhao.util.http.CommonHTTPRequest;
import com.withiter.quhao.util.tool.ParseJson;
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
	public boolean isLogined = false;
	public String phone = "";
	public AccountInfo accessInfo = null;
	public boolean isAuto = false;
	public static Context mContext;
	/**
	 * 单例
	 */
	private static QHClientApplication instance;

	@Override
	public void onCreate() {
		QuhaoLog.i(TAG, "onCreate method is called");
		QHClientApplication.mContext = this;
		isLogined = false;
		instance = this;
		initAccountConfig();
		createSDCardDir();
		super.onCreate();
	}

	public QHClientApplication() {
		super();
	}

	public static QHClientApplication getInstance() {
		if (instance == null) {
			throw new IllegalStateException("Not yet initialized");
		}
		return instance;
	}

	private void initAccountConfig() {

		QuhaoLog.i(TAG, "init database config");

		SharedprefUtil.put(this, QuhaoConstant.IS_LOGIN, "false");
		
		String phone = SharedprefUtil.get(this, QuhaoConstant.PHONE, "");
		String password = SharedprefUtil.get(this, QuhaoConstant.PASSWORD, "");
		String isAutoLogin = SharedprefUtil.get(this, QuhaoConstant.IS_AUTO_LOGIN, "");
		
		if (StringUtils.isNotNull(phone) && StringUtils.isNotNull(password)) {
			if ("true".equalsIgnoreCase(isAutoLogin)) {
				String url = "AccountController/login?phone=" + phone.trim() + "&password=" + password.trim();
				QuhaoLog.i(TAG, "the login url is : " + url);
				try {
					String result = CommonHTTPRequest.get(url);
					QuhaoLog.i(TAG, result);
					if (StringUtils.isNotNull(result)) {
						LoginInfo loginInfo = ParseJson.getLoginInfo(result);
						AccountInfo account = new AccountInfo();
//						account.setUserId("1");
						account.build(loginInfo);
						QuhaoLog.i(TAG, account.msg);
						
						if (account.msg.equals("fail")) {
							SharedprefUtil.put(this, QuhaoConstant.IS_LOGIN, "false");
							Toast.makeText(this, "登陆失败", Toast.LENGTH_LONG).show();
							return;
						}
						
						if (account.msg.equals("success")) {
							SharedprefUtil.put(this, QuhaoConstant.ACCOUNT_ID, loginInfo.accountId);
							SharedprefUtil.put(this, QuhaoConstant.PHONE, phone.trim());
							SharedprefUtil.put(this, QuhaoConstant.PASSWORD, password.trim());
							SharedprefUtil.put(this, QuhaoConstant.IS_AUTO_LOGIN, isAutoLogin.trim());
							SharedprefUtil.put(this, QuhaoConstant.IS_LOGIN, "true");
							this.phone = phone;
							this.isLogined = true;
							Toast.makeText(this, "登录成功", Toast.LENGTH_LONG).show();
							return;
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					QuhaoLog.e(TAG, e);
					SharedprefUtil.put(this, QuhaoConstant.IS_LOGIN, "false");
					Toast.makeText(this, "登陆失败", Toast.LENGTH_LONG).show();
				} 
			} else {
				SharedprefUtil.put(this, QuhaoConstant.IS_LOGIN, "false");
			}
		} else {
			SharedprefUtil.put(this, QuhaoConstant.IS_LOGIN, "false");
			QuhaoLog.i(TAG, "accessInfo is null");
		}
	}

	/**
	 * 在SD卡上创建一个文件夹
	 */
	public void createSDCardDir() {
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			// 创建一个文件夹对象，赋值为外部存储器的目录
			File sdcardDir = Environment.getExternalStorageDirectory();
			// 得到一个路径，内容是sdcard的文件夹路径和名字
			String path = sdcardDir.getPath() + "/quhao";
			File path1 = new File(path);
			if (!path1.exists()) {
				// 若不存在，创建目录，可以在应用启动的时候创建
				path1.mkdirs();
			}
		} else {
			return;
		}

	}
}
