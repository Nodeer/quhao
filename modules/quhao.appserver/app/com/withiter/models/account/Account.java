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
public class Account extends AccountEntityDef {

	public void signUp(String repeatPassword) {
	}

	/**
	 * @author Cross
	 * @param loginemail
	 * @param loginpassword
	 * @return
	 */
	public String login(String loginemail, String loginpassword) {
		return "";
	}

	
	public String newValidate(){
		Account account=Account.find("email", email).first();
		Validation.min("E-mail: at least 6 chars.", email.length(), 6);
		Validation.max("E-mail: at most 40 chars.", email.length(), 40);
		Validation.email("E-mail: Invalid e-mail address.", email);
		if (Validation.hasErrors()) {
			return Validation.errors().get(0).getKey();
		}
		synchronized (Account.class) {
			if (Account.filter("email", email).count() > 0) {
				if(account.isFinishedOnboarding){
					Validation.addError("E-mail: e-mail address exists.",
							I18nKeys.V_ALREADY_EXISTS);
				}else return null;
				
			}

			if (Validation.hasErrors()) {
				return Validation.errors().get(0).getKey();
			} else {
				create();
			}
		}
		return null;
	}
	/**
	 * @author Cross
	 * @return
	 */
	public String validate() {
		firstName = firstName.trim();
		lastName = lastName.trim();
		email = email.trim().toLowerCase();
		password = password.trim();
		location = location.trim();

		Validation.min("First Name: at least 2 chars.", firstName.length(), 2);
		Validation.max("First Name: at most 20 chars.", firstName.length(), 20);

		Validation.min("Last Name: at least 2 chars.", lastName.length(), 2);
		Validation.max("Last Name: at most 20 chars.", lastName.length(), 20);

		Validation.min("E-mail: at least 6 chars.", email.length(), 6);
		Validation.max("E-mail: at most 40 chars.", email.length(), 40);
		Validation.email("E-mail: Invalid e-mail address.", email);

		//Validation.min("Passowrd: at least 6 chars.", password.length(), 6);
		//Validation.max("Password: at most 12 chars.", password.length(), 12);

		//password = Codec.hexSHA1(password);

		if (Validation.hasErrors()) {
			return Validation.errors().get(0).getKey();
		}

		synchronized (Account.class) {
			if (Account.filter("email", email).count() > 0) {
				Validation.addError("E-mail: e-mail address exists.",
						I18nKeys.V_ALREADY_EXISTS);
			}

			if (Validation.hasErrors()) {
				return Validation.errors().get(0).getKey();
			} else {
				create();
			}
		}
		return null;
	}

	public static Account findUserByUserEmail(String email) {
		Account account = Account.q().filter("email", email).first();
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
	public static boolean saveUserImage(File userImage, Account account) {
		return saveFile("UserImage", userImage, account.id());
	}

	/**
	 * @author Cross
	 * @param account
	 * @return
	 */
	public static InputStream getUserImage(Account account) {
		return getFile("UserImage", account.id());
	}

	/**
	 * @author Cross
	 * @param account
	 * @param oldpassword
	 * @return
	 */
	public static boolean validatePassword(Account account, String oldpassword) {
		boolean flag = false;
		String hexedOldPwd = Codec.hexSHA1(oldpassword);
		if (account.password.equals(hexedOldPwd)) {
			flag = true;
		}
		return flag;
	}

	/**
	 * @author Cross
	 * @param account
	 * @param newpassword
	 */
	public static void updatePassword(Account account, String newpassword) {
		account.password = Codec.hexSHA1(newpassword);
		account.save();
	}

	public String displayName() {
		return this.firstName + " " + this.lastName;
	}

	/**
	 * get register and disabled accounts
	 * @author Cross
	 * @return
	 */
	public static List<Account> getUnenabledAccounts(){
		MorphiaQuery q = Account.q();
		q.filter("enable", false);
		return q.asList();
	}
}
