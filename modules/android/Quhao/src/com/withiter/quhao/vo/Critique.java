package com.withiter.quhao.vo;

public class Critique {

	public String accountId;
	
	public String nickName;
	
	public int level;
	
	public int star;
	
	public double average;
	
	public String desc;
	
	public String updateDate;
	
	public Critique(String accountId, String nickName,int level, int star, double average,String desc,String updateDate)
	{
		this.accountId = accountId;
		this.nickName = nickName;
		this.level = level;
		this.star = star;
		this.average = average;
		this.desc = desc;
		this.updateDate = updateDate;
	}
}
