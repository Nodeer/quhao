package com.withiter.models.account;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.joda.time.DateTime;

import play.Play;
import play.modules.morphia.Model.NoAutoTimestamp;
import cn.bran.japid.util.StringUtils;

import com.google.code.morphia.annotations.Entity;
import com.withiter.common.Constants.CreditStatus;
import com.withiter.common.Constants.ReservationStatus;
import com.withiter.models.merchant.Haoma;
import com.withiter.models.merchant.Merchant;

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
	 * 获取到第index个reservation
	 * @param merchantId 商家id
	 * @param seatNumber 座位类型
	 * @param index 第index个reservation
	 * @return Reservation
	 */
	public static Reservation findReservationForSMSRemind(String merchantId, int seatNumber, int index){
		MorphiaQuery q = Reservation.q();
		Merchant m = Merchant.findByMid(merchantId);

		Calendar c = Calendar.getInstance();
		String openTime = m.openTime;
		int openTimeHour = Integer.parseInt(openTime.split(":")[0]);
		c.set(Calendar.AM_PM, Calendar.AM);
		c.set(Calendar.HOUR, openTimeHour);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		
		Calendar c2 = Calendar.getInstance();
		String closeTime = m.closeTime;
		int closeTimeHour = Integer.parseInt(closeTime.split(":")[0]);
		c.set(Calendar.AM_PM, Calendar.AM);
		c.set(Calendar.HOUR, closeTimeHour);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		
		// query latest one day's reservation
		q.filter("created >", c.getTime());
		q.filter("created <", c2.getTime());
		q.filter("merchantId", merchantId).filter("seatNumber", seatNumber);
		q.filter("valid", true);
		
		// 得到第index个Reservation
		q.order("created");
		q.offset(index);
		return q.first();
	}
	
	/**
	 * get next page current reservations by account ID
	 * 
	 * @param accountId
	 *            account ID
	 * @param page
	 *            the page number
	 * @param sortBy
	 *            排序方式
	 * @return reservations list
	 */
	public static List<Reservation> findValidReservations(String accountId, int page, String sortBy) {
		MorphiaQuery q = Reservation.q();
		q.filter("accountId", accountId).filter("valid", true);

		if (!StringUtils.isEmpty(sortBy)) {
			q = sortBy(q, sortBy);
		} else {
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
	public static Reservation reservationExist(String accountId, String mid, int seatNumber) {
		MorphiaQuery q = Reservation.q();

		q.filter("accountId", accountId).filter("merchantId", mid).filter("valid", true).filter("seatNumber", seatNumber);
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
	 * 
	 * @param accountId
	 *            account ID
	 * @param page
	 *            the page number
	 * @param sortBy
	 *            排序方式
	 * @return reservations list
	 */
	public static List<Reservation> findHistroyReservations(String accountId, int page, String sortBy) {
		MorphiaQuery q = Reservation.q();
		q.filter("accountId", accountId).filter("valid", false);

		if (!StringUtils.isEmpty(sortBy)) {
			q = sortBy(q, sortBy);
		} else {
			q = sortBy(q, "-created");
		}
		return paginate(q, page);
	}

	/**
	 * 通用排序
	 * 
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
	 * 
	 * @param q
	 * @param page
	 * @return
	 */
	private static List<Reservation> paginate(MorphiaQuery q, int page) {
		q.offset((page - 1) * DEFAULT_PAGE_ITEMS_NUMBER).limit(DEFAULT_PAGE_ITEMS_NUMBER);
		return q.asList();
	}

	/**
	 * 当排队情况有变化时，需要推送到所有关联的用户
	 * when current object updated, this function will automatically invoked.
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
	public static boolean cancel(String reservationId) {
		Reservation r = Reservation.findById(reservationId);
		if (r != null) {
			r.status = ReservationStatus.canceled;
			r.valid = false;
			r.modified = new Date();
			r.save();
			return true;
		}

		return false;
	}

	/**
	 * Finish one reservation by reservation id
	 * 
	 * @param reservationId
	 *            the id of reservation
	 */
	public static boolean finish(String reservationId) {
		Reservation r = Reservation.findById(reservationId);
		if (r != null) {
			r.status = ReservationStatus.finished;
			r.valid = false;
			r.modified = new Date();
			r.save();

			String accountId = r.accountId;
			if(accountId == null){
				return true;
			}
			
			int finishedJifen = Integer.parseInt(Play.configuration.getProperty("credit.finished.jifen"));
			Account account = Account.findById(accountId);
			account.jifen = account.jifen + finishedJifen;
			account.modified = new Date();
			account.save();

			// 增加积分消费情况
			Credit credit = new Credit();
			credit.accountId = r.accountId;
			credit.merchantId = r.merchantId;
			credit.reservationId = r.id();
			credit.cost = true;
			credit.jifen = finishedJifen;
			credit.status = CreditStatus.finished;
			credit.created = new Date();
			credit.modified = new Date();
			credit.create();
			return true;
		}

		return false;
	}

	/**
	 * Expire one reservation by reservation id
	 * 
	 * @param reservationId
	 *            the id of reservation
	 */
	public static boolean expire(String reservationId) {
		Reservation r = Reservation.findById(reservationId);
		if (r != null) {
			r.status = ReservationStatus.expired;
			r.valid = false;
			r.modified = new Date();
			r.save();

			return true;
		}

		return false;
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
	public static List<Reservation> getReservationsByMerchantIdAndAccountId(String accountId, String mid) {
		MorphiaQuery q = Reservation.q();
		q.filter("accountId", accountId).filter("merchantId", mid).filter("valid", true);
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
	public static long findCountBetweenCurrentNoAndMyNumber(String mid, int currentNo, int myNumber, int seatNumber) {
		MorphiaQuery q = Reservation.q();
		q.filter("seatNumber", seatNumber).filter("merchantId", mid).filter("status", "canceled").filter("myNumber", "<" + myNumber).filter("myNumber", ">" + currentNo);
		return q.count();
	}

	/**
	 * Get the reservations which finished by merchant author
	 * 
	 * @param seatNumber
	 * @param currentNumber
	 * @param mid
	 * @return
	 */
	public static Reservation findReservationForHandle(int seatNumber, int currentNumber, String mid) {
		MorphiaQuery q = Reservation.q();
		q.filter("created >", (new DateTime(System.currentTimeMillis() - 1000l * 60 * 60 * 24).toDate()));
		q.filter("merchantId", mid).filter("status", "active");
		q.filter("seatNumber", seatNumber).filter("myNumber", currentNumber);
		return q.first();
	}

	public static List<Reservation> findReservationsByMerchantIdandDate(String mid, Date beforeDate) {
		MorphiaQuery q = Reservation.q();
		q.filter("created >", new DateTime(beforeDate.getTime()).toDate()).filter("merchantId", mid);
		return q.asList();
	}

	/**
	 * 统计一年当中此商家的Reservation
	 * @param mid 商家ID
	 * @return
	 */
	public static List<Reservation> findReservationsByMerchantIdandDate(String mid) {
		long duration = System.currentTimeMillis() - 1000l * 60 * 60 * 24 * 365;
		Date d = new Date(duration);
		return findReservationsByMerchantIdandDate(mid, d);
	}

	/**
	 * 
	 * @param rid
	 *            id of reservation
	 * @return reservation
	 */
	public static Reservation findByRid(String rid) {
		MorphiaQuery q = Reservation.q();
		q.filter("_id", new ObjectId(rid));
		if (q.asKeyList().size() == 0) {
			return null;
		}
		return (Reservation) q.asList().get(0);
	}
	
	public static Reservation queryForCancel(String merchantId, int seatNumber, int currentNumber){
		MorphiaQuery q = Reservation.q();
		Merchant m = Merchant.findByMid(merchantId);

		Calendar c = Calendar.getInstance();
		String openTime = m.openTime;
		int openTimeHour = Integer.parseInt(openTime.split(":")[0]);
		c.set(Calendar.AM_PM, Calendar.AM);
		c.set(Calendar.HOUR, openTimeHour);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		
		// query latest one day's reservation
//		q.filter("created >", (new DateTime(c.getTimeInMillis()).toDate()));
//		q.filter("merchantId", merchantId).filter("seatNumber", seatNumber);
//		q.filter("myNumber", currentNumber);

		q.filter("created >", (new DateTime(c.getTimeInMillis()).toDate()));
		q.filter("merchantId", merchantId).filter("seatNumber", seatNumber);
		
		q.filter("myNumber >", currentNumber);
		
		//TODO wjzwjz valid match true?
		q.filter("valid", "true");
		q.order("myNumber").limit(1);

		return q.first();
	}
}
