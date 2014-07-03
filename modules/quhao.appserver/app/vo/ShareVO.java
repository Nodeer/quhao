package vo;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

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
	
	
	public static void main(String[] args) throws InterruptedException {
		List<ShareVO> list = new ArrayList<ShareVO>();
		ShareVO s = null;
		for(int i = 0; i< 10;i++){
			s = new ShareVO();
			s.id = i+"";
			s.date = new Date();
			list.add(s);
			Thread.sleep(500);
		}
		
		Collections.shuffle(list);
		for(ShareVO svo : list){
			System.out.println(svo.id+", "+ svo.date);
		}
		
		System.out.println("=============");
		
		// 对list按照时间排序
		Collections.sort(list, new Comparator(){
			@Override
			public int compare(Object arg0, Object arg1) {
				if(((ShareVO)arg0).date.before(((ShareVO)arg1).date)){
					return 1;
				} else {
					return -1;
				}
			}
			
		});
		
		for(ShareVO svo : list){
			System.out.println(svo.id+", "+ svo.date);
		}
	}
}
