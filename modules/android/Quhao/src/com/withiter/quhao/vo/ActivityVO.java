package com.withiter.quhao.vo;

import java.util.Date;

public class ActivityVO {
	public String activityId;
	public String mid;
	public String cityCode;
	public String image;
	public String start;
	public String end;
	public boolean enable;
	
	public ActivityVO(String activityId,String mid,String cityCode,String image,String start,String end,boolean enable) {
		this.activityId = activityId;
		this.mid = mid;
		this.cityCode = cityCode;
		this.image = image;
		this.start = start;
		this.end = end;
		this.enable = enable;
	}
}
