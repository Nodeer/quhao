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
import com.withiter.common.ContentType;
import com.google.code.morphia.annotations.Entity;
import com.mongodb.DB;
import com.mongodb.Mongo;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;

@Entity
@NoAutoTimestamp
public class Reservation extends ReservationEntityDef {

	public static List<Reservation> findValidReservations(String accountId) {
		MorphiaQuery q = Reservation.q();
		q.filter("accountId", accountId).filter("valid", true);
		return q.asList();
	}
	
	public static Reservation reservationExist(String accountId,
			String mid, int seatNumber){
		MorphiaQuery q = Reservation.q();
		q.filter("accountId", accountId).filter("merchantId", mid).filter("valid", true).filter("seatNumber", seatNumber);
		return q.first();
	}

	public static List<Reservation> findHistroyReservations(String accountId)
	{
			MorphiaQuery q = Reservation.q();
			q.filter("accountId", accountId).filter("valid", false);
			return q.asList();
	}
}