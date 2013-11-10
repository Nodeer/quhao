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
import com.withiter.quhao.vo.HelpVO;

public class HelpAdapter extends BaseAdapter {

	private ListView listView;
	private Activity activity;
	public List<HelpVO> helpList;

	public HelpAdapter(Activity activity, ListView listView,List<HelpVO> helpList) {
		super();
		this.activity = activity;
		this.listView= listView;
		this.helpList = helpList;

	}

	@Override
	public int getCount() {
		return helpList.size();
	}

	@Override
	public Object getItem(int position) {
		return helpList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		HelpVO help = (HelpVO) getItem(position);
		synchronized (help) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				LayoutInflater inflator = (LayoutInflater) parent.getContext()
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflator.inflate(R.layout.more_help_list_item,
						null);
				holder.title = (TextView) convertView.findViewById(R.id.help_title);
				holder.desc = (TextView) convertView.findViewById(R.id.help_desc);

				/*
				 * 重新设置图片的宽高
				 * holder.img.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
				 */
				/*
				 * 重新设置Layout 的宽高 holder.img.setLayoutParams(new
				 * LinearLayout.LayoutParams( LayoutParams.WRAP_CONTENT,
				 * LayoutParams.WRAP_CONTENT));
				 */

			}
			if (holder == null) {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.title.setText((position+1) + ". " + help.title);
			holder.desc.setText(help.desc);
			
			convertView.setTag(holder);
			return convertView;
		}

	}

	class ViewHolder {
		TextView title;
		TextView desc;
	}
}
