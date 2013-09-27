package com.withiter.models.merchant;

import com.withiter.common.Constants.ReservationStatus;
import com.withiter.models.account.Reservation;

public class Haoma extends HaomaEntityDef {

	/**
	 * Find Haoma by merchant id
	 * @param merchantId id of merchant
	 * @return
	 */
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
		
		Haoma haoma = Haoma.findByMerchantId(mid);
		Paidui paidui = haoma.haomaMap.get(seatNumber);
		//paidui.enable = true;
		paidui.maxNumber += 1;
		haoma.save();
		
		Reservation reservation = new Reservation();
		reservation.accountId = accountId;
		reservation.merchantId = mid;
		reservation.myNumber = paidui.maxNumber;
		reservation.seatNumber = seatNumber;
		reservation.status = ReservationStatus.active;
		//reservation.beforeYou = paidui.currentNumber - (paidui.canceled + paidui.expired + paidui.finished);
		reservation.valid = true;
		
		reservation.save();
		
		return reservation;
	}
	
	/**
	 * 更新X人座位的finished, canceled, expired 数量
	 * @param haoma
	 * @param mid
	 * @param myNumber
	 * @param seatNumber
	 * @param status
	 * @return
	 */
	public synchronized static Haoma updateByXmethod(Haoma haoma, String mid, int myNumber, int seatNumber, ReservationStatus status){
		Paidui p = haoma.haomaMap.get(seatNumber);
		switch(status){
			case canceled : 
				if(myNumber>=p.currentNumber)
				{
					p.canceled += 1;
				}
				
				break;
			case finished : 
				if(myNumber==p.currentNumber)
				{
					p.currentNumber += 1;
				}
				
				break;
			case expired : 
				if(myNumber==p.currentNumber)
				{
					p.currentNumber += 1;
				}
				break;
		}
		haoma.save();
		return haoma;
	}
}
