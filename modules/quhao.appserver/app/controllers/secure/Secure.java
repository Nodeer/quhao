package controllers.secure;

import japidviews._javatags.I18nKeys;
import play.data.validation.Required;
import play.data.validation.Validation;
import play.i18n.Messages;
import play.libs.Codec;
import play.libs.Crypto;
import play.modules.morphia.Model.MorphiaQuery;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Http.Request;
import cn.bran.play.JapidController;

import com.withiter.common.Constants;
import com.withiter.models.account.Account;

import controllers.backend.self.AccountController;

public class Secure extends JapidController {

	@Before(unless = { "login", "authenticate", "logout" })
	static void checkAccess() {
		if (!session.contains(Constants.SESSION_USERNAME)) {
			flash.put("url", "GET".equals(request.method) ? request.url : "/");
			login();
		}
//		Check check = getActionAnnotation(Check.class);
//		if (check != null) {
//			check(check);
//		}
//		check = getControllerInheritedAnnotation(Check.class);
//		if (check != null) {
//			check(check);
//		}
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
		renderJapidWith("japidviews.backend.merchant.MerchantManagementController.index");
//		Http.Cookie remember = request.cookies.get("rememberme");
//		if (remember != null && remember.value.indexOf("-") > 0) {
//			String sign = remember.value.substring(0,
//					remember.value.indexOf("-"));
//			String userid = remember.value.substring(remember.value
//					.indexOf("-") + 1);
//			if (Crypto.sign(userid).equals(sign)) {
//				session.put("userid", userid);
//				redirectToOriginalURL();
//			}
//		}
//		flash.keep("url");
//		renderJapid("");
	}

	public static void authenticate(@Required String email, String password,
			boolean remember) {
		Boolean allowed = (Boolean) Security.authenticate(email, password,
				remember);
		if (Validation.hasErrors() || !allowed) {
			flash.keep("url");
			params.flash();
			renderJapidWith("japidviews.rsecure.Secure.login", email);
			return;
		}
		redirectToOriginalURL();
	}

	public static void logout() {
		Security.onDisconnect();
		session.clear();
		response.removeCookie("rememberme");
		Security.onDisconnected();
		flash.success("secure.logout");
		redirect("/");
	}

	static void redirectToOriginalURL() {
//		Security.onAuthenticated();
		String url = flash.get("url");
		if (url == null) {
			url = "/";
		}
		redirect(url);
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

		static boolean authenticate(String email, String password,
				boolean remember) {
			Validation.required(Messages.get(I18nKeys.F_EMAIL), email);
			Validation.required(Messages.get(I18nKeys.F_PASSWORD), password);
			if (!Validation.hasErrors()) {
				MorphiaQuery q = Account.q();
				q.and(q.criteria("password").equal(Codec.hexSHA1(password)), q
						.criteria("email").equal(email));
				Account user = q.first();
				if (user == null) {
					Validation.addError("form",
							"validation.usernameUnmatchPassword");
				}
				if (Validation.hasErrors()) {
					return false;
				} else {
					session.put("userid", user.id());
					if (remember) {
						response.setCookie("rememberme", Crypto.sign(user.id())
								+ "-" + user.id(), "30d");
					}
					return true;
				}
			} else {
				return false;
			}
		}

		static boolean isConnected() {
			if (session.contains("userid")) {
				return true;
			} else {
				Http.Cookie remember = Request.current().cookies
						.get("rememberme");
				if (remember != null && remember.value.indexOf("-") > 0) {
					String sign = remember.value.substring(0,
							remember.value.indexOf("-"));
					String userid = remember.value.substring(remember.value
							.indexOf("-") + 1);
					if (Crypto.sign(userid).equals(sign)) {
						session.put("userid", userid);
						redirectToOriginalURL();
					}
				}
				return session.contains("userid");
			}
		}
	}
}
