package com.withiter.models.merchant;

import com.google.code.morphia.annotations.Entity;

import play.modules.morphia.Model.MorphiaQuery;
@Entity
public class GaodeToMerchant extends GaodeToMerchantEntityDef {

	/**
	 * 通过用户ID和POI ID查询
	 * 
	 * @param aid 用户id
	 * @param mid of 商家id
	 * @return 希望的用户数
	 */
	public static GaodeToMerchant queryByPoiId(String poiId) {
		MorphiaQuery q = GaodeToMerchant.q();
		q.filter("poiId", poiId);
		if(null != q.asList() && !q.asList().isEmpty())
		{
			return (GaodeToMerchant) q.asList().get(0);
		}
		else
		{
			return null;
		}
	}
}
