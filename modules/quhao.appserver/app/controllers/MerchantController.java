package controllers;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import play.mvc.Before;
import play.mvc.Http.Header;
import vo.CategoryVO;
import vo.ErrorVO;
import vo.HaomaVO;
import vo.MerchantVO;
import vo.ReservationVO;
import vo.TopMerchantVO;

import com.withiter.common.Constants;
import com.withiter.common.Constants.CreditStatus;
import com.withiter.models.account.Account;
import com.withiter.models.account.Credit;
import com.withiter.models.account.Reservation;
import com.withiter.models.merchant.Category;
import com.withiter.models.merchant.Comment;
import com.withiter.models.merchant.Haoma;
import com.withiter.models.merchant.Merchant;
import com.withiter.models.merchant.Paidui;
import com.withiter.models.merchant.TopMerchant;
import com.withiter.utils.DesUtils;

/**
 * 所有商家的操作
 * 
 * @author Cross Lee
 */

public class MerchantController extends BaseController {

	private static Logger logger = LoggerFactory.getLogger(MerchantController.class);

	/**
	 * Interception any caller on this controller, will first invoke this method
	 */
	@Before
	static void checkAuthentification() {
		boolean mobileAgent = false;
		boolean sessionExist = false;

		Header userAgentHeader = request.headers.get("user-agent");
		Header sessionAndroidHeader = request.headers.get("quhao-android-session");
		Header sessionIOSHeader = request.headers.get("quhao-ios-session");
		
		for(String k : request.headers.keySet()){
			logger.debug("header key: "+k);
			logger.debug("header value: " + request.headers.get(k).values);
		}
		
		if (userAgentHeader.values.contains("QuhaoAndroid")) {
			mobileAgent = true;
			return;
//			if(sessionAndroidHeader != null){
//				String phone  = new DesUtils().decrypt(sessionAndroidHeader.value());
//				Account account = Account.findByPhone(phone);
//				if(StringUtils.isNotEmpty(phone) && account != null){
//					session.put(account.id(), account.id());
//					return;
//				}
//			}
		}
		
		if (userAgentHeader.values.contains("QuhaoiPhone")) {
			mobileAgent = true;
			return;
//			if(sessionIOSHeader != null){
//				String phone  = new DesUtils().decrypt(sessionIOSHeader.value());
//				Account account = Account.findByPhone(phone);
//				if(StringUtils.isNotEmpty(phone) && account != null){
//					session.put(account.id(), account.id());
//					return;
//				}
//			}
		}
		
		if (userAgentHeader.values.contains("Windows")) {

		}

		logger.debug("The caller agent is mobile : " + mobileAgent);
		logger.debug("The caller agent is pc : " + !mobileAgent);

//		if (mobileAgent) {
//			if (!sessionExist) {
//				ErrorVO evo = new ErrorVO();
//				evo.key = "NO_LOGIN";
//				evo.cause = "SESSION_EXPIRED";
//				renderJSON(evo);
//			}
//			return;
//		}

		if (!session.contains(Constants.SESSION_USERNAME)) {
			logger.debug("no session is found in Constants.SESSION_USERNAME");
			renderJapidWith("japidviews.backend.merchant.MerchantManagementController.index");
		}
	}

	/**
	 * 返回所有分类
	 */
	public static void allCategories() {
		List<Category> categories = Category.getAll();
		List<CategoryVO> categoriesVO = new ArrayList<CategoryVO>();
		for (Category c : categories) {
			categoriesVO.add(CategoryVO.build(c));
		}
		renderJSON(categoriesVO);
	}

	public static void merchantByCategory(String cateType) {
		List<Merchant> merchantList = Merchant.findByType(cateType);
		List<MerchantVO> merchantVOList = new ArrayList<MerchantVO>();
		for (Merchant m : merchantList) {
			merchantVOList.add(MerchantVO.build(m));
		}
		renderJSON(merchantVOList);
	}

	/**
	 * 进入分类商家
	 * 
	 * @param page
	 *            分页
	 * @param cateType
	 *            菜系
	 * @param sortBy
	 *            排序
	 */
	public static void nextPage(int page, String cateType, String sortBy) {

		page = (page == 0) ? 1 : page;

		// TODO remove test condiftion

		sortBy = "-modified";

		List<Merchant> merchantList = Merchant.nextPage(cateType, page, sortBy);
		List<MerchantVO> merchantVOList = new ArrayList<MerchantVO>();
		for (Merchant m : merchantList) {
			merchantVOList.add(MerchantVO.build(m));
		}
		renderJSON(merchantVOList);
	}

	/**
	 * 返回商家详细信息
	 * 
	 * @param id
	 *            商家id
	 */
	public static void merchant(String id) {
		System.out.println("merchant:" + id);
		Merchant m = Merchant.findByMid(id);
		Comment c = Comment.latestOne(id);
		if (c == null) {
			c = new Comment();
			c.mid = id;
		}
		renderJSON(MerchantVO.build(m, c));
	}

	/**
	 * 返回当前商家所有N人桌排队信息
	 * 
	 * @param merchantId
	 *            商家id
	 */
	public static void quhao(String id) {
		Haoma haoma = Haoma.findByMerchantId(id);
		HaomaVO vo = HaomaVO.build(haoma);
		renderJSON(vo);
	}

	/**
	 * 根据merchant id 和 座位号 查看 当前排队情况
	 * 
	 * @param merchantId
	 *            商家id
	 * @param seatNo
	 *            座位号
	 * 
	 * @return json 座位号
	 */
	public static void getCurrentNo(String id, String seatNo) {
		Haoma haoma = Haoma.findByMerchantId(id);
		// HaomaVO vo = HaomaVO.build(haoma);
		Iterator ite = haoma.haomaMap.keySet().iterator();
		while (ite.hasNext()) {
			Integer key = (Integer) ite.next();
			if (key.equals(Integer.valueOf(seatNo))) {
				renderJSON(haoma.haomaMap.get(key).currentNumber);

			}
		}
		// renderJSON(vo);
	}

	/**
	 * 获取用户的座位号码情况
	 * 
	 * @param id
	 *            商家id
	 * @param set
	 *            几人桌
	 */
	public static void getReservations(String accountId, String mid) {
		List<ReservationVO> rvos = new ArrayList<ReservationVO>();
		Haoma haoma = Haoma.findByMerchantId(mid);

		ReservationVO rvo = null;
		List<Reservation> reservations = Reservation.getReservationsByMerchantIdAndAccountId(accountId, mid);
		if (null != reservations && reservations.size() > 0) {

			for (Reservation r : reservations) {
				Paidui paidui = haoma.haomaMap.get(r.seatNumber);

				rvo = new ReservationVO();

				int canclCount = (int) Reservation.findCountBetweenCurrentNoAndMyNumber(mid, paidui.currentNumber, r.myNumber, r.seatNumber);

				rvo.beforeYou = r.myNumber - (paidui.currentNumber + canclCount);
				rvo.currentNumber = paidui.currentNumber;
				rvo.build(r);
				rvos.add(rvo);
			}
		}

		renderJSON(rvos);
	}

	/**
	 * 用户拿号了
	 * 
	 * @param id
	 *            商家id
	 * @param set
	 *            几人桌
	 */
	public static void nahao(String accountId, String mid, int seatNumber) {
		ReservationVO rvo = new ReservationVO();
		Reservation r = Reservation.reservationExist(accountId, mid, seatNumber);
		Haoma haoma = Haoma.findByMerchantId(mid);
		if (r != null) {
			Paidui paidui = haoma.haomaMap.get(seatNumber);
			// rvo.beforeYou = paidui.currentNumber - (paidui.canceled +
			// paidui.expired + paidui.finished);
			rvo.tipValue = "ALREADY_HAVE";
			int canclCount = (int) Reservation.findCountBetweenCurrentNoAndMyNumber(mid, paidui.currentNumber, r.myNumber, seatNumber);

			rvo.beforeYou = r.myNumber - (paidui.currentNumber + canclCount);
			// rvo.beforeYou = reservation.myNumber-( paidui.currentNumber +
			// paidui.canceled);
			rvo.currentNumber = paidui.currentNumber;
			rvo.build(r);
			renderJSON(rvo);
		}

		Account account = Account.findById(accountId);
		int left = account.jifen;
		if (left < 1) {
			Paidui paidui = haoma.haomaMap.get(seatNumber);
			// rvo.beforeYou = paidui.currentNumber - (paidui.canceled +
			// paidui.expired + paidui.finished);
			rvo.currentNumber = paidui.currentNumber;
			rvo.tipValue = "NO_MORE_JIFEN";
			rvo.accountId = accountId;
			rvo.merchantId = mid;
			rvo.myNumber = 0;
			rvo.seatNumber = seatNumber;
			renderJSON(rvo);
		}
		if (left >= 1) {
			Reservation reservation = Haoma.nahao(accountId, mid, seatNumber);

			Haoma haomaNew = Haoma.findByMerchantId(mid);

			rvo.currentNumber = haomaNew.haomaMap.get(seatNumber).currentNumber;

			int cancelCount = (int) Reservation.findCountBetweenCurrentNoAndMyNumber(mid, haomaNew.haomaMap.get(seatNumber).currentNumber, reservation.myNumber, seatNumber);

			rvo.beforeYou = reservation.myNumber - (haomaNew.haomaMap.get(seatNumber).currentNumber + cancelCount);
			// rvo.beforeYou = reservation.myNumber-( paidui.currentNumber +
			// paidui.canceled);

			rvo.tipKey = true;
			rvo.tipValue = "NAHAO_SUCCESS";
			rvo.build(reservation);
			account.jifen -= 1;
			account.save();

			// 增加积分消费情况
			Credit credit = new Credit();
			credit.accountId = reservation.accountId;
			credit.merchantId = reservation.merchantId;
			credit.reservationId = reservation.id();
			credit.cost = false;
			credit.status = CreditStatus.getNumber;
			credit.created = new Date();
			credit.modified = new Date();
			credit.create();
			renderJSON(rvo);
		}

	}

	/**
	 * Cancel one reservation by reservation id
	 * 
	 * @param reservationId
	 *            the id of reservation
	 */
	public static void cancel(String reservationId) {
		Reservation.cancel(reservationId);
	}

	/**
	 * Finish one reservation by reservation id
	 * 
	 * @param reservationId
	 *            the id of reservation
	 */
	public static void finish(String reservationId) {
		Reservation.finish(reservationId);
	}

	/**
	 * Expire one reservation by reservation id
	 * 
	 * @param reservationId
	 *            the id of reservation
	 */
	public static void expire(String reservationId) {
		Reservation.expire(reservationId);
	}

	/**
	 * Top merchant 列表
	 * 
	 * @param x
	 *            top merchant 数量
	 */
	public static void getTopMerchants(int x) {
		List<TopMerchant> topMerchants = TopMerchant.topX(x);
		List<TopMerchantVO> topMerchantVos = new ArrayList<TopMerchantVO>();
		if (null != topMerchants && !topMerchants.isEmpty()) {
			for (TopMerchant topMerchant : topMerchants) {
				topMerchantVos.add(TopMerchantVO.build(topMerchant));
			}
		}
		renderJSON(topMerchantVos);
	}

	/**
	 * 模糊查询
	 * 
	 * @return 返回匹配的商家列表
	 * @param name
	 *            商家名称
	 */
	public static void getMerchantsByName(String name) {
		List<Merchant> merchantList = Merchant.findByName(name);
		List<MerchantVO> merchantVOList = null;
		if (null != merchantList && !merchantList.isEmpty()) {
			merchantVOList = new ArrayList<MerchantVO>();
			for (Merchant m : merchantList) {
				merchantVOList.add(MerchantVO.build(m));
			}
		}
		renderJSON(merchantVOList);
	}

	/**
	 * @return 返回新加入的商家列表
	 * @param date
	 *            日期
	 */
	public static void getLastMerchants(int page, String date, String sortBy) {
		page = (page == 0) ? 1 : page;

		List<Merchant> merchantList = Merchant.findByDate(page, date, sortBy);
		List<MerchantVO> merchantVOList = new ArrayList<MerchantVO>();
		for (Merchant m : merchantList) {
			merchantVOList.add(MerchantVO.build(m));
		}
		renderJSON(merchantVOList);

	}

}
