package com.withiter.quhao;

import android.app.Application;
import android.util.Log;

import com.withiter.quhao.domain.AccountInfo;
import com.withiter.quhao.util.db.AccountInfoColumn;
import com.withiter.quhao.util.db.DBException;
import com.withiter.quhao.util.tool.DBTools;
import com.withiter.quhao.util.tool.InfoHelper;

public class QHClientApplication extends Application {

	private static final String TAG = QHClientApplication.class.getName();
	public boolean isLogined = false;
	public AccountInfo accessInfo = null;
	public boolean isAuto = false;
	private static String CREATE_ACCOUNT_TABLE = "";

	static {
		StringBuilder sb = new StringBuilder("");
		sb.append("CREATE TABLE ").append(" accountinfo (")
				.append(AccountInfoColumn._ID)
				.append(" integer primary key autoincrement,")
				.append(AccountInfoColumn.PHONE).append(" text,")
				.append(AccountInfoColumn.EMAIL).append(" text,")
				.append(AccountInfoColumn.PASSWORD).append(" text,")
				.append(AccountInfoColumn.NICKNAME).append(" text,")
				.append(AccountInfoColumn.BIRTHDAY).append(" text,")
				.append(AccountInfoColumn.USERIMAGE).append(" text,")
				.append(AccountInfoColumn.ENABLE).append(" text,")
				.append(AccountInfoColumn.MOBILEOS).append(" text,")
				.append(" isAuto text,").append(AccountInfoColumn.LASTLOGIN)
				.append(" text)");
		CREATE_ACCOUNT_TABLE = sb.toString();
	}

	/**
	 * 单例
	 */
	private static QHClientApplication instance;

	public QHClientApplication() {
		super();
	}

	public static QHClientApplication getInstance() {
		if (instance == null) {
			throw new IllegalStateException("Not yet initialized");
		}
		return instance;
	}

	@Override
	public void onCreate() {
		Log.i(TAG, "onCreate method is called");
		isLogined = false;
		instance = this;
		initDBConfig();
		initConfig();
		super.onCreate();
	}

	private void initDBConfig() {
		Log.i(TAG, "init database config");
		accessInfo = InfoHelper.getAccountInfo(this);
		DBTools.init(instance);
		boolean flag = false;
		try {
			flag = DBTools.getInstance().tabbleIsExist("accountinfo");
			Log.i(TAG, "accountinfo table exists : " + flag);
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(TAG, e.getCause().toString());
		}
		if (!flag) {
			String sql = CREATE_ACCOUNT_TABLE;
			createTable("accountinfo", sql);
			sql = null;
		}
	}

	private void createTable(String tableName, String sql) {
		try {
			DBTools.getInstance().creatTable(tableName, false, sql);
		} catch (DBException e) {
			Log.d(TAG, "The table " + tableName
					+ " already exist, create failure");
		}
	}

	private void initConfig() {
		// TODO Auto-generated method stub
	}
}
