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
	public String phone = "";					// 手机号码
	@Indexed
	public String email = "";					// 邮箱
	@Indexed
	public String password = "";				// 密码
	@Indexed
	public String nickname = "";				// 昵称
	public String authcode;						// 验证码
	public Date authDate;						// 获取验证码时间
	public int signIn;							// 每日签到总次数
	public boolean isSignIn = false;			// 是否已经签到
	public int jifen = 10;						// 默认10个积分
	public int dianping = 0;					// 点评次数
	public int zhaopian = 0;					// 添加的照片个数
	public String birthDay = "";				// 生日
	public String userImage = "";				// 用户头像
	public boolean enable = false;				// 账号是否激活
	public Constants.MobileOSType mobileOS;		// 手机类型（Android/iOS） 
	public Date lastLogin = new Date();			// 最后一次登陆时间
}
