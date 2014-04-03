package controllers.secure;

import com.withiter.models.account.Account;
import com.withiter.utils.StringUtils;


public class Security {
	
//	static boolean authenticate(String username, String password) {
//	    return true;
//	}
	
	public boolean check(String phone, String email, String password){
		boolean flag = false;
		Account account = null;
		if (StringUtils.isEmpty(phone)) {
			account = Account.findByEmail(email);
		} else {
			account = Account.findByPhone(phone);
		}

		if (account != null) {
			flag = account.validatePassword(password);
		}
		
		return flag;
	}
}
