package controllers;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import play.Play;
import play.modules.morphia.Model.MorphiaQuery;
import play.mvc.Before;
import play.mvc.Http.Header;
import play.mvc.With;
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
import com.withiter.models.merchant.Attention;
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
	 
	@Before
	static void checkAuthentification() {
		boolean mobileAgent = false;

		Header userAgentHeader = request.headers.get("user-agent");
		if (userAgentHeader.values.contains("QuhaoAndroid")) {
			mobileAgent = true;
			logger.debug("The caller agent is mobile : " + mobileAgent);
			logger.debug("The caller agent is pc : " + !mobileAgent);
			return;
		}
		
		if (userAgentHeader.values.contains("QuhaoiPhone")) {
			mobileAgent = true;
			logger.debug("The caller agent is mobile : " + mobileAgent);
			logger.debug("The caller agent is pc : " + !mobileAgent);
			return;
		}
		
		if (userAgentHeader.values.contains("Windows")) {
			mobileAgent = false;
			logger.debug("The caller agent is mobile : " + mobileAgent);
			logger.debug("The caller agent is pc : " + !mobileAgent);
		}

		if (!session.contains(Constants.SESSION_USERNAME)) {
			logger.debug("no session is found in Constants.SESSION_USERNAME");
			renderJapidWith("japidviews.backend.merchant.MerchantManagementController.index");
		}
	}
	 */
	
	/**
	 * 根据城市代码，返回所有分类
	 * @param cityCode
	 */
	public static void allCategories(String cityCode) {
		List<Category> categories = Category.getAll();
		List<CategoryVO> categoriesVO = new ArrayList<CategoryVO>();
		for (Category c : categories) {
			MorphiaQuery q = Merchant.q();
			q.filter("cityCode", cityCode).filter("cateType", c.cateType);
			c.count = q.count();
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
	public static void nextPage(int page, String cateType, String sortBy, String cityCode) {

		page = (page == 0) ? 1 : page;

		// TODO remove test condiftion

		sortBy = "-modified";

		List<Merchant> merchantList = Merchant.nextPage(cateType, page, sortBy, cityCode);
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
	 * 返回商家详细信息（增加了用户是否关注商家）
	 * 
	 * @param id 商家id
	 * @param accountId 用户id
	 */
	public static void merchantNew(String id,String accountId) {
		Merchant m = Merchant.findByMid(id);
		Comment c = Comment.latestOne(id);
		if (c == null) {
			c = new Comment();
			c.mid = id;
		}
		
		boolean isAttention=false;
		if(!accountId.equals("")){
			Attention attention =Attention.getAttentionById(id, accountId);
			if(attention==null){
				isAttention=false;
			}else{
				isAttention=attention.flag;
			}
		}
		renderJSON(MerchantVO.build(m, c,isAttention));
	}
	
	/**
	 * 返回商家详细信息（增加了用户是否关注商家）
	 * 
	 * @param id 商家id
	 * @param accountId 用户id
	 */
	public static void querytMerchantDetail(String merchantId,String accountId,String isLogined) {
		
		Map<String, Object> merchantDetails = new HashMap<String, Object>();
		Merchant m = Merchant.findByMid(merchantId);
		Comment c = Comment.latestOne(merchantId);
		if (c == null) {
			c = new Comment();
			c.mid = merchantId;
		}
		
		boolean isAttention=false;
		if(!accountId.equals("")){
			Attention attention =Attention.getAttentionById(merchantId, accountId);
			if(attention==null){
				isAttention=false;
			}else{
				isAttention=attention.flag;
			}
		}
		
		if(null != m)
		{
			merchantDetails.put("merchant", MerchantVO.build(m, c,isAttention));
		}
		
		if(null != m && m.enable && "false".equals(isLogined))
		{
			Haoma haoma = Haoma.findByMerchantId(m.id());

			//haoma.updateSelf();

			HaomaVO haomaVO = HaomaVO.build(haoma);
			merchantDetails.put("haomaVO", haomaVO);
		}
		
		if(null != m && m.enable && "true".equals(isLogined))
		{
			List<ReservationVO> rvos = new ArrayList<ReservationVO>();
			Haoma haoma = Haoma.findByMerchantId(merchantId);
			HaomaVO haomaVO = HaomaVO.build(haoma);
			merchantDetails.put("haomaVO", haomaVO);
			
			ReservationVO rvo = null;
			List<Reservation> reservations = Reservation.getReservationsByMerchantIdAndAccountId(accountId, merchantId);
			if (null != reservations && reservations.size() > 0) {
				for (Reservation r : reservations) {
					Paidui paidui = haoma.haomaMap.get(r.seatNumber);

					rvo = new ReservationVO();

					int canclCount = (int) Reservation.findCountBetweenCurrentNoAndMyNumber(merchantId, paidui.currentNumber, r.myNumber, r.seatNumber);
					rvo.beforeYou = r.myNumber - (paidui.currentNumber + canclCount);
					rvo.currentNumber = paidui.currentNumber;
					
					rvo.build(r);
					rvos.add(rvo);
				}
				
				merchantDetails.put("rvos", rvos);
			}

		}
		
		renderJSON(merchantDetails);
	}
	
	/**
	 * 返回商家详细信息
	 * 
	 * @param id
	 *            商家id
	 */
	public static void queryMerchantByPoiId(String poiId) {
		System.out.println("merchant:" + poiId);
		Merchant m = Merchant.queryMerchantByPoiId(poiId);
		if(m!=null)
		{
			renderJSON(m.id());
		}
		else
		{
			renderJSON("");
		}
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
		haoma.updateSelf();

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
		
		// if r != null, means current user had been got a paidui ticket
		if (r != null) {
			rvo.tipValue = "ALREADY_HAVE";
			rvo.build(r);
			renderJSON(rvo);
		}

		Account account = Account.findById(accountId);
		int getNumberJifen = Integer.parseInt(Play.configuration.getProperty("credit.getnumber.jifen"));
		int left = account.jifen;
		if (left < getNumberJifen) {
			Paidui paidui = haoma.haomaMap.get(seatNumber);
			rvo.currentNumber = paidui.currentNumber;
			rvo.tipValue = "NO_MORE_JIFEN";
			rvo.accountId = accountId;
			rvo.merchantId = mid;
			rvo.myNumber = 0;
			rvo.seatNumber = seatNumber;
			renderJSON(rvo);
		}
		if (left >= getNumberJifen) {
			Reservation reservation = Haoma.nahao(accountId, mid, seatNumber, null);
			Haoma haomaNew = Haoma.findByMerchantId(mid);
			rvo.currentNumber = haomaNew.haomaMap.get(seatNumber).currentNumber;
			int cancelCount = (int) Reservation.findCountBetweenCurrentNoAndMyNumber(mid, haomaNew.haomaMap.get(seatNumber).currentNumber, reservation.myNumber, seatNumber);
			rvo.beforeYou = reservation.myNumber - (haomaNew.haomaMap.get(seatNumber).currentNumber + cancelCount);
			rvo.tipKey = true;
			rvo.tipValue = "NAHAO_SUCCESS";
			rvo.build(reservation);
			account.jifen -= getNumberJifen;
			account.save();

			// 增加积分消费情况
			Credit credit = new Credit();
			credit.accountId = reservation.accountId;
			credit.merchantId = reservation.merchantId;
			credit.reservationId = reservation.id();
			credit.cost = false;
			credit.jifen=-getNumberJifen;
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
		boolean flag = Reservation.cancel(reservationId);
		
		Reservation r = Reservation.findByRid(reservationId);
		Haoma haoma = Haoma.findByMerchantId(r.merchantId);
		haoma.updateSelf();
		renderJSON(flag);
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
	 * 模糊查询 route:search
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
				if(!m.enable){
					merchantVOList.add(MerchantVO.build(m));
				}
			}
		}
		renderJSON(merchantVOList);
	}

	/**
	 * @return 返回新加入的商家列表
	 * @param date
	 *            日期
	 */
	public static void getLastMerchants(int page, String cateType , String date, String sortBy, String cityCode) {
		page = (page == 0) ? 1 : page;

		List<Merchant> merchantList = Merchant.findByDate(cateType, date, sortBy, cityCode);
		List<MerchantVO> merchantVOList = new ArrayList<MerchantVO>();
		for (Merchant m : merchantList) {
			merchantVOList.add(MerchantVO.build(m));
		}
		renderJSON(merchantVOList);

	}
}
