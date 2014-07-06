package com.withiter.quhao.adapter;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.withiter.quhao.QHClientApplication;
import com.withiter.quhao.R;
import com.withiter.quhao.activity.ImagePagerActivity;
import com.withiter.quhao.activity.LoginActivity;
import com.withiter.quhao.task.CreateShareNiceTask;
import com.withiter.quhao.task.JsonPack;
import com.withiter.quhao.util.DateUtils;
import com.withiter.quhao.util.StringUtils;
import com.withiter.quhao.util.tool.QuhaoConstant;
import com.withiter.quhao.util.tool.SharedprefUtil;
import com.withiter.quhao.view.gridview.NoScrollGridView;
import com.withiter.quhao.vo.ShareVO;

public class ShareListAdapter extends BaseAdapter {

	private ListView listView;
	public List<ShareVO> shares;
	private Activity activity;
	private DisplayImageOptions options = null;
	
	private ImageLoadingListener animateFirstListener;
	
	public ShareListAdapter(Activity activity, ListView listView, List<ShareVO> shares,ImageLoadingListener animateFirstListener) {
		super();
		this.listView = listView;
		this.shares = shares;
		this.activity = activity;
		options = new DisplayImageOptions.Builder()
		.showImageOnLoading(R.drawable.share_list_default_img)
		.showImageForEmptyUri(R.drawable.share_list_default_img)
		.showImageOnFail(R.drawable.share_list_default_img)
		.cacheInMemory(true)
		.cacheOnDisk(true)
		.considerExifParams(true)
		.displayer(new RoundedBitmapDisplayer(50))
		.build();
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
				holder.shareNiceLayout = (LinearLayout) convertView.findViewById(R.id.share_nice_layout);
				holder.shareNiceImg = (ImageView) convertView.findViewById(R.id.share_nice_img);
			}
			
			if (holder == null) {
				holder = (ViewHolder) convertView.getTag();
			}

//			AsynImageLoader.getInstance().showImageAsyn(holder.userImg, position, shareVO.image, R.drawable.person_avatar);
			ImageLoader.getInstance().displayImage(shareVO.userImage, holder.userImg, options, animateFirstListener);
			holder.nickName.setText(shareVO.nickName);
			if (StringUtils.isNull(shareVO.nickName)) {
				holder.nickName.setText("无名氏");
			}
			
			holder.content.setText(shareVO.content);
			
			if (StringUtils.isNull(shareVO.address) || !shareVO.showAddress) {
				holder.location.setText("未公开");
			}
			else
			{
				holder.location.setText(shareVO.address);
			}
			
			String created = DateUtils.yyyyMMddHHmmss2yyyyMMdd(shareVO.date);
			
			holder.date.setText(created);
			
			if (StringUtils.isNotNull(shareVO.dis)) {
				double dis = Double.parseDouble(shareVO.dis);
				if(dis > 0)
				{
					if(dis>1000)
					{
						
						NumberFormat nf = NumberFormat.getNumberInstance();
				        nf.setMaximumFractionDigits(1);
						holder.distance.setText(nf.format(dis/1000) + "km");
					}
					else
					{
						holder.distance.setText(String.valueOf((int)dis) + "m");
					}
					
				}
				else
				{
					holder.distance.setText("未定位");
				}
			}
			else
			{
				holder.distance.setText("未定位");
			}
//			AsynImageLoader.getInstance().showImageAsyn(holder.shareImg, position, shareVO.image, R.drawable.person_avatar);
//			ImageLoader.getInstance().displayImage(shareVO.image, holder.shareImg, options, animateFirstListener);
			
			
			if (StringUtils.isNotNull(shareVO.image)) 
			{
				final String[] imgs = new String[]{shareVO.image};
				holder.shareImg.setVisibility(View.VISIBLE);
				holder.shareImg.setAdapter(new MyGridAdapter(imgs, activity,animateFirstListener));
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
			
			holder.niceCount.setText(shareVO.up + "");
			
			final String positionStr = String.valueOf(position);
			final String sId = shareVO.id;
			final long count = shareVO.up;
			
			if (shareVO.shareNiced) {
				holder.niceCount.setTextColor(activity.getResources().getColor(R.color.red_text));
				holder.shareNiceImg.setImageResource(R.drawable.share_list_item_nice_ed);
				holder.shareNiceLayout.setEnabled(false);
			}
			else
			{
				holder.niceCount.setTextColor(activity.getResources().getColor(R.color.black_little));
				holder.shareNiceImg.setImageResource(R.drawable.share_list_item_nice_nor);
				holder.shareNiceLayout.setEnabled(true);
				holder.shareNiceLayout.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {

						if (QHClientApplication.getInstance().isLogined) {
							
							String accountId = SharedprefUtil.get(activity, QuhaoConstant.ACCOUNT_ID, "");
							
							String url = "share/addNice?sid=" + sId + "&accountId=" + accountId;
							final CreateShareNiceTask task = new CreateShareNiceTask(R.string.waitting, activity, url);
							task.execute(new Runnable() {
								
								@Override
								public void run() {
									JsonPack jsonPack = task.jsonPack;
									if (jsonPack != null && StringUtils.isNotNull(jsonPack.getObj()) && "true".equals(jsonPack.getObj())) {
										Message msg = refreshNiceHandler.obtainMessage();
										Map<String, String> obj = new HashMap<String, String>();
										obj.put("position", positionStr);
										obj.put("up", (count + 1) + "");
										msg.obj = obj;
										msg.sendToTarget();
									}
									else
									{
										Message msg = refreshNiceHandler.obtainMessage();
										Map<String, String> obj = new HashMap<String, String>();
										obj.put("position", positionStr);
										obj.put("up", "");
										msg.obj = obj;
										msg.sendToTarget();
									}
									
								}
							}, new Runnable() {
								@Override
								public void run() {
									Message msg = refreshNiceHandler.obtainMessage();
									Map<String, String> obj = new HashMap<String, String>();
									obj.put("position", positionStr);
									obj.put("openNum", "");
									msg.obj = obj;
									msg.sendToTarget();
								}
							});
				
						} else {
							Intent intentGetNumber = new Intent(activity, LoginActivity.class);
							intentGetNumber.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							activity.startActivity(intentGetNumber);
						}
						
					}
				});
			}

			convertView.setTag(holder);
			return convertView;
		}

	}
	
	private Handler refreshNiceHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Map<String, String> obj2 = (Map<String, String>) msg.obj;
			if (StringUtils.isNotNull(obj2.get("up"))) {
				int openNum = Integer.valueOf(obj2.get("up"));
				int position = Integer.valueOf(obj2.get("position"));
				updateView(position,openNum);
			}
			else {
				Toast.makeText(activity, "亲，网络有点异常哦。", Toast.LENGTH_SHORT).show();
			}
		}
	};
	
	
	private void updateView(int position, int up) {
		
		int visiblePos = listView.getFirstVisiblePosition();
		shares.get(position).up = up;
		shares.get(position).shareNiced = true;
		int offset = position - visiblePos;
		// 只有在可见区域才更新
		if(offset < 0) {
			return;
		}
		
		View view = listView.getChildAt(offset);
		ViewHolder holder = (ViewHolder)view.getTag();
		holder.niceCount.setText("" + up);
		holder.niceCount.setTextColor(activity.getResources().getColor(R.color.red_text));
		holder.shareNiceImg.setImageResource(R.drawable.share_list_item_nice_ed);
		holder.shareNiceLayout.setEnabled(false);
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
		
		LinearLayout shareNiceLayout;
		
		ImageView shareNiceImg;
	}
}
