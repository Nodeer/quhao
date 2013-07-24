package com.withiter.models.merchant;

import java.util.HashMap;
import java.util.Map;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Indexed;
import com.withiter.models.BaseModel;

@Entity
public abstract class HaomaEntityDef extends BaseModel {

	@Indexed
	String merchantId;
	Map<Integer, Integer> haoma = new HashMap<Integer, Integer>();
}

