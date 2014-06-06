package com.withiter.quhao.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
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
						left.userImage = (ImageView) convertView.findViewById(R.id.iv_userhead);
						left.content = (TextView) convertView.findViewById(R.id.tv_chatcontent);
						left.content.setTag("content_" + position);
//						SpannableString spannableString = FaceConversionUtil.getInstace().getExpressionString(activity, chat.msg);
//						left.content.setText(spannableString);
						left.content.setText(chat.msg);
						
						if(StringUtils.isNotNull(chat.userImage) && chat.userImage.contains(QuhaoConstant.HTTP_URL))
						{
							AsynImageLoader.getInstance().showImageAsyn(left.userImage, position, chat.userImage, R.drawable.person_avatar);
						}
						
						convertView.setTag(left);
						
						break;
					case 1:
						convertView = inflator.inflate(R.layout.chatting_item_msg_text_right, null);
						right = new ViewHolderRight();
						right.userImage = (ImageView) convertView.findViewById(R.id.iv_userhead);
						right.content = (TextView) convertView.findViewById(R.id.tv_chatcontent);
						right.content.setTag("content_" + position);
//						SpannableString spannableString1 = FaceConversionUtil.getInstace().getExpressionString(activity, chat.msg);
//						right.content.setText(spannableString1);
						right.content.setText(chat.msg);
						
						if(StringUtils.isNotNull(chat.userImage) && chat.userImage.contains(QuhaoConstant.HTTP_URL))
						{
							AsynImageLoader.getInstance().showImageAsyn(right.userImage, position, chat.userImage, R.id.person_avatar);
						}
						
						convertView.setTag(right);
						break;
				}
				
			}else
			{
				switch(type)
				{
					case 0:
						left = (ViewHolderLeft) convertView.getTag();
						
						if(StringUtils.isNotNull(chat.userImage) && chat.userImage.contains(QuhaoConstant.HTTP_URL))
						{
							AsynImageLoader.getInstance().showImageAsyn(left.userImage, position, chat.userImage, R.id.person_avatar);
						}
						left.content.setTag("content_" + position);
//						SpannableString spannableString2 = FaceConversionUtil.getInstace().getExpressionString(activity, chat.msg);
//						left.content.setText(spannableString2);
						left.content.setText(chat.msg);
						
						break;
					case 1:
						right = (ViewHolderRight) convertView.getTag();
						
						right.content.setTag("content_" + position);
//						SpannableString spannableString3 = FaceConversionUtil.getInstace().getExpressionString(activity, chat.msg);
//						right.content.setText(spannableString3);
						right.content.setText(chat.msg);
						
						if(StringUtils.isNotNull(chat.userImage) && chat.userImage.contains(QuhaoConstant.HTTP_URL))
						{
							AsynImageLoader.getInstance().showImageAsyn(right.userImage, position, chat.userImage, R.id.person_avatar);
						}
						
						break;
				}
			}

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
		ImageView userImage;
		TextView content;
	}
	
	class ViewHolderRight {
		ImageView userImage;
		TextView content;
	}
	
}
