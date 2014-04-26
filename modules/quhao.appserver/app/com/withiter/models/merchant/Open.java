package com.withiter.models.merchant;

import java.util.List;

import play.modules.morphia.Model.MorphiaQuery;

import com.google.code.morphia.annotations.Entity;

@Entity
public class Open extends OpenEntityDef{
	/**
	 * @param aid 用户id
	 * @param mid of 商家id
	 * @return 
	 */
	public static long getAccountById(String mid,String accountId) {
		MorphiaQuery q = Open.q();
		q.filter("accountId", accountId);
		q.filter("mid", mid);
		
		return q.count();
	}
	
	/**
	 * @param mid of 商家id
	 * @return 希望的用户数
	 */
	public static long getNumberByMid(String mid){
		MorphiaQuery q = Open.q();
		q.filter("mid", mid);
		
		return q.count();
	}
}
