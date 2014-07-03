package com.withiter.models.social;

import com.google.code.morphia.annotations.Indexed;
import com.withiter.models.BaseModel;

public class ShareNiceEntityDef extends BaseModel {

	@Indexed
	public String aid;
	@Indexed
	public String sid;
}
