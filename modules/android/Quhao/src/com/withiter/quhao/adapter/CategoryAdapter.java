package com.withiter.quhao.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.withiter.quhao.R;
import com.withiter.quhao.activity.MainActivity;
import com.withiter.quhao.util.AsyncImageLoader;
import com.withiter.quhao.util.AsyncImageLoader.ImageCallback;
import com.withiter.quhao.vo.Category;

public class CategoryAdapter extends BaseAdapter {

	private ListView listView;
	public List<Category> categorys;
	private MainActivity activity;
	private AsyncImageLoader asyncImageLoader;

	public CategoryAdapter(MainActivity activity, ListView listView,
			List<Category> categorys) {
		super();
		this.activity = activity;
		this.listView = listView;
		this.categorys = categorys;
		asyncImageLoader = new AsyncImageLoader();

	}

	@Override
	public int getCount() {
		return categorys.size();
	}

	@Override
	public Object getItem(int position) {
		return categorys.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Drawable cachedImage = null;
		Category category = (Category) getItem(position);
		synchronized (category) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				LayoutInflater inflator = (LayoutInflater) parent.getContext()
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflator.inflate(R.layout.category_list_item,
						null);
				holder.img = (ImageView) convertView.findViewById(R.id.img);
				holder.content = (TextView) convertView
						.findViewById(R.id.txt_content);
				holder.btnEnter = (TextView) convertView
						.findViewById(R.id.btn_enter);

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
			holder.img.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {

				}
			});

			holder.btnEnter.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {

				}
			});

			String imageUrl = category.url;

			holder.img.setTag(imageUrl);
			if (null != imageUrl && !"".equals(imageUrl)) {
				cachedImage = asyncImageLoader.loadDrawable(imageUrl,
						new ImageCallback() {
							@Override
							public void imageLoaded(Drawable imageDrawable,
									String imageUrl) {
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
				holder.img.setImageResource(R.drawable.title_img);
			}

			holder.content.setTag("content_" + position);
			holder.btnEnter.setTag("btnEnter_" + position);
			holder.content.setText(category.categoryType);
			holder.btnEnter.setText(String.valueOf(category.count));
			convertView.setTag(holder);
			return convertView;
		}

	}

	class ViewHolder {
		ImageView img;
		TextView content;
		TextView btnEnter;
	}
}
