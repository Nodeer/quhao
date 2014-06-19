package com.withiter.quhao.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.withiter.quhao.QHClientApplication;
import com.withiter.quhao.R;
import com.withiter.quhao.activity.MerchantChatActivity;
import com.withiter.quhao.activity.MerchantChatRoomsActivity;
import com.withiter.quhao.task.GetChatPortTask;
import com.withiter.quhao.task.JsonPack;
import com.withiter.quhao.util.StringUtils;
import com.withiter.quhao.util.tool.AsynImageLoader;
import com.withiter.quhao.util.tool.QuhaoConstant;
import com.withiter.quhao.vo.ReservationVO;

public class MerchantChatRoomAdapter extends BaseAdapter {

	private ListView listView;
	public List<ReservationVO> rvos;
	private MerchantChatRoomsActivity activity;

	public MerchantChatRoomAdapter(MerchantChatRoomsActivity activity, ListView listView, List<ReservationVO> rvos) {
		super();
		this.activity = activity;
		this.listView = listView;
		this.rvos = rvos;
	}

	@Override
	public int getCount() {
		return rvos.size();
	}

	@Override
	public Object getItem(int position) {
		return rvos.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ReservationVO rvo = (ReservationVO) getItem(position);
		synchronized (rvo) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				LayoutInflater inflator = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflator.inflate(R.layout.merchant_chat_room_list_item, null);
				holder.merchantImg = (ImageView) convertView.findViewById(R.id.merchantImg);
				holder.merchantName = (TextView) convertView.findViewById(R.id.merchantName);
				holder.btnChat = (Button) convertView.findViewById(R.id.btn_chat);
				holder.layout = (RelativeLayout) convertView.findViewById(R.id.layout);
			}
			if (holder == null) {
				holder = (ViewHolder) convertView.getTag();
			}
			
			String merchantImg = rvo.merchantImage;
			holder.merchantImg.setImageResource(R.drawable.no_logo);
			// get image from memory/SDCard/URL stream
			AsynImageLoader.getInstance().showImageAsyn(holder.merchantImg,position, merchantImg, R.drawable.no_logo);
			holder.merchantName.setTag("merchantNamer_" + position);
			holder.merchantName.setText(rvo.merchantName);
			
			final String mid = rvo.merchantId;
			final String merchantName = rvo.merchantName;
			holder.btnChat.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					
					
					final GetChatPortTask task = new GetChatPortTask(R.string.waitting, activity, "chat?mid=" +mid);
					task.execute(new Runnable() {
						
						@Override
						public void run() {
							JsonPack jsonPack = task.jsonPack;
							String port = jsonPack.getObj();
							if ("false".equals(port)) {
								Toast.makeText(activity, "亲，房间人数已满，请稍后再来。", Toast.LENGTH_SHORT).show();
								return;
							}
							Intent intent = new Intent();
							//uid=uid1&image=image1&mid=mid1&user=11
							String image = QHClientApplication.getInstance().accountInfo.userImage;
							if(StringUtils.isNotNull(image) && image.contains(QuhaoConstant.HTTP_URL))
							{
								image = "/" + image.substring(QuhaoConstant.HTTP_URL.length());
							}
							if (QHClientApplication.getInstance().accountInfo == null) {
								Toast.makeText(activity, "亲，账号登录过期了哦", Toast.LENGTH_SHORT).show();
								return;
							}
							intent.putExtra("uid", QHClientApplication.getInstance().accountInfo.accountId);
							intent.putExtra("image", image);
							intent.putExtra("mid", mid);
							intent.putExtra("user", QHClientApplication.getInstance().accountInfo.phone);
							intent.putExtra("merchantName", merchantName);
							intent.putExtra("port", port);
							intent.setClass(activity, MerchantChatActivity.class);
							activity.startActivity(intent);
						}
					},new Runnable() {
						
						@Override
						public void run() {
							Toast.makeText(activity, "亲，房间人数已满，请稍后再来。", Toast.LENGTH_SHORT).show();
							return;
						}
					});
					
				}
			});
			convertView.setTag(holder);
			return convertView;
		}

	}
	
	class ViewHolder {
		public ImageView merchantImg;
		public TextView merchantName;
		public Button btnChat;
		public RelativeLayout layout;
	}
	
}
