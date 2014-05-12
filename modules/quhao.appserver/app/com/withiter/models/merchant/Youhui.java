package com.withiter.models.merchant;

import java.util.List;
import java.util.Random;

import com.google.code.morphia.annotations.Entity;

@Entity
public class Youhui extends YouhuiEntityDef {

	/**
	 * 获取商家所有可用的优惠信息
	 * 
	 * @param mid
	 * @return
	 */
	public static List<Youhui> getAllEnabledYouhui(String mid) {
		MorphiaQuery q = Youhui.q();
		q.filter("mid", mid).filter("enable", true);
		return q.asList();
	}

	/**
	 * @获取随机优惠
	 * @param mid
	 * @return
	 */
	@Deprecated
	public static Youhui getRandomEnabledYouhui(String mid) {
		List<Youhui> list = getAllEnabledYouhui(mid);
		if(list == null || list.size() == 0){
			return null;
		}
		Random rd = new Random();
		int x = 0;
		int y = list.size();
		int n = y - x;
		int random = rd.nextInt(n) + x;
		return list.get(random);
	}
}
