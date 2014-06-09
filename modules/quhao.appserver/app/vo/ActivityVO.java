package vo;

import java.util.Date;

import com.withiter.models.activity.Activity;

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
		this.image = a.image;
		this.start = a.start;
		this.end = a.end;
		this.enable = a.enable;
		return this;
	}
}
