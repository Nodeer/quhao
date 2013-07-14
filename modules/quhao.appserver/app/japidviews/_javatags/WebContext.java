package japidviews._javatags;

import com.withiter.models.account.Account;

import controllers.rsecure.SecurityHelper;

public class WebContext {
	public static Account user() {
		return SecurityHelper.user();
	}

}
