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

	public String validateThenCreate() {
		phone = this.phone.trim().toLowerCase();
		email = this.email.trim().toLowerCase();
		password = this.password.trim();

		if (!StringUtils.isEmpty(phone)) {
			Validation.phone("Invalid phone number", phone);
			if (phone.length() != 11) {
				Validation.addError("Invalid phone number",
						"Invalid phone number");
			}
		}
		if (!StringUtils.isEmpty(email)) {
			Validation.email("Invalid email address", email);
		}
		if (!StringUtils.isEmpty(password)) {
			Validation.min("At least 6 length", password.length(), 6);
			Validation.max("At most 20 length", password.length(), 20);
		}

		if (Validation.hasErrors()) {
			return Validation.errors().get(0).getKey();
		}

		synchronized (Reservation.class) {
			if (!StringUtils.isEmpty(phone)) {
				if (Reservation.filter("phone", phone).count() > 0) {
					Validation.addError("phone exists.",
							I18nKeys.V_ALREADY_EXISTS);
				}
			}
			if (!StringUtils.isEmpty(email)) {
				if (Reservation.filter("email", email).count() > 0) {
					Validation.addError("email exists.",
							I18nKeys.V_ALREADY_EXISTS);
				}
			}

			if (Validation.hasErrors()) {
				return Validation.errors().get(0).getKey();
			} else {
				this.password = Codec.hexSHA1(password);
				create();
			}
		}
		return null;
	}

	public static Reservation findByEmail(String email) {
		Reservation account = Reservation.q().filter("email", email).first();
		if (account == null) {
			return null;
		}
		return account;
	}

	public static Reservation findByPhone(String phone) {
		Reservation account = Reservation.q().filter("phone", phone).first();
		if (account == null) {
			return null;
		}
		return account;
	}

	/**
	 * @author Cross
	 * @param collectionName
	 *            : table name, default is UserImage
	 * @param file
	 *            : file you want to store
	 * @param filename
	 *            : file name
	 * @return true if save successfully, otherwise return false
	 */
	private static boolean saveFile(String collectionName, File file,
			String userId) {
		try {
			GridFS gridFS = new GridFS(MorphiaQuery.ds().getDB(),
					collectionName);
			GridFSInputFile gfs = gridFS.createFile(file);
			gfs.put("aliases", file.getName());
			gfs.put("filename", userId);
			gfs.put("contentType",
					file.getName().substring(file.getName().lastIndexOf(".")));
			gfs.save();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * @author Cross
	 * @param collectionName
	 * @param filename
	 * @return
	 */
	private static InputStream getFile(String collectionName, String filename) {
		InputStream is = null;
		try {
			GridFS gridFS = new GridFS(MorphiaQuery.ds().getDB(),
					collectionName);
			List<GridFSDBFile> dbfiles = gridFS.find(filename);
			if (!dbfiles.isEmpty()) {
				GridFSDBFile dbfile = dbfiles.get(dbfiles.size() - 1);
				if (dbfile != null) {
					is = dbfile.getInputStream();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return is;
	}

	/**
	 * @author Cross
	 * @param userImage
	 * @param account
	 * @return
	 */
	public static boolean saveUserImage(File userImage, Reservation account) {
		return saveFile("UserImage", userImage, account.id());
	}

	/**
	 * @author Cross
	 * @param account
	 * @return
	 */
	public static InputStream getUserImage(Reservation account) {
		return getFile("UserImage", account.id());
	}

	public boolean validatePassword(String password) {
		boolean flag = false;
		String hexedPwd = Codec.hexSHA1(password);
		if (this.password.equals(hexedPwd)) {
			flag = true;
		}
		return flag;
	}

	/**
	 * @author Cross
	 * @param account
	 * @param newpassword
	 */
	public static void updatePassword(Reservation account, String newpassword) {
		account.password = Codec.hexSHA1(newpassword);
		account.save();
	}

	/**
	 * get register and disabled accounts
	 * 
	 * @author Cross
	 * @return
	 */
	public static List<Reservation> getUnenabledAccounts() {
		MorphiaQuery q = Reservation.q();
		q.filter("enable", false);
		return q.asList();
	}

}
