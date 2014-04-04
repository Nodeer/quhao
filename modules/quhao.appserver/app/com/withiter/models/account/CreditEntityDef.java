package com.withiter.models.account;

import com.withiter.common.Constants.CreditStatus;
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
	 * 增加或减少积分
	 */
	public int jifen;
	
	/**
	 * 积分消费状态 finished, getNumber
	 */
	public CreditStatus status;
	
	/**
	 * 逻辑删除标识
	 */
	public boolean available = true;
}
