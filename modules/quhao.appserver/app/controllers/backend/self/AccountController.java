package controllers.backend.self;

import java.util.ArrayList;
import java.util.List;

import notifiers.MailsController;
import play.Play;
import play.libs.Codec;
import play.mvc.Scope.Session;
import vo.MerchantVO;
import vo.account.AccountVO;

import com.withiter.common.Constants;
import com.withiter.models.account.Account;
import com.withiter.models.backendMerchant.MerchantAccountRel;
import com.withiter.models.merchant.Merchant;

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
			
			// add merchant list into account view object
			List<Merchant> mList = MerchantAccountRel.getMerchantByUid(avo.uid);
			if(mList == null || mList.isEmpty()){
				
			}else{
				for(Merchant m : mList){
					avo.mList.add(MerchantVO.build(m));
				}
			}
			
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
			String hexedUid = Codec.hexSHA1(account.id());
			String url = Play.configuration.getProperty("application.domain")+"/b/self/AccountController/active?hid="+hexedUid+"&oid="+account.id();
			try {
				MailsController.sendTo(account.email, url);
			} catch (Exception e) {
				avo.error = "内部错误，请联系管理员";
				e.printStackTrace();
			}
			renderJSON(avo);
		}else{
			AccountVO avo = new AccountVO();
			avo.error = result;
			renderJSON(avo);
		}
	}
	
	public static void active(String oid, String hid){
		String hexedUid = Codec.hexSHA1(oid);
		if(hexedUid.equals(hid)){
			Account account = Account.findById(oid);
			account.enable=true;
			account.save();
			SelfManagementController.index(account.id());
//			renderJapidWith("japidviews.backend.self.SelfManagementController.index", account.id());
		}
		else{
			// TODO update active filed page
//			renderJapidWith("japidviews.backend.self.SelfManagementController.index", "");
		}
			
		
	}
}
