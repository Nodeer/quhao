package com.withiter.quhao.vo;

import java.util.List;


public class ShareVO {

	public String id;
	public String content;
	public String image;
	public List<String> images;
	public String aid;
	public String x;
	public String y;
	public String address;
	public String dis;
	public String date;
	public boolean deleted;
	
	public ShareVO(String id, String content, String image, List<String> images, String aid, String x, String y, String address, String dis, String date, boolean deleted)
	{
		this.id = id;
		this.content = content;
		this.image = image;
		this.images = images;
		this.aid = aid;
		this.x = x;
		this.y = y;
		this.address = address;
		this.dis = dis;
		this.date = date;
		this.deleted = deleted;
	}
}
