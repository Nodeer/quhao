package controllers;

import java.io.File;

import cn.bran.japid.util.StringUtils;
import play.Play;
import vo.AppVersionVO;

public class AppController extends BaseController {
	
	public static void appCode(){
		String android = Play.configuration.getProperty("app.versioncode.android");
		String ios = Play.configuration.getProperty("app.versioncode.android");
		AppVersionVO avo = new AppVersionVO(android, ios);
		renderJSON(avo);
	}
	
	public static void down(){
		String type = params.get("t");
		if(StringUtils.isEmpty(type)){
			return;
		}
		
		if("android".equals(type)){
			String dir = Play.configuration.getProperty("merchants.path");
			File f = new File(dir+"/benbangcai.csv");
			renderBinary(f);
		}
	}

	public static void help(){
		renderJSON("test");
	}
}
