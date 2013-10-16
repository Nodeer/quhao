package vo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.withiter.models.merchant.Comment;
import com.withiter.models.merchant.Merchant;
import com.withiter.models.merchant.Tese;

public class MerchantVO {

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
	
	/**
	 * add by CROSS 2013-9-27
	 * eg: {2,4,6,8} 此商家有2人，4人，6人，8人桌
	 */
	public String[] seatType;
	
	public String x;
	public String y;
	
	public String merchantImage;
	
	
	public String commentAverageCost;
	public int commentXingjiabi;
	public int commentKouwei;
	public int commentHuanjing;
	public int commentFuwu;
	public String commentContent;
	public String commentDate;
	
	public static MerchantVO build(Merchant m) {
		MerchantVO vo = new MerchantVO();
		vo.id = m.id();
		vo.address = m.address;
		vo.averageCost = m.averageCost;
		vo.cateType = m.cateType;
		vo.closeTime = m.closeTime;
		vo.description = m.description;
		vo.enable = m.enable;
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
		vo.x = m.x;
		vo.y = m.y;
		vo.seatType = m.seatType;
		
		vo.merchantImage = m.merchantImage;
		
		return vo;
	}
	
	public static MerchantVO build(Merchant m, Comment c) {
		MerchantVO vo = new MerchantVO();
		vo.id = m.id();
		vo.address = m.address;
		vo.averageCost = m.averageCost;
		vo.cateType = m.cateType;
		vo.closeTime = m.closeTime;
		vo.description = m.description;
		vo.enable = m.enable;
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
		vo.x = m.x;
		vo.y = m.y;
		vo.seatType = m.seatType;
		
		vo.merchantImage = m.merchantImage;
		
		vo.commentAverageCost = c.averageCost;
		vo.commentContent = c.content;
		vo.commentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(c.date);
		vo.commentFuwu = c.fuwu;
		vo.commentHuanjing = c.huanjing;
		vo.commentKouwei = c.kouwei;
		vo.commentXingjiabi = c.xingjiabi;
		
		return vo;
	}
}
