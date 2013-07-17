package controllers;

import java.util.List;

import com.withiter.models.merchant.Category;

/**
 *  所有商家的操作
 *	@author Cross Lee 
 */
public class MerchantController extends BaseController {

	public static void allCategories(){
		List<Category> categories = Category.getAll();
		renderJSON(categories);
	}
}
