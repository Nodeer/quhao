package vo;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import play.Play;

import com.withiter.models.admin.MerchantAccount;
import com.withiter.models.merchant.Merchant;
/*make up with two parts:1) account information
 * 						 2) merchant information 
 * 
 * */
public class BackendMerchantInfoVO {
	
	private static Logger logger = LoggerFactory.getLogger(BackendMerchantInfoVO.class);
	
	public String mid;
	public String name = "";
	public String nickName;
	public String merchantImage = "";
	public String address = "";
	public String telephone = "";
	public String cateType = "";
	public String cateType1 = "";
	public String description;
	public String openTime;
	public String closeTime;	
	public int markedCount;
	public Date joinedDate = new Date();
	public boolean enable = false;
	public String x;
	public String y;
	
	public boolean merchantExist = false;
	
	/**
	 * add by CROSS 2013-9-27
	 * eg: {2,4,6,8} 此商家有2人，4人，6人，8人桌
	 */
	public String[] seatType;
	
	public String cityCode = "";	//	城市代码
	
	public String aid;
	public String phone = "";
	public String email = "";
	public String password = "";
	public String birthDay = "";
	public String userImage = "";
//	public Constants.MobileOSType mobileOS;
	public Date lastLogin = new Date();
	
	public List<String> tags = null;
	
	public float averageCost = 0f;
	public float grade = 0f;
	public float kouwei = 0f;
	public float huanjing = 0f;
	public float fuwu = 0f;
	public float xingjiabi = 0f;
	
	public long openRequestCount = 0;
	
	public List<String> imgSrc = new ArrayList<String>();
	
	public static BackendMerchantInfoVO build(Merchant m, MerchantAccount a, long openRequestCount) {
		BackendMerchantInfoVO vo = new BackendMerchantInfoVO();
		//merchant info
		if(m != null){
			vo.merchantExist = true;
			vo.mid = m.id();
			vo.address = m.address;
			try {
				vo.merchantImage = URLDecoder.decode(m.merchantImage, "UTF-8");
				logger.debug(vo.merchantImage);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			
			vo.cateType = m.cateType;
			vo.cateType1 = m.cateType1;
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
			
			vo.grade = m.grade;
			vo.averageCost = m.averageCost;
			vo.kouwei = m.kouwei;
			vo.huanjing = m.huanjing;
			vo.fuwu = m.fuwu;
			vo.xingjiabi = m.xingjiabi;
			
			vo.seatType = m.seatType;
			
			vo.cityCode = m.cityCode;
			
			vo.openRequestCount = openRequestCount;
			
			
			String imageStorePath = Play.configuration.getProperty("image.store.path");
			// generate merchant image list
			if(!m.merchantImageSet.isEmpty()){
				Iterator it = m.merchantImageSet.iterator();
				while(it.hasNext()){
					vo.imgSrc.add(imageStorePath+it.next().toString());
				}
			}
		}
		
		
		//account info
		vo.aid = a.id();
		vo.email = a.email;
		vo.password = a.password;
		vo.lastLogin = a.lastLogin;
		
		return vo;
	}
	
	
	public static BackendMerchantInfoVO build(Merchant m, MerchantAccount a) {
		BackendMerchantInfoVO vo = new BackendMerchantInfoVO();
		//merchant info
		if(m != null){
			vo.merchantExist = true;
			vo.mid = m.id();
			vo.address = m.address;
			try {
				vo.merchantImage = URLDecoder.decode(m.merchantImage, "UTF-8");
				logger.debug(vo.merchantImage);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			
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
			
			vo.grade = m.grade;
			vo.averageCost = m.averageCost;
			vo.kouwei = m.kouwei;
			vo.huanjing = m.huanjing;
			vo.fuwu = m.fuwu;
			vo.xingjiabi = m.xingjiabi;
			
			vo.seatType = m.seatType;
			
			vo.cityCode = m.cityCode;
			
			String imageStorePath = Play.configuration.getProperty("image.store.path");
			// generate merchant image list
			if(!m.merchantImageSet.isEmpty()){
				Iterator it = m.merchantImageSet.iterator();
				while(it.hasNext()){
					vo.imgSrc.add(imageStorePath+it.next().toString());
				}
			}
		}
		
		
		//account info
		vo.aid = a.id();
		vo.email = a.email;
		vo.password = a.password;
		vo.lastLogin = a.lastLogin;
		
		return vo;
	}
	
}
