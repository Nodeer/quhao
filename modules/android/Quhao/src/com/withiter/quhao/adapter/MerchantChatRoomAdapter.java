package com.withiter.quhao.adapter;

import java.text.NumberFormat;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.withiter.quhao.R;
import com.withiter.quhao.activity.MerchantChatRoomsActivity;
import com.withiter.quhao.vo.Merchant;

public class MerchantChatRoomAdapter extends BaseAdapter {

	private ListView listView;
	public List<Merchant> merchants;
	private MerchantChatRoomsActivity activity;

	public MerchantChatRoomAdapter(MerchantChatRoomsActivity activity, ListView listView, List<Merchant> merchants) {
		super();
		this.activity = activity;
		this.listView = listView;
		this.merchants = merchants;
	}

	@Override
	public int getCount() {
		return merchants.size();
	}

	@Override
	public Object getItem(int position) {
		return merchants.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Merchant merchant = (Merchant) getItem(position);
		synchronized (merchant) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				LayoutInflater inflator = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflator.inflate(R.layout.merchant_chat_room_list_item, null);
				holder.seq = (TextView) convertView.findViewById(R.id.seq);
				holder.merchantName = (TextView) convertView.findViewById(R.id.merchantName);
				holder.distance = (TextView) convertView.findViewById(R.id.distance);
//				holder.renqi = (TextView) convertView.findViewById(R.id.renqi);
				holder.layout = (LinearLayout) convertView.findViewById(R.id.layout);
			}
			if (holder == null) {
				holder = (ViewHolder) convertView.getTag();
			}
			
			holder.seq.setTag("seq_" + position);
			if (position + 1 < 10) {
				holder.seq.setText((position + 1) + " ");
			}
			else
			{
				holder.seq.setText("" + (position + 1));
			}
			
			if (position == 0) {
				holder.seq.setBackgroundResource(R.drawable.bg_red_little);
			}
			
			if (position == 1 || position == 2 || position == 3) {
				holder.seq.setBackgroundResource(R.drawable.bg_blue_little);
			}
			
			if (position > 3) {
				holder.seq.setBackgroundResource(R.drawable.bg_gray_little);
			}
			
			holder.merchantName.setTag("merchantNamer_" + position);
			holder.merchantName.setText(merchant.name);
			holder.distance.setTag("distance_" + position);
			
			if(merchant.distance > 0)
			{
				if(merchant.distance>1000)
				{
					
					NumberFormat nf = NumberFormat.getNumberInstance();
			        nf.setMaximumFractionDigits(1);
			        holder.distance.setText(nf.format(merchant.distance/1000) + "km");
				}
				else
				{
					holder.distance.setText(String.valueOf((int)merchant.distance) + "m");
				}
				
			}
			else
			{
				holder.distance.setText("未定位");
			}
			
//			holder.renqi.setTag("renqi_" + position);
//			holder.renqi.setText("");
			
			
			
			convertView.setTag(holder);
			return convertView;
		}

	}
	
	class ViewHolder {
		public TextView seq;
		public TextView merchantName;
		public TextView distance;
//		public TextView renqi;
		public LinearLayout layout;
	}
	
}
