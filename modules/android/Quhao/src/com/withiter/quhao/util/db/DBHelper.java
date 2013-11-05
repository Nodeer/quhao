package com.withiter.quhao.util.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.withiter.quhao.util.QuhaoLog;
import com.withiter.quhao.util.tool.QuhaoConstant;

public class DBHelper extends SQLiteOpenHelper {
	
	private static final String TAG = DBHelper.class.getName();
	
	public DBHelper(Context context) {
		super(context, QuhaoConstant.DATABASE_NAME, null, QuhaoConstant.DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// db.execSQL("DROP TABLE IF EXISTS " +ACCOUNT_TABLE);
		db.execSQL(QuhaoConstant.CREATE_ACCOUNT_TABLE);
		QuhaoLog.d("accountinfo", QuhaoConstant.CREATE_ACCOUNT_TABLE);

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
			if(cursor != null)
			{
				cursor.close();
			}
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
		QuhaoLog.i(TAG, QuhaoConstant.CREATE_ACCOUNT_TABLE);
		System.out.println(QuhaoConstant.CREATE_ACCOUNT_TABLE);
		db.execSQL(QuhaoConstant.CREATE_ACCOUNT_TABLE);
//		db.close();
	}

	public void creatTable(String tableName, boolean isRenew, String sql)
			throws DBException {
		SQLiteDatabase newsDB = null;
		try {
			newsDB = this.getWritableDatabase();
			if (isRenew) {
				newsDB.execSQL("DROP TABLE IF EXSITS " + tableName);
			}
			newsDB.execSQL(sql);
		} catch (SQLException e) {
			throw new DBException(e);
		}
		finally
		{
			newsDB.close();
		}
	}

	/**
	 * 插入一条新纪录的方法
	 * 
	 * @param tableName
	 *            对应的数据表名
	 * @param values
	 *            列名和值对
	 * @return 成功则返回插入的行号，失败返回0
	 * @throws DBException
	 *             数据库操作异常
	 */
	public long insertRecord(String tableName, ContentValues values)
			throws DBException {
		long rowID = 0;
		SQLiteDatabase newsDB = null;
		try {
			newsDB = this.getWritableDatabase();
			rowID = newsDB.insert(tableName, null, values);
		} catch (SQLException e) {
			// throw new DBException(e);
		}
		finally
		{
			newsDB.close();
		}
		return rowID;
	}
	
	/**
	 * 外部写好的非查询的SQL语句直接执行，通常不建议直接使用该方法
	 * 
	 * @param sql
	 *            完整的非查询类的SQL语句
	 * @throws DBException
	 *             数据库操作异常
	 */
	public void execSQL(String sql) throws DBException {
		SQLiteDatabase newsDB = null;
		try {
			newsDB = this.getWritableDatabase();
			newsDB.execSQL(sql);
		} catch (SQLException e) {
			// throw new DBException(e);
		}
		finally
		{
			newsDB.close();
		}
	}
	
	
	/**
	 * 查询方法
	 * 
	 * @param tableName
	 *            要查询的表名
	 * @param col
	 *            要查询的字段名数组
	 * @param selection
	 *            相当于sql语句的where部分，如果想返回所有的数据，那么就直接置为null
	 * @param selectionArg
	 *            在selection部分，你有可能用到“?”,那么在selectionArgs定义的字符串会代替selection中的“?”
	 * @param groupBy
	 *            定义查询出来的数据是否分组，如果为null则说明不用分组
	 * @param having
	 *            相当于sql语句当中的having部分
	 * @param orderBy
	 *            来描述我们期望的返回值是否需要排序，如果设置为null则说明不需要排序
	 * @return Cursor 游标对象
	 * @throws DBException
	 *             数据库操作异常
	 */
	public Cursor query(String tableName, String[] col, String selection,
			String[] selectionArg, String groupBy, String having, String orderBy)
			throws DBException {
		SQLiteDatabase newsDB = null;
		Cursor cur = null;
		try {
			newsDB = this.getReadableDatabase();
			cur = newsDB.query(tableName, col, selection, selectionArg,
					groupBy, having, orderBy);
		} catch (SQLException e) {
			// throw new DBException(e);
		}
		return cur;
	}
	
	/**
	 * 更新一条纪录的方法
	 * 
	 * @param tableName
	 *            对应的数据表名
	 * @param values
	 *            列名和值对
	 * @param whereClause
	 *            相当于sql语句的where部分
	 * @param whereArgs
	 *            替换whereClause中的“?”
	 * @return 更新成功返回true，失败返回false
	 * @throws DBException
	 *             数据库操作异常
	 */
	public boolean updateRecord(String tableName, ContentValues values,
			String whereClause, String[] whereArgs) throws DBException {
		long rowID = -1;
		SQLiteDatabase newsDB = null;
		try {
			newsDB = this.getWritableDatabase();
			rowID = newsDB.update(tableName, values, whereClause, whereArgs);
		} catch (SQLException e) {
			// throw new DBException(e);
		}
		finally
		{
			newsDB.close();
		}
		return rowID > 0;
	}
}
