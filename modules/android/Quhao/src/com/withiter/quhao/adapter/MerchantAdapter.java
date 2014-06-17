package com.withiter.quhao.adapter;

import java.text.NumberFormat;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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

import com.withiter.quhao.QHClientApplication;
import com.withiter.quhao.R;
import com.withiter.quhao.activity.GetNumber2Activity;
import com.withiter.quhao.activity.LoginActivity;
import com.withiter.quhao.activity.MerchantDetailActivity;
import com.withiter.quhao.util.QuhaoLog;
import com.withiter.quhao.util.StringUtils;
import com.withiter.quhao.util.tool.AsynImageLoader;
import com.withiter.quhao.vo.Merchant;

public class MerchantAdapter extends BaseAdapter {

	private ListView listView;
	public List<Merchant> merchants;
	private static String TAG = MerchantAdapter.class.getName();
	private Activity activity;

	public MerchantAdapter(Activity activity, ListView listView, List<Merchant> merchants) {
		super();
		this.listView = listView;
		this.merchants = merchants;
		this.activity = activity;
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

			QuhaoLog.i(TAG, "merchant adapter's imageUrl : " + imageUrl);

//			holder.img.setTag(imageUrl + position);
			holder.img.setImageResource(R.drawable.no_logo);
			AsynImageLoader.getInstance().showImageAsyn(holder.img, position,imageUrl, R.drawable.no_logo);
			/*
			if (null != imageUrl && !"".equals(imageUrl)) {
				cachedImage = asyncImageLoader.loadDrawable(imageUrl, position, new ImageCallback() {

					@Override
					public void imageLoaded(Drawable imageDrawable, String imageUrl, int position) {
						ImageView imageViewByTag = (ImageView) listView.findViewWithTag(imageUrl + position);
						if (null != imageViewByTag && null != imageDrawable) {
							imageViewByTag.setImageDrawable(imageDrawable);
							imageViewByTag.invalidate();
							imageDrawable.setCallback(null);
							imageDrawable = null;
						}

					}
				});

			}
			// // 设置图片给imageView 对象
			if (null != cachedImage) {
				holder.img.setImageDrawable(cachedImage);
				holder.img.invalidate();
				cachedImage.setCallback(null);
				cachedImage = null;
			} else {
				holder.img.setImageResource(R.drawable.no_logo);
			}
			*/
			holder.content.setTag("content_" + position);
			holder.content.setText(merchant.name);
			holder.youhuiLayout.setTag("youhui_layout_" + position);
			if(merchant.youhuiExist)
			{
				holder.youhuiLayout.setVisibility(View.VISIBLE);
			}
			else
			{
				holder.youhuiLayout.setVisibility(View.GONE);
			}
			
			
			if(merchant.enable)
			{
				holder.btnGetNumber.setEnabled(true);
			}
			else
			{
				holder.btnGetNumber.setEnabled(false);
			}
			
			final boolean enable = merchant.enable;
			final String merchantId = merchant.id;
			final String mName = merchant.name;
			holder.btnGetNumber.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					
					if (QHClientApplication.getInstance().isLogined) {
						if (!enable) {
							Toast.makeText(activity, "商家原因，暂时无法取号。", Toast.LENGTH_SHORT).show();
							return;
						}
						Intent intentGetNumber = new Intent();
						intentGetNumber.putExtra("merchantId", merchantId);
						intentGetNumber.putExtra("merchantName", mName);
						intentGetNumber.setClass(activity, GetNumber2Activity.class);
						activity.startActivity(intentGetNumber);
			
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
		LinearLayout dianpingLayout;
		RelativeLayout youhuiLayout;
	}
}
