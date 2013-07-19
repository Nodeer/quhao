package com.withiter.models.merchant;

import java.util.List;

import com.google.code.morphia.annotations.Entity;

@Entity
public class Merchant extends MerchantEntityDef {

	public static List<Merchant> findByType(String cateType) {
		MorphiaQuery q = Merchant.q();
		q.filter("cateType", cateType);
		return q.asList();
	}

	@Override
	public String toString() {
		return "[this.name:" + this.name + "],[this.address：" + this.address
				+ "],[this.telephone：" + this.telephone[0]
				+ "],[this.averageCost：" + this.averageCost
				+ "],[this.openTime：" + this.openTime + "],[this.closeTime："
				+ this.closeTime + "],[this.description：" + this.description
				+ "],[this.fuwu：" + this.fuwu + "],[this.huanjing："
				+ this.huanjing + "],[this.kouwei：" + this.kouwei
				+ "],[this.xingjiabi：" + this.xingjiabi + "],[this.grade："
				+ this.grade + "],[this.markedCount：" + this.markedCount
				+ "],[this.nickName：" + this.nickName + "],[this.cateType："
				+ this.cateType + "],[this.enable：" + this.enable
				+ "],[this.joinedDate：" + this.joinedDate + "],[this.tags："
				+ this.tags + "],[this.teses：" + this.teses + "]";
	}
}
