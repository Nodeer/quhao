package com.withiter.quhao.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.withiter.quhao.R;

public class MyGridAdapter extends BaseAdapter {
	private String[] files;

	private LayoutInflater mLayoutInflater;

	private DisplayImageOptions options;
	
	private ImageLoadingListener animateFirstListener;
	
	public MyGridAdapter(String[] files, Context context,ImageLoadingListener animateFirstListener) {
		this.files = files;
		mLayoutInflater = LayoutInflater.from(context);
		this.animateFirstListener = animateFirstListener;
		options = new DisplayImageOptions.Builder()
		.showImageOnLoading(R.drawable.no_logo)
		.showImageForEmptyUri(R.drawable.no_logo)
		.showImageOnFail(R.drawable.no_logo)
		.cacheInMemory(true)
		.cacheOnDisk(true)
		.considerExifParams(true)
//		.displayer(new RoundedBitmapDisplayer(20))
		.build();
	}

	@Override
	public int getCount() {
		return files == null ? 0 : files.length;
	}

	@Override
	public String getItem(int position) {
		return files[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		MyGridViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new MyGridViewHolder();
			convertView = mLayoutInflater.inflate(R.layout.gridview_item,
					parent, false);
			viewHolder.imageView = (ImageView) convertView
					.findViewById(R.id.album_image);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (MyGridViewHolder) convertView.getTag();
		}
		String url = getItem(position);

		ImageLoader.getInstance().displayImage(url, viewHolder.imageView,options,animateFirstListener);

		return convertView;
	}

	private static class MyGridViewHolder {
		ImageView imageView;
	}
}
