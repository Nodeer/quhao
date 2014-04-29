package com.withiter.models.merchant;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;

import com.google.code.morphia.annotations.Entity;

@Entity
public class Attention extends AttentionEntityDef{

	/**
	 * @param aid of account
	 * @param mid of merchant
	 * @return Attention
	 */
	public static Attention getAttentionById(String mid,String aid) {
		MorphiaQuery q = Attention.q();
		q.filter("accountId", aid);
		q.filter("mid", mid);
		
		return q.first();
	}
	
	/**
	 * get accounts
	 * @param mid of merchant
	 * @return
	 */
	public static List<Attention> getAccountsByMid(String mid) {
		MorphiaQuery q = Attention.q();
		q.filter("mid", mid);
		q.filter("flag", true);
		
		return q.asList();
	}
	
	public static List<Merchant> getMerchantsByAid(String aid){
		MorphiaQuery q = Attention.q();
		q.filter("accountId", aid).filter("flag", true).criteria("mid");
		List<Attention> as = q.asList();
		
		List<ObjectId> mids = new ArrayList<ObjectId>();
		for(Attention s : as){
			mids.add(new ObjectId(s.mid));
		}
		
		MorphiaQuery mq = Merchant.q();
		if(mids.size() == 0){
			return null;
		}else{
			mq.filter("_id in", mids);
			return mq.asList();
		}
	}
	
	public static int getAttentionCountByAid(String aid){
		MorphiaQuery q = Attention.q();
		q.filter("accountId", aid).filter("flag", true);
//		List<Attention> as = q.asList();
		
		return (int) q.count();
		
	}
}
