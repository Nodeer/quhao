package com.withiter.models.merchant;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import org.bson.types.ObjectId;
import org.joda.time.DateTime;

import play.modules.morphia.Model.MorphiaQuery;
import play.modules.morphia.Model.NoAutoTimestamp;
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
	@Deprecated
	public static List<Merchant> findByType(String cateType) {
		MorphiaQuery q = Merchant.q();
		q.or(q.criteria("cateType").equal(cateType),q.criteria("cateType1").equal(cateType));
		return q.asList();
	}

	/**
	 * Get merchant list by category type and page number.
	 * @param cateType the type of category
	 * @return the list of merchant
	 */
	public static List<Merchant> findByType(String cateType, int page) {
		MorphiaQuery q = Merchant.q();
		q.or(q.criteria("cateType").equal(cateType),q.criteria("cateType1").equal(cateType));
		paginate(q, page);
		return q.asList();
	}
	
	/**
	 * Get merchant by _id
	 * @param mid
	 * @return
	 */
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
	 * get next page merchants
	 * 
	 * @param cateType the type of category
	 * @param page the page number
	 * @param sortBy 排序方式
	 * @return
	 */
	public static List<Merchant> nextPage(String cateType, int page, String sortBy, String cityCode) {
		MorphiaQuery q = Merchant.q();
		if (!StringUtils.isEmpty(cateType)) {
//			q.filter("cateType", cateType);
			q.or(q.criteria("cateType").equal(cateType),q.criteria("cateType1").equal(cateType));
		} else {
			q.filter("cateType", CateType.benbangcai.toString());
		}
		
		// 先按照enable排序
		q = sortBy(q, "enable");
		
		if (!StringUtils.isEmpty(sortBy)) {
			q = sortBy(q, sortBy);
		}
		
		q.filter("cityCode", cityCode);
		return paginate(q, page);
	}

	/**
	 * Search merchants by key word name
	 * @param name the key word
	 * @return the top 10 merchants
	 */
	public static List<Merchant> findByName(String name) {
		MorphiaQuery q = Merchant.q();
		Pattern pattern = Pattern.compile("^.*" + name + ".*$", Pattern.CASE_INSENSITIVE);
		q.filter("name", pattern).filter("enable",true).limit(DEFAULT_PAGE_ITEMS_NUMBER);
		return q.asList();
	}

	/**
	 * Search merchants by key word name and cityCode
	 * @param name the key word
	 * @return the top 10 merchants
	 */
	public static List<Merchant> findByName(String name, String cityCode) {
		MorphiaQuery q = Merchant.q();
		Pattern pattern = Pattern.compile("^.*" + name + ".*$", Pattern.CASE_INSENSITIVE);
		q.filter("cityCode", cityCode).filter("name", pattern).filter("enable",true).limit(DEFAULT_PAGE_ITEMS_NUMBER);
		return q.asList();
	}
	
	/**
	 * Search merchants by key word name
	 * @param name the key word
	 * @return the merchants
	 */
	public static List<Merchant> searchByName(String name) {
		MorphiaQuery q = Merchant.q();
		Pattern pattern = Pattern.compile("^.*" + name + ".*$", Pattern.CASE_INSENSITIVE);
		q.filter("name", pattern).filter("enable",false);
		return q.asList();
	}
	
	/**
	 * Search merchants by key word name and cityCode
	 * @param name the key word
	 * @return the merchants
	 */
	public static List<Merchant> searchByName(String name, String cityCode) {
		MorphiaQuery q = Merchant.q();
		Pattern pattern = Pattern.compile("^.*" + name + ".*$", Pattern.CASE_INSENSITIVE);
		q.filter("cityCode", cityCode).filter("name", pattern).filter("enable",false);
		return q.asList();
	}
	
	/**
	 * Check merchants by key word name
	 * @param name the key word
	 * @return the merchants
	 */
	public static List<Merchant> checkByName(String name) {
		MorphiaQuery q = Merchant.q();
		Pattern pattern = Pattern.compile("^.*" + name + ".*$", Pattern.CASE_INSENSITIVE);
		q.filter("name", pattern).filter("enable",true);
		return q.asList();
	}
	
	/**
	 * Check merchants by key word name and cityCode
	 * @param name the key word
	 * @return the merchants
	 */
	public static List<Merchant> checkByName(String name, String cityCode) {
		MorphiaQuery q = Merchant.q();
		Pattern pattern = Pattern.compile("^.*" + name + ".*$", Pattern.CASE_INSENSITIVE);
		q.filter("cityCode", cityCode).filter("name", pattern).filter("enable",true);
		return q.asList();
	}
	
	/**
	 * 
	 * @param date joinedDate Of Merchant
	 * @return the newest merchants
	 */
	public static List<Merchant> findByDate(String cateType,String date,String sortBy) {
		MorphiaQuery q = Merchant.q();
		q.filter("cateType", cateType);

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date dateTemp = new Date();
		try {
			dateTemp = sdf.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}	
		if(null != date){
			q.filter("joinedDate >",dateTemp);
		}else{
			q.filter("joinedDate >",dateTemp);
		}
		if (!StringUtils.isEmpty(sortBy)) {
			q = sortBy(q, sortBy);
		}
		return q.asList();
	}
	
	/**
	 * 
	 * @param date joinedDate Of Merchant
	 * @return the newest merchants
	 */
	public static List<Merchant> findByDate(String cateType,String date,String sortBy, String cityCode) {
		MorphiaQuery q = Merchant.q();
		q.filter("cateType", cateType);

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date dateTemp = new Date();
		try {
			dateTemp = sdf.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}	
		if(null != date){
			q.filter("joinedDate >",dateTemp);
		}else{
			q.filter("joinedDate >",dateTemp);
		}
		if (!StringUtils.isEmpty(sortBy)) {
			q = sortBy(q, sortBy);
		}
		
		q.filter("cityCode", cityCode);
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

	/**
	 * 通过Reservation查找Merchant
	 * @param reservations 所有reservation
	 * @return
	 */
	public static List<Merchant> findbyReservations(List<Reservation> reservations) {
		if (null != reservations && !reservations.isEmpty()) {
			ArrayList alist = new ArrayList();
			Reservation reservation = null;
			for (int i = 0; i < reservations.size(); i++) {
				reservation = reservations.get(i);
				if (!StringUtils.isEmpty(reservation.merchantId)) {
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
	 * 通过POIID查询merchant
	 * @param poiId
	 * @return
	 */
	public static Merchant queryMerchantByPoiId(String poiId) {
		System.out.println(poiId);
		MorphiaQuery q = Merchant.q();
		q.filter("poiId", poiId);
		List<Merchant> merchants = q.asList();
		if(merchants.size() == 0){
			return null;
		}
		return merchants.get(0);
	}
	
	/**
	 * update merchant evaluate
	 */
	public void updateEvaluate(){
		MorphiaQuery commentQ = Comment.q();
		commentQ.filter("mid", this.id());
		if(commentQ.count() == 0){
			return;
		}
		this.xingjiabi =  commentQ.average("xingjiabi");
		this.kouwei =  commentQ.average("kouwei");
		this.huanjing =  commentQ.average("huanjing");
		this.fuwu =  commentQ.average("fuwu");
		this.grade =  commentQ.average("grade");
		this.averageCost =  commentQ.average("averageCost");
		
		this.save();
	}
	
	/**
	 * 不用排队商家
	 * @return
	 */
	public static List<ObjectId> noQueueMerchants(){
		MorphiaQuery q = Haoma.q();
		q.filter("noNeedPaidui", true);
		q.retrievedFields(true, "merchantId");
		
		List<Haoma> hList = q.asList();
		List<ObjectId> mList = new ArrayList<ObjectId>(); 
		
		if(hList != null && !hList.isEmpty()){
			for(Haoma h : hList){
				mList.add(new ObjectId(h.merchantId));
			}
		}
		return mList;
	}
}
