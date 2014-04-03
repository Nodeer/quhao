package controllers.backend.admin;

import com.withiter.models.account.Account;

import play.data.validation.Required;
import play.libs.Codec;
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
	
	public static void accountGen(@Required String email, @Required String password){
		validation.required(email).message("请输入Email");
		validation.required(password).message("请输入Password");
		
		if(validation.hasErrors()){
			renderHtml(validation.errors().get(0));
		}
		
		// check email exist or not
		Account a = Account.findByEmail(email);
		if(a != null){
			renderHtml("Email已经存在了，请换个其他的Email");
		}
		
		a = new Account();
		a.email = email;
		a.password = Codec.hexSHA1(password);
		a.save();
		
		// TODO 发送验证邮件
		
		String result = "生成成功，email:" + a.email + "  password:" + password;
		
		renderHtml(result);
	}
}
