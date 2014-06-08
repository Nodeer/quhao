package controllers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import play.modules.morphia.Model.MorphiaQuery;
import play.mvc.Before;
import vo.ActivityVO;

import com.withiter.common.Constants;
import com.withiter.models.activity.Activity;

public class ActivityController extends BaseController {
	
	private static Logger logger = LoggerFactory.getLogger(ActivityController.class);

	/**
	 * Interception any caller on this controller, will first invoke this method
	 */
	@Before
	static void checkAuthentification() {
		if(session.contains(Constants.SESSION_USERNAME)){
			return;
		}
		
		Map headers = request.headers;
		Iterator it = headers.keySet().iterator();
		while(it.hasNext()){
			String key = (String) it.next();
			logger.debug(key+", " +headers.get(key));
		}
		
		if(headers.containsKey("user-agent")){
			if(!(request.headers.get("user-agent").values.contains("QuhaoAndroid") || request.headers.get("user-agent").values.contains("QuhaoIOS"))){
				renderJSON("请使用Android/iOS APP访问。");
			}
		} else {
			renderJSON("请使用Android/iOS APP访问。");
		}
	}
	
	public static void activity(){
		MorphiaQuery q = Activity.q();
		q.filter("enable", true);
		List<Activity> as = q.asList();
		logger.debug("The size of activity is : " + as.size());
		List<ActivityVO> vos = new ArrayList<ActivityVO>();
		for(Activity a : as){
			vos.add(new ActivityVO().build(a));
		}
		renderJSON(vos);
	}
}
