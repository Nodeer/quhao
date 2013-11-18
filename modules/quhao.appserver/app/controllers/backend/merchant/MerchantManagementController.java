package controllers.backend.merchant;

import play.mvc.With;
import vo.account.AccountVO;

import com.withiter.models.account.Account;

import controllers.BaseController;
import controllers.secure.Secure;

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
