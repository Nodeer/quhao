package com.withiter.models.merchant;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import play.modules.morphia.Model.MorphiaQuery;
import play.modules.morphia.Model.MorphiaUpdateOperations;
import cn.bran.japid.util.StringUtils;

import com.google.code.morphia.annotations.Entity;
import com.withiter.common.Constants.CateType;
import com.withiter.models.account.Account;

@Entity
public class TopMerchant extends TopMerchantEntityDef {
	private static int DEFAULT_PAGE_ITEMS_NUMBER = 10;

	public static List<TopMerchant> findByType(String cateType) {
		MorphiaQuery q = TopMerchant.q();
		q.filter("cateType", cateType);
		return q.asList();
	}
	
	public static List<TopMerchant> topX(int x){
		MorphiaQuery q = TopMerchant.q();
		q.filter("enable", true).limit(x);
		return q.asList();
	}
	
	public static List<TopMerchant> topX(int x, String cityCode){
		MorphiaQuery q = TopMerchant.q();
		q.filter("cityCode", cityCode).filter("enable", true).limit(x);
		return q.asList();
	}

	/**
	 * get next page merchants
	 * @param cateType
	 * @param page
	 * @param sortBy
	 * @return
	 */
	public static List<TopMerchant> nextPage(String cateType, int page, String sortBy){
		MorphiaQuery q = TopMerchant.q();
		if(!StringUtils.isEmpty(cateType)){
			q.filter("cateType", cateType);
		}else{
			q.filter("cateType", CateType.benbangcai.toString());
		}
		if(!StringUtils.isEmpty(sortBy)){
			q = sortBy(q, sortBy);
		}
		return paginate(q, page);
	}
	
	private static MorphiaQuery sortBy(MorphiaQuery q, String sortBy) {
		q.order(sortBy);
		return q;
	}
	
	private static List<TopMerchant> paginate(MorphiaQuery q, int page){
		q.offset((page - 1) * DEFAULT_PAGE_ITEMS_NUMBER).limit(DEFAULT_PAGE_ITEMS_NUMBER);
		return q.asList();
	}
	
	@Override
	public String toString() {
		String telStr = "";
		if(this.telephone != null){
			StringBuffer sb = new StringBuffer();
			for(int i = 0; i < this.telephone.length; i++){
			 sb. append(telephone[i]);
			}
			telStr = sb.toString();
		}
		return "[this.name:" + this.name + "],[this.address:" + this.address
				+ "],[this.telephone:" + telStr
				+ "],[this.averageCost:" + this.averageCost
				+ "],[this.openTime:" + this.openTime + "],[this.closeTime:"
				+ this.closeTime + "],[this.description:" + this.description
				+ "],[this.fuwu:" + this.fuwu + "],[this.huanjing:"
				+ this.huanjing + "],[this.kouwei:" + this.kouwei
				+ "],[this.xingjiabi:" + this.xingjiabi + "],[this.grade:"
				+ this.grade + "],[this.markedCount:" + this.markedCount
				+ "],[this.nickName:" + this.nickName + "],[this.cateType:"
				+ this.cateType + "],[this.enable:" + this.enable
				+ "],[this.joinedDate:" + this.joinedDate + "],[this.tags:"
				+ this.tags + "],[this.teses:" + this.teses + "]";
	}
	
	public static void updateTopMerchantForTest(){
		MorphiaQuery q = TopMerchant.q();
		if(q.countAll() < 6){
			MorphiaQuery mq = Merchant.q();
			mq.criteria("merchantImage").notEqual("");
			List<Merchant> mList = mq.limit(6).asList();
			for(Merchant m : mList){
				if(q.filter("mid", m.id()).count() <= 0){
					TopMerchant tm = TopMerchant.build(m);
					tm.save();
				}
			}
		}
	}
	
	public static TopMerchant build(Merchant m){
		TopMerchant tm = new TopMerchant();
		if(m == null){
			return null;
		}
		
		tm.mid = m.id();
		tm.address = m.address;
		tm.averageCost = m.averageCost;
		tm.cateType = m.cateType;
		tm.openTime = m.openTime;
		tm.closeTime = m.closeTime;
		tm.description = m.description;
		
		tm.enable = m.enable;
		tm.fuwu = m.fuwu;
		tm.grade = m.grade;
		tm.huanjing = m.huanjing;
		tm.joinedDate = m.joinedDate;
		tm.kouwei = m.kouwei;
		tm.markedCount = m.markedCount;
		tm.name = m.name;
		tm.nickName = m.nickName;
		tm.tags = m.tags;
		tm.telephone = m.telephone;
		tm.teses = m.teses;
		tm.xingjiabi = m.xingjiabi;
		
		tm.merchantImage = m.merchantImage;
		tm.merchantImageSet = m.merchantImageSet;
		
		tm.cityCode = m.cityCode;
		
		return tm;
	}
	
	public String formatStartDate(){
		SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd");
		String formattedStart = sdf.format(this.start);
		return formattedStart;
	}
	
	public String formatEndDate(){
		SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd");
		String formattedEnd = sdf.format(this.end);
		return formattedEnd;
	}

	/**
	 * job 每天检查topmerchant是否到期
	 */
	public static void verifyAndupdateTops() {
		// end time < current time
		MorphiaQuery q = TopMerchant.q();
		q.filter("end <", new Date());
		MorphiaUpdateOperations o = TopMerchant.o();
		o.set("enable", false);
		o.update(q);
		
		// start time < current time < end time
		MorphiaQuery q1 = TopMerchant.q();
		Date now = new Date();
		q1.filter("start <", now).filter("end >", now);
		MorphiaUpdateOperations oo = TopMerchant.o();
		oo.set("enable", true);
		oo.update(q);
	}
}

