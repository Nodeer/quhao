package com.withiter.quhao.vo;

public class Category {

	public long count = 0;
	public String categoryType;
	public String cateName;
	public String url = "";

	public Category() {

	}

	public Category(long count, String categoryType, String cateName,
			String url) {
		this.count = count;
		this.categoryType = categoryType;
		this.cateName = cateName;
		this.url = url;
	}
}
