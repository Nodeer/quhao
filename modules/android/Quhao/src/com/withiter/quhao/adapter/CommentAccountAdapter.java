package com.withiter.quhao.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.withiter.quhao.R;
import com.withiter.quhao.util.StringUtils;
import com.withiter.quhao.vo.Comment;

public class CommentAccountAdapter extends BaseAdapter {

	private ListView listView;
	private Activity activity;
	public List<Comment> comments;

	public CommentAccountAdapter(Activity activity, ListView listView,List<Comment> comments) {
		super();
		this.activity = activity;
		this.listView= listView;
		this.comments = comments;

	}

	@Override
	public int getCount() {
		return comments.size();
	}

	@Override
	public Object getItem(int position) {
		return comments.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Comment comment = (Comment) getItem(position);
		synchronized (comment) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				LayoutInflater inflator = (LayoutInflater) parent.getContext()
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflator.inflate(R.layout.comment_account_list_item,
						null);
				holder.modified = (TextView) convertView.findViewById(R.id.modified);
				holder.merchantName = (TextView) convertView.findViewById(R.id.merchantName);
//				holder.merchantAddress = (TextView) convertView.findViewById(R.id.merchantAddress);
//				holder.fuwu = (TextView) convertView.findViewById(R.id.fuwu);
//				holder.huanjing = (TextView) convertView.findViewById(R.id.huanjing);
//				holder.kouwei = (TextView) convertView.findViewById(R.id.kouwei);
//				holder.xingjiabi = (TextView) convertView.findViewById(R.id.xingjiabi);
				holder.star = (ImageView) convertView.findViewById(R.id.star);
				holder.content = (TextView) convertView.findViewById(R.id.content);
				
				holder.averageCost = (TextView) convertView.findViewById(R.id.averageCost);

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

			holder.modified.setText(comment.modified);
			holder.merchantName.setText(comment.merchantName);
//			holder.merchantAddress.setText(comment.merchantAddress);
//			holder.fuwu.setText(String.valueOf(comment.fuwu));
//			holder.huanjing.setText(String.valueOf(comment.huanjing));
//			holder.kouwei.setText(String.valueOf(comment.kouwei));
//			holder.xingjiabi.setText(String.valueOf(comment.xingjiabi));
			holder.star = (ImageView) convertView.findViewById(R.id.star);
			holder.content.setText(comment.content);
			holder.averageCost.setText(comment.averageCost);
			float avgValue = (comment.fuwu + comment.huanjing + comment.kouwei + comment.xingjiabi)/4;
			int avg = Math.round(avgValue);
			
			switch (avg) {
			case 0:
				holder.star.setImageResource(R.drawable.star00);
				break;
			case 1:
				holder.star.setImageResource(R.drawable.star10);
				break;
			case 2:
				holder.star.setImageResource(R.drawable.star20);
				break;
			case 3:
				holder.star.setImageResource(R.drawable.star30);
				break;
			case 4:
				holder.star.setImageResource(R.drawable.star40);
				break;
			case 5:
				holder.star.setImageResource(R.drawable.star50);
				break;
			default:
				break;
			}
			convertView.setTag(holder);
			return convertView;
		}

	}

	class ViewHolder {
//		TextView nickName;
		TextView merchantName;
//		TextView merchantAddress;
//		TextView xingjiabi;
//		TextView kouwei;
//		TextView huanjing;
//		TextView fuwu;
		ImageView star;
		TextView averageCost;
		TextView content;
		TextView modified;
	}
}
