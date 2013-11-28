package com.withiter.quhao.domain;

import java.io.Serializable;

import com.withiter.quhao.vo.LoginInfo;

public class AccountInfo implements Serializable {

	private static final long serialVersionUID = 9060527069391618394L;

	public String userId;
	public String accountId;
	public String phone;
	
	public String jifen;

	public String email;
	public String password;
	public String nickName;
	public String birthday;
	public String userImage;
	public String enable;
	public String mobileOS;
	public String signIn;
	public String isSignIn;

	public String dianping;
	public String isAuto = "false";
	public String msg;
	public String lastLogin;

	public void build(LoginInfo loginInfo) {
		this.msg = loginInfo.msg;
		this.accountId = loginInfo.accountId;
		this.phone = loginInfo.phone;
		this.email = loginInfo.email;
		this.password = loginInfo.password;
		this.nickName = loginInfo.nickName;
		this.birthday = loginInfo.birthday;
		this.userImage = loginInfo.userImage;
		this.enable = loginInfo.enable;
		this.mobileOS = loginInfo.mobileOS;
		this.lastLogin = loginInfo.lastLogin;
		this.signIn = loginInfo.signIn;
		this.isSignIn = loginInfo.isSignIn;
		this.jifen = loginInfo.jifen;
		this.dianping = loginInfo.dianping;
	}
	
}
