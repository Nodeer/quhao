package controllers.backend.self;

import notifiers.MailsController;
import play.mvc.Scope.Session;
import vo.account.AccountVO;

import cn.bran.japid.util.StringUtils;

import com.withiter.common.Constants;
import com.withiter.models.account.Account;

import controllers.BaseController;

public class AccountController extends BaseController {

	public static void test(){
		renderJSON("aaa");
	}
	
	/**
	 * merchant login function
	 */
	public static void login(){
		String userName = params.get("userName");
		String userPwd = params.get("userPwd");
		String result = Account.validate(userName, userPwd);
		AccountVO avo = new AccountVO();
		if(result != null){
			avo.error = result;
			renderJSON(avo);
		}else{
			Account account = null;
			if(userName.contains("@")){
				account = Account.findByEmail(userName);
			}else{
				account = Account.findByPhone(userName);
			}
			avo = AccountVO.build(account);
			avo.error = "";
			Session.current().put(Constants.SESSION_USERNAME, account);
			renderJSON(avo);
		}
	}
	
	/**
	 * merchant sign up function
	 */
	public static void signup(){
		String userName = params.get("userName_su");
		String userPwd1 = params.get("userPwd1_su");
		String userPwd2 = params.get("userPwd2_su");
		
		Account account = new Account();
		String result = account.signupValidate(userName, userPwd1, userPwd2);
		if(result == null){
			AccountVO avo = AccountVO.build(account);
			avo.error = "";
			MailsController.sendTo("withiter@126.com");
//			MailsController.sendBySignUp("withiter@126.com");
			renderJSON(avo);
		}else{
			AccountVO avo = new AccountVO();
			avo.error = result;
			renderJSON(avo);
		}
	}
}
