package com.withiter.models.merchant;

import java.util.List;

import com.google.code.morphia.annotations.Entity;

@Entity
public class Merchant extends MerchantEntityDef {

	public static List<Merchant> findByType(String cateType) {
		MorphiaQuery q = Merchant.q();
		q.filter("cateType", cateType);
		return q.asList();
	}
	
	
}
