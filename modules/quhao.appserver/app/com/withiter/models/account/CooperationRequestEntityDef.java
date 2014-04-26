package com.withiter.models.account;

import java.util.Date;

import play.modules.morphia.Model.NoAutoTimestamp;

import com.google.code.morphia.annotations.Entity;
import com.withiter.models.BaseModel;

@Entity
@NoAutoTimestamp
public class CooperationRequestEntityDef extends BaseModel{

	public String companyName;				// 公司名称
	public String peopleName;				// 联系人姓名
	public String peopleContact;			// 联系方式
	public String peopleEmail;				// 电子邮箱
	public Date createTime = new Date();	// 创建时间
	public boolean handle = false;			// 是否处理
	
}
