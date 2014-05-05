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
		
		List<Youhui> youhuiList = Youhui.getAllEnabledYouhui(mid);
		List<YouhuiVO> yvoList = new ArrayList<YouhuiVO>();
		for(Youhui yh : youhuiList){
			yvoList.add(YouhuiVO.build(yh));
		}
		renderJSON(youhuiList);
	}
}
