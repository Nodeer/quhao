package com.withiter.models.merchant;

import java.util.Date;
import java.util.List;

import play.modules.morphia.Model.MorphiaQuery;

import com.google.code.morphia.annotations.Entity;

@Entity
public class Merchant extends MerchantEntityDef {
	private static int DEFAULT_PAGE_ITEMS_NUMBER = 5;

	public static List<Merchant> findByType(String cateType) {
		MorphiaQuery q = Merchant.q();
		q.filter("cateType", cateType);
		return q.asList();
	}

	
	// TODO add pagenate here
//	public static List<Merchant> allNextPage(int page) {
//		return findAll(page);
//	}
//	
//	public static List<Merchant> findAll(int pageSize, String sortBy) {
//		MorphiaQuery q = Merchant.q().limit(100);
//		q = sortBy(q, sortBy);
//		return paginate(q, pageSize, lastCreated);
//	}
//	
//	private static MorphiaQuery sortBy(MorphiaQuery q, HomePageSortBy sortBy) {
//		if (sortBy == HomePageSortBy.DATE) {
//			q = q.order("-" + "created");
//		}
//		if (sortBy == HomePageSortBy.NAME) {
//			q = q.order("-title");
//		}
//		if (sortBy == HomePageSortBy.SOURCE) {
//			q = q.order("domainName");
//		}
//		return q;
//	}
	
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
}
