package com.withiter.quhao.util.tool;

import com.withiter.quhao.util.db.DBException;
import com.withiter.quhao.util.db.DBHelper;

import android.content.ContentValues;
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
		try {
			rowID = newsDB.insert(tableName, null, values);
		} catch (SQLException e) {
			// throw new DBException(e);
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
		try {
			newsDB.execSQL(sql);
		} catch (SQLException e) {
			// throw new DBException(e);
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
		Cursor cur = null;
		try {
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
		try {
			rowID = newsDB.update(tableName, values, whereClause, whereArgs);
		} catch (SQLException e) {
			// throw new DBException(e);
		}
		return rowID > 0;
	}
	
	/**
	 * 关闭连接
	 */
	public void onClose() {
		if (dbHelper != null) {
			dbHelper.close();
		}
	}
}
