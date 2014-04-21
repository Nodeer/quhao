package com.withiter.models.merchant;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.code.morphia.annotations.Indexed;
import com.google.code.morphia.annotations.Reference;
import com.withiter.models.BaseModel;

/**
 * 关注Model
 * @author user
 *
 */
public class AttentionEntityDef extends BaseModel {
	@Indexed
	public String mid;
	@Indexed
	public String accountId;
	public boolean flag = false;	// true 表示关注， false表示不关注
	
}
