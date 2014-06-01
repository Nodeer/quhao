package controllers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import play.mvc.Before;
import play.mvc.Scope.Session;
import vo.BackendMerchantInfoVO;
import vo.YouhuiVO;
import cn.bran.japid.util.StringUtils;

import com.withiter.common.Constants;
import com.withiter.models.admin.MerchantAccount;
import com.withiter.models.merchant.Merchant;
import com.withiter.models.merchant.Youhui;

public class YouhuiController extends BaseController {
	
	private static Logger logger = LoggerFactory.getLogger(controllers.YouhuiController.class);

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
	 * APP 显示优惠信息
	 */
	public static void youhui(){
		String mid = params.get("mid");
		if(StringUtils.isEmpty(mid)){
			renderJSON(false);
		}
		
		List<Youhui> youhuiList = Youhui.getAllEnabledYouhui(mid);
		if(youhuiList == null || youhuiList.isEmpty()){
			renderJSON(false);
		}
		
		List<YouhuiVO> voList = new ArrayList<YouhuiVO>();
		for(Youhui youhui : youhuiList){
			voList.add(YouhuiVO.build(youhui));
		}
		
		renderJSON(voList);
	}
	
	/**
	 * 判断是否有优惠
	 */
	public static void youhuiExist(){
		String mid = params.get("mid");
		if(StringUtils.isEmpty(mid)){
			renderJSON(false);
		}
		
		List<Youhui> youhuiList = Youhui.getAllEnabledYouhui(mid);
		if(youhuiList == null || youhuiList.isEmpty()){
			renderJSON(false);
		}
		renderJSON(true);
	}
}
