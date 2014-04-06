package controllers.backend.admin;

import notifiers.MailsController;
import play.Play;
import play.data.validation.Required;
import play.libs.Codec;
import vo.AdminVO;

import com.withiter.models.account.Account;

import controllers.BaseController;

public class AdminController extends BaseController {

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
