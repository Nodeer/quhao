package vo;

import java.util.Date;
import java.util.List;

import com.withiter.models.merchant.Tese;
import com.withiter.models.merchant.TopMerchant;

public class TopMerchantVO {

	public String id;
	public String name = "";
	public String address = "";
	public String[] telephone = {""};

	public String cateType;
	
	public String grade = "";
	public String averageCost = "";
	public List<String> tags = null;
	
	public int kouwei;
	public int huanjing;
	public int fuwu;
	public int xingjiabi;;
	
	public List<Tese> teses;
	public String nickName;
	public String description;
	public String openTime;
	public String closeTime;	
	public int markedCount;
	public boolean enable = false;
	public String joinedDate = new Date().toString();
	
	public static TopMerchantVO build(TopMerchant m) {
		TopMerchantVO vo = new TopMerchantVO();
		vo.id = m.id();
		vo.address = m.address;
		vo.averageCost = m.averageCost;
		vo.cateType = m.cateType;
		vo.closeTime = m.closeTime;
		vo.description = m.description;
		
		// TODO update the top merchant enable attribute
//		vo.enable = m.enable;
		vo.enable = true;
		vo.fuwu = m.fuwu;
		vo.grade = m.grade;
		vo.huanjing = m.huanjing;
		vo.joinedDate = m.joinedDate;
		vo.kouwei = m.kouwei;
		vo.markedCount = m.markedCount;
		vo.name = m.name;
		vo.nickName = m.nickName;
		vo.openTime = m.openTime;
		vo.tags = m.tags;
		vo.telephone = m.telephone;
		vo.teses = m.teses;
		vo.xingjiabi = m.xingjiabi;
		
		return vo;
	}
}
