package com.withiter.models.activity;

import java.util.List;

import play.modules.morphia.Model.MorphiaQuery;

import com.google.code.morphia.annotations.Entity;

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
}
