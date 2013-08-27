package com.withiter.quhao.vo;

public class LoginInfo {
	public String phone;
	public String email;
	public String password;
	public String nickName;
	public String birthday;
	public String userImage;
	public String enable;
	public String mobileOS;
	public String lastLogin;

	public LoginInfo(String phone, String email, String password,
			String nickName, String birthday, String userImage, String enable,
			String mobileOS, String lastLogin) {
		this.phone = phone;
		this.email = email;
		this.password = password;
		this.nickName = nickName;
		this.birthday = birthday;
		this.userImage = userImage;
		this.enable = enable;
		this.mobileOS = mobileOS;
		this.lastLogin = lastLogin;
	}

	public LoginInfo() {
		// TODO Auto-generated constructor stub
	}
}
