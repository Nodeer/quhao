package controllers.backend.merchant;

import vo.account.AccountVO;

import com.withiter.models.account.Account;

import controllers.BaseController;

//@With(Secure.class)
public class MerchantManagementController extends BaseController {

	public static void index(){
		renderJapid();
	}
	
	public static void home(String uid){
		Account account = Account.findById(uid);
		renderJapid(AccountVO.build(account));
	}
	
	public static void login(){
		
	}
}
