package com.withiter.quhao.util.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {
	public static final String DATABASE_NAME = "quhao.db";
	public static final int DATABASE_VERSION = 1;
	public static final String ACCOUNT_TABLE = "accountinfo";

	private static final String CREATE_ACCOUNT_TABLE = "CREATE TABLE "
			+ ACCOUNT_TABLE + " (" + AccountInfoColumn._ID
			+ " integer primary key autoincrement," + AccountInfoColumn.PHONE
			+ " text," + AccountInfoColumn.EMAIL + " text,"
			+ AccountInfoColumn.PASSWORD + " text,"
			+ AccountInfoColumn.NICKNAME + " text,"
			+ AccountInfoColumn.BIRTHDAY + " text,"
			+ AccountInfoColumn.USERIMAGE + " text," + AccountInfoColumn.ENABLE
			+ " text," + AccountInfoColumn.MOBILEOS + " text,"
			+ AccountInfoColumn.LASTLOGIN + " text)";

	public DBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_ACCOUNT_TABLE);
		Log.d("accountinfo", CREATE_ACCOUNT_TABLE);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}
}
