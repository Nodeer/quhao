package com.withiter.models.merchant;

import java.util.Date;
import java.util.List;

import com.google.code.morphia.annotations.Indexed;
import com.google.code.morphia.annotations.Reference;
import com.withiter.common.Constants;
import com.withiter.common.Constants.CateType;
import com.withiter.models.BaseModel;


public abstract class MerchantEntityDef extends BaseModel {
	@Indexed
	public String name = "";
	@Indexed
	public String address = "";
	@Indexed
	public String[] telephone;

	public CateType cateType;
	
	public String grade = "";
	public String averageCost = "";
	
	public int kouwei;
	public int huanjing;
	public int fuwu;
	
	@Reference
	public List<Tese> teses; 
	public String nickName;
	public String description;
	public Date openTime;
	public Date closeTime;	
	public int markedCount;
	public boolean enable = false;
	public Date joinedDate = new Date();
	
}
