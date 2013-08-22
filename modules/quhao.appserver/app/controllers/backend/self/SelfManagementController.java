package controllers.backend.self;

import vo.BackendMerchantInfoVO;

import com.withiter.models.account.Account;
import com.withiter.models.merchant.Merchant;

import controllers.BaseController;

public class SelfManagementController extends BaseController {
/*
 * 1) account information(included information:email or phone...) 
 * 2) Merchant information(included information:name, address...)
 * 
 * 
 * 
 * */
	public static void index(String uid){
		Account account = Account.findById(uid);
		Merchant merchant = Merchant.findById(uid);
		BackendMerchantInfoVO bmivo = BackendMerchantInfoVO.build(merchant, account);
		renderJapid(bmivo);
	}
	
}
