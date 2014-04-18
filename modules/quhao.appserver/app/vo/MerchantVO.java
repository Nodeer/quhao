package vo;

import java.io.UnsupportedEncodingException;

import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cn.bran.japid.util.StringUtils;

import com.withiter.models.merchant.Comment;
import com.withiter.models.merchant.Merchant;
import com.withiter.models.merchant.Tese;

import controllers.backend.self.SelfManagementController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MerchantVO {

	private static Logger logger = LoggerFactory.getLogger(MerchantVO.class);
	
	public String id;
	public String name = "";
	public String address = "";
	public String[] telephone = {""};

	public String cateType;
	
	public List<String> tags = null;
	
	public float averageCost = 0f;
	public float grade = 0f;
	public float kouwei = 0f;
	public float huanjing = 0f;
	public float fuwu = 0f;
	public float xingjiabi = 0f;
	
	public List<Tese> teses;
	public String nickName;
	public String description;
	public String openTime;
	public String closeTime;	
	public int markedCount;
	public boolean enable = false;
	public Date joinedDate = new Date();
	
	/**
	 * add by CROSS 2013-9-27
	 * eg: {2,4,6,8} 此商家有2人，4人，6人，8人桌
	 */
	public String[] seatType;
	
	public String x;
	public String y;
	
	public String merchantImage;
	
	public String cityCode;
	
	public String commentAverageCost;
	public float commentXingjiabi;
	public float commentKouwei;
	public float commentHuanjing;
	public float commentFuwu;
	public String commentContent;
	public String commentDate;
	//是否关注商家
	public boolean isAttention;
	
	/**
	 * @param m
	 * @return
	 */
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
		vo.cityCode = m.cityCode;
		
		try {
			vo.merchantImage = URLDecoder.decode(m.merchantImage, "UTF-8");
			logger.debug("vo.merchantImage"+vo.merchantImage);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
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
		
		vo.cityCode = m.cityCode;
		
		try {
			vo.merchantImage = URLDecoder.decode(m.merchantImage, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		vo.commentAverageCost = c.averageCost;
		vo.commentContent = StringUtils.isEmpty(c.content) ? "暂无评论" : c.content;
		vo.commentDate = StringUtils.isEmpty(c.content) ? "" : new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(c.modified);
		vo.commentFuwu = c.fuwu;
		vo.commentHuanjing = c.huanjing;
		vo.commentKouwei = c.kouwei;
		vo.commentXingjiabi = c.xingjiabi;
		
		return vo;
	}
	
	public static MerchantVO build(Merchant m, Comment c,boolean isAttention) {
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
		
		try {
			vo.merchantImage = URLDecoder.decode(m.merchantImage, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		vo.commentAverageCost = c.averageCost;
		vo.commentContent = StringUtils.isEmpty(c.content) ? "暂无评论" : c.content;
		vo.commentDate = StringUtils.isEmpty(c.content) ? "" : new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(c.modified);
		vo.commentFuwu = c.fuwu;
		vo.commentHuanjing = c.huanjing;
		vo.commentKouwei = c.kouwei;
		vo.commentXingjiabi = c.xingjiabi;
		
		vo.isAttention=isAttention;
		return vo;
	}
	
	public static MerchantVO buildSimpleVo(Merchant m) {
		MerchantVO vo = new MerchantVO();
		vo.id = m.id();
		vo.enable = m.enable;
		return vo;
	}
}
