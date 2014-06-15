package controllers;

import java.io.File;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import play.Play;
import play.libs.Codec;
import play.modules.morphia.Model.MorphiaQuery;
import play.mvc.Before;
import vo.AppVersionVO;
import cn.bran.japid.util.StringUtils;

import com.withiter.common.Constants;
import com.withiter.models.appconfig.AppConfig;

public class AppController extends BaseController {

	/**
	 * 显示app version信息
	 */
	public static void appCode(){
		String android = null;
		String ios = null;
		
		MorphiaQuery q = AppConfig.q();
		List<AppConfig> configs = q.asList();
		if(configs == null || configs.isEmpty()){
			AppConfig androidconfig = new AppConfig();
			androidconfig.type = "Android";
			androidconfig.version = "1.0";
			androidconfig.erweimalink = "";
			androidconfig.save();
			AppConfig iosconfig = new AppConfig();
			iosconfig.type = "iOS";
			iosconfig.version = "1.0";
			iosconfig.version = "";
			iosconfig.save();
			
			configs.add(androidconfig);
			configs.add(iosconfig);
		}
		for(AppConfig c : configs){
			if(c.type.equalsIgnoreCase("android")){
				android = c.version;
			}
			if(c.type.equalsIgnoreCase("ios")){
				ios = c.version;
			}
		}
		
		AppVersionVO avo = new AppVersionVO(android, ios);
		renderJSON(avo);
	}
	
	/**
	 * app 下载请求
	 */
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
