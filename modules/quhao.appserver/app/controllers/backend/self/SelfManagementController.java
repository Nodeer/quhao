package controllers.backend.self;

import java.util.List;

import vo.BackendMerchantInfoVO;
import cn.bran.japid.util.StringUtils;

import com.withiter.models.account.Account;
import com.withiter.models.backendMerchant.MerchantAccountRel;
import com.withiter.models.merchant.Merchant;

import controllers.BaseController;

public class SelfManagementController extends BaseController {
/*
 * 1) account information(included information:email or phone...) 
 * 2) Merchant information(included information:name, address...)
 * 
 * 
 * 
 * */
	
	/**
	 * 登录成功，通过uid查询出当前account的对应的merchant信息
	 * @param uid
	 */
	public static void index(String uid){
		Account account = Account.findById(uid);
		List<MerchantAccountRel> relList = MerchantAccountRel.getMerchantAccountRelList(uid);
		Merchant merchant = null;
		if(relList == null || relList.isEmpty()){
		
		}else{
			MerchantAccountRel rel = relList.get(0);
			String mid = rel.mid;
			merchant = Merchant.findById(mid);
		}
		BackendMerchantInfoVO bmivo = BackendMerchantInfoVO.build(merchant, account);
		renderJapid(bmivo);
	}
	
	public static void editMerchant(String uid, String mid){
		String merchantName = params.get("merchantName");
		String description = params.get("description");
		String address = params.get("address");
		String tel = params.get("tel");
		String cateType = params.get("cateType");
		String closeTime = params.get("closeTime");
		
		System.out.println(merchantName);
		System.out.println(address);
		System.out.println(tel);
		System.out.println(cateType);
		
		if(StringUtils.isEmpty(mid)){
			Merchant m = new Merchant();
			m.name = merchantName;
			m.description = description;
			m.address = address;
			m.telephone = tel.split(",");
			m.cateType = cateType;
			m.closeTime = closeTime;
			m.save();
			
			MerchantAccountRel rel = new MerchantAccountRel();
			rel.mid = m.id();
			rel.uid = uid;
			rel.save();
//			BackendMerchantInfoVO bmivo = BackendMerchantInfoVO.build(merchant, account);
//			renderJapidWith("japidviews.backend.self.SelfManagementController.index", args);
		}else{
			Merchant m = Merchant.findById(mid);
			m.name = merchantName;
			m.description = description;
			m.address = address;
			m.telephone = tel.split(",");
			m.cateType = cateType;
			m.closeTime = closeTime;
			m.save();
		}
		index(uid);
	}
	
}
