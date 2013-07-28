package com.withiter.quhao.vo;

public class Category
{

	public long count = 0;
	public String categoryType;
	public String url = "";
	
	public Category()
	{
		
	}
	
	public Category(long count, String categoryType, String url)
	{
		this.count = count;
		this.categoryType = categoryType;
		this.url = url;
	}
}
