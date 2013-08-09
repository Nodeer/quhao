package vo.account;

import java.util.Date;

import com.withiter.common.Constants;
import com.withiter.common.Constants.MobileOSType;
import com.withiter.models.account.Account;

public class AccountVO {

	public String phone = "";
	public String email = "";
	public String password = "";
	public String nickname = "";

	public int jifen = 0;
	public String birthDay = "";
	public String userImage = "";
	public boolean enable = false;
	public String mobileOS;
	public Date lastLogin;
	
	public static AccountVO build(Account account) {
		AccountVO avo = new AccountVO();
		
		avo.phone = account.phone;
		avo.email = account.email;
		avo.nickname = account.nickname;
		avo.jifen = account.jifen;
		avo.birthDay = account.birthDay;
		avo.userImage = account.userImage;
		avo.enable = account.enable;
		avo.mobileOS = (account.mobileOS != null) ? account.mobileOS.toString() : MobileOSType.WEB.toString();
		avo.lastLogin = account.lastLogin;
		
		return avo;
	}
}
