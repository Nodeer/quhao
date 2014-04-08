package controllers.backend.admin;

import notifiers.MailsController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import play.Play;
import play.data.validation.Required;
import play.libs.Codec;
import play.mvc.Before;
import vo.AdminVO;

import com.withiter.common.Constants;
import com.withiter.models.account.Account;

import controllers.BaseController;

public class AdminController extends BaseController {
	
	private static Logger logger = LoggerFactory.getLogger(AdminController.class);

	/**
	 * Interception any caller on this controller, will first invoke this method
	 */
	@Before(unless={"index","login"})
	static void checkAuthentification() {
		if (!session.contains(Constants.ADMIN_SESSION_USERNAME)) {
			logger.debug("no session is found in Constants.ADMIN_SESSION_USERNAME");
			renderJapidWith("japidviews.backend.admin.AdminController.index");
		}
	}
	
	public static void index(){
		renderJapid();
	}
	
	public static void login(@Required String email, @Required String password){
		validation.required(email).message("请输入Email");
		validation.required(password).message("请输入Password");
		
		if(validation.hasErrors()){
			renderHtml(validation.errors().get(0));
		}

		validation.equals(email, "admin@quhao.la").message("请输入正确的Email");
		validation.equals(password, "Group@withiter").message("请输入正确的Password");
		
		if(validation.hasErrors()){
			renderHtml(validation.errors().get(0));
		}
		
		session.put(Constants.ADMIN_SESSION_USERNAME, email);
		
		home();
	}
	
	
	public static void home(){
		renderJapid();
	}
	
	public static void genAccount(String email, String password){
		validation.required(email).message("请输入Email");
		validation.required(password).message("请输入Password");
		
		
		validation.email(email).message("请输入正确格式的Email");
		validation.minSize(password, 6).message("密码长度为6-20");
		validation.maxSize(password, 20).message("密码长度为6-20");
		
		AdminVO avo = new AdminVO();
		
		if(validation.hasErrors()){
			avo.error = validation.errors().get(0).toString();
			avo.result = validation.errors().get(0).toString();
			renderJSON(avo);
		}
		
		// check email exist or not
		Account a = Account.findByEmail(email);
		if(a != null){
			avo.error = "Email已经存在了，请换个其他的Email";
			avo.result = "Email已经存在了，请换个其他的Email";
			renderJSON(avo);
		}
		
		a = new Account();
		a.email = email;
		a.password = Codec.hexSHA1(password);
		a.save();
		
		String hexedUid = Codec.hexSHA1(a.id());
		String url = Play.configuration.getProperty("application.domain")
				+ "/active?hid=" + hexedUid
				+ "&oid=" + a.id();
		
		MailsController.sendTo(a.email, url);
		
		avo.error = "";
		avo.result = "生成成功<br/>email:" + a.email + "<br/>password:" + password;
		
		renderJSON(avo);
	}
}
