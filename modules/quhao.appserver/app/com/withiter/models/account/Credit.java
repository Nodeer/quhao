package com.withiter.models.account;

import java.util.List;

import play.modules.morphia.Model.MorphiaQuery;
import play.modules.morphia.Model.NoAutoTimestamp;

import cn.bran.japid.util.StringUtils;

import com.google.code.morphia.annotations.Entity;
import com.withiter.common.Constants.CateType;
import com.withiter.models.merchant.Merchant;

@Entity
@NoAutoTimestamp
public class Credit extends CreditEntityDef {
	private static int DEFAULT_PAGE_ITEMS_NUMBER = 10;
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

	/**
	 * get next page credits by account ID
	 * @param accountId account ID
	 * @param page the page number
	 * @param sortBy 排序方式
	 * @return credit list
	 */
	public static List<Credit> findByAccountId(String accountId,int page,String sortBy) {
		MorphiaQuery q = Credit.q();
		q.filter("accountId", accountId);
		
		if (!StringUtils.isEmpty(sortBy)) {
			q = sortBy(q, sortBy);
		}else{
			q = sortBy(q,"-created");
		}
		return paginate(q, page);
	}
	
	/**
	 * 通用排序
	 * @param q
	 * @param sortBy
	 * @return
	 */
	private static MorphiaQuery sortBy(MorphiaQuery q, String sortBy) {
		q.order(sortBy);
		return q;
	}
	
	/**
	 * 通用分页
	 * @param q
	 * @param page
	 * @return
	 */
	private static List<Credit> paginate(MorphiaQuery q, int page) {
		q.offset((page - 1) * DEFAULT_PAGE_ITEMS_NUMBER).limit(
				DEFAULT_PAGE_ITEMS_NUMBER);
		return q.asList();
	}

}
