package com.withiter.quhao.vo;

public class Paidui {
	public Integer currentNumber = 0;
	public Integer canceled = 0;
	public Integer expired = 0;
	public Integer finished = 0;
	public boolean enable = false;
	
	public Paidui(Integer currentNumber,Integer canceled,Integer expired,Integer finished,boolean enable)
	{
		this.currentNumber = currentNumber;
		this.canceled = canceled;
		this.expired = expired;
		this.finished = finished;
		this.enable = enable;
	}
}
