package com.withiter.quhao.vo;

public class ChatVO {

	public String type;
//	public Date date;
	public String name;
	public String userId;
	public String userImage;
	public String msg;
	public String msgFrom;
	
	public ChatVO(String type,String name,String userId,String userImage,String msg,String msgFrom)
	{
		this.type = type;
		this.name = name;
		this.userId = userId;
		this.userImage = userImage;
		this.msg = msg;
		this.msgFrom = msgFrom;
	}
}
