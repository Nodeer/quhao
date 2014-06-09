package com.withiter.models.activity;

import java.util.Date;

import com.withiter.models.BaseModel;

public abstract class ActivityEntityDef extends BaseModel {

	public String mid;
	public String cityCode;
	public String image;
	public Date start;
	public Date end;
	public boolean enable;
}
