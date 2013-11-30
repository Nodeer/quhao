package com.withiter.quhao.vo;


public class Comment {

	private String uid;
	
	public String accountId;
	
	public String nickName;
	
	public String mid;
	
	public String merchantName;
	
	public String merchantAddress;
	
	public String rId;
	
	public String averageCost = "0";
	public float xingjiabi = 0;
	public float kouwei = 0;
	public float huanjing = 0;
	public float fuwu = 0;
	
	public String content;
	
	public String created;
	public String modified;
	
	public Comment(String uid,String accountId, String nickName,String mid,String merchantName,String merchantAddress,String rId,String averageCost, 
			float xingjiabi, float kouwei,float huanjing,float fuwu,String content,String created,String modified)
	{
		this.uid = uid;
		this.accountId = accountId;
		this.nickName = nickName;
		this.mid = mid;
		this.merchantName = merchantName;
		this.merchantAddress = merchantAddress;
		this.rId = rId;
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
