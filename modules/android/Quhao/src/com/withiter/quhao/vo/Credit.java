package com.withiter.quhao.vo;


public class Credit {

	/**
	 * 用户帐号ID
	 */
	public String accountId;
	
	/**
	 * 酒店ID
	 */
	public String merchantId;
	
	/**
	 * 酒店名称
	 */
	public String merchantName;
	
	/**
	 * 酒店地址
	 */
	public String merchantAddress;
	
	/**
	 * 关联的预定的Id
	 */
	public String reservationId;
	
	/**
	 * 座位类型
	 */
//	public int seatNumber;
	
	/**
	 * 我的座位号码
	 */
//	public int myNumber;
	
	/**
	 * true代表增加积分， false代表减少积分
	 */
	public boolean cost;
	
	/**
	 * 积分消费状态 finished, getNumber
	 */
	public String status;
	
	/**
	 * 增加日期
	 */
	public String created;
	
	/*
	public Credit(String accountId, String merchantId, String merchantName,String merchantAddress,String reservationId,int seatNumber
			,int myNumber,boolean cost,String status,String created) {
		
		this.accountId = accountId;
		this.merchantId = merchantId;
		this.merchantName = merchantName;
		this.merchantAddress = merchantAddress;
		this.reservationId = reservationId;
		this.seatNumber = seatNumber;
		this.myNumber = myNumber;
		this.cost = cost;
		this.status = status;
		this.created = created;
	}
	 */
	public Credit(String accountId, String merchantId, String merchantName,String merchantAddress,String reservationId,boolean cost,String status,String created) {
		
		this.accountId = accountId;
		this.merchantId = merchantId;
		this.merchantName = merchantName;
		this.merchantAddress = merchantAddress;
		this.reservationId = reservationId;
//		this.seatNumber = seatNumber;
//		this.myNumber = myNumber;
		this.cost = cost;
		this.status = status;
		this.created = created;
	}
}
