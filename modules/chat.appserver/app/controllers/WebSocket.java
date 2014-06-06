package controllers;

import static play.libs.F.Matcher.ClassOf;
import static play.libs.F.Matcher.Equals;
import static play.mvc.Http.WebSocketEvent.SocketClosed;
import static play.mvc.Http.WebSocketEvent.TextFrame;

import java.util.Map;

import models.ChatRoom;
import models.ChatRoomFactory;
import models.chat.ChatPort;
import models.chat.MerchantPort;
import play.Logger;
import play.Play;
import play.data.validation.Required;
import play.libs.F.Either;
import play.libs.F.EventStream;
import play.libs.F.Promise;
import play.mvc.Controller;
import play.mvc.Http.WebSocketClose;
import play.mvc.Http.WebSocketEvent;
import play.mvc.WebSocketController;

public class WebSocket extends Controller {

	public static void room(String user) {
		render(user);
	}

	public static class ChatRoomSocket extends WebSocketController {
		private static String roomUserLimit = Play.configuration.getProperty("room.user.limit");

		public static void join(@Required String user, @Required String uid, @Required String mid, @Required String image) {

			if (validation.hasErrors()) {
				renderJSON(false);
			}

			// Get chat room with mid
			Map<String, ChatRoom> rooms = ChatRoomFactory.rooms();

			ChatRoom room = null;
			if (rooms.get(mid) == null) {
				room = new ChatRoom(mid);
				rooms.put(mid, room);
				// 创建MerchantPort对象
				MerchantPort mp = new MerchantPort();
				mp.port = Long.parseLong(Play.configuration.get("http.port").toString());
				mp.mid = mid;
				mp.save();
				Logger.info("MerchantPort(mid:%s, port:%d) created", mp.mid,mp.port);
				ChatPort.updateRoomCount(rooms.size(), mp.port);
				Logger.info("Update rooms counts(%d) on port %d", rooms.size(), mp.port);
			} else {
				room = rooms.get(mid);
			}

			Logger.info("This server rooms counts are : %d. current room id is %s.", rooms.size(), room.mid);
			if (room.socketNumber >= Integer.parseInt(roomUserLimit)) {
				return;
			}

			// Socket connected, join the chat room
			EventStream<ChatRoom.Event> roomMessagesStream = room.join(user, uid, image);

			// Loop while the socket is open
			while (inbound.isOpen()) {
				// Wait for an event (either something coming on the inbound
				// socket channel, or ChatRoom messages)
				Either<WebSocketEvent, ChatRoom.Event> e = await(Promise.waitEither(inbound.nextEvent(), roomMessagesStream.nextEvent()));

				// Case: User typed 'quit'
				for (String userMessage : TextFrame.and(Equals("quit")).match(e._1)) {
					room.leave(user, uid, image);
					outbound.send("quit:ok");
					disconnect();
				}

				// Case: TextEvent received on the socket
				for (String userMessage : TextFrame.match(e._1)) {
					Logger.debug("Got message -> %s:%s:%s:%s", user, uid, image, userMessage);
					room.say(user, uid, image, userMessage);
				}

				// Case: Someone joined the room
				for (ChatRoom.Join joined : ClassOf(ChatRoom.Join.class).match(e._2)) {
					outbound.send("join:%s", joined.user);
				}

				// Case: New message on the chat room
				for (ChatRoom.Message message : ClassOf(ChatRoom.Message.class).match(e._2)) {
					outbound.send("message:%s:%s:%s:%s", message.user, message.uid, message.image, message.text);
				}

				// Case: Someone left the room
				for (ChatRoom.Leave left : ClassOf(ChatRoom.Leave.class).match(e._2)) {
					// Logger.debug("leave:%s", left.user);
					outbound.send("leave:%s", left.user);
				}

				// Case: The socket has been closed
				for (WebSocketClose closed : SocketClosed.match(e._1)) {
					room.leave(user, uid, image);
					disconnect();
				}
			}
		}
	}
}
