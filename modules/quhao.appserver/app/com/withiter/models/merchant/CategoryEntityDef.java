package com.withiter.models.merchant;

import com.withiter.common.Constants.CateType;
import com.withiter.models.BaseModel;

public abstract class CategoryEntityDef extends BaseModel {

	public long count = 0;
	public String cateType;
	public boolean enable = true;
	
}
