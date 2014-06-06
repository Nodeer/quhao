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
		q.filter("port", Long.parseLong(port));
		return q.first();
	}

	/**
	 * 更新此port对应的房间数
	 * @param port
	 */
	public static void updateRoomCount(int roomCount, long port) {
		ChatPort cp = findByPort(String.valueOf(port));
		cp.rooms = roomCount;
		cp.save();
	}
}
