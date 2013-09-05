package com.withiter.quhao.vo;

public class ReservationVO {

	public boolean tipKey = false;
	public String tipValue = "";
	
	public String accountId;
	public String merchantId;
	public String seatNumber;
	public String myNumber;
	public String beforeYou;
	public boolean valid;
	public String status;
	
	public ReservationVO()
	{
		
	}
	
	public ReservationVO(String accountId,String merchantId,String seatNumber,String myNumber,String beforeYou,
			boolean valid,boolean tipKey,String tipValue){
		this.accountId = accountId;
		this.merchantId = merchantId;
		this.seatNumber = seatNumber;
		this.myNumber = myNumber;

		this.beforeYou = beforeYou;
		this.valid = valid;
		//this.status = status;
		this.tipKey = tipKey;
		this.tipValue = tipValue;
	}
}
