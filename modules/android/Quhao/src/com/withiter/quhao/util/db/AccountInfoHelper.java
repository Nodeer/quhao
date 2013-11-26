package com.withiter.quhao.util.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.withiter.quhao.domain.AccountInfo;
import com.withiter.quhao.util.QuhaoLog;
import com.withiter.quhao.util.tool.QuhaoConstant;

public class AccountInfoHelper {
//	private DBHelper dbHelper;
//	private Context context;
//
//	private static final String TAG = AccountInfoHelper.class.getName();
//	public AccountInfoHelper(Context context) {
//		this.context = context;
//	}
//
//	public AccountInfoHelper open() {
//		this.dbHelper = new DBHelper(this.context);
//		return this; 
//	}
//	
//	public void dropAccountInfoTable(){
////		newDB.delete(DBHelper.ACCOUNT_TABLE, null, null);
//		try {
//			dbHelper.execSQL("DROP TABLE IF EXISTS " +QuhaoConstant.ACCOUNT_TABLE);
//		} catch (DBException e) {
//			
//			e.printStackTrace();
//		}
//	}
//
//	public List<AccountInfo> getAccountInfos() {
//		List<AccountInfo> accounts = new ArrayList<AccountInfo>();
//		AccountInfo account = null;
//
//		Cursor cursor;
//		try {
//			cursor = dbHelper.query(QuhaoConstant.ACCOUNT_TABLE,
//					AccountInfoColumn.PROJECTION, null, null, null, null, null);
//			if (cursor.getCount() > 0) {
//				cursor.moveToFirst();
//				while (!cursor.isAfterLast()) {
////					account = new AccountInfo();
////					account.setUserId(cursor
////							.getString(AccountInfoColumn.USERID_COLUMN));
////					account.setAccountId(cursor
////							.getString(AccountInfoColumn.ACCOUNTID_COLUMN));
////					account.setPhone(cursor
////							.getString(AccountInfoColumn.PHONE_COLUMN));
////					account.setEmail(cursor
////							.getString(AccountInfoColumn.EMAIL_COLUMN));
////					account.setPassword(cursor
////							.getString(AccountInfoColumn.PASSWORD_COLUMN));
////					account.setNickName(cursor
////							.getString(AccountInfoColumn.NICKNAME_COLUMN));
////					account.setBirthday(cursor
////							.getString(AccountInfoColumn.BIRTHDAY_COLUMN));
////					account.setUserImage(cursor
////							.getString(AccountInfoColumn.USERIMAGE_COLUMN));
////					account.setEnable(cursor
////							.getString(AccountInfoColumn.ENABLE_COLUMN));
////					account.setMobileOS(cursor
////							.getString(AccountInfoColumn.MOBILEOS_COLUMN));
////					account.setSignIn(cursor
////							.getString(AccountInfoColumn.SIGNIN_COLUMN));
////					account.setIsSignIn(cursor
////							.getString(AccountInfoColumn.ISSIGNIN_COLUMN));
////					account.setDianping(cursor
////							.getString(AccountInfoColumn.DIANPING_COLUMN));
////					account.setZhaopian(cursor
////							.getString(AccountInfoColumn.ZHAOPIAN_COLUMN));
////					account.setJifen(cursor
////							.getString(AccountInfoColumn.JIFEN_COLUMN));
////					account.setIsAuto(cursor
////							.getString(AccountInfoColumn.ISAUTO_COLUMN));
////					account.setMsg(cursor
////							.getString(AccountInfoColumn.MSG_COLUMN));
////					account.setLastLogin(cursor
////							.getString(AccountInfoColumn.LASTLOGIN_COLUMN));
////
////					accounts.add(account);
////					cursor.moveToNext();
//				}
//
//			}
//			if(cursor != null)
//			{
//				cursor.close();
//			}
//			cursor = null;
//		} catch (DBException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//		 
//		return accounts;
//	}
//
//	/**
//	 * 查询用户userId
//	 * 
//	 * @return
//	 */
//	public String queryUserId() {
//		String userId = "";
//		String[] col = new String[] { AccountInfoColumn.USERID };
//
//		if (dbHelper != null) {
//
//			Cursor cursor = null;
//			try {
//				cursor = dbHelper.query(QuhaoConstant.ACCOUNT_TABLE, col, null,
//						null, null, null, null);
//			} catch (DBException e) {
//				
//			}
//
//			int rowCount = 0;
//			if (null != cursor) {
//				rowCount = cursor.getCount();
//			}
//
//			if (rowCount > 0) {
//				cursor.moveToNext();
//				userId = cursor.getString(0);
//			}
//			if(cursor != null)
//			{
//				cursor.close();
//			}
//		}
//		// Log.e("+++++++++++++++ queryUserId", ""+ userId);
//		return userId;
//	}
//	
//	/**
//	 * 保存用户信息
//	 * 
//	 * @param userId
//	 * @param uname
//	 * @param password
//	 * @param isAuto
//	 */
//	public void saveAccountInfo(AccountInfo account) {
//		String oldUserId = queryUserId();
//
//		// 如果已经有记录，更新
//		if (!"".equals(oldUserId)) {
//			// 只更新是否自动登录
//			if (null == account.getUserId()) {
//				updateIsAuto(oldUserId, account.getUserId());
//			} else {
//				updateAccountinfo(account);
//			}
//		} else {
//			insertAccountinfo(account);
//		}
//
//	}
//	
//	/**
//	 * 更新用户信息
//	 * 
//	 * @param userId
//	 * @param password
//	 */
//	public void updateAccountinfo(AccountInfo account) {
//		String whereClause = AccountInfoColumn.USERID + " = ?";
//		String[] whereArgs = new String[] { account.getUserId() };
//		ContentValues value = new ContentValues();
//		value.put(AccountInfoColumn.ACCOUNTID, account.getAccountId());
//		value.put(AccountInfoColumn.PHONE, account.getPhone());
//		value.put(AccountInfoColumn.EMAIL, account.getEmail());
//		value.put(AccountInfoColumn.PASSWORD, account.getPassword());
//		value.put(AccountInfoColumn.NICKNAME, account.getNickName());
//		value.put(AccountInfoColumn.BIRTHDAY, account.getBirthday());
//		value.put(AccountInfoColumn.USERIMAGE, account.getUserImage());
//		value.put(AccountInfoColumn.ENABLE, account.getEnable());
//		value.put(AccountInfoColumn.MOBILEOS, account.getMobileOS());
//		value.put(AccountInfoColumn.SIGNIN, account.getSignIn());
//		value.put(AccountInfoColumn.ISSIGNIN, account.getIsSignIn());
////		value.put(AccountInfoColumn.DIANPING, account.getDianping());
////		value.put(AccountInfoColumn.ZHAOPIAN, account.getZhaopian());
////		value.put(AccountInfoColumn.JIFEN, account.getJifen());
//		value.put(AccountInfoColumn.ISAUTO, account.getIsAuto());
//		value.put(AccountInfoColumn.MSG, account.getMsg());
//		value.put(AccountInfoColumn.LASTLOGIN, account.getLastLogin());
//
//		try {
//			
//			dbHelper.updateRecord("accountinfo", value, whereClause, whereArgs);
//			Log.i(TAG, "updateDataByNameFromDB dic_address success! ");
//		} catch (DBException e) {
//			Log.w(TAG,
//					"updateDataByNameFromDB dic_address failed! "
//							+ e.toString());
//		}
//		// Log.e("+++++++++++++++ updateUserinfo", "");
//		// queryUserInfo();
//	}
//	
//	/**
//	 * 插入用户信息
//	 * 
//	 * @param userId
//	 * @param password
//	 */
//	public void insertAccountinfo(AccountInfo account) {
//		
//		// 插入记录
//		ContentValues value = new ContentValues();
//		value.put(AccountInfoColumn.USERID, account.getUserId());
//		value.put(AccountInfoColumn.ACCOUNTID, account.getAccountId());
//		value.put(AccountInfoColumn.PHONE, account.getPhone());
//		value.put(AccountInfoColumn.EMAIL, account.getEmail());
//		value.put(AccountInfoColumn.PASSWORD, account.getPassword());
//		value.put(AccountInfoColumn.NICKNAME, account.getNickName());
//		value.put(AccountInfoColumn.BIRTHDAY, account.getBirthday());
//		value.put(AccountInfoColumn.USERIMAGE, account.getUserImage());
//		value.put(AccountInfoColumn.ENABLE, account.getEnable());
//		value.put(AccountInfoColumn.MOBILEOS, account.getMobileOS());
//		value.put(AccountInfoColumn.SIGNIN, account.getSignIn());
//		value.put(AccountInfoColumn.ISSIGNIN, account.getIsSignIn());
////		value.put(AccountInfoColumn.DIANPING, account.getDianping());
////		value.put(AccountInfoColumn.ZHAOPIAN, account.getZhaopian());
////		value.put(AccountInfoColumn.JIFEN, account.getJifen());
//		value.put(AccountInfoColumn.ISAUTO, account.getIsAuto());
//		value.put(AccountInfoColumn.MSG, account.getMsg());
//		value.put(AccountInfoColumn.LASTLOGIN, account.getLastLogin());
//
//		try {
//			dbHelper.insertRecord(QuhaoConstant.ACCOUNT_TABLE, value);
//			Log.i(TAG, "InsertDataToDB member_node success! ");
//		} catch (DBException e) {
//			Log.w(TAG, "InsertDataToDB member_node failed! " + e.toString());
//		}
//		// Log.e("+++++++++++++++ insertuserinfo", "");
//		// queryUserInfo();
//	}
//	
//	/**
//	 * 更新是否自动登录
//	 * 
//	 * @param oldUserId
//	 * @param isAuto
//	 */
//	public void updateIsAuto(String oldUserId, String isAuto) {
//		String whereClause = AccountInfoColumn.USERID + " = ?";
//		String[] whereArgs = new String[] { oldUserId };
//		ContentValues value = new ContentValues();
//		value.put("isAuto", isAuto);
//
//		try {
//			dbHelper.updateRecord(QuhaoConstant.ACCOUNT_TABLE, value, whereClause, whereArgs);
//		} catch (DBException e) {
//		}
//		// Log.e("+++++++++++++++ updateIsAuto", "");
//		// queryUserInfo();
//	}
//	
//	public void close() {
//		if (dbHelper != null) {
//			dbHelper.close();
//		}
//	}
//
//	/**
//	 * check if the account table exists 
//	 * @param accountTable table name
//	 * @return
//	 */
//	public boolean tabbleIsExist(String accountTable) {
//		QuhaoLog.i(TAG, "tabbleIsExist invoked!");
//		return dbHelper.tabbleIsExist(QuhaoConstant.ACCOUNT_TABLE);
//	}
//
//	/**
//	 * create account table
//	 */
//	public void createAccountTable() {
//		QuhaoLog.i(TAG, "createAccountTable invoked!");
//		dbHelper.createTable(QuhaoConstant.ACCOUNT_TABLE);
//	}
}
