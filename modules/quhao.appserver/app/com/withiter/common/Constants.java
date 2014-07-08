package com.withiter.common;

import java.util.HashMap;
import java.util.Map;

public class Constants {

	public enum MobileOSType {
		IOS, ANDROID, WEB
	}
	
	public enum CateType{
		benbangcai, chuancai, dongnanyacai, haixian, huoguo,
		liaoli, tianpinyinpin, xiangcai,
		xiaochikuaican, xican, xinjiangqingzhen, yuecai, 
		zhongcancaixi, zizhucan, shandongcai, jiangsucai, zhejiangcai, 
		anhuicai, fujiancai, dongbeicai, xibeicai, beijingcai, hubeicai, 
		yunguicai, taiwancai
	}
	
	public static Map<String, String> categorys = new HashMap<String, String>();
	
	static {
		categorys.put(CateType.benbangcai.toString(), "上海菜");
		categorys.put(CateType.chuancai.toString(), "川菜");
		categorys.put(CateType.dongnanyacai.toString(), "东南亚菜");
		categorys.put(CateType.haixian.toString(), "海鲜");
		categorys.put(CateType.huoguo.toString(), "火锅");
		categorys.put(CateType.liaoli.toString(), "料理");
		categorys.put(CateType.tianpinyinpin.toString(), "甜品饮品");
		categorys.put(CateType.xiangcai.toString(), "湘菜");
		categorys.put(CateType.xiaochikuaican.toString(), "小吃快餐");
		categorys.put(CateType.xican.toString(), "西餐");
		categorys.put(CateType.xinjiangqingzhen.toString(), "新疆清真");
		categorys.put(CateType.yuecai.toString(), "粤菜");
		categorys.put(CateType.zhongcancaixi.toString(), "中餐");
		categorys.put(CateType.zizhucan.toString(), "自助餐");
		categorys.put(CateType.shandongcai.toString(), "山东菜");
		categorys.put(CateType.jiangsucai.toString(), "江苏菜");
		categorys.put(CateType.zhejiangcai.toString(), "浙江菜");
		categorys.put(CateType.anhuicai.toString(), "安徽菜");
		categorys.put(CateType.fujiancai.toString(), "福建菜");
		categorys.put(CateType.dongbeicai.toString(), "东北菜");
		categorys.put(CateType.xibeicai.toString(), "西北菜");
		categorys.put(CateType.beijingcai.toString(), "北京菜");
		categorys.put(CateType.hubeicai.toString(), "湖北菜");
		categorys.put(CateType.yunguicai.toString(), "云贵菜");
		categorys.put(CateType.taiwancai.toString(), "台湾菜");
	}
	
	public enum SortBy{
		cateType, grade, averageCost, kouwei, huanjing, fuwu, xingjiabi, markedCount
	}
	
	public enum DirectionMode{
		driving, walking, transit
	}
	
	public enum ReservationStatus{
		finished, canceled, expired, active,
		invalidByMerchantUpdate // when Merchant's seatType updated, the reservations will be this status
	}
	
	/**
	 * finished : 吃饭完成
	 * getNumber : 获取号码
	 * exchange : 兑换
	 * comment : 评价
	 * expired : 过期 
	 */
	public enum CreditStatus{
		finished, 		// finish one reservation, will get back one credit
		getNumber,		// get one reservation, will cost one credit
		exchange,		// exchange to get the credits
		comment,		// comment will get back one credit
		canceled,		// cancel will get back one credit
		expired,		// expired reservation, will not get back one credit
	}
	
	public enum YudingStatus{
		created,		// 用户创建了预定
		canceled,		// 用户取消了预定
		confirmed,		// 商家确认了预定
		finished,		// 商家完成此次预定
		expired			// 商家过期了此预定
	}
	
	public static String COOKIE_USERNAME = "quhao_username";
	public static String SESSION_USERNAME = "quhao_username";

	public static String ADMIN_COOKIE_USERNAME = "admin_quhao_username";
	public static String ADMIN_SESSION_USERNAME = "admin_quhao_username";
	
	public static String SESSION_ANDROID = "quhaoandroidsession";
	public static String SESSION_IOS = "quhaoiossession";
}
