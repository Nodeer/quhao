package controllers;

import play.Logger;
import vo.account.LoginVO;
import cn.bran.japid.util.StringUtils;

import com.withiter.common.Constants;
import com.withiter.models.account.Account;

public class AccountController extends BaseController {
	
	public static void signup(String phone, String email, String password, String os){
		Account account = new Account();
		account.password = password;
		if(!StringUtils.isEmpty(phone)){
			account.phone = phone;
			account.email = "";
		}
		if(!StringUtils.isEmpty(email)){
			account.email = email;
			account.phone = "";
		}
		
		if(Constants.MobileOSType.ANDROID.toString().equalsIgnoreCase(os)){
			account.mobileOS = Constants.MobileOSType.ANDROID;
		}
		if(Constants.MobileOSType.IOS.toString().equalsIgnoreCase(os)){
			account.mobileOS = Constants.MobileOSType.IOS;
		}
		
		String result = account.validateThenCreate();
		
		Logger.info(result);
		
		if(result != null){
			renderHtml(result);
		}else{
			renderHtml("success");
		}
	}
	
	public static void enable(){
		
	}
	
	public static void login(String phone, String email, String password){
		LoginVO loginVO = new LoginVO();
		Account account = null;
		if(StringUtils.isEmpty(phone)){
			account = Account.findByEmail(email);
		}else{
			account = Account.findByPhone(phone);
		}
		
		if(account != null){
			boolean flag = account.validatePassword(password);
			if(flag){
				loginVO.msg = "success";
				loginVO.build(account);
			}else{
				loginVO.msg = "fail";
			}
		}
		renderJSON(loginVO);
	}
	
	public static void logout(){
		
	}
}
