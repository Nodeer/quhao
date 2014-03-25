package com.withiter.quhao.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.withiter.quhao.R;
import com.withiter.quhao.vo.Category;

public class CategoryGridAdapter extends BaseAdapter {

	private List<? extends Object> list;
	private GridView grid;
	private Context context;

	public CategoryGridAdapter(List<? extends Object> list, GridView grid,
			Context context) {
		this.list = list;
		this.grid = grid;
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
		Drawable cachedImage = null;
		Category category = null;
		Object item = getItem(position);
		category = (Category) item;

		synchronized (item) {
			ViewHolder holder = null;
			if (null == convertView) {
				holder = new ViewHolder();
				LayoutInflater inflator = (LayoutInflater) parent.getContext()
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflator.inflate(R.layout.category_item, null);
				// holder.img = (ImageView) convertView.findViewById(R.id.img);
				holder.itemView = (TextView) convertView
						.findViewById(R.id.category_item_type);
				holder.countView = (TextView) convertView
						.findViewById(R.id.category_item_count);
			}

			if (holder == null) {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.itemView.setText(category.cateName);
			holder.countView.setText("(" + category.count + ")");
			convertView.setTag(holder);
			return convertView;
		}

	}

	class ViewHolder {
		// ImageView img;
		TextView itemView;
		TextView countView;
	}
}
