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
	public String name = "";
	@Indexed
	public String address = "";
	@Indexed
	public String[] telephone = {""};
	
	public String cateType;
	public String merchantImage = "";
	
	public Set merchantImageSet = new HashSet<String>();
	
	// 百度坐标
	public String x;
	public String y;
	
	public String grade = "";
	public String averageCost = "";
	public List<String> tags = null;
	
	public int kouwei;
	public int huanjing;
	public int fuwu;
	public int xingjiabi;
	
	@Reference
	public List<Tese> teses;
	public String nickName;
	public String description;
	public String openTime;
	public String closeTime;	
	public int markedCount;
	public boolean enable = false;
	public String joinedDate = new Date().toString();
	
	/**
	 * add by CROSS 2013-9-27
	 * eg: {2,4,6,8} 此商家有2人，4人，6人，8人桌
	 */
	public String[] seatType;
	
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
