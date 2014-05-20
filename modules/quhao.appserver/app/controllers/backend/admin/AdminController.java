package controllers.backend.admin;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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
import play.libs.Images;
import play.modules.morphia.Model.MorphiaQuery;
import play.mvc.Before;
import vo.AdminVO;
import cn.bran.japid.util.StringUtils;

import com.mongodb.gridfs.GridFSInputFile;
import com.withiter.common.Constants;
import com.withiter.models.account.CooperationRequest;
import com.withiter.models.admin.MerchantAccount;
import com.withiter.models.merchant.Merchant;
import com.withiter.models.merchant.TopMerchant;
import com.withiter.models.opinion.Opinion;
import com.withiter.utils.ExceptionUtil;

import controllers.BaseController;
import controllers.UploadController;

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
	
	/**
	 * 跳转到置顶商家页面
	 */
	public static void topmerchant(){
		MorphiaQuery q = TopMerchant.q();
		q.filter("enable", true);
		List<TopMerchant> tops = q.asList();
		logger.debug("the size of top merchant is %s", tops.size());
		renderJapid(tops);
		
	}
	
	/**
	 * 取消置顶
	 * @param mid 商家id
	 */
	public static void disableTop(String mid){
		TopMerchant t = TopMerchant.findById(new ObjectId(mid));
		t.enable = false;
		t.save();
		topmerchant();
	}
	
	/**
	 * 生成置顶商家
	 * @param mid 商家id
	 * @param starttime 置顶开始时间
	 * @param endtime 置顶结束时间
	 */
	public static void enableTop(){
		
		String mid = params.get("mid");
		String starttime = params.get("starttime");
		String endtime = params.get("endtime");
		
		if(StringUtils.isEmpty(mid) || StringUtils.isEmpty(starttime) || StringUtils.isEmpty(endtime)){
			renderJSON("商家ID/开始时间/结束时间 不能为空");
		}
		
		String image = params.get("image");
		
		Merchant m = Merchant.findByMid(mid);
		if(m == null){
			renderJSON("没有找到对应的商家ID");
		}
		if(!m.enable){
			renderJSON("此商家还没有开通取号啦服务不能置顶，m.enable=false");
		}
		TopMerchant t = TopMerchant.build(m);
		try {
			t.start = DateFormat.getDateInstance().parse(starttime);
			t.end = DateFormat.getDateInstance().parse(endtime);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error(ExceptionUtil.getTrace(e));
		}
		
		t.save();
		
		if(!StringUtils.isEmpty(image)){
			GridFSInputFile file = uploadFirst(image, t.id());
			if (file != null) {
				try {
					String imageStorePath = Play.configuration.getProperty("image.store.path");
					t.merchantImage = URLEncoder.encode(imageStorePath + file.getFilename(), "UTF-8");
					t.save();
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
					logger.error(ExceptionUtil.getTrace(e));
				}
			}
		}
		renderJSON(true);
		
	}
	
	/**
	 * 上传Top merchant图片
	 * @param param
	 * @param mid
	 * @return
	 */
	private static GridFSInputFile uploadFirst(String param, String tmid) {
		GridFSInputFile gfsFile = null;
		
		logger.debug(""+params.get(param, File[].class));
		logger.debug(params.get(param, File[].class).length+"");
		
		File[] files = params.get(param, File[].class);
		for (File file : files) {
			try {
				File desFile = Play.getFile("public/upload/" + file.getName());
				Images.resize(file, desFile, 320, 120);
				gfsFile = UploadController.saveBinary(desFile, tmid);
				desFile.delete();
				break;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (gfsFile == null) {
			return null;
		} else {
			return gfsFile;
		}
	}
	
	/**
	 * 跳转到合作申请页面
	 * @param page 第page页
	 */
	public static void cooperate(int page){
		if(page <= 0){
			page = 1;
		}
		List<CooperationRequest> list = CooperationRequest.nextNoHandle(page);
		logger.debug("CooperationRequest's size is: " + list.size());
		renderJapid(list);
	}
	
	/**
	 * 标记合作申请为“已处理”
	 */
	public static void requesthandle(){
		String rid = params.get("rid");
		logger.debug(rid);
		
		boolean flag = CooperationRequest.markHandled(rid);
		if(flag){
			renderJSON(true);
		}else{
			renderJSON(false);
		}
	}
	
	public static void feedback(int page){
		if(page <=0){
			page = 1;
		}
		List<Opinion> list = Opinion.nextNoHandle(page);
		renderJapid(list);
	}
	
	/**
	 * 
	 */
	public static void logout(){
		session.current().remove(Constants.ADMIN_SESSION_USERNAME);
		session.current().remove(Constants.ADMIN_COOKIE_USERNAME);
		index();
	}
}
