package com.withiter.quhao.vo;

public class Category {

	public long count = 0;
	public String categoryType;
	public String cateName;

	public Category() {

	}

	public Category(long count, String categoryType, String cateName) {
		this.count = count;
		this.categoryType = categoryType;
		this.cateName = cateName;
	}
}
