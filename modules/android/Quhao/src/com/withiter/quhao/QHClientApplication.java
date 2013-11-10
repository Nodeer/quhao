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
	public AccountInfo accessInfo = null;
	public boolean isAuto = false;
	public static Context mContext;
	/**
	 * 单例
	 */
	private static QHClientApplication instance;

	/*
	 * static { StringBuilder sb = new StringBuilder("");
	 * sb.append("CREATE TABLE ").append(" accountinfo (")
	 * .append(AccountInfoColumn.USERID) .append(" integer primary key,")
	 * .append(AccountInfoColumn.USERID).append(" text,")
	 * .append(AccountInfoColumn.PHONE).append(" text,")
	 * .append(AccountInfoColumn.EMAIL).append(" text,")
	 * .append(AccountInfoColumn.PASSWORD).append(" text,")
	 * .append(AccountInfoColumn.NICKNAME).append(" text,")
	 * .append(AccountInfoColumn.BIRTHDAY).append(" text,")
	 * .append(AccountInfoColumn.USERIMAGE).append(" text,")
	 * .append(AccountInfoColumn.ENABLE).append(" text,")
	 * .append(AccountInfoColumn.MOBILEOS).append(" text,")
	 * .append(AccountInfoColumn.SIGNIN).append(" text,")
	 * .append(AccountInfoColumn.ISSIGNIN).append(" text,")
	 * .append(AccountInfoColumn.DIANPING).append(" text,")
	 * .append(AccountInfoColumn.ZHAOPIAN).append(" text,")
	 * .append(AccountInfoColumn.JIFEN).append(" text,")
	 * .append(AccountInfoColumn.ISAUTO).append(" text,")
	 * .append(AccountInfoColumn.MSG).append(" text,")
	 * .append(AccountInfoColumn.LASTLOGIN).append(" text)");
	 * CREATE_ACCOUNT_TABLE = sb.toString(); }
	 */
	@Override
	public void onCreate() {
		QuhaoLog.i(TAG, "onCreate method is called");
		QHClientApplication.mContext = this;
		isLogined = false;
		instance = this;
		initDBConfig();
		initConfig();
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

	private void initDBConfig() {
		SharedprefUtil.put(this, QuhaoConstant.IS_LOGIN,"false");
		QuhaoLog.i(TAG, "init database config");
		String phone = SharedprefUtil.get(this, QuhaoConstant.PHONE, "");
		String password = SharedprefUtil.get(this, QuhaoConstant.PASSWORD, "");
		String isAutoLogin = SharedprefUtil.get(this, QuhaoConstant.IS_AUTO_LOGIN, "");
		if (StringUtils.isNotNull(phone) && StringUtils.isNotNull(password)) {
			if ("true".equalsIgnoreCase(isAutoLogin)) {
				String url = "AccountController/login?";
				url = url + "phone=" + phone.trim() + "&";
				url = url + "password=" + password.trim();
				QuhaoLog.i(TAG, "the login url is : " + url);
				try {
					String result = CommonHTTPRequest.get(url);
					QuhaoLog.i(TAG, result);
					if (StringUtils.isNull(result)) {
					} else {
						LoginInfo loginInfo = ParseJson.getLoginInfo(result);
						AccountInfo account = new AccountInfo();
						account.setUserId("1");
						account.build(loginInfo);
						QuhaoLog.i(TAG, account.msg);
						if (account.msg.equals("fail")) {
							
							SharedprefUtil.put(this, QuhaoConstant.IS_LOGIN,"false");
							Toast.makeText(this, "登陆失败", Toast.LENGTH_LONG).show();
							return;
						}
						if (account.msg.equals("success")) {
							SharedprefUtil.put(this, QuhaoConstant.ACCOUNT_ID,loginInfo.accountId);
							SharedprefUtil.put(this, QuhaoConstant.PHONE,phone.trim());
							SharedprefUtil.put(this, QuhaoConstant.PASSWORD,password.trim());
							SharedprefUtil.put(this, QuhaoConstant.IS_AUTO_LOGIN,isAutoLogin.trim());
							SharedprefUtil.put(this, QuhaoConstant.IS_LOGIN,"true");
							Toast.makeText(this, "登录成功", Toast.LENGTH_LONG).show();
							return;
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					SharedprefUtil.put(this, QuhaoConstant.IS_LOGIN,"false");
					Toast.makeText(this, "登陆失败", Toast.LENGTH_LONG).show();
				} finally {
				}
			}
			else
			{
				SharedprefUtil.put(this, QuhaoConstant.IS_LOGIN,"false");
			}
		} else {
			SharedprefUtil.put(this, QuhaoConstant.IS_LOGIN, "false");
			QuhaoLog.i(TAG, "accessInfo is null");
		}
	}
	/*
	private void initDBConfig() {
		QuhaoLog.i(TAG, "init database config");
		accessInfo = InfoHelper.getAccountInfo(this);
		if (accessInfo != null) {
			QuhaoLog.i(TAG, "accessInfo is not null");
			String isAuto = accessInfo.isAuto;
			SharedprefUtil.put(this, QuhaoConstant.IS_AUTO_LOGIN, isAuto);
			if ("true".equals(isAuto)) {
				String url = "AccountController/login?";
				url = url + "phone=" + accessInfo.getPhone() + "&";
				url = url + "password=" + accessInfo.getPassword();
				QuhaoLog.i(TAG, "the login url is : " + url);
				try {
					String result = CommonHTTPRequest.get(url);
					QuhaoLog.i(TAG, result);
					if (StringUtils.isNull(result)) {
					} else {
						LoginInfo loginInfo = ParseJson.getLoginInfo(result);
						AccountInfo account = new AccountInfo();
						account.setUserId("1");
						account.build(loginInfo);
						QuhaoLog.i(TAG, account.msg);
						if (account.msg.equals("fail")) {
							SharedprefUtil.put(this, QuhaoConstant.IS_LOGIN,
									"false");
							Toast.makeText(this, "登陆失败",
									Toast.LENGTH_LONG).show();
							return;
						}
						if (account.msg.equals("success")) {
							SharedprefUtil.put(this, QuhaoConstant.IS_LOGIN,
									"true");
							Toast.makeText(this, "登录成功", Toast.LENGTH_LONG)
									.show();
							return;
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					SharedprefUtil.put(this, QuhaoConstant.IS_LOGIN, "false");
					Toast.makeText(this, "登陆失败", Toast.LENGTH_LONG).show();
				} finally {
				}
			}
			
		} else {
			SharedprefUtil.put(this, QuhaoConstant.IS_LOGIN, "false");
			SharedprefUtil.put(this, QuhaoConstant.IS_AUTO_LOGIN, "false");
			QuhaoLog.i(TAG, "accessInfo is null");
		}
	}*/

	private void initConfig() {
		createSDCardDir();
	}

	/**
	 * 在SD卡上创建一个文件夹
	 */
	public void createSDCardDir() {
		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {
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
