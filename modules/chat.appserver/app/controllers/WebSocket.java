package controllers;

import static play.libs.F.Matcher.ClassOf;
import static play.libs.F.Matcher.Equals;
import static play.mvc.Http.WebSocketEvent.SocketClosed;
import static play.mvc.Http.WebSocketEvent.TextFrame;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.StringUtils;

import models.ChatRoom;
import models.ChatRoomFactory;
import play.Logger;
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

		public static void join(@Required String user,@Required String uid,@Required String mid,@Required String image) {

			if(validation.hasErrors()){
				renderJSON(false);
			}
			
			// Get chat room with mid
			Map<String, ChatRoom> rooms = ChatRoomFactory.rooms();
			
			ChatRoom room = null;
			if(rooms.get(mid) == null){
				room = new ChatRoom(mid);
				rooms.put(mid, room);
			} else {
				room = rooms.get(mid);
			}
			
			
//			ChatRoom room = ChatRoom.get();

			// Socket connected, join the chat room
			EventStream<ChatRoom.Event> roomMessagesStream = room.join(user, uid, image);

			System.out.println("current rooms size is : " + rooms.size());
			System.out.println("current room mid is : " + room.mid);
			System.out.println("mid : "+mid);
			System.out.println("uid :"+uid);
			System.out.println("image :"+image);
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
					System.out.printf("get message:%s:%s\r\n", user, userMessage);
					room.say(user, uid, image, userMessage);
				}

				// Case: Someone joined the room
				for (ChatRoom.Join joined : ClassOf(ChatRoom.Join.class).match(e._2)) {
					outbound.send("join:%s", joined.user);
				}

				// Case: New message on the chat room
				for (ChatRoom.Message message : ClassOf(ChatRoom.Message.class).match(e._2)) {
					System.out.printf("send message:%s:%s:%s:%s", message.user, uid, image, message.text);
					System.out.println();
					outbound.send("message:%s:%s:%s:%s", message.user, uid, image, message.text);
				}

				// Case: Someone left the room
				for (ChatRoom.Leave left : ClassOf(ChatRoom.Leave.class).match(e._2)) {
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
