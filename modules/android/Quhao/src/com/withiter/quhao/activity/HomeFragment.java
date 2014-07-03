package com.withiter.quhao.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.withiter.quhao.QHClientApplication;
import com.withiter.quhao.R;
import com.withiter.quhao.adapter.ActivityAdapter;
import com.withiter.quhao.adapter.MyPagerAdapter;
import com.withiter.quhao.task.GetActivitiesTask;
import com.withiter.quhao.task.GetChooseHardMerchantTask;
import com.withiter.quhao.task.JsonPack;
import com.withiter.quhao.task.TopMerchantsTask;
import com.withiter.quhao.util.ActivityUtil;
import com.withiter.quhao.util.QuhaoLog;
import com.withiter.quhao.util.StringUtils;
import com.withiter.quhao.util.tool.ParseJson;
import com.withiter.quhao.util.tool.QuhaoConstant;
import com.withiter.quhao.view.viewpager.AutoScrollViewPager;
import com.withiter.quhao.vo.ActivityVO;
import com.withiter.quhao.vo.Category;
import com.withiter.quhao.vo.Merchant;
import com.withiter.quhao.vo.TopMerchant;

public class HomeFragment extends Fragment implements OnClickListener {

	private static final int UNLOCK_CLICK = 1000;
	
	private String LOGTAG = HomeFragment.class.getName();

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
	private LinearLayout activityLayout;

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
		// 检查网络
		if (!ActivityUtil.isNetWorkAvailable(getActivity())) {
			Toast.makeText(getActivity(), R.string.network_error_info, Toast.LENGTH_SHORT).show();
		}
		
		if(contentView != null) {
			ViewGroup vg = (ViewGroup) contentView.getParent();
			vg.removeView(contentView);
			getActivity().registerReceiver(cityChangeReceiver, new IntentFilter(QuhaoConstant.ACTION_CITY_CHANGED));
			activityListView.setVisibility(View.VISIBLE);
			getTopMerchantsFromServerAndDisplay();
			getActivities();
			return contentView;
		}
		
		// 主页面layout
		contentView = inflater.inflate(R.layout.main_fragment_layout, container, false);
		
		activityLayout = (LinearLayout) contentView.findViewById(R.id.activity_layout);
		searchTextView = (Button) contentView.findViewById(R.id.edit_search);
		searchTextView.setOnClickListener(this);

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
		
		// 城市选择按钮
		cityBtn = (TextView) contentView.findViewById(R.id.city);
		cityBtn.setOnClickListener(this);
		
		cityBtn.setText(QHClientApplication.getInstance().defaultCity.cityName);
		activityListView.setVisibility(View.VISIBLE);
		
		getActivity().registerReceiver(cityChangeReceiver, new IntentFilter(QuhaoConstant.ACTION_CITY_CHANGED));
		getTopMerchantsFromServerAndDisplay();
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
			}
			
		}
	};
	
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
					
					DisplayImageOptions options = new DisplayImageOptions.Builder()
					.showImageOnLoading(R.drawable.no_logo)
					.showImageForEmptyUri(R.drawable.no_logo)
					.showImageOnFail(R.drawable.no_logo)
					.cacheInMemory(true)
					.cacheOnDisk(true)
					.considerExifParams(true)
					.displayer(new RoundedBitmapDisplayer(20))
					.build();
					ImageLoader.getInstance().displayImage(topMerchants.get(num).merchantImage, image, options, new AnimateFirstDisplayListener());
//					AsynImageLoader.getInstance().showImageAsyn(image, 0, topMerchants.get(num).merchantImage, R.drawable.no_logo);
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
						mViewPager.startAutoScroll(3000);
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
		QuhaoLog.i(LOGTAG, LOGTAG + " onResume");
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
			}
		});

	}

	private Handler activitiesUpdateHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 200) {
				super.handleMessage(msg);
				if (null == activityAdapter) {
					DisplayImageOptions options = new DisplayImageOptions.Builder()
					.showImageOnLoading(R.drawable.no_logo)
					.showImageForEmptyUri(R.drawable.no_logo)
					.showImageOnFail(R.drawable.no_logo)
					.cacheInMemory(true)
					.cacheOnDisk(true)
					.considerExifParams(true)
					.displayer(new RoundedBitmapDisplayer(20))
					.build();
					activityAdapter = new ActivityAdapter(getActivity(),activityListView,activityList,options,new AnimateFirstDisplayListener());
					activityListView.setAdapter(activityAdapter);
				}
				else
				{
					activityAdapter.Activities = activityList;
				}
				
				activityAdapter.notifyDataSetChanged();
				if(null != activityList && !activityList.isEmpty()) {
					activityLayout.setVisibility(View.VISIBLE);
					activityListView.setVisibility(View.VISIBLE);
					int totalHeight = 0;    
			        for (int i = 0, len = activityAdapter.getCount(); i < len; i++) { //listAdapter.getCount()返回数据项的数目    
				        View listItem = activityAdapter.getView(i, null, activityListView);    
				        listItem.measure(0, 0); //计算子项View 的宽高    
				        totalHeight += listItem.getMeasuredHeight(); //统计所有子项的总高度    
			        }
			            
			        android.view.ViewGroup.LayoutParams params = activityListView.getLayoutParams();   
			        params.height = totalHeight + (activityListView.getDividerHeight() * (activityListView.getCount() - 1));    
			        activityListView.setLayoutParams(params);
				}
				else {
					activityLayout.setVisibility(View.GONE);
					activityListView.setVisibility(View.GONE);
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
				activitiesUpdateHandler.obtainMessage(200, activityList).sendToTarget();

			}
		}, new Runnable() {

			@Override
			public void run() {
				if (null == activityList) {
					activityList = new ArrayList<ActivityVO>();
				}
				activityList.clear();
				activitiesUpdateHandler.obtainMessage(200, activityList).sendToTarget();
			}
		});

	}
	
	@Override
	public void onClick(View v) {

		if (isClick) {
			return;
		}
		isClick = true;

		switch (v.getId()) {
		// 我的关注事件按钮
		case R.id.my_attention:
			unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			if (QHClientApplication.getInstance().isLogined) {
				Intent attention = new Intent();
//				attention.setClass(getActivity(), MyAttentionListActivity.class);
				attention.setClass(getActivity(), ShareListActivity.class);
				startActivity(attention);
			} else {
				
				Intent login1 = new Intent(getActivity(), LoginActivity.class);
				login1.putExtra("activityName", this.getClass().getName());
				login1.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				startActivity(login1);
			}

			break;
		// 马上就吃按钮事件
		case R.id.no_sequence_merchants:
			unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			Intent login1 = new Intent(getActivity(), NoQueueMerchantListActivity.class);
			login1.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivity(login1);
			break;
		// 取号排队按钮事件
		case R.id.btn_get_number:
			unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
//			getCategoriesFromServerAndDisplay();
			Intent intent3 = new Intent();
			intent3.setClass(getActivity(), MerchantListActivity.class);
			startActivity(intent3);

			break;
		// 聊聊天吧按钮事件
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
			
		// 选择困难症按钮事件
		case R.id.btn_choose_hard:
			unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
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
					else {
						Toast.makeText(getActivity(), "亲，该城市暂未开通，请选择其他城市！", Toast.LENGTH_SHORT).show();
						return;
					}
					
				}
			}, new Runnable() {
				@Override
				public void run() {
					Toast.makeText(getActivity(), "亲，该城市暂未开通，请选择其他城市！", Toast.LENGTH_SHORT).show();
					return;
				}
			});

			break;
		case R.id.city:
			unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			Intent intent = new Intent();
			intent.setClass(getActivity(), CitySelectActivity.class);
			startActivity(intent);
			break;
		case R.id.edit_search:
			unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			Intent intent2 = new Intent(getActivity(), MerchantsSearchActivity.class);
			startActivity(intent2);
			break;
		default:
			unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			break;
		}

	}
	
	private static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

		static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

		@Override
		public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
			if (loadedImage != null) {
				ImageView imageView = (ImageView) view;
				boolean firstDisplay = !displayedImages.contains(imageUri);
				if (firstDisplay) {
					FadeInBitmapDisplayer.animate(imageView, 500);
					displayedImages.add(imageUri);
				}
			}
		}
	}
}
