package com.withiter.quhao.adapter;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.withiter.quhao.R;
import com.withiter.quhao.activity.CreditCostListActivity;
import com.withiter.quhao.activity.MerchantDetailActivity;
import com.withiter.quhao.util.StringUtils;
import com.withiter.quhao.vo.Credit;

public class CreditAdapter extends BaseAdapter {

	private ListView listView;
	public List<Credit> credits;
	private static String TAG = CreditAdapter.class.getName();
	public String isShowDelete;
	private Activity activity;

	public CreditAdapter(Activity activity, ListView listView, List<Credit> credits) {
		super();
		this.listView = listView;
		this.credits = credits;
		this.activity = activity;
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
				holder.cbLayout = (LinearLayout) convertView.findViewById(R.id.cb_layout);
				holder.cb = (CheckBox) convertView.findViewById(R.id.item_cb);
				holder.creditLayout = (LinearLayout) convertView.findViewById(R.id.credit_layout);
				
			}
			if (holder == null) {
				holder = (CreditCostHolder) convertView.getTag();
			}

			if("true".equals(isShowDelete))
			{
				holder.cbLayout.setVisibility(View.VISIBLE);
				holder.cb.setVisibility(View.VISIBLE);
				holder.cb.setChecked("true".equals(credit.isChecked));
				
			}
			else
			{
				holder.cb.setVisibility(View.GONE);
				holder.cbLayout.setVisibility(View.GONE);
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
			
			// 取消号码增加积分
			if ("canceled".equals(credit.status)) {
				holder.desc.setText("取消号码,系统返还一个积分。");
			}
			
			// 取号消费积分
			if ("getNumber".equals(credit.status)) {
				holder.desc.setText("排队取号,消费一个积分");
			}

			// 完成消费返还积分
			if ("finished".equals(credit.status)) {
				holder.desc.setText("完成消费,系统返还一个积分。");
			}
			
			// 评论增加积分
			if ("comment".equals(credit.status)) {
				holder.desc.setText("评论,增加一个积分。");
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
			
			final String merchantId = credit.merchantId;
			
			holder.creditLayout.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if(StringUtils.isNotNull(merchantId))
					{
						Intent intent = new Intent();
						intent.putExtra("merchantId", merchantId);
						intent.setClass(activity, MerchantDetailActivity.class);
						activity.startActivity(intent);
						activity.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
					}
					else
					{
						AlertDialog.Builder builder = new Builder(activity);
						builder.setTitle("温馨提示");
						builder.setMessage("对不起，亲，不是商家不能查看哦。");
						builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								dialog.dismiss();
							}
						});
						builder.create().show();
					}
					
				}
			});
			convertView.setTag(holder);

			return convertView;
		}

	}
	
	
}
