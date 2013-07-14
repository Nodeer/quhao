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
	public String nickname = "";

	@Indexed
	public String email = "";

	@Indexed
	public String password = "";

	public String location = "";

	public String firstName = "";

	public String birthDay = "";
	public String role = "";
	public String lastName = "";

	public String facebookId = "";

	// @Transient
	public String facebookAccessToken = "";

	public String phoneNumber = "";

	@Indexed
	public String username = "";

	// public File userImage = null;

	public boolean enable = false;

	public Constants.MobileOSType mobileOS;

	public boolean isSuper = false; // TXC
	// 2013-5-3

	public boolean isInactiveBeta = false;;

	public Date lastLogin = new Date();

	public String isNotShowWelcome = "false";

	public Map<String, List<String>> userFavorites = new HashMap<String, List<String>>();// 2013-6-24

	public boolean isFinishedOnboarding = false;// 2013-6-24

	public String state = "";

	public String city = "";

	public String highSchool = "";

	public String graduationYear = "";

	public String gender = "";

	public String lng = "";

	public String lat = "";

}
