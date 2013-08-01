package com.withiter.common;

public class Constants {

	public enum MobileOSType {
		IOS, ANDROID
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
		finish, cancel, expired
	}
	
	public static String COOKIE_USERNAME = "quhao_username";

	public static String SESSION_USERNAME = "quhao_username";
}
