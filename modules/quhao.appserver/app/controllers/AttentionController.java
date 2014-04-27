package controllers;

import java.util.ArrayList;
import java.util.List;

import vo.MerchantVO;

import com.withiter.models.merchant.Attention;
import com.withiter.models.merchant.Merchant;
import com.withiter.utils.StringUtils;

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
	
	/**
	 * 返回我的关注商家
	 */
	public static void marked(){
		String aid = params.get("aid");
		if(StringUtils.isEmpty(aid)){
			renderJSON(false);
		}
		
		List<Merchant> ms = Attention.getMerchantsByAid(aid);
		List<MerchantVO> avos = new ArrayList<MerchantVO>();
		MerchantVO mvo = null;
		if(ms != null){
			for(Merchant m : ms){
				mvo = MerchantVO.build(m);
				avos.add(mvo);
			}
		}
		renderJSON(avos);
	}
}