package controllers;

import java.util.ArrayList;
import java.util.List;

import vo.CategoryVO;
import vo.MerchantVO;

import com.withiter.models.merchant.Category;
import com.withiter.models.merchant.Merchant;

/**
 *  所有商家的操作
 *	@author Cross Lee 
 */
public class MerchantController extends BaseController {

	/**
	 * 返回所有分类
	 */
	public static void allCategories(){
		List<Category> categories = Category.getAll();
		List<CategoryVO> categoriesVO = new ArrayList<CategoryVO>();
		for(Category c: categories){
			categoriesVO.add(CategoryVO.build(c));
		}
		renderJSON(categoriesVO);
	}
	
	public static void merchantByCategory(String cateType){
		List<Merchant> merchantList = Merchant.findByType(cateType);
		renderJSON(merchantList);
	}
	
	/**
	 * 进入分类商家
	 * @param page 分页
	 * @param cateType 菜系
	 * @param sortBy 排序
	 */
	public static void nextPage(int page, String cateType, String sortBy){
		List<Merchant> merchantList = Merchant.nextPage(cateType, page, sortBy);
		List<MerchantVO> merchantVOList = new ArrayList<MerchantVO>();
		for(Merchant m : merchantList){
			merchantVOList.add(MerchantVO.build(m));
		}
		renderJSON(merchantVOList);
	}
	
	public static void merchant(String id){
		Merchant m = Merchant.findById(id);
		renderJSON(MerchantVO.build(m));
	}
	
	
	
	
	
	public static void quhao(String merchantId){
		
	}
}
