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
import com.withiter.quhao.vo.ReservationVO;

public class ReservationAdapter extends BaseAdapter {

	private ListView listView;
	public List<ReservationVO> rvos;
	private Activity activity;

	public ReservationAdapter(Activity activity, ListView listView,
			List<ReservationVO> rvos) {
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
				LayoutInflater inflator = (LayoutInflater) parent.getContext()
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflator.inflate(R.layout.reservation_list_item,
						null);
				holder.myNumber = (TextView) convertView
						.findViewById(R.id.myNumber);
				holder.seatNo = (TextView) convertView
						.findViewById(R.id.seatNo);
				holder.beforeYou = (TextView) convertView.findViewById(R.id.beforeYou);
				holder.currentNumber = (TextView) convertView.findViewById(R.id.currentNumber);

			}
			if (holder == null) {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.myNumber.setTag("myNumber_" + position);
			holder.myNumber.setText(rvo.myNumber);
			holder.seatNo.setTag("seatNo_" + position);
			holder.seatNo.setText(rvo.seatNumber);
			holder.beforeYou.setTag("beforeYou_" + position);
			holder.beforeYou.setText(rvo.beforeYou);
			holder.currentNumber.setTag("currentNumber_" + position);
			holder.currentNumber.setText(rvo.currentNumber);
			convertView.setTag(holder);
			return convertView;
		}

	}

	class ViewHolder {
		TextView myNumber;
		TextView seatNo;
		TextView beforeYou;
		TextView currentNumber;
	}
}
