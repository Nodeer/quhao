package controllers;

import java.util.ArrayList;
import java.util.List;

import vo.CategoryVO;
import vo.MerchantVO;

import com.withiter.models.merchant.Category;
import com.withiter.models.merchant.Haoma;
import com.withiter.models.merchant.Merchant;

/**
 *  所有商家的操作
 *	@author Cross Lee 
 */
public class MerchantController extends BaseController {

	/**
	 * 返回所有分类
	 */
	public static void allCategories() {
		List<Category> categories = Category.getAll();
		List<CategoryVO> categoriesVO = new ArrayList<CategoryVO>();
		for(Category c: categories){
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
	 * @param page 分页
	 * @param cateType 菜系
	 * @param sortBy 排序
	 */
	public static void nextPage(int page, String cateType, String sortBy) {

		page = (page == 0)? 1 : page;
		
		List<Merchant> merchantList = Merchant.nextPage(cateType, page, sortBy);
		List<MerchantVO> merchantVOList = new ArrayList<MerchantVO>();
		for(Merchant m : merchantList){
			merchantVOList.add(MerchantVO.build(m));
		}
		renderJSON(merchantVOList);
	}
	
	/**
	 * 返回商家详细信息
	 * @param id 商家id
	 */
	public static void merchant(String id) {
		Merchant m = Merchant.findById(id);
		renderJSON(MerchantVO.build(m));
	}
	
	
	
	
	/**
	 * 返回当前商家所有N人桌排队信息
	 * @param merchantId 商家id
	 */
	public static void quhao(String id){
		Haoma haoma = Haoma.findByMerchantId(id);
		renderJSON(haoma);
	}
	
	/**
	 * 用户拿号了
	 * @param id 商家id
	 * @param set 几人桌
	 */
	public static void nahao(String id, int set){
		
	}
}
