package com.withiter.quhao.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.withiter.quhao.vo.TopMerchant;

public class MyPagerAdapter extends PagerAdapter {
	public List<ImageView> mViews;
	public List<TopMerchant> mDatas;
	private Context mContext;

	// public Set<ImageTask> mTask = new HashSet<ImageTask>();

	public MyPagerAdapter(Context context, List<ImageView> views,
			List<TopMerchant> datas) {
		mContext = context;
		if (views == null)
		{
			mViews = new ArrayList<ImageView>();
		}
		else
		{
			mViews = views;
		}

		this.mDatas = datas;
	}

	public MyPagerAdapter(List<ImageView> views, Context context) {
		mContext = context;
		if (views == null)
			mViews = new ArrayList<ImageView>();
		else
			mViews = views;
	}

	public void setDatas(List<TopMerchant> datas) {
		mDatas = datas;
	}

	@Override
	public int getCount() {
		return mViews.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	@Override
	public int getItemPosition(Object object) {
		return POSITION_NONE;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		Log.e("wjzwjz", "destroy the view id : " + mViews.get(position).getId());
		container.removeView(mViews.get(position));
	}
	
	/*
	@Override
	public void destroyItem(View arg0, int arg1, Object arg2) {
		View view = (View) arg0;
		Log.e("wjzwjz", "the view id : " + view.getId() + " - " + view.toString());
		((ViewPager) arg0).removeView(view);
		view = null;
	}
	 */
	
	/*
	@Override
	public Object instantiateItem(View arg0, int arg1) {

		ImageView view = (ImageView) ((ViewPager) arg0).getChildAt(arg1);
		Log.e("wjzwjz", "view : " + (view==null));
		if (view == null) {
			view = (ImageView) mViews.get(arg1);
			Log.e("wjzwjz",  " - view.parent : " + (view.getParent()==null));
			if(view.getParent()==null)
			{
				
				((ViewPager) arg0).addView(view);
				
			}
			else
			{
				ViewGroup vg = (ViewGroup) view.getParent();
				vg.removeView(mViews.get(arg1));
				((ViewPager) arg0).addView(view);
			}

		}
		view.setImageResource(R.drawable.no_logo);
		AsynImageLoader.getInstance().showImageAsyn(view, arg1, mDatas.get(arg1).merchantImage, R.drawable.no_logo);
		return view;
	}
	 */
	
	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		Log.e("wjzwjz", "instantiateItem1 : " + position);
		container.addView(mViews.get(position)); 
		Log.e("wjzwjz", "instantiateItem2 : " + position);
		return mViews.get(position);
	}
	@Override
	public void restoreState(Parcelable arg0, ClassLoader arg1) {
		// TODO Auto-generated method stub
	}

	@Override
	public Parcelable saveState() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void startUpdate(View arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void finishUpdate(View arg0) {
		// TODO Auto-generated method stub

	}
}
