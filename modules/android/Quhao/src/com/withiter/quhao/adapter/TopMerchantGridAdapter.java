package com.withiter.quhao.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.withiter.quhao.R;
import com.withiter.quhao.task.ImageTask;
import com.withiter.quhao.util.AsyncImageLoader;
import com.withiter.quhao.util.QuhaoLog;
import com.withiter.quhao.util.StringUtils;
import com.withiter.quhao.util.tool.PhoneTool;
import com.withiter.quhao.vo.TopMerchant;

public class TopMerchantGridAdapter extends BaseAdapter {

	private List<? extends Object> list;
	private AsyncImageLoader asyncImageLoader;
	private Context context;
	Drawable cachedImage = null;

	private static int getViewTimes = 0;

	private String TAG = TopMerchantGridAdapter.class.getName();

	public TopMerchantGridAdapter(List<? extends Object> list, Context context) {
		this.list = list;
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ImageView imageView;
		QuhaoLog.i(TAG, "getView times : " + (getViewTimes++));
		QuhaoLog.i(TAG, "getView " + position + " " + convertView);

		TopMerchant topMerchant = (TopMerchant) this.getItem(position);

		final int defaultWidth = PhoneTool.getScreenWidth() / 3;
		final int defaultHight = PhoneTool.getScreenHeight() / 7;

		if (null == convertView) {
			imageView = new ImageView(context);
			imageView.setLayoutParams(new GridView.LayoutParams(defaultWidth, defaultHight));
			imageView.setPadding(8, 8, 8, 8);
		} else {
			imageView = (ImageView) convertView;
		}

		imageView.setScaleType(ImageView.ScaleType.FIT_XY);
		
        // no content on top merchant grid
		if (StringUtils.isNull(topMerchant.merchantImage)) {
			imageView.setImageResource(R.drawable.no_logo);
			return imageView;
		}
		
		final String imageUrl = topMerchant.merchantImage;
		QuhaoLog.d(TAG, "asyncImageLoader, the imageUrl is : " + imageUrl);
		if (StringUtils.isNotNull(imageUrl)) {
			
			final ImageTask task = new ImageTask(imageView, imageUrl, true, context);
			task.execute();
//			// Android 4.0 之后不能在主线程中请求HTTP请求
//			new Thread(new Runnable(){
//			    @Override
//			    public void run() {
//			    	cachedImage = asyncImageLoader.loadDrawable(imageUrl, position);
//					imageView.setImageDrawable(cachedImage);
//			    }
//			}).start();
			
		}
		
        return imageView;
	}
	
	class ViewHolder {
		ImageView img;
		TextView itemView;
		TextView countView;
	}
}
