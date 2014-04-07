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
import com.withiter.quhao.util.StringUtils;
import com.withiter.quhao.vo.Credit;

public class CreditAdapter extends BaseAdapter {

	private ListView listView;
	public List<Credit> credits;
	private static String TAG = CreditAdapter.class.getName();
	public String isShowDelete;

	public CreditAdapter(Activity activity, ListView listView, List<Credit> credits) {
		super();
		this.listView = listView;
		this.credits = credits;
		this.isShowDelete = "false";
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
			CreditCostHolder holder = null;
			if (convertView == null) {
				holder = new CreditCostHolder();
				LayoutInflater inflator = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

				convertView = inflator.inflate(R.layout.credit_cost_list_item, null);
				holder.merchantName = (TextView) convertView.findViewById(R.id.merchantName);
				holder.merchantAddress = (TextView) convertView.findViewById(R.id.merchantAddress);
				holder.desc = (TextView) convertView.findViewById(R.id.description);
				holder.date = (TextView) convertView.findViewById(R.id.date);
				holder.cb = (CheckBox) convertView.findViewById(R.id.item_cb);
			}
			if (holder == null) {
				holder = (CreditCostHolder) convertView.getTag();
			}

			if("true".equals(isShowDelete))
			{
				holder.cb.setVisibility(View.VISIBLE);
				holder.cb.setChecked("true".equals(credit.isChecked));
				
			}
			else
			{
				holder.cb.setVisibility(View.GONE);
			}
			
			if(StringUtils.isNotNull(credit.merchantName))
			{
				convertView.findViewById(R.id.merchantLayout).setVisibility(View.VISIBLE);
				holder.merchantName.setTag("merchantName_" + position);
				holder.merchantName.setText(credit.merchantName);
				holder.merchantAddress.setTag("merchantAddress_" + position);
				holder.merchantAddress.setText(credit.merchantAddress);
			}
			else
			{
				convertView.findViewById(R.id.merchantLayout).setVisibility(View.GONE);
			}
			
			// 取号消费积分
			if ("getNumber".equals(credit.status)) {
				holder.desc.setText("排队取号,消费一个积分");
			}

			// 完成消费返还积分
			if ("finished".equals(credit.status)) {
				holder.desc.setText("完成消费,系统返还一个积分");
			}
			
			// 评论增加积分
			if ("comment".equals(credit.status)) {
				holder.desc.setText("评论,增加一个积分");
			}

			if ("exchange".equals(credit.status)) {
				holder.desc.setText("签到数量足够，增加一个积分。");
			}
			
			//其实过期是没有积分消费记录增加的。也就是说数据库中不可能有这样的数据
			if ("expired".equals(credit.status)) {
				holder.desc.setText("过期，丢失一个积分。");
			}
			
			holder.desc.setTag("status_" + position);
			holder.date.setTag("created_" + position);
			holder.date.setText(credit.created);
			convertView.setTag(holder);

			return convertView;
		}

	}
	
	
}
