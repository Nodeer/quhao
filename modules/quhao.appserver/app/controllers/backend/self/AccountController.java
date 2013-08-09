package controllers.backend.self;

import com.withiter.models.account.Account;

import cn.bran.japid.util.StringUtils;
import controllers.BaseController;

public class AccountController extends BaseController {

	public static void test(){
		renderJSON("aaa");
	}
	
	public static void login(){
		System.out.println(params.allSimple());
		
		String userName = params.get("userName");
		String userPwd = params.get("userPwd");
		
		renderJSON(false);
	}
	
	public static void signup(){
		System.out.println(params.allSimple());
		
		String userName = params.get("userName_su");
		String userPwd1 = params.get("userPwd1_su");
		String userPwd2 = params.get("userPwd2_su");
		
		Account account = new Account();
		boolean flag = account.signupValidate(userName, userPwd1, userPwd2);
		if(flag){
			renderJapidWith("japidviews.backend.self.SelfManagementController.home",account);
		}
		renderJSON(false);
	}
}
