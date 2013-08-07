package controllers.backend.self;

import controllers.BaseController;

public class AccountController extends BaseController {

	public static void test(){
		renderJSON("aaa");
	}
	
	public static void login(){
		System.out.println(params.allSimple());
		renderJSON(false);
	}
}
