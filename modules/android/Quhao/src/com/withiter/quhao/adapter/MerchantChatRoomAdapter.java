package com.withiter.quhao.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.withiter.quhao.QHClientApplication;
import com.withiter.quhao.R;
import com.withiter.quhao.activity.MerchantChatActivity;
import com.withiter.quhao.activity.MerchantChatRoomsActivity;
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
				holder.merchantAddress = (TextView) convertView.findViewById(R.id.merchantAddress);
				holder.layout = (RelativeLayout) convertView.findViewById(R.id.layout);
			}
			if (holder == null) {
				holder = (ViewHolder) convertView.getTag();
			}
			
			// if merchant has no image, set no_logo as default
			if(StringUtils.isNull(rvo.merchantImage)){
				holder.merchantImg.setImageResource(R.drawable.no_logo);
			}
			
			String merchantImg = rvo.merchantImage;
			// get image from memory/SDCard/URL stream
			AsynImageLoader.getInstance().showImageAsyn(holder.merchantImg,position, merchantImg, R.drawable.no_logo);
			holder.merchantName.setTag("merchantNamer_" + position);
			holder.merchantName.setText(rvo.merchantName);
			holder.merchantAddress.setTag("merchantAddress_" + position);
			holder.merchantAddress.setText(rvo.merchantAddress);
			
			final String mid = rvo.merchantId;
			
			holder.layout.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					
					Intent intent = new Intent();
					//uid=uid1&image=image1&mid=mid1&user=11
					String image = QHClientApplication.getInstance().accountInfo.userImage;
					if(StringUtils.isNotNull(image) && image.contains(QuhaoConstant.HTTP_URL))
					{
						image = "/" + image.substring(QuhaoConstant.HTTP_URL.length());
					}
					intent.putExtra("uid", QHClientApplication.getInstance().accountInfo.accountId);
					intent.putExtra("image", image);
					intent.putExtra("mid", mid);
					intent.putExtra("user", QHClientApplication.getInstance().accountInfo.nickName);
					intent.setClass(activity, MerchantChatActivity.class);
					activity.startActivity(intent);
					activity.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left); 
					
				}
			});
			convertView.setTag(holder);
			return convertView;
		}

	}
	
	class ViewHolder {
		public ImageView merchantImg;
		public TextView merchantName;
		public TextView merchantAddress;
		public RelativeLayout layout;
	}
	
}
