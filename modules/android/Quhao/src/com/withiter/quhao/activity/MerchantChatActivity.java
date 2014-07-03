package com.withiter.quhao.activity;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.rockerhieu.emojicon.EmojiconGridFragment;
import com.rockerhieu.emojicon.EmojiconsFragment;
import com.rockerhieu.emojicon.emoji.Emojicon;
import com.withiter.quhao.R;
import com.withiter.quhao.adapter.MerchantChatAdapter;
import com.withiter.quhao.util.QuhaoLog;
import com.withiter.quhao.util.StringUtils;
import com.withiter.quhao.util.tool.PhoneTool;
import com.withiter.quhao.util.tool.QuhaoConstant;
import com.withiter.quhao.vo.ChatVO;

public class MerchantChatActivity extends FragmentActivity implements EmojiconGridFragment.OnEmojiconClickedListener, 
	EmojiconsFragment.OnEmojiconBackspaceClickedListener,OnClickListener {
	
	public static boolean backClicked = false;
	private String LOGTAG = MerchantChatActivity.class.getName();
	
	private WebSocketClient mWebSocketClient;
	
	private ListView chatListView;
	
	private Button mBtnSend;// 发送btn
	
	private EditText chatMsgEdit;
	
	private ImageButton faceBtn;
	
	private List<ChatVO> chats;
	
	private MerchantChatAdapter chatAdapter;
	
	//uid=uid1&image=image1&mid=mid1&user=11
	private String uid;
	
	private String image;
	
	private String mid;
	
	private String user;
	
	private String merchantName;
	
	private String port;
	
	private TextView merchantNameView;
	
	protected Button btnBack;
	
	private LinearLayout btnBackLayout;
	
	private View faceFragment; 

	// 网络是否可用
	protected static boolean networkOK = false;
	
	private long firstTime;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.merchant_chat_layout);
		networkOK = PhoneTool.isNetworkAvailable(this);
		btnBack = (Button) findViewById(R.id.back_btn);
		btnBackLayout = (LinearLayout) findViewById(R.id.back_btn_layout);
		faceBtn = (ImageButton) this.findViewById(R.id.btn_face);
		faceBtn.setOnClickListener(this);
		btnBackLayout.setOnClickListener(this);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
		btnBack.setOnClickListener(this);
		
		merchantNameView = (TextView) this.findViewById(R.id.merchantName);
		chatListView = (ListView) this.findViewById(R.id.chat_listview);
		
		chatMsgEdit = (EditText) this.findViewById(R.id.et_sendmessage);
		chatMsgEdit.setOnClickListener(this);
		
		this.mBtnSend = (Button) this.findViewById(R.id.btn_send);
		mBtnSend.setOnClickListener(this);
		
		faceFragment = this.findViewById(R.id.emojicons);
		faceFragment.setVisibility(View.GONE);

		chats = new ArrayList<ChatVO>();
		
		uid = getIntent().getStringExtra("uid");
		image = getIntent().getStringExtra("image");
		mid = getIntent().getStringExtra("mid");
		user = getIntent().getStringExtra("user");
		port = getIntent().getStringExtra("port");
		merchantName = getIntent().getStringExtra("merchantName");
		merchantNameView.setText(merchantName);
		connectWebSocket();
	}
	
	private void connectWebSocket() {
        URI uri = null;
        
        
        try {
        	
        	String userName = URLEncoder.encode(user,"UTF-8");
        	String imageUrl =URLEncoder.encode(image,"UTF-8");
        	
            String url = "ws://www.quhao.la:"+port+"/websocket/room/socket?uid=" + uid + "&image=" + imageUrl + "&mid=" + mid + "&user=" + userName;
            Log.e(LOGTAG, url);
            uri = new URI(url);
            
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        } catch (UnsupportedEncodingException e) {
        	
			e.printStackTrace();
			return;
		}

        mWebSocketClient = new WebSocketClient(uri) {
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
                    		if(!message.startsWith("join") && !message.startsWith("leave"))
                    		{
                    			// 消息类型:昵称：用户ID：头像：消息内容
                        		String[] strs = message.split(":");
                        		String type = strs[0];
//                        		Date date = new Date(Long.valueOf(strs[1]));
                        		String name = strs[1];
                        		String userId = strs[2];
                        		String userImage = QuhaoConstant.HTTP_URL.substring(0, QuhaoConstant.HTTP_URL.length()-1) + strs[3];
                        		String msg = strs[4];
                        		if (strs.length>5) {
									for (int i = 4; i < strs.length; i++) {
										if (i == strs.length-1) {
											msg = msg + strs[i];
											continue;
										}
										msg = strs[i] + ":";
									}
								}
                        		//ws://www.quhao.la:9000/websocket/room/socket?uid=uid1&image=image1&mid=mid1&user=11
                        		String msgFrom = "server";
                        		if(uid.equals(userId))
                        		{
                        			msgFrom = "client";
                        		}
                        		
                        		chat = new ChatVO(type, name, userId, userImage, msg,msgFrom);
                        		chats.add(chat);
                    		}
                    		
                    		if (chatAdapter == null) {
                    			DisplayImageOptions options = new DisplayImageOptions.Builder()
            					.showImageOnLoading(R.drawable.no_logo)
            					.showImageForEmptyUri(R.drawable.no_logo)
            					.showImageOnFail(R.drawable.no_logo)
            					.cacheInMemory(true)
            					.cacheOnDisk(true)
            					.considerExifParams(true)
            					.displayer(new RoundedBitmapDisplayer(20))
            					.build();
                    			chatAdapter = new MerchantChatAdapter(MerchantChatActivity.this, chatListView, chats,options,new AnimateFirstDisplayListener());
                    			chatListView.setAdapter(chatAdapter);
							}
                    		else
                    		{
                    			chatAdapter.chats = chats;
                    		}
                    		
                    		chatAdapter.notifyDataSetChanged();
                    	}
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
        mWebSocketClient.connect();
        
    }
	
//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		if (keyCode == KeyEvent.KEYCODE_BACK
//				&& ((FaceRelativeLayout) findViewById(R.id.FaceRelativeLayout))
//						.hideFaceView()) {
//			return true;
//		}
//		return super.onKeyDown(keyCode, event);
//	}
	
	
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
		
	}
	
	@Override
	protected void onDestroy() {

		if (mWebSocketClient != null) {
			mWebSocketClient.close();
			
		}
		mWebSocketClient = null;
		super.onDestroy();
	}
	
	@Override
	public void onClick(View v) {
		
		switch(v.getId())
		{
			case R.id.btn_send: 
				
				if(StringUtils.isNull(chatMsgEdit.getText().toString().trim()))
				{
//					Toast.makeText(this, "亲，请填写发送内容！", Toast.LENGTH_SHORT).show();
					return;
				}
				
				long currentTime = System.currentTimeMillis();
				
				if ((currentTime-firstTime)<=2000) {
					Toast toast = Toast.makeText(this, "亲，发送频率太高，请稍后再发", Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
					return;
				}
				
				if (mWebSocketClient.getConnection().isOpen()) {
					
					firstTime = currentTime;
					mWebSocketClient.send(chatMsgEdit.getText().toString().trim());
					chatMsgEdit.setText("");
				}
				else
				{
					Toast.makeText(this, "亲，聊天室已经关闭！", Toast.LENGTH_SHORT).show();
				}
				
				break;
			case R.id.btn_face:
				
				InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				boolean softFlag = im.hideSoftInputFromWindow(chatMsgEdit.getWindowToken(), 0);
				if (faceFragment.getVisibility() == View.VISIBLE) {
					
					faceFragment.setVisibility(View.GONE);
					faceBtn.setImageResource(R.drawable.ib_face);
					if (!softFlag) {
						im.showSoftInput(chatMsgEdit, 0);
//						im.hideSoftInputFromWindow(chatMsgEdit.getWindowToken(),
//			                    InputMethodManager.HIDE_NOT_ALWAYS);
					}
//					if(!im.isActive()){
//		            	im.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS);
//					}
					
				}
				else
				{
					if (softFlag) {
						im.hideSoftInputFromWindow(chatMsgEdit.getWindowToken(),
			                    InputMethodManager.HIDE_NOT_ALWAYS);
					}
					showFaceHandler.sendEmptyMessageDelayed(1000, 400);
					
				}
				break;
			case R.id.et_sendmessage:
				if (faceFragment != null && faceFragment.getVisibility() == View.VISIBLE) {
					faceFragment.setVisibility(View.GONE);
					faceBtn.setImageResource(R.drawable.ib_face);
				}
				break;
			case R.id.back_btn:
				if (mWebSocketClient != null && mWebSocketClient.getConnection() != null && mWebSocketClient.getConnection().isConnecting()) {
					mWebSocketClient.close();
				}
				AnimateFirstDisplayListener.displayedImages.clear();
				onBackPressed();
				finish();
				break;
			case R.id.back_btn_layout:
				if (mWebSocketClient != null && mWebSocketClient.getConnection() != null && mWebSocketClient.getConnection().isConnecting()) {
					mWebSocketClient.close();
				}
				onBackPressed();
				finish();
				break;
			default:
				break;	
		}
		
	}

	private Handler showFaceHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 1000) {
							
				faceFragment.setVisibility(View.VISIBLE);
				faceBtn.setImageResource(R.drawable.keyboard);
			}
		}
		
	};
	
	@Override
    public void onEmojiconClicked(Emojicon emojicon) {
        EmojiconsFragment.input(chatMsgEdit, emojicon);
    }

    @Override
    public void onEmojiconBackspaceClicked(View v) {
        EmojiconsFragment.backspace(chatMsgEdit);
    }
    
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
        	
            // 获得当前得到焦点的View，一般情况下就是EditText（特殊情况就是轨迹求或者实体案件会移动焦点）
            View v = getCurrentFocus();

            if (isShouldHideInput(v, ev)) {
                hideSoftInput(v.getWindowToken());
                faceFragment.setVisibility(View.GONE);
                faceBtn.setImageResource(R.drawable.ib_face);
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时没必要隐藏
     * 
     * @param v
     * @param event
     * @return
     */
    private boolean isShouldHideInput(View v, MotionEvent event) {
    	
    	if (v.getId() == R.id.btn_face) {
			return false;
		}
    	
    	if (faceBtn != null) {
			int[] l = {0,0};
			faceBtn.getLocationInWindow(l);
			int left = l[0], top = l[1], bottom = top + faceBtn.getHeight(), right = left
                    + faceBtn.getWidth();
			if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击EditText的事件，忽略它。
                return false;
            }
		}
    	
    	if ( mBtnSend != null) {
			int[] l = {0,0};
			mBtnSend.getLocationInWindow(l);
			int left = l[0], top = l[1], bottom = top + mBtnSend.getHeight(), right = left
                    + mBtnSend.getWidth();
			if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击EditText的事件，忽略它。
                return false;
            }
		}
    	
    	if (faceFragment != null && faceFragment.getVisibility() == View.VISIBLE) {
			int[] l = {0,0};
			faceFragment.getLocationInWindow(l);
			int left = l[0], top = l[1], bottom = top + faceFragment.getHeight(), right = left
                    + faceFragment.getWidth();
			if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击EditText的事件，忽略它。
                return false;
            }
		}
    	
    	
        if (v != null && (v instanceof EditText)) {
            int[] l = { 0, 0 };
            v.getLocationInWindow(l);
            int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left
                    + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击EditText的事件，忽略它。
                return false;
            } else {
                return true;
            }
        }
        // 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditView上，和用户用轨迹球选择其他的焦点
        return false;
    }

    /**
     * 多种隐藏软件盘方法的其中一种
     * 
     * @param token
     */
    private void hideSoftInput(IBinder token) {
        if (token != null) {
            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(token,
                    InputMethodManager.HIDE_NOT_ALWAYS);
            
//            if(im.isActive()){
//            	im.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS);
//			}
        }
    }
    
    @Override
    protected void onStop() {
    	if (mWebSocketClient != null && mWebSocketClient.getConnection() != null && mWebSocketClient.getConnection().isConnecting()) {
			mWebSocketClient.close();
		}
    	super.onStop();
    	
    }
    
    private static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

		static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

		@Override
		public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
			if (loadedImage != null) {
				ImageView imageView = (ImageView) view;
				boolean firstDisplay = !displayedImages.contains(imageUri);
				if (firstDisplay) {
					FadeInBitmapDisplayer.animate(imageView, 500);
					displayedImages.add(imageUri);
				}
			}
		}
	}
}
