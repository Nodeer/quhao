package com.withiter.quhao.util.tool;

import com.withiter.quhao.util.db.DBException;
import com.withiter.quhao.util.db.DBHelper;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public final class DBTools {

	private static DBTools dbTools = null;
	private SQLiteDatabase newsDB;
	private DBHelper dbHelper;

	private DBTools(Context context) {
		dbHelper = new DBHelper(context);
		newsDB = dbHelper.getWritableDatabase();
	}

	public static void init(Context context) {
		dbTools = new DBTools(context);
	}

	public static DBTools getInstance() {
		return dbTools;
	}

	public void creatTable(String tableName, boolean isRenew, String sql)
			throws DBException {
		try {
			if (isRenew) {
				newsDB.execSQL("DROP TABLE IF EXSITS " + tableName);
			}
			newsDB.execSQL(sql);
		} catch (SQLException e) {
			throw new DBException(e);
		}
	}

	/**
	 * 判断某张表是否存在
	 * 
	 * @param tableName
	 *            表名
	 * @return
	 * @throws Exception 
	 */
	public boolean tabbleIsExist(String tableName) throws Exception {
		boolean result = false;
		if (tableName == null) {
			return false;
		}
		Cursor cursor = null;
		try {
			String sql = "select count(*) as c from Sqlite_master  where type ='table' and name ='"
					+ tableName.trim() + "' ";
			cursor = newsDB.rawQuery(sql, null);
			if (cursor.moveToNext()) {
				int count = cursor.getInt(0);
				if (count > 0) {
					result = true;
				}
			}
		} catch (Exception e) {
			throw new Exception(e);
		}
		return result;
	}
}
