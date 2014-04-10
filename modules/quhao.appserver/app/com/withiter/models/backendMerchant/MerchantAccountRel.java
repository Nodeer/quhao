package com.withiter.models.backendMerchant;

import java.util.ArrayList;
import java.util.List;

import com.google.code.morphia.annotations.Entity;
import com.withiter.models.merchant.Merchant;


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
	
	public static List<Merchant> getMerchantByUid(String uid){
		List<Merchant> mList = new ArrayList<Merchant>();
		List<MerchantAccountRel> relList = getMerchantAccountRelList(uid);
		if(relList == null || relList.size() == 0){
			return null;
		}
		
		for(MerchantAccountRel rel : relList){
			mList.add((Merchant) Merchant.findById(rel.mid));
		}
		return mList;
	}
	
	public static MerchantAccountRel findByMid(String mid){
		MorphiaQuery q = MerchantAccountRel.q();
		q.filter("mid", mid);
		return q.first();
	}
}
