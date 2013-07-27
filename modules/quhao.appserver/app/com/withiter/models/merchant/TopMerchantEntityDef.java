package com.withiter.models.merchant;

import java.util.Date;
import java.util.List;

import com.google.code.morphia.annotations.Indexed;
import com.google.code.morphia.annotations.Reference;
import com.withiter.common.Constants;
import com.withiter.common.Constants.CateType;
import com.withiter.models.BaseModel;


public abstract class TopMerchantEntityDef extends BaseModel {
	@Indexed
	public String name = "";
	@Indexed
	public String address = "";
	@Indexed
	public String[] telephone = {""};
	
	public String cateType;
	
	public String grade = "";
	public String averageCost = "";
	public List<String> tags = null;
	
	public int kouwei;
	public int huanjing;
	public int fuwu;
	public int xingjiabi;;
	
	@Reference
	public List<Tese> teses;
	public String nickName;
	public String description;
	public String openTime;
	public String closeTime;	
	public int markedCount;
	public boolean enable = false;
	public String joinedDate = new Date().toString();
	
}
