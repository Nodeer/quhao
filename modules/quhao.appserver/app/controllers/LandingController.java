package controllers;

import java.io.File;

import play.Play;
import play.libs.Codec;
import vo.AppConfigVO;

import com.withiter.models.appconfig.AppConfig;

public class LandingController extends BaseController {
	
	/**
	 * 跳转到首页
	 */
	public static void index() {
		AppConfig android = AppConfig.android();
		AppConfigVO vo = null;
		if (android != null) {
			vo = AppConfigVO.bulid(android);
		}
		renderJapid(vo);
	}
	
	/**
	 * 跳转到首页
	 */
	public static void index1() {
		AppConfig android = AppConfig.android();
		AppConfigVO vo = null;
		if (android != null) {
			vo = AppConfigVO.bulid(android);
		}
		renderJapid();
	}

	/**
	 * 跳转到关于页面
	 */
	public static void about() {
		renderJapid();
	}

	/**
	 * 跳转到商家页面
	 */
	public static void business() {
		String randomID = Codec.UUID();
		renderJapid(randomID);
	}
	/**
	 * 跳转到商家页面
	 * TODO test, remove
	 */
	public static void b() {
		String randomID = Codec.UUID();
		renderJapid(randomID);
	}

	/**
	 * Android下载
	 */
	public static void androidDown() {
		String android = Play.configuration.getProperty("android.download.path");
		response.setHeader("Content-Disposition", "attachment; filename=Quhao.apk");
		response.setContentTypeIfNotSet("application/vnd.android.package-archive");
		renderBinary(new File(android));
	}
}
