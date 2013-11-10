package com.withiter.quhao.util.db;

import android.provider.BaseColumns;

public class AccountInfoColumn implements BaseColumns {
	public AccountInfoColumn() {
	}

	public static final String USERID = "USERID";
	public static final String ACCOUNTID = "ACCOUNTID";
	public static final String PHONE = "PHONE";
	public static final String EMAIL = "EMAIL";
	public static final String PASSWORD = "PASSWORD";
	public static final String NICKNAME = "NICKNAME";
	public static final String BIRTHDAY = "BIRTHDAY";
	public static final String USERIMAGE = "USERIMAGE";
	public static final String ENABLE = "ENABLE";
	public static final String MOBILEOS = "MOBILEOS";
	public static final String SIGNIN = "SIGNIN";
	public static final String ISSIGNIN = "ISSIGNIN";
//	public static final String DIANPING = "DIANPING";
//	public static final String ZHAOPIAN = "ZHAOPIAN";
//	public static final String JIFEN = "JIFEN";
	public static final String ISAUTO = "ISAUTO";
	public static final String MSG = "MSG";
	public static final String LASTLOGIN = "LASTLOGIN";

	public static final int USERID_COLUMN = 0;
	public static final int ACCOUNTID_COLUMN = 1;
	public static final int PHONE_COLUMN = 2;
	public static final int EMAIL_COLUMN = 3;
	public static final int PASSWORD_COLUMN = 4;
	public static final int NICKNAME_COLUMN = 5;
	public static final int BIRTHDAY_COLUMN = 6;
	public static final int USERIMAGE_COLUMN = 7;
	public static final int ENABLE_COLUMN = 8;
	public static final int MOBILEOS_COLUMN = 9;
	public static final int SIGNIN_COLUMN = 10;
	public static final int ISSIGNIN_COLUMN = 11;
//	public static final int DIANPING_COLUMN = 11;
//	public static final int ZHAOPIAN_COLUMN = 12;
//	public static final int JIFEN_COLUMN = 13;
	public static final int ISAUTO_COLUMN = 12;
	public static final int MSG_COLUMN = 13;
	public static final int LASTLOGIN_COLUMN = 14;

	public static final String[] PROJECTION = { 
			USERID, // 0
			ACCOUNTID,//1
			PHONE, // 2
			EMAIL, // 3
			PASSWORD, // 4
			NICKNAME, // 5
			BIRTHDAY, // 6
			USERIMAGE, // 7
			ENABLE, // 8
			MOBILEOS, // 9
			SIGNIN, // 10
			ISSIGNIN, // 11
//			DIANPING, // 11
//			ZHAOPIAN, // 12
//			JIFEN, // 13
			ISAUTO, // 12
			MSG, // 13
			LASTLOGIN // 14
	};
}
