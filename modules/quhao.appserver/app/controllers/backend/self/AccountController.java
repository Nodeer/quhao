package controllers.backend.self;

import java.util.Date;
import java.util.List;

import notifiers.MailsController;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import play.Play;
import play.cache.Cache;
import play.data.validation.Required;
import play.libs.Codec;
import play.mvc.Before;
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
import com.withiter.models.merchant.Haoma;
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
	
	private static Logger logger = LoggerFactory.getLogger(AccountController.class);
	
	/**
	 * Interception any caller on this controller, will first invoke this method
	 */
	@Before(only={"logout","updatePwd"})
	static void checkAuthentification() {
		if (!session.contains(Constants.SESSION_USERNAME)) {
			logger.debug("no session is found in Constants.SESSION_USERNAME");
			String randomID = Codec.UUID();
			renderJapidWith("japidviews.LandingController.business", randomID);
		}
	}

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
			
			// update account last login date
			account.lastLogin = new Date();
			account.save();
			avo = MerchantAccountVO.build(account);
			avo.error = "";

			// add merchant list into account view object
			List<Merchant> mList = MerchantAccountRel.getMerchantByUid(avo.uid);
			if (mList == null || mList.isEmpty()) {
			} else {
				for (Merchant m : mList) {
					// 更新商家评价信息
					m.updateEvaluate();
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
	
	/**
	 * 商家提交合作信息
	 * @param captchaCode 验证码
	 * @param randomID 随机数
	 */
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
		
		CooperationRequest c = new CooperationRequest(companyName,peopleName,peopleContact,peopleEmail);
		c.save();
		
		// 商家合作申请发送邮件提醒
//		String subject = "商家合作申请提醒";
//		String href = Play.configuration.getProperty("application.domain")+"/admin";
//		String content = "登陆后台管理查看详细信息<a href='"+href+"'>" + href + "</a>";
		
//		MailsController.sendTo(subject, content, "cross@quhao.la", "service@quhao.la", "admin@quhao.la", "mag_lee@126.com");
		svo.errorKey = "true";
    	svo.errorText = "";
		renderJSON(svo);
	}
	
	/**
	 * 忘记密码
	 */
	public static void forget(){
		String email = params.get("resetEmail");
		CommonVO cvo = new CommonVO();
		if(StringUtils.isEmpty(email)){
			cvo.success = false;
			cvo.key = "false";
			cvo.value = "邮箱不能为空";
			renderJSON(cvo);
		}
		
		MerchantAccount ma = MerchantAccount.findByEmail(email);
		if(ma == null){
			cvo.success = false;
			cvo.key = "false";
			cvo.value = "邮箱不存在，请检查";
			renderJSON(cvo);
		}
		
		String subject = MailsController.SUBJECT_RESET_PASSWORD;
		String hexedUid = Codec.hexSHA1(ma.id());
		String url = Play.configuration.getProperty("application.domain")
				+ "/reset?hid=" + hexedUid
				+ "&oid=" + ma.id();
		String content= "点击下面链接重置您的密码：<br/><br/>"
				+ "<a href='"+url+"'>"+url+"</a><br/>"+"如无法点击，请将链接拷贝到浏览器地址栏中直接访问.";
		MailsController.sendTo(subject, content, ma.email);
		
		cvo.success = true;
		cvo.key = "true";
		cvo.value = "已发送邮件到此邮箱，请登陆邮箱重置密码";
		renderJSON(cvo);
	}
	
	/**
	 * 跳转至重置密码页面
	 */
	public static void reset(){
		String oid = params.get("oid");
		String hid = params.get("hid");
		String hexedUid = Codec.hexSHA1(oid);
		if (hexedUid.equals(hid)) {
			renderJapid(true, oid, hid);
		} else {
			renderJapid(false, oid, hid);
		}
	}
	
	/**
	 * 重设密码
	 */
	public static void resetPassword(){
		String oid = params.get("oid");
		String hid = params.get("hid");
		String password = params.get("password");
		String passwordR = params.get("passwordR");
		CommonVO cvo = new CommonVO();
		if(StringUtils.isEmpty(password) || StringUtils.isEmpty(passwordR)){
			cvo.success = false;
			cvo.key = "false";
			cvo.value = "密码/重复密码不能为空";
			renderJSON(cvo);
		}
		
		if(password.length() < 6 || password.length() > 20){
			cvo.success = false;
			cvo.key = "false";
			cvo.value = "密码长度6-20";
		}
		
		if(!password.equals(passwordR)){
			cvo.success = false;
			cvo.key = "false";
			cvo.value = "两次密码不一致";
			renderJSON(cvo);
		}
		
		String hexedUid = Codec.hexSHA1(oid);
		if (hexedUid.equals(hid)) {
			MerchantAccount account = MerchantAccount.findById(oid);
			account.password = Codec.hexSHA1(password);
			account.save();
			cvo.success = true;
			cvo.key = "true";
			String loginUrl = Play.configuration.getProperty("application.domain")+"/business";
			cvo.value = "密码更改成功！<a href='"+loginUrl+"'>点击登陆</a>";
			renderJSON(cvo);
		} else {
			cvo.success = false;
			cvo.key = "false";
			cvo.value = "密码更改失败，请联系管理员admin@quhao.la";
			renderJSON(cvo);
		}
	}
}
