package com.withiter.quhao.domain;

import java.io.Serializable;

import com.withiter.quhao.util.StringUtils;
import com.withiter.quhao.vo.LoginInfo;

public class AccountInfo implements Serializable {

	public String getSignIn() {
		return signIn;
	}

	public void setSignIn(String signIn) {
		this.signIn = signIn;
	}

	public String getIsSignIn() {
		return isSignIn;
	}

	public void setIsSignIn(String isSignIn) {
		this.isSignIn = isSignIn;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 9060527069391618394L;
	
	public String userId;
	public String phone;
	
	public String email;
	public String password;
	public String nickName;
	public String birthday;
	public String userImage;
	public String enable;
	public String mobileOS;
	public String signIn;
	public String isSignIn;
	
	public String isAuto = "false";
	public String msg;
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
	

	public String getIsAuto() {
		return isAuto;
	}

	public void setIsAuto(String isAuto) {
		if(StringUtils.isNotNull(isAuto)){
			this.isAuto = isAuto;
		}else{
			this.isAuto = "false";
		}
	}

	public String getUserId() {
		return userId;
	}

	public String getPhone() {
		return phone;
	}

	public String getEmail() {
		return email;
	}

	public String getPassword() {
		return password;
	}

	public String getNickName() {
		return nickName;
	}

	public String getBirthday() {
		return birthday;
	}

	public String getUserImage() {
		return userImage;
	}

	public String getEnable() {
		return enable;
	}

	public String getMobileOS() {
		return mobileOS;
	}

	public String getMsg() {
		return msg;
	}

	public String getLastLogin() {
		return lastLogin;
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
		this.signIn = loginInfo.signIn;
		this.isSignIn = loginInfo.isSignIn;
	}

}
