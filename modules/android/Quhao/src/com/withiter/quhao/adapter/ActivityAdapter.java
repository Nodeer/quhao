package com.withiter.quhao.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.withiter.quhao.R;
import com.withiter.quhao.activity.MerchantDetailActivity;
import com.withiter.quhao.util.QuhaoLog;
import com.withiter.quhao.vo.ActivityVO;

public class ActivityAdapter extends BaseAdapter {

	private ListView listView;
	public List<ActivityVO> Activities;
	private static String TAG = ActivityAdapter.class.getName();
	private Activity activity;
	private DisplayImageOptions options;
	private ImageLoadingListener animateFirstListener;

	public ActivityAdapter(Activity activity, ListView listView, List<ActivityVO> Activities,DisplayImageOptions options,ImageLoadingListener animateFirstListener) {
		super();
		this.listView = listView;
		this.Activities = Activities;
		this.activity = activity;
		this.options = options;
		this.animateFirstListener = animateFirstListener;
	}

	@Override
	public int getCount() {
		return Activities.size();
	}

	@Override
	public Object getItem(int position) {
		return Activities.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ActivityVO activityVO = (ActivityVO) getItem(position);
		synchronized (activityVO) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				LayoutInflater inflator = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflator.inflate(R.layout.home_activity_list_item, null);
				holder.img = (ImageView) convertView.findViewById(R.id.img);
			}
			if (holder == null) {
				holder = (ViewHolder) convertView.getTag();
			}

			String imageUrl = activityVO.image;

			QuhaoLog.i(TAG, "merchant adapter's imageUrl : " + imageUrl);

			ImageLoader.getInstance().displayImage(imageUrl, holder.img, options, animateFirstListener);
			
//			holder.img.setTag(imageUrl + position);
//			holder.img.setImageResource(R.drawable.no_logo);
//			AsynImageLoader.getInstance().showImageAsyn(holder.img, position,imageUrl, R.drawable.no_logo);
			final String mid = activityVO.mid;
			holder.img.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					
					Intent intent = new Intent();
					intent.setClass(activity, MerchantDetailActivity.class);
					intent.putExtra("merchantId", mid);
					activity.startActivity(intent);
				}
			});
			convertView.setTag(holder);
			return convertView;
		}

	}

	class ViewHolder {
		ImageView img;
	}
}
