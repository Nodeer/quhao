package com.withiter.models.account;

import japidviews._javatags.I18nKeys;

import java.io.File;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
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

@Entity
@NoAutoTimestamp
public class Account extends AccountEntityDef {

	/**
	 * validate user name and password for login function
	 * @param userName
	 * @param userPwd
	 * @return if return null, means pass validation. otherwise, the returned value is the error content.
	 */
	public static String validate(String userName, String userPwd){
		Validation.required(Messages.get(I18nKeys.F_USERNAME), userName);
		Validation.range(Messages.get(I18nKeys.F_USERNAME), userName.length(),
				6, 30);

		Validation.required(Messages.get(I18nKeys.F_PASSWORD), userPwd);
		Validation.range(Messages.get(I18nKeys.F_PASSWORD), userPwd.length(),
				6, 12);

		if (Validation.hasErrors()){
			return Validation.errors().get(0).toString();
		}
		
		String password = Codec.hexSHA1(userPwd);
		MorphiaQuery q = Account.q();
		if(userName.contains("@")){
			q.filter("email", userName);
		}else{
			q.filter("phone", userName);
		}
		q.filter("password", password);
		if(q.first() != null){
			Account a = q.first();
			if(a.enable){
				return null;
			}else{
				return "账号还未激活，请进入邮箱进行激活";
			}
		}else{
			return "账号密码错误！";
		}
	}
	
	/**
	 * validate and create self Account object.
	 * @return if return null, means pass validation. otherwise, the returned value is the error content.
	 */
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
			Validation.max("At most 12 length", password.length(), 12);
		}

		if (Validation.hasErrors()) {
			return Validation.errors().get(0).getKey();
		}

		synchronized (Account.class) {
			if (!StringUtils.isEmpty(phone)) {
				if (Account.filter("phone", phone).count() > 0) {
					Validation.addError("该手机号码已注册",
							I18nKeys.V_ALREADY_EXISTS);
				}
			}
			if (!StringUtils.isEmpty(email)) {
				if (Account.filter("email", email).count() > 0) {
					Validation.addError("该邮箱已注册",
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

	/**
	 * Find Account object by email.
	 * @param email
	 * @return
	 */
	public static Account findByEmail(String email) {
		Account account = Account.q().filter("email", email).first();
		if (account == null) {
			return null;
		}
		return account;
	}

	/**
	 * Find Account object by mobile number
	 * @param phone the mobile number
	 * @return
	 */
	public static Account findByPhone(String phone) {
		Account account = Account.q().filter("phone", phone).first();
		if (account == null) {
			return null;
		}
		return account;
	}
	/**
	 * Find Exists Account object by mobile number
	 * @param phone the mobile number
	 * @return
	 */
	public static Account findExistsAccount(String phone) {
		Account account = Account.q().filter("phone", phone).filter("enable", true).first();
		if (account == null) {
			return null;
		}
		return account;
	}
	/**
	 * Find Account object 
	 * @param phone the mobile number
	 * @param authCode
	 * @return
	 */
	public static Account findAccount(String phone,String authCode) {
	    Calendar calendar = new GregorianCalendar();
	    Date date2 = new Date();
	    calendar.setTime(date2);
	    calendar.add(calendar.DATE, -1);
	    date2 = calendar.getTime();
		
		Account account = Account.q().filter("phone", phone).filter("authcode", authCode).filter("authDate >",date2).first();
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
	 * Validate the password
	 * @param password
	 * @return true if the password is right
	 */
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

	/**
	 * Validate for sign up function.
	 * @param userName user name, email/mobile number
	 * @param userPwd1 password
	 * @param userPwd2 repeat password
	 * @return
	 */
	public String signupValidate(String userName, String userPwd1,
			String userPwd2) {
		
		Validation.required(Messages.get(I18nKeys.F_USERNAME), userName);
		Validation.range(Messages.get(I18nKeys.F_USERNAME), userName.length(),
				6, 20);

		Validation.required(Messages.get(I18nKeys.F_PASSWORD), userPwd1);
		Validation.range(Messages.get(I18nKeys.F_PASSWORD), userPwd1.length(),
				6, 12);
		if (!Validation.hasError(Messages.get(I18nKeys.F_PASSWORD))) {
			if (!userPwd1.equals(userPwd2)) {
				Validation.addError(Messages.get(I18nKeys.F_PASSWORD),
						I18nKeys.V_REPEAT_PASSWORD_DOES_NOT_EQUAL);
			}
		}

		String password = Codec.hexSHA1(userPwd1);

		if (Validation.hasErrors()){
			return Validation.errors().get(0).toString();
		}
		
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
				return Validation.errors().get(0).toString();
			} else {
				this.password = password;
				this.save();
			}
		}
		return null;
	}

	public static void cleanSignUp() {
//		DB db =  Account.db();
//		db.command("db.Account.update({\"className\":\"com.withiter.models.account.Account\"},{$set:{\"isSignIn\":\"true\"}},false,true)");
		
		List<Account> allAccounts = findAll();
		for (Account account : allAccounts) {
			account.isSignIn = false;
			account.save();
		}
		
	}
}
