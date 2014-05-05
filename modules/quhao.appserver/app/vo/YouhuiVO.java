package vo;

import com.withiter.models.merchant.Youhui;

public class YouhuiVO {
	public String mid = "";
	public boolean enable = false;
	public String title = "";
	public String content = "";

	public static YouhuiVO build(Youhui yh) {
		YouhuiVO yvo = new YouhuiVO();
		yvo.mid = yh.mid;
		yvo.enable = yh.enable;
		yvo.title = yh.title;
		yvo.content = yh.content;
		return yvo;
	}

}
