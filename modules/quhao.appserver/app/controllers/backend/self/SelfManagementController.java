package controllers.backend.self;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.httpclient.HttpException;
import org.bson.types.ObjectId;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import play.Play;
import play.data.validation.Required;
import play.libs.Codec;
import play.libs.Images;
import play.mvc.Before;
import play.mvc.Scope.Session;
import vo.BackendMerchantInfoVO;
import vo.HaomaVO;
import vo.ReservationVO;
import vo.StatisticsVO;
import vo.YouhuiVO;
import vo.YudingVO;
import vo.account.MerchantAccountVO;
import cn.bran.japid.util.StringUtils;

import com.mongodb.gridfs.GridFSInputFile;
import com.withiter.common.Constants;
import com.withiter.common.Constants.YudingStatus;
import com.withiter.common.jpush.JPushReminder;
import com.withiter.common.sms.business.SMSBusiness;
import com.withiter.models.account.Account;
import com.withiter.models.account.Reservation;
import com.withiter.models.admin.MerchantAccount;
import com.withiter.models.backendMerchant.MerchantAccountRel;
import com.withiter.models.merchant.Category;
import com.withiter.models.merchant.Haoma;
import com.withiter.models.merchant.Merchant;
import com.withiter.models.merchant.Open;
import com.withiter.models.merchant.Youhui;
import com.withiter.models.merchant.Yuding;
import com.withiter.utils.ExceptionUtil;

import controllers.BaseController;
import controllers.UploadController;

public class SelfManagementController extends BaseController {

	private static Logger logger = LoggerFactory.getLogger(SelfManagementController.class);

	/**
	 * Interception any caller on this controller, will first invoke this method
	 */
	@Before
	static void checkAuthentification() {
		if (!session.contains(Constants.SESSION_USERNAME)) {
			logger.debug("no session is found in Constants.SESSION_USERNAME");
			String randomID = Codec.UUID();
			renderJapidWith("japidviews.LandingController.business", randomID);
		}
	}

	/**
	 * 登录成功，通过uid查询出当前account的对应的merchant信息
	 * 
	 * @param uid
	 */
	public static void index(String uid) {
		MerchantAccount account = MerchantAccount.findById(uid);
		List<MerchantAccountRel> relList = MerchantAccountRel.getMerchantAccountRelList(uid);
		Merchant merchant = null;
		long openRequestCount = 0;
		boolean editable = false;
		if (relList == null || relList.isEmpty()) {
		} else {
			MerchantAccountRel rel = relList.get(0);
			String mid = rel.mid;
			merchant = Merchant.findById(mid);
			
			// 检查是否能编辑
			if(merchant != null){
				Haoma haoma = Haoma.findByMerchantId(merchant.id());
				editable = haoma.checkEditAble();
			}
			openRequestCount = Open.getNumberByMid(mid);
		}
		BackendMerchantInfoVO bmivo = BackendMerchantInfoVO.build(merchant, account, openRequestCount);
		bmivo.editable = editable;
		renderJapid(bmivo);
	}

	public static void checkEditAble(){
		String mid = params.get("mid");
		Merchant merchant = Merchant.findById(mid);
		
		// 检查是否能编辑
		if(merchant != null){
			Haoma haoma = Haoma.findByMerchantId(merchant.id());
			boolean editable = haoma.checkEditAble();
			renderJSON(editable);
		}
		renderJSON(false);
	}
	
	public static void editMerchant(String uid, String mid) {
		Merchant m = null;
		String merchantName = params.get("merchantName");
		String description = params.get("description");
		String cityCode = params.get("cityCode");
		String address = params.get("address");
		String x = params.get("x");
		String y = params.get("y");
		String tel = params.get("tel");
		String cateType = params.get("cateType");
		String cateName = params.get("cateName");
		String cateType1 = params.get("cateType1");
		String cateName1 = params.get("cateName1");
		String openTime = params.get("openTime");
		String closeTime = params.get("closeTime");
		String merchantImage = params.get("merchantImage");
		String merchantImageBig = params.get("merchantImageBig");
		String[] seatType = params.getAll("seatType");
		String dianpingFen = params.get("dianpingFen");
		String dianpingLink = params.get("dianpingLink");
		if (StringUtils.isEmpty(mid)) { // new merchant
			m = new Merchant();
			m.save();
			MerchantAccountRel rel = new MerchantAccountRel();
			rel.mid = m.id();
			rel.uid = uid;
			rel.save();
		} else {
			m = Merchant.findById(mid);

			// check rel exist, if yes -> operation is update. if no -> new MerchantAccountRel
			MerchantAccountRel r = MerchantAccountRel.findByMid(mid);
			if (r == null) {
				// new MerchantAccountRel
				MerchantAccountRel rel = new MerchantAccountRel();
				rel.mid = m.id();
				rel.uid = uid;
				rel.save();
			}
		}

		if (!StringUtils.isEmpty(merchantName)) {
			m.name = merchantName;
		}
		m.description = description;
		m.cityCode = cityCode;
		m.address = address;
		m.x = x;
		m.y = y;
		double[] d = { Double.parseDouble(y), Double.parseDouble(x) };
		m.loc = d;
		m.telephone = tel.split(",");
		m.cateType = cateType;
		m.cateName = cateName;
		if (!StringUtils.isEmpty(cateType1) && !StringUtils.isEmpty(cateName1)) {
			m.cateType1 = cateType1;
			m.cateName1 = cateName1;
		} else {
			m.cateType1 = "";
			m.cateName1 = "";
		}
		m.openTime = openTime;
		m.closeTime = closeTime;
		m.enable = true;
		m.seatType = seatType;
		m.dianpingFen = dianpingFen;
		m.dianpingLink = dianpingLink;
		m.save();

		logger.debug("merchant seatType------");
		for(String s:m.seatType){
			logger.debug("key1: "+s);
		}
		logger.debug("merchant seatType------");
		
		Haoma haoma = Haoma.findByMerchantId(m.id());
		// version ++
		haoma.version += 1;
		Iterator it = haoma.haomaMap.keySet().iterator();

		// 清除所有老的排队的reservation
		while(it.hasNext()){
			Integer key = (Integer) it.next();
			// set the reservations status with this seatNumber to
			// invalid(valid = false)
			// and the change Constants.ReservationStatus status to
			// invalidByMerchantUpdate
			Reservation.invalidByMerchantUpdate(key, m.id());
		}
		haoma.updatePaidui();
		haoma.save();
		haoma.check();
		
		Iterator it2 = haoma.haomaMap.keySet().iterator();
		logger.debug("haoma seatType------");
		while(it2.hasNext()){
			logger.debug("key: "+it2.next());
		}
		logger.debug("haoma seatType------");
		
		// update the category counts
		Category.updateCounts();

		// 更新商家图片
		if (!StringUtils.isEmpty(merchantImage)) {
			GridFSInputFile file = uploadFirst(merchantImage, m.id());
			if (file != null) {
				m.merchantImageSet.add(file.getFilename());
//				if (StringUtils.isEmpty(m.merchantImage)) {
					String imageStorePath = Play.configuration.getProperty("image.store.path");
					try {
						m.merchantImage = URLEncoder.encode(imageStorePath + file.getFilename(), "UTF-8");
						logger.debug(m.merchantImage);
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
//				}
				m.save();
			}
		}
		// 更新商家大图片
		if (!StringUtils.isEmpty(merchantImageBig)) {
			GridFSInputFile file = uploadFirst(merchantImageBig, m.id(), 834, 346);
			if (file != null) {
				m.merchantImageSet.add(file.getFilename());
//				if (StringUtils.isEmpty(m.merchantImageBig)) {
					String imageStorePath = Play.configuration.getProperty("image.store.path");
					try {
						m.merchantImageBig = URLEncoder.encode(imageStorePath + file.getFilename(), "UTF-8");
						logger.debug(m.merchantImageBig);
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
//				}
				m.save();
			}
		}
		index(uid);
	}

	/**
	 * 进入排队管理页面
	 */
	public static void goPaiduiPage() {
		String mid = params.get("mid");
		Haoma haoma = Haoma.findByMerchantId(mid);
		haoma.updateSelf();
		HaomaVO haomaVO = HaomaVO.build(haoma);
		String uid = Session.current().get(Constants.SESSION_USERNAME);
		MerchantAccount account = MerchantAccount.findById(uid);
		Merchant merchant = Merchant.findById(mid);
		BackendMerchantInfoVO bmivo = BackendMerchantInfoVO.build(merchant, account);
		renderJapid(haomaVO, bmivo);
	}

	/**
	 * 进入个人信息管理
	 */
	public static void goPersonalPage() {
		String aid = params.get("aid");
		MerchantAccount account = MerchantAccount.findById(aid);
		MerchantAccountVO avo = MerchantAccountVO.build(account);
		String mid = params.get("mid");
		Merchant merchant = null;
		if (!StringUtils.isEmpty(mid)) {
			merchant = Merchant.findById(mid);
		}
		BackendMerchantInfoVO bmivo = BackendMerchantInfoVO.build(merchant, account);
		renderJapid(avo, bmivo);
	}

	/**
	 * 消费统计管理
	 */
	public static void goStatisticPage() {
		String mid = params.get("mid");
		long lastDayFinishCount = Reservation.lastDayFinishCount(mid);
		long lastDayCancelCount = Reservation.lastDayCancelCount(mid);
		long lastMonthFinishCount = Reservation.lastMonthFinishCount(mid);
		long lastMonthCancelCount = Reservation.lastMonthCancelCount(mid);
		long lastThreeMonthsFinishCount = Reservation.lastThreeMonthsFinishCount(mid);
		long lastThreeMonthsCancelCount = Reservation.lastThreeMonthsCancelCount(mid);
		long todayFinishCount = Reservation.todayFinishCount(mid);
		long todayCancelCount = Reservation.todayCancelCount(mid);
		
		StatisticsVO svo = new StatisticsVO();
		svo.lastDayFinish = lastDayFinishCount;
		svo.lastDayCancel = lastDayCancelCount;
		svo.lastMonthFinish = lastMonthFinishCount;
		svo.lastMonthCancel = lastMonthCancelCount;
		svo.lastThreeMonthFinish = lastThreeMonthsFinishCount;
		svo.lastThreeMonthCancel = lastThreeMonthsCancelCount;
		svo.todayFinish = todayFinishCount;
		svo.todayCancel = todayCancelCount;

		String uid = Session.current().get(Constants.SESSION_USERNAME);
		MerchantAccount account = MerchantAccount.findById(uid);
		Merchant merchant = Merchant.findById(mid);
		BackendMerchantInfoVO bmivo = BackendMerchantInfoVO.build(merchant, account);
		renderJapid(svo, bmivo);
	}

	/**
	 * 优惠管理
	 */
	public static void goYouhuiPage() {
		String mid = params.get("mid");

		List<Youhui> youhuiList = Youhui.getAllEnabledYouhui(mid);
		List<YouhuiVO> yvoList = new ArrayList<YouhuiVO>();
		for (Youhui yh : youhuiList) {
			yvoList.add(YouhuiVO.build(yh));
		}

		String uid = Session.current().get(Constants.SESSION_USERNAME);
		MerchantAccount account = MerchantAccount.findById(uid);
		Merchant merchant = Merchant.findById(mid);
		BackendMerchantInfoVO bmivo = BackendMerchantInfoVO.build(merchant, account);
		renderJapid(yvoList, bmivo);
	}
	
	/**
	 * 预定管理
	 */
	public static void goYudingPage(@Required String mid) {
		List<Yuding> YudingList = Yuding.getAllNotHandledYuding(mid);
		List<YudingVO> yvoList = new ArrayList<YudingVO>();
		for (Yuding yd : YudingList) {
			yvoList.add(YudingVO.build(yd));
		}
		
		String uid = Session.current().get(Constants.SESSION_USERNAME);
		MerchantAccount account = MerchantAccount.findById(uid);
		Merchant merchant = Merchant.findById(mid);
		BackendMerchantInfoVO bmivo = BackendMerchantInfoVO.build(merchant, account);
		renderJapid(yvoList, bmivo);
	}
	
	/**
	 * 同意用户预定
	 * route: *		/b/w/yuding/tongyi					backend.self.SelfManagementController.tongyi
	 */
	public static void tongyi(@Required String yid){
		if(validation.hasErrors()){
			renderJSON(false);
		}
		
		Yuding yuding = Yuding.findById(new ObjectId(yid));
		if(yuding != null){
			yuding.status = YudingStatus.confirmed;
			yuding.save();
			// 发送短信通知
			Merchant m = Merchant.findByMid(yuding.mid);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");
			
			//设置模板ID
			//401215 恭喜预定成功! 预定信息：时间(#code1#)，商家(#code2#)，地点(#code3#)，人数(#code4#)，#code5#，座位保留15分钟，到店请出示此短信，如有疑问，可联系餐厅，电话:#code6#。【取号啦】
			long tpl_id = 401215l;
			//设置对应的模板变量值
			String tpl_value = "";
			if(yuding.baojian){
				if(yuding.baojianOptional){
					tpl_value ="#code1#="+sdf.format(yuding.shijian)+"&#code2#="+m.name+"&#code3#="+m.address+"&#code4#="+yuding.renshu+"&#code5#=如果没有包间，安排大厅";
				} else {
					tpl_value ="#code1#="+sdf.format(yuding.shijian)+"&#code2#="+m.name+"&#code3#="+m.address+"&#code4#="+yuding.renshu+"&#code5#=包间必须";
				}
			}
			// 电话
			if(m.telephone !=null && m.telephone.length > 0){
				tpl_value += "&#code6#="+m.telephone[0];
			}
			int code = SMSBusiness.tplSendSms(tpl_id,tpl_value, yuding.mobile);
			if (code == 0) {
				renderJSON(true);
			} else {
				renderJSON(false);
			}
		}
	}
	
	/**
	 * 拒绝用户预定
	 * route: *		/b/w/yuding/reject					backend.self.SelfManagementController.tongyi
	 */
	public static void reject(@Required String yid, @Required String type){
		if(validation.hasErrors()){
			renderJSON(false);
		}
		play.Logger.info("type:" + type);
		Yuding yuding = Yuding.findById(new ObjectId(yid));
		if(yuding != null){
			yuding.status = YudingStatus.rejected;
			yuding.save();
			// 发送短信通知
			Merchant m = Merchant.findByMid(yuding.mid);
			
			//设置模板ID
			//401228 很抱歉，您在[#code1#]的预定不成功。原因是：#code2#。如有疑问，可联系餐厅，电话:#code3#。【取号啦】
			long tpl_id = 401228l;
			//设置对应的模板变量值
			String tpl_value = "";
			if("1".equals(type)){
				tpl_value = "#code1#="+m.name+"&#code2#=此时间段不能接受预定";
			}
			if("2".equals(type)){
				tpl_value = "#code1#="+m.name+"&#code2#=没有包厢了";
			}
			if("3".equals(type)){
				tpl_value = "#code1#="+m.name+"&#code2#=预定已满（没有座位）";
			}
			// 电话
			if(m.telephone !=null && m.telephone.length > 0){
				tpl_value += "&#code3#="+m.telephone[0];
			}
			
			int code = SMSBusiness.tplSendSms(tpl_id,tpl_value, yuding.mobile);
			if (code != 0) {
				logger.error("Send SMS failed!!!");
			}
			goYudingPage(yuding.mid);
		}
	}
	
	/**
	 * 完成预定
	 * route: *		/b/w/yuding/finish					backend.self.SelfManagementController.finish
	 */
	public static void finish(@Required String yid){
		if(validation.hasErrors()){
			renderJSON(false);
		}
		Yuding yuding = Yuding.findById(new ObjectId(yid));
		if(yuding != null){
			yuding.status = YudingStatus.finished;
			yuding.save();
		}
		renderJSON(true);
	}

	/**
	 * 过期预定
	 * route: *		/b/w/yuding/expire					backend.self.SelfManagementController.expire
	 */
	public static void expire(@Required String yid){
		if(validation.hasErrors()){
			renderJSON(false);
		}
		Yuding yuding = Yuding.findById(new ObjectId(yid));
		if(yuding != null){
			yuding.status = YudingStatus.expired;
			yuding.save();
			// 发送短信通知
			Merchant m = Merchant.findByMid(yuding.mid);
			
			// 401210	很抱歉，您在[#code1#]的预定已过期。如有疑问，可联系餐厅，电话:#code2#。【取号啦】
			long tpl_id = 401210l;
			String tpl_value = "#code1#="+m.name;
			
			// 电话
			if(m.telephone !=null && m.telephone.length > 0){
				tpl_value += "&#code2#="+m.telephone[0];
			}
			
			int code = SMSBusiness.tplSendSms(tpl_id,tpl_value, yuding.mobile);
			if (code == 0) {
				renderJSON(true);
			} else {
				logger.error("Send SMS failed!!!");
				renderJSON(false);
			}
		}
	}
	
	/**
	 * 临时取消用户的预定
	 * route: *		/b/w/yuding/cancelTemp					backend.self.SelfManagementController.cancelTemp
	 */
	public static void cancelTemp(@Required String yid){
		if(validation.hasErrors()){
			renderJSON(false);
		}
		Yuding yuding = Yuding.findById(new ObjectId(yid));
		if(yuding != null){
			yuding.status = YudingStatus.cancelTemp;
			yuding.save();
			// 发送短信通知
			Merchant m = Merchant.findByMid(yuding.mid);
			
			// 401198	「取号啦」通知您，您在[#code1#]的预定已成功取消。如有疑问，可联系餐厅，电话:#code2#。【取号啦】
			long tpl_id = 401198l;
			String tpl_value = "#code1#="+m.name;
			
			// 电话
			if(m.telephone !=null && m.telephone.length > 0){
				tpl_value += "&#code2#="+m.telephone[0];
			}
			
			int code = SMSBusiness.tplSendSms(tpl_id,tpl_value, yuding.mobile);
			if (code == 0) {
				renderJSON(true);
			} else {
				logger.error("Send SMS failed!!!");
				renderJSON(false);
			}
		}
	}

	/**
	 * 添加优惠信息
	 */
	public static void saveYouhui() {
		String mid = params.get("mid");
		String title = params.get("title");
		String content = params.get("content");

		Youhui y = new Youhui();
		y.mid = mid;
		y.title = title;
		y.content = content;
		y.enable = true;
		y.save();

		Merchant m = Merchant.findByMid(mid);
		m.youhui = true;
		m.save();
		renderJSON(true);
	}

	/**
	 * 取消优惠信息
	 */
	public static void disableYouhui() {
		String mid = params.get("mid");
		String yid = params.get("yid");

		Youhui y = Youhui.findById(new ObjectId(yid));
		y.enable = false;
		y.save();

		Merchant m = Merchant.findByMid(mid);
		m.updateYouhuiInfo();
		renderJSON(true);
	}

	/**
	 * refresh paidui table
	 */
	public static void paiduiPageAutoRefresh() {
		String mid = params.get("mid");
		Haoma haoma = Haoma.findByMerchantId(mid);
		haoma.updateSelf();
		HaomaVO haomaVO = HaomaVO.build(haoma);
		renderJapidWith("japidviews.backend.self.SelfManagementController.goPaiduiPageRefresh", haomaVO);
	}

	/**
	 * finish one reservation by merchant
	 */
	public static void finishByMerchant() {
		String cNumber = params.get("currentNumber");
		String sNumber = params.get("seatNumber");
		String mid = params.get("mid");

		if (StringUtils.isEmpty(cNumber) || StringUtils.isEmpty(sNumber) || StringUtils.isEmpty(mid)) {
			renderJSON("false");
		}
		int currentNumber = Integer.parseInt(cNumber);
		int seatNumber = Integer.parseInt(sNumber);
		
		synchronized (SelfManagementController.class) {
			Haoma haoma = Haoma.findByMerchantId(mid);
			Reservation r = Reservation.findReservationForHandle(seatNumber, currentNumber, mid, haoma.version);
			if (r != null) {
				boolean flag = Reservation.finish(r.id());
				haoma.updateSelf();
				
				Reservation rr = Reservation.findReservationForSMSRemind(mid, seatNumber, 4, haoma.version);
				if (rr == null) {
					logger.debug("No number 4 reservation, no need to send sms");
					renderJSON(String.valueOf(flag));
				}
				String aid = rr.accountId;
				if (aid == null) {
					renderJSON(String.valueOf(flag));
				}
				Account account = Account.findById(aid);
				// 短信提醒第4位
				smsRemind(mid, seatNumber, haoma.version);
				// JPush提醒第4位
				if(StringUtils.isEmpty(account.password)){
					// 现场取号用户
					renderJSON(String.valueOf(flag));
				}
				// app用户
				String remind = Play.configuration.getProperty("service.push.remind");
				JPushReminder.sendAlias(account.phone, remind);
				renderJSON(String.valueOf(flag));
			} else {
				renderJSON("ALREADY_CANCELED");
			}
		}
	}

	/**
	 * expire one reservation by merchant
	 */
	public static void expireByMerchant() {
		String cNumber = params.get("currentNumber");
		String sNumber = params.get("seatNumber");
		String mid = params.get("mid");

		if (StringUtils.isEmpty(cNumber) || StringUtils.isEmpty(sNumber) || StringUtils.isEmpty(mid)) {
			renderJSON("false");
		}
		int currentNumber = Integer.parseInt(cNumber);
		int seatNumber = Integer.parseInt(sNumber);

		synchronized (SelfManagementController.class) {
			Haoma haoma = Haoma.findByMerchantId(mid);
			Reservation r = Reservation.findReservationForHandle(seatNumber, currentNumber, mid, haoma.version);
			if (r != null) {
				boolean flag = Reservation.expire(r.id());
				
				
				// 找到被过期的用户
				Account expiredAccount = null;
				if(!com.withiter.utils.StringUtils.isEmpty(r.accountId)){
					expiredAccount = Account.findById(new ObjectId(r.accountId));
				}
				if(expiredAccount != null){
					String remind = Play.configuration.getProperty("service.push.expiredRemind");
					JPushReminder.sendAlias(expiredAccount.phone, remind);
				}
				
				haoma.updateSelf();
				
				Reservation rr = Reservation.findReservationForSMSRemind(mid, seatNumber, 4, haoma.version);
				if (rr == null) {
					renderJSON(String.valueOf(flag));
				}
				String aid = rr.accountId;
				if (aid == null) {
					renderJSON(String.valueOf(flag));
				}
				Account account = Account.findById(aid);
				// 短信提醒第4位
				smsRemind(mid, seatNumber, haoma.version);
				// JPush提醒第4位
				if(StringUtils.isEmpty(account.password)){
					// 现场取号用户
					renderJSON(String.valueOf(flag));
				}
				// app用户
				String remind = Play.configuration.getProperty("service.push.remind");
				JPushReminder.sendAlias(account.phone, remind);
				
				renderJSON(String.valueOf(flag));
			} else {
				renderJSON("ALREADY_CANCELED");
			}
		}
	}

	private static void smsRemind(String mid, int seatNumber, long version) {
		Reservation r = Reservation.findReservationForSMSRemind(mid, seatNumber, 4, version);
		if (r == null) {
			return;
		}
		String aid = r.accountId;
		if (aid == null) {
			return;
		}
		Account account = Account.findById(aid);
		// send message
		// 401102	「取号啦」提醒您，在您前面还有#code#个人，请根据您的时间状况前往商家，或者取消排号。推荐使用「取号啦」APP，排队社交新体验。【取号啦】
		long tpl_id = 401102l;
		String tpl_value = "#code#=4";
		int i = SMSBusiness.tplSendSms(tpl_id, tpl_value, account.phone);
		int j = 0;
		while (i < 0) {
			i = SMSBusiness.tplSendSms(tpl_id, tpl_value, account.phone);
			j++;
			if (j == 3) {
				logger.error("发送提醒短信失败");
				break;
			}
		}
	}

	/**
	 * 现场输入手机号取号
	 */
	public static void quhaoOnsite() {
		synchronized (SelfManagementController.class) {
			String tel = params.get("tel");
			String seatN = params.get("seatNumber");
			String mid = params.get("mid");
			
			ReservationVO rvo = new ReservationVO();
			if (StringUtils.isEmpty(tel) || StringUtils.isEmpty(seatN) || StringUtils.isEmpty(mid)) {
				rvo.tipKey = false;
				rvo.tipValue = "NAHAO_FAILED";
				renderJSON(rvo);
			}
			
			Account account = Account.findByPhone(tel);
			if (account == null) {
				Account a = new Account();
				a.phone = tel;
				a.save();
			}
			
			int seatNumber = Integer.parseInt(seatN);
			Reservation reservation = Haoma.nahao(null, mid, seatNumber, tel);
			Haoma haomaNew = Haoma.findByMerchantId(mid);
			
			rvo.currentNumber = haomaNew.haomaMap.get(seatNumber).currentNumber;
			int cancelCount = (int) Reservation.findCountBetweenCurrentNoAndMyNumber(mid, haomaNew.haomaMap.get(seatNumber).currentNumber, reservation.myNumber, seatNumber, haomaNew.version);
			rvo.beforeYou = reservation.myNumber - (haomaNew.haomaMap.get(seatNumber).currentNumber + cancelCount);
			rvo.tipKey = true;
			rvo.tipValue = "NAHAO_SUCCESS";
			rvo.build(reservation);
			
			// send message
			long tpl_id = 0l;
			String tpl_value = "#code1#="+reservation.myNumber+"&#code2#="+rvo.beforeYou;;
			if(rvo.beforeYou <= 5){
				// 401095	您的排队号是#code1#号，在您前面还有#code2#位排队，很快就到你了，请不要离开哦。推荐使用「取号啦」APP，排队社交新体验。【取号啦】
				tpl_id = 401095l;
			} else {
				// 401093	您的排队号是#code1#号，在您前面还有#code2#位排队，推荐使用「取号啦」APP，排队社交新体验。【取号啦】
				tpl_id = 401093l;
			}
				
			int i = SMSBusiness.tplSendSms(tpl_id, tpl_value, account.phone);
			int j = 0;
			while (i != 0) {
				i = SMSBusiness.tplSendSms(tpl_id, tpl_value, account.phone);
				j++;
				if (j == 3) {
//					Haoma.nahaoRollback(reservation);
					rvo.tipValue = "发送短信失败，请重新发送";
					rvo.tipKey = false;
					break;
				}
			}
			renderJSON(rvo);
		}
	}

	/**
	 * 更新商家信息，上传图片
	 * @param param request 参数
	 * @param mid 商家id
	 * @return
	 */
	private static GridFSInputFile uploadFirst(String param, String mid) {
		GridFSInputFile gfsFile = null;
		File[] files = params.get(param, File[].class);
		for (File file : files) {
			try {
				File desFile = Play.getFile("public/upload/" + file.getName());
				Images.resize(file, desFile, 147, 126);
				gfsFile = UploadController.saveBinary(desFile, mid);
				desFile.delete();
				break;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (gfsFile == null) {
			return null;
		} else {
			return gfsFile;
		}
	}
	
	/**
	 * 上传图片（商家页面）
	 * @param param
	 * @param mid
	 * @param resizeX
	 * @param resizeY
	 * @return
	 */
	private static GridFSInputFile uploadFirst(String param, String mid, int resizeX, int resizeY) {
		GridFSInputFile gfsFile = null;
		File[] files = params.get(param, File[].class);
		for (File file : files) {
			try {
				File desFile = Play.getFile("public/upload/" + file.getName());
				Images.resize(file, desFile, resizeX, resizeY);
				gfsFile = UploadController.saveBinary(desFile, mid);
				desFile.delete();
				break;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (gfsFile == null) {
			return null;
		} else {
			return gfsFile;
		}
	}

	/**
	 * 改变商家状态（开放取号，关闭取号）
	 * 
	 * @param mid
	 *            商家id
	 * @param online
	 *            在线状态（true->开放取号，false->关闭取号）
	 */
	public static void changeStatus() {
		String mid = params.get("mid");
		String online = params.get("online");
		if (StringUtils.isEmpty(mid) || StringUtils.isEmpty(online)) {
			renderJSON(false);
		}
		boolean flag = Merchant.changeStatus(mid, Boolean.valueOf(online));
		renderJSON(flag);
	}
}
