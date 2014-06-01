package controllers;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import play.mvc.Before;
import cn.bran.japid.util.StringUtils;

import com.withiter.models.merchant.Open;

public class OpenController extends BaseController {

	private static Logger logger = LoggerFactory.getLogger(controllers.OpenController.class);

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
	 * 用户希望开通取号
	 * 
	 * @param mid
	 *            商家id
	 * @param accountId
	 *            用户id
	 * @return String
	 */
	public static void openService(String mid, String accountId) {
		if (!StringUtils.isEmpty(mid) && !StringUtils.isEmpty(accountId)) {
			Open open = new Open();
			open.accountId = accountId;
			open.mid = mid;
			open.save();
			long num = open.getNumberByMid(mid);
			renderText(num);
		} else {
			renderText("error");
		}
	}
}
