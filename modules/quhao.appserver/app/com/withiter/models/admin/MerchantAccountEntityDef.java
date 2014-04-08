package com.withiter.models.admin;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.withiter.common.Constants;
import com.withiter.models.BaseModel;
import com.google.code.morphia.annotations.Embedded;
import com.google.code.morphia.annotations.Indexed;

public abstract class MerchantAccountEntityDef extends BaseModel {
	@Indexed
	public String email = "";
	@Indexed
	public String password = "";
	public boolean enable = false;
	public Date lastLogin = new Date();
}
