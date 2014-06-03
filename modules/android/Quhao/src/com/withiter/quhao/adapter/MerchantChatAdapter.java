package com.withiter.quhao.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.withiter.quhao.R;
import com.withiter.quhao.util.StringUtils;
import com.withiter.quhao.util.tool.AsynImageLoader;
import com.withiter.quhao.util.tool.QuhaoConstant;
import com.withiter.quhao.vo.ChatVO;

public class MerchantChatAdapter extends BaseAdapter {

	private ListView listView;
	public List<ChatVO> chats;
	private Activity activity;

	public MerchantChatAdapter(Activity activity, ListView listView, List<ChatVO> chats) {
		super();
		this.listView = listView;
		this.chats = chats;
		this.activity = activity;
	}

	@Override
	public int getCount() {
		return chats.size();
	}

	@Override
	public Object getItem(int position) {
		return chats.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ChatVO chat = (ChatVO) getItem(position);
		int type = getItemViewType(position);
		ViewHolderLeft left = null;
		ViewHolderRight right = null;
		synchronized (chat) {
			
			LayoutInflater inflator = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			if (convertView == null) {
				switch(type)
				{
					case 0:
						convertView = inflator.inflate(R.layout.chatting_item_msg_text_left, null);
						left = new ViewHolderLeft();
						left.joinLayout = (LinearLayout) convertView.findViewById(R.id.join_layout);
						left.chatLayout = (RelativeLayout) convertView.findViewById(R.id.chat_layout);
						left.userImage = (ImageView) convertView.findViewById(R.id.iv_userhead);
						left.userName = (TextView) convertView.findViewById(R.id.tv_username);
						left.content = (TextView) convertView.findViewById(R.id.tv_chatcontent);
						left.joinName = (TextView) convertView.findViewById(R.id.tv_join);
						
						left.userName.setTag("userName_" + position);
						left.userName.setText(chat.name);
						
						left.joinLayout.setTag("join_layout_" + position);
						left.chatLayout.setTag("chat_layout_" + position);
						left.userName.setTag("userName_" + position);
						left.content.setTag("content_" + position);
						
						if ("join".equals(chat.type) || "leave".equals(chat.type)) 
						{
							left.joinLayout.setVisibility(View.VISIBLE);
							left.chatLayout.setVisibility(View.GONE);
							if("join".equals(chat.type))
							{
								left.joinName.setText(chat.name + "加入了");
							}
							else
							{
								left.joinName.setText(chat.name + "离开了");
							}
							
						}
						else
						{
							left.joinLayout.setVisibility(View.GONE);
							left.chatLayout.setVisibility(View.VISIBLE);
							if(StringUtils.isNotNull(chat.userImage) && chat.userImage.contains(QuhaoConstant.HTTP_URL))
							{
								AsynImageLoader.getInstance().showImageAsyn(left.userImage, position, chat.userImage, R.drawable.person_avatar);
							}
							
							left.userName.setText(chat.name);
							left.content.setText(chat.msg);
						}
						
						convertView.setTag(left);
						
						break;
					case 1:
						convertView = inflator.inflate(R.layout.chatting_item_msg_text_right, null);
						right = new ViewHolderRight();
						right.joinLayout = (LinearLayout) convertView.findViewById(R.id.join_layout);
						right.chatLayout = (RelativeLayout) convertView.findViewById(R.id.chat_layout);
						right.userImage = (ImageView) convertView.findViewById(R.id.iv_userhead);
						right.userName = (TextView) convertView.findViewById(R.id.tv_username);
						right.content = (TextView) convertView.findViewById(R.id.tv_chatcontent);
						right.joinName = (TextView) convertView.findViewById(R.id.tv_join);
						
						right.userName.setTag("userName_" + position);
						right.userName.setText(chat.name);
						
						right.joinLayout.setTag("join_layout_" + position);
						right.chatLayout.setTag("chat_layout_" + position);
						right.userName.setTag("userName_" + position);
						right.content.setTag("content_" + position);
						
						if ("join".equals(chat.type) || "leave".equals(chat.type)) 
						{
							right.joinLayout.setVisibility(View.VISIBLE);
							right.chatLayout.setVisibility(View.GONE);
							if("join".equals(chat.type))
							{
								right.joinName.setText(chat.name + "加入了");
							}
							else
							{
								right.joinName.setText(chat.name + "离开了");
							}
							
						}
						else
						{
							right.joinLayout.setVisibility(View.GONE);
							right.chatLayout.setVisibility(View.VISIBLE);
							if(StringUtils.isNotNull(chat.userImage) && chat.userImage.contains(QuhaoConstant.HTTP_URL))
							{
								AsynImageLoader.getInstance().showImageAsyn(right.userImage, position, chat.userImage, R.id.person_avatar);
							}
							right.userName.setText(chat.name);
							right.content.setText(chat.msg);
						}
						
						convertView.setTag(right);
						break;
				}
//				if("server".equals(chat.msgFrom))
//				{
//					convertView = inflator.inflate(R.layout.chatting_item_msg_text_left, null);
//				}
//				else
//				{
//					convertView = inflator.inflate(R.layout.chatting_item_msg_text_right, null);
//				}
				
			}else
			{
				switch(type)
				{
					case 0:
						left = (ViewHolderLeft) convertView.getTag();
						
						left.userName.setTag("userName_" + position);
						left.userName.setText(chat.name);
						
						left.joinLayout.setTag("join_layout_" + position);
						left.chatLayout.setTag("chat_layout_" + position);
						left.userName.setTag("userName_" + position);
						left.content.setTag("content_" + position);
						
						if ("join".equals(chat.type) || "leave".equals(chat.type)) 
						{
							left.joinLayout.setVisibility(View.VISIBLE);
							left.chatLayout.setVisibility(View.GONE);
							if("join".equals(chat.type))
							{
								left.joinName.setText(chat.name + "加入了");
							}
							else
							{
								left.joinName.setText(chat.name + "离开了");
							}
							
						}
						else
						{
							left.joinLayout.setVisibility(View.GONE);
							left.chatLayout.setVisibility(View.VISIBLE);
//							AsynImageLoader.getInstance().showImageAsyn(left.userImage, position, chat.userImage, R.id.person_avatar);
							if(StringUtils.isNotNull(chat.userImage) && chat.userImage.contains(QuhaoConstant.HTTP_URL))
							{
								AsynImageLoader.getInstance().showImageAsyn(left.userImage, position, chat.userImage, R.id.person_avatar);
							}
							left.userName.setText(chat.name);
							left.content.setText(chat.msg);
						}
						
//						convertView.setTag(left);
						
						break;
					case 1:
						right = (ViewHolderRight) convertView.getTag();
						right.userName.setTag("userName_" + position);
						right.userName.setText(chat.name);
						
						right.joinLayout.setTag("join_layout_" + position);
						right.chatLayout.setTag("chat_layout_" + position);
						right.userName.setTag("userName_" + position);
						right.content.setTag("content_" + position);
						
						if ("join".equals(chat.type) || "leave".equals(chat.type)) 
						{
							right.joinLayout.setVisibility(View.VISIBLE);
							right.chatLayout.setVisibility(View.GONE);
							if("join".equals(chat.type))
							{
								right.joinName.setText(chat.name + "加入了");
							}
							else
							{
								right.joinName.setText(chat.name + "离开了");
							}
							
						}
						else
						{
							right.joinLayout.setVisibility(View.GONE);
							right.chatLayout.setVisibility(View.VISIBLE);
//							AsynImageLoader.getInstance().showImageAsyn(right.userImage, position, chat.userImage, R.id.person_avatar);
							if(StringUtils.isNotNull(chat.userImage) && chat.userImage.contains(QuhaoConstant.HTTP_URL))
							{
								AsynImageLoader.getInstance().showImageAsyn(right.userImage, position, chat.userImage, R.id.person_avatar);
							}
							right.userName.setText(chat.name);
							right.content.setText(chat.msg);
						}
						
//						convertView.setTag(right);
						break;
				}
			}
//			if (holder == null) {
//				
//			}

			return convertView;
		}

	}
	
	
	@Override
	public int getItemViewType(int position) {
		ChatVO chat = (ChatVO) getItem(position);
		if ("server".equals(chat.msgFrom)) {
			return 0;
		}
		return 1;
	}
	
	@Override
	public int getViewTypeCount() {
		return 2;
	}
	
	class ViewHolderLeft {
		LinearLayout joinLayout;
		RelativeLayout chatLayout;
		ImageView userImage;
		TextView content;
		TextView userName;
		TextView joinName;
	}
	
	class ViewHolderRight {
		LinearLayout joinLayout;
		RelativeLayout chatLayout;
		ImageView userImage;
		TextView content;
		TextView userName;
		TextView joinName;
	}
	
}
