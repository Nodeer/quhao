package com.withiter.models.merchant;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import org.bson.types.ObjectId;
import org.joda.time.DateTime;

import play.Logger;
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
		MorphiaQuery q = Merchant.q();
		q.filter("_id", new ObjectId(mid));
		if(q.asKeyList().size() == 0){
			return null;
		}
		return (Merchant) q.asList().get(0);
	}

	/**
	 * 取号排队按钮，下一页
	 * 
	 * @param page the page number
	 * @param cityCode the city code
	 * @return
	 */
	public static List<Merchant> nextPage(int page, String cityCode) {
		MorphiaQuery q = Merchant.q();
		q.filter("cityCode", cityCode);
		q = sortBy(q, "-enable");
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
			q.or(q.criteria("cateType").equal(cateType),q.criteria("cateType1").equal(cateType));
		}
		// 先按照enable排序
		if (!StringUtils.isEmpty(sortBy)) {
			q = sortBy(q, "-enable,"+sortBy);
		}else{
			q = sortBy(q, "-enable");
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
		q.filter("name", pattern).limit(DEFAULT_PAGE_ITEMS_NUMBER);
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
		q.filter("cityCode", cityCode).filter("name", pattern).limit(DEFAULT_PAGE_ITEMS_NUMBER);
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
		q.filter("cityCode", cityCode).filter("enable",false).filter("name", pattern);
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
		Logger.debug("start to update evaluate datas");
		MorphiaQuery commentQ = Comment.q();
		commentQ.filter("mid", this.id());
		if(commentQ.count() == 0){
			return;
		}
		
		long count1 = commentQ.count();
		long xingjiabiSum = commentQ.sum("xingjiabi");
		long kouweiSum = commentQ.sum("kouwei");
		long huanjingSum = commentQ.sum("huanjing");
		long fuwuSum = commentQ.sum("fuwu");
		long gradeSum = commentQ.sum("grade");
		long averageCostSum = commentQ.sum("averageCost");
		Logger.info(((float)xingjiabiSum/(float)count1)+"xingjiabiSum/count1");
		Logger.info(((float)kouweiSum/(float)count1)+"kouweiSum/count1");
		Logger.info(((float)huanjingSum/(float)count1)+"huanjingSum/count1");
		Logger.info(((float)fuwuSum/(float)count1)+"fuwuSum/count1");
		Logger.info(((float)gradeSum/(float)count1)+"gradeSum/count1");
		Logger.info(((float)averageCostSum/(float)count1)+"averageCostSum/count1");
		
		
		this.xingjiabi =  commentQ.average("xingjiabi");
		this.kouwei =  commentQ.average("kouwei");
		this.huanjing =  commentQ.average("huanjing");
		this.fuwu =  commentQ.average("fuwu");
		this.grade =  commentQ.average("grade");
		this.averageCost =  commentQ.average("averageCost");
		
		// update mark count
		MorphiaQuery q = Attention.q();
		q.filter("mid", this.id()).filter("flag", true);
		long count = q.count();
		if(q.count() > 0){
			this.markedCount = (int)count;
		}
		
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
	
	/**
	 * 改变商家状态（开放取号，关闭取号）
	 * @param mid	商家id
	 * @param online	在线状态（true->开放取号，false->关闭取号）
	 * @return 是否更改成功
	 */
	public static boolean changeStatus(String mid, boolean online){
		Merchant m = Merchant.findByMid(mid);
		if(m == null){
			return false;
		}
		
		m.online = online;
		m.save();
		return true;
	}

	/**
	 * 更新优惠状态
	 */
	public void updateYouhuiInfo() {
		MorphiaQuery q = Youhui.q();
		q.filter("mid", this.id()).filter("enable", true);
		if(q.count() > 0){
			this.youhui = true;
		} else {
			this.youhui = false;
		}
		this.save();
	}
	
	public static Merchant findOneTuijian(String cityCode){
		MorphiaQuery q = Merchant.q();
		// 开通取号啦排队服务商家 & 在线商家
		q.filter("enable", true).filter("online", true).filter("cityCode", cityCode);
		
		// 在营业时间内的商家
//		Calendar now = Calendar.getInstance();
//		q.filter("openTime <", now.get(Calendar.HOUR_OF_DAY)+":00");
//		q.filter("closeTime >", now.get(Calendar.HOUR_OF_DAY)+":00");
		long count = q.count();
		Logger.debug("All tuijian merchant's size : %d", count);
		int offsets = (int) (Math.random() * count);
		Logger.debug("随机获取的offset: %d", offsets);
		q.offset(offsets);
		return q.first();
	}
}
