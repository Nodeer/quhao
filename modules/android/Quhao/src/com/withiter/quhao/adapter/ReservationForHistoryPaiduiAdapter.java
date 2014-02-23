package com.withiter.quhao.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.withiter.quhao.R;
import com.withiter.quhao.activity.CreateCommentActivity;
import com.withiter.quhao.activity.MerchantDetailActivity;
import com.withiter.quhao.activity.QuhaoHistoryStatesActivity;
import com.withiter.quhao.util.tool.ProgressDialogUtil;
import com.withiter.quhao.vo.ReservationVO;

public class ReservationForHistoryPaiduiAdapter extends BaseAdapter {

	private ListView listView;
	public List<ReservationVO> rvos;
	private QuhaoHistoryStatesActivity activity;
	private ProgressDialogUtil progress;

	public ReservationForHistoryPaiduiAdapter(QuhaoHistoryStatesActivity activity, ListView listView, List<ReservationVO> rvos) {
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
			ViewHolderHistoryPaidui holder = null;
			if (convertView == null) {
				holder = new ViewHolderHistoryPaidui();
				LayoutInflater inflator = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflator.inflate(R.layout.paidui_history_list_item, null);
				holder.merchantLayout = (LinearLayout) convertView.findViewById(R.id.merchantLayout);
				holder.merchantName = (TextView) convertView.findViewById(R.id.merchantName);
				holder.merchantAddress = (TextView) convertView.findViewById(R.id.merchantAddress);
				holder.myNumber = (TextView) convertView.findViewById(R.id.myNumber);
				holder.seatNo = (TextView) convertView.findViewById(R.id.seatNo);
				holder.beforeYou = (TextView) convertView.findViewById(R.id.beforeYou);
				holder.currentNumber = (TextView) convertView.findViewById(R.id.currentNumber);
				holder.commentBtn = (Button) convertView.findViewById(R.id.btn_comment);
				holder.isComment = (TextView) convertView.findViewById(R.id.is_comment);
			}
			if (holder == null) {
				holder = (ViewHolderHistoryPaidui) convertView.getTag();
			}
			
			final String merchantId = rvo.merchantId;
			holder.merchantLayout.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					progress = new ProgressDialogUtil(activity, R.string.empty, R.string.waitting, false);
					progress.showProgress();
					Intent intent = new Intent(activity, MerchantDetailActivity.class);
					intent.putExtra("merchantId", merchantId);
					intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
					activity.startActivity(intent);
					progress.closeProgress();
					activity.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
				}
			});
			holder.merchantName.setTag("merchantNamer_" + position);
			holder.merchantName.setText(rvo.merchantName);
			holder.merchantAddress.setTag("merchantAddress_" + position);
			holder.merchantAddress.setText(rvo.merchantAddress);

			holder.myNumber.setTag("myNumber_" + position);
			holder.myNumber.setText(rvo.myNumber);
			holder.seatNo.setTag("seatNo_" + position);
			holder.seatNo.setText(rvo.seatNumber);
			holder.beforeYou.setTag("beforeYou_" + position);
			holder.beforeYou.setText(rvo.beforeYou);
			holder.currentNumber.setTag("currentNumber_" + position);
			holder.currentNumber.setText(rvo.currentNumber);
			
			if(rvo.isCommented)
			{
				holder.isComment.setVisibility(View.VISIBLE);
				holder.commentBtn.setVisibility(View.GONE);
			}
			else
			{
				holder.isComment.setVisibility(View.GONE);
				holder.commentBtn.setVisibility(View.VISIBLE);
				final String reservationId = rvo.rId;
				holder.commentBtn.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {

						progress = new ProgressDialogUtil(activity, R.string.empty, R.string.waitting, false);
						progress.showProgress();
						try {
							Intent intent = new Intent();
							intent.putExtra("rId", reservationId);
							intent.setClass(activity, CreateCommentActivity.class);
							activity.startActivity(intent);
							activity.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							progress.closeProgress();
						}
					
					}
				});
			}
			
			convertView.setTag(holder);
			return convertView;
		}

	}

}
