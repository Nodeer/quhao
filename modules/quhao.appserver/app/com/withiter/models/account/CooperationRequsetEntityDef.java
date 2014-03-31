package com.withiter.models.account;

import java.util.Date;

import play.modules.morphia.Model.NoAutoTimestamp;

import com.google.code.morphia.annotations.Entity;
import com.withiter.models.BaseModel;

@Entity
@NoAutoTimestamp
public class CooperationRequsetEntityDef extends BaseModel{

	public String companyName;
	public String peopleName;
	public String peopleContact;
	public String peopleEmail;
	public Date createTime = new Date();
	public boolean handle = false;
	
}
