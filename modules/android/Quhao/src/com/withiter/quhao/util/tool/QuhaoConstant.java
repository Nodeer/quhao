package com.withiter.quhao.util.tool;

public class QuhaoConstant {

	public static String HTTP_URL = "http://192.168.0.12:9081/";
//	public static String HTTP_URL = "http://10.0.2.2:9081/";
	public static final String NEW_BODY = null;
	public static final int ADVERTISE_PIC_MAX = 409600;
	public static final String SHARED_PREFERENCES = "SHARED_PREFERENCES";
	public static final String IS_LOGIN = "IS_LOGIN";
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
	public static final String SINA_APP_KEY = "3183077347";
	
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
	
}
