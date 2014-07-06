package com.withiter.models.social;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import play.modules.morphia.Model.MorphiaQuery;

import com.google.code.morphia.annotations.Entity;
import com.withiter.models.merchant.Merchant;

@Entity
public class Share extends ShareEntityDef {

	private static int DEFAULT_PAGE_ITEMS_NUMBER = 10;
	
	public static List<Share> nextPage(int page, String cityCode, long time){
		MorphiaQuery q = Share.q();
		q.filter("cityCode", cityCode);
		q.filter("deleted", false);
		
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date(time);
		q.filter("created >", date);
		q.order("-created");
		return paginate(q, page);
	}
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
