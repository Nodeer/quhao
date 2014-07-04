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
	public String userImage;
	public String nickName;
	public long up;
	public boolean showAddress;
	public boolean deleted;
	
	public boolean shareNiced = false;
	
	public ShareVO(String id, String content, String image, List<String> images, String aid, String x, String y, String address, String dis, String date, 
			String nickName, String userImage, long up, boolean showAdress,boolean deleted)
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
		this.nickName = nickName;
		this.userImage = userImage;
		this.up = up;
		this.showAddress = showAdress;
		this.deleted = deleted;
	}
}
