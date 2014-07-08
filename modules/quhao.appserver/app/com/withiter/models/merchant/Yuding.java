package com.withiter.models.merchant;

import java.util.List;

import cn.bran.japid.util.StringUtils;

import com.google.code.morphia.annotations.Entity;
import com.withiter.common.Constants.YudingStatus;

@Entity
public class Yuding extends YudingEntityDef {

	/**
	 * 查看所有没有处理的预定
	 * @param mid	商家id
	 * @return
	 */
	public static List<Yuding> getAllNotHandledYuding(String mid) {
		MorphiaQuery q = Yuding.q();
		q.filter("mid", mid);
		q.or(q.criteria("status").equal(YudingStatus.created), q.criteria("status").equal(YudingStatus.confirmed));
		return q.asList();
	}

	/**
	 * 查找我的预定
	 * @param mid	商家id
	 * @param aid	用户id
	 * @return
	 */
	public static Yuding findByMidAndAid(String mid, String aid, String mobile) {
		MorphiaQuery q = Yuding.q();
		q.filter("mid", mid);
		if(!StringUtils.isEmpty(aid)){
			q.filter("aid", aid);
		} else {
			q.filter("mobile", mobile);
		}
		q.or(q.criteria("status").equal(YudingStatus.created), q.criteria("status").equal(YudingStatus.confirmed));
		return q.first();
	}
}
