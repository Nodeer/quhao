package com.withiter.models.social;

import java.util.List;

import play.modules.morphia.Model.MorphiaQuery;

import com.google.code.morphia.annotations.Entity;
import com.withiter.models.merchant.Merchant;

@Entity
public class Share extends ShareEntityDef {

	private static int DEFAULT_PAGE_ITEMS_NUMBER = 10;
	
	public static List<Share> nextPage(int page, String cityCode){
		MorphiaQuery q = Share.q();
		q.filter("cityCode", cityCode);
		q.filter("deleted", false);
		q.order("-created");
		return paginate(q, page);
	}
	
	/**
	 * 通用分页
	 * @param q
	 * @param page
	 * @return
	 */
	private static List<Share> paginate(MorphiaQuery q, int page) {
		q.offset((page - 1) * DEFAULT_PAGE_ITEMS_NUMBER).limit(
				DEFAULT_PAGE_ITEMS_NUMBER);
		return q.asList();
	}
}
