package com.withiter.quhao.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.withiter.quhao.R;
import com.withiter.quhao.util.AsyncImageLoader;
import com.withiter.quhao.util.AsyncImageLoader.ImageCallback;
import com.withiter.quhao.util.QuhaoLog;
import com.withiter.quhao.util.StringUtils;
import com.withiter.quhao.util.tool.PhoneTool;
import com.withiter.quhao.vo.TopMerchant;

public class TopMerchantGridAdapter extends BaseAdapter {

	private List<? extends Object> list;
	private AsyncImageLoader asyncImageLoader;
	private GridView grid;
	private Context context;

	private static int getViewTimes = 0;

	private String TAG = TopMerchantGridAdapter.class.getName();

	public TopMerchantGridAdapter(List<? extends Object> list, GridView grid, Context context) {
		this.list = list;
		this.grid = grid;
		this.context = context;
		asyncImageLoader = new AsyncImageLoader();
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
	public View getView(int position, View convertView, ViewGroup parent) {

		QuhaoLog.i(TAG, "getView times : " + (getViewTimes++));
		QuhaoLog.i(TAG, "getView " + position + " " + convertView);

		Drawable cachedImage = null;
		TopMerchant topMerchant = (TopMerchant) this.getItem(position);

		final int defaultHight = PhoneTool.getScreenHeight() / 6;
		final int defaultWidth = PhoneTool.getScreenWidth() / 4;
		
		ViewHolder holder = null;
		if (null == convertView) {
			holder = new ViewHolder();
			LayoutInflater inflator = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflator.inflate(R.layout.topmerchant_item, null);
			holder.img = (ImageView) convertView.findViewById(R.id.img);
			holder.img.setAdjustViewBounds(true);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		// this holder has no top merchant
		if (StringUtils.isNull(topMerchant.id)) {
			cachedImage = context.getResources().getDrawable(R.drawable.no_logo);
			cachedImage.setBounds(0, 0, defaultWidth, defaultHight);
			holder.img.setImageDrawable(cachedImage);
			holder.img.setAdjustViewBounds(true);
			convertView.setTag(holder);
			return convertView;
		}

		String imageUrl = topMerchant.url;
		QuhaoLog.d(TAG, "asyncImageLoader, the imageUrl is : " + imageUrl);
		
		if (StringUtils.isNotNull(imageUrl)) {
			cachedImage = asyncImageLoader.loadDrawable(imageUrl, position, new ImageCallback() {
				@Override
				public void imageLoaded(Drawable imageDrawable, String imageUrl, int position) {
					ImageView imageViewByTag = (ImageView) grid.findViewWithTag(imageUrl);
					if (null != imageViewByTag && null != imageDrawable) {
						imageDrawable.setBounds(0, 0, defaultWidth, defaultHight);
						imageViewByTag.setImageDrawable(imageDrawable);
						imageDrawable.setCallback(null);
						imageDrawable = null;
					}
				}
			});
		}

		holder.img.setTag(imageUrl);
		// 重新设置图片的宽高
		holder.img.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
		holder.img.setAdjustViewBounds(true);
		// 设置图片给imageView 对象
		if (null != cachedImage) {
			cachedImage.setBounds(0, 0, defaultWidth, defaultHight);
			holder.img.setImageDrawable(cachedImage);
//			cachedImage.setCallback(null);
//			cachedImage = null;
		} else {
			cachedImage = context.getResources().getDrawable(R.drawable.no_logo);
			cachedImage.setBounds(0, 0, defaultWidth, defaultHight);
			holder.img.setImageDrawable(cachedImage);
		}

		// set the default height
		QuhaoLog.i(TAG, "the defaultHight is :" + defaultHight);
		if (convertView != null) {
			convertView.setMinimumHeight(defaultHight);
		}

		convertView.setTag(holder);
		return convertView;
		// }

	}

	class ViewHolder {
		ImageView img;
		TextView itemView;
		TextView countView;
	}
}
