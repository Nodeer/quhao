package com.withiter.quhao.adapter;

import java.util.List;

import android.app.Activity;
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
import com.withiter.quhao.util.QuhaoLog;
import com.withiter.quhao.util.AsyncImageLoader.ImageCallback;
import com.withiter.quhao.vo.Merchant;

public class MerchantAdapter extends BaseAdapter {

	private ListView listView;
	public List<Merchant> merchants;
	// private Activity activity;
	private AsyncImageLoader asyncImageLoader;
	private static String TAG = MerchantAdapter.class.getName();

	public MerchantAdapter(Activity activity, ListView listView,
			List<Merchant> merchants) {
		super();
		// this.activity = activity;
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
				holder.merchantAddress = (TextView) convertView
						.findViewById(R.id.merchantAddress);
			}
			if (holder == null) {
				holder = (ViewHolder) convertView.getTag();
			}

			String imageUrl = merchant.merchantImage;

			QuhaoLog.i(TAG, "merchant adapter's imageUrl : " + imageUrl);
			
			
			holder.img.setTag(imageUrl + position);
			if (null != imageUrl && !"".equals(imageUrl)) {
				cachedImage = asyncImageLoader.loadDrawable(imageUrl, position,
						new ImageCallback() {

							@Override
							public void imageLoaded(Drawable imageDrawable,
									String imageUrl, int position) {
								ImageView imageViewByTag = (ImageView) listView
										.findViewWithTag(imageUrl + position);
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
			// // 设置图片给imageView 对象
			if (null != cachedImage) {
				holder.img.setImageDrawable(cachedImage);
				holder.img.invalidate();
				cachedImage.setCallback(null);
				cachedImage = null;
			} else {
				holder.img.setImageResource(R.drawable.no_logo);
			}

			holder.content.setTag("content_" + position);

			holder.merchantAddress.setTag("merchantAddress_" + position);
			holder.content.setText(merchant.name);
			holder.merchantAddress.setText(merchant.address);
			convertView.setTag(holder);
			return convertView;
		}

	}

	class ViewHolder {
		ImageView img;
		TextView content;
		TextView merchantAddress;
		Button btn;
	}
}
