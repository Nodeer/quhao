package com.withiter.quhao.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.withiter.quhao.QHClientApplication;
import com.withiter.quhao.R;
import com.withiter.quhao.adapter.ActivityAdapter;
import com.withiter.quhao.adapter.MyPagerAdapter;
import com.withiter.quhao.data.CategoryData;
import com.withiter.quhao.task.AllCategoriesTask;
import com.withiter.quhao.task.GetActivitiesTask;
import com.withiter.quhao.task.GetChooseHardMerchantTask;
import com.withiter.quhao.task.JsonPack;
import com.withiter.quhao.task.TopMerchantsTask;
import com.withiter.quhao.util.ActivityUtil;
import com.withiter.quhao.util.StringUtils;
import com.withiter.quhao.util.tool.AsynImageLoader;
import com.withiter.quhao.util.tool.ParseJson;
import com.withiter.quhao.util.tool.QuhaoConstant;
import com.withiter.quhao.view.refresh.PullToRefreshView;
import com.withiter.quhao.view.refresh.PullToRefreshView.OnFooterRefreshListener;
import com.withiter.quhao.view.refresh.PullToRefreshView.OnHeaderRefreshListener;
import com.withiter.quhao.view.viewpager.AutoScrollViewPager;
import com.withiter.quhao.vo.ActivityVO;
import com.withiter.quhao.vo.Category;
import com.withiter.quhao.vo.Merchant;
import com.withiter.quhao.vo.TopMerchant;

public class HomeFragment extends Fragment implements OnHeaderRefreshListener, OnFooterRefreshListener, OnClickListener {

	private static final int UNLOCK_CLICK = 1000;

	private Button searchTextView;
	private TextView cityBtn;
	private List<TopMerchant> topMerchants;
	private List<Category> categorys = null;
	private boolean isClick;
	private TextView homeAdTitle;								// 广告简单介绍
	private AutoScrollViewPager mViewPager;
	private LinearLayout adBottomLayout;
	private List<ImageView> mPoints;
	private int mPosition;										// pager的位置,就是当前图片的索引号
	private MyPagerAdapter mPagerAdapter;
	private PullToRefreshView mPullToRefreshView;
	
	private LinearLayout noResultLayout;

	private float xDistance, yDistance;
	/** 记录按下的X坐标 **/
	private float mLastMotionX, mLastMotionY;
	/** 是否是左右滑动 **/
	private boolean mIsBeingDragged = true;

	private View contentView;
	private ImageView myAttentions;
	private ImageView noSequenceMerchants;
	private ImageView merchantChatView;
	private ImageView getNumberView;
	private ImageView chooseHardView;
	
	private ListView activityListView;
	
	private List<ActivityVO> activityList;
	
	private ActivityAdapter activityAdapter;
	
	private List<ImageView> views;

	@Override
	public void onAttach(Activity activity) {
		Log.e("wjzwjz", "HomeFragment onAttach");
		super.onAttach(activity);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		Log.e("wjzwjz", "HomeFragment onActivityCreated");
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onViewStateRestored(Bundle savedInstanceState) {
		Log.e("wjzwjz", "HomeFragment onViewStateRestored");
		super.onViewStateRestored(savedInstanceState);
	}

	@Override
	public void onStart() {
		Log.e("wjzwjz", "HomeFragment onStart");
		super.onStart();
	}

	@Override
	public void onPause() {
		Log.e("wjzwjz", "HomeFragment onPause");
		if (mViewPager!= null && null != views && !views.isEmpty() && mPagerAdapter != null) {
			mViewPager.stopAutoScroll();
		}
		super.onPause();
	}

	@Override
	public void onStop() {
		Log.e("wjzwjz", "HomeFragment onStop");
		if (mViewPager!= null && null != views && !views.isEmpty() && mPagerAdapter != null) {
			mViewPager.stopAutoScroll();
		}
		
		super.onStop();
	}

	@Override
	public void onDestroyView() {
		Log.e("wjzwjz", "HomeFragment onDestroyView");
		getActivity().unregisterReceiver(cityChangeReceiver);
		super.onDestroyView();
	}

	@Override
	public void onDestroy() {
		Log.e("wjzwjz", "HomeFragment onDestroy");
		super.onDestroy();
	}

	@Override
	public void onDetach() {
		Log.e("wjzwjz", "HomeFragment onDetach");
		super.onDetach();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		if (!ActivityUtil.isNetWorkAvailable(getActivity())) {
			Toast.makeText(getActivity(), R.string.network_error_info, Toast.LENGTH_SHORT).show();
		}
		
		if(contentView != null)
		{
			ViewGroup vg = (ViewGroup) contentView.getParent();
			vg.removeView(contentView);
			getActivity().registerReceiver(cityChangeReceiver, new IntentFilter(QuhaoConstant.ACTION_CITY_CHANGED));
			
			return contentView;
		}
		
		contentView = inflater.inflate(R.layout.main_fragment_layout, container, false);
		mPullToRefreshView = (PullToRefreshView) contentView.findViewById(R.id.main_pull_refresh_view);

		mPullToRefreshView.setOnHeaderRefreshListener(this);
		mPullToRefreshView.setOnFooterRefreshListener(this);
		
		noResultLayout = (LinearLayout) contentView.findViewById(R.id.no_result_layout);

		searchTextView = (Button) contentView.findViewById(R.id.edit_search);
		searchTextView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), MerchantsSearchActivity.class);
				startActivity(intent);
			}
		});

		myAttentions = (ImageView) contentView.findViewById(R.id.my_attention);
		noSequenceMerchants = (ImageView) contentView.findViewById(R.id.no_sequence_merchants);
		merchantChatView = (ImageView) contentView.findViewById(R.id.btn_chat_room);
		getNumberView = (ImageView) contentView.findViewById(R.id.btn_get_number);
		chooseHardView = (ImageView) contentView.findViewById(R.id.btn_choose_hard);
		myAttentions.setOnClickListener(this);
		noSequenceMerchants.setOnClickListener(this);
		merchantChatView.setOnClickListener(this);
		getNumberView.setOnClickListener(this);
		chooseHardView.setOnClickListener(this);

		mViewPager = (AutoScrollViewPager) contentView.findViewById(R.id.home_view_pager);
		mViewPager.setInterval(3000);
		homeAdTitle = (TextView) contentView.findViewById(R.id.home_ad_title);
		adBottomLayout = (LinearLayout) contentView.findViewById(R.id.home_ad_bottom_layout);
		mPoints = new ArrayList<ImageView>();
		InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(searchTextView.getWindowToken(), 0);

		// top merchant function
		topMerchants = new ArrayList<TopMerchant>();

		
		activityListView = (ListView) contentView.findViewById(R.id.activity_list_view);
		
		// all categories
		categorys = new ArrayList<Category>();
//		categorysGird = (GridView) contentView.findViewById(R.id.categorys);
//		categorysGird.setOnItemClickListener(categorysClickListener);
		activityListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				ActivityVO vo = activityList.get(position);
				Intent intent = new Intent();
				intent.putExtra("merchantId", vo.mid);
				intent.setClass(getActivity(), MerchantDetailActivity.class);
				startActivity(intent);
				
			}
		});

		// 城市选择按钮
		cityBtn = (TextView) contentView.findViewById(R.id.city);
		cityBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isClick) {
					return;
				}
				isClick = true;
				switch (v.getId()) {
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
		
		cityBtn.setText(QHClientApplication.getInstance().defaultCity.cityName);
//		categorysGird.setVisibility(View.VISIBLE);
		activityListView.setVisibility(View.VISIBLE);
		noResultLayout.setVisibility(View.GONE);
		
		getActivity().registerReceiver(cityChangeReceiver, new IntentFilter(QuhaoConstant.ACTION_CITY_CHANGED));
		getTopMerchantsFromServerAndDisplay();
		getCategoriesFromServerAndDisplay();
		getActivities();
		return contentView;
	}

	private BroadcastReceiver cityChangeReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			
			String action = intent.getAction();
			if(QuhaoConstant.ACTION_CITY_CHANGED.equals(action))
			{
				cityBtn.setText(QHClientApplication.getInstance().defaultCity.cityName);
				getTopMerchantsFromServerAndDisplay();
				getActivities();
				getCategoriesFromServerAndDisplay();
			}
			
		}
	};
	
	@Override
	public void onHeaderRefresh(PullToRefreshView view) {
		mPullToRefreshView.postDelayed(new Runnable() {

			@Override
			public void run() {
//				categorysGird.setVisibility(View.VISIBLE);
				activityListView.setVisibility(View.VISIBLE);
				noResultLayout.setVisibility(View.GONE);
				getTopMerchantsFromServerAndDisplay();
				getCategoriesFromServerAndDisplay();
				getActivities();
				mPullToRefreshView.onHeaderRefreshComplete();
				// mPullToRefreshView.onHeaderRefreshComplete();
			}
		}, 1000);

	}

	private void buildPager() {
		// 广告下方的view
		adBottomLayout.setGravity(Gravity.CENTER_VERTICAL);
		if (null != mPoints && !mPoints.isEmpty()) {
			for (int i = 0; i < mPoints.size(); i++) {
				adBottomLayout.removeView(mPoints.get(i));
			}
		}
		mPoints = new ArrayList<ImageView>();
		adBottomLayout.getChildAt(0);

		if (null != views && !views.isEmpty()) {
			for (int i = 0; i < views.size(); i++) {
				if (views.get(i).getParent() != null) {
					ViewGroup vg = (ViewGroup) views.get(i).getParent();
					vg.removeView(views.get(i));
				}
				
			}
		}
		
		views = new ArrayList<ImageView>();
		ImageView image;
		if (topMerchants != null) {
			for (int num = 0; num < topMerchants.size(); num++) {
				image = new ImageView(getActivity());
				// 图片缩放
				image.setScaleType(ScaleType.FIT_XY);
				views.add(image);

				if (StringUtils.isNotNull(topMerchants.get(num).merchantImage)) {
					image.setImageResource(R.drawable.no_logo);
					AsynImageLoader.getInstance().showImageAsyn(image, 0, topMerchants.get(num).merchantImage, R.drawable.no_logo);
				} else {
					image.setImageResource(R.drawable.no_logo);
				}

				// 广告下方的原于圆点view
				ImageView point = new ImageView(getActivity());
				point.setAdjustViewBounds(true);
				if (num == 0) {
					homeAdTitle.setText(topMerchants.get(num).name);
					point.setBackgroundResource(R.drawable.point_white);
				} else {
					point.setBackgroundResource(R.drawable.point_deep);
				}

				point.setLayoutParams(new LayoutParams(10, 10));
				mPoints.add(point);

				// LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				// LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

				// lp.setMargins(10, 0, 10, 0);

				image.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						if (topMerchants.get(mPosition) != null) {
							String mid = topMerchants.get(mPosition).mid;
//							QuhaoLog.d(TAG, "mid:" + mid);
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

						}
					}
				});

				if (point.getParent() != null) {
					ViewGroup vg = (ViewGroup) point.getParent();
					vg.removeView(point);
				}

				adBottomLayout.addView(point);
			}

			if (null == mPagerAdapter) {
				mPagerAdapter = new MyPagerAdapter(getActivity(), views, topMerchants);
			}
			else
			{
				mPagerAdapter.mViews = views;
				mPagerAdapter.mDatas = topMerchants;
			}
			
			
			mViewPager.setAdapter(mPagerAdapter);

			mViewPager.startAutoScroll();
			mViewPager.setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					mViewPager.getGestureDetector().onTouchEvent(event);
					final float x = event.getRawX();
					final float y = event.getRawY();

					switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						mViewPager.stopAutoScroll();
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
							mLastMotionX = x;
							mLastMotionY = y;
							((ViewParent) v.getParent()).requestDisallowInterceptTouchEvent(true);
						} else {
							mIsBeingDragged = false;
							((ViewParent) v.getParent()).requestDisallowInterceptTouchEvent(false);
						}
						break;
					case MotionEvent.ACTION_UP:
						mViewPager.startAutoScroll();
						break;
					case MotionEvent.ACTION_CANCEL:
						if (mIsBeingDragged) {
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
					mPoints.get(i).setBackgroundResource(R.drawable.point_white);
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
	}

	@Override
	public void onResume() {
		if (mViewPager!= null && null != views && !views.isEmpty() && mPagerAdapter != null) {
			mViewPager.startAutoScroll();
		}
		super.onResume();
		
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
		// progressTopMerchant = new ProgressDialogUtil(getActivity(),
		// R.string.empty, R.string.querying, false);
		// progressTopMerchant.showProgress();
		final TopMerchantsTask task = new TopMerchantsTask(0, getActivity(), "getTopMerchants?x=6&cityCode=" + QHClientApplication.getInstance().defaultCity.cityCode);
		task.execute(new Runnable() {

			@Override
			public void run() {
				JsonPack result = task.jsonPack;
				if (null == topMerchants) {
					topMerchants = new ArrayList<TopMerchant>();
				}
				topMerchants.clear();
				topMerchants.addAll(ParseJson.getTopMerchants(result.getObj()));

				// check the numbers of top merchant
				int topMerchantCount = topMerchants.size();
				if (topMerchantCount < 6) {
					for (int i = 0; i < 6 - topMerchantCount; i++) {
						TopMerchant topMerchant = new TopMerchant();
						topMerchants.add(topMerchant);
					}
				}

				// 改变top Merchant的显示方式为滑动形式的。
				buildPager();
				// topMerchantsUpdateHandler.obtainMessage(200,
				// topMerchants).sendToTarget();

			}
		}, new Runnable() {

			@Override
			public void run() {
				JsonPack result = task.jsonPack;
				if (null == topMerchants) {
					topMerchants = new ArrayList<TopMerchant>();
				}
				topMerchants.clear();
				topMerchants.addAll(ParseJson.getTopMerchants(result.getObj()));

				// check the numbers of top merchant
				int topMerchantCount = topMerchants.size();
				if (topMerchantCount < 6) {
					for (int i = 0; i < 6 - topMerchantCount; i++) {
						TopMerchant topMerchant = new TopMerchant();
						topMerchants.add(topMerchant);
					}
				}
				//改变top Merchant的显示方式为滑动形式的。
				buildPager();
				// topMerchantsUpdateHandler.obtainMessage(200,
				// topMerchants).sendToTarget();

			}
		});

	}

	private OnItemClickListener categorysClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			Category category = categorys.get(position);
//			QuhaoLog.d(TAG, "the category is : " + category.categoryType + ", the count is : " + category.count);
//			Intent intent = new Intent();
//			intent.putExtra("categoryType", category.categoryType);
//			intent.putExtra("cateName", category.cateName);
//			intent.putExtra("categoryCount", String.valueOf(category.count));
//			intent.setClass(getActivity(), MerchantListActivity.class);
//			startActivity(intent);
			
			Intent intent = new Intent();
			intent.putExtra("categoryType", category.categoryType);
			intent.putExtra("cateName", category.cateName);
			intent.putExtra("categoryCount", String.valueOf(category.count));
			
			ArrayList<CategoryData> categoryDatas = new ArrayList<CategoryData>();
			if (categorys != null && !categorys.isEmpty()) {
				CategoryData data = null;
				
				for (int i = 0; i < categorys.size(); i++) {
					data = new CategoryData();
					data.setCount(categorys.get(i).count);
					data.setCategoryType(categorys.get(i).categoryType);
					data.setCateName(categorys.get(i).cateName);
					categoryDatas.add(data);
				}
			}
			
			Bundle mBundle = new Bundle();
			mBundle.putParcelableArrayList("categorys", categoryDatas);
			intent.putExtras(mBundle);
			
			intent.setClass(getActivity(), MerchantListActivity.class);
			startActivity(intent);
			
		}
	};

	
	private Handler categorysUpdateHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 200) {
				super.handleMessage(msg);
//				categoryGridAdapter = new CategoryGridAdapter(categorys, getActivity());
//				categorysGird.setAdapter(categoryGridAdapter);
//				categoryGridAdapter.notifyDataSetChanged();
//				if(null != categorys && !categorys.isEmpty())
//				{
//					categorysGird.setVisibility(View.VISIBLE);
//					noResultLayout.setVisibility(View.GONE);
//				}
//				else
//				{
//					categorysGird.setVisibility(View.GONE);
//					noResultLayout.setVisibility(View.VISIBLE);
//				}
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			}
		}
	};
	
	
	private Handler activitiesUpdateHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 200) {
				super.handleMessage(msg);
				activityAdapter = new ActivityAdapter(getActivity(),activityListView,activityList);
				activityListView.setAdapter(activityAdapter);
				activityAdapter.notifyDataSetChanged();
				if(null != activityList && !activityList.isEmpty())
				{
					activityListView.setVisibility(View.VISIBLE);
					noResultLayout.setVisibility(View.GONE);
				}
				else
				{
					activityListView.setVisibility(View.GONE);
					noResultLayout.setVisibility(View.VISIBLE);
				}
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			}
		}
	};

	/**
	 * get all categories from server and display them
	 */
	public void getActivities() {

		final GetActivitiesTask task = new GetActivitiesTask(0, getActivity(), "app/activity?cityCode=" + QHClientApplication.getInstance().defaultCity.cityCode);
		task.execute(new Runnable() {

			@Override
			public void run() {
				JsonPack result = task.jsonPack;
				if (null == activityList) {
					activityList = new ArrayList<ActivityVO>();
				}
				activityList.clear();
				activityList.addAll(ParseJson.getActivities(result.getObj()));
				activitiesUpdateHandler.obtainMessage(200, categorys).sendToTarget();

			}
		}, new Runnable() {

			@Override
			public void run() {
				if (null == activityList) {
					activityList = new ArrayList<ActivityVO>();
				}
				activitiesUpdateHandler.obtainMessage(200, categorys).sendToTarget();
			}
		});

	}
	
	/**
	 * get all categories from server and display them
	 */
	public void getCategoriesFromServerAndDisplay() {

		final AllCategoriesTask task = new AllCategoriesTask(0, getActivity(), "allCategories?cityCode=" + QHClientApplication.getInstance().defaultCity.cityCode);
		task.execute(new Runnable() {

			@Override
			public void run() {
				String result = task.result;
				if (null == categorys) {
					categorys = new ArrayList<Category>();
				}
				categorys.clear();
				categorys.addAll(ParseJson.getCategorys(result));
				categorysUpdateHandler.obtainMessage(200, categorys).sendToTarget();

			}
		}, new Runnable() {

			@Override
			public void run() {
				String result = task.result;
				if (null == categorys) {
					categorys = new ArrayList<Category>();
				}
				categorys.clear();
				categorys.addAll(ParseJson.getCategorys(result));
				categorysUpdateHandler.obtainMessage(200, categorys).sendToTarget();
			}
		});

	}

	@Override
	public void onFooterRefresh(PullToRefreshView view) {
		// 处理下拉刷新最新数据
		mPullToRefreshView.onFooterRefreshComplete();
	}

	@Override
	public void onClick(View v) {

		if (isClick) {
			return;
		}
		isClick = true;

		switch (v.getId()) {
		case R.id.my_attention:
			unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			if (QHClientApplication.getInstance().isLogined) {
				Intent attention = new Intent();
				attention.setClass(getActivity(), MyAttentionListActivity.class);
				startActivity(attention);
			} else {
				
				Intent login1 = new Intent(getActivity(), LoginActivity.class);
				login1.putExtra("activityName", this.getClass().getName());
				login1.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				startActivity(login1);
			}

			break;
		case R.id.no_sequence_merchants:
			unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
//			Builder dialog = new AlertDialog.Builder(getActivity());
//			dialog.setTitle("温馨提示").setMessage("亲，暂未开放，敬请期待。").setPositiveButton("确定", null);
//			dialog.show();
			Intent login1 = new Intent(getActivity(), NoQueueMerchantListActivity.class);
			login1.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivity(login1);
			
//			Intent login1 = new Intent(getActivity(), MerchantChatRoomsActivity.class);
////			login1.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//			startActivity(login1);
			break;
		case R.id.btn_get_number:
			unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			if (QHClientApplication.getInstance().isLogined) {
				
				if (categorys != null && !categorys.isEmpty()) {
					Intent intent = new Intent();
					ArrayList<CategoryData> categoryDatas = new ArrayList<CategoryData>();
					intent.putExtra("categoryType", categorys.get(0).categoryType);
					intent.putExtra("cateName", categorys.get(0).cateName);
					intent.putExtra("categoryCount", String.valueOf(categorys.get(0).count));
					CategoryData data = null;
					
					for (int i = 0; i < categorys.size(); i++) {
						data = new CategoryData();
						data.setCount(categorys.get(i).count);
						data.setCategoryType(categorys.get(i).categoryType);
						data.setCateName(categorys.get(i).cateName);
						categoryDatas.add(data);
					}
					
					Bundle mBundle = new Bundle();
					mBundle.putParcelableArrayList("categorys", categoryDatas);
					intent.putExtras(mBundle);
					
					intent.setClass(getActivity(), MerchantListActivity.class);
					startActivity(intent);
				}
				
				
			} else {
				
				Intent login3 = new Intent(getActivity(), LoginActivity.class);
				login3.putExtra("activityName", this.getClass().getName());
				login3.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				startActivity(login3);
			}

			break;
		case R.id.btn_chat_room:
			unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			if (QHClientApplication.getInstance().isLogined) {
				Intent attention = new Intent();
				attention.setClass(getActivity(), MerchantChatRoomsActivity.class);
				startActivity(attention);
			} else {
				
				Intent login2 = new Intent(getActivity(), LoginActivity.class);
				login2.putExtra("activityName", this.getClass().getName());
				login2.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				startActivity(login2);
			}

			break;
		case R.id.btn_choose_hard:
			unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			// http://localhost:9081/app/tuijian?cityCode=021&userX=11.011&userY=34.222
			String url = "app/tuijian?cityCode=" + QHClientApplication.getInstance().defaultCity.cityCode;
			AMapLocation location = QHClientApplication.getInstance().location;
			if (location != null) {
				url = url + "&userX=" + location.getLongitude() + "&userY=" + location.getLatitude();
			}
			
			final GetChooseHardMerchantTask task = new GetChooseHardMerchantTask(R.string.waitting, getActivity(), url);
			task.execute(new Runnable() {
				
				@Override
				public void run() {
					JsonPack jsonPack = task.jsonPack;
					Merchant merchant = ParseJson.getMerchant(jsonPack.getObj());
					if (null != merchant && StringUtils.isNotNull(merchant.id)) {
						Intent chooseHardIntent = new Intent();
						chooseHardIntent.putExtra("merchantId", merchant.id);
						chooseHardIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
						chooseHardIntent.setClass(getActivity(), MerchantDetailActivity.class);
						startActivity(chooseHardIntent);
					}
					else
					{
						Toast.makeText(getActivity(), "亲，暂时没得选哦！", Toast.LENGTH_SHORT).show();
						return;
					}
					
				}
			}, new Runnable() {
				
				@Override
				public void run() {
					Toast.makeText(getActivity(), "亲，暂时没得选哦！", Toast.LENGTH_SHORT).show();
					return;
				}
			});

			break;
		default:
			unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			break;
		}

	}
}
