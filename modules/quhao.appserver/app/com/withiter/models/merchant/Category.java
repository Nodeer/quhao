package com.withiter.models.merchant;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.code.morphia.annotations.Entity;
import com.withiter.common.Constants;
import com.withiter.common.Constants.CateType;

@Entity
public class Category extends CategoryEntityDef {

	public static Map<String, String> categorys = new HashMap<String, String>();
	
	/**
	 * update category counts for CategoryJob
	 */
	public static void updateCounts() {
		CateType[] categories = Constants.CateType.values();
		Map<String, String> cates = Constants.categorys;
		for(CateType cate : categories){
			MorphiaQuery q = Category.q();
			q.filter("cateType", cate.toString());
			Category c = null;
			if(q.first() != null){
				c = q.first();
			}else{
				c = new Category();
			}
			c.cateType = cate.toString();
			c.cateName = cates.get(cate.toString());
			c.count = count(cate.toString());
			c.save();
		}
	}

	// 系统第一次启动时，初始化Category
	public static void init(){
		CateType[] categories = Constants.CateType.values();
		Map<String, String> cates = Constants.categorys;
		Category c = null;
		for(CateType cate : categories){
			c = new Category();
			c.cateType = cate.toString();
			c.cateName = cates.get(cate.toString());
			c.save();
		}
	}
	
	private static long count(String cateType){
		MorphiaQuery q = Merchant.q();
		q.filter("cateType", cateType.toLowerCase());
		return q.count();
	}
	
	/**
	 * Get all categories
	 * @return the list of all category
	 */
	public static List<Category> getAll() {
		MorphiaQuery q = Category.q();
		q.filter("enable", true);
		return q.asList();
	}
	
	public Category(String cateType, int count){
		this.cateType = cateType;
		this.count = count;
	}
	
	public Category(){
		
	}
}
