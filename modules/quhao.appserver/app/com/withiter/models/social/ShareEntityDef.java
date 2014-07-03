package com.withiter.models.social;

import java.util.List;

import com.withiter.models.BaseModel;

public abstract class ShareEntityDef extends BaseModel{
	public String content;
	public String image;
	public String[] images;
	public String aid;
	public String x;
	public String y;
	public String cityCode;
	
	public String userImage;								// 分享的用户头像
	public String nickName;									// 分享的用户名
	public long up;											// 赞的次数
	
	// 坐标
	public double[] loc = new double[2];					// 坐标[x,y],用于mongodb的距离查询
	public String address;
	public boolean deleted = false;
}
