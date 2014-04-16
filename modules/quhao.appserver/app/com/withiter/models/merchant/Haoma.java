package com.withiter.models.merchant;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.bran.japid.util.StringUtils;

import com.withiter.common.Constants.ReservationStatus;
import com.withiter.models.account.Reservation;

/**
 * Haoma contains whole paidui information
 * 
 * @author Cross
 * 
 */
public class Haoma extends HaomaEntityDef {
	
	private static Logger logger = LoggerFactory.getLogger(Haoma.class);

	/**
	 * Find Haoma by merchant id
	 * 
	 * @param merchantId
	 *            id of merchant
	 * @return
	 */
	public static Haoma findByMerchantId(String merchantId) {

		MorphiaQuery q = Haoma.q();
		q.filter("merchantId", merchantId);
		if (q.first() != null) {
			return q.first();
		} else {
			Haoma haoma = new Haoma();
			haoma.merchantId = merchantId;
			haoma.initPaidui();
			return haoma;
		}
	}

	public void initPaidui() {
		logger.debug("merchant id : " + this.merchantId);
		Merchant m = Merchant.findByMid(this.merchantId);
		String[] seatType = m.seatType;
		Paidui p = null;
		if (seatType == null) {
			return;
		}
		for (String i : seatType) {
			p = new Paidui();
			this.haomaMap.put(Integer.parseInt(i), p);
		}
	}

	/**
	 * 拿号（同步方法）
	 * 
	 * @param accountId
	 *            用户id
	 * @param mid
	 *            商家id
	 * @param seatNumber
	 *            座位数
	 * @return Reservation
	 */
	public synchronized static Reservation nahao(String accountId, String mid, int seatNumber, String tel) {
		Haoma haoma = Haoma.findByMerchantId(mid);
		Paidui paidui = haoma.haomaMap.get(seatNumber);
		paidui.enable = true;
		paidui.maxNumber += 1;
		haoma.save();

		Reservation reservation = new Reservation();
		
		// 如果tel是空，说明是APP拿号。否则是现场手机拿号。
		if(StringUtils.isEmpty(tel)){
			reservation.accountId = accountId;
		}
		
		reservation.merchantId = mid;
		reservation.myNumber = paidui.maxNumber;
		reservation.seatNumber = seatNumber;
		reservation.status = ReservationStatus.active;
		reservation.valid = true;
		reservation.created = new Date();
		reservation.modified = new Date();
		reservation.save();
		
		return reservation;
	}
	
	/**
	 * 拿号（同步方法）
	 * 
	 * @param accountId
	 *            用户id
	 * @param mid
	 *            商家id
	 * @param seatNumber
	 *            座位数
	 * @return Reservation
	 */
	public synchronized static void nahaoRollback(Reservation reservation) {
		Haoma haoma = Haoma.findByMerchantId(reservation.merchantId);
		Paidui paidui = haoma.haomaMap.get(reservation.seatNumber);
		paidui.maxNumber -= 1;
		haoma.save();
		reservation.valid = false;
		reservation.status = ReservationStatus.canceled;
		reservation.save();
	}

	/**
	 * 更新X人座位的finished, canceled, expired 数量
	 * 
	 * @param haoma
	 * @param mid
	 * @param myNumber
	 * @param seatNumber
	 * @param status
	 * @return
	 */
	public synchronized static Haoma updateByXmethod(Haoma haoma, String mid, int myNumber, int seatNumber, ReservationStatus status) {
		Paidui p = haoma.haomaMap.get(seatNumber);
		switch (status) {
		case canceled:
			if (myNumber >= p.currentNumber) {
				p.canceled += 1;
			}

			break;
		case finished:
			p.currentNumber += 1;
			p.finished += 1;
			break;
		case expired:
			p.currentNumber += 1;
			p.expired += 1;
			break;
		}
		haoma.save();
		return haoma;
	}
	
	public void updateSelf(){
		Iterator ite = this.haomaMap.keySet().iterator();
		while(ite.hasNext()){
			Integer key = (Integer)ite.next();
			Paidui p = this.haomaMap.get(key);
			if(!p.enable){
				continue;
			}
			
			// if maxNumber > 0 and currentNumber == 0, then set currentNumber to 1
			if(p.maxNumber > 0 && p.currentNumber == 0 ){
				p.currentNumber = 1;
				this.save();
			}
			
			// check current number is valid or not, if not valid: current number ++
			// otherwise save current number.
			Reservation r = Reservation.queryForCancel(merchantId, key, p.currentNumber);
			while(r !=null && !r.valid){
				p.currentNumber += 1;
				this.save();
				r = Reservation.queryForCancel(merchantId, key, p.currentNumber);
			}
		}
	}
	
	/**
	 * 清除排队信息（初始化）
	 */
	public static void clearPaidui(){
		MorphiaQuery q = Haoma.q();
		long count = q.count();
		List<Haoma> hList = new ArrayList<Haoma>();
		
		long time = 0;
		
		int countPerPage = 10;
		if(count > 0){
			if(count % 10 ==0){
				time = count / 10;
				logger.debug("count : " + count);
				logger.debug("time : " + time);
				// 整数页每十条做初始化操作
				for(int i=0;i< time; i++){
					hList = q.offset(i*countPerPage).limit(countPerPage).asList();
					for(Haoma h : hList){
						h.initPaidui();
						h.save();
					}
				}
			} else {
				time = count / 10 +1;
				logger.debug("count : " + count);
				logger.debug("time : " + time);
				for(int i=0;i< time; i++){
					hList = q.offset(i*countPerPage).limit(countPerPage).asList();
					for(Haoma h : hList){
						h.initPaidui();
						h.save();
					}
				}
			}
		}

		
		

		// 最后几条做初始化操作
//		hList = q.offset(i*countPerPage).asList();
//		for(Haoma h : hList){
//			h.initPaidui();
//			h.save();
//		}
	}
}
