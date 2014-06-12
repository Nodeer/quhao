package com.withiter.quhao.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.withiter.quhao.R;
import com.withiter.quhao.util.tool.AsynImageLoader;
import com.withiter.quhao.vo.TopMerchant;

public class MyPagerAdapter extends PagerAdapter {
	private List<ImageView> mViews;
	private List<TopMerchant> mDatas;
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
	public void destroyItem(View arg0, int arg1, Object arg2) {
		View view = (View) arg0;
		((ViewPager) arg0).removeView(view);
		view = null;
	}

	@Override
	public Object instantiateItem(View arg0, int arg1) {

		ImageView view = (ImageView) ((ViewPager) arg0).getChildAt(arg1);
		if (view == null) {
			view = (ImageView) mViews.get(arg1);
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
