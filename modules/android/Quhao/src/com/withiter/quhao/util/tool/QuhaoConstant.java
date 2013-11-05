package com.withiter.quhao.util.tool;

import com.withiter.quhao.util.db.AccountInfoColumn;

public class QuhaoConstant {

	public static String HTTP_URL = "http://192.168.0.20:9081/";
//	public static String HTTP_URL = "http://10.0.2.2:9081/";
	public static final String NEW_BODY = null;
	public static final int ADVERTISE_PIC_MAX = 409600;
	public static final String SHARED_PREFERENCES = "SHARED_PREFERENCES";
	public static final String IS_LOGIN = "IS_LOGIN";
	public static final String IS_AUTO_LOGIN = "IS_AUTO_LOGIN";
	public static boolean test = false;
	
	/**
	 * 共享缓存文件名称
	 */
	public static final String CONFIG_CACHE = "QUHAO_CACHE";
	
	/**
	 * 存储卡上存储图片目录
	 */
	public static final String IMAGES_SD_URL = "quhao/images";
	
	/**
	 * 存储卡上存储日志目录
	 */
	public static final String LOGS_SD_URL = "quhao/logs";
	
	/**
	 * 新浪的应用key， 在官方申请的
	 */
	public static final String SINA_APP_KEY = "3183077347";//"3183077347";
	
	/**
	 * 开发者的REDIRECT URL
	 */
	public static final String SINA_REDIRECT_URL = "http://www.sina.com";
	
	/**
	 * 新支持的scope,支持传入多个scope权限，用逗号分隔
	 */
	public static final String SINA_SCOPE = "email,direct_messages_read,direct_messages_write," +
			"friendships_groups_read,friendships_groups_write,statuses_to_me_read," +
			"follow_app_official_microblog,invitation_write";
	/**
	 * 下一頁url
	 */
	public static String NEXT_CMD = "";

	/**
	 * 更新時間
	 */
	public static long TIME_STAMP = 0;
	public static long TIME_STAMP_OLD = 0;
	public static String CREATE_ACCOUNT_TABLE = "CREATE TABLE accountinfo (" + AccountInfoColumn._ID
			+ " integer primary key autoincrement," 
			+ AccountInfoColumn.USERID + " text,"
			+ AccountInfoColumn.PHONE + " text,"
			+ AccountInfoColumn.EMAIL + " text,"
			+ AccountInfoColumn.PASSWORD + " text,"
			+ AccountInfoColumn.NICKNAME + " text,"
			+ AccountInfoColumn.BIRTHDAY + " text,"
			+ AccountInfoColumn.USERIMAGE + " text," 
			+ AccountInfoColumn.ENABLE + " text," 
			+ AccountInfoColumn.MOBILEOS + " text,"
			+ AccountInfoColumn.SIGNIN + " text,"
			+ AccountInfoColumn.ISSIGNIN + " text,"
//			+ AccountInfoColumn.DIANPING + " text,"
//			+ AccountInfoColumn.ZHAOPIAN + " text,"
//			+ AccountInfoColumn.JIFEN + " text,"
			+ AccountInfoColumn.ISAUTO + " text,"
			+ AccountInfoColumn.MSG + " text,"
			+ AccountInfoColumn.LASTLOGIN + " text)";
	public static final String ACCOUNT_TABLE = "accountinfo";
	
	public static final String DATABASE_NAME = "quhao.db";
	
	public static final int DATABASE_VERSION = 1;
}
