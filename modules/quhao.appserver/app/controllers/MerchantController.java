package controllers;

import java.util.List;

import com.withiter.models.merchant.Category;
import com.withiter.models.merchant.Merchant;

/**
 *  所有商家的操作
 *	@author Cross Lee 
 */
public class MerchantController extends BaseController {

	public static void allCategories(){
		List<Category> categories = Category.getAll();
		renderJSON(categories);
	}
	
	public static void merchantByCategory(String cateType){
		List<Merchant> merchantList = Merchant.findByType(cateType);
		renderJSON(merchantList);
	}
}
