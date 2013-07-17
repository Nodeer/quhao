package com.withiter.models.merchant;

import com.withiter.common.Constants.CateType;
import com.withiter.models.BaseModel;

public abstract class CategoryEntityDef extends BaseModel {

	public CateType cateType;
	public int merchantCount = 0;
}
