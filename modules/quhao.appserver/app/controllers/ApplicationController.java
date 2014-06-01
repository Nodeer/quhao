package controllers;

import play.cache.Cache;
import play.libs.Images;
import play.mvc.Controller;


public class ApplicationController extends Controller {
	/**
	 * 生成验证码
	 * @param id
	 */
	public static void captcha(String id) {
	    Images.Captcha captcha = Images.captcha();
	    String code = captcha.getText("#E4EAFD");
	    Cache.set(id, code, "10mn");
	    renderBinary(captcha);
	}
}
