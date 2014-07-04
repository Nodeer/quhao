package vo;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.withiter.models.social.Share;

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
	public String userImage;
	public String nickName;
	public long up;
	public boolean showAddress;
	public boolean deleted;
	
	public void build(Share s) {
		this.id = s.id();
		this.content = s.content;
		
		if(!StringUtils.isEmpty(s.image)){
			try {
				this.image = URLDecoder.decode(s.image, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		if(!StringUtils.isEmpty(s.userImage)){
			try {
				this.userImage = URLDecoder.decode(s.userImage, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		
		this.nickName = s.nickName;
		this.up = s.up;
		this.images = s.images;
		this.aid = s.aid;
		this.x = s.x;
		this.y = s.y;
		this.address = s.address;
		this.showAddress = s.showAddress;
		this.date = s.created;
		this.deleted = s.deleted;
	}
}
