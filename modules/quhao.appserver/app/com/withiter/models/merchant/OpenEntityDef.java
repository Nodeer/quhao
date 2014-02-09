package com.withiter.models.merchant;

import com.google.code.morphia.annotations.Indexed;
import com.withiter.models.BaseModel;

public class OpenEntityDef extends BaseModel {
	@Indexed
	public String mid;
	@Indexed
	public String accountId;	
}
