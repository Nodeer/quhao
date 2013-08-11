package com.withiter.quhao.vo;


public class Merchant
{
	public String id;
	public String imgUrl = "";
	public String name;
	public String address;
	public String phone;
	public String cateType;
	public String grade;
	public String averageCost;
	public String tags;
	public Integer kouwei;
	public Integer huanjing;
	public Integer fuwu;
	public Integer xingjiabi;
	public String teses;
	public String nickName;
	public String description;
	public String openTime;
	public String closeTime;
	public Integer marketCount;
	public boolean enable;
	public String joinedDate;
	
	public Merchant(String id,String imgUrl,String name,String address,String phone,String cateType,String grade,
			String averageCost,String tags,Integer kouwei, Integer huanjing, Integer fuwu, Integer xingjiabi,
			String teses, String nickName, String description, String openTime, String closeTime, Integer marketCount,
			boolean enable, String joinedDate)
	{
		this.id = id;
		this.imgUrl = imgUrl;
		this.name = name;
		this.address = address;
		this.phone = phone;
		this.cateType = cateType;
		this.grade = grade;
		this.averageCost = averageCost;
		this.tags = tags;
		this.kouwei = kouwei;
		this.huanjing = huanjing;
		this.fuwu = fuwu;
		this.xingjiabi = xingjiabi;
		this.teses = teses;
		this.nickName = nickName;
		this.description = description;
		this.openTime = openTime;
		this.closeTime = closeTime;
		this.marketCount = marketCount;
		this.enable = enable;
		this.joinedDate = joinedDate;
	}
	
	public Merchant()
	{
		
	}
}
