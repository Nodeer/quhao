package com.withiter.quhao.adapter;

import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Looper;
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
import com.withiter.quhao.activity.MerchantDetailActivity;
import com.withiter.quhao.activity.QuhaoCurrentStatesActivity;
import com.withiter.quhao.exception.NoResultFromHTTPRequestException;
import com.withiter.quhao.util.StringUtils;
import com.withiter.quhao.util.http.CommonHTTPRequest;
import com.withiter.quhao.util.tool.ProgressDialogUtil;
import com.withiter.quhao.vo.ReservationVO;

public class ReservationForCurrentPaiduiAdapter extends BaseAdapter {

	private ListView listView;
	public List<ReservationVO> rvos;
	private QuhaoCurrentStatesActivity activity;
	private ProgressDialogUtil progress;

	public ReservationForCurrentPaiduiAdapter(QuhaoCurrentStatesActivity activity, ListView listView, List<ReservationVO> rvos) {
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
			ViewHolderCurrentPaidui holder = null;
			if (convertView == null) {
				holder = new ViewHolderCurrentPaidui();
				LayoutInflater inflator = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflator.inflate(R.layout.paidui_current_list_item, null);
				holder.merchantLayout = (LinearLayout) convertView.findViewById(R.id.merchantLayout);
				holder.merchantName = (TextView) convertView.findViewById(R.id.merchantName);
				holder.merchantAddress = (TextView) convertView.findViewById(R.id.merchantAddress);
				holder.myNumber = (TextView) convertView.findViewById(R.id.myNumber);
				holder.seatNo = (TextView) convertView.findViewById(R.id.seatNo);
				holder.beforeYou = (TextView) convertView.findViewById(R.id.beforeYou);
				holder.currentNumber = (TextView) convertView.findViewById(R.id.currentNumber);
				holder.cancelBtn = (Button) convertView.findViewById(R.id.btn_cancel);
			}
			if (holder == null) {
				holder = (ViewHolderCurrentPaidui) convertView.getTag();
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
			
			final String reservationId = rvo.rId;
			holder.cancelBtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					AlertDialog.Builder builder = new Builder(activity);
					builder.setTitle("温馨提示");
					builder.setMessage("您确定要取消该号码吗？");
					builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							Thread thread = new Thread(new Runnable()
							{

								@Override
								public void run() {
									Looper.prepare();
									progress = new ProgressDialogUtil(activity, R.string.empty, R.string.waitting, false);
									progress.showProgress();
									try {
										String url = "";
										url = "MerchantController/cancel?reservationId=" + reservationId;
										
										String buf = CommonHTTPRequest.get(url);
										if (StringUtils.isNull(buf) || "[]".equals(buf)) {
											throw new NoResultFromHTTPRequestException();
									 	} else {
									 		if("true".equals(buf.trim()))
									 		{
												activity.initData();
									 		}
									 		else
									 		{
												activity.initData();
									 		}
										}

									} catch (Exception e) {
										e.printStackTrace();
									} finally {
										progress.closeProgress();
										Looper.loop();
									}
								
							}});
							thread.start();
					}});
					builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
					builder.create().show();
					
					
				}
			});
			
			convertView.setTag(holder);
			return convertView;
		}

	}

}
