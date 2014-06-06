package com.withiter.models.chat;

import play.Play;

import com.google.code.morphia.annotations.Entity;

@Entity
public class ChatPort extends ChatPortEntity {

	/**
	 * 根据port查询数据库
	 * @param port
	 * @return
	 */
	public static ChatPort findByPort(String port) {
		MorphiaQuery q = ChatPort.q();
		q.filter("port", port);
		return q.first();
	}

	/**
	 * 查找一个可以分配的port
	 * @return
	 */
	public static ChatPort findOne() {
		long limit = Long.parseLong(Play.configuration.get("chatserver.room.limit").toString());
		MorphiaQuery q = ChatPort.q();
		q.filter("rooms <", limit);
		return q.first();
	}
}
