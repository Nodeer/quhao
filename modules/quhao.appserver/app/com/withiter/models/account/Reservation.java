package com.withiter.models.account;

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.bson.types.ObjectId;
import org.joda.time.DateTime;

import play.Logger;
import play.Play;
import play.modules.morphia.Model.NoAutoTimestamp;
import cn.bran.japid.util.StringUtils;

import com.google.code.morphia.annotations.Entity;
import com.withiter.common.Constants;
import com.withiter.common.Constants.CreditStatus;
import com.withiter.common.Constants.ReservationStatus;
import com.withiter.models.merchant.Haoma;
import com.withiter.models.merchant.Merchant;
import com.withiter.utils.RemindDateUtils;

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
		q.filter("accountId", accountId).filter("valid", true).filter("status !=", ReservationStatus.canceled).filter("available", true);
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
		q.filter("accountId", accountId).filter("valid", true).filter("available", true);

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
		q.filter("accountId", accountId).filter("valid", false).filter("available", true);
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
		q.filter("accountId", accountId).filter("valid", false).filter("available", true);

		if (!StringUtils.isEmpty(sortBy)) {
			q = sortBy(q, sortBy);
		} else {
			q = sortBy(q, "-created");
		}
		return paginate(q, page);
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
	public static List<Reservation> findHistroyReservationsNew(String accountId, String sortBy) {
		MorphiaQuery q = Reservation.q();
		q.filter("accountId", accountId).filter("valid", false).filter("available", true).filter("status !=", ReservationStatus.invalidByMerchantUpdate);

		if (!StringUtils.isEmpty(sortBy)) {
			q = sortBy(q, sortBy);
		} else {
			q = sortBy(q, "-created");
		}
		return  q.asList();
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
//		String mid = this.merchantId;
//		int myNumber = this.myNumber;
//		int seatNumber = this.seatNumber;
//		Haoma haoma = Haoma.findByMerchantId(mid);
//		Haoma.updateByXmethod(haoma, mid, myNumber, seatNumber, this.status);
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
			
			// 返还积分
			String aid = r.accountId;
			Account account = Account.findById(new ObjectId(aid));
			int jifen = Integer.parseInt(Play.configuration.getProperty("credit.cancel.jifen"));
			account.jifen += jifen;
			account.save();
			
			// 增加积分消费情况
			Credit credit = new Credit();
			credit.accountId = r.accountId;
			credit.merchantId = r.merchantId;
			credit.reservationId = r.id();
			credit.cost = true;
			credit.jifen = jifen;
			credit.status = CreditStatus.canceled;
			credit.created = new Date();
			credit.modified = new Date();
			credit.create();
			
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
			// 手机号产生的reservation
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
			
			// 增加积分消费情况
			Credit credit = new Credit();
			credit.accountId = r.accountId;
			credit.merchantId = r.merchantId;
			credit.reservationId = r.id();
			credit.cost = true;
			credit.jifen = Integer.parseInt(Play.configuration.getProperty("credit.getnumber.jifen"));
			credit.status = CreditStatus.expired;
			credit.created = new Date();
			credit.modified = new Date();
			credit.create();
			
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
	public static long findCountBetweenCurrentNoAndMyNumber(String mid, int currentNo, int myNumber, int seatNumber, long version) {
		MorphiaQuery q1 = Reservation.q();
		q1.filter("version", version);
		Reservation rese = q1.filter("seatNumber", seatNumber).filter("myNumber =" , currentNo).order("-created").first();
		
		MorphiaQuery q = Reservation.q();
		q.filter("version", version);
		q.filter("seatNumber", seatNumber).filter("merchantId", mid).filter("status", "canceled").filter("myNumber <" , myNumber).filter("myNumber >" , currentNo);
		if(rese != null){
			q.filter("created >=", rese.created);
		}
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
	public static Reservation findReservationForHandle(int seatNumber, int currentNumber, String mid, long version) {
		MorphiaQuery q = Reservation.q();
		q.filter("version", version);
//		q.filter("created >", (new DateTime(System.currentTimeMillis() - 1000l * 60 * 60 * 24).toDate()));
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
	
	public static Reservation queryForCancel(String merchantId, int seatNumber, int currentNumber, long version){
//		Merchant m = Merchant.findByMid(merchantId);
//		
//		// 查询出商家更新信息的时间
//		Date newestDate = m.modified;
//		MorphiaQuery q = Reservation.q();
//		
//		q.filter("created >=", (new DateTime(newestDate).toDate()));
//		q.filter("merchantId", merchantId).filter("seatNumber", seatNumber);
//		q.filter("myNumber =", currentNumber);
//		q.order("-created");
//		
//		return q.first();
		
		
		MorphiaQuery q = Reservation.q();
// 		Merchant m = Merchant.findByMid(merchantId);
 		
 		q.filter("version", version);
 		q.filter("merchantId", merchantId);
// 		q.filter("status !=","invalidByMerchantUpdate");
 		q.filter("seatNumber", seatNumber);
 		q.filter("myNumber =", currentNumber);
 		

//		Calendar c = Calendar.getInstance();
//		String openTime = m.openTime;
//		int openTimeHour = Integer.parseInt(openTime.split(":")[0]);
//		c.set(Calendar.AM_PM, Calendar.AM);
//		c.set(Calendar.HOUR, openTimeHour);
//		c.set(Calendar.MINUTE, 0);
//		c.set(Calendar.SECOND, 0);
// 		
//		q.filter("created >", (new DateTime(c.getTimeInMillis()).toDate()));
// 		q.filter("merchantId", merchantId).filter("seatNumber", seatNumber);
//		q.filter("status !=","invalidByMerchantUpdate");
//		
// 		q.filter("myNumber =", currentNumber);
//		q.order("myNumber").limit(1);
//		q.order("-created");
		
 		return q.first();
	}
	
	/**
	 * 商家更新资料导致当前所有的Reservation都变成invalid状态
	 * @param seatNumber
	 * @param merchantId
	 */
	public static void invalidByMerchantUpdate(int seatNumber, String merchantId){
		MorphiaQuery q = Reservation.q();
		q.filter("merchantId", merchantId).filter("seatNumber", seatNumber);
		q.filter("valid", true);
		
		Iterator it = q.iterator();
		Reservation r = null;
		while(it.hasNext()){
			r = (Reservation)it.next();
			r.valid = false;
			r.status = ReservationStatus.invalidByMerchantUpdate;
			r.save();
		}
	}

	/**
	 * 当天截止到当前时间所有Reservation
	 * @param mid
	 * @return
	 */
	private static MorphiaQuery todayCount(String mid) {
		MorphiaQuery q = Reservation.q();
		RemindDateUtils utils = new RemindDateUtils();
		Date todayStart = utils.getTodayStartTime();
		q.filter("created >", todayStart).filter("merchantId", mid);
		return q;
	}
	
	/**
	 * 前一天所有Reservation
	 * @param mid
	 * @return
	 */
	private static MorphiaQuery lastDayCount(String mid){
		MorphiaQuery q = Reservation.q();
		RemindDateUtils utils = new RemindDateUtils();
		Date lastDayStart = utils.getLastDayStartTime();
		Date lastDayEnd = utils.getLastDayEndTime();
		q.filter("created >", lastDayStart).filter("created <", lastDayEnd).filter("merchantId", mid);
		return q;
	}
	
	/**
	 * 上一个月所有Reservation
	 * @param mid
	 * @return
	 */
	private static MorphiaQuery lastMonthCount(String mid){
		MorphiaQuery q = Reservation.q();
		RemindDateUtils utils = new RemindDateUtils();
		Date lastMonthStart = utils.getLastMonthStartTime();
		Date lastMonthEnd = utils.getLastMonthEndTime();
		Logger.debug("lastMonthStart : start date -> %s", lastMonthStart);
		Logger.debug("lastMonthEnd : end date -> %s", lastMonthEnd);
		q.filter("created >", lastMonthStart).filter("created <", lastMonthEnd).filter("merchantId", mid);
		Logger.debug("merchantId -> %s", mid);
		Logger.debug("last month reservation count -> %s", q.count());
		return q;
	}
	
	/**
	 * 上一个月所有Reservation
	 * @param mid
	 * @return
	 */
	private static MorphiaQuery lastThreeMonthsCount(String mid){
		MorphiaQuery q = Reservation.q();
		RemindDateUtils utils = new RemindDateUtils();
		Date lastThreeMonthsStart = utils.getLastThreeMonthsStartTime();
		Date lastThreeMonthsEnd = utils.getLastMonthEndTime();
		q.filter("created >", lastThreeMonthsStart).filter("created <", lastThreeMonthsEnd).filter("merchantId", mid);
		return q;
	}
	
	
	public static long lastMonthFinishCount(String mid) {
		MorphiaQuery q = lastMonthCount(mid);
		q.filter("status", Constants.ReservationStatus.finished);
		return q.count();
	}

	public static long lastMonthCancelCount(String mid) {
		MorphiaQuery q = lastMonthCount(mid);
		q.filter("status", Constants.ReservationStatus.canceled);
		return q.count();
	}

	public static long lastQuarterFinishCount(String mid) {
		// TODO Auto-generated method stub
		return 0;
	}

	public static long lastQuarterCancelCount(String mid) {
		// TODO Auto-generated method stub
		return 0;
	}

	public static long lastThreeMonthsFinishCount(String mid) {
		MorphiaQuery q = lastThreeMonthsCount(mid);
		q.filter("status", Constants.ReservationStatus.finished);
		return q.count();
	}

	public static long lastThreeMonthsCancelCount(String mid) {
		MorphiaQuery q = lastThreeMonthsCount(mid);
		q.filter("status", Constants.ReservationStatus.canceled);
		return q.count();
	}
	
	public static long lastDayFinishCount(String mid) {
		MorphiaQuery q = lastDayCount(mid);
		q.filter("status", Constants.ReservationStatus.finished);
		return q.count();
	}
	
	public static long lastDayCancelCount(String mid) {
		MorphiaQuery q = lastDayCount(mid);
		q.filter("status", Constants.ReservationStatus.canceled);
		return q.count();
	}

	public static long todayFinishCount(String mid) {
		MorphiaQuery q = todayCount(mid);
		q.filter("status", Constants.ReservationStatus.finished);
		return q.count();
	}

	public static long todayCancelCount(String mid) {
		MorphiaQuery q = todayCount(mid);
		q.filter("status", Constants.ReservationStatus.canceled);
		return q.count();
	}
}
