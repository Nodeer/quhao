package controllers.backend.self;

import java.util.Date;
import java.util.List;

import notifiers.MailsController;

import org.apache.commons.lang.StringUtils;

import play.Play;
import play.cache.Cache;
import play.data.validation.Required;
import play.libs.Codec;
import play.mvc.Scope.Session;
import vo.MerchantVO;
import vo.account.CommonVO;
import vo.account.MerchantAccountVO;
import vo.account.SignupVO;

import com.withiter.common.Constants;
import com.withiter.models.account.Account;
import com.withiter.models.account.CooperationRequest;
import com.withiter.models.admin.MerchantAccount;
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
		String email = params.get("userName");
		String password = params.get("userPwd");
		String result = MerchantAccount.validate(email, password);
		MerchantAccountVO avo = new MerchantAccountVO();
		if (result != null) {
			avo.error = result;
			renderJSON(avo);
		} else {
			MerchantAccount account = MerchantAccount.findByEmail(email);
			
			// update account last login datews
			account.lastLogin = new Date();
			account.save();
			
			avo = MerchantAccountVO.build(account);
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
		MerchantAccountVO avo = new MerchantAccountVO();

		Session.current().remove(aid);
		session.clear();
		renderJSON(avo);
	}

	/**
	 * merchant sign up function
	 */
	public static void signup() {
		
	}

	public static void active(String oid, String hid) {
		String hexedUid = Codec.hexSHA1(oid);
		if (hexedUid.equals(hid)) {
			MerchantAccount account = MerchantAccount.findById(oid);
			account.enable = true;
			account.save();
			renderJapidWith("japidviews.backend.self.AccountController.result", true);
		} else {
			renderJapidWith("japidviews.backend.self.AccountController.result", false);
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

		MerchantAccount account = MerchantAccount.findById(uid);
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
	
	// 商家提交合作信息
	public static void submitinfo(@Required(message="请输入验证码") String captchaCode, String randomID){
		String companyName = params.get("companyName");
		String peopleName = params.get("peopleName");
		String peopleContact = params.get("peopleContact");
		String peopleEmail = params.get("peopleEmail");
		
		SignupVO svo = new SignupVO();
		if(!captchaCode.equalsIgnoreCase(Cache.get(randomID).toString())){
			svo.errorKey = "false";
			svo.errorText = "验证码不正确，请重试";
			renderJSON(svo);
		}
		
		if(StringUtils.isEmpty(companyName) || StringUtils.isEmpty(peopleName) || StringUtils.isEmpty(peopleContact) || StringUtils.isEmpty(peopleEmail)){
			svo.errorKey = "false";
	    	svo.errorText = "字段不能为空";
			renderJSON(false);
		}
		
		CooperationRequest c = new CooperationRequest(companyName,peopleName,peopleContact,peopleContact);
		c.save();
		String subject = "商家合作申请提醒";
		String href = Play.configuration.getProperty("application.domain")+"/admin";
		String content = "登陆后台管理查看详细信息<a href='"+href+"'>" + href + "</a>";
		
		MailsController.sendTo(subject, content, "cross@quhao.la", "service@quhao.la", "admin@quhao.la", "mag_lee@126.com");
		svo.errorKey = "true";
    	svo.errorText = "";
		renderJSON(svo);
	}
}
