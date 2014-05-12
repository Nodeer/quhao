package controllers;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;

import play.modules.morphia.Model.MorphiaUpdateOperations;

import vo.MerchantVO;

import com.withiter.models.account.Account;
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
			Account account = Account.findById(accountId);
			if (attention == null) {
				
				Attention a=new Attention();
				a.accountId=accountId;
				a.mid=mid;
				a.flag=true;
				a.save();
				
				account.guanzhu=account.guanzhu+1;
				
			}else{
				if(flag==1){
					attention.flag=false;
					account.guanzhu=account.guanzhu-1;
				}else{
					attention.flag=true;
					account.guanzhu=account.guanzhu+1;
				}	
				attention.save();
				
			}
			account.save();
			renderText("success");
		}else{
			renderText("error");
		}
	}
	
	/**
	 * 返回我的关注商家
	 */
	public static void marked(double userX, double userY){
		String aid = params.get("aid");
		if(StringUtils.isEmpty(aid)){
			renderJSON(false);
		}
		
		List<Merchant> ms = Attention.getMerchantsByAid(aid);
		List<MerchantVO> avos = new ArrayList<MerchantVO>();
		MerchantVO mvo = null;
		if(ms != null){
			for(Merchant m : ms){
				mvo = MerchantVO.build(m, userX, userY);
				avos.add(mvo);
			}
		}
		renderJSON(avos);
	}
}