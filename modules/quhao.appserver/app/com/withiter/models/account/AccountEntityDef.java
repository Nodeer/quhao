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

	public String birthDay = "";
	public String userImage = "";
	public boolean enable = false;
	public Constants.MobileOSType mobileOS;
	public Date lastLogin = new Date();
}
