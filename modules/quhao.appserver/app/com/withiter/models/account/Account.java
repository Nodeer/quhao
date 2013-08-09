package com.withiter.models.account;

import japidviews._javatags.I18nKeys;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import play.data.validation.Validation;
import play.i18n.Messages;
import play.libs.Codec;
import play.modules.morphia.Model.NoAutoTimestamp;
import cn.bran.japid.util.StringUtils;

import com.google.code.morphia.annotations.Entity;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;
import com.withiter.exceptions.ValidationException;

@Entity
@NoAutoTimestamp
public class Account extends AccountEntityDef {

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

		synchronized (Account.class) {
			if (!StringUtils.isEmpty(phone)) {
				if (Account.filter("phone", phone).count() > 0) {
					Validation.addError("phone exists.",
							I18nKeys.V_ALREADY_EXISTS);
				}
			}
			if (!StringUtils.isEmpty(email)) {
				if (Account.filter("email", email).count() > 0) {
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

	public static Account findByEmail(String email) {
		Account account = Account.q().filter("email", email).first();
		if (account == null) {
			return null;
		}
		return account;
	}

	public static Account findByPhone(String phone) {
		Account account = Account.q().filter("phone", phone).first();
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
	public static void updatePassword(Account account, String newpassword) {
		account.password = Codec.hexSHA1(newpassword);
		account.save();
	}

	/**
	 * get register and disabled accounts
	 * 
	 * @author Cross
	 * @return
	 */
	public static List<Account> getUnenabledAccounts() {
		MorphiaQuery q = Account.q();
		q.filter("enable", false);
		return q.asList();
	}

	
	public boolean signupValidate(String userName, String userPwd1,
			String userPwd2) {
		
		boolean flag = false;
		
		Validation.required(Messages.get(I18nKeys.F_USERNAME), userName);
		Validation.range(Messages.get(I18nKeys.F_USERNAME), userName.length(),
				6, 20);

		Validation.required(Messages.get(I18nKeys.F_PASSWORD), userPwd1);
		Validation.range(Messages.get(I18nKeys.F_PASSWORD), userPwd1.length(),
				8, 12);
		if (!Validation.hasError(Messages.get(I18nKeys.F_PASSWORD))) {
			if (!userPwd1.equals(userPwd2)) {
				Validation.addError(Messages.get(I18nKeys.F_PASSWORD),
						I18nKeys.V_REPEAT_PASSWORD_DOES_NOT_EQUAL);
			}
		}

		String password = Codec.hexSHA1(userPwd1);

		if (Validation.hasErrors())
			throw new ValidationException();
		
		synchronized (Account.class) {
			if(userName.contains("@")){
				if (Account.filter("email", userName).count() > 0) {
					Validation.addError(Messages.get(I18nKeys.F_EMAIL),
							I18nKeys.V_ALREADY_EXISTS);
				}
				this.email = userName;
			}else{
				if (Account.filter("phone", userName).count() > 0) {
					Validation.addError(Messages.get(I18nKeys.F_PHONE),
							I18nKeys.V_ALREADY_EXISTS);
				}
				this.phone = userName;
			}
			if (Validation.hasErrors()) {
				throw new ValidationException();
			} else {
				this.password = password;
				flag = create();
			}
		}
		
		return flag;
	}
}
