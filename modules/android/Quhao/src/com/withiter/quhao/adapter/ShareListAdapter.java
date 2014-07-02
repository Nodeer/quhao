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
import com.withiter.quhao.vo.ShareVO;

public class ShareListAdapter extends BaseAdapter {

	private ListView listView;
	public List<ShareVO> shares;
	private Activity activity;
	
	public ShareListAdapter(Activity activity, ListView listView, List<ShareVO> shares) {
		super();
		this.listView = listView;
		this.shares = shares;
		this.activity = activity;
	}

	@Override
	public int getCount() {
		return shares.size();
	}

	@Override
	public Object getItem(int position) {
		return shares.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ShareVO shareVO = (ShareVO) getItem(position);
		synchronized (shareVO) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				LayoutInflater inflator = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflator.inflate(R.layout.share_list_item, null);
				holder.userImg = (ImageView) convertView.findViewById(R.id.img);
				holder.userImg.setAdjustViewBounds(true);
				holder.content = (TextView) convertView.findViewById(R.id.merchantName);
				holder.distance = (TextView) convertView.findViewById(R.id.distance);
//				holder.pinfenImage = (ImageView) convertView.findViewById(R.id.pingfen);
			}
			
			if (holder == null) {
				holder = (ViewHolder) convertView.getTag();
			}

			convertView.setTag(holder);
			return convertView;
		}

	}

	class ViewHolder {
		ImageView userImg;
		ImageView shareImg;
		TextView content;
//		ImageView pinfenImage;
		TextView location;
		TextView distance;
	}
}
