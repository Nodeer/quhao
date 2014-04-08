package com.withiter.models.admin;

import japidviews._javatags.I18nKeys;

import java.util.List;

import play.data.validation.Validation;
import play.i18n.Messages;
import play.libs.Codec;
import play.modules.morphia.Model.MorphiaQuery;
import play.modules.morphia.Model.NoAutoTimestamp;

import com.google.code.morphia.annotations.Entity;
import com.withiter.models.account.Account;

@Entity
@NoAutoTimestamp
public class MerchantAccount extends MerchantAccountEntityDef {

	/**
	 * Find Account object by email.
	 * 
	 * @param email
	 * @return
	 */
	public static MerchantAccount findByEmail(String email) {
		MerchantAccount account = MerchantAccount.q().filter("email", email).first();
		if (account == null) {
			return null;
		}
		return account;
	}

	/**
	 * 分页查询，每页十条数据。
	 * @param page 第page页
	 * @param countPerPage 每页的记录数
	 * @return
	 */
	public static List<MerchantAccount> findNext(int page, int countPerPage) {
		MorphiaQuery q = MerchantAccount.q();
		q.offset((page-1)*countPerPage).limit(countPerPage);
		
		return q.asList();
	}
	
	/**
	 * validate user name and password for login function
	 * 
	 * @param userName
	 * @param userPwd
	 * @return if return null, means pass validation. otherwise, the returned
	 *         value is the error content.
	 */
	public static String validate(String email, String password) {
		Validation.required("Email不能为空", email);
		Validation.range("Email长度在6-20", email.length(),
				6, 20);

		Validation.required("密码不能为空", password);
		Validation.range("密码长度在6-20", password.length(),
				6, 20);
		
		Validation.email("Email格式不正确", email);

		if (Validation.hasErrors()) {
			return Validation.errors().get(0).toString();
		}

		String passwordHexed = Codec.hexSHA1(password);
		MorphiaQuery q = MerchantAccount.q();
		q.filter("email", email);
		q.filter("password", passwordHexed);
		if (q.first() != null) {
			MerchantAccount a = q.first();
			if (a.enable) {
				return null;
			} else {
				return "账号还未激活，请进入邮箱进行激活";
			}
		} else {
			return "账号密码错误！";
		}
	}
	
	/**
	 * Validate the password
	 * 
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
	public static void updatePassword(MerchantAccount account, String newpassword) {
		account.password = Codec.hexSHA1(newpassword);
		account.save();
	}
}
