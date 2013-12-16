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

import com.amap.api.services.core.PoiItem;
import com.withiter.quhao.R;
import com.withiter.quhao.util.StringUtils;

public class MerchantNearByAdapter extends BaseAdapter {

	private ListView listView;
	public List<PoiItem> merchants;
	private static String TAG = MerchantNearByAdapter.class.getName();

	public MerchantNearByAdapter(Activity activity, ListView listView, List<PoiItem> merchants) {
		super();
		this.listView = listView;
		this.merchants = merchants;

	}

	@Override
	public int getCount() {
		return merchants.size();
	}

	@Override
	public Object getItem(int position) {
		return merchants.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		PoiItem merchant = (PoiItem) getItem(position);
		synchronized (merchant) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				LayoutInflater inflator = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflator.inflate(R.layout.merchant_nearby_item, null);
				holder.merchantName = (TextView) convertView.findViewById(R.id.merchantName);
				holder.merchantAddress = (TextView) convertView.findViewById(R.id.merchantAddress);
				holder.tel = (TextView) convertView.findViewById(R.id.tel);
			}
			if (holder == null) {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.merchantName.setTag("merchantName_" + position);
			holder.merchantName.setText(merchant.getTitle());
			
			holder.merchantAddress.setTag("merchantAddress_" + position);
			holder.merchantAddress.setText(merchant.getSnippet());
			
			if (StringUtils.isNull(merchant.getTel())) {
				holder.tel.setText("电话 ：暂无");
			} else {
				holder.tel.setText("电话：" + merchant.getTel());
			}
			convertView.setTag(holder);
			return convertView;
		}

	}

	class ViewHolder {
		TextView merchantName;
		TextView merchantAddress;
		TextView tel;
	}
}
