package com.withiter.quhao;

import android.app.Application;
import android.util.Log;

import com.withiter.quhao.domain.AccountInfo;
import com.withiter.quhao.util.db.AccountInfoColumn;
import com.withiter.quhao.util.db.DBException;
import com.withiter.quhao.util.tool.DBTools;
import com.withiter.quhao.util.tool.InfoHelper;

public class QHClientApplication extends Application {

	private static final String TAG = "QHClientApplication";
	public boolean isLogined = false;
	public AccountInfo accessInfo = null;
	public boolean isAuto = false;
	private static final String CREATE_ACCOUNT_TABLE = "CREATE TABLE "
			+ " accountinfo (" + AccountInfoColumn._ID
			+ " integer primary key autoincrement," + AccountInfoColumn.PHONE
			+ " text," + AccountInfoColumn.EMAIL + " text,"
			+ AccountInfoColumn.PASSWORD + " text,"
			+ AccountInfoColumn.NICKNAME + " text,"
			+ AccountInfoColumn.BIRTHDAY + " text,"
			+ AccountInfoColumn.USERIMAGE + " text," + AccountInfoColumn.ENABLE
			+ " text," + AccountInfoColumn.MOBILEOS + " text,"
			+ " isAuto text," + AccountInfoColumn.LASTLOGIN + " text)";

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
		Log.d(TAG, "onCreate");
		isLogined = false;
		instance = this;
		initDBConfig();
		initConfig();
		super.onCreate();
	}

	private void initDBConfig() {
		Log.d(TAG, "init database config");
		accessInfo = InfoHelper.getAccountInfo(this);
		DBTools.init(instance);
		String sql = CREATE_ACCOUNT_TABLE;
		createTable("accountinfo", sql);
		sql = null;
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
