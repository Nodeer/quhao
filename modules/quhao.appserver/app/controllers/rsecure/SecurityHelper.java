package controllers.rsecure;

import play.mvc.Scope.RenderArgs;
import play.mvc.Scope.Session;

import com.withiter.common.Constants;
import com.withiter.models.account.Account;

public class SecurityHelper {

	public static final String	CHECK_LOGGEDIN	= "loggedIn";

	public static Account user() {
		Account a = (Account) RenderArgs.current().get("user");
		if (a == null && Secure.Security.isConnected()) {
			a = Account.findByEmail(Session.current().get(Constants.SESSION_USERNAME));
			if (a != null) {
				RenderArgs.current().put("user", a);
			}
		}
		return a;
	}

	public static boolean check(String... profile) {
		return Secure.Security.check(profile);
	}
	
	public static void checkAccess(){
		Secure.checkAccess();
	}

}
