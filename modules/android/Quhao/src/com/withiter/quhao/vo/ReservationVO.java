package com.withiter.quhao.vo;

public class ReservationVO {

	public boolean tipKey = false;
	public String tipValue = "";
	
	public String accountId;
	public String merchantId;
	public String seatNumber;
	public String myNumber;
	public String beforeYou;
	public String currentNumber;
	public boolean valid;
	public String status;
	
	public String merchantName;
	
	public String merchantAddress;
	
	public ReservationVO()
	{
		
	}
	
	public ReservationVO(String accountId,String merchantId,String seatNumber,String myNumber,String beforeYou,
			String currentNumber,boolean valid,boolean tipKey,String tipValue,String merchantName,String merchantAddress){
		this.accountId = accountId;
		this.merchantId = merchantId;
		this.seatNumber = seatNumber;
		this.myNumber = myNumber;

		this.beforeYou = beforeYou;
		this.currentNumber = currentNumber;
		this.valid = valid;
		//this.status = status;
		this.tipKey = tipKey;
		this.tipValue = tipValue;
		this.merchantName = merchantName;
		this.merchantAddress = merchantAddress;
	}
}
