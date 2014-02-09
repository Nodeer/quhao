package controllers;

import com.withiter.models.merchant.Attention;

public class AttentionController extends BaseController{
	
	/**
	 *  商家评价
	 * 
	 * @param mid 商家id
	 * @param accountId 用户id
	 * @param flag=1 标示是取消关注 flag=0 关注
	 * @return  String
	 */
	public static void updateAttention(String mid,String accountId,int flag) {
		if(!mid.equals("")&&!accountId.equals("")){
			Attention attention = Attention.getAttentionById(mid,accountId);
			if (attention == null) {
				Attention a=new Attention();
				a.accountId=accountId;
				a.mid=mid;
				a.flag=true;
				a.save();
			}else{
				if(flag==1){
					attention.flag=false;
				}else{
					attention.flag=true;
				}	
				attention.save();
			}
			renderText("success");
		}else{
			renderText("error");
		}
	}
}
