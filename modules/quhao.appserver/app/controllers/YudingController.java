package controllers;

import java.util.Date;

import play.data.validation.Required;
import vo.YudingVO;

import com.withiter.common.Constants.YudingStatus;
import com.withiter.models.merchant.Yuding;

public class YudingController extends BaseController {

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
		
		if(validation.hasErrors()){
			renderJSON(validation.errors());
		}
		
		Yuding y = new Yuding();
		y.mid = mid;
		y.aid = aid;
		y.renshu = Integer.parseInt(renshu);
		y.shijian = new Date(Long.parseLong(shijian));
		y.baojian = Boolean.parseBoolean(baojian);
		y.xing = xing;
		y.mobile = mobile;
		y.status = YudingStatus.created;
		y.save();
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
