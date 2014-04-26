package controllers;

import java.util.Date;

import com.withiter.models.merchant.Open;

public class OpenController extends BaseController{
	
	/**
	 *  用户希望开通取号
	 * 
	 * @param mid 商家id
	 * @param accountId 用户id
	 * @return  String
	 */
	public static void openService(String mid,String accountId) {
		if(!mid.equals("")&&!accountId.equals("")){
			Open open = new Open();
			open.accountId=accountId;
			open.mid=mid;
			open.save();
			
			long num = open.getNumberByMid(mid);
			renderText(num);
		}else{
			renderText("error");
		}
	}
}
