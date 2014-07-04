package com.withiter.models.merchant;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.code.morphia.annotations.Indexed;
import com.google.code.morphia.annotations.Reference;
import com.withiter.models.BaseModel;


public abstract class MerchantEntityDef extends BaseModel {
	@Indexed
	public String name = "";								// 商家名称 default value from AMAP
	@Indexed
	public String address = "";								// 商家地址 default value from AMAP
	@Indexed
	public String[] telephone = {""};						// 商家电话 default value from AMAP
	
	@Indexed
	public String poiId;									// 高德地图唯一标示 value from AMAP
	public String cateType;									// 口味 default value from AMAP
	public String cateName;									// 口味（中文）
	public String cateType1;								// 口味 default value from AMAP
	public String cateName1;								// 口味（中文）
	public String merchantImage = "";						// 商家 LOGO
	public String merchantImageBig = "";					// 商家 LOGO 大图
	
	public String cityCode;									// 商家所在城市码 value from AMAP
	public String postcode;									// 商家PostCode（如：上海021）value from AMAP
	public String email;									// 商家邮箱 value from AMAP
	public String website;									// 商家网址 value from AMAP
	public Set merchantImageSet = new HashSet<String>();	// 存放商家展示图片
	
	// 高德坐标
	public String x;										// (Lng 经度)商家地图X坐标 value from AMAP
	public String y;										// (Lat 纬度)商家地图Y坐标 value from AMAP
	
	// 坐标
	public double[] loc = new double[2];					// 坐标[x,y],用于mongodb的距离查询
	
	public List<String> tags = null;						// 商家关键字
	public float averageCost = 0f;							// 人均消费
	public float grade = 0f;								// 综合评价
	public float kouwei = 0f;								// 口味评价
	public float huanjing = 0f;								// 环境评价
	public float fuwu = 0f;									// 服务评价
	public float xingjiabi = 0f;							// 性价比评价
	
	@Reference
	public List<Tese> teses;								// 商家特色
	public String nickName = "";							// 商家别名
	public String description = "";							// 商家描述
	public String openTime;									// 营业开始时间
	public String closeTime;								// 营业结束时间
	public int markedCount = 0;								// 关注数
	public boolean enable = false;							// 是否正常使用后台排队平台
	public boolean online = true;							// 默认在线
	public boolean youhui = false;							// 是否有优惠
	public Date joinedDate = new Date();					// 导入数据库时间 
	public String[] seatType;								// 桌位类型 eg: {2,4,6,8} 此商家有2人，4人，6人，8人桌
	
	public String dianpingFen = "0";						// 显示大众点评评分 0 表示不显示
	public String dianpingLink = "";						// 大众点评商家链接
	
	public String gTelephone(){
		StringBuilder sb = new StringBuilder();
		String tels = "";
		for(String s : telephone){
			sb.append(s).append(",");
		}
		if(sb.length() - 1 == sb.lastIndexOf(",")){
			tels = sb.substring(0,sb.length() - 1);
		}
		return tels;
	}
	
}
