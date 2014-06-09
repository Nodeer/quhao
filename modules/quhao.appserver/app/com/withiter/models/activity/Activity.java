package com.withiter.models.activity;

import java.util.Date;
import java.util.List;

import play.modules.morphia.Model.MorphiaQuery;
import play.modules.morphia.Model.MorphiaUpdateOperations;

import com.google.code.morphia.annotations.Entity;
import com.withiter.models.merchant.TopMerchant;

@Entity
public class Activity extends ActivityEntityDef {
	
	/**
	 * 根据城市代码返回对应的活动列表
	 * @param cityCode
	 * @return
	 */
	public static List<Activity> activityByCityCode(String cityCode){
		MorphiaQuery q = Activity.q();
		q.filter("enable", true).filter("cityCode", cityCode);
		return q.asList();
	}
	
	/**
	 * job 每天检查topmerchant是否到期
	 */
	public static void verifyAndupdateActivity() {
		// end time < current time
		MorphiaQuery q = Activity.q();
		q.filter("end <", new Date());
		MorphiaUpdateOperations o = Activity.o();
		o.set("enable", false);
		o.update(q);
		
		// start time < current time < end time
		MorphiaQuery q1 = Activity.q();
		Date now = new Date();
		q1.filter("start <", now).filter("end >", now);
		MorphiaUpdateOperations oo = Activity.o();
		oo.set("enable", true);
		oo.update(q1);
	}
}
