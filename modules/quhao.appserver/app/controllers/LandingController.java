package controllers;

import java.io.File;

import play.Play;
import play.libs.Codec;
import vo.AppConfigVO;

import com.withiter.models.appconfig.AppConfig;

public class LandingController extends BaseController {
	public static void index() {
		AppConfig android = AppConfig.android();
		AppConfigVO vo = null;
		if(android != null){
			vo = AppConfigVO.bulid(android);
		}
		renderJapid(vo);
	}

	public static void home() {
		renderJapid();
	}

	public static void about() {
		renderJapid();
	}

	public static void business() {
		String randomID = Codec.UUID();
		renderJapid(randomID);
	}
	
	public static void androidDown(){
		String android = Play.configuration.getProperty("android.download.path");
	    response.setHeader("Content-Disposition", "attachment; filename=Quhao.apk");
	    response.setContentTypeIfNotSet("application/vnd.android.package-archive");
	    
		renderBinary(new File(android));
	}
}
