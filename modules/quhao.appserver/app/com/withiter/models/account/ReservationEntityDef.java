package com.withiter.models.account;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.withiter.common.Constants;
import com.withiter.models.BaseModel;
import com.google.code.morphia.annotations.Embedded;
import com.google.code.morphia.annotations.Indexed;

public abstract class ReservationEntityDef extends BaseModel {
	public String accountId;						// Account ID
	public String merchantId;						// Merchant ID
	public int seatNumber;							// 座位类型
	public int myNumber;							// 我的号码
	public boolean isCommented = false;				// 是否评价
	public boolean valid;							// 是否合法
	public Constants.ReservationStatus status;		// 状态
	public boolean available = true;				// 逻辑删除标识
	public long version = 0l;						// 和排队一致的version
}
