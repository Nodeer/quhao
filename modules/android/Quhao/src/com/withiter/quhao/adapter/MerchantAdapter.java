package com.withiter.quhao.adapter;

import java.text.NumberFormat;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.withiter.quhao.QHClientApplication;
import com.withiter.quhao.R;
import com.withiter.quhao.util.QuhaoLog;
import com.withiter.quhao.util.StringUtils;
import com.withiter.quhao.util.tool.AsynImageLoader;
import com.withiter.quhao.util.tool.DistanceUtil;
import com.withiter.quhao.vo.Merchant;

public class MerchantAdapter extends BaseAdapter {

	private ListView listView;
	public List<Merchant> merchants;
	private static String TAG = MerchantAdapter.class.getName();

	public MerchantAdapter(Activity activity, ListView listView, List<Merchant> merchants) {
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
				holder.merchantEnable = (TextView) convertView.findViewById(R.id.merchant_enable);
//				holder.pinfenImage = (ImageView) convertView.findViewById(R.id.pingfen);
				holder.merchantRenjun = (TextView) convertView.findViewById(R.id.merchantRenjun);
			}
			if (holder == null) {
				holder = (ViewHolder) convertView.getTag();
			}

			String imageUrl = merchant.merchantImage;

			QuhaoLog.i(TAG, "merchant adapter's imageUrl : " + imageUrl);

//			holder.img.setTag(imageUrl + position);
			
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
			holder.merchantEnable.setTag("merchantEnable_" + position);
			
			if(merchant.enable)
			{
				holder.merchantEnable.setText("可以取号");
			}
			else
			{
				holder.merchantEnable.setText("暂不支持取号");
			}
			
			holder.distance.setTag("distance_" + position);
			AMapLocation location = QHClientApplication.getInstance().location;
			if(null != location && merchant.lat != 0 && merchant.lng != 0)
			{
				double distance = DistanceUtil.computeDistance(location.getLatitude(), location.getLongitude(), merchant.lat, merchant.lng);
				if(distance>1000)
				{
					
					NumberFormat nf = NumberFormat.getNumberInstance();
			        nf.setMaximumFractionDigits(2);
					holder.distance.setText(nf.format(distance/1000) + "千米");
				}
				else
				{
					holder.distance.setText(String.valueOf((int)distance) + "米");
				}
				
//				holder.distance.setText(String.valueOf(DistanceUtil.computeDistance(lp.getLatitude(), lp.getLongitude(), merchant.lat, merchant.lng)));
//				Log.e("wjzwjz distance : ", String.valueOf(DistanceUtil.computeDistance(lp.getLatitude(), lp.getLongitude(), merchant.lat, merchant.lng)));
			}
			else
			{
				holder.distance.setText("没有定位信息，暂时无法显示距离");
			}
			
			/*
			if (StringUtils.isNull(merchant.grade)) {
				merchant.grade = "0.0";
			} else {
				merchant.grade = merchant.grade.replace("%", "");
			}
			QuhaoLog.i(TAG, merchant.grade);
			float score = Float.parseFloat(merchant.grade) / 100;
			if (score == 0.0f) {
				holder.pinfenImage.setImageResource(R.drawable.star00);
			}
			if (score > 0.0f && score < 1.0f) {
				holder.pinfenImage.setImageResource(R.drawable.star05);
			}
			if (score == 1.0f) {
				holder.pinfenImage.setImageResource(R.drawable.star10);
			}
			if (score > 1.0f && score < 2.0f) {
				holder.pinfenImage.setImageResource(R.drawable.star15);
			}
			if (score == 2.0f) {
				holder.pinfenImage.setImageResource(R.drawable.star20);
			}
			if (score > 2.0f && score < 3.0f) {
				holder.pinfenImage.setImageResource(R.drawable.star25);
			}
			if (score == 3.0f) {
				holder.pinfenImage.setImageResource(R.drawable.star30);
			}
			if (score > 3.0f && score < 4.0f) {
				holder.pinfenImage.setImageResource(R.drawable.star35);
			}
			if (score == 4.0f) {
				holder.pinfenImage.setImageResource(R.drawable.star40);
			}
			if (score > 4.0f && score < 5.0f) {
				holder.pinfenImage.setImageResource(R.drawable.star45);
			}
			if (score == 5.0f) {
				holder.pinfenImage.setImageResource(R.drawable.star50);
			}
			*/
			if (StringUtils.isNull(merchant.averageCost)) {
				holder.merchantRenjun.setText("人均消费：暂无");
			} else {
				holder.merchantRenjun.setText("人均消费：￥" + merchant.averageCost);
			}
			convertView.setTag(holder);
			return convertView;
		}

	}

	class ViewHolder {
		ImageView img;
		TextView content;
		TextView merchantEnable;
//		ImageView pinfenImage;
		TextView merchantRenjun;
		TextView distance;
	}
}
