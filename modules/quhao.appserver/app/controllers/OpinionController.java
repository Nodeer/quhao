package controllers;

import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import play.mvc.Before;

import com.withiter.models.opinion.Opinion;

public class OpinionController extends BaseController {
	private static Logger logger = LoggerFactory.getLogger(controllers.OpinionController.class);

	/**
	 * Interception any caller on this controller, will first invoke this method
	 */
	@Before
	static void checkAuthentification() {
		Map headers = request.headers;
		Iterator it = headers.keySet().iterator();
		while (it.hasNext()) {
			String key = (String) it.next();
			logger.debug(key + ", " + headers.get(key));
		}

		if (headers.containsKey("user-agent")) {
			if (!(request.headers.get("user-agent").values.contains("QuhaoAndroid") || request.headers.get("user-agent").values.contains("QuhaoIOS"))) {
				renderJSON("请使用Android/iOS APP访问。");
			}
		} else {
			renderJSON("请使用Android/iOS APP访问。");
		}
	}

	/**
	 * add feedback
	 * 
	 * @param phone
	 * @param email
	 * @param feedback
	 */
	public static void createOpinion(String opinion, String contact) {
		Opinion opinionTO = new Opinion();
		opinionTO.contact = contact;
		opinionTO.opinion = opinion;
		opinionTO.save();
		renderJSON("success");
	}
}
