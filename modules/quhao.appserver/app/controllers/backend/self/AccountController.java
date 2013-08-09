package controllers.backend.self;

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
		
		String userName = params.get("userName");
		String userPwd = params.get("userPwd");
		
		renderJSON(false);
	}
}
