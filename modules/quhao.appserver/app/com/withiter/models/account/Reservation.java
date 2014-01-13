package com.withiter.models.account;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.joda.time.DateTime;

import play.modules.morphia.Model.NoAutoTimestamp;
import cn.bran.japid.util.StringUtils;

import com.google.code.morphia.annotations.Entity;
import com.withiter.common.Constants.CreditStatus;
import com.withiter.common.Constants.ReservationStatus;
import com.withiter.models.merchant.Haoma;

@Entity
@NoAutoTimestamp
public class Reservation extends ReservationEntityDef {
	private static int DEFAULT_PAGE_ITEMS_NUMBER = 10;
	/**
	 * Find valid reservation list by account id
	 * 
	 * @param accountId
	 *            user account id
	 * @return the list of valid reservation
	 */
	public static List<Reservation> findValidReservations(String accountId) {
		MorphiaQuery q = Reservation.q();
		q.filter("accountId", accountId).filter("valid", true);
		return q.asList();
	}
	
	/**
	 * get next page current reservations  by account ID
	 * @param accountId account ID
	 * @param page the page number
	 * @param sortBy 排序方式
	 * @return reservations list
	 */

	public static List<Reservation> findValidReservations(String accountId,int page,String sortBy) {
		MorphiaQuery q = Reservation.q();
		q.filter("accountId", accountId).filter("valid", true);
		
		if (!StringUtils.isEmpty(sortBy)) {
			q = sortBy(q, sortBy);
		}else{
			q = sortBy(q, "-created");
		}
		return paginate(q, page);
	}
	
	/**
	 * Check whether does the reservation exist by account id, merchant id and
	 * seat number
	 * 
	 * @param accountId
	 *            user account id
	 * @param mid
	 *            merchant id
	 * @param seatNumber
	 *            the number of seat
	 * @return Reservation object
	 */
	public static Reservation reservationExist(String accountId, String mid,
			int seatNumber) {
		MorphiaQuery q = Reservation.q();
		
		q.filter("accountId", accountId).filter("merchantId", mid)
				.filter("valid", true).filter("seatNumber", seatNumber);
		return q.first();
	}

	/**
	 * Find the history reservations by account id
	 * 
	 * @param accountId
	 *            user account id
	 * @return the list of history reservations (valid == false)
	 */
	public static List<Reservation> findHistroyReservations(String accountId) {
		MorphiaQuery q = Reservation.q();
		q.filter("accountId", accountId).filter("valid", false);
		return q.asList();
	}

	/**
	 * get next page reservations by account ID
	 * @param accountId account ID
	 * @param page the page number
	 * @param sortBy 排序方式
	 * @return reservations list
	 */
	public static List<Reservation> findHistroyReservations(String accountId,int page,String sortBy) {
		MorphiaQuery q = Reservation.q();
		q.filter("accountId", accountId).filter("valid", false);
		
		if (!StringUtils.isEmpty(sortBy)) {
			q = sortBy(q, sortBy);
		}else{
			q = sortBy(q, "-created");
		}
		return paginate(q, page);
	}
	
	/**
	 * 通用排序
	 * @param q
	 * @param sortBy
	 * @return
	 */
	private static MorphiaQuery sortBy(MorphiaQuery q, String sortBy) {
		q.order(sortBy);
		return q;
	}
	
	/**
	 * 通用分页
	 * @param q
	 * @param page
	 * @return
	 */
	private static List<Reservation> paginate(MorphiaQuery q, int page) {
		q.offset((page - 1) * DEFAULT_PAGE_ITEMS_NUMBER).limit(
				DEFAULT_PAGE_ITEMS_NUMBER);
		return q.asList();
	}
	
	/**
	 * 当排队情况有变化时，需要推送到所有关联的用户 when current object updated, this function will
	 * automatically invoked.
	 */
	@OnUpdate
	private void updateHaoma() {
		String mid = this.merchantId;
		int myNumber = this.myNumber;
		int seatNumber = this.seatNumber;
		Haoma haoma = Haoma.findByMerchantId(mid);
		Haoma.updateByXmethod(haoma, mid, myNumber, seatNumber, this.status);
		pushToClient();
	}

	private void pushToClient() {
		// TODO add push to client.
	}

	/**
	 * Cancel one reservation by reservation id
	 * 
	 * @param reservationId
	 *            the id of reservation
	 */
	public static void cancel(String reservationId) {
		Reservation r = Reservation.findById(reservationId);
		if (r != null) {
			r.status = ReservationStatus.canceled;
			r.valid = false;
			r.modified = new Date();
			r.save();
		}
	}

	/**
	 * Finish one reservation by reservation id
	 * 
	 * @param reservationId
	 *            the id of reservation
	 */
	public static void finish(String reservationId) {
		Reservation r = Reservation.findById(reservationId);
		if (r != null) {
			r.status = ReservationStatus.finished;
			r.valid = false;
			r.modified = new Date();
			r.save();
			
			String accountId = r.accountId;
			Account account = Account.findById(accountId);
			account.jifen = account.jifen + 1;
			account.modified = new Date();
			account.save();
			
			//增加积分消费情况
			Credit credit = new Credit();
			credit.accountId = r.accountId;
			credit.merchantId = r.merchantId;
			credit.reservationId = r.id();
			credit.cost = true;
			credit.status = CreditStatus.finished;
			credit.created = new Date();
			credit.modified = new Date();
			credit.create();
		}
	}

	/**
	 * Expire one reservation by reservation id
	 * 
	 * @param reservationId
	 *            the id of reservation
	 */
	public static void expire(String reservationId) {
		Reservation r = Reservation.findById(reservationId);
		if (r != null) {
			r.status = ReservationStatus.expired;
			r.valid = false;
			r.modified = new Date();
			r.save();
			
			//cost one credit
			Credit credit = new Credit();
			credit.accountId = r.accountId;
			credit.merchantId = r.merchantId;
			credit.reservationId = r.id();
			credit.cost = true;
			credit.status = CreditStatus.expired;
			credit.created = new Date();
			credit.modified = new Date();
			credit.create();
		}
	}

	/**
	 * 
	 * query reservations by merchant id and account id
	 * 
	 * @param accountId
	 *            account id
	 * @param mid
	 *            merchant id
	 * @return the reservations
	 */
	public static List<Reservation> getReservationsByMerchantIdAndAccountId(
			String accountId, String mid) {
		MorphiaQuery q = Reservation.q();
		q.filter("accountId", accountId).filter("merchantId", mid)
				.filter("valid", true);
		return q.asList();
	}

	/**
	 * 
	 * query previous number
	 * 
	 * @param accountId
	 * @param mid
	 * @return
	 */
	public static long findPreviousNo(String mid, int seatNumber) {
		MorphiaQuery q = Reservation.q();
		q.filter("merchantId", mid).filter("seatNumber", seatNumber);
		return q.max("myNumber");
	}

	/**
	 * 
	 * query previous number
	 * 
	 * @param accountId
	 * @param mid
	 * @return
	 */
	public static long findCountBetweenCurrentNoAndMyNumber(String mid,
			int currentNo, int myNumber, int seatNumber) {
		MorphiaQuery q = Reservation.q();

		q.filter("seatNumber", seatNumber).filter("merchantId", mid)
				.filter("status", "canceled")
				.filter("myNumber", "<" + myNumber)
				.filter("myNumber", ">" + currentNo);

		return q.count();
	}
	
	/**
	 * Get the reservations which finished by merchant author
	 * @param seatNumber
	 * @param currentNumber
	 * @param mid
	 * @return
	 */
	public static Reservation findReservationForHandle(int seatNumber, int currentNumber, String mid){
		MorphiaQuery q = Reservation.q();
		q.filter("created >", (new DateTime(System.currentTimeMillis() - 1000l*60*60*24).toDate()))
		.filter("merchantId", mid)
		.filter("status", "active")
		.filter("seatNumber", seatNumber)
		.filter("myNumber", currentNumber);
		
		return q.first();
	}
	
	public static List<Reservation> findReservationsByMerchantIdandDate(String mid, Date beforeDate){
		List<Reservation> rList = new ArrayList<Reservation>();
		MorphiaQuery q = Reservation.q();
		q.filter("created", ">"+ beforeDate.getTime())
		.filter("merchantId", mid);
		rList = q.asList();
		return rList;
	}
	
	public static List<Reservation> findReservationsByMerchantIdandDate(String mid){
		List<Reservation> rList = new ArrayList<Reservation>();
		long duration = System.currentTimeMillis() - 1000l*60*60*24*365;
		Date d = new Date(duration);
		findReservationsByMerchantIdandDate(mid, d);
		return rList;
	}
	/**
	 * 
	 * @param rid  id of reservation
	 * @return reservation
	 */
	public static Reservation findByRid(String rid){
		MorphiaQuery q = Reservation.q();
		q.filter("_id", new ObjectId(rid));
		if(q.asKeyList().size() == 0){
			return null;
		}
		return (Reservation) q.asList().get(0);
	}
}
