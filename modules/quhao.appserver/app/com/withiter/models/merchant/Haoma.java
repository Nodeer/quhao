package com.withiter.models.merchant;

import com.withiter.models.BaseModel;

public class Haoma extends HaomaEntityDef {

	public static Haoma findByMerchantId(String merchantId) {
		
		MorphiaQuery q = Haoma.q();
		q.filter("merchantId", merchantId);
		if(q.first() != null){
			return q.first();
		}else{
			Haoma haoma = new Haoma();
			haoma.merchantId = merchantId;
			haoma.initPaidui();
			return haoma;
		}
	}
	
	private void initPaidui(){
		Paidui p = null;
		for(int i=1; i <= 20; i++){
			p = new Paidui();
			this.haoma.put(i, p);
		}
	}
}
