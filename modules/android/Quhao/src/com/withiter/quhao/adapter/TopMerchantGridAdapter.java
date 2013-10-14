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
import com.withiter.quhao.vo.TopMerchant;

public class TopMerchantGridAdapter extends BaseAdapter {

	private List<? extends Object> list;
	private AsyncImageLoader asyncImageLoader;
	private GridView grid;
	private Context context;

	private String TAG = TopMerchantGridAdapter.class.getName();
	
	public TopMerchantGridAdapter(List<? extends Object> list, GridView grid,
			Context context) {
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
		
		QuhaoLog.i(TAG,"getView " + position + " " + convertView);
		
		Drawable cachedImage = null;
		TopMerchant topMerchant = null;
		Object item = getItem(position);
		topMerchant = (TopMerchant) item;

		synchronized (item) {
			ViewHolder holder = null;
			if (null == convertView) {
				holder = new ViewHolder();
				LayoutInflater inflator = (LayoutInflater) parent.getContext()
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflator.inflate(R.layout.topmerchant_item, null);
				holder.img = (ImageView) convertView.findViewById(R.id.img);
				// holder.itemView = (TextView)
				// convertView.findViewById(R.id.text);
			}

			if (holder == null) {
				holder = (ViewHolder) convertView.getTag();
			}

			String imageUrl = "";
			imageUrl = topMerchant.url;
			holder.img.setTag(imageUrl);
			QuhaoLog.i(TAG, "the imageUrl is : " + imageUrl);
			if (null != imageUrl && !"".equals(imageUrl)) {
				cachedImage = asyncImageLoader.loadDrawable(imageUrl,position,
						new ImageCallback() {
							@Override
							public void imageLoaded(Drawable imageDrawable,
									String imageUrl,int position) {
								ImageView imageViewByTag = (ImageView) grid
										.findViewWithTag(imageUrl);
								if (null != imageViewByTag
										&& null != imageDrawable) {
									imageDrawable.setBounds(0, 0,
											imageDrawable.getIntrinsicWidth(),
											imageDrawable.getIntrinsicHeight());
//									imageViewByTag.setCompoundDrawables(null,
//											imageDrawable, null, null);
									imageDrawable.setCallback(null);
									imageDrawable = null;
								}

							}
						});
			}

			/* 重新设置图片的宽高 */
			holder.img.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
			/*
			 * 重新设置Layout 的宽高 holder.img.setLayoutParams(new
			 * LinearLayout.LayoutParams( LayoutParams.WRAP_CONTENT,
			 * LayoutParams.WRAP_CONTENT));
			 */
			// 设置图片给imageView 对象
			if (null != cachedImage) {
				cachedImage.setBounds(0, 0, cachedImage.getIntrinsicWidth(),
						cachedImage.getIntrinsicHeight());
				// holder.img.setCompoundDrawables(null, cachedImage, null,
				// null);
				holder.img.setImageDrawable(cachedImage);
				cachedImage.setCallback(null);
				cachedImage = null;
			} else {
				cachedImage = context.getResources().getDrawable(
						R.drawable.default_icon1);
				cachedImage.setBounds(0, 0, cachedImage.getIntrinsicWidth(),
						cachedImage.getIntrinsicHeight());
				// holder.img.setCompoundDrawables(null, cachedImage, null,
				// null);
				holder.img.setImageDrawable(cachedImage);
			}
			// holder.itemView.setText(topMerchant.name);

			convertView.setTag(holder);
			return convertView;
		}

	}

	class ViewHolder {
		ImageView img;
		TextView itemView;
		TextView countView;
	}
}
