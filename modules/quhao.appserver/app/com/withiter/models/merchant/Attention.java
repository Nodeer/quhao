package com.withiter.models.merchant;

import java.util.List;

import play.modules.morphia.Model.MorphiaQuery;

import com.google.code.morphia.annotations.Entity;
import com.withiter.models.account.Account;

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
}
