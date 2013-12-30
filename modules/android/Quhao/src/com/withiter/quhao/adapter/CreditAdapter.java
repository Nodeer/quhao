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
import com.withiter.quhao.util.AsyncImageLoader;
import com.withiter.quhao.vo.Credit;

public class CreditAdapter extends BaseAdapter {

	private ListView listView;
	public List<Credit> credits;
	private AsyncImageLoader asyncImageLoader;
	private static String TAG = CreditAdapter.class.getName();

	public CreditAdapter(Activity activity, ListView listView, List<Credit> credits) {
		super();
		this.listView = listView;
		this.credits = credits;
		asyncImageLoader = new AsyncImageLoader();

	}

	@Override
	public int getCount() {
		return credits.size();
	}

	@Override
	public Object getItem(int position) {
		return credits.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Credit credit = (Credit) getItem(position);
		synchronized (credit) {
			if ("finished".equals(credit.status) || "getNumber".equals(credit.status) || "credit".equals(credit.status)) {
				MerchantHolder holder = null;
				if (convertView == null) {
					holder = new MerchantHolder();
					LayoutInflater inflator = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

					convertView = inflator.inflate(R.layout.credit_cost_merchant_list_item, null);
					holder.merchantName = (TextView) convertView.findViewById(R.id.merchantName);
					holder.merchantAddress = (TextView) convertView.findViewById(R.id.merchantAddress);
					holder.action = (TextView) convertView.findViewById(R.id.action);
					holder.desc = (TextView) convertView.findViewById(R.id.description);

				}
				if (holder == null) {
					holder = (MerchantHolder) convertView.getTag();
				}

				holder.merchantName.setTag("merchantName_" + position);
				holder.merchantName.setText(credit.merchantName);

				holder.merchantAddress.setTag("merchantAddress_" + position);
				holder.merchantAddress.setText(credit.merchantAddress);

				// 取号消费积分
				if (credit.status.equals("getNumber")) {
					holder.action.setText("排队取号");
					holder.desc.setText("消费一个积分");
				}

				// 完成消费返还积分
				if (credit.status.equals("finished")) {
					holder.action.setText("完成消费");
					holder.desc.setText("系统返还一个积分");
				}

				convertView.setTag(holder);
			}

			// 兑换积分
			if ("exchange".equals(credit.status)) {
				ExchangeHolder holder = null;
				if (convertView == null) {
					holder = new ExchangeHolder();
					LayoutInflater inflator = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					convertView = inflator.inflate(R.layout.credit_cost_exchange_list_item, null);
					holder.action = (TextView) convertView.findViewById(R.id.action);
					holder.desc = (TextView) convertView.findViewById(R.id.description);

				}
				if (holder == null) {
					holder = (ExchangeHolder) convertView.getTag();
				}
				
				holder.action.setText("签到兑换");
				holder.desc.setText("连续5天签到，系统奖励一个积分");

				convertView.setTag(holder);
			}
			return convertView;
		}

	}

	class MerchantHolder {
		TextView merchantName;
		TextView merchantAddress;
		TextView action;
		TextView desc;
	}

	class ExchangeHolder {
		TextView action;
		TextView desc;
	}
}
