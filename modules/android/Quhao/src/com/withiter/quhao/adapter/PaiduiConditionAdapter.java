package com.withiter.quhao.adapter;

import java.text.NumberFormat;
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
import com.withiter.quhao.util.QuhaoLog;
import com.withiter.quhao.util.StringUtils;
import com.withiter.quhao.util.tool.AsynImageLoader;
import com.withiter.quhao.vo.Paidui;

public class PaiduiConditionAdapter extends BaseAdapter {

	private ListView listView;
	public List<Paidui> paiduis;
	private static String TAG = PaiduiConditionAdapter.class.getName();

	public PaiduiConditionAdapter(Activity activity, ListView listView, List<Paidui> paiduis) {
		super();
		this.listView = listView;
		this.paiduis = paiduis;
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
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				LayoutInflater inflator = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflator.inflate(R.layout.paidui_condition_list_item, null);
				holder.seatNo = (TextView) convertView.findViewById(R.id.seat_no);
				holder.currentNo = (TextView) convertView.findViewById(R.id.current_no);
				holder.maxNo = (TextView) convertView.findViewById(R.id.max_no);
			}
			if (holder == null) {
				holder = (ViewHolder) convertView.getTag();
			}


			holder.seatNo.setTag("seatNo_" + position);
			holder.seatNo.setText("座位人数：" + paidui.seatNo);
			
			holder.currentNo.setTag("currentNo_" + position);
			holder.currentNo.setText("最大号码：" + paidui.currentNumber);
			
			holder.maxNo.setTag("maxNo_" + position);
			holder.maxNo.setText("下一号码：" + paidui.maxNumber);
			
			convertView.setTag(holder);
			return convertView;
		}

	}

	class ViewHolder {
		TextView seatNo;
		TextView currentNo;
		TextView maxNo;
	}
}
