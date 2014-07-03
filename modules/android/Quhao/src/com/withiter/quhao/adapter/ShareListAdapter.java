package com.withiter.quhao.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.withiter.quhao.R;
import com.withiter.quhao.activity.ImagePagerActivity;
import com.withiter.quhao.util.StringUtils;
import com.withiter.quhao.view.gridview.NoScrollGridView;
import com.withiter.quhao.vo.ShareVO;

public class ShareListAdapter extends BaseAdapter {

	private ListView listView;
	public List<ShareVO> shares;
	private Activity activity;
	private DisplayImageOptions options = null;
	
	private ImageLoadingListener animateFirstListener;
	
	public ShareListAdapter(Activity activity, ListView listView, List<ShareVO> shares,DisplayImageOptions options,ImageLoadingListener animateFirstListener) {
		super();
		this.listView = listView;
		this.shares = shares;
		this.activity = activity;
		this.options = options;
		this.animateFirstListener = animateFirstListener;
	}

	@Override
	public int getCount() {
		return shares.size();
	}

	@Override
	public Object getItem(int position) {
		return shares.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ShareVO shareVO = (ShareVO) getItem(position);
		synchronized (shareVO) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				LayoutInflater inflator = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflator.inflate(R.layout.share_list_item, null);
				holder.userImg = (ImageView) convertView.findViewById(R.id.user_img);
				holder.nickName = (TextView) convertView.findViewById(R.id.nickName);
				holder.shareImg = (NoScrollGridView) convertView.findViewById(R.id.share_img);
				holder.content = (TextView) convertView.findViewById(R.id.share_content);
				holder.location = (TextView) convertView.findViewById(R.id.location);
				holder.distance = (TextView) convertView.findViewById(R.id.distance);
				holder.date = (TextView) convertView.findViewById(R.id.date);
				holder.niceCount = (TextView) convertView.findViewById(R.id.nice_count);
			}
			
			if (holder == null) {
				holder = (ViewHolder) convertView.getTag();
			}

//			AsynImageLoader.getInstance().showImageAsyn(holder.userImg, position, shareVO.image, R.drawable.person_avatar);
			ImageLoader.getInstance().displayImage(shareVO.image, holder.userImg, options, animateFirstListener);
			holder.nickName.setText(shareVO.address);
			
			holder.content.setText(shareVO.content);
			
			holder.location.setText(shareVO.address);
			
			holder.distance.setText(shareVO.dis);
			
			holder.date.setText(shareVO.date);
			
			holder.niceCount.setText(shareVO.address);
			
//			AsynImageLoader.getInstance().showImageAsyn(holder.shareImg, position, shareVO.image, R.drawable.person_avatar);
//			ImageLoader.getInstance().displayImage(shareVO.image, holder.shareImg, options, animateFirstListener);
			
			
			if (StringUtils.isNotNull(shareVO.image)) 
			{
				final String[] imgs = new String[]{shareVO.image};
				holder.shareImg.setVisibility(View.VISIBLE);
				holder.shareImg.setAdapter(new MyGridAdapter(imgs, activity));
				holder.shareImg.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						imageBrower(position,imgs);
					}
				});
			}
			else
			{
				holder.shareImg.setVisibility(View.GONE);
			}
			
			convertView.setTag(holder);
			return convertView;
		}

	}
	
	private void imageBrower(int position, String[] urls) {
		Intent intent = new Intent(activity, ImagePagerActivity.class);
		// 图片url,为了演示这里使用常量，一般从数据库中或网络中获取
		intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, urls);
		intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, position);
		activity.startActivity(intent);
	}

	class ViewHolder {
		ImageView userImg;
		TextView nickName;
		NoScrollGridView shareImg;
		TextView content;
		TextView location;
//		ImageView pinfenImage;
		TextView distance;
		TextView date;
		TextView niceCount;
	}
}
