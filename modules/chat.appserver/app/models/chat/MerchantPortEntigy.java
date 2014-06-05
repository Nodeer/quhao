package models.chat;

import models.BaseModel;

import com.google.code.morphia.annotations.Indexed;

public abstract class MerchantPortEntigy extends BaseModel {

	@Indexed
	public String mid;
	@Indexed
	public long port;
}
