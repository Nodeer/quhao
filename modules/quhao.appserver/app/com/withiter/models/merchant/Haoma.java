package com.withiter.models.merchant;

import com.withiter.models.account.Reservation;

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
			this.haomaMap.put(i, p);
		}
	}
	
	/**
	 * 拿号（同步方法）
	 * @param accountId	用户id
	 * @param mid	商家id
	 * @param seatNumber 座位数
	 * @return	Reservation
	 */
	public synchronized static Reservation nahao(String accountId, String mid, int seatNumber){
		
		// TODO need to check the java.lang.StackOverflowError issue
		
		Haoma haoma = Haoma.findByMerchantId(mid);
		Paidui paidui = haoma.haomaMap.get(seatNumber);
		int number = paidui.currentWait+1;
		paidui.enable = true;
		paidui.currentWait = number;
		
		haoma.haomaMap.put(seatNumber, paidui);
		haoma.save();
		
		Reservation reservation = new Reservation();
		reservation.accountId = accountId;
		reservation.merchantId = mid;
		reservation.myNumber = number;
		reservation.seatNumber = seatNumber;
		
		return reservation;
	}
}
