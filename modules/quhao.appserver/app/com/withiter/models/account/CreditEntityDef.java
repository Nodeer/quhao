package com.withiter.models.account;

import com.withiter.models.BaseModel;

public class CreditEntityDef extends BaseModel{

	/**
	 * 用户帐号ID
	 */
	public String accountId;
	
	/**
	 * 酒店ID
	 */
	public String merchantId;
	
	/**
	 * 关联的预定的Id
	 */
	public String reservationId;
	
	/**
	 * true代表增加积分， false代表减少积分
	 */
	public boolean cost;
	
	/**
	 * 积分消费状态 finished, getNumber
	 */
	public String status;
	
}
