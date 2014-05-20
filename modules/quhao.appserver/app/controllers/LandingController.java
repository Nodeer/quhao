package controllers;

import java.io.File;

import play.Play;
import play.libs.Codec;

public class LandingController extends BaseController {
	public static void index() {
//		String android = Play.configuration.getProperty("android");
		renderJapid();
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
		response.setContentTypeIfNotSet("application/vnd.android.package-archive");
		renderBinary(new File(android));
	}
}
