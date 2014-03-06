package controllers.backend.self;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.httpclient.HttpException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import play.Play;
import play.libs.Images;
import play.mvc.Before;
import play.mvc.Http.Header;
import play.mvc.Scope.Session;
import vo.BackendMerchantInfoVO;
import vo.HaomaVO;
import vo.PaiduiVO;
import vo.ReservationVO;
import vo.account.AccountVO;
import cn.bran.japid.util.StringUtils;

import com.mongodb.gridfs.GridFSInputFile;
import com.withiter.common.Constants;
import com.withiter.common.Constants.CreditStatus;
import com.withiter.common.sms.business.SMSBusiness;
import com.withiter.models.account.Account;
import com.withiter.models.account.Credit;
import com.withiter.models.account.Reservation;
import com.withiter.models.backendMerchant.MerchantAccountRel;
import com.withiter.models.merchant.Category;
import com.withiter.models.merchant.Haoma;
import com.withiter.models.merchant.Merchant;
import com.withiter.models.merchant.Paidui;
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
			renderJapidWith("japidviews.backend.merchant.MerchantManagementController.index");
		}
	}

	/*
	 * 1) account information(included information:email or phone...) 2)
	 * Merchant information(included information:name, address...)
	 */

	/**
	 * 登录成功，通过uid查询出当前account的对应的merchant信息
	 * 
	 * @param uid
	 */
	public static void index(String uid) {
		Account account = Account.findById(uid);
		List<MerchantAccountRel> relList = MerchantAccountRel.getMerchantAccountRelList(uid);
		Merchant merchant = null;
		if (relList == null || relList.isEmpty()) {

		} else {
			MerchantAccountRel rel = relList.get(0);
			String mid = rel.mid;
			merchant = Merchant.findById(mid);
		}
		BackendMerchantInfoVO bmivo = BackendMerchantInfoVO.build(merchant, account);
		renderJapid(bmivo);
	}

	public static void editMerchant(String uid, String mid) {

		Merchant m = null;

		String merchantName = params.get("merchantName");
		String description = params.get("description");
		String address = params.get("address");
		String tel = params.get("tel");
		String cateType = params.get("cateType");
		String cateName = params.get("cateName");
		String openTime = params.get("openTime");
		String closeTime = params.get("closeTime");
		String merchantImage = params.get("merchantImage");

		String[] seatType = params.getAll("seatType");

		if (StringUtils.isEmpty(mid)) {
			m = new Merchant();
			m.save();
			MerchantAccountRel rel = new MerchantAccountRel();
			rel.mid = m.id();
			rel.uid = uid;
			rel.save();
		} else {
			m = Merchant.findById(mid);
			MerchantAccountRel rel = new MerchantAccountRel();
			rel.mid = m.id();
			rel.uid = uid;
			rel.save();
		}
		if (!StringUtils.isEmpty(merchantName)) {
			m.name = merchantName;
		}
		m.description = description;
		m.address = address;
		m.telephone = tel.split(",");
		m.cateType = cateType;
		m.cateName = cateName;
		m.openTime = openTime;
		m.closeTime = closeTime;
		m.enable = true;
		m.seatType = seatType;
		m.save();

		Set<String> seatTypeSet = new HashSet<String>();
		for (String seatNoNeedToEnable : seatType) {
			System.out.println(seatNoNeedToEnable);
			seatTypeSet.add(seatNoNeedToEnable);
			// haoma.haomaMap.get(Integer.parseInt(seatNoNeedToEnable)).enable =
			// true;
		}

		Haoma haoma = Haoma.findByMerchantId(m.id());
		Iterator it = haoma.haomaMap.keySet().iterator();
		while (it.hasNext()) {
			Integer key = (Integer) it.next();
			if (seatTypeSet.contains(key.toString())) {
				haoma.haomaMap.get(key).enable = true;
				seatTypeSet.remove(key.toString());
			} else {
				haoma.haomaMap.get(key).enable = false;
			}
		}

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

		// update the category counts
		Category.updateCounts();

		if (!StringUtils.isEmpty(merchantImage)) {
			GridFSInputFile file = uploadFirst(merchantImage, m.id());
			if (file != null) {
				m.merchantImageSet.add(file.getFilename());
				if (StringUtils.isEmpty(m.merchantImage)) {
					String server = Play.configuration.getProperty("application.domain");
					String imageStorePath = Play.configuration.getProperty("image.store.path");
					try {
						m.merchantImage = URLEncoder.encode(server + imageStorePath + file.getFilename(), "UTF-8");
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
		Account account = Account.findById(uid);
		Merchant merchant = Merchant.findById(mid);
		BackendMerchantInfoVO bmivo = BackendMerchantInfoVO.build(merchant, account);

		renderJapid(haomaVO, bmivo);
	}

	// TODO add personal page
	public static void goPersonalPage() {
		String aid = params.get("aid");
		Account account = Account.findById(aid);
		AccountVO avo = AccountVO.build(account);

		String mid = params.get("mid");
		Merchant merchant = Merchant.findById(mid);
		BackendMerchantInfoVO bmivo = BackendMerchantInfoVO.build(merchant, account);

		renderJapid(avo, bmivo);
	}

	// TODO add statistic report here
	public static void goStatisticPage() {
		String mid = params.get("mid");
		List<Reservation> rList = Reservation.findReservationsByMerchantIdandDate(mid);
		List<ReservationVO> voList = ReservationVO.build(rList);

		System.out.println(voList.size());

		renderJapid(voList);
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
			smsRemind(mid, seatNumber);
			renderJSON(flag);
		} else {
			renderJSON(false);
		}
	}
	
	private static void smsRemind(String mid, int seatNumber){
		Reservation r = Reservation.findReservationForSMSRemind(mid, seatNumber, 5);
		String aid = r.accountId;
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

		int seatNumber = Integer.parseInt(seatN);
		Reservation reservation = Haoma.nahao(null, mid, seatNumber, tel);
		Haoma haomaNew = Haoma.findByMerchantId(mid);
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
