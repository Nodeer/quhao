package com.withiter.quhao.util.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.withiter.quhao.util.QuhaoLog;

public class DBHelper extends SQLiteOpenHelper {
	
	private static final String TAG = DBHelper.class.getName();
	
	public static final String DATABASE_NAME = "quhao.db";
	public static final int DATABASE_VERSION = 1;
	public static final String ACCOUNT_TABLE = "accountinfo";

	private static final String CREATE_ACCOUNT_TABLE = "CREATE TABLE "
			+ ACCOUNT_TABLE + " (" + AccountInfoColumn._ID
			+ " integer primary key autoincrement," 
			+ AccountInfoColumn.USERID + " text,"
			+ AccountInfoColumn.PHONE + " text,"
			+ AccountInfoColumn.EMAIL + " text,"
//			+ AccountInfoColumn.PASSWORD + " text,"
			+ AccountInfoColumn.NICKNAME + " text,"
			+ AccountInfoColumn.BIRTHDAY + " text,"
			+ AccountInfoColumn.USERIMAGE + " text," 
			+ AccountInfoColumn.ENABLE + " text," 
			+ AccountInfoColumn.MOBILEOS + " text,"
			+ AccountInfoColumn.SIGNIN + " text,"
			+ AccountInfoColumn.ISSIGNIN + " text,"
			+ AccountInfoColumn.ISAUTO + " text,"
			+ AccountInfoColumn.MSG + " text,"
			+ AccountInfoColumn.LASTLOGIN + " text)";

	public DBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// db.execSQL("DROP TABLE IF EXISTS " +ACCOUNT_TABLE);
		db.execSQL(CREATE_ACCOUNT_TABLE);
		QuhaoLog.d("accountinfo", CREATE_ACCOUNT_TABLE);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

	/**
	 * 判断某张表是否存在
	 * 
	 * @param tabName
	 *            表名
	 * @return
	 */
	public boolean tabbleIsExist(String tableName) {
		boolean result = false;
		if (tableName == null) {
			return false;
		}
		SQLiteDatabase db = null;
		Cursor cursor = null;
		try {
			db = this.getReadableDatabase();
			String sql = "select count(*) as c from Sqlite_master  where type ='table' and name ='"
					+ tableName.trim() + "' ";
			cursor = db.rawQuery(sql, null);
			if (cursor.moveToNext()) {
				int count = cursor.getInt(0);
				if (count > 0) {
					result = true;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			cursor.close();
//			db.close();
		}
		return result;
	}

	/**
	 * create table
	 * @param tableName table name
	 */
	public void createTable(String tableName) {
		SQLiteDatabase db = this.getReadableDatabase();
		QuhaoLog.i(TAG, CREATE_ACCOUNT_TABLE);
		System.out.println(CREATE_ACCOUNT_TABLE);
		db.execSQL(CREATE_ACCOUNT_TABLE);
//		db.close();
	}

}
