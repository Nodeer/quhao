package controllers.backend.self;

import java.util.Date;
import java.util.List;

import notifiers.MailsController;

import org.apache.commons.lang.StringUtils;

import play.Play;
import play.libs.Codec;
import play.mvc.Scope.Session;
import vo.MerchantVO;
import vo.account.AccountVO;
import vo.account.CommonVO;

import com.withiter.common.Constants;
import com.withiter.models.account.Account;
import com.withiter.models.backendMerchant.MerchantAccountRel;
import com.withiter.models.merchant.Merchant;

import controllers.BaseController;

/**
 * Account Controller for backend merchant
 * 
 * @author CROSS
 * 
 */
//@With(Secure.class)
public class AccountController extends BaseController {

	/**
	 * merchant login function
	 */
	public static void login() {
		String userName = params.get("userName");
		String userPwd = params.get("userPwd");
		String result = Account.validate(userName, userPwd);
		AccountVO avo = new AccountVO();
		if (result != null) {
			avo.error = result;
			renderJSON(avo);
		} else {
			Account account = null;
			if (userName.contains("@")) {
				account = Account.findByEmail(userName);
			} else {
				account = Account.findByPhone(userName);
			}
			
			// update account last login datews
			account.lastLogin = new Date();
			account.save();
			
			avo = AccountVO.build(account);
			avo.error = "";

			// add merchant list into account view object
			List<Merchant> mList = MerchantAccountRel.getMerchantByUid(avo.uid);
			if (mList == null || mList.isEmpty()) {

			} else {
				for (Merchant m : mList) {
					avo.mList.add(MerchantVO.build(m));
				}
			}
			session.put(Constants.SESSION_USERNAME, account.id());
			Session.current().put(account.id(), account.id());
			renderJSON(avo);
		}
	}

	/**
	 * merchant logout function
	 */
	public static void logout() {
		String aid = params.get("aid");
		AccountVO avo = new AccountVO();

		Session.current().remove(aid);
		session.clear();
		renderJSON(avo);
	}

	/**
	 * merchant sign up function
	 */
	public static void signup() {
		String userName = params.get("userName_su");
		String userPwd1 = params.get("userPwd1_su");
		String userPwd2 = params.get("userPwd2_su");

		Account account = new Account();
		String result = account.signupValidate(userName, userPwd1, userPwd2);
		if (result == null) {
			AccountVO avo = AccountVO.build(account);
			avo.error = "";
			String hexedUid = Codec.hexSHA1(account.id());
			String url = Play.configuration.getProperty("application.domain")
					+ "/b/self/AccountController/active?hid=" + hexedUid
					+ "&oid=" + account.id();
			try {
				MailsController.sendTo(account.email, url);
			} catch (Exception e) {
				avo.error = "内部错误，请联系管理员";
				e.printStackTrace();
			}
			renderJSON(avo);
		} else {
			AccountVO avo = new AccountVO();
			avo.error = result;
			renderJSON(avo);
		}
	}

	public static void active(String oid, String hid) {
		String hexedUid = Codec.hexSHA1(oid);
		if (hexedUid.equals(hid)) {
			Account account = Account.findById(oid);
			account.enable = true;
			account.save();
			SelfManagementController.index(account.id());
		} else {
			renderJapidWith("japidviews.backend.self.AccountController.activeFailed");
		}
	}

	/**
	 * validate the old password
	 */
	public static void updatePwd() {
		String uid = params.get("uid");
		String oPwd = params.get("oPwd");
		String nPwd = params.get("nPwd");
		String nPwdR = params.get("nPwdR");
		System.out.println(uid);
		System.out.println(oPwd);
		System.out.println(nPwd);
		System.out.println(nPwdR);

		Account account = Account.findById(uid);
		CommonVO cvo = new CommonVO();
		if (account == null) {
			cvo.success = false;
			cvo.value = "用户不存在";
			renderJSON(cvo);
		}

		boolean flag = account.validatePassword(oPwd);
		if (!flag) {
			cvo.success = false;
			cvo.value = "原始密码不正确";
			renderJSON(cvo);
		}

		if (StringUtils.isEmpty(nPwd) || nPwd.length() < 6
				|| nPwd.length() > 12) {
			cvo.success = false;
			cvo.value = "新密码长度6-12个字符";
			renderJSON(cvo);
		}

		account.updatePassword(account, nPwd);
		cvo.success = true;
		renderJSON(cvo);
	}
}
