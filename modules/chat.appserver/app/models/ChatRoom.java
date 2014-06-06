package models;

import models.chat.ChatPort;
import models.chat.MerchantPort;
import play.Logger;
import play.libs.F.ArchivedEventStream;
import play.libs.F.EventStream;

public class ChatRoom {

	
	public String mid;
	public int limit = 15;
	public volatile int socketNumber = 0;

	public ChatRoom(String mid) {
		super();
		this.mid = mid;
	}

	public ChatRoom(String mid, int limit) {
		super();
		this.mid = mid;
		this.limit = limit;
	}

	// ~~~~~~~~~ Let's chat!

	public ArchivedEventStream<ChatRoom.Event> chatEvents = new ArchivedEventStream<ChatRoom.Event>(limit);

	/**
	 * For WebSocket, when a user join the room we return a continuous event
	 * stream of ChatEvent
	 */
	public EventStream<ChatRoom.Event> join(String user, String uid, String image) {
		chatEvents.publish(new Join(user, uid, image));
		socketNumber++;
		Logger.debug("current room mid is : %s, uid is %s, nickname is %s joined. user counts are %d.", this.mid, uid, user, socketNumber);
		return chatEvents.eventStream();
	}

	/**
	 * A user leave the room
	 */
	public void leave(String user, String uid, String image) {
		chatEvents.publish(new Leave(user, uid, image));
		socketNumber--;
		Logger.debug("current room mid is : %s, uid is %s, nickname is %s, leaved. user counts are %d.", this.mid, uid, user, socketNumber);
		if (socketNumber == 0) {
			ChatRoomFactory.rooms().remove(this.mid);
			MerchantPort mp = MerchantPort.findByMid(this.mid);
			if(mp != null){
				mp.delete();
				Logger.info("MerchantPort (mid:%s, port:%d) deleted.", mp.mid, mp.port);
				ChatPort.updateRoomCount(ChatRoomFactory.rooms().size(), mp.port);
				Logger.info("Update rooms counts(%d) on port %d", ChatRoomFactory.rooms().size(), mp.port);
			}
			Logger.info("room id is : %s, and remove room from ChatRoomFactory.", this.mid);
		}
	}

	/**
	 * A user say something on the room
	 */
	public void say(String user, String uid, String image, String text) {
		if (text == null || text.trim().equals("")) {
			return;
		}
		chatEvents.publish(new Message(user, uid, image, text));
	}

	// ~~~~~~~~~ Chat room events
	public static abstract class Event {

		final public String type;
		final public Long timestamp;

		public Event(String type) {
			this.type = type;
			this.timestamp = System.currentTimeMillis();
		}

	}

	public static class Join extends Event {

		final public String user;
		final public String uid;
		final public String image;

		public Join(String user, String uid, String image) {
			super("join");
			this.user = user;
			this.uid = uid;
			this.image = image;
		}

	}

	public static class Leave extends Event {

		final public String user;
		final public String uid;
		final public String image;

		public Leave(String user, String uid, String image) {
			super("leave");
			this.user = user;
			this.uid = uid;
			this.image = image;
		}

	}

	public static class Message extends Event {

		final public String user;
		final public String uid;
		final public String image;
		final public String text;

		public Message(String user, String uid, String image, String text) {
			super("message");
			this.user = user;
			this.uid = uid;
			this.image = image;
			this.text = text;
		}

	}
}
