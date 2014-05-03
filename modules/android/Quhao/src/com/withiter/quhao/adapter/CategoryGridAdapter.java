package com.withiter.quhao.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.withiter.quhao.R;
import com.withiter.quhao.vo.Category;

public class CategoryGridAdapter extends BaseAdapter {

	private List<? extends Object> list;
	private Context context;

	public CategoryGridAdapter(List<? extends Object> list, Context context) {
		this.list = list;
		this.context = context;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Category category = null;
		Object item = getItem(position);
		category = (Category) item;

		synchronized (item) {
			ViewHolder holder = null;
			if (null == convertView) {
				holder = new ViewHolder();
				LayoutInflater inflator = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflator.inflate(R.layout.category_item, null);
				holder.itemView = (TextView) convertView.findViewById(R.id.category_item_type);
			}

			if (holder == null) {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.itemView.setText(category.cateName);
			convertView.setTag(holder);
			return convertView;
		}

	}

	class ViewHolder {
		TextView itemView;
	}
}
