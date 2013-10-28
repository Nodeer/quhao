package controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.HttpException;
import org.apache.commons.lang.StringUtils;

import play.Logger;
import play.libs.Codec;
import vo.MerchantVO;
import vo.PersonalInfoVO;
import vo.ReservationVO;
import vo.account.LoginVO;
import vo.account.SignupVO;

import com.withiter.common.Constants;
import com.withiter.common.sms.business.SMSBusiness;
import com.withiter.models.account.Account;
import com.withiter.models.account.Reservation;
import com.withiter.models.merchant.Merchant;

public class AccountController extends BaseController {

	/**
	 * 手机号注册生成随即6位数字验证码
	 * 
	 * @param mobile
	 *            手机号码
	 * @param os
	 *            注册手持设备类型（Android，iOS） <br/>
	 *            返回JSON SignupVO
	 *            返回SignupVO对象，需对errorKey进行判断，如果不是空字符串，则表示生成失败，否则生成成功。
	 */
	public static void GenerateAuthCode(String mobile, String os) {
		Account account = new Account();
		SignupVO suVO = new SignupVO();
		suVO.errorKey = "mobile";
		if (StringUtils.isEmpty(mobile)) {
			suVO.errorText = "号码不能为空";
			renderJSON(suVO);
		}
		if (Account.findByPhone(mobile) != null) {
			suVO.errorText = "此号码已被注册";
			renderJSON(suVO);
		}
		account.phone = mobile;
		if (Constants.MobileOSType.ANDROID.toString().equalsIgnoreCase(os)) {
			account.mobileOS = Constants.MobileOSType.ANDROID;
		}
		if (Constants.MobileOSType.IOS.toString().equalsIgnoreCase(os)) {
			account.mobileOS = Constants.MobileOSType.IOS;
		}

		try {
			int result = SMSBusiness.sendAuthCodeForSignup(mobile);
			if (result == 0) {
				suVO.errorText = "发送短信出错";
				renderJSON(suVO);
			} else {
				account.password = Codec.hexSHA1(String.valueOf(result));
				account.save();
				suVO.errorKey = "";
				suVO.errorText = "";
				renderJSON(suVO);
			}
		} catch (HttpException e) {
			suVO.errorText = e.toString();
			e.printStackTrace();
			renderJSON(suVO);
		} catch (IOException e) {
			suVO.errorText = e.toString();
			e.printStackTrace();
			renderJSON(suVO);
		}
	}

	/**
	 * 通过手机号和验证码进行注册
	 * 
	 * @param mobile
	 *            手机号码
	 * @param code
	 *            验证码
	 * @param os
	 *            手机操作系统 <br/>
	 *            返回JSON SignupVO
	 *            返回SignupVO对象，需对errorKey进行判断，如果不是空字符串，则表示生成失败，否则生成成功。
	 */
	public static void signupWithMobile(String mobile, String code, String os) {
		SignupVO suVO = new SignupVO();
		suVO.errorKey = "mobile";
		if (StringUtils.isEmpty(mobile)) {
			suVO.errorText = "手机号码不能为空";
			renderJSON(suVO);
		}
		if (StringUtils.isEmpty(code)) {
			suVO.errorText = "验证码不能为空";
			renderJSON(suVO);
		}

		Account account = Account.findByPhone(mobile);
		if (account == null) {
			suVO.errorText = "手机号码尚未接收过验证码";
			renderJSON(suVO);
		}

		if (account.password.equals(Codec.hexSHA1(code))) {
			account.enable = true;
			account.save();
			suVO.errorKey = "";
			suVO.errorText = "";
			renderJSON(suVO);
		}
	}

	/**
	 * Account sign up with mobile number or email address
	 * 
	 * @param phone
	 * @param email
	 * @param password
	 * @param os
	 *            the type of end user (ANDROID, IOS, WEB)
	 */
	public static void signup(String phone, String email, String password,
			String os) {
		Account account = new Account();
		account.password = password;
		if (!StringUtils.isEmpty(phone)) {
			account.phone = phone;
			account.email = "";
		}
		if (!StringUtils.isEmpty(email)) {
			account.email = email;
			account.phone = "";
		}

		if (Constants.MobileOSType.ANDROID.toString().equalsIgnoreCase(os)) {
			account.mobileOS = Constants.MobileOSType.ANDROID;
		}
		if (Constants.MobileOSType.IOS.toString().equalsIgnoreCase(os)) {
			account.mobileOS = Constants.MobileOSType.IOS;
		}

		String result = account.validateThenCreate();

		Logger.info(result);

		if (result != null) {
			renderHtml(result);
		} else {
			renderHtml("success");
		}
	}

	public static void enable() {

	}

	/**
	 * login with mobile or email
	 * 
	 * @param phone
	 *            the mobile number
	 * @param email
	 *            the email
	 * @param password
	 *            the password
	 */
	public static void login(String phone, String email, String password) {
		LoginVO loginVO = new LoginVO();
		Account account = null;
		if (StringUtils.isEmpty(phone)) {
			account = Account.findByEmail(email);
		} else {
			account = Account.findByPhone(phone);
		}

		if (account != null) {
			boolean flag = account.validatePassword(password);
			if (flag) {
				loginVO.msg = "success";
				loginVO.errorCode = 0;
				loginVO.build(account);
			} else {
				loginVO.errorCode = -2;
				loginVO.msg = "fail";
			}
		} else {
			loginVO.errorCode = -1;
			loginVO.msg = "fail";
		}
		renderJSON(loginVO);
	}

	public static void logout() {

	}

	/**
	 * sign in
	 * 
	 * @param phone
	 * @param email
	 */
	public static void signIn(String phone, String email) {
		Account account = null;
		if (null != phone) {
			account = Account.findByPhone(phone);
		} else if (null != email) {
			account = Account.findByEmail(email);
		}

		LoginVO loginVO = new LoginVO();

		if (null == account) {
			loginVO.errorCode = -1;
			loginVO.msg = "account is not exsit";
		} else if (!account.isSignIn) {
			account.signIn = account.signIn + 1;
			account.isSignIn = true;
			account.save();
			loginVO.errorCode = 1;
			loginVO.msg = "success";
			loginVO.build(account);
		} else if (account.isSignIn) {
			loginVO.errorCode = -2;
			loginVO.msg = "you have signed in";
			loginVO.build(account);
		}

		renderJSON(loginVO);
	}

	/**
	 * 
	 * get current merchants by account id
	 * 
	 * @param accountId
	 *            account id
	 */
	public static void getCurrentMerchants(String accountId) {
		List<Reservation> currentReservations = Reservation
				.findValidReservations(accountId);

		List<ReservationVO> currentReservationVOs = new ArrayList<ReservationVO>();
		ReservationVO reservationVO = null;
		for (Reservation reservation : currentReservations) {
			reservationVO = new ReservationVO();
			reservationVO.build(reservation);
			currentReservationVOs.add(reservationVO);
		}

		List<Merchant> currentMerchants = Merchant
				.findbyReservations(currentReservations);
		List<MerchantVO> currentMerchantVOs = new ArrayList<MerchantVO>();
		for (Merchant merchant : currentMerchants) {
			currentMerchantVOs.add(MerchantVO.build(merchant));

		}

		renderJSON(currentMerchantVOs);
	}

	/**
	 * 
	 * get history merchants by account id
	 * 
	 * @param accountId
	 *            account id
	 */
	public static void getHistoryMerchants(String accountId) {
		List<Reservation> histroyReservations = Reservation
				.findHistroyReservations(accountId);

		List<ReservationVO> histroytReservationVOs = new ArrayList<ReservationVO>();
		ReservationVO reservationVO = null;
		for (Reservation reservation : histroyReservations) {
			reservationVO = new ReservationVO();
			reservationVO.build(reservation);
			histroytReservationVOs.add(reservationVO);
		}

		List<Merchant> histroyMerchants = Merchant
				.findbyReservations(histroyReservations);

		List<MerchantVO> histroyMerchantVOs = new ArrayList<MerchantVO>();
		for (Merchant merchant : histroyMerchants) {
			histroyMerchantVOs.add(MerchantVO.build(merchant));

		}

		renderJSON(histroyMerchantVOs);
	}

	public static void getIntegralCost(String accountId) {

	}

	/**
	 * Get personal info by mobile number or email address
	 * 
	 * @param phone
	 *            the mobile number
	 * @param email
	 *            the email address
	 * @throws Exception
	 */
	public static void getPersonalInfo(String phone, String email)
			throws Exception {
		Account account = null;
		if (null != phone) {
			account = Account.findByPhone(phone);
		} else if (null != email) {
			account = Account.findByEmail(email);
		}

		LoginVO loginVO = new LoginVO();

		if (null == account) {
			loginVO.errorCode = -1;
			loginVO.msg = "account is not exsit";
		} else {
			loginVO.errorCode = 1;
			loginVO.msg = "success";
			loginVO.build(account);
		}

		renderJSON(loginVO);
	}
}
