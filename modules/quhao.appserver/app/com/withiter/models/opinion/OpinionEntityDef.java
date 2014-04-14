package com.withiter.models.opinion;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.withiter.common.Constants;
import com.withiter.models.BaseModel;
import com.google.code.morphia.annotations.Embedded;
import com.google.code.morphia.annotations.Indexed;

public abstract class OpinionEntityDef extends BaseModel {
	@Indexed
	public String contact = "";

	public String opinion = "";
	
	public boolean handle = false;
}
