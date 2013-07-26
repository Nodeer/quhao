package com.withiter.models.merchant;

import java.util.HashMap;
import java.util.Map;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Indexed;
import com.withiter.models.BaseModel;

@Entity
public abstract class HaomaEntityDef extends BaseModel {

	@Indexed
	public String merchantId;
	public Map<Integer, Paidui> haomaMap = new HashMap<Integer, Paidui>();
	
	public class Paidui{
		public int currentWait = 0;
		public boolean enable = false;
	}
}

