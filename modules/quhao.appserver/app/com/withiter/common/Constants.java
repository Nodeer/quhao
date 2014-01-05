package com.withiter.common;

import java.util.HashMap;
import java.util.Map;

public class Constants {

	public enum MobileOSType {
		IOS, ANDROID, WEB
	}
	
	public enum CateType{
		benbangcai, chuancai, dongnanyacai, haixian, hanguoliaoli, huoguo,
		mianbaodangao, ribenliaoli, shaokao, tianpinyinpin, xiangcai,
		xiaochikuaican, xican, xinjiangqingzhen, yuecaiguan, zhongcancaixi, zizhucan
	}
	
	public static Map<String, String> categorys = new HashMap<String, String>();
	
	static{
		categorys.put(CateType.benbangcai.toString(), "本帮菜");
		categorys.put(CateType.chuancai.toString(), "川菜");
		categorys.put(CateType.dongnanyacai.toString(), "东南亚菜");
		categorys.put(CateType.haixian.toString(), "海鲜");
		categorys.put(CateType.hanguoliaoli.toString(), "火锅料理");
		categorys.put(CateType.huoguo.toString(), "火锅");
		categorys.put(CateType.mianbaodangao.toString(), "面包蛋糕");
		categorys.put(CateType.ribenliaoli.toString(), "日本料理");
		categorys.put(CateType.shaokao.toString(), "烧烤");
		categorys.put(CateType.tianpinyinpin.toString(), "甜品饮品");
		categorys.put(CateType.xiangcai.toString(), "湘菜");
		categorys.put(CateType.xiaochikuaican.toString(), "小吃快餐");
		categorys.put(CateType.xican.toString(), "西餐");
		categorys.put(CateType.xinjiangqingzhen.toString(), "新疆清真");
		categorys.put(CateType.yuecaiguan.toString(), "粤菜馆");
		categorys.put(CateType.zhongcancaixi.toString(), "中餐菜系");
		categorys.put(CateType.zizhucan.toString(), "自助餐");
		
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
		finished, getNumber,exchange,credit
	}
	
	public static String COOKIE_USERNAME = "quhao_username";

	public static String SESSION_USERNAME = "quhao_username";
	
	public static String SESSION_ANDROID = "quhaoandroidsession";
	public static String SESSION_IOS = "quhaoiossession";
}
