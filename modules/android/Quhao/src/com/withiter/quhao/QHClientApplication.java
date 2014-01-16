package com.withiter.quhao;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.os.Environment;
import android.view.WindowManager;
import android.widget.Toast;

import com.withiter.quhao.domain.AccountInfo;
import com.withiter.quhao.util.QuhaoLog;
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
	public boolean isLogined = false; 				// Global attribute, indicates the login status
	public String phone = ""; 						// Global attribute, indicates current login phone number
	public AccountInfo accountInfo = null;			// Global attribute, indicates current login account
	
	public boolean isAuto = false;
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
		QuhaoLog.i(TAG, "onCreate method is called");
		QHClientApplication.mContext = this;
		// isLogined = false;
		// instance = this;
		initServerConfig();
		initAccountConfig();
		initSDCardConfig();
		super.onCreate();
	}

	public QHClientApplication() {
		super();
	}

	/**
	 * Initial the server configuration
	 */
	private void initServerConfig(){
		try {
			QuhaoLog.i(TAG, "start to init configurations from application.properties");
			InputStream input = getResources().openRawResource(R.raw.application);
			BufferedReader read = new BufferedReader(new InputStreamReader(input));
			String line = "";
			while ((line = read.readLine()) != null) {
				if(line.startsWith("#")){
					continue;
				}
				if (line.contains("app.server")) {
					QuhaoConstant.HTTP_URL = line.split("=")[1];
					QuhaoLog.i(TAG, "server url is : " + QuhaoConstant.HTTP_URL);
				}
			}
		} catch (NotFoundException e) {
			e.printStackTrace();
			QuhaoLog.e(TAG, e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			QuhaoLog.e(TAG, e.getMessage());
		}
	}
	
	/**
	 * Initial the account configuration
	 */
	private void initAccountConfig() {
		
		if(!PhoneTool.isNetworkAvailable(this)){
			QuhaoLog.w(TAG, "Network is not available!");
			return;
		}
		
		QuhaoLog.i(TAG, "Initial the account configuration");
		SharedprefUtil.put(this, QuhaoConstant.IS_LOGIN, "false");

		String phone = SharedprefUtil.get(this, QuhaoConstant.PHONE, "");
		String password = SharedprefUtil.get(this, QuhaoConstant.PASSWORD, "");
		String isAutoLogin = SharedprefUtil.get(this, QuhaoConstant.IS_AUTO_LOGIN, "");

		if (!Boolean.getBoolean(isAutoLogin) || StringUtils.isNull(phone) || StringUtils.isNull(password)) {
			return;
		}

		String decryptPassword = new DesUtils().decrypt(password);
		String url = "AccountController/login?phone=" + phone + "&password=" + decryptPassword;
		try {
			String result = CommonHTTPRequest.post(url);
			if(StringUtils.isNull(result)){
				SharedprefUtil.put(this, QuhaoConstant.IS_LOGIN, "false");
				Toast.makeText(this, "自动登陆失败，请进入个人中心登陆", Toast.LENGTH_LONG).show();
				return;
			}
			
			LoginInfo loginInfo = ParseJson.getLoginInfo(result);
			AccountInfo account = new AccountInfo();
			account.build(loginInfo);
			QuhaoLog.d(TAG, account.msg);

			if (account.msg.equals("fail")) {
				SharedprefUtil.put(this, QuhaoConstant.IS_LOGIN, "false");
				Toast.makeText(this, "自动登陆失败，请进入个人中心登陆", Toast.LENGTH_LONG).show();
				return;
			}

			if (account.msg.equals("success")) {
				SharedprefUtil.put(this, QuhaoConstant.ACCOUNT_ID, loginInfo.accountId);
				SharedprefUtil.put(this, QuhaoConstant.PHONE, phone);
				String encryptPassword = new DesUtils().encrypt(password);
				SharedprefUtil.put(this, QuhaoConstant.PASSWORD, encryptPassword);
				SharedprefUtil.put(this, QuhaoConstant.IS_AUTO_LOGIN, isAutoLogin);
				this.accountInfo = account;
				this.phone = phone;
				this.isLogined = true;
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
			QuhaoLog.e(TAG, e);
			Toast.makeText(this, "自动登陆失败，请进入个人中心登陆", Toast.LENGTH_LONG).show();
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
}
