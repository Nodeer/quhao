package com.withiter.quhao.vo;


public class SignupVO {
	public String errorKey;
	public String errorText;
	
	public SignupVO(String errorKey,String errorText)
	{
		this.errorKey = errorKey;
		this.errorText = errorText;
	}
}
