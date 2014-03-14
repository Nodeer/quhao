package com.withiter.models.merchant;


import com.google.code.morphia.annotations.Indexed;
import com.withiter.models.BaseModel;

/**
 * 
 * 当用户查看附近商家的时候，如果点击的商家在quhao服务器中没有记录，并且在这个对象对应的表中也没有记录， 将会增加一个该对象，并且存储在数据库。
 * 如果点击的商家在取号服务器中没有记录，但是在这个对象对应的表中有记录，将会查询出该对象，并且增加count次数。
 * 
 * @author Jazze
 *
 */
public class GaodeToMerchantEntityDef extends BaseModel {
	
	/**
	 * 高德对应的poi ID
	 */
	@Indexed
	public String poiId = "";
	
	/**
	 * 状态 true：已经处理， false 未处理
	 */
	public String status;
	
	/**
	 * 相同用户点击次数
	 */
	public int count;
}
