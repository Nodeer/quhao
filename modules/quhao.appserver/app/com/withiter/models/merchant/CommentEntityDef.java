package com.withiter.models.merchant;

import java.util.Date;

import com.withiter.models.BaseModel;

public abstract class CommentEntityDef extends BaseModel {

	public String uid;
	public String mid;
	public String averageCost;
	public int xingjiabi;
	public int kouwei;
	public int huanjing;
	public int fuwu;
	
	public Date date;
	public String location;
}
