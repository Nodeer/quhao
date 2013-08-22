package com.withiter.models.merchant;

import java.util.List;

import com.google.code.morphia.annotations.Entity;
import com.withiter.common.Constants;
import com.withiter.common.Constants.CateType;

@Entity
public class Category extends CategoryEntityDef {

	/**
	 * update category counts for CategoryJob
	 */
	public static void updateCounts() {
		CateType[] categories = Constants.CateType.values();
		for(CateType cate : categories){
			MorphiaQuery q = Category.q();
			q.filter("cateType", cate.toString());
			Category c = null;
			if(q.first() != null){
				c = q.first();
			}else{
				c = new Category();
				c.cateType = cate.toString();
			}
			long count = count(cate.toString());
			c.count = count;
			if(cate == CateType.mianbaodangao || cate == CateType.tianpinyinpin || cate == CateType.xiaochikuaican){
				c.enable = false;
			}
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
