package com.withiter.models.chat;

import com.google.code.morphia.annotations.Indexed;
import com.withiter.models.BaseModel;

public abstract class MerchantPortEntigy extends BaseModel {

	@Indexed
	public String mid;
	@Indexed
	public long port;
	@Indexed
	public int socketNumber;
}
