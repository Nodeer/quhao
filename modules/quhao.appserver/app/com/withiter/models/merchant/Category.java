package com.withiter.models.merchant;

import java.util.List;

import com.google.code.morphia.annotations.Entity;
import com.withiter.common.Constants.CateType;

@Entity
public class Category extends CategoryEntityDef {

	// update category counts for CategoryJob
	public static void updateCounts() {
		// TODO update category counts for CategoryJob
	}

	public static List<Category> getAll() {
		MorphiaQuery q = Category.q();
		return q.asList();
	}
	
	public Category(CateType cateType, int count){
		this.cateType = cateType;
		this.count = count;
	}
}
