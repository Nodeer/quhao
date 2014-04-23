package com.withiter.models.account;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.withiter.common.Constants;
import com.withiter.models.BaseModel;
import com.google.code.morphia.annotations.Embedded;
import com.google.code.morphia.annotations.Indexed;

public abstract class AccountEntityDef extends BaseModel {
	@Indexed
	public String phone = "";
	@Indexed
	public String email = "";
	@Indexed
	public String password = "";
	@Indexed
	public String nickname = "";
	/**
	 * 验证码
	 */
	public String authcode;
	/**
	 * 获取验证码的时间
	 */
	public Date authDate;
	public int signIn;
	
	public boolean isSignIn = false;

	public int jifen = 10;						// 默认10个积分
	
	public int dianping = 0;
	public int zhaopian = 0;
	
	public String birthDay = "";
	public String userImage = "";
	public boolean enable = false;
	public Constants.MobileOSType mobileOS;
	public Date lastLogin = new Date();
}
