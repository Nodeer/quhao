package com.withiter.quhao.vo;


public class LoginInfo {
	public String msg;
	public String phone;
	public String jifen;
	public String email;
	public String password;
	public String nickName;
	public String birthday;
	public String userImage;
	public String enable;
	public String mobileOS;
	public String lastLogin;

	public String birthDay = "";
	
	public String signIn;
	public String isSignIn;
	public String dianping;
	public String zhaopian;
	
	public LoginInfo(String msg, String phone, String jifen, String email, String password,
			String nickName, String birthday, String userImage, String enable,
			String mobileOS, String lastLogin,String signIn,String isSignIn,String dianping,String zhaopian) {
		this.msg = msg;
		this.phone = phone;
		this.jifen = jifen;
		this.email = email;
		this.password = password;
		this.nickName = nickName;
		this.birthday = birthday;
		this.userImage = userImage;
		this.enable = enable;
		this.mobileOS = mobileOS;
		this.lastLogin = lastLogin;
		this.signIn = signIn;
		this.isSignIn = isSignIn;
		this.dianping = dianping;
		this.zhaopian = zhaopian;
	}

	public LoginInfo() {
		// TODO Auto-generated constructor stub
	}
}
