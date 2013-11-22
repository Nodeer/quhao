package com.withiter.common;

public class Constants {

	public enum MobileOSType {
		IOS, ANDROID, WEB
	}
	
	public enum CateType{
		benbangcai, chuancai, dongnanyacai, haixian, hanguoliaoli, huoguo,
		mianbaodangao, ribenliaoli, shaokao, tianpinyinpin, xiangcai,
		xiaochikuaican, xican, xinjiangqingzhen, yuecaiguan, zhongcancaixi, zizhucan
	}
	
	public enum SortBy{
		cateType, grade, averageCost, kouwei, huanjing, fuwu, xingjiabi, markedCount
	}
	
	public enum DirectionMode{
		driving, walking, transit
	}
	
	public enum ReservationStatus{
		finished, canceled, expired, active
	}
	
	/**
	 * 
	 * finished : 吃饭完成
	 * 
	 * getNumber : 获取号码
	 * 
	 * exchange : 兑换 
	 * @author ASUS
	 *
	 */
	public enum CreditStatus{
		finished, getNumber,exchange
	}
	
	public static String COOKIE_USERNAME = "quhao_username";

	public static String SESSION_USERNAME = "quhao_username";
	
	public static String SESSION_ANDROID = "quhaoandroidsession";
	public static String SESSION_IOS = "quhaoiossession";
}
