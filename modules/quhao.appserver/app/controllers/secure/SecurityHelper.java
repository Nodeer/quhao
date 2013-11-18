package controllers.secure;

import play.mvc.Scope.RenderArgs;
import play.mvc.Scope.Session;

import com.withiter.models.account.Account;

public class SecurityHelper {

	public static final String CHECK_LOGGEDIN = "loggedIn";

	public static Account user() {
		Account a = (Account) RenderArgs.current().get("user");
		if (a == null && Secure.Security.isConnected()) {
			a = (Account) Account.findById(Session.current().get("userid"));
			if (a != null) {
				RenderArgs.current().put("user", a);
			}
		}
		return a;
	}

	public static boolean check(String... profile) {
		return Secure.Security.check(profile);
	}

}
