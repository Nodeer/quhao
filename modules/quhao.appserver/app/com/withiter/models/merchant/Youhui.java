package com.withiter.models.merchant;

import java.util.List;

import com.google.code.morphia.annotations.Entity;

@Entity
public class Youhui extends YouhuiEntityDef {

	/**
	 * 获取商家所有可用的优惠信息
	 * @param mid
	 * @return
	 */
	public static List<Youhui> getAllEnabledYouhui(String mid) {
		MorphiaQuery q = Youhui.q();
		q.filter("mid", mid).filter("enable", true);
		return q.asList();
	}

}
