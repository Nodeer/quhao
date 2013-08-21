package controllers;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import play.Logger;
import vo.MerchantVO;
import vo.PersonalInfoVO;
import vo.ReservationVO;
import vo.account.AccountVO;
import vo.account.LoginVO;

import com.withiter.common.Constants;
import com.withiter.models.account.Account;
import com.withiter.models.account.Reservation;
import com.withiter.models.merchant.Merchant;

public class CopyOfAccountController extends BaseController {

	/**
	 * Account sign up with mobile number or email address
	 * @param phone
	 * @param email
	 * @param password
	 * @param os the type of end user (ANDROID, IOS, WEB)
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
	 * @param phone the mobile number
	 * @param email the email
	 * @param password the password
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
				loginVO.build(account);
			} else {
				loginVO.msg = "fail";
			}
		}
		renderJSON(loginVO);
	}

	public static void logout() {

	}

	/**
	 * Get personal info by mobile number or email address
	 * @param phone the mobile number
	 * @param email the email address
	 * @throws Exception
	 */
	public static void getPersonalInfo(String phone, String email)
			throws Exception {
		PersonalInfoVO personalInfo = new PersonalInfoVO();
		Account account = null;
		if (null != phone) {
			account = Account.findByPhone(phone);
		} else if (null != email) {
			account = Account.findByEmail(email);
		}

		if (null == account) {
			personalInfo.errorCode = -1;
			personalInfo.msg = "account is not exsit";
		}

		LoginVO loginVO = new LoginVO();
		loginVO.build(account);

		List<Reservation> currentReservations = Reservation
				.findValidReservations(String.valueOf(account.getId()));

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

		List<Reservation> histroyReservations = Reservation
				.findHistroyReservations(String.valueOf(account.getId()));

		List<ReservationVO> histroytReservationVOs = new ArrayList<ReservationVO>();
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

		personalInfo.build(loginVO, currentReservationVOs, currentMerchantVOs,
				histroytReservationVOs, histroyMerchantVOs);

		renderJSON(personalInfo);
	}
	
	public static void findAllAccount()
	{
		List<Account> accounts = new ArrayList<Account>();
		LoginVO loginVO = null;
		accounts = Account.findAll();
		List<LoginVO> loginVOs = new ArrayList<LoginVO>();
		for (Account account : accounts)
		{
			loginVO = new LoginVO();
			loginVO.build(account);
			loginVOs.add(loginVO);
		}
		
		renderJSON(loginVOs);
	}
	
	public static void addAccount()
	{
		Account account = Account.findByPhone("13405039857");
		account.email = "wjz628956@126.com";
		account.password = "quhao";
		account.nickname = "大地85号";
		account.birthDay = "Sep 2, 1985 3:40:27 PM";
		account.mobileOS = Constants.MobileOSType.ANDROID;
		
		account.save();
		
		System.out.println("[account.phone:" + account.phone + "],[account.email:" + account.email
				+ "],[account.password:" + account.password
				+ "],[account.jifen:" + account.jifen + "],[account.nickname:"
				+ account.nickname + "],[account.birthDay:" + account.birthDay
				+ "],[account.userImage:" + account.userImage + "],[account.enable:"
				+ account.enable + "],[account.mobileOS:" + account.mobileOS
				+ "],[account.lastLogin:" + account.lastLogin + "]"
				);
		
		AccountVO accountVO = AccountVO.build(account);
		renderJSON(accountVO);
	}
	
}
