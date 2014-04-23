package com.withiter.quhao.activity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewParent;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.withiter.quhao.QHClientApplication;
import com.withiter.quhao.R;
import com.withiter.quhao.adapter.CategoryGridAdapter;
import com.withiter.quhao.adapter.MyPagerAdapter;
import com.withiter.quhao.task.AllCategoriesTask;
import com.withiter.quhao.task.TopMerchantsTask;
import com.withiter.quhao.util.ActivityUtil;
import com.withiter.quhao.util.ImageTask;
import com.withiter.quhao.util.QuhaoLog;
import com.withiter.quhao.util.StringUtils;
import com.withiter.quhao.util.tool.ParseJson;
import com.withiter.quhao.util.tool.ProgressDialogUtil;
import com.withiter.quhao.util.tool.QuhaoConstant;
import com.withiter.quhao.view.refresh.PullToRefreshView;
import com.withiter.quhao.view.refresh.PullToRefreshView.OnFooterRefreshListener;
import com.withiter.quhao.view.refresh.PullToRefreshView.OnHeaderRefreshListener;
import com.withiter.quhao.view.viewpager.MyViewPager;
import com.withiter.quhao.vo.Category;
import com.withiter.quhao.vo.TopMerchant;

public class HomeFragment extends Fragment implements
OnHeaderRefreshListener, OnFooterRefreshListener{
	
	private static String TAG = HomeFragment.class.getName();
	
	private static final int UNLOCK_CLICK = 1000;
	
//	private GridView topMerchantsGird;
//	private TopMerchantGridAdapter topMerchantGridAdapter;
	private Button searchTextView;
	private GridView categorysGird;
	private CategoryGridAdapter categoryGridAdapter;
	private TextView cityBtn;
	private List<TopMerchant> topMerchants;
	private List<Category> categorys = null;
	protected ProgressDialogUtil progressCategory;
	protected ProgressDialogUtil progressTopMerchant;
	private boolean isClick;
	private TextView homeAdTitle;// 广告简单介绍
	private MyViewPager mViewPager;
	
	private LinearLayout adBottomLayout;
	
	private List<ImageView> mPoints;
	
	private int mPosition;// pager的位置,就是当前图片的索引号
	
	private MyPagerAdapter mPagerAdapter;
	
	private PullToRefreshView mPullToRefreshView;
	
	private float xDistance, yDistance;
	/** 记录按下的X坐标  **/
	private float mLastMotionX,mLastMotionY;
	/** 是否是左右滑动   **/
	private boolean mIsBeingDragged = true;
	
	// /////执行广告自动滚动需要用的///////////////
//	private ScheduledExecutorService scheduledExecutorService;
	
//	private boolean isFirstScheduled;
	
	private View contentView;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		Log.e("wjzwjz", "HomeFragment onAttach");
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Log.e("wjzwjz", "HomeFragment onActivityCreated");
	}
	
	@Override
	public void onViewStateRestored(Bundle savedInstanceState) {
		super.onViewStateRestored(savedInstanceState);
		Log.e("wjzwjz", "HomeFragment onViewStateRestored");
	}
	
	@Override
	public void onStart() {
		super.onStart();
		/*
		if(scheduledExecutorService != null && isFirstScheduled)
		{
			scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
			// 当Activity显示出来后，每三秒钟切换一次图片显示
			scheduledExecutorService.scheduleAtFixedRate(new ScrollTask(), 1, 3,
					TimeUnit.SECONDS);
			
		}*/
		Log.e("wjzwjz", "HomeFragment onStart");
	}
	
	@Override
	public void onPause() {
		super.onPause();
		Log.e("wjzwjz", "HomeFragment onPause");
	}
	
	@Override
	public void onStop() {
		super.onStop();
		/*
		// 当Activity不可见的时候停止切换
		if(null != scheduledExecutorService)
		{
			scheduledExecutorService.shutdown();
		}
		*/
		Log.e("wjzwjz", "HomeFragment onStop");
	}
	
	/*
	// 切换当前显示的图片
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			mViewPager.setCurrentItem(mPosition);// 切换当前显示的图片
			homeAdTitle.setText(topMerchants.get(mPosition).name);
		};
	};

	// 换行切换任务

	private class ScrollTask implements Runnable {
		public void run() {
			synchronized (mViewPager) {
				// System.out.println("mPosition: " + mPosition);
				mPosition = (mPosition + 1) % mPoints.size();
				handler.obtainMessage().sendToTarget(); // 通过Handler切换图片
				// System.out.println("切换图片++++"+mPosition);
			}
		}

	}*/
	
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		Log.e("wjzwjz", "HomeFragment onDestroyView");
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.e("wjzwjz", "HomeFragment onDestroy");
	}
	
	@Override
	public void onDetach() {
		super.onDetach();
		Log.e("wjzwjz", "HomeFragment onDetach");
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.e("wjzwjz", "HomeFragment onCreateView");
		
		contentView = inflater.inflate(R.layout.main_fragment_layout, container,false);
		mPullToRefreshView = (PullToRefreshView) contentView.findViewById(R.id.main_pull_refresh_view);
		
		mPullToRefreshView.setOnHeaderRefreshListener(this);
		mPullToRefreshView.setOnFooterRefreshListener(this);
//		mPullToRefreshView.setLastUpdated(new Date().toLocaleString());
		
		searchTextView = (Button) contentView.findViewById(R.id.edit_search);
		searchTextView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), MerchantsSearchActivity.class);
				startActivity(intent);
				getActivity().overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
			}
		});
		
		mViewPager = (MyViewPager) contentView.findViewById(R.id.home_view_pager);
		
		homeAdTitle = (TextView) contentView.findViewById(R.id.home_ad_title);
		adBottomLayout = (LinearLayout) contentView.findViewById(R.id.home_ad_bottom_layout);
		mPoints = new ArrayList<ImageView>();
		InputMethodManager inputMethodManager = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(searchTextView.getWindowToken(), 0);

		// top merchant function
		topMerchants = new ArrayList<TopMerchant>();
//		topMerchantsGird = (GridView) contentView.findViewById(R.id.topMerchants);
//		topMerchantsGird.setOnItemClickListener(topMerchantClickListener);
		
		// all categories
		categorys = new ArrayList<Category>();
		categorysGird = (GridView) contentView.findViewById(R.id.categorys);
		
		categorysGird.setOnItemClickListener(categorysClickListener);
		
		cityBtn = (TextView) contentView.findViewById(R.id.city);
		
		cityBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

				if(isClick)
				{
					return;
				}
				
				isClick = true;
				
				switch(v.getId())
				{
					case R.id.city:
						Intent intent = new Intent();
						intent.setClass(getActivity(), CitySelectActivity.class);
						startActivity(intent);
		 			default:
						unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
					break;
				}
			}
		});
		
		return contentView;
	}
	
	@Override
	public void onHeaderRefresh(PullToRefreshView view) {
		mPullToRefreshView.postDelayed(new Runnable() {

			@Override
			public void run() {
				getTopMerchantsFromServerAndDisplay();
				getCategoriesFromServerAndDisplay();
				mPullToRefreshView.onHeaderRefreshComplete("更新于:"+new Date().toLocaleString());
//				mPullToRefreshView.onHeaderRefreshComplete();
			}
		}, 1000);

	}
	
	private void buildPager() {
		// 广告下方的view
		adBottomLayout.setGravity(Gravity.CENTER_VERTICAL);
		if(null != mPoints && !mPoints.isEmpty())
		{
			for (int i = 0; i < mPoints.size(); i++) {
				adBottomLayout.removeView(mPoints.get(i));
			}
		}
		mPoints = new ArrayList<ImageView>();
		adBottomLayout.getChildAt(0);
		
		ArrayList<ImageView> views = new ArrayList<ImageView>();
		ImageView image;
		if (topMerchants != null) {
			for (int num = 0; num < topMerchants.size(); num++) {
				image = new ImageView(getActivity());
				// 图片缩放
				image.setScaleType(ScaleType.FIT_XY);
				views.add(image);
				
				if(StringUtils.isNotNull(topMerchants.get(num).merchantImage))
				{
					ImageTask task = new ImageTask(image,topMerchants.get(num).merchantImage, false, getActivity());

					task.execute(new Runnable() {

						@Override
						public void run() {

						}

					},new Runnable() {
						
						@Override
						public void run() {
							
						}
					});
				}
				else
				{
					image.setImageResource(R.drawable.no_logo);
				}
				
				// 广告下方的原于圆点view
				ImageView point = new ImageView(getActivity());
				point.setAdjustViewBounds(true);
				if (num == 0)
				{
					homeAdTitle.setText(topMerchants.get(num).name);
					point.setBackgroundResource(R.drawable.point_white);
				}
				else
				{
					point.setBackgroundResource(R.drawable.point_deep);
				}

				point.setLayoutParams(new LayoutParams(10, 10));
				mPoints.add(point);

//				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
//						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

//				lp.setMargins(10, 0, 10, 0);

				image.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						if (topMerchants.get(mPosition)!=null) {
							String mid = topMerchants.get(mPosition).mid;
							QuhaoLog.d(TAG, "mid:" + mid);
							if (StringUtils.isNull(mid)) {
								Builder dialog = new AlertDialog.Builder(getActivity());
								dialog.setTitle("温馨提示").setMessage("推荐商家虚席以待").setPositiveButton("确定", null);
								dialog.show();
								return;
							}
							Intent intent = new Intent();
							intent.putExtra("merchantId", mid);
							intent.setClass(getActivity(), MerchantDetailActivity.class);
							startActivity(intent);
							getActivity().overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);

						}
					}
				});
				
				if(point.getParent() != null)
				{
					ViewGroup vg = (ViewGroup) point.getParent();
					vg.removeView(point);
				}
				
				adBottomLayout.addView(point);
			}

			mPagerAdapter = new MyPagerAdapter(getActivity(), views, topMerchants);
			mViewPager.setAdapter(mPagerAdapter);
			
			mViewPager.setOnTouchListener(new OnTouchListener() {
				
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					mViewPager.getGestureDetector().onTouchEvent(event);
					final float x = event.getRawX();
					final float y = event.getRawY();
					
	                switch (event.getAction()) {  
	                case MotionEvent.ACTION_DOWN:  
	                    xDistance = yDistance = 0f;
	                	mLastMotionX = x;
	                	mLastMotionY = y;
	                case MotionEvent.ACTION_MOVE:  
	                    final float xDiff = Math.abs(x - mLastMotionX);
	                    final float yDiff = Math.abs(y - mLastMotionY);
	                    xDistance += xDiff;
	                    yDistance += yDiff;
	                    
	                    float dx = xDistance - yDistance;
	                    // 左右滑动避免和下拉刷新冲突   
	                    if (xDistance > yDistance || Math.abs(xDistance - yDistance) < 0.00001f) {
	                        mIsBeingDragged = true;
	                        mLastMotionX =  x;
	                        mLastMotionY = y;
	                        ((ViewParent) v.getParent()).requestDisallowInterceptTouchEvent(true);
	                    } else {
	                        mIsBeingDragged = false;
	                        ((ViewParent) v.getParent()).requestDisallowInterceptTouchEvent(false);
	                    }
	                    break;  
	                case MotionEvent.ACTION_UP:  
	                 	break;  
	                case MotionEvent.ACTION_CANCEL:
	                	if(mIsBeingDragged) {
	                		((ViewParent) v.getParent()).requestDisallowInterceptTouchEvent(false);
						}
	                	break;
	                default:  
	                    break;  
	                }  
	                return false;  
				}
			});
			if (topMerchants.size() > 1) {
				adBottomLayout.setVisibility(View.VISIBLE);
			} else {
				adBottomLayout.setVisibility(View.GONE);
			}
			mViewPager.setOnPageChangeListener(new MyListener());
		} else {
			image = new ImageView(getActivity());
			image.setAdjustViewBounds(true);
			views.add(image);
			mViewPager.setAdapter(new MyPagerAdapter(views, getActivity()));
		}
		/*
		if(null == scheduledExecutorService)
		{
			scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
			
			// 当Activity显示出来后，每三秒钟切换一次图片显示
			scheduledExecutorService.scheduleAtFixedRate(new ScrollTask(), 1, 3,
					TimeUnit.SECONDS);
			
			isFirstScheduled = true;
		}*/

	}

	// 广告滑动监听
	class MyListener implements OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int arg0) {
			
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			
		}

		// 当选状态
		@Override
		public void onPageSelected(int arg0) {
			// System.out.println("广告" + arg0);
			// currentItem = arg0;
			homeAdTitle.setText(topMerchants.get(arg0).name);
			mPosition = arg0;
			int i = 0;
			for (i = 0; i < mPoints.size(); i++) {
				mPoints.get(i).setBackgroundResource(R.drawable.point_deep);
				if (arg0 == i) {
					mPoints.get(i)
							.setBackgroundResource(R.drawable.point_white);
				}
			}

			// if (i == 3) {
			// System.out.println("动画设计和计划的话手机号");
			// onPageSelected(0);
			// }

		}

	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.e("wjzwjz", "HomeFragment onCreate");
		// TODO add default view here
		if (!ActivityUtil.isNetWorkAvailable(getActivity())) {
			Builder dialog = new AlertDialog.Builder(getActivity());
			dialog.setTitle("温馨提示").setMessage("Wifi/蜂窝网络未打开，或者网络情况不是很好哟").setPositiveButton("确定", null);
			dialog.show();
			
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		Log.e("wjzwjz", "HomeFragment onResume");
		cityBtn.setText(QHClientApplication.getInstance().defaultCity.cityName);
		getTopMerchantsFromServerAndDisplay();
		getCategoriesFromServerAndDisplay();
	}
	
	protected Handler unlockHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == UNLOCK_CLICK) {
				
				isClick = false;
			}
		}
	};
	
	/**
	 * get top merchants from server side and display
	 */
	public void getTopMerchantsFromServerAndDisplay() {
//		progressTopMerchant = new ProgressDialogUtil(getActivity(), R.string.empty, R.string.querying, false);
//		progressTopMerchant.showProgress();
		final TopMerchantsTask task = new TopMerchantsTask(0, getActivity(), "MerchantController/getTopMerchants?x=6&cityCode=" + QHClientApplication.getInstance().defaultCity.cityCode);
		task.execute(new Runnable() {
			
			@Override
			public void run() {
				String result = task.result;
				if (null == topMerchants) {
					topMerchants = new ArrayList<TopMerchant>();
				}
				topMerchants.clear();
				topMerchants.addAll(ParseJson.getTopMerchants(result));
				
				// check the numbers of top merchant
				int topMerchantCount = topMerchants.size();
				if (topMerchantCount < 6) {
					for (int i = 0; i < 6 - topMerchantCount; i++) {
						TopMerchant topMerchant = new TopMerchant();
						topMerchants.add(topMerchant);
					}
				}
				
				//TODO: 改变top Merchant的显示方式为滑动形式的。
				buildPager();
//				topMerchantsUpdateHandler.obtainMessage(200, topMerchants).sendToTarget();
				
			}
		}, new Runnable() {
			
			@Override
			public void run() {
				String result = task.result;
				if (null == topMerchants) {
					topMerchants = new ArrayList<TopMerchant>();
				}
				topMerchants.clear();
				topMerchants.addAll(ParseJson.getTopMerchants(result));

				// check the numbers of top merchant
				int topMerchantCount = topMerchants.size();
				if (topMerchantCount < 6) {
					for (int i = 0; i < 6 - topMerchantCount; i++) {
						TopMerchant topMerchant = new TopMerchant();
						topMerchants.add(topMerchant);
					}
				}
				//TODO: 改变top Merchant的显示方式为滑动形式的。
				buildPager();
//				topMerchantsUpdateHandler.obtainMessage(200, topMerchants).sendToTarget();
				
			}
		});
		
		/*
		Thread t = new Thread() {
			@Override
			public void run() {
				Looper.prepare();
				try {
					QuhaoLog.d(TAG, "Start to get Top Merchants data form server.");
					String result = CommonHTTPRequest.get("MerchantController/getTopMerchants?x=6");
					QuhaoLog.d(TAG, result);
					if (StringUtils.isNull(result)) {
						// TODO display error page here
						unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
					} else {
						if (null == topMerchants) {
							topMerchants = new ArrayList<TopMerchant>();
						}
						topMerchants.clear();
						topMerchants.addAll(ParseJson.getTopMerchants(result));

						// check the numbers of top merchant
						int topMerchantCount = topMerchants.size();
						if (topMerchantCount < 6) {
							for (int i = 0; i < 6 - topMerchantCount; i++) {
								TopMerchant topMerchant = new TopMerchant();
								topMerchants.add(topMerchant);
							}
						}
						topMerchantsUpdateHandler.obtainMessage(200, topMerchants).sendToTarget();
					}
				} catch (ClientProtocolException e) {
					// TODO display error page here
					unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
					e.printStackTrace();
					Builder dialog = new AlertDialog.Builder(getActivity());
					dialog.setTitle("温馨提示").setMessage("使用\"取号\"人数火爆，亲，稍等片刻").setPositiveButton("确定", null);
					dialog.show();
				} catch (IOException e) {
					// TODO display error page here
					unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
					// Log.e(TAG, e.getCause().toString(), e);
					e.printStackTrace();
					Builder dialog = new AlertDialog.Builder(getActivity());
					dialog.setTitle("温馨提示").setMessage("使用\"取号\"人数火爆，亲，稍等片刻").setPositiveButton("确定", null);
					dialog.show();
				} finally {
					progressTopMerchant.closeProgress();
				}
				Looper.loop();
			}
		};
		t.start();
		*/
	}
	
	private OnItemClickListener topMerchantClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			String mid = topMerchants.get(position).mid;
			QuhaoLog.d(TAG, "mid:" + mid);
			if (StringUtils.isNull(mid)) {
				Builder dialog = new AlertDialog.Builder(getActivity());
				dialog.setTitle("温馨提示").setMessage("推荐商家虚席以待").setPositiveButton("确定", null);
				dialog.show();
				return;
			}
			Intent intent = new Intent();
			intent.putExtra("merchantId", mid);
			intent.setClass(getActivity(), MerchantDetailActivity.class);
			startActivity(intent);
			getActivity().overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
		}
	};
	
	private OnItemClickListener categorysClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			Category category = categorys.get(position);
			QuhaoLog.d(TAG, "the category is : " + category.categoryType + ", the count is : " + category.count);
			Intent intent = new Intent();
			intent.putExtra("categoryType", category.categoryType);
			intent.putExtra("cateName", category.cateName);
			intent.putExtra("categoryCount", String.valueOf(category.count));

			intent.setClass(getActivity(), MerchantListActivity.class);

			startActivity(intent);
			getActivity().overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
		}
	};
	
	/**
	 * 处理top merchant的UI更新
	 
	private Handler topMerchantsUpdateHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 200) {
				super.handleMessage(msg);
				topMerchantGridAdapter = new TopMerchantGridAdapter(topMerchants, getActivity());
				topMerchantsGird.setAdapter(topMerchantGridAdapter);
				topMerchantGridAdapter.notifyDataSetChanged();
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			}
		}
	};
	*/
	private Handler categorysUpdateHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 200) {
				super.handleMessage(msg);
				categoryGridAdapter = new CategoryGridAdapter(categorys, getActivity());
				categorysGird.setAdapter(categoryGridAdapter);
				categoryGridAdapter.notifyDataSetChanged();
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			}
		}
	};

	/**
	 * get all categories from server and display them
	 */
	public void getCategoriesFromServerAndDisplay() {
//		progressCategory = new ProgressDialogUtil(getActivity(), R.string.empty, R.string.querying, false);
//		progressCategory.showProgress();
		
		final AllCategoriesTask task = new AllCategoriesTask(0, getActivity(), "MerchantController/allCategories?cityCode=" + QHClientApplication.getInstance().defaultCity.cityCode);
		task.execute(new Runnable() {
			
			@Override
			public void run() {
				String result = task.result;
				if (StringUtils.isNull(result)) {
					unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				} else {
					if (null == categorys) {
						categorys = new ArrayList<Category>();
					}
					categorys.clear();
					categorys.addAll(ParseJson.getCategorys(result));
					categorysUpdateHandler.obtainMessage(200, categorys).sendToTarget();
				}
				
			}
		}, new Runnable() {
			
			@Override
			public void run() {
				String result = task.result;
				if (StringUtils.isNull(result)) {
					unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				} else {
					if (null == categorys) {
						categorys = new ArrayList<Category>();
					}
					categorys.clear();
					categorys.addAll(ParseJson.getCategorys(result));
					categorysUpdateHandler.obtainMessage(200, categorys).sendToTarget();
				}
			}
		});
		
		/*
		Thread t = new Thread() {
			@Override
			public void run() {
				Looper.prepare();
				try {
					QuhaoLog.v(TAG, "get categorys data form server begin");
					String result = CommonHTTPRequest.get("MerchantController/allCategories");
					if (StringUtils.isNull(result)) {
						unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
					} else {
						if (null == categorys) {
							categorys = new ArrayList<Category>();
						}
						categorys.clear();
						categorys.addAll(ParseJson.getCategorys(result));
						categorysUpdateHandler.obtainMessage(200, categorys).sendToTarget();
					}

				} catch (Exception e) {
					unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
					e.printStackTrace();
				} finally {
					progressCategory.closeProgress();
				}
				Looper.loop();
			}
		};
		t.start();
		*/
	}

	@Override
	public void onFooterRefresh(PullToRefreshView view) {
		// 处理下拉刷新最新数据
		mPullToRefreshView.onFooterRefreshComplete();
	}
}
