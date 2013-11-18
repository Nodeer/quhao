package com.withiter.models.merchant;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
	
	public String merchantImage = "";
	public Set merchantImageSet = new HashSet<String>();
	
	public String mid;
	
	public String cateType;
	
	public List<String> tags = null;
	
	public float averageCost = 0f;
	public float grade = 0f;
	public float kouwei = 0f;
	public float huanjing = 0f;
	public float fuwu = 0f;
	public float xingjiabi = 0f;
	
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
