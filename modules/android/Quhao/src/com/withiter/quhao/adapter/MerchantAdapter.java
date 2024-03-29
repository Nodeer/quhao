package com.withiter.quhao.adapter;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.withiter.quhao.QHClientApplication;
import com.withiter.quhao.R;
import com.withiter.quhao.activity.GetNumberActivity;
import com.withiter.quhao.activity.LoginActivity;
import com.withiter.quhao.activity.MerchantDetailActivity;
import com.withiter.quhao.activity.MyNumberActivity;
import com.withiter.quhao.data.ReservationData;
import com.withiter.quhao.task.CreateMerchentOpenTask;
import com.withiter.quhao.task.GetReservationsTask;
import com.withiter.quhao.task.JsonPack;
import com.withiter.quhao.util.StringUtils;
import com.withiter.quhao.util.tool.ParseJson;
import com.withiter.quhao.util.tool.QuhaoConstant;
import com.withiter.quhao.util.tool.SharedprefUtil;
import com.withiter.quhao.vo.Merchant;
import com.withiter.quhao.vo.ReservationVO;

public class MerchantAdapter extends BaseAdapter {

	private ListView listView;
	public List<Merchant> merchants;
	private Activity activity;
	
	private DisplayImageOptions options = null;
	
	private ImageLoadingListener animateFirstListener;
	
	private Handler refreshOpenHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Map<String, String> obj2 = (Map<String, String>) msg.obj;
			if (StringUtils.isNotNull(obj2.get("openNum"))) {
				int openNum = Integer.valueOf(obj2.get("openNum"));
				int position = Integer.valueOf(obj2.get("position"));
				updateView(position,openNum);
			}
			else {
				Toast.makeText(activity, "亲，网络有点异常哦。", Toast.LENGTH_SHORT).show();
			}
		}
	};
	
	
	private void updateView(int position, int openNum) {
		
		int visiblePos = listView.getFirstVisiblePosition();
		merchants.get(position).openNum = openNum;
		int offset = position - visiblePos;
		// 只有在可见区域才更新
		if(offset < 0) {
			return;
		}
		
		View view = listView.getChildAt(offset);
		ViewHolder holder = (ViewHolder)view.getTag();
		holder.btnOpen.setText("希望开通(" + openNum + ")");
	}
	
	public MerchantAdapter(Activity activity, ListView listView, List<Merchant> merchants,DisplayImageOptions options,ImageLoadingListener animateFirstListener) {
		super();
		this.listView = listView;
		this.merchants = merchants;
		this.activity = activity;
		this.options = options;
		this.animateFirstListener = animateFirstListener;
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
		Merchant merchant = (Merchant) getItem(position);
		synchronized (merchant) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				LayoutInflater inflator = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflator.inflate(R.layout.merchant_list_item, null);
				holder.img = (ImageView) convertView.findViewById(R.id.img);
				holder.img.setAdjustViewBounds(true);
				holder.content = (TextView) convertView.findViewById(R.id.merchantName);
				holder.distance = (TextView) convertView.findViewById(R.id.distance);
				holder.btnGetNumber = (Button) convertView.findViewById(R.id.get_number);
				holder.btnOpen = (Button) convertView.findViewById(R.id.open);
				holder.btnOffline = (Button) convertView.findViewById(R.id.btn_offline);
//				holder.pinfenImage = (ImageView) convertView.findViewById(R.id.pingfen);
				holder.merchantRenjun = (TextView) convertView.findViewById(R.id.merchantRenjun);
				holder.dianpingLayout = (LinearLayout) convertView.findViewById(R.id.dianping_layout);
				holder.dazhongdianping = (TextView) convertView.findViewById(R.id.dazhongdianping);
				holder.youhuiLayout = (RelativeLayout) convertView.findViewById(R.id.youhui_layout);
			}
			
			if (holder == null) {
				holder = (ViewHolder) convertView.getTag();
			}

			String imageUrl = merchant.merchantImage;
//			holder.img.setImageResource(R.drawable.no_logo);
//			AsynImageLoader.getInstance().showImageAsyn(holder.img, position,imageUrl, R.drawable.no_logo);
			ImageLoader.getInstance().displayImage(imageUrl, holder.img, options, animateFirstListener);
			holder.content.setTag("content_" + position);
			holder.content.setText(merchant.name);
			holder.youhuiLayout.setTag("youhui_layout_" + position);
			if(merchant.youhuiExist) {
				holder.youhuiLayout.setVisibility(View.VISIBLE);
			} else {
				holder.youhuiLayout.setVisibility(View.GONE);
			}
			if(merchant.enable) {
				holder.btnOpen.setVisibility(View.GONE);
				if (merchant.online) {
					holder.btnGetNumber.setVisibility(View.VISIBLE);
					holder.btnOffline.setVisibility(View.GONE);
				}
				else
				{
					holder.btnOffline.setVisibility(View.VISIBLE);
					holder.btnGetNumber.setVisibility(View.GONE);
				}
				
				
			} else {
				holder.btnOpen.setVisibility(View.VISIBLE);
				holder.btnOpen.setText("希望开通(" + merchant.openNum + ")");
				holder.btnGetNumber.setVisibility(View.GONE);
				holder.btnOffline.setVisibility(View.GONE);
			}
			
			final boolean enable = merchant.enable;
			final String merchantId = merchant.id;
			final String mName = merchant.name;
			final boolean online = merchant.online;
			holder.btnGetNumber.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					
					if (QHClientApplication.getInstance().isLogined) {
						if (!enable) {
							Toast.makeText(activity, "亲，商家未开通，暂时无法取号。", Toast.LENGTH_SHORT).show();
							return;
						}
						
						if (!online) {
							Toast.makeText(activity, "商家离线，暂时无法取号。", Toast.LENGTH_SHORT).show();
							return;
						}
						String accountId = SharedprefUtil.get(activity, QuhaoConstant.ACCOUNT_ID, "");
						String url = "getReservations?accountId=" + accountId + "&mid=" + merchantId;
						if (StringUtils.isNotNull(merchantId) && StringUtils.isNotNull(accountId)) {
							final GetReservationsTask task = new GetReservationsTask(R.string.waitting, activity, url);
							task.execute(new Runnable() {
								
								@Override
								public void run() {
									JsonPack jsonPack = task.jsonPack;
									List<ReservationVO> rvos = ParseJson.getReservations(jsonPack.getObj());
									if (null != rvos && !rvos.isEmpty()) {
										
										ReservationVO rvo = rvos.get(0);
										ReservationData data = new ReservationData();
										data.setAccountId(rvo.accountId);
										data.setBeforeYou(rvo.beforeYou);
										data.setCurrentNumber(rvo.currentNumber);
										data.setMerchantAddress(rvo.merchantAddress);
										data.setMerchantId(rvo.merchantId);
										data.setMerchantImage(rvo.merchantImage);
										data.setMerchantName(rvo.merchantName);
										data.setMyNumber(rvo.myNumber);
										data.setrId(rvo.rId);
										data.setSeatNumber(rvo.seatNumber);
										
										Intent intentMyNo = new Intent();
										Bundle bundle = new Bundle();
										bundle.putParcelable("rvo", data);
										intentMyNo.putExtras(bundle);
										
										intentMyNo.setClass(activity, MyNumberActivity.class);
										activity.startActivity(intentMyNo);
									}
									else
									{
										Intent intentGetNumber = new Intent();
										intentGetNumber.putExtra("merchantId", merchantId);
										intentGetNumber.putExtra("merchantName", mName);
										intentGetNumber.setClass(activity, GetNumberActivity.class);
										activity.startActivity(intentGetNumber);
									}
									
								}
							},new Runnable() {
								
								@Override
								public void run() {
									Intent intentGetNumber = new Intent();
									intentGetNumber.putExtra("merchantId", merchantId);
									intentGetNumber.putExtra("merchantName", mName);
									intentGetNumber.setClass(activity, GetNumberActivity.class);
									activity.startActivity(intentGetNumber);
									
								}
							});
						}
			
					} else {
						Intent intentGetNumber = new Intent(activity, LoginActivity.class);
						intentGetNumber.putExtra("activityName", MerchantDetailActivity.class.getName());
						intentGetNumber.putExtra("merchantId", merchantId);
						intentGetNumber.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						activity.startActivity(intentGetNumber);
					}
					
				}
			});
			
			final String positionStr = String.valueOf(position);
			holder.btnOpen.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {

					if (QHClientApplication.getInstance().isLogined) {
						
						String accountId = SharedprefUtil.get(activity, QuhaoConstant.ACCOUNT_ID, "");
						
						String url = "openService?mid=" + merchantId + "&accountId=" + accountId;
						final CreateMerchentOpenTask task = new CreateMerchentOpenTask(R.string.waitting, activity, url);
						task.execute(new Runnable() {
							
							@Override
							public void run() {
								JsonPack jsonPack = task.jsonPack;
								if (jsonPack != null && StringUtils.isNotNull(jsonPack.getObj())) {
									Message msg = refreshOpenHandler.obtainMessage();
									Map<String, String> obj = new HashMap<String, String>();
									obj.put("position", positionStr);
									obj.put("openNum", task.jsonPack.getObj());
									msg.obj = obj;
									msg.sendToTarget();
								}
								else
								{
									Message msg = refreshOpenHandler.obtainMessage();
									Map<String, String> obj = new HashMap<String, String>();
									obj.put("position", positionStr);
									obj.put("openNum", "");
									msg.obj = obj;
									msg.sendToTarget();
								}
								
							}
						}, new Runnable() {
							@Override
							public void run() {
								Message msg = refreshOpenHandler.obtainMessage();
								Map<String, String> obj = new HashMap<String, String>();
								obj.put("position", positionStr);
								obj.put("openNum", "");
								msg.obj = obj;
								msg.sendToTarget();
							}
						});
			
					} else {
						Intent intentGetNumber = new Intent(activity, LoginActivity.class);
						intentGetNumber.putExtra("activityName", MerchantDetailActivity.class.getName());
						intentGetNumber.putExtra("merchantId", merchantId);
						intentGetNumber.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						activity.startActivity(intentGetNumber);
					}
					
				}
			});
			holder.distance.setTag("distance_" + position);
			
			if(merchant.distance > 0)
			{
				if(merchant.distance>1000)
				{
					
					NumberFormat nf = NumberFormat.getNumberInstance();
			        nf.setMaximumFractionDigits(1);
					holder.distance.setText(nf.format(merchant.distance/1000) + "km");
				}
				else
				{
					holder.distance.setText(String.valueOf((int)merchant.distance) + "m");
				}
				
			}
			else
			{
				holder.distance.setText("未定位");
			}
			
			if ("0".equals(merchant.dianpingFen)) {
				holder.dianpingLayout.setVisibility(View.INVISIBLE);
			}
			else
			{
				holder.dianpingLayout.setVisibility(View.VISIBLE);
				holder.dazhongdianping.setText(merchant.dianpingFen);
			}
			
			if (StringUtils.isNull(merchant.averageCost)) {
				holder.merchantRenjun.setText("暂无");
			} else {
				holder.merchantRenjun.setText(merchant.averageCost);
			}
			convertView.setTag(holder);
			return convertView;
		}

	}

	class ViewHolder {
		ImageView img;
		TextView content;
//		ImageView pinfenImage;
		TextView merchantRenjun;
		TextView dazhongdianping;
		TextView distance;
		Button btnGetNumber;
		Button btnOpen;
		Button btnOffline;
		LinearLayout dianpingLayout;
		RelativeLayout youhuiLayout;
	}
}
