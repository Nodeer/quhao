package com.withiter.quhao.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.withiter.quhao.R;
import com.withiter.quhao.vo.Paidui;

public class PaiduiAdapter extends BaseAdapter {

	private ListView listView;
	public List<Paidui> paiduis;
	private Activity activity;

	public PaiduiAdapter(Activity activity, ListView listView, List<Paidui> paiduis) {
		super();
		this.listView = listView;
		this.paiduis = paiduis;
		this.activity = activity;
	}

	@Override
	public int getCount() {
		return paiduis.size();
	}

	@Override
	public Object getItem(int position) {
		return paiduis.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Paidui paidui = (Paidui) getItem(position);
		synchronized (paidui) {
			ViewHolderGetNoPaidui holder = null;
			if (convertView == null) {
				holder = new ViewHolderGetNoPaidui();
				LayoutInflater inflator = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflator.inflate(R.layout.get_number_paidui_list_item, null);
				holder.cb = (CheckBox) convertView.findViewById(R.id.paidui_cb);
				holder.seatNo = (TextView) convertView.findViewById(R.id.seat_number);
				holder.currentNo = (TextView) convertView.findViewById(R.id.current_number);
			}
			if (holder == null) {
				holder = (ViewHolderGetNoPaidui) convertView.getTag();
			}

			holder.cb.setTag("isChecked_" + position);
			holder.seatNo.setTag("seat_number_" + position);
			holder.currentNo.setTag("current_number_" + position);
			holder.seatNo.setText("桌位：" + paidui.seatNo);
			holder.currentNo.setText("当前号码：" + paidui.currentNumber);
			
			if (paidui.isChecked) {
				holder.cb.setChecked(true);
				holder.seatNo.setTextColor(activity.getResources().getColor(R.color.red_text));
				holder.currentNo.setTextColor(activity.getResources().getColor(R.color.red_text));
			}
			else
			{

				holder.cb.setChecked(false);
				holder.seatNo.setTextColor(activity.getResources().getColor(R.color.black));
				holder.currentNo.setTextColor(activity.getResources().getColor(R.color.black));
			
			}
			
			
			
			convertView.setTag(holder);
			return convertView;
		}

	}

}
