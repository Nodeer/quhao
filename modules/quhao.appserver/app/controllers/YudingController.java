package controllers;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import play.data.validation.Required;
import play.mvc.Before;
import vo.YudingVO;

import com.withiter.common.Constants.YudingStatus;
import com.withiter.models.merchant.Yuding;

public class YudingController extends BaseController {
	
	private static Logger logger = LoggerFactory.getLogger(controllers.YudingController.class);

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
	 * 添加预定
	 * @param mid	商家id
	 * @param renshu	人数
	 * @param shijian	预定时间
	 * @param baojian	是否要包间
	 * @param xing		贵姓
	 * @param mobile	手机号码
	 */
	public static void add(@Required String mid, @Required String renshu, @Required String shijian, @Required String baojian, @Required String xing, @Required String mobile){
		String aid = params.get("aid");
		String baojianOptional = params.get("baojianOptional");
		
		if(validation.hasErrors()){
			renderJSON(validation.errors());
		}
		
		Yuding y = new Yuding();
		y.mid = mid;
		y.aid = aid;
		y.renshu = Integer.parseInt(renshu);
		y.shijian = new Date(Long.parseLong(shijian));
		y.baojian = Boolean.parseBoolean(baojian);
		if(y.baojian){
			y.baojianOptional = Boolean.parseBoolean(baojianOptional);
		}
		
		y.xing = xing;
		y.mobile = mobile;
		y.status = YudingStatus.created;
		y.save();
		renderJSON(true);
	}
	
	/**
	 * 用户取消预定
	 * @param yid
	 */
	public static void cancel(@Required String yid){
		if(validation.hasErrors()){
			renderJSON(validation.errors());
		}
		Yuding y = Yuding.findById(new ObjectId(yid));
		if(y != null){
			y.status = YudingStatus.canceled;
			y.save();
		}
		renderJSON(true);
	}
	
	/**
	 * 查看
	 * @param aid
	 * @param mid
	 */
	public static void my(String aid, @Required String mobile, @Required String mid){
		if(validation.hasErrors()){
			renderJSON(validation.errors());
		}
		
		Yuding y = Yuding.findByMidAndAid(mid, aid, mobile);
		if(y != null){
			renderJSON(YudingVO.build(y));
		} else {
			renderJSON("");
		}
	}
}
