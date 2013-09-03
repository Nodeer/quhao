package com.withiter.models.backendMerchant;

import java.util.List;

import com.google.code.morphia.annotations.Entity;


@Entity
public class MerchantAccountRel extends MerchantAccountRelEntityDef{
	
	/**
	 * Get MerchantAccountRel object list by using uid
	 * @param uid
	 */
	public static List<MerchantAccountRel> getMerchantAccountRelList(String uid){
		List<MerchantAccountRel> relList = null;
		MorphiaQuery q = MerchantAccountRel.q();
		q.filter("uid", uid);
		relList = q.asList();
		return relList;
	}
}
