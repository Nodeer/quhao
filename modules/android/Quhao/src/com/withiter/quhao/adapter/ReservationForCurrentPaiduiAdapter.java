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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.withiter.quhao.R;
import com.withiter.quhao.activity.MerchantDetailActivity;
import com.withiter.quhao.activity.QuhaoCurrentStatesActivity;
import com.withiter.quhao.exception.NoResultFromHTTPRequestException;
import com.withiter.quhao.util.ActivityUtil;
import com.withiter.quhao.util.StringUtils;
import com.withiter.quhao.util.http.CommonHTTPRequest;
import com.withiter.quhao.util.tool.AsynImageLoader;
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
				holder.merchantImg = (ImageView) convertView.findViewById(R.id.merchantImg);
				holder.merchantName = (TextView) convertView.findViewById(R.id.merchantName);
				holder.myNumber = (TextView) convertView.findViewById(R.id.myNumber);
				holder.beforeYou = (TextView) convertView.findViewById(R.id.beforeYou);
				holder.cancelBtn = (Button) convertView.findViewById(R.id.btn_cancel);
				holder.youhuiLayout = (RelativeLayout) convertView.findViewById(R.id.layout_top_youhui);
			}
			if (holder == null) {
				holder = (ViewHolderCurrentPaidui) convertView.getTag();
			}
			
			final String merchantId = rvo.merchantId;
			
			// if merchant has no image, set no_logo as default
			if(StringUtils.isNull(rvo.merchantImage)){
				holder.merchantImg.setImageResource(R.drawable.no_logo);
			}
			
			String merchantImg = rvo.merchantImageBig;
			holder.merchantImg.setImageResource(R.drawable.no_logo);
			// get image from memory/SDCard/URL stream
			AsynImageLoader.getInstance().showImageAsyn(holder.merchantImg,position, merchantImg, R.drawable.no_logo);
			holder.merchantImg.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					progress = new ProgressDialogUtil(activity, R.string.empty, R.string.waitting, false);
					progress.showProgress();
					Intent intent = new Intent(activity, MerchantDetailActivity.class);
					intent.putExtra("merchantId", merchantId);
					intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
					activity.startActivity(intent);
					progress.closeProgress();
				}
			});
			holder.merchantName.setTag("merchantNamer_" + position);
			holder.merchantName.setText(rvo.merchantName);

			holder.myNumber.setTag("myNumber_" + position);
			holder.myNumber.setText(rvo.myNumber);
			holder.beforeYou.setTag("beforeYou_" + position);
			holder.beforeYou.setText(rvo.beforeYou);
			
			final String reservationId = rvo.rId;
			holder.cancelBtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					if (!ActivityUtil.isNetWorkAvailable(activity)) {
						Toast.makeText(activity, R.string.network_error_info, Toast.LENGTH_SHORT).show();
						return;
					}
					
					cancelListener(reservationId);

				}
					
			});
			
			if (rvo.youhui) 
			{
				holder.youhuiLayout.setVisibility(View.VISIBLE);
			}
			else
			{
				holder.youhuiLayout.setVisibility(View.GONE);
			}
			convertView.setTag(holder);
			return convertView;
		}

	}
	
	private void cancelListener(final String reservationId) {
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
							if (!ActivityUtil.isNetWorkAvailable(activity)) {
								Toast.makeText(activity, R.string.network_error_info, Toast.LENGTH_SHORT).show();
								progress.closeProgress();
								return;
							}
							String url = "";
							url = "cancel?reservationId=" + reservationId;
							
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

}
