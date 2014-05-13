package com.withiter.quhao.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.withiter.quhao.R;
import com.withiter.quhao.vo.YouhuiVO;

public class YouhuiAdapter extends BaseAdapter {

	private ListView listView;
	private Activity activity;
	public List<YouhuiVO> youhuis;

	public YouhuiAdapter(Activity activity, ListView listView,List<YouhuiVO> youhuis) {
		super();
		this.activity = activity;
		this.listView= listView;
		this.youhuis = youhuis;

	}

	@Override
	public int getCount() {
		return youhuis.size();
	}

	@Override
	public Object getItem(int position) {
		return youhuis.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		YouhuiVO youhui = (YouhuiVO) getItem(position);
		synchronized (youhui) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				LayoutInflater inflator = (LayoutInflater) parent.getContext()
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflator.inflate(R.layout.youhui_list_item,
						null);
				holder.title = (TextView) convertView.findViewById(R.id.title);
				holder.content = (TextView) convertView.findViewById(R.id.content);

			}
			if (holder == null) {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.title.setTag("title_" + position);
			holder.title.setText(youhui.title);
			holder.title.setTag("content_" + position);
			holder.content.setText(youhui.content);
			
			convertView.setTag(holder);
			return convertView;
		}

	}

	class ViewHolder {
		TextView title;
		TextView content;
	}
}
