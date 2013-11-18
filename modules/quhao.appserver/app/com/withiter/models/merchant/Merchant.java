package com.withiter.models.merchant;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import org.bson.types.ObjectId;
import org.joda.time.DateTime;

import cn.bran.japid.util.StringUtils;

import com.google.code.morphia.annotations.Entity;
import com.withiter.common.Constants.CateType;
import com.withiter.models.account.Reservation;

@Entity
public class Merchant extends MerchantEntityDef {
	private static int DEFAULT_PAGE_ITEMS_NUMBER = 10;

	/**
	 * Get merchant list by category type.
	 * @param cateType the type of category
	 * @return the list of merchant
	 */
	public static List<Merchant> findByType(String cateType) {
		MorphiaQuery q = Merchant.q();
		q.filter("cateType", cateType);
		return q.asList();
	}
	
	public static Merchant findByMid(String mid){
		System.out.println(mid);
		MorphiaQuery q = Merchant.q();
		q.filter("_id", new ObjectId(mid));
		if(q.asKeyList().size() == 0){
			return null;
		}
		return (Merchant) q.asList().get(0);
	}

	/**
	 * get next page merchants
	 * 
	 * @param cateType the type of category
	 * @param page the page number
	 * @param sortBy 排序方式
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

	/**
	 * Search merchants by key word name
	 * @param name the key word
	 * @return the top 10 merchants
	 */
	public static List<Merchant> findByName(String name) {
		MorphiaQuery q = Merchant.q();
		//首字查询
		//Pattern pattern = Pattern.compile("^" + name + ".*$", Pattern.CASE_INSENSITIVE);
		Pattern pattern = Pattern.compile("^.*" + name + ".*$", Pattern.CASE_INSENSITIVE);
		q.filter("name", pattern).limit(DEFAULT_PAGE_ITEMS_NUMBER);
		return q.asList();
	}
	
	/**
	 * 
	 * @param date joinedDate Of Merchant
	 * @return the newest merchants
	 */
	public static List<Merchant> findByDate(int page,String date,String sortBy) {
		MorphiaQuery q = Merchant.q();
		if(null!=date){
			q.filter("joinedDate >",date);
		}else{
			q.filter("joinedDate >",new Date().toString());
		}
		if (!StringUtils.isEmpty(sortBy)) {
			q = sortBy(q, sortBy);
		}
		return paginate(q, page);

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

	// TODO add comments here
	public static List<Merchant> findbyReservations(
			List<Reservation> reservations) {
		/*
		String whereSql = "where 1=1 ";
		if (null != reservations && !reservations.isEmpty()) {
			whereSql = whereSql + "and _id in (";
		} else {
			return new ArrayList<Merchant>();
		}
		Reservation reservation = null;
		for (int i = 0; i < reservations.size(); i++) {
			reservation = reservations.get(i);
			if (null != reservation.merchantId
					&& !"".equals(reservation.merchantId)) {
				if (i < reservations.size() - 1) {
					whereSql = whereSql + "\'"+ reservation.merchantId + "\',";
					continue;
				}
				whereSql = whereSql + "\'"+ reservation.merchantId + "\'";
				//whereSql = whereSql + reservation.merchantId;
			}
		}
		whereSql = whereSql + ")";
		System.out.println(whereSql);
		MorphiaQuery q = Merchant.q();
		q.where(whereSql);
		return q.asList();
		
		List<Merchant> merchants = new ArrayList<Merchant>();
		
		Reservation reservation = null;
		MorphiaQuery q = Merchant.q();
		for (int i = 0; i < reservations.size(); i++) {
			reservation = reservations.get(i);
			if (null != reservation.merchantId
					&& !"".equals(reservation.merchantId)) {
				q.filter("_id", new ObjectId(reservation.merchantId));
				List<Merchant> temps = q.asList();
				if(temps.size()>0)
				{
					merchants.addAll(temps);
				}
			}
		}
		
		return merchants;
		*/
		if (null != reservations && !reservations.isEmpty()) {
			ArrayList alist=new ArrayList();
			Reservation reservation = null;
			for (int i = 0; i < reservations.size(); i++) {
				reservation = reservations.get(i);
				if (null != reservation.merchantId
						&& !"".equals(reservation.merchantId)) {
					alist.add(new ObjectId(reservation.merchantId));
				}
			}
			MorphiaQuery q = Merchant.q();
			q.filter("_id in ",alist);

			return q.asList();
		} else {
			return new ArrayList<Merchant>();
		}	
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
	private static List<Merchant> paginate(MorphiaQuery q, int page) {
		q.offset((page - 1) * DEFAULT_PAGE_ITEMS_NUMBER).limit(
				DEFAULT_PAGE_ITEMS_NUMBER);
		return q.asList();
	}

	/**
	 * play job for updating merchant evaluate, eg: grade, xingjiabi, kouwei, huanjing, fuwu, renjun
	 */
	public static void updateMerchantEvaluate() {
		
		
	}
}
