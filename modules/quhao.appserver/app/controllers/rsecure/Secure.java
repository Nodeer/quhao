package controllers.rsecure;

import java.io.File;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import notifiers.MailsController;
import play.Play;
import play.data.validation.Required;
import play.data.validation.Validation;
import play.libs.Codec;
import play.libs.Crypto;
import play.modules.morphia.Model.MorphiaQuery;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Http.Request;
import vo.account.EditProfilePageVO;
import vo.account.LoginPageVO;
import cn.bran.play.JapidController;

import com.withiter.common.Constants;
import com.withiter.utils.DesUtils;
import com.withiter.models.account.Account;


public class Secure extends JapidController {

//	@Before(unless = { "login", "authenticate", "logout", "resetpwd", "resetPasswordByEmail", "resetPassword", "signup", "signupresult", "resentEmail", "enable", "setPassWord", "enableUser" })
	static void checkAccess() {
		if (!session.contains(Constants.SESSION_USERNAME)) {
			flash.put("url", "GET".equals(request.method) ? request.url : "/");
			if (request.url.startsWith("/home?rid=")) {
				flash.put("commentRid", request.querystring);
			}
			login();
		}
		Check check = getActionAnnotation(Check.class);
		if (check != null) {
			check(check);
		}
		check = getControllerInheritedAnnotation(Check.class);
		if (check != null) {
			check(check);
		}
	}

	public static void check(Check check) {
		for (String profile : check.value()) {
			boolean hasProfile = Security.check(profile);
			if (!hasProfile) {
				Security.onCheckFailed(profile);
			}
		}
	}

	public static void login() {
		Http.Cookie username = request.cookies.get(Constants.COOKIE_USERNAME);
		if (username != null) {
			if (session.contains(Constants.COOKIE_USERNAME)) {
				redirectToOriginalURL();
			}
		}
		flash.keep("url");
		renderJapid();
//		redirect("/");
	}

	public static void authenticate(@Required Account account, String checkpassword) {
		LoginPageVO lpVO = login(account, checkpassword);
		if (lpVO != null) {
			flash.keep("url");
			params.flash(); 	
			renderJSON(lpVO);
			return;
		}else{
			lpVO=new LoginPageVO();
			lpVO.errorText="ok";
		}
		//renderJapidWith("japidviews.rsecure.Secure.login", lpVO);

		Account user = SecurityHelper.user();
		user.lastLogin = new Date();
		user.save();
		renderJSON(lpVO);
		//redirectToOriginalURL();
	}

	public static void logout() {
		Security.onDisconnect();
		session.clear();
		response.removeCookie(Constants.COOKIE_USERNAME);
		response.removeCookie("rememberme");
		Security.onDisconnected();
		redirect("/");
	}

	static void redirectToOriginalURL() {
		Security.onAuthenticated();
		String url = flash.get("url");
		if (url == null) {
			url = "/";
		}
		if (flash.contains("commentRid")) {
			url += "?" + flash.get("commentRid");
		}
		redirect(url);
	}

	/**
	 * Set Password
	 * 
	 * @param code
	 * @param date
	 * @param partnerId
	 */
	public static void enable(String code, String partnerId) {
		Account account = null;
		if (null != code && !"".equals(code)) {
			
		}
		redirect("/home");
	}

	/**
	 * Set Password
	 * 
	 * @param password
	 * @param code
	 * @param date
	 * @param partnerId
	 */
	public static void setPassWord(String password, String code, String date, String partnerId) {
		if (null != code && !"".equals(code)) {
			try {
				DesUtils des = new DesUtils();
				String email = des.decrypt(code);
				Account account = Account.findUserByUserEmail(email);
				if (account != null) {
					account.password = Codec.hexSHA1(password);
					account.saveModel();

					if (account.username != null && !"".equals(account.username)) {
						session.put(Constants.SESSION_USERNAME, account.username);
						response.setCookie(Constants.COOKIE_USERNAME, account.username);
					} else {
						session.put(Constants.SESSION_USERNAME, account.email);
						response.setCookie(Constants.COOKIE_USERNAME, account.email);
					}

					String path = "/AccountController/enableUser?code=" + code + "&date=" + date + "&partnerId=" + partnerId;
					redirect(path);
				}
			} catch (Exception e) {
			}
		}
		renderJapidWith("AccountController/setpassword", code, date, partnerId);
	}


	public static class Security extends Controller {
		static void onAuthenticated() {
		}

		static boolean check(String... profiles) {
			for (String profile : profiles) {
				boolean hasProfile = Security.check(profile);
				if (!hasProfile) {
					return false;
				}
			}
			return true;
		}

		static boolean check(String profile) {
			if (profile.equals(SecurityHelper.CHECK_LOGGEDIN)) {
				if (SecurityHelper.user() != null) {
					return true;
				} else {
					session.remove("userid");
					response.removeCookie("rememberme");
					return false;
				}
			}
			return false;
		}

		static void onCheckFailed(String profile) {
			forbidden();
		}

		static void onDisconnected() {
		}

		static void onDisconnect() {
		}

		static boolean authenticate(String username, String password, boolean remember) {
			Validation.required("login.username", username);
			Validation.required("login.password", password);
			if (!Validation.hasErrors()) {
				MorphiaQuery q = Account.q();
				q.and(q.criteria("password").equal(Codec.hexSHA1(password)), q.or(q.criteria("nickname").equal(username), q.criteria("email").equal(username)));
				Account user = q.first();
				if (user == null) {
					Validation.addError("form", "validation.usernameUnmatchPassword");
				}
				if (Validation.hasErrors()) {
					return false;
				} else {
					session.put("userid", user.id());
					if (remember) {
						response.setCookie("rememberme", Crypto.sign(user.id()) + "-" + user.id(), "30d");
					}
					return true;
				}
			} else {
				return false;
			}
		}

		static boolean isConnected() {
			if (session.contains(Constants.SESSION_USERNAME)) {
				return true;
			} else {
				Http.Cookie remember = Request.current().cookies.get(Constants.COOKIE_USERNAME);
				String username = "";
				if (remember != null) {
					username = remember.value;
				}
				if (remember != null) {
					session.put(Constants.SESSION_USERNAME, username);
					redirectToOriginalURL();
				}
				return session.contains(Constants.SESSION_USERNAME);
				/*
				 * if (remember != null && remember.value.indexOf("-") > 0) {
				 * String sign = remember.value.substring(0,
				 * remember.value.indexOf("-")); String userid =
				 * remember.value.substring(remember.value.indexOf("-") + 1); if
				 * (Crypto.sign(userid).equals(sign)) { session.put("userid",
				 * userid); redirectToOriginalURL(); } }
				 */
			}
		}
	}

	public static LoginPageVO login(Account account, String checkpassword) {
		String errorTip = "invalid";
		LoginPageVO lpVO = new LoginPageVO();
		if (account.email == null || "".equals(account.email)) {
			lpVO.errorField = "email";
			lpVO.errorText = "Please enter a valid e-mail address.";
			// renderJapidWith("japidviews.rsecure.Secure.login", lpVO);
			return lpVO;
		}

		Validation.email(errorTip, account.email);
		if (Validation.hasErrors()) {
			lpVO.errorField = "email";
			lpVO.errorText = "Please enter a valid e-mail address.";
			// renderJapidWith("japidviews.rsecure.Secure.login", lpVO);
			return lpVO;
		}

		Account accountForLogin = new Account();
		String loginResult = null;
		loginResult = accountForLogin.login(account.email, account.password);
		if (loginResult == null) {
			Account a=Account.find("email", account.email).first();
			if(!a.isFinishedOnboarding){
				lpVO.errorText="Registration is not complete";
				return lpVO;
			}
			if (account.username == null || "".equals(account.username)) {
				session.put(Constants.SESSION_USERNAME, account.email);
				response.setCookie(Constants.COOKIE_USERNAME, account.email);
			} else {
				session.put(Constants.SESSION_USERNAME, account.username);
				response.setCookie(Constants.COOKIE_USERNAME, account.username);
			}
			// redirect("/home");
			flash.put("url", "/home");
			return null;
		} else {
			if (loginResult.equals("notenabled")) {
				lpVO.errorField = "notenabled";
				lpVO.errorText = account.email;
//				SignupPageVO spVO = new SignupPageVO();
//				spVO.email = account.email;
//				spVO.enable = PartnerInvitation.isPartnerSignedUp(account.email);
				//renderJapidWith("japidviews.AccountController.signupresult", spVO);
			} else {
				lpVO.errorField = "password";
				lpVO.errorText = "Invalide email address or password.";
			}
			// renderJapidWith("japidviews.rsecure.Secure.login", lpVO);
			return lpVO;
		}
	}
	/**
	 * Get user's profile and redirect to settings page
	 * 
	 * @author Cross
	 */
	public static void settings() {
		
	}
	public static void editProfile(File userImage, Account account, String applicationType) {
		
	}
	public static void showUserImage() {
		String email = session.get(Constants.SESSION_USERNAME);
		Account currentAccount = Account.findUserByUserEmail(email);
		InputStream is = Account.getUserImage(currentAccount);
		renderBinary(is);
	}

	public static void showUserImageForEmail(String email) {
		// TXC
		// System.out.println(email);
		Account currentAccount = Account.findUserByUserEmail(email);
		InputStream is = Account.getUserImage(currentAccount);
		renderBinary(is);
	}
	
	/**
	 * @author Cross
	 * @param oldpassword
	 * @param newpassword
	 * @param newpasswordrep
	 */
	public static void updatePassword(String oldpassword, String newpassword, String newpasswordrep) {
		EditProfilePageVO eppVO = new EditProfilePageVO();

		String email = session.get(Constants.SESSION_USERNAME);
		Account account = Account.findUserByUserEmail(email);

		if (!Account.validatePassword(account, oldpassword)) {
			eppVO.errorKey = "old";
			eppVO.errorText = "Your old password is incorrect!";
			renderJSON(eppVO);
			return;
		}

		Validation.range("newpassword", newpassword.length(), 6, 20);
		if (Validation.hasErrors()) {
			eppVO.errorKey = "new";
			eppVO.errorText = "New password length should be between 6 and 20 chars";
			renderJSON(eppVO);
			return;
		}

		Account.updatePassword(account, newpassword);

		eppVO.errorKey = "success";
		eppVO.errorText = "Update password successfully";
		renderJSON(eppVO);
	}
	
	public static void disconnectFB(String objId){
		
	}
}

