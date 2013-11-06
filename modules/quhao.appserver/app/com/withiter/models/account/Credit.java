package com.withiter.models.account;

import java.util.List;

import play.modules.morphia.Model.MorphiaQuery;
import play.modules.morphia.Model.NoAutoTimestamp;

import com.google.code.morphia.annotations.Entity;
import com.withiter.models.merchant.Merchant;

@Entity
@NoAutoTimestamp
public class Credit extends CreditEntityDef {

	/**
	 * find credits by account ID
	 * @param accountId account ID
	 * @return credit list
	 */
	public static List<Credit> findByAccountId(String accountId) {
		MorphiaQuery q = Credit.q();
		q.filter("accountId", accountId);
		return q.asList();
	}

	
}
