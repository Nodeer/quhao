package models;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChatRoomFactory {

	private static Map<String, ChatRoom> instance = null;
    public static Map rooms() {
        if(instance == null) {
            instance = new ConcurrentHashMap<String, ChatRoom>();
        }
        return instance;
    }
}
