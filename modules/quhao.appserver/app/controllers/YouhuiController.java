package controllers;

import java.util.ArrayList;
import java.util.List;

import play.mvc.Scope.Session;
import vo.BackendMerchantInfoVO;
import vo.YouhuiVO;
import cn.bran.japid.util.StringUtils;

import com.withiter.common.Constants;
import com.withiter.models.admin.MerchantAccount;
import com.withiter.models.merchant.Merchant;
import com.withiter.models.merchant.Youhui;

public class YouhuiController extends BaseController {

	public static void youhui(){
		String mid = params.get("mid");
		if(StringUtils.isEmpty(mid)){
			renderJSON(false);
		}
		
		Youhui youhui = Youhui.getRandomEnabledYouhui(mid);
		renderJSON(YouhuiVO.build(youhui));
	}
}
