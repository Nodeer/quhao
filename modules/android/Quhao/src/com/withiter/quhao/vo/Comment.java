package com.withiter.quhao.vo;


public class Comment {

	private String uid;
	
	public String accountId;
	
	public String nickName;
	
	private String mid;
	
	public String averageCost = "0";
	public int xingjiabi = 0;
	public int kouwei = 0;
	public int huanjing = 0;
	public int fuwu = 0;
	
	public String content;
	
	public String created;
	public String modified;
	
	public Comment(String uid,String accountId, String nickName,String mid,String averageCost, 
			int xingjiabi, int kouwei,int huanjing,int fuwu,String content,String created,String modified)
	{
		this.uid = uid;
		this.accountId = accountId;
		this.nickName = nickName;
		this.mid = mid;
		this.averageCost = averageCost;
		this.xingjiabi = xingjiabi;
		this.kouwei = kouwei;
		this.huanjing = huanjing;
		this.fuwu = fuwu;
		this.content = content;
		this.created = created;
		this.modified = modified;
	}
}
