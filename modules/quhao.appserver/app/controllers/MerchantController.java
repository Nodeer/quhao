package controllers;

import java.util.ArrayList;
import java.util.List;

import vo.CategoryVO;
import vo.HaomaVO;
import vo.MerchantVO;
import vo.ReservationVO;
import vo.TopMerchantVO;

import com.withiter.models.account.Reservation;
import com.withiter.models.merchant.Category;
import com.withiter.models.merchant.Haoma;
import com.withiter.models.merchant.Merchant;
import com.withiter.models.merchant.TopMerchant;

/**
 * 所有商家的操作
 * 
 * @author Cross Lee
 */
public class MerchantController extends BaseController {

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
		renderJSON(merchantList);
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
		Merchant m = Merchant.findById(id);
		renderJSON(MerchantVO.build(m));
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
		if(r != null){
			rvo.tipValue = "您已经拿过此家商家的号，无法重复拿号！";
			rvo.build(r);
		}else{
			Reservation reservation = Haoma.nahao(accountId, mid, seatNumber);
			reservation.save();
			rvo.tipKey = true;
			rvo.tipValue = "拿号成功！";
			rvo.build(reservation);
		}
		
		renderJSON(rvo);
	}

	/**
	 * Top merchant 列表
	 * 
	 * @param x top merchant 数量
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
}
