package com.withiter.quhao.domain;

import java.io.Serializable;

import com.withiter.quhao.vo.LoginInfo;

public class AccountInfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 9060527069391618394L;
	public String msg;
	public String userId;
	public String phone;
	public String email;
	public String password;
	public String nickName;
	public String birthday;
	public String userImage;
	public String enable;
	public String mobileOS;
	public String lastLogin;

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public void setUserImage(String userImage) {
		this.userImage = userImage;
	}

	public void setEnable(String enable) {
		this.enable = enable;
	}

	public void setMobileOS(String mobileOS) {
		this.mobileOS = mobileOS;
	}

	public void setLastLogin(String lastLogin) {
		this.lastLogin = lastLogin;
	}

	public void build(LoginInfo loginInfo) {
		this.msg = loginInfo.msg;
		this.phone = loginInfo.phone;
		this.email = loginInfo.email;
		this.password = loginInfo.password;
		this.nickName = loginInfo.nickName;
		this.birthday = loginInfo.birthday;
		this.userImage = loginInfo.userImage;
		this.enable = loginInfo.enable;
		this.mobileOS = loginInfo.mobileOS;
		this.lastLogin = loginInfo.lastLogin;
	}

}
