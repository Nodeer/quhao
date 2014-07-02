package vo;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Date;
import java.util.List;

import com.withiter.models.social.Share;
import com.withiter.utils.ExceptionUtil;

public class ShareVO extends ErrorVO {

	public String id;
	public String content;
	public String image;
	public String[] images;
	public String aid;
	public String x;
	public String y;
	public String address;
	public Double dis;
	public Date date;
	public boolean deleted;
	
	public void build(Share s) {
		this.id = s.id();
		this.content = s.content;
		try {
			this.image = URLDecoder.decode(s.image, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
//		this.image = s.image;
		this.images = s.images;
		this.aid = s.aid;
		this.x = s.x;
		this.y = s.y;
		this.address = s.address;
		this.date = s.created;
		this.deleted = s.deleted;
	}
}
