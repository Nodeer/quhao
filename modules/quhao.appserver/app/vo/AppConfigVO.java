package vo;

import com.withiter.models.appconfig.AppConfig;

public class AppConfigVO {

	public String id;
	public String type;
	public String version;
	public String erweimalink;
	
	public static AppConfigVO bulid(AppConfig ac) {
		AppConfigVO acvo = new AppConfigVO();
		acvo.id = ac.id();
		acvo.type = ac.type;
		acvo.version = ac.version;
		acvo.erweimalink = ac.erweimalink;
		return acvo;
	}
}
