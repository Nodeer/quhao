package com.withiter.models.merchant;

import java.util.Date;

import com.withiter.models.BaseModel;

public abstract class CommentEntityDef extends BaseModel {

	public String uid;
	public String mid;
	public String averageCost = "0";
	public float xingjiabi = 0;
	public float kouwei = 0;
	public float huanjing = 0;
	public float fuwu = 0;

	public String content = "";

	public Date date = new Date();
	public String location;
}
