package com.withiter.quhao.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.withiter.quhao.R;
import com.withiter.quhao.util.AsyncImageLoader;
import com.withiter.quhao.util.AsyncImageLoader.ImageCallback;
import com.withiter.quhao.vo.Merchant;

public class SearchAdapter extends BaseAdapter {
	private ListView listView;
	public List<Merchant> merchants;
	private AsyncImageLoader asyncImageLoader;

	public SearchAdapter(ListView listView,
			List<Merchant> merchants) {
		super();
		this.listView = listView;
		this.merchants = merchants;
		asyncImageLoader = new AsyncImageLoader();

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
		Drawable cachedImage = null;
		Merchant merchant = (Merchant) getItem(position);
		synchronized (merchant) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				LayoutInflater inflator = (LayoutInflater) parent.getContext()
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflator.inflate(R.layout.merchant_list_item,
						null);
				holder.img = (ImageView) convertView.findViewById(R.id.img);
				holder.btn = (Button) convertView
						.findViewById(R.id.btnMerchantDetail);
				holder.content = (TextView) convertView
						.findViewById(R.id.merchantName);
				holder.btnEnter = (TextView) convertView
						.findViewById(R.id.merchantAddress);

				/*
				 * 重新设置图片的宽高
				 * holder.img.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
				 */
				/*
				 * 重新设置Layout 的宽高 holder.img.setLayoutParams(new
				 * LinearLayout.LayoutParams( LayoutParams.WRAP_CONTENT,
				 * LayoutParams.WRAP_CONTENT));
				 */

			}
			if (holder == null) {
				holder = (ViewHolder) convertView.getTag();
			}

			String imageUrl = merchant.merchantImage;

			holder.img.setTag(imageUrl);
			if (null != imageUrl && !"".equals(imageUrl)) {
				cachedImage = asyncImageLoader.loadDrawable(imageUrl,position,
						new ImageCallback() {

							@Override
							public void imageLoaded(Drawable imageDrawable,
									String imageUrl,int position) {
								ImageView imageViewByTag = (ImageView) listView
										.findViewWithTag(imageUrl);
								if (null != imageViewByTag
										&& null != imageDrawable) {
									imageViewByTag
											.setImageDrawable(imageDrawable);
									imageViewByTag.invalidate();
									imageDrawable.setCallback(null);
									imageDrawable = null;
								}

							}
						});

			}
			// 设置图片给imageView 对象
			if (null != cachedImage) {
				holder.img.setImageDrawable(cachedImage);
				holder.img.invalidate();
				cachedImage.setCallback(null);
				cachedImage = null;
			} else {
				holder.img.setImageResource(R.drawable.no_logo);
			}

			holder.content.setTag("content_" + position);

			holder.btnEnter.setTag("btnEnter_" + position);
			holder.content.setText(merchant.name);
			holder.btnEnter.setText(merchant.address);
			convertView.setTag(holder);
			return convertView;
		}

	}

	class ViewHolder {
		ImageView img;
		TextView content;
		TextView btnEnter;
		Button btn;
	}
}
