package controllers.backend.self;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.httpclient.HttpException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import play.Play;
import play.libs.Codec;
import play.libs.Images;
import play.mvc.Before;
import play.mvc.Scope.Session;
import vo.BackendMerchantInfoVO;
import vo.HaomaVO;
import vo.ReservationVO;
import vo.StatisticsVO;
import vo.YouhuiVO;
import vo.account.MerchantAccountVO;
import cn.bran.japid.util.StringUtils;

import com.mongodb.gridfs.GridFSInputFile;
import com.withiter.common.Constants;
import com.withiter.common.sms.business.SMSBusiness;
import com.withiter.models.account.Account;
import com.withiter.models.account.Reservation;
import com.withiter.models.admin.MerchantAccount;
import com.withiter.models.backendMerchant.MerchantAccountRel;
import com.withiter.models.merchant.Category;
import com.withiter.models.merchant.Haoma;
import com.withiter.models.merchant.Merchant;
import com.withiter.models.merchant.Open;
import com.withiter.models.merchant.Paidui;
import com.withiter.models.merchant.Youhui;
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

	/*
	 * 1) account information(included information:email or phone...) 
	 * 2) Merchant information(included information:name, address...)
	 */

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
		if (relList == null || relList.isEmpty()) {

		} else {
			MerchantAccountRel rel = relList.get(0);
			String mid = rel.mid;
			merchant = Merchant.findById(mid);
			openRequestCount = Open.getNumberByMid(mid);
		}
		BackendMerchantInfoVO bmivo = BackendMerchantInfoVO.build(merchant, account, openRequestCount);
		renderJapid(bmivo);
	}

	public static void editMerchant(String uid, String mid) {

		Merchant m = null;

		String merchantName = params.get("merchantName");
		String description = params.get("description");
		String cityCode = params.get("cityCode");
		String address = params.get("address");
		String tel = params.get("tel");
		String cateType = params.get("cateType");
		String cateName = params.get("cateName");
		
		String cateType1 = params.get("cateType1");
		String cateName1 = params.get("cateName1");
		
		String openTime = params.get("openTime");
		String closeTime = params.get("closeTime");
		String merchantImage = params.get("merchantImage");

		String[] seatType = params.getAll("seatType");

		if (StringUtils.isEmpty(mid)) {		// new merchant
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
			if(r == null){
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
		m.telephone = tel.split(",");
		m.cateType = cateType;
		m.cateName = cateName;
		if(!StringUtils.isEmpty(cateType1) && !StringUtils.isEmpty(cateName1)){
			m.cateType1 = cateType1;
			m.cateName1 = cateName1;
		}
		m.openTime = openTime;
		m.closeTime = closeTime;
		m.enable = true;
		m.seatType = seatType;
		m.save();

		// 设置桌位类型
		Set<String> seatTypeSet = new HashSet<String>();
		for (String seatNoNeedToEnable : seatType) {
			System.out.println(seatNoNeedToEnable);
			seatTypeSet.add(seatNoNeedToEnable);
		}

		Haoma haoma = Haoma.findByMerchantId(m.id());
		Iterator it = haoma.haomaMap.keySet().iterator();
		
		// 循环老的排队信息，设置最新的桌位以及清除非开启的桌位类型对应信息
		while (it.hasNext()) {
			Integer key = (Integer) it.next();
			if (seatTypeSet.contains(key.toString())) {
				haoma.haomaMap.get(key).enable = true;
				seatTypeSet.remove(key.toString());
			} else {
				// reset Paidui object to original status
				// currentNumber = 0; maxNumber = 0; enable = false; ...
				Paidui p = haoma.haomaMap.get(key);
				p.reset();
				
				// set the reservations status with this seatNumber to invalid(valid = false) 
				// and the change Constants.ReservationStatus status to invalidByMerchantUpdate
				Reservation.invalidByMerchantUpdate(key,m.id());
			}
		}

		// 老的桌位类型之外，都初始化。
		if (seatTypeSet.size() != 0) {
			Iterator ite = seatTypeSet.iterator();
			Paidui p = null;
			while (ite.hasNext()) {
				p = new Paidui();
				p.enable = true;
				haoma.haomaMap.put(Integer.parseInt(ite.next().toString()), p);
			}
		}
		haoma.save();
		haoma.check();

		// update the category counts
		Category.updateCounts();

		if (!StringUtils.isEmpty(merchantImage)) {
			GridFSInputFile file = uploadFirst(merchantImage, m.id());
			if (file != null) {
				m.merchantImageSet.add(file.getFilename());
				if (StringUtils.isEmpty(m.merchantImage)) {
					String imageStorePath = Play.configuration.getProperty("image.store.path");
					try {
						m.merchantImage = URLEncoder.encode(imageStorePath + file.getFilename(), "UTF-8");
						logger.debug(m.merchantImage);
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}
				m.save();
			}
		}

		index(uid);
	}

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
		if(!StringUtils.isEmpty(mid)){
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
		
		StatisticsVO svo = new StatisticsVO();
		svo.lastDayFinish = lastDayFinishCount;
		svo.lastDayCancel = lastDayCancelCount;
		svo.lastMonthFinish = lastMonthFinishCount;
		svo.lastMonthCancel = lastMonthCancelCount;
		svo.lastThreeMonthFinish = lastThreeMonthsFinishCount;
		svo.lastThreeMonthCancel = lastThreeMonthsCancelCount;

		String uid = Session.current().get(Constants.SESSION_USERNAME);
		MerchantAccount account = MerchantAccount.findById(uid);
		Merchant merchant = Merchant.findById(mid);
		BackendMerchantInfoVO bmivo = BackendMerchantInfoVO.build(merchant, account);
		renderJapid(svo, bmivo);
	}

	/**
	 * 优惠管理
	 */
	public static void goYouhuiPage(){
		String mid = params.get("mid");
		
		List<Youhui> youhuiList = Youhui.getAllEnabledYouhui(mid);
		List<YouhuiVO> yvoList = new ArrayList<YouhuiVO>();
		for(Youhui yh : youhuiList){
			yvoList.add(YouhuiVO.build(yh));
		}
		
		String uid = Session.current().get(Constants.SESSION_USERNAME);
		MerchantAccount account = MerchantAccount.findById(uid);
		Merchant merchant = Merchant.findById(mid);
		BackendMerchantInfoVO bmivo = BackendMerchantInfoVO.build(merchant, account);
		renderJapid(yvoList, bmivo);
	}
	
	/**
	 * 添加优惠信息
	 */
	public static void saveYouhui(){
		String mid = params.get("mid");
		String title = params.get("title");
		String content = params.get("content");
		
		logger.debug(mid);
		logger.debug(title);
		logger.debug(content);
		
		Youhui y = new Youhui();
		y.mid = mid;
		y.title = title;
		y.content = content;
		y.enable = true;
		y.save();
		
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
			renderJSON(false);
		}
		int currentNumber = Integer.parseInt(cNumber);
		int seatNumber = Integer.parseInt(sNumber);

		Reservation r = Reservation.findReservationForHandle(seatNumber, currentNumber, mid);
		if (r != null) {
			boolean flag = Reservation.finish(r.id());
			Haoma haoma = Haoma.findByMerchantId(mid);
			haoma.updateSelf();

			smsRemind(mid, seatNumber);
			renderJSON(flag);
		} else {
			renderJSON(false);
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
			renderJSON(false);
		}
		int currentNumber = Integer.parseInt(cNumber);
		int seatNumber = Integer.parseInt(sNumber);

		Reservation r = Reservation.findReservationForHandle(seatNumber, currentNumber, mid);
		if (r != null) {
			boolean flag = Reservation.expire(r.id());
			Haoma haoma = Haoma.findByMerchantId(mid);
			haoma.updateSelf();

			smsRemind(mid, seatNumber);
			renderJSON(flag);
		} else {
			renderJSON(false);
		}
	}
	
	private static void smsRemind(String mid, int seatNumber){
		Reservation r = Reservation.findReservationForSMSRemind(mid, seatNumber, 4);
		if(r == null){
			return;
		}
		String aid = r.accountId;
		if(aid == null){
			return;
		}
		Account account = Account.findById(aid);
		// send message
		String remind = Play.configuration.getProperty("service.sms.remind");
		try {
			int i = SMSBusiness.sendSMS(account.phone, remind);
			int j = 0;
			while (i < 0) {
				i = SMSBusiness.sendSMS(account.phone, remind);
				j++;
				if (j == 3) {
					logger.error("发送提醒短信失败");
					break;
				}
			}
		} catch (HttpException e) {
			e.printStackTrace();
			logger.error(ExceptionUtil.getTrace(e));
		} catch (IOException e) {
			e.printStackTrace();
			logger.error(ExceptionUtil.getTrace(e));
		}

	}

	/**
	 * 现场输入手机号取号
	 */
	public static void quhaoOnsite() {
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
		if(account  == null){
			Account a = new Account();
			a.phone = tel;
			a.save();
		}

		int seatNumber = Integer.parseInt(seatN);
		Reservation reservation = Haoma.nahao(null, mid, seatNumber, tel);
		Haoma haomaNew = Haoma.findByMerchantId(mid);
		haomaNew.updateSelf();
		rvo.currentNumber = haomaNew.haomaMap.get(seatNumber).currentNumber;
		int cancelCount = (int) Reservation.findCountBetweenCurrentNoAndMyNumber(mid, haomaNew.haomaMap.get(seatNumber).currentNumber, reservation.myNumber, seatNumber);
		rvo.beforeYou = reservation.myNumber - (haomaNew.haomaMap.get(seatNumber).currentNumber + cancelCount);
		rvo.tipKey = true;
		rvo.tipValue = "NAHAO_SUCCESS";
		rvo.build(reservation);

		// send message
		String paiduihaoTip = Play.configuration.getProperty("service.sms.paiduihao");
		String qianmianTip = Play.configuration.getProperty("service.sms.qianmian");
		String apptuijian = Play.configuration.getProperty("service.sms.apptuijian");
		String content = paiduihaoTip + reservation.myNumber + qianmianTip + rvo.beforeYou + apptuijian;
		try {
			int i = SMSBusiness.sendSMS(tel, content);
			int j = 0;
			while (i < 0) {
				i = SMSBusiness.sendSMS(tel, content);
				j++;
				if (j == 3) {
					Haoma.nahaoRollback(reservation);
					rvo.tipValue = "发送短信失败，请重新发送";
					rvo.tipKey = false;
					break;
				}
			}
		} catch (HttpException e) {
			Haoma.nahaoRollback(reservation);
			rvo.tipValue = "发送短信失败，请重新发送";
			rvo.tipKey = false;
			e.printStackTrace();
			logger.error(ExceptionUtil.getTrace(e));
		} catch (IOException e) {
			Haoma.nahaoRollback(reservation);
			rvo.tipValue = "发送短信失败，请重新发送";
			rvo.tipKey = false;
			e.printStackTrace();
			logger.error(ExceptionUtil.getTrace(e));
		}

		renderJSON(rvo);
	}

	private static GridFSInputFile uploadFirst(String param, String mid) {
		GridFSInputFile gfsFile = null;
		File[] files = params.get(param, File[].class);
		for (File file : files) {
			try {
				File desFile = Play.getFile("public/upload/" + file.getName());
				Images.resize(file, desFile, 100, 60);
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
}
