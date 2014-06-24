package vo;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.bran.japid.util.StringUtils;

import com.withiter.models.merchant.Comment;
import com.withiter.models.merchant.Merchant;
import com.withiter.models.merchant.Tese;
import com.withiter.utils.DistanceUtils;

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
	public boolean online = false;
	public boolean youhui = false;
	public Date joinedDate = new Date();
	public String dianpingFen = "0";
	public String dianpingLink = "";
	
	/**
	 * add by CROSS 2013-9-27
	 * eg: {2,4,6,8} 此商家有2人，4人，6人，8人桌
	 */
	public String[] seatType;
	
	public String x;
	public String y;
	
	public String merchantImage;
	public String merchantImageBig;
	
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
	//用户和商家之间距离
	public double distance;
	//希望开通数量
	public long openNum;
	//取消号码时候弹出优惠提示的条件
	public int checkTime;
	
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
		vo.online = m.online;
		vo.youhui = m.youhui;
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
		vo.dianpingFen = m.dianpingFen;
		vo.dianpingLink = m.dianpingLink;
		
		try {
			vo.merchantImage = URLDecoder.decode(m.merchantImage, "UTF-8");
			vo.merchantImageBig = URLDecoder.decode(m.merchantImageBig, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		return vo;
	}
	
	public static MerchantVO build(Merchant m, Comment c) {
		MerchantVO vo = build(m);
		
		vo.commentAverageCost = String.valueOf(c.averageCost);
		vo.commentContent = StringUtils.isEmpty(c.accountId) ? "暂无评论" : c.content;
		vo.commentDate = StringUtils.isEmpty(c.content) ? "" : new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(c.modified);
		vo.commentFuwu = c.fuwu;
		vo.commentHuanjing = c.huanjing;
		vo.commentKouwei = c.kouwei;
		vo.commentXingjiabi = c.xingjiabi;
		
		return vo;
	}
	
	
	public static MerchantVO build(Merchant m, Comment c,boolean isAttention) {
		MerchantVO vo = build(m, c);
		
		vo.isAttention=isAttention;
		return vo;
	}
	
	public static MerchantVO build(Merchant m, Comment c,boolean isAttention, long num) {
		MerchantVO vo = build(m, c, isAttention);
		
		vo.openNum = num;
		return vo;
	}
	
	public static MerchantVO buildSimpleVo(Merchant m) {
		MerchantVO vo = new MerchantVO();
		vo.id = m.id();
		vo.enable = m.enable;
		return vo;
	}
	
	/**
	 * @param m
	 * @param userX 经度
	 * @param userY 纬度
	 * @return
	 */
	public static MerchantVO build(Merchant m, double userX, double userY) {
		MerchantVO vo = build(m);
		if(userX != 0 && userY != 0){
			vo.distance = DistanceUtils.GetDistance(Double.parseDouble(m.y), Double.parseDouble(m.x), userX, userY);
		}else{
			vo.distance = -1;
		}
		
		return vo;
	}
	
	/**
	 * @param m
	 * @param userX 经度
	 * @param userY 纬度
	 * @param openNum 希望开通数
	 * @return
	 */
	public static MerchantVO build(Merchant m, double userX, double userY, long openNum) {
		MerchantVO vo = build(m);
		vo.openNum = openNum;
		if(userX != 0 && userY != 0){
			vo.distance = DistanceUtils.GetDistance(Double.parseDouble(m.y), Double.parseDouble(m.x), userX, userY);
		}else{
			vo.distance = -1;
		}
		
		return vo;
	}
}
