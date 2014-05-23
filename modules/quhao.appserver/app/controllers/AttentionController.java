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
			Merchant merchant = Merchant.findByMid(mid);
			Account account = Account.findById(accountId);
			if(null == merchant || null == account){
				renderText("error");
			}
			
			if (attention == null) {
				
				Attention a=new Attention();
				a.accountId=accountId;
				a.mid=mid;
				a.flag=true;
				a.save();
				merchant.markedCount = merchant.markedCount + 1;
				account.guanzhu=account.guanzhu+1;
				
			}else{
				if(flag==1){
					attention.flag=false;
					account.guanzhu=account.guanzhu-1;
					merchant.markedCount = merchant.markedCount - 1;
				}else{
					attention.flag=true;
					merchant.markedCount = merchant.markedCount + 1;
					account.guanzhu=account.guanzhu+1;
				}	
				attention.save();
				
			}
			merchant.save();
			account.save();
			renderText("success");
		}else{
			renderText("error");
		}
	}
	
	/**
	 * @param userX  用户所在经度
	 * @param userY  用户所在纬度
	 * 返回我的关注商家
	 */
	public static void marked(String aid, double userX, double userY){
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