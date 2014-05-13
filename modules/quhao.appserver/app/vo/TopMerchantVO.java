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

import com.withiter.models.merchant.Tese;
import com.withiter.models.merchant.TopMerchant;
import com.withiter.utils.ExceptionUtil;

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
	public boolean online = false;
	public String joinedDate = new Date().toString();

	public String merchantImage = "";
	public String mid;

	public static TopMerchantVO build(TopMerchant m) {
		TopMerchantVO vo = new TopMerchantVO();
		vo.id = m.id();
		vo.enable = m.enable;
		vo.online = m.online;
		vo.name = m.name;
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
			e.printStackTrace();
			logger.error(ExceptionUtil.getTrace(e));
		}

		logger.info(TopMerchantVO.class.getName() + " vo.merchantImage : "
				+ vo.merchantImage);

		return vo;
	}
}
