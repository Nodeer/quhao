package models.chat;

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
}
