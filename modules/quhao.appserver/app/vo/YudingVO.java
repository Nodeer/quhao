package vo;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.withiter.common.Constants.YudingStatus;
import com.withiter.models.merchant.Yuding;

public class YudingVO extends ErrorVO {
	public String id;
	public String mid;
	public String aid;
	public int renshu;
	public String shijian;
	public boolean baojian;
	public boolean baojianOptional;
	public String xing;
	public String mobile;
	public YudingStatus status;
	
	public static YudingVO build(Yuding y) {
		YudingVO vo = new YudingVO();
		vo.id = y.id();
		vo.mid = y.mid;
		vo.aid = y.aid;
		vo.renshu = y.renshu;
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		vo.shijian = sdf.format(y.shijian);
		
		vo.baojian = y.baojian;
		vo.baojianOptional = y.baojianOptional;
		vo.xing = y.xing;
		vo.mobile = y.mobile;
		vo.status = y.status;
		return vo;
	}
}
