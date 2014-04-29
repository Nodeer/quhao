package vo.account;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vo.TopMerchantVO;

import com.google.code.morphia.annotations.Indexed;
import com.withiter.common.Constants;
import com.withiter.models.account.Account;
import com.withiter.utils.ExceptionUtil;

public class LoginVO {

	private static Logger logger = LoggerFactory.getLogger(LoginVO.class);

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
	public int guanzhu;

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
		try {
			this.userImage = URLDecoder.decode(account.userImage, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			logger.error(ExceptionUtil.getTrace(e));
		}
		this.enable = account.enable;
		this.mobileOS = account.mobileOS;
		this.lastLogin = account.lastLogin;
		
		this.signIn = account.signIn;
		this.isSignIn = account.isSignIn;
		this.jifen = account.jifen;
		this.dianping = account.dianping;
		this.zhaopian = account.zhaopian;
		this.guanzhu = account.guanzhu;
	}
}
