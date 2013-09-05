package com.withiter.models.account;

import japidviews._javatags.I18nKeys;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.bson.types.ObjectId;

import play.Play;
import play.data.validation.Validation;
import play.i18n.Messages;
import play.libs.Codec;
import play.modules.morphia.Model.MorphiaQuery;
import play.modules.morphia.Model.NoAutoTimestamp;

import cn.bran.japid.util.StringUtils;

import com.withiter.common.Constants;
import com.withiter.common.Constants.ReservationStatus;
import com.withiter.common.ContentType;
import com.withiter.models.merchant.Haoma;
import com.google.code.morphia.annotations.Entity;
import com.mongodb.DB;
import com.mongodb.Mongo;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;

@Entity
@NoAutoTimestamp
public class Reservation extends ReservationEntityDef {

	/**
	 * Find valid reservation list by account id
	 * @param accountId user account id
	 * @return the list of valid reservation
	 */
	public static List<Reservation> findValidReservations(String accountId) {
		MorphiaQuery q = Reservation.q();
		q.filter("accountId", accountId).filter("valid", true);
		return q.asList();
	}

	/**
	 * Check whether does the reservation exist by account id, merchant id and seat number
	 * @param accountId user account id
	 * @param mid merchant id
	 * @param seatNumber the number of seat
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
	 * @param accountId user account id
	 * @return the list of history reservations (valid == false)
	 */
	public static List<Reservation> findHistroyReservations(String accountId) {
		MorphiaQuery q = Reservation.q();
		q.filter("accountId", accountId).filter("valid", false);
		return q.asList();
	}

	/**
	 * 当排队情况有变化时，需要推送到所有关联的用户
	 * when current object updated, this function will automatically invoked.
	 */
	@OnUpdate
	private void updateHaoma(){
		String mid = this.merchantId;
		int myNumber = this.myNumber;
		int seatNumber = this.seatNumber;
		Haoma haoma = Haoma.findByMerchantId(mid);
		Haoma.updateByXmethod(haoma, mid, myNumber, seatNumber, this.status);
		pushToClient();
	}
	
	private void pushToClient(){
		// TODO add push to client.
	}

	/**
	 * Cancel one reservation by reservation id
	 * @param reservationId the id of reservation
	 */
	public static void cancel(String reservationId) {
		Reservation r = Reservation.findById(reservationId);
		if(r != null){
			r.status = ReservationStatus.canceled;
			r.valid = false;
			r.save();
		}
	}
	
	/**
	 * Finish one reservation by reservation id
	 * @param reservationId the id of reservation
	 */
	public static void finish(String reservationId) {
		Reservation r = Reservation.findById(reservationId);
		if(r != null){
			r.status = ReservationStatus.finished;
			r.valid = false;
			r.save();
		}
	}

	/**
	 * Expire one reservation by reservation id
	 * @param reservationId the id of reservation
	 */
	public static void expire(String reservationId) {
		Reservation r = Reservation.findById(reservationId);
		if(r != null){
			r.status = ReservationStatus.expired;
			r.valid = false;
			r.save();
		}
	}

	/**
	 * 
	 * query reservations by merchant id and account id
	 * 
	 * @param accountId account id
	 * @param mid merchant id
	 * @return the reservations 
	 */
	public static List<Reservation> getReservationsByMerchantIdAndAccountId(
			String accountId, String mid)
	{
		MorphiaQuery q = Reservation.q();
		q.filter("accountId", accountId).filter("merchantId", mid)
				.filter("valid", true);
		return q.asList();
	}
	
}
