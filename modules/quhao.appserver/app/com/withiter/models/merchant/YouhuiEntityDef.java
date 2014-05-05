package com.withiter.models.merchant;

import com.google.code.morphia.annotations.Indexed;
import com.withiter.models.BaseModel;

public abstract class YouhuiEntityDef extends BaseModel{
	@Indexed
	public String mid = "";
	@Indexed
	public boolean enable = false;
	public String title = "";
	public String content = "";
}
