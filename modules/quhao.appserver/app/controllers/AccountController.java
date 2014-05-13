package controllers;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.httpclient.HttpException;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import play.Play;
import play.libs.Codec;
import play.libs.Images;
import play.modules.morphia.Model.MorphiaQuery;
import play.modules.morphia.Model.MorphiaUpdateOperations;
import vo.ReservationVO;
import vo.UserAgreementVO;
import vo.account.CreditVO;
import vo.account.LoginVO;
import vo.account.SignupVO;

import com.google.code.morphia.query.UpdateResults;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSInputFile;
import com.withiter.common.Constants;
import com.withiter.common.Constants.CreditStatus;
import com.withiter.common.sms.business.SMSBusiness;
import com.withiter.models.account.Account;
import com.withiter.models.account.Credit;
import com.withiter.models.account.Reservation;
import com.withiter.models.merchant.Attention;
import com.withiter.models.merchant.Comment;
import com.withiter.models.merchant.Merchant;
import com.withiter.utils.StringUtils;

public class AccountController extends BaseController {

	private static Logger logger = LoggerFactory.getLogger(controllers.AccountController.class);
	private static String USER_IMAGE = "UserImage";

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
		SignupVO suVO = new SignupVO();
		suVO.errorKey = "mobile";
		if (StringUtils.isEmpty(mobile)) {
			suVO.errorText = "号码不能为空";
			renderJSON(suVO);
		}
		Account account = Account.findExistsAccount(mobile);
		if (account != null) {
			suVO.errorText = "此号码已注册";
			renderJSON(suVO);
		}
		account =Account.findByPhone(mobile);
		if (account == null) {
			account = new Account();
			account.phone = mobile;
		}
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
				account.authcode = String.valueOf(result);
				account.authDate = new Date();
				account.save();
				suVO.errorKey = "";
				suVO.errorText = "验证码24小时之内有效";
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
	 * 忘记密码时获取6位数字验证码
	 * 
	 * @param mobile
	 *            手机号码
	 * 
	 *            返回JSON SignupVO
	 *            返回SignupVO对象，需对errorKey进行判断，如果不是空字符串，则表示生成失败，否则生成成功。
	 */
	public static void getAuthCode(String mobile) {
		SignupVO suVO = new SignupVO();
		suVO.errorKey = "mobile";
		if (StringUtils.isEmpty(mobile)) {
			suVO.errorText = "号码不能为空";
			renderJSON(suVO);
		}
		Account account = Account.findByPhone(mobile);
		if (account == null) {
			suVO.errorText = "此号码还没注册";
			renderJSON(suVO);
		}
		try {
			int result = SMSBusiness.sendAuthCodeForSignup(mobile);
			if (result == 0) {
				suVO.errorText = "发送短信出错";
				renderJSON(suVO);
			} else {
				account.authcode = String.valueOf(result);
				account.authDate = new Date();
				account.save();
				suVO.errorKey = "";
				suVO.errorText = "验证码24小时之内有效";
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
	 * 忘记密码
	 * 
	 * @param mobile
	 *            手机号码
	 * @param code
	 *            验证码
	 * @param password
	 *            密码 返回JSON SignupVO
	 *            返回SignupVO对象，需对errorKey进行判断，如果不是空字符串，则表示生成失败，否则生成成功。
	 */
	public static void updatePassCode(String mobile, String code, String password) {
		SignupVO suVO = new SignupVO();
		suVO.errorKey = "mobile";
		if (StringUtils.isEmpty(mobile)) {
			suVO.errorKey = "0";
			suVO.errorText = "手机号码不能为空";
			renderJSON(suVO);
		}
		if (StringUtils.isEmpty(code)) {
			suVO.errorKey = "0";
			suVO.errorText = "验证码不能为空";
			renderJSON(suVO);
		}

		Account account = Account.findAccount(mobile, code);
		if (account == null) {
			suVO.errorKey = "0";
			suVO.errorText = "验证码错误或者已过期";
			renderJSON(suVO);
		} else {
			account.password = Codec.hexSHA1(String.valueOf(password));
			account.authcode = "";
			account.authDate = null;
			account.save();
			suVO.errorKey = "1";
			suVO.errorText = "修改成功";
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
	public static void signupWithMobile(String mobile, String code, String password, String os) {
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
		account = Account.findExistsAccount(mobile);
		if (account != null) {
			suVO.errorText = "此号码已注册";
			renderJSON(suVO);
		}
		account = Account.findAccount(mobile, code);
		if (account == null) {
			suVO.errorText = "验证码错误或者已过期";
			renderJSON(suVO);
		}
		account.password = Codec.hexSHA1(String.valueOf(password));
		account.authcode = "";
		account.authDate = null;
		account.enable = true;
		account.nickname = "qh"+mobile;
		account.save();
		suVO.errorKey = "1";
		suVO.errorText = "注册成功";
		renderJSON(suVO);
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
	public static void signup(String phone, String email, String password, String os) {
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

		logger.debug(result);

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
				
				int count = Comment.getCommentCountByAccountId(account.id());
				loginVO.dianping = count;
				
				session.put(Constants.SESSION_USERNAME, account.id());
				session.put(account.id(), account.id());

				logger.debug(session.getId());
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
	public static void queryByAccountId(String accountId) {
		LoginVO loginVO = new LoginVO();
		Account account = Account.findById(accountId);

		if (account != null) {
			loginVO.msg = "success";
			loginVO.errorCode = 0;
			loginVO.build(account);
			int count = Comment.getCommentCountByAccountId(account.id());
			loginVO.dianping = count;
			
			session.put(Constants.SESSION_USERNAME, account.id());
			session.put(account.id(), account.id());

			logger.debug(session.getId());
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
	public static void signIn(String accountId) {
		int exchangePer = Integer.parseInt(Play.configuration.getProperty("credit.exchange.jifen"));
		Account account = null;
		if (null != accountId) {
			account = Account.findById(accountId);
		}

		LoginVO loginVO = new LoginVO();

		if (null == account) {
			loginVO.errorCode = -1;
			loginVO.msg = "account is not exsit";
		} else if (!account.isSignIn) {
			account.signIn = account.signIn + 1;
			account.isSignIn = true;
			int count = Comment.getCommentCountByAccountId(account.id());
			loginVO.dianping = count;
			session.put(Constants.SESSION_USERNAME, account.id());
			session.put(account.id(), account.id());
			if(account.signIn%5==0){
				account.jifen=account.jifen+exchangePer;
				// 增加积分消费
				Credit credit = new Credit();
				credit.accountId = accountId;
				credit.merchantId = "";
				credit.reservationId = "";
				credit.cost = true;
				credit.jifen=exchangePer;
				credit.status = CreditStatus.exchange;
				credit.created = new Date();
				credit.modified = new Date();
				credit.save();

			}
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
	public static void getCurrentMerchants(String accountId,int page,String sortBy, double userX, double userY) {
		List<Reservation> currentReservations = Reservation.findValidReservations(accountId,page,sortBy);

		List<ReservationVO> currentReservationVOs = new ArrayList<ReservationVO>();
		ReservationVO reservationVO = null;
		Merchant merchant = null;
		for (Reservation reservation : currentReservations) {
			reservationVO = new ReservationVO();
			merchant = Merchant.findById(reservation.merchantId);
			reservationVO.merchantName = merchant.name;
			reservationVO.merchantAddress = merchant.address;
			// 添加检查优惠时间
			int checkTime = Integer.parseInt(Play.configuration.getProperty("cancelNumber.checkTime"));
			reservationVO.promptYouhuiTime = checkTime;
			try {
				reservationVO.merchantImage = URLDecoder.decode(merchant.merchantImage, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			reservationVO.build(reservation, merchant.x, merchant.y, userX, userY);
			currentReservationVOs.add(reservationVO);
		}

		renderJSON(currentReservationVOs);
	}

	/**
	 * 
	 * get history merchants by account id
	 * 
	 * @param accountId
	 *            account id
	 */
	public static void getHistoryMerchants(String accountId,String sortBy) {
		List<Reservation> histroyReservations = Reservation.findHistroyReservationsNew(accountId,sortBy);

		List<ReservationVO> histroytReservationVOs = new ArrayList<ReservationVO>();
		ReservationVO reservationVO = null;
		Merchant merchant = null;
		for (Reservation reservation : histroyReservations) {
			reservationVO = new ReservationVO();
			merchant = Merchant.findById(reservation.merchantId);
			reservationVO.merchantName = merchant.name;
			reservationVO.merchantAddress = merchant.address;
			try {
				reservationVO.merchantImage = URLDecoder.decode(merchant.merchantImage, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			reservationVO.build(reservation);
			histroytReservationVOs.add(reservationVO);
		}

		/*
		 * List<Merchant> histroyMerchants = Merchant
		 * .findbyReservations(histroyReservations);
		 * 
		 * List<MerchantVO> histroyMerchantVOs = new ArrayList<MerchantVO>();
		 * for (Merchant merchant : histroyMerchants) {
		 * histroyMerchantVOs.add(MerchantVO.build(merchant));
		 * 
		 * }
		 */
		renderJSON(histroytReservationVOs);
	}

	/**
	 * 根据帐号ID查找积分消费情况
	 * 
	 * @param accountId
	 *            帐号ID
	 */
	public static void getCreditCost(String accountId,String sortBy) {

		
		List<CreditVO> creditVOs = new ArrayList<CreditVO>();
		if (StringUtils.isEmpty(accountId)) {
			renderJSON(creditVOs);
			return;
		}

		List<Credit> credits = Credit.findByAccountId(accountId,sortBy);
		CreditVO creditVO = null;
		for (Credit credit : credits) {
			creditVO = new CreditVO();
			creditVO.build(credit);
			if (StringUtils.isNotEmpty(credit.merchantId)) {
				Merchant merchant = Merchant.findById(credit.merchantId);
				creditVO.merchantName = merchant.name;
				creditVO.merchantAddress = merchant.address;
			}

//			if (StringUtils.isNotEmpty(credit.reservationId)) {
//				Reservation reservation = Reservation.findById(credit.reservationId);
//				creditVO.seatNumber = reservation.seatNumber;
//				creditVO.myNumber = reservation.myNumber;
//			}

			creditVOs.add(creditVO);
		}

		renderJSON(creditVOs);
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
	public static void getPersonalInfo(String phone, String email) throws Exception {
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
			
			int count = Comment.getCommentCountByAccountId(account.id());
			loginVO.dianping = count;
			
		}

		renderJSON(loginVO);
	}
	
	/**
	 * 逻辑删除历史取号信息
	 * @param id
	 */
	public static void delHistoryReservation(String id) {
		if(!id.equals("")){
			String[] array = id.split(",");
			int i;
			ArrayList ids = new ArrayList();
			for(i = 0; i< array.length; i++){
				if(!array[i].equals("")){
					ids.add(new ObjectId(array[i]));
				}
			}
			MorphiaQuery q = Reservation.q();
			q.filter(" _id in", ids);
			
			MorphiaUpdateOperations o = Reservation.o();
			o.set("available", false);
			o.update(q);
			
			renderText("success");
		}else{
			renderText("error");
		}
	}
	
	/**
	 * 逻辑删除积分消费情况的历史信息
	 * @param id
	 */
	public static void delHistoryCredit(String id) {
		if(!id.equals("")){
			String[] array = id.split(",");
			int i;
			ArrayList ids = new ArrayList();
			for(i = 0; i< array.length; i++){
				if(!array[i].equals("")){
					ids.add(new ObjectId(array[i]));
				}
			}
			MorphiaQuery q = Credit.q();
			q.filter(" _id in", ids);
			
			MorphiaUpdateOperations o = Credit.o();
			o.set("available", false);
			o.update(q);
			
			renderText("success");
		}else{
			renderText("error");
		}
	}
	
	/**
	 *  用户更改头像
	 */
	public static void updateUserImage()
	{
		String accountId = params.get("accountId");
		if(!StringUtils.isEmpty(accountId)){
			String userImage = params.get("userImage");

			if (!StringUtils.isEmpty(userImage)) {
				GridFSInputFile file = uploadFirst(userImage, accountId);
				if (file != null) {
					if (!StringUtils.isEmpty(accountId)) {
						String imageStorePath = Play.configuration.getProperty("imageUser.store.path");
						try {							
							MorphiaUpdateOperations o = Account.o();
							o.set("userImage", URLEncoder.encode(imageStorePath + file.getFilename(), "UTF-8"));
							o.update("_id", new ObjectId(accountId));
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
						renderText("success");
					}
				}
			}
		}
	    renderText("error");
	}
	
	private static GridFSInputFile uploadFirst(String param, String aid) {
		GridFSInputFile gfsFile = null;
		File[] files = params.get(param, File[].class);
		for (File file : files) {
			try {
				gfsFile = UploadController.saveBinaryForUser(file, aid);
				break;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (gfsFile == null) {
			return null;
		} else {
			return gfsFile;
		}
	}
	
	public static void updateUserName(String accoutId, String name) {
		SignupVO suVO = new SignupVO();
		if (StringUtils.isEmpty(accoutId)) {
			suVO.errorKey = "0";
			suVO.errorText = "账号不能为空";
			renderJSON(suVO);
		}
		if (StringUtils.isEmpty(name)) {
			suVO.errorKey = "0";
			suVO.errorText = "用户名不能为空";
			renderJSON(suVO);
		}

		Account account = Account.isExistsName(accoutId,name);
		if (account != null) {
			suVO.errorKey = "2";
			suVO.errorText = "用户名已被占用";
			renderJSON(suVO);
		} 
		
		MorphiaUpdateOperations o = Account.o();
		o.set("nickname", name);
		MorphiaQuery q = Account.createQuery();
		q.filter("_id", new ObjectId(accoutId));
	    UpdateResults<Account> result = o.update(q);
		if(result.getUpdatedCount()!=0){
			suVO.errorKey = "1";
			suVO.errorText = "修改成功";
			renderJSON(suVO);
		}else{
			suVO.errorKey = "0";
			suVO.errorText = "修改失败";
			renderJSON(suVO);
		}
	}
	
	/**
	 * 修改密码
	 * @param mobile
	 * @param code
	 * @param password
	 */
	public static void updatePassword(String accoutId, String newPassWord, String oldPass) {
		SignupVO suVO = new SignupVO();
		if (StringUtils.isEmpty(accoutId)) {
			suVO.errorKey = "0";
			suVO.errorText = "账号不能为空";
			renderJSON(suVO);
		}
		if (StringUtils.isEmpty(newPassWord)) {
			suVO.errorKey = "0";
			suVO.errorText = "新密码不能为空";
			renderJSON(suVO);
		}
		Account account = Account.validatePassword(accoutId, oldPass);
		if(account == null){
			suVO.errorKey = "0";
			suVO.errorText = "旧密码不正确";
			renderJSON(suVO);
		}else{
			account.password = Codec.hexSHA1(String.valueOf(newPassWord));
			account.save();
			suVO.errorKey = "1";
			suVO.errorText = "修改成功";
			renderJSON(suVO);
		}
	}
	
	public static void getUserAgreement()
	{
		UserAgreementVO vo = new UserAgreementVO();
		renderJSON(vo);
	}
	
}
