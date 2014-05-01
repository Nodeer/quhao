package controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import play.Play;
import play.modules.morphia.Model.MorphiaQuery;
import vo.CategoryVO;
import vo.HaomaVO;
import vo.MerchantVO;
import vo.ReservationVO;
import vo.TopMerchantVO;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.CommandResult;
import com.withiter.common.Constants.CreditStatus;
import com.withiter.models.account.Account;
import com.withiter.models.account.Credit;
import com.withiter.models.account.Reservation;
import com.withiter.models.merchant.Attention;
import com.withiter.models.merchant.Category;
import com.withiter.models.merchant.Comment;
import com.withiter.models.merchant.Haoma;
import com.withiter.models.merchant.Merchant;
import com.withiter.models.merchant.Open;
import com.withiter.models.merchant.Paidui;
import com.withiter.models.merchant.TopMerchant;

/**
 * 所有商家的操作
 * 
 * @author Cross Lee
 */

public class MerchantController extends BaseController {

	private static Logger logger = LoggerFactory.getLogger(MerchantController.class);
	private static int NEAR_MERCHANT_PAGE_ITEMS_NUMBER = 20;
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
			if(c.count == 0){
				continue;
			}
			categoriesVO.add(CategoryVO.build(c));
		}
		renderJSON(categoriesVO);
	}

	/**
	 * 通过cateType以及page查询商家列表
	 * page > 0
	 */
	@Deprecated
	public static void merchantByCategory() {
		String cateType = params.get("cateType");
		String pageStr = params.get("page");
		int page = 1;
		if(StringUtils.isEmpty(cateType)){
			renderJSON("cateType类型不能为空");
		}
		
		if(!StringUtils.isEmpty(pageStr)){
			page = Integer.parseInt(pageStr);
			if(page < 1){
				page = 1;
			}
		}
		
		// 返回的list，每页10条数据
		List<Merchant> merchantList = Merchant.findByType(cateType, page);
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
	public static void nextPage(int page, String cateType, String sortBy, String cityCode, double userX, double userY) {
		page = (page == 0) ? 1 : page;
		if(StringUtils.isEmpty(sortBy)){
			sortBy = "-modified";
		}

		List<Merchant> merchantList = Merchant.nextPage(cateType, page, sortBy, cityCode);
		List<MerchantVO> merchantVOList = new ArrayList<MerchantVO>();
		for (Merchant m : merchantList) {
			merchantVOList.add(MerchantVO.build(m, userX, userY));
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
		
		// 更新商家评价信息
		m.updateEvaluate();
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
		long openNum = 0;
		if(!accountId.equals("")){
			Attention attention =Attention.getAttentionById(merchantId, accountId);
			if(attention==null){
				isAttention=false;
			}else{
				isAttention=attention.flag;
			}
			
			openNum = Open.getNumberByMid(merchantId);
		}
		
		if(null != m) {
			merchantDetails.put("merchant", MerchantVO.build(m, c, isAttention, openNum));
		}
		
		if(null != m && m.enable && "false".equals(isLogined)) {
			Haoma haoma = Haoma.findByMerchantId(m.id());
			//haoma.updateSelf();

			HaomaVO haomaVO = HaomaVO.build(haoma);
			merchantDetails.put("haomaVO", haomaVO);
		}
		
		if(null != m && m.enable && "true".equals(isLogined)) {
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
		if(StringUtils.isEmpty(poiId)){
			renderJSON("poiid不能为空");
		}
		Merchant m = Merchant.queryMerchantByPoiId(poiId);
		if(m!=null) {
			MerchantVO vo = MerchantVO.buildSimpleVo(m);
			renderJSON(vo);
		} else {
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
		if(StringUtils.isEmpty(id) || StringUtils.isEmpty(seatNo)){
			renderJSON("商家ID为空或者座位号为空");
		}
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
	 * 获取用户的座位及号码情况（座位人数，当前号码，我的号码，在你前面等等）
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
		haoma.updateSelf();
		
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
	 * WebService for APP
	 * 
	 * @param x
	 *            top merchant 数量
	 */
	public static void getTopMerchants(int x, String cityCode) {
		List<TopMerchant> topMerchants = TopMerchant.topX(x, cityCode);
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
	public static void getMerchantsByName(String name, String cityCode) {
		List<Merchant> merchantList = null;
		if(!StringUtils.isEmpty(cityCode)){
			merchantList = Merchant.findByName(name, cityCode);
		}else{
			merchantList = Merchant.findByName(name);
		}
		
		logger.debug("search result size: " + merchantList.size());
		
		List<MerchantVO> merchantVOList = null;
		if (null != merchantList && !merchantList.isEmpty()) {
			merchantVOList = new ArrayList<MerchantVO>();
			for (Merchant m : merchantList) {
				if(m.enable){
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
	
	/**
	 * 周边不排队商家
	 * @param page	分页
	 * @param userX X坐标
	 * @param userY	Y坐标
	 * @param maxDis 最远的距离
	 * @param cityCode 城市代码
	 */
	public static void getNearNoQueueMerchants(int page, double userX, double userY, double maxDis, String cityCode) {
		page = (page == 0) ? 1 : page;
		int num = (page - 1) * NEAR_MERCHANT_PAGE_ITEMS_NUMBER;
		BasicDBObject cmdBody = new BasicDBObject("aggregate", "Merchant");
		List<BasicDBObject> pipeline = new ArrayList<BasicDBObject>();

		BasicDBObject geoNearParams = new BasicDBObject();
		geoNearParams.put("near", new double[] { userX, userY });
		if(maxDis>=0){
			geoNearParams.put("maxDistance", maxDis / 6371);
		}
		geoNearParams.put("distanceField", "dis");
		geoNearParams.put("distanceMultiplier", 6371000);
		geoNearParams.put("spherical", true);
		geoNearParams.put("num", num + NEAR_MERCHANT_PAGE_ITEMS_NUMBER);

		pipeline.add(new BasicDBObject("$geoNear", geoNearParams));
		if (num != 0) {
			pipeline.add(new BasicDBObject("$skip", num));
		}
		BasicDBObject matchParams = new BasicDBObject();
		if (!StringUtils.isEmpty(cityCode)) {
			matchParams.put("cityCode", cityCode);	
		}
		List<ObjectId> list = Merchant.noQueueMerchants();
		matchParams.put("_id", new BasicDBObject("$in", list));
		pipeline.add(new BasicDBObject("$match", matchParams));
		
		BasicDBObject projectParams = new BasicDBObject();
		projectParams.put("_id", 1);
		projectParams.put("name", 1);
		projectParams.put("dis", 1);
		projectParams.put("averageCost", 1);
		projectParams.put("merchantImage", 1);
		projectParams.put("grade", 1);
		projectParams.put("enable", 1);

		pipeline.add(new BasicDBObject("$project", projectParams));
		cmdBody.put("pipeline", pipeline);
		if (!MorphiaQuery.ds().getDB().command(cmdBody).ok()) {
			logger.warn("NoQueue geoNear查询出错: "
					+ MorphiaQuery.ds().getDB().command(cmdBody)
							.getErrorMessage());
		}

		CommandResult myResult = MorphiaQuery.ds().getDB().command(cmdBody);
		if (myResult.containsField("result")) {
			List<MerchantVO> merchantVOList = analyzeResults(myResult);
			renderJSON(merchantVOList);
		} else {
			renderJSON("");
		}
	}
	
	/**
	 * 附件商家用
	 * @param page 分页用
	 * @param userX  用户所在经度
	 * @param userY  用户所在纬度
	 * @param maxDis 限制最大距范围
	 * @param cityCode 城市代码
	 */
	public static void getNearMerchants(int page, double userX, double userY, double maxDis, String cityCode) {
		page = (page == 0) ? 1 : page;
		int num = (page - 1) * NEAR_MERCHANT_PAGE_ITEMS_NUMBER;
		BasicDBObject cmdBody = new BasicDBObject("aggregate", "Merchant");
		List<BasicDBObject> pipeline = new ArrayList<BasicDBObject>();

		BasicDBObject geoNearParams = new BasicDBObject();
		geoNearParams.put("near", new double[] { userX, userY });
		if(maxDis>=0){
			geoNearParams.put("maxDistance", maxDis / 6371);
		}
		geoNearParams.put("distanceField", "dis");
		geoNearParams.put("distanceMultiplier", 6371000);
		geoNearParams.put("spherical", true);
		geoNearParams.put("num", num + NEAR_MERCHANT_PAGE_ITEMS_NUMBER);

		pipeline.add(new BasicDBObject("$geoNear", geoNearParams));
		if (num != 0) {
			pipeline.add(new BasicDBObject("$skip", num));
		}
		if (!StringUtils.isEmpty(cityCode)) {
			BasicDBObject matchParams = new BasicDBObject();
			matchParams.put("cityCode", cityCode);
			pipeline.add(new BasicDBObject("$match", matchParams));
		}
		
		BasicDBObject projectParams = new BasicDBObject();
		projectParams.put("_id", 1);
		projectParams.put("name", 1);
		projectParams.put("dis", 1);
		projectParams.put("averageCost", 1);
		projectParams.put("merchantImage", 1);
		projectParams.put("grade", 1);
		projectParams.put("enable", 1);

		pipeline.add(new BasicDBObject("$project", projectParams));
		cmdBody.put("pipeline", pipeline);

		if (!MorphiaQuery.ds().getDB().command(cmdBody).ok()) {
			logger.warn("geoNear查询出错: "
					+ MorphiaQuery.ds().getDB().command(cmdBody)
							.getErrorMessage());
		}

		CommandResult myResult = MorphiaQuery.ds().getDB().command(cmdBody);
		if (myResult.containsField("result")) {
			List<MerchantVO> merchantVOList = analyzeResults(myResult);
			renderJSON(merchantVOList);
		} else {
			renderJSON("");
		}
	}
	
	/**
	 * 解析geoNear返回的数据
	 * @param commandResult
	 * @return
	 */
	private static List<MerchantVO> analyzeResults(CommandResult commandResult) {
        List<MerchantVO> lists = new ArrayList<MerchantVO>();  
		 BasicDBList resultList = (BasicDBList) commandResult.get("result");
	     Iterator<Object> it = resultList.iterator();
	     BasicDBObject resultContainer = null;
	     ObjectId resultId = null;
	     MerchantVO m = null;
		   while (it.hasNext()) {
			  m = new MerchantVO();
		      resultContainer = (BasicDBObject) it.next();
		      resultId = (ObjectId)resultContainer.get("_id");

		      m.id = resultId.toString();
		      m.distance = resultContainer.getDouble("dis");
		      m.averageCost = Float.parseFloat(resultContainer.get("averageCost").toString());
		      m.name = resultContainer.getString("name");
		      m.merchantImage = resultContainer.getString("merchantImage");
		      m.grade = Float.parseFloat(resultContainer.get("grade").toString());
		      m.enable = resultContainer.getBoolean("enable");
		      
		      lists.add(m);
		   }
		   return lists;
	}
}
