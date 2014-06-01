package controllers;

import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import play.mvc.Before;
import vo.HaomaVO;

import com.withiter.models.merchant.Haoma;

public class ReservationController extends BaseController{

	private static Logger logger = LoggerFactory.getLogger(controllers.ReservationController.class);

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
	 * APP 实时排队信息
	 */
	public static void paiduiStatusForApp(){
		String mid = params.get("mid");
		Haoma haoma = Haoma.findByMerchantId(mid);
		HaomaVO haomaVO = HaomaVO.build(haoma);
		renderJSON(haomaVO);
	}
}
