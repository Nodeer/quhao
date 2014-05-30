package models;

import java.util.*;

import play.libs.*;
import play.libs.F.*;

public class ChatRoom {
    
	public String mid;
	
	public ChatRoom(String mid) {
		super();
		this.mid = mid;
	}
	
    // ~~~~~~~~~ Let's chat! 
    
	final ArchivedEventStream<ChatRoom.Event> chatEvents = new ArchivedEventStream<ChatRoom.Event>(50);
    
    /**
     * For WebSocket, when a user join the room we return a continuous event stream
     * of ChatEvent
     */
    public EventStream<ChatRoom.Event> join(String user, String uid, String image) {
        chatEvents.publish(new Join(user, uid, image));
        return chatEvents.eventStream();
    }
    
    /**
     * A user leave the room
     */
    public void leave(String user, String uid, String image) {
        chatEvents.publish(new Leave(user, uid, image));
    }
    
    /**
     * A user say something on the room
     */
    public void say(String user,String uid, String image, String text) {
        if(text == null || text.trim().equals("")) {
            return;
        }
        chatEvents.publish(new Message(user, uid, image, text));
    }
    
    /**
     * For long polling, as we are sometimes disconnected, we need to pass 
     * the last event seen id, to be sure to not miss any message
     */
    public Promise<List<IndexedEvent<ChatRoom.Event>>> nextMessages(long lastReceived) {
        return chatEvents.nextEvents(lastReceived);
    }
    
    /**
     * For active refresh, we need to retrieve the whole message archive at
     * each refresh
     */
    public List<ChatRoom.Event> archive() {
        return chatEvents.archive();
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
        
        public Join(String user,String uid, String image) {
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
        
        public Leave(String user,String uid, String image) {
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
        
        public Message(String user,String uid, String image, String text) {
            super("message");
            this.user = user;
            this.uid = uid;
            this.image = image;
            this.text = text;
        }
        
    }
    
    // ~~~~~~~~~ Chat room factory

//    static ChatRoom instance = null;
//    public static ChatRoom get() {
//        if(instance == null) {
//            instance = new ChatRoom();
//        }
//        return instance;
//    }
    
}

