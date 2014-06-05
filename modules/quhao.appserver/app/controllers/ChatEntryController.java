package controllers;

import play.Logger;
import play.data.validation.Required;
import vo.ErrorVO;

import com.withiter.models.chat.ChatPort;
import com.withiter.models.chat.MerchantPort;

public class ChatEntryController extends BaseController {

	public static void redirect(@Required String mid){
		if(validation.hasErrors()){
			renderJSON(new ErrorVO("false","mid is null"));
		}
		// 1st, 判断MerchantPort是否存在
		MerchantPort mp = MerchantPort.findByMid(mid);
		if(mp != null){ // 存在，则返回port给前台
			Logger.info("Find MerchantPort object, return port : %d", mp.port);
			renderJSON(mp.port);
		}
		
		// 2nd, MerchantPort不存在，查看当前所有port对应的房间数量，选择room少于20的port
		ChatPort cp = ChatPort.findOne();
		if(cp != null) { // 可以分配一个chat服务器
			renderJSON(cp.port);
			Logger.info("Can't find MerchantPort object, find one from ChatPort, return port : %d", cp.port);
		}
		
		// 3th, 没有少于20的port了，无法分配聊天
		Logger.error("No more avaliable port!!!");
		renderJSON(new ErrorVO("false","no more port"));
		
	}
}
