package vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import play.Play;

import com.withiter.models.account.Account;
import com.withiter.models.merchant.Merchant;
/*make up with two parts:1) account information
 * 						 2) merchant information 
 * 
 * */
public class BackendMerchantInfoVO {
	public String mid;
	public String name = "";
	public String nickName;
	public String merchantImage = "";
	public String address = "";
	public String telephone = "";
	public String cateType = "";
	public String description;
	public String openTime;
	public String closeTime;	
	public int markedCount;
	public String joinedDate = new Date().toString();
	public boolean enable = false;
	public String x;
	public String y;
	
	public boolean merchantExist = false;
	
	/**
	 * add by CROSS 2013-9-27
	 * eg: {2,4,6,8} 此商家有2人，4人，6人，8人桌
	 */
	public String[] seatType;
	
	public String aid;
	public String phone = "";
	public String email = "";
	public String password = "";
	public String birthDay = "";
	public String userImage = "";
//	public Constants.MobileOSType mobileOS;
	public Date lastLogin = new Date();
	
	public List<String> imgSrc = new ArrayList<String>();
	
	public static BackendMerchantInfoVO build(Merchant m, Account a) {
		BackendMerchantInfoVO vo = new BackendMerchantInfoVO();
		//merchant info
		if(m != null){
			vo.merchantExist = true;
			vo.mid = m.id();
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
			vo.telephone = m.gTelephone();
			vo.x = m.x;
			vo.y = m.y;
			
			vo.seatType = m.seatType;
			
			
			String server = Play.configuration.getProperty("application.domain");
			String imageStorePath = Play.configuration.getProperty("image.store.path");
			// generate merchant image list
			if(!m.merchantImageSet.isEmpty()){
				Iterator it = m.merchantImageSet.iterator();
				while(it.hasNext()){
					vo.imgSrc.add(server+imageStorePath+it.next().toString());
				}
			}
		}
		
		
		//account info
		vo.aid = a.id();
		vo.phone = a.phone;
		vo.email = a.email;
		vo.password = a.password;
		vo.birthDay = a.birthDay;
		vo.userImage = a.userImage;
		vo.lastLogin = a.lastLogin;
		
		return vo;
	}
	
}
