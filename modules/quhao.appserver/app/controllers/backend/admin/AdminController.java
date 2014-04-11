package controllers.backend.admin;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import notifiers.MailsController;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import play.Play;
import play.data.validation.Required;
import play.libs.Codec;
import play.modules.morphia.Model.MorphiaQuery;
import play.mvc.Before;
import vo.AdminVO;

import com.withiter.common.Constants;
import com.withiter.models.admin.MerchantAccount;
import com.withiter.models.merchant.Merchant;
import com.withiter.models.merchant.TopMerchant;

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
	
	/**
	 * 管理员登陆
	 * @param email 登陆的email
	 * @param password 登陆的密码
	 */
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
	
	/**
	 * 管理员首页
	 */
	public static void home(){
		renderJapid();
	}
	
	
	/**
	 * 生成商家账号
	 * @param email
	 * @param password
	 */
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
		MerchantAccount a = MerchantAccount.findByEmail(email);
		if(a != null){
			avo.error = "Email已经存在了，请换个其他的Email";
			avo.result = "Email已经存在了，请换个其他的Email";
			renderJSON(avo);
		}
		
		a = new MerchantAccount();
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
	
	/**
	 * 显示生成账号情况
	 */
	public static void accounts(int page){
		if(page == 0){
			page = 1;
		}
		int totalSize = MerchantAccount.totalSize();
		int countPerPage = 10;
		int totalPage = totalSize / countPerPage;
		if(totalSize % countPerPage != 0){
			totalPage++;
		}
		List<MerchantAccount> list = MerchantAccount.findNext(page, countPerPage);
		if(list == null || list.isEmpty()){
			list = new ArrayList<MerchantAccount>();
			renderJapid(list, page, totalPage);
		}else{
			renderJapid(list, page, totalPage);
		}
	}
	
	public static void topmerchant(){
		MorphiaQuery q = TopMerchant.q();
		q.filter("enable", true);
		List<TopMerchant> tops = q.asList();
		logger.debug("the size of top merchant is %s", tops.size());
		renderJapid(tops);
		
	}
	
	public static void disableTop(String mid){
		TopMerchant t = TopMerchant.findById(new ObjectId(mid));
		t.enable = false;
		t.save();
		topmerchant();
	}
	public static void enableTop(String mid, String starttime, String endtime){
		Merchant m = Merchant.findByMid(mid);
		if(!m.enable){
			renderJSON(false);
		}
		TopMerchant t = TopMerchant.build(m);
		t.enable = true;
		try {
			t.start = DateFormat.getDateInstance().parse(starttime);
			t.end = DateFormat.getDateInstance().parse(endtime);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		t.save();
		renderJSON(true);
		
//		topmerchant();
	}
}
