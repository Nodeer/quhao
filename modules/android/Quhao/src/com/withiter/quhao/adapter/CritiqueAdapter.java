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
import com.withiter.quhao.vo.Critique;

public class CritiqueAdapter extends BaseAdapter {

	private ListView listView;
	private Activity activity;
	public List<Critique> critiques;

	public CritiqueAdapter(Activity activity, ListView listView,List<Critique> critiques) {
		super();
		this.activity = activity;
		this.listView= listView;
		this.critiques = critiques;

	}

	@Override
	public int getCount() {
		return critiques.size();
	}

	@Override
	public Object getItem(int position) {
		return critiques.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Critique critique = (Critique) getItem(position);
		synchronized (critique) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				LayoutInflater inflator = (LayoutInflater) parent.getContext()
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflator.inflate(R.layout.critique_list_item,
						null);
				holder.nickName = (TextView) convertView.findViewById(R.id.nickName);
				holder.level = (ImageView) convertView.findViewById(R.id.level);
				holder.star = (ImageView) convertView.findViewById(R.id.star);
				holder.average = (TextView) convertView.findViewById(R.id.average);
				holder.critiqueDesc = (TextView) convertView.findViewById(R.id.critique_desc);
				holder.updateDate = (TextView) convertView.findViewById(R.id.updateDate);

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

			holder.nickName.setText(critique.nickName);
			holder.average.setText(String.valueOf(critique.average));
			
			holder.critiqueDesc.setText(critique.desc);
			
			holder.updateDate.setText(critique.updateDate);
			holder.level.setTag("btnEnter_" + position);
			
			switch (critique.level) {
			case 1:
				holder.level.setImageResource(R.drawable.star00);
				break;
			case 2:
				holder.level.setImageResource(R.drawable.star10);
				break;
			case 3:
				holder.level.setImageResource(R.drawable.star20);
				break;
			case 4:
				holder.level.setImageResource(R.drawable.star30);
				break;
			case 5:
				holder.level.setImageResource(R.drawable.star40);
				break;
			case 6:
				holder.level.setImageResource(R.drawable.star50);
				break;
			default:
				break;
			}
			
			switch (critique.star) {
			case 1:
				holder.star.setImageResource(R.drawable.star00);
				break;
			case 2:
				holder.star.setImageResource(R.drawable.star10);
				break;
			case 3:
				holder.star.setImageResource(R.drawable.star20);
				break;
			case 4:
				holder.star.setImageResource(R.drawable.star30);
				break;
			case 5:
				holder.star.setImageResource(R.drawable.star40);
				break;
			case 6:
				holder.star.setImageResource(R.drawable.star50);
				break;
			default:
				break;
			}
			convertView.setTag(holder);
			return convertView;
		}

	}

	class ViewHolder {
		TextView nickName;
		ImageView level;
		ImageView star;
		TextView average;
		TextView critiqueDesc;
		TextView updateDate;
	}
}
