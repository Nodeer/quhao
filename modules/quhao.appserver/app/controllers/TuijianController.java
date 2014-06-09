package controllers;

import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import play.mvc.Before;
import vo.MerchantVO;
import cn.bran.japid.util.StringUtils;

import com.withiter.common.Constants;
import com.withiter.models.merchant.Merchant;

public class TuijianController extends BaseController {
	
	private static Logger logger = LoggerFactory.getLogger(TuijianController.class);

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
	
	/**
	 * 选择困难症，推荐一个商家
	 */
	public static void tuijian(){
		String cityCode = params.get("cityCode");
		String userX = params.get("userX");
		String userY = params.get("userY");
		if(StringUtils.isEmpty(cityCode)){
			cityCode = "021";
		}
		
		Merchant m = Merchant.findOneTuijian(cityCode);
		if(StringUtils.isEmpty(userX) || StringUtils.isEmpty(userY)){
			renderJSON(MerchantVO.build(m));
		} else {
			renderJSON(MerchantVO.build(m, Double.valueOf(userX), Double.valueOf(userY)));
		}
	}
}
