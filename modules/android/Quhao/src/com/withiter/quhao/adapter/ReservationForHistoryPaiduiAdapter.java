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
import android.widget.ListView;
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
				holder.merchantAddress = (TextView) convertView.findViewById(R.id.merchantAddress);
				holder.myNumber = (TextView) convertView.findViewById(R.id.myNumber);
				holder.seatNo = (TextView) convertView.findViewById(R.id.seatNo);
				holder.commentBtn = (Button) convertView.findViewById(R.id.btn_comment);
				holder.isComment = (TextView) convertView.findViewById(R.id.is_comment);
				holder.cb = (CheckBox) convertView.findViewById(R.id.item_cb);
			}
			if (holder == null) {
				holder = (ViewHolderHistoryPaidui) convertView.getTag();
			}
			
			if("true".equals(isShowDelete))
			{
				holder.cb.setVisibility(View.VISIBLE);
				holder.cb.setChecked("true".equals(rvo.isChecked));
				
			}
			else
			{
				holder.cb.setVisibility(View.GONE);
			}
			
			final String merchantId = rvo.merchantId;
			// if merchant has no image, set no_logo as default
			if(StringUtils.isNull(rvo.merchantImage)){
				holder.merchantImg.setImageResource(R.drawable.no_logo);
			}
			
			String merchantImg = rvo.merchantImage;
			holder.merchantImg.setImageResource(R.drawable.no_logo);
			AsynImageLoader.getInstance().showImageAsyn(holder.merchantImg,position, merchantImg, R.drawable.no_logo);
			// get image from memory/SDCard/URL stream
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
			
			holder.merchantName.setTag("merchantNamer_" + position);
			holder.merchantName.setText(rvo.merchantName);
			holder.merchantAddress.setTag("merchantAddress_" + position);
			holder.merchantAddress.setText(rvo.merchantAddress);

			holder.myNumber.setTag("myNumber_" + position);
			holder.myNumber.setText(rvo.myNumber);
			holder.seatNo.setTag("seatNo_" + position);
			holder.seatNo.setText(rvo.seatNumber);
			
			if(rvo.isCommented)
			{
				holder.isComment.setVisibility(View.VISIBLE);
				holder.commentBtn.setVisibility(View.GONE);
				holder.isComment.setText("已评价");
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
