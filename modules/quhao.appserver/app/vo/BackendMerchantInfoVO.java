package vo;

import java.util.Date;

import com.google.code.morphia.annotations.Indexed;
import com.withiter.common.Constants;
import com.withiter.models.account.Account;
import com.withiter.models.merchant.Merchant;
/*make up with two parts:1) account information
 * 						 2) merchant information 
 * 
 * */
public class BackendMerchantInfoVO {
	public String id;
	public String name = "";
	public String nickName;
	public String merchantImage = "";
	public String address = "";
	public String[] telephone = {""};
	public String cateType = "";
	public String description;
	public String openTime;
	public String closeTime;	
	public int markedCount;
	public String joinedDate = new Date().toString();
	public boolean enable = false;
	public String x;
	public String y;
	
	public String phone = "";
	public String email = "";
	public String password = "";
	public String birthDay = "";
	public String userImage = "";
//	public Constants.MobileOSType mobileOS;
	public Date lastLogin = new Date();
	
	public static BackendMerchantInfoVO build(Merchant m, Account a) {
		BackendMerchantInfoVO vo = new BackendMerchantInfoVO();
		//merchant info
		vo.id = m.id();
		vo.address = m.address;
		vo.merchantImage = m.merchantImage;
		vo.cateType = m.cateType;
		vo.closeTime = m.closeTime;
		vo.description = m.description;
		vo.enable = m.enable;
		vo.joinedDate = m.joinedDate;
		vo.markedCount = m.markedCount;
		vo.name = m.name;
		vo.nickName = m.nickName;
		vo.openTime = m.openTime;
		vo.telephone = m.telephone;
		vo.x = m.x;
		vo.y = m.y;
		
		//account info
		vo.phone = a.phone;
		vo.email = a.email;
		vo.password = a.password;
		vo.birthDay = a.birthDay;
		vo.userImage = a.userImage;
		vo.lastLogin = a.lastLogin;
		
		return vo;
	}
	
}
