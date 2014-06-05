package models.chat;

import models.BaseModel;

public abstract class ChatPortEntity extends BaseModel {

	public long port;						// 聊天端口
	public int rooms = 0;					// 当前端口已经开启房间数
	
}
