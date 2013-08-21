package com.withiter.quhao.util.db;

import java.util.ArrayList;
import java.util.List;

import com.withiter.quhao.domain.AccountInfo;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class AccountInfoHelper {
	private DBHelper dbHelper;
	private SQLiteDatabase newDB;
	private Context context;

	public AccountInfoHelper(Context context) {
		this.context = context;
	}

	public AccountInfoHelper open() {
		this.dbHelper = new DBHelper(this.context);
		this.newDB = this.dbHelper.getWritableDatabase();
		return this;
	}

	public List<AccountInfo> getAccountInfos() {
		List<AccountInfo> accounts = new ArrayList<AccountInfo>();
		AccountInfo account = null;

		Cursor cursor = newDB.query(DBHelper.ACCOUNT_TABLE,
				AccountInfoColumn.PROJECTION, null, null, null, null, null);

		if (cursor.getCount() > 0) {
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				account = new AccountInfo();
				account.setUserId(cursor
						.getString(AccountInfoColumn._ID_ACCOUNT));
				account.setPhone(cursor
						.getString(AccountInfoColumn.PHONE_COLUMN));
				account.setEmail(cursor
						.getString(AccountInfoColumn.EMAIL_COLUMN));
				account.setPassword(cursor
						.getString(AccountInfoColumn.PASSWORD_COLUMN));
				account.setNickName(cursor
						.getString(AccountInfoColumn.NICKNAME_COLUMN));
				account.setBirthday(cursor
						.getString(AccountInfoColumn.BIRTHDAY_COLUMN));
				account.setUserImage(cursor
						.getString(AccountInfoColumn.USERIMAGE_COLUMN));
				account.setEnable(cursor
						.getString(AccountInfoColumn.ENABLE_COLUMN));
				account.setMobileOS(cursor
						.getString(AccountInfoColumn.MOBILEOS_COLUMN));
				account.setLastLogin(cursor
						.getString(AccountInfoColumn.LASTLOGIN_COLUMN));

				accounts.add(account);
				cursor.moveToNext();
			}

		}
		cursor.close();
		cursor = null;
		return accounts;
	}

	public void close() {
		if (dbHelper != null) {
			dbHelper.close();
		}
	}
}
