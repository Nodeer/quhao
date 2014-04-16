package com.withiter.quhao.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.withiter.quhao.R;
import com.withiter.quhao.util.QuhaoLog;
import com.withiter.quhao.util.StringUtils;
import com.withiter.quhao.util.tool.AsynImageLoader;
import com.withiter.quhao.util.tool.PhoneTool;
import com.withiter.quhao.vo.TopMerchant;

public class TopMerchantGridAdapter extends BaseAdapter {

	private List<? extends Object> list;
	private Context context;
	Drawable cachedImage = null;

	private static int getViewTimes = 0;

	private String TAG = TopMerchantGridAdapter.class.getName();

	public TopMerchantGridAdapter(List<? extends Object> list, Context context) {
		this.list = list;
		this.context = context;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		QuhaoLog.i(TAG, "getView times : " + (getViewTimes++));
		QuhaoLog.i(TAG, "getView " + position + " " + convertView);

		TopMerchant topMerchant = (TopMerchant) this.getItem(position);

		final int defaultWidth = PhoneTool.getScreenWidth() / 3;
		final int defaultHight = PhoneTool.getScreenHeight() / 7;

		synchronized(topMerchant)
		{
			ViewHolder holder = null;
			if(convertView == null)
			{
				holder = new ViewHolder();
				LayoutInflater inflator = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflator.inflate(R.layout.topmerchant_item, null);
				holder.img = (ImageView) convertView.findViewById(R.id.img);
			}
			
			if (holder == null) {
				holder = (ViewHolder) convertView.getTag();
			}
			
//			LayoutParams lp = holder.img.getLayoutParams();
//			lp.height = defaultHight;
//			lp.width = defaultWidth;
//			holder.img.setLayoutParams(lp);
//			holder.img.setPadding(8, 8, 8, 8);
			
			QuhaoLog.i(TAG, "top merchant adapter's imageUrl : " + topMerchant.merchantImage);
			
			if (StringUtils.isNull(topMerchant.merchantImage)) {
				holder.img.setTag(topMerchant.merchantImage + "Image" + position);
				holder.img.setImageResource(R.drawable.no_logo);
			}else
			{
				AsynImageLoader.getInstance().showImageAsyn(holder.img, position,topMerchant.merchantImage, R.drawable.no_logo);
			}
			holder.img.setScaleType(ImageView.ScaleType.FIT_XY);
			convertView.setTag(holder);
	        return convertView;
		}
	}
	
	class ViewHolder {
		ImageView img;
	}
}
