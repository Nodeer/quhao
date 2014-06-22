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
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.withiter.quhao.R;
import com.withiter.quhao.activity.CreateCommentActivity;
import com.withiter.quhao.activity.MerchantDetailActivity;
import com.withiter.quhao.activity.QuhaoHistoryStatesActivity;
import com.withiter.quhao.util.StringUtils;
import com.withiter.quhao.util.tool.AsynImageLoader;
import com.withiter.quhao.util.tool.ProgressDialogUtil;
import com.withiter.quhao.vo.ReservationVO;

public class ReservationForHistoryPaiduiAdapter extends BaseAdapter {

	private ListView listView;
	public List<ReservationVO> rvos;
	private QuhaoHistoryStatesActivity activity;
	public String isShowDelete;
	private ProgressDialogUtil progress;

	public ReservationForHistoryPaiduiAdapter(QuhaoHistoryStatesActivity activity, ListView listView, List<ReservationVO> rvos) {
		super();
		this.activity = activity;
		this.listView = listView;
		this.rvos = rvos;
		this.isShowDelete = "false";
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
				holder.merchantImg = (ImageView) convertView.findViewById(R.id.merchantImg);
				holder.merchantName = (TextView) convertView.findViewById(R.id.merchantName);
				holder.commentBtn = (Button) convertView.findViewById(R.id.btn_comment);
				holder.isComment = (TextView) convertView.findViewById(R.id.is_comment);
				holder.cb = (CheckBox) convertView.findViewById(R.id.item_cb);
				holder.cbLayout = (LinearLayout) convertView.findViewById(R.id.cb_layout);
				holder.date = (TextView) convertView.findViewById(R.id.date);
				holder.youhuiLayout = (RelativeLayout) convertView.findViewById(R.id.youhui_layout);
				holder.paiduiLayout = (RelativeLayout) convertView.findViewById(R.id.paidui_layout);
			}
			if (holder == null) {
				holder = (ViewHolderHistoryPaidui) convertView.getTag();
			}
			
			if("true".equals(isShowDelete))
			{
				holder.cbLayout.setVisibility(View.VISIBLE);
				holder.cb.setVisibility(View.VISIBLE);
				holder.cb.setChecked("true".equals(rvo.isChecked));
				
			}
			else
			{
				holder.cbLayout.setVisibility(View.GONE);
				holder.cb.setVisibility(View.GONE);
			}
			
			final String merchantId = rvo.merchantId;
			// if merchant has no image, set no_logo as default
			if(StringUtils.isNull(rvo.merchantImage)){
				holder.merchantImg.setImageResource(R.drawable.no_logo);
			}
			
			
			if (rvo.youhui) 
			{
				holder.youhuiLayout.setVisibility(View.VISIBLE);
			}
			else
			{
				holder.youhuiLayout.setVisibility(View.GONE);
			}
			
			String merchantImg = rvo.merchantImageBig;
			holder.merchantImg.setImageResource(R.drawable.no_logo);
			AsynImageLoader.getInstance().showImageAsyn(holder.merchantImg,position, merchantImg, R.drawable.no_logo);
			// get image from memory/SDCard/URL stream
			
			holder.paiduiLayout.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					Intent intent = new Intent(activity, MerchantDetailActivity.class);
					intent.putExtra("merchantId", merchantId);
					activity.startActivity(intent);
				}
			});
			
			/*
			holder.merchantImg.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					progress = new ProgressDialogUtil(activity, R.string.empty, R.string.waitting, false);
					progress.showProgress();
					Intent intent = new Intent(activity, MerchantDetailActivity.class);
					intent.putExtra("merchantId", merchantId);
					activity.startActivity(intent);
					progress.closeProgress();
				}
			});
			*/
			holder.merchantName.setTag("merchantNamer_" + position);
			holder.merchantName.setText(rvo.merchantName);

			holder.date.setTag("date_" + position);
			holder.date.setText(rvo.created);
			
			if(rvo.isCommented)
			{
				holder.isComment.setVisibility(View.GONE);
//				holder.commentBtn.setVisibility(View.GONE);
				holder.commentBtn.setVisibility(View.VISIBLE);
				holder.commentBtn.setBackgroundResource(R.drawable.btn_commented);
				holder.commentBtn.setEnabled(false);
//				holder.isComment.setText("已评价");
			}
			else
			{
				holder.isComment.setVisibility(View.VISIBLE);
				holder.commentBtn.setVisibility(View.GONE);
				String status = rvo.status;
				if ("canceled".equals(status)) {
					holder.isComment.setText("取消记录不能评价");
				}
				else if("expired".equals(status))
				{
					holder.isComment.setText("过期记录不能评价");
				}
				else if("finished".equals(status))
				{
					holder.isComment.setVisibility(View.GONE);
					holder.commentBtn.setVisibility(View.VISIBLE);
					holder.commentBtn.setEnabled(true);
					holder.commentBtn.setBackgroundResource(R.drawable.btn_comment);
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
							} catch (Exception e) {
								e.printStackTrace();
							} finally {
								progress.closeProgress();
							}
						
						}
					});
				}
				else
				{
					holder.isComment.setVisibility(View.GONE);
					holder.commentBtn.setVisibility(View.GONE);
				}
				
			}
			
			convertView.setTag(holder);
			return convertView;
		}

	}

}
