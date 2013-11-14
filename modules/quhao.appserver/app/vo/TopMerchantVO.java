package vo;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.withiter.jobs.CategoryJob;
import com.withiter.models.merchant.Tese;
import com.withiter.models.merchant.TopMerchant;

public class TopMerchantVO {

	private static Logger logger = LoggerFactory.getLogger(TopMerchantVO.class);

	public String id;
	public String name = "";
	public String address = "";
	public String[] telephone = { "" };

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

	public String merchantImage = "";
	public String mid;

	public static TopMerchantVO build(TopMerchant m) {
		TopMerchantVO vo = new TopMerchantVO();
		vo.id = m.id();
		// vo.address = m.address;
		// vo.averageCost = m.averageCost;
		// vo.cateType = m.cateType;
		// vo.closeTime = m.closeTime;
		// vo.description = m.description;
		vo.enable = m.enable;
		// vo.fuwu = m.fuwu;
		// vo.grade = m.grade;
		// vo.huanjing = m.huanjing;
		// vo.joinedDate = m.joinedDate;
		// vo.kouwei = m.kouwei;
		// vo.markedCount = m.markedCount;
		vo.name = m.name;
		// vo.nickName = m.nickName;
		// vo.openTime = m.openTime;
		// vo.tags = m.tags;
		// vo.telephone = m.telephone;
		// vo.teses = m.teses;
		// vo.xingjiabi = m.xingjiabi;
		vo.mid = m.mid;

		if (StringUtils.isEmpty(m.merchantImage)) {
			Iterator it = m.merchantImageSet.iterator();
			while (it.hasNext()) {
				vo.merchantImage = it.next().toString();
				break;
			}
		} else {
			vo.merchantImage = m.merchantImage;
		}

		try {
			vo.merchantImage = URLDecoder.decode(vo.merchantImage, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		logger.info(TopMerchantVO.class.getName() + " vo.merchantImage : "
				+ vo.merchantImage);

		return vo;
	}
}
