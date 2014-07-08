package com.withiter.models.merchant;

import java.util.Date;

import com.withiter.common.Constants.YudingStatus;
import com.withiter.models.BaseModel;

public abstract class YudingEntityDef extends BaseModel {
	public String mid;				// 商家id
	public String aid;				// 用户id，可选
	public int renshu;				// 人数
	public Date shijian;			// 时间
	public boolean baojian;			// 有包间时用包间
	public boolean baojianOptional;	// 如果没有包间是否可接受大厅
	public String xing;				// 称呼
	public String mobile;			// 手机号码
	public YudingStatus status;		// 预定状态
}
