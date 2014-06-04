package com.withiter.quhao.activity;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_10;
import org.java_websocket.handshake.ServerHandshake;

import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.withiter.quhao.R;
import com.withiter.quhao.adapter.MerchantChatAdapter;
import com.withiter.quhao.util.QuhaoLog;
import com.withiter.quhao.util.StringUtils;
import com.withiter.quhao.util.tool.QuhaoConstant;
import com.withiter.quhao.vo.ChatVO;

public class MerchantChatActivity extends QuhaoBaseActivity {
	
	public static boolean backClicked = false;
	private String LOGTAG = MerchantChatActivity.class.getName();
	
	private WebSocketClient mWebSocketClient;
	
	private ListView chatListView;
	
	private Button mBtnSend;// 发送btn
	
	private EditText chatMsgEdit;
	
	private List<ChatVO> chats;
	
	private MerchantChatAdapter chatAdapter;
	
	//uid=uid1&image=image1&mid=mid1&user=11
	private String uid;
	
	private String image;
	
	private String mid;
	
	private String user;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.merchant_chat_layout);
		super.onCreate(savedInstanceState);
		btnBack.setOnClickListener(goBack(this,this.getClass().getName()));
		
		chatListView = (ListView) this.findViewById(R.id.chat_listview);
		
		chatMsgEdit = (EditText) this.findViewById(R.id.chat_editmessage);
		
		this.mBtnSend = (Button) this.findViewById(R.id.chat_send);
		mBtnSend.setOnClickListener(this);
		
		chats = new ArrayList<ChatVO>();
		
		uid = getIntent().getStringExtra("uid");
		image = getIntent().getStringExtra("image");
		mid = getIntent().getStringExtra("mid");
		user = getIntent().getStringExtra("user");
		
		connectWebSocket();
	}
	
	private void connectWebSocket() {
        URI uri = null;
        
        
        try {
//        	userName = URLEncoder.encode(user,"UTF-8");
//        	String userName = new String(user.getBytes("iso-8859-1"),"UTF-8");
//        	String imageUrl = new String(image.getBytes("iso-8859-1"),"UTF-8");
        	
//        	String userName = Charsetfunctions.stringUtf8(Charsetfunctions.utf8Bytes("你好"));
//        	String imageUrl = Charsetfunctions.stringUtf8(Charsetfunctions.utf8Bytes(image));
        	
//            String url = "ws://www.quhao.la:9000/websocket/room/socket?uid=" + uid + "&image=" + image + "&mid=" + mid + "&user=" + user;
//            url = "ws://192.168.2.112:9000/websocket/room/socket?uid=uid1&image=image1&mid=mid1&user=" + userName;
//            url = "wss://www.quhao.la:9000/websocket/room/socket?uid=uid1&image=image1&mid=mid1&user=%E5%91%B5%E5%91%B5";
//            url = "http://192.168.2.112:9000/websocket/room/socket?uid=uid1&image=image1&mid=mid1&user=\"%\"E5\"%\"91\"%\"B5\"%\"E5\"%\"91\"%\"B5";
//            uri = URIUtils.createURI("ws", "192.168.2.112", 9000, "/websocket/room/socket", "uid=uid1&image=image1&mid=mid1&user=%E5%91%B5%E5%91%B5", null);
//            Uri uri1 = Uri.parse( "http://192.168.2.112:9000/websocket/room/socket?uid=uid1&image=image1&mid=mid1&user=\"%\"E5\"%\"91\"%\"B5\"%\"E5\"%\"91\"%\"B5");
        	String url = "wss://www.quhao.la:9000/websocket/room/socket";
            Log.e(LOGTAG, url);
            uri = new URI(url);
            
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        Map<String, String> headers = new HashMap<String, String>();
        headers.put("uid", uid);
        headers.put("image", image);
        headers.put("mid", mid);
        headers.put("user", user);
        mWebSocketClient = new WebSocketClient(uri, new Draft_10(), headers) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                Log.e("Websocket", "Opened");
//                mWebSocketClient.send("Hello from " + Build.MANUFACTURER + " " + Build.MODEL);
            }

            @Override
            public void onMessage(String s) {
            	Log.e("wjzwjz", s);
                final String message = s;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                    	if(null == chats)
                    	{
                    		chats = new ArrayList<ChatVO>();
                    	}
                    	
                    	ChatVO chat = null;
                    	if(StringUtils.isNotNull(message))
                    	{
                    		if(message.startsWith("join") || message.startsWith("leave"))
                    		{
                    			String[] strs = message.split(":");
                    			String type = strs[0];
//                        		Date date = new Date(Long.valueOf(strs[1]));
                        		String name = strs[1];
                        		String msgFrom = "server";
                        		chat = new ChatVO(type, name, "", "", "", msgFrom);
                    		}
                    		else
                    		{
                    			// 消息类型:昵称：用户ID：头像：消息内容
                        		String[] strs = message.split(":");
                        		String type = strs[0];
//                        		Date date = new Date(Long.valueOf(strs[1]));
                        		String name = strs[1];
                        		String userId = strs[2];
                        		String userImage = QuhaoConstant.HTTP_URL.substring(0, QuhaoConstant.HTTP_URL.length()-1) + strs[3];
                        		String msg = strs[4];
                        		//ws://www.quhao.la:9000/websocket/room/socket?uid=uid1&image=image1&mid=mid1&user=11
                        		String msgFrom = "server";
                        		if(uid.equals(userId))
                        		{
                        			msgFrom = "client";
                        		}
                        		
                        		chat = new ChatVO(type, name, userId, userImage, msg,msgFrom);
                    		}
                    		chats.add(chat);
                    		
                    		if (chatAdapter == null) {
                    			chatAdapter = new MerchantChatAdapter(MerchantChatActivity.this, chatListView, chats);
                    			chatListView.setAdapter(chatAdapter);
							}
                    		else
                    		{
                    			chatAdapter.chats = chats;
                    		}
                    		
                    		chatAdapter.notifyDataSetChanged();
                    	}
//                    	messagesView.setText(messagesView.getText() + "\n" + message);
                    }
                });
            }
            
            @Override
            public void onClose(int i, String s, boolean b) {
                Log.e("Websocket", "Closed " + s);
            }

            @Override
            public void onError(Exception e) {
                Log.e("Websocket", "Error " + e.getMessage());
                this.connect();
            }
        };
//        mWebSocketClient = new WebSocketClient(uri) {
//            @Override
//            public void onOpen(ServerHandshake serverHandshake) {
//                Log.e("Websocket", "Opened");
////                mWebSocketClient.send("Hello from " + Build.MANUFACTURER + " " + Build.MODEL);
//            }
//
//            @Override
//            public void onMessage(String s) {
//            	Log.e("wjzwjz", s);
//                final String message = s;
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                    	if(null == chats)
//                    	{
//                    		chats = new ArrayList<ChatVO>();
//                    	}
//                    	
//                    	ChatVO chat = null;
//                    	if(StringUtils.isNotNull(message))
//                    	{
//                    		if(message.startsWith("join") || message.startsWith("leave"))
//                    		{
//                    			String[] strs = message.split(":");
//                    			String type = strs[0];
////                        		Date date = new Date(Long.valueOf(strs[1]));
//                        		String name = strs[1];
//                        		String msgFrom = "server";
//                        		chat = new ChatVO(type, name, "", "", "", msgFrom);
//                    		}
//                    		else
//                    		{
//                    			// 消息类型:昵称：用户ID：头像：消息内容
//                        		String[] strs = message.split(":");
//                        		String type = strs[0];
////                        		Date date = new Date(Long.valueOf(strs[1]));
//                        		String name = strs[1];
//                        		String userId = strs[2];
//                        		String userImage = QuhaoConstant.HTTP_URL.substring(0, QuhaoConstant.HTTP_URL.length()-1) + strs[3];
//                        		String msg = strs[4];
//                        		//ws://www.quhao.la:9000/websocket/room/socket?uid=uid1&image=image1&mid=mid1&user=11
//                        		String msgFrom = "server";
//                        		if(uid.equals(userId))
//                        		{
//                        			msgFrom = "client";
//                        		}
//                        		
//                        		chat = new ChatVO(type, name, userId, userImage, msg,msgFrom);
//                    		}
//                    		chats.add(chat);
//                    		
//                    		if (chatAdapter == null) {
//                    			chatAdapter = new MerchantChatAdapter(MerchantChatActivity.this, chatListView, chats);
//                    			chatListView.setAdapter(chatAdapter);
//							}
//                    		else
//                    		{
//                    			chatAdapter.chats = chats;
//                    		}
//                    		
//                    		chatAdapter.notifyDataSetChanged();
//                    	}
////                    	messagesView.setText(messagesView.getText() + "\n" + message);
//                    }
//                });
//            }
//
//            @Override
//            public void onClose(int i, String s, boolean b) {
//                Log.e("Websocket", "Closed " + s);
//            }
//
//            @Override
//            public void onError(Exception e) {
//                Log.e("Websocket", "Error " + e.getMessage());
//                this.connect();
//            }
//        };
        mWebSocketClient.connect();
        
    }
	
	@Override
	public void finish() {
		super.finish();
		QuhaoLog.i(LOGTAG, LOGTAG + " finished");
	}

	@Override
	protected void onResume() {
		backClicked = false;
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
		QuhaoLog.i(LOGTAG, LOGTAG + " on pause");
		if (backClicked) {
			overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
		}
	}
	
	@Override
	public void onClick(View v) {
		
		switch(v.getId())
		{
			case R.id.chat_send: 
				if(StringUtils.isNull(chatMsgEdit.getText().toString().trim()))
				{
					Toast.makeText(this, "亲，请填写发送内容！", Toast.LENGTH_SHORT).show();
					return;
				}
				if (mWebSocketClient.getConnection().isOpen()) {
					
					mWebSocketClient.send(chatMsgEdit.getText().toString());
					chatMsgEdit.setText("");
				}
				else
				{
					Toast.makeText(this, "亲，聊天室已经关闭！", Toast.LENGTH_SHORT).show();
				}
				
				break;
			default:
				break;	
		}
		
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return false;
	}

}
