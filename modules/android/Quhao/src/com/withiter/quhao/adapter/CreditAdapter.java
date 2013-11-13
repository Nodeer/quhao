package com.withiter.quhao.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.withiter.quhao.R;
import com.withiter.quhao.util.AsyncImageLoader;
import com.withiter.quhao.util.QuhaoLog;
import com.withiter.quhao.util.AsyncImageLoader.ImageCallback;
import com.withiter.quhao.util.StringUtils;
import com.withiter.quhao.vo.Credit;
import com.withiter.quhao.vo.Merchant;

public class CreditAdapter extends BaseAdapter {

	private ListView listView;
	public List<Credit> credits;
	private AsyncImageLoader asyncImageLoader;
	private static String TAG = CreditAdapter.class.getName();

	public CreditAdapter(Activity activity, ListView listView,
			List<Credit> credits) {
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
			if("finished".equals(credit.status) || "getNumber".equals(credit.status))
			{
				MerchantHolder holder = null;
				if (convertView == null) {
					holder = new MerchantHolder();
					LayoutInflater inflator = (LayoutInflater) parent.getContext()
							.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					
						convertView = inflator.inflate(R.layout.credit_cost_merchant_list_item,
								null);
						holder.merchantName = (TextView) convertView
								.findViewById(R.id.merchantName);
						holder.merchantAddress = (TextView) convertView
								.findViewById(R.id.merchantAddress);
						holder.desc = (TextView) convertView.findViewById(R.id.description);
					
					
				}
				if (holder == null) {
					holder = (MerchantHolder) convertView.getTag();
				}
	
				
				holder.merchantName.setTag("merchantName_" + position);
				holder.merchantName.setText(credit.merchantName);
	
				holder.merchantAddress.setTag("merchantAddress_" + position);
				holder.merchantAddress.setText(credit.merchantAddress);
				
				if(credit.cost)
				{
					holder.desc.setText("增加积分,座位号:" + credit.seatNumber + ",我的号码:" +credit.myNumber);
				}
				
				
				convertView.setTag(holder);
			}
			else if("exchange".equals(credit.status))
			{
				ExchangeHolder holder = null;
				if (convertView == null) {
					holder = new ExchangeHolder();
					LayoutInflater inflator = (LayoutInflater) parent.getContext()
							.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					
					convertView = inflator.inflate(R.layout.credit_cost_exchange_list_item,
							null);
					holder.desc = (TextView) convertView.findViewById(R.id.description);
					
					
				}
				if (holder == null) {
					holder = (ExchangeHolder) convertView.getTag();
				}
				
				holder.desc.setText("使用签到增加积分");
				
				convertView.setTag(holder);
			}
			return convertView;
		}

	}

	class MerchantHolder {
		TextView merchantName;
		TextView merchantAddress;
		TextView desc;
	}
	
	class ExchangeHolder {
		TextView desc;
	}
}
