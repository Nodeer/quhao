package com.withiter.quhao.vo;

import java.util.Date;
import java.util.List;

public class TopMerchant {

	public String id;
	public String name = "";
	public String url;
	
	public String address = "";
	public String[] telephone = {""};

	public String cateType;
	
	public String grade = "";
	public String averageCost = "";
	public List<String> tags = null;
	
	public int kouwei;
	public int huanjing;
	public int fuwu;
	public int xingjiabi;;
	
//	public List<Tese> teses;
	public String nickName;
	public String description;
	public String openTime;
	public String closeTime;	
	public int markedCount;
	public boolean enable = false;
	public String joinedDate = new Date().toString();
	
	public String merchantImage;
	
	public TopMerchant() {

	}

	public TopMerchant(String id, String imgUrl, String name) {
		this.id = id;
		this.url = imgUrl;
		this.name = name;
	}
}
