package com.withiter.models.merchant;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import cn.bran.japid.util.StringUtils;

import com.google.code.morphia.annotations.Entity;
import com.withiter.common.Constants.CateType;
import com.withiter.common.Constants.SortBy;
import com.withiter.models.account.Reservation;

@Entity
public class Merchant extends MerchantEntityDef {
	private static int DEFAULT_PAGE_ITEMS_NUMBER = 10;

	public static List<Merchant> findByType(String cateType) {
		MorphiaQuery q = Merchant.q();
		q.filter("cateType", cateType);
		return q.asList();
	}

	/**
	 * get next page merchants
	 * 
	 * @param cateType
	 * @param page
	 * @param sortBy
	 * @return
	 */
	public static List<Merchant> nextPage(String cateType, int page,
			String sortBy) {
		MorphiaQuery q = Merchant.q();
		if (!StringUtils.isEmpty(cateType)) {
			q.filter("cateType", cateType);
		} else {
			q.filter("cateType", CateType.benbangcai.toString());
		}
		if (!StringUtils.isEmpty(sortBy)) {
			q = sortBy(q, sortBy);
		}
		return paginate(q, page);
	}

	private static MorphiaQuery sortBy(MorphiaQuery q, String sortBy) {
		q.order(sortBy);
		return q;
	}

	private static List<Merchant> paginate(MorphiaQuery q, int page) {
		q.offset((page - 1) * DEFAULT_PAGE_ITEMS_NUMBER).limit(
				DEFAULT_PAGE_ITEMS_NUMBER);
		return q.asList();
	}

	public static List<Merchant> findByName(String name) {
		MorphiaQuery q = Merchant.q();
		//首字查询
		//Pattern pattern = Pattern.compile("^" + name + ".*$", Pattern.CASE_INSENSITIVE);
		Pattern pattern = Pattern.compile("^.*" + name + ".*$", Pattern.CASE_INSENSITIVE);
		q.filter("name", pattern);
		return q.asList();
	}
	
	@Override
	public String toString() {
		String telStr = "";
		if (this.telephone != null) {
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < this.telephone.length; i++) {
				sb.append(telephone[i]);
			}
			telStr = sb.toString();
		}
		return "[this.name:" + this.name + "],[this.address:" + this.address
				+ "],[this.telephone:" + telStr + "],[this.averageCost:"
				+ this.averageCost + "],[this.openTime:" + this.openTime
				+ "],[this.closeTime:" + this.closeTime
				+ "],[this.description:" + this.description + "],[this.fuwu:"
				+ this.fuwu + "],[this.huanjing:" + this.huanjing
				+ "],[this.kouwei:" + this.kouwei + "],[this.xingjiabi:"
				+ this.xingjiabi + "],[this.grade:" + this.grade
				+ "],[this.markedCount:" + this.markedCount
				+ "],[this.nickName:" + this.nickName + "],[this.cateType:"
				+ this.cateType + "],[this.enable:" + this.enable
				+ "],[this.joinedDate:" + this.joinedDate + "],[this.tags:"
				+ this.tags + "],[this.teses:" + this.teses + "]";
	}

	public static List<Merchant> findbyReservations(
			List<Reservation> reservations) {
		String whereSql = "";
		if (null != reservations && !reservations.isEmpty()) {
			whereSql = "id in (";
		} else {
			return new ArrayList<Merchant>();
		}
		Reservation reservation = null;
		for (int i = 0; i < reservations.size(); i++) {
			reservation = reservations.get(i);
			if (null != reservation.merchantId
					&& !"".equals(reservation.merchantId)) {
				if (i < reservations.size() - 1) {
					whereSql = whereSql + reservation.merchantId + ",";
					continue;
				}
				whereSql = whereSql + reservation.merchantId;
			}
		}
		whereSql = whereSql + ")";
		MorphiaQuery q = Merchant.q();
		q.where(whereSql);
		return q.asList();
	}
}
