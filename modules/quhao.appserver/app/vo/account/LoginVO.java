package vo.account;

import java.util.Date;

import com.google.code.morphia.annotations.Indexed;
import com.withiter.common.Constants;
import com.withiter.models.account.Account;

public class LoginVO {

	public int errorCode;
	
	public String msg;
	public String accountId;
	public String phone = "";
	public String email = "";
	public String password = "";
	public String nickname = "";

	public String birthDay = "";
	public String userImage = "";
	public boolean enable = false;
	public Constants.MobileOSType mobileOS;
	public Date lastLogin = null;
	
	public int jifen;
	public int signIn;
	public boolean isSignIn = false;
	public int dianping;
	public int zhaopian;
	
	public LoginVO(){
		super();
	}
	
	public LoginVO(String msg) {
		super();
		this.msg = msg;
	}
	
	public void build(Account account){
		this.accountId = account.id();
		this.phone = account.phone;
		this.email = account.email;
		this.password = account.password;
		this.nickname = account.nickname;
		this.birthDay = account.birthDay;
		this.userImage = account.userImage;
		this.enable = account.enable;
		this.mobileOS = account.mobileOS;
		this.lastLogin = account.lastLogin;
		
		this.signIn = account.signIn;
		this.isSignIn = account.isSignIn;
		this.jifen = account.jifen;
		this.dianping = account.dianping;
		this.zhaopian = account.zhaopian;
	}
}
