package vo;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Date;

import play.Logger;

import com.withiter.models.activity.Activity;
import com.withiter.utils.ExceptionUtil;

public class ActivityVO {
	public String activityId;
	public String mid;
	public String cityCode;
	public String image;
	public Date start;
	public Date end;
	public boolean enable;
	
	public ActivityVO build(Activity a) {
		this.activityId = a.id();
		this.mid = a.mid;
		this.cityCode = a.cityCode;
//		this.image = a.image;
		Logger.debug(a.image);
		try {
			this.image = URLDecoder.decode(a.image, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			ExceptionUtil.getTrace(e);
		}
		Logger.debug(this.image);
		this.start = a.start;
		this.end = a.end;
		this.enable = a.enable;
		return this;
	}
}
