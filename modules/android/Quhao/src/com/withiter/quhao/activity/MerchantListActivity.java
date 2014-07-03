package com.withiter.quhao.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.withiter.quhao.QHClientApplication;
import com.withiter.quhao.R;
import com.withiter.quhao.adapter.MerchantAdapter;
import com.withiter.quhao.task.AllCategoriesTask;
import com.withiter.quhao.util.ActivityUtil;
import com.withiter.quhao.util.QuhaoLog;
import com.withiter.quhao.util.StringUtils;
import com.withiter.quhao.util.http.CommonHTTPRequest;
import com.withiter.quhao.util.tool.ParseJson;
import com.withiter.quhao.view.expandtab.ViewLeft;
import com.withiter.quhao.view.expandtab.ViewRight;
import com.withiter.quhao.view.refresh.PullToRefreshView;
import com.withiter.quhao.view.refresh.PullToRefreshView.OnFooterRefreshListener;
import com.withiter.quhao.view.refresh.PullToRefreshView.OnHeaderRefreshListener;
import com.withiter.quhao.vo.Category;
import com.withiter.quhao.vo.Merchant;

/**
 * 商家列表页面
 */
public class MerchantListActivity extends QuhaoBaseActivity implements OnHeaderRefreshListener, OnFooterRefreshListener {

	private String LOGTAG = MerchantListActivity.class.getName();
	protected ListView merchantsListView;
	private List<Merchant> merchants;
	private MerchantAdapter merchantAdapter;
	private final int UNLOCK_CLICK = 1000;
	private int page;
	private String categoryType = "-1";
	private boolean isFirst = true;
	private boolean needToLoad = true;
	public static boolean backClicked = false;
	private PullToRefreshView mPullToRefreshView;
//	private ExpandTabView expandTabView; 
	private ArrayList<View> mViewArray = new ArrayList<View>();
	private ViewLeft viewLeft;
	private ViewRight viewRight;
//	private ArrayList<CategoryData> categorys;
	private List<String> categoryTypes;
	private List<String> categoryNames;
	private List<String> sortByValues;
	private List<String> sortByItems;
	private String defaultSortBy = "-1";
	
	private List<Category> categoryList;
	
	private LinearLayout categoryLayout;
	
	private LinearLayout queueLayout;
	
	private TextView categoryNameView;
	
	private TextView queueNameView;
	
	private PopupWindow popupWindow1;
	
	private PopupWindow popupWindow2;
	
	private int displayWidth;
	private int displayHeight;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.merchants);
		super.onCreate(savedInstanceState);
		this.merchants = new ArrayList<Merchant>();
		this.page = getIntent().getIntExtra("page", 1);
		QuhaoLog.i(LOGTAG, "init page is : " + this.page);
//		this.categoryType = getIntent().getStringExtra("categoryType");
//		this.categorys = getIntent().getParcelableArrayListExtra("categorys");
		btnBack.setOnClickListener(goBack(this, this.getClass().getName()));
		this.findViewById(R.id.loadingbar).setVisibility(View.VISIBLE);
		this.findViewById(R.id.serverdata).setVisibility(View.GONE);
		mPullToRefreshView = (PullToRefreshView) this.findViewById(R.id.main_pull_refresh_view);
		mPullToRefreshView.setOnHeaderRefreshListener(this);
		mPullToRefreshView.setOnFooterRefreshListener(this);
		mPullToRefreshView.setEnableFooterView(true);
		
		categoryLayout = (LinearLayout) this.findViewById(R.id.category_layout);
		queueLayout = (LinearLayout) this.findViewById(R.id.queue_layout);
		categoryNameView = (TextView) this.findViewById(R.id.categoryName);
		queueNameView = (TextView) this.findViewById(R.id.queueName);
		categoryLayout.setOnClickListener(this);
		queueLayout.setOnClickListener(this);
		displayWidth = this.getWindowManager().getDefaultDisplay().getWidth();
		displayHeight = this.getWindowManager().getDefaultDisplay().getHeight();
		
		getCategoriesFromServerAndDisplay();
		initView();
	}
	
	private Handler categorysUpdateHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 200) {
				super.handleMessage(msg);
				if (categoryList != null && !categoryList.isEmpty()) {
					
					initExpandView();
				}
				else {
					Toast.makeText(MerchantListActivity.this, "亲，该城市暂未开通，请选择其他城市。", Toast.LENGTH_SHORT).show();
				}
				
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			}
		}
	};
	
	/**
	 * get all categories from server and display them
	 */
	public void getCategoriesFromServerAndDisplay() {
		
		if (!ActivityUtil.isNetWorkAvailable(getApplicationContext())) {
//			Toast.makeText(getApplicationContext(), R.string.network_error_info, Toast.LENGTH_SHORT).show();
//			merchants = new ArrayList<Merchant>();
			queryErrorHandler.sendEmptyMessage(200);
			return;
		}
		
		final AllCategoriesTask task = new AllCategoriesTask(0, MerchantListActivity.this, "allCategories?cityCode=" + QHClientApplication.getInstance().defaultCity.cityCode);
		task.execute(new Runnable() {
			@Override
			public void run() {
				String result = task.result;
				if (null == categoryList) {
					categoryList = new ArrayList<Category>();
				}
				categoryList.clear();
				categoryList.addAll(ParseJson.getCategorys(result));
				QHClientApplication.getInstance().categorys = categoryList;
				categorysUpdateHandler.obtainMessage(200, categoryList).sendToTarget();
			}
		}, new Runnable() {

			@Override
			public void run() {
				String result = task.result;
				if (null == categoryList) {
					categoryList = new ArrayList<Category>();
				}
				categoryList.clear();
				categoryList.addAll(ParseJson.getCategorys(result));
				categorysUpdateHandler.obtainMessage(200, categoryList).sendToTarget();
			}
		});

	}

	private Handler merchantsUpdateHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 200) {
				super.handleMessage(msg);
				LinearLayout.LayoutParams merchantsParams = (LayoutParams) merchantsListView.getLayoutParams();

				// 设置自定义的layout
				merchantsListView.setLayoutParams(merchantsParams);
				merchantsListView.invalidate();
				merchantsListView.setVisibility(View.VISIBLE);

				// 默认isFirst是true.
				if (isFirst) {
					
					DisplayImageOptions options = new DisplayImageOptions.Builder()
					.showImageOnLoading(R.drawable.no_logo)
					.showImageForEmptyUri(R.drawable.no_logo)
					.showImageOnFail(R.drawable.no_logo)
					.cacheInMemory(true)
					.cacheOnDisk(true)
					.considerExifParams(true)
					.displayer(new RoundedBitmapDisplayer(20))
					.build();
					merchantAdapter = new MerchantAdapter(MerchantListActivity.this, merchantsListView, merchants,options,animateFirstListener);
					merchantsListView.setAdapter(merchantAdapter);
					isFirst = false;
				} else {
					merchantAdapter.merchants = merchants;
				}

				merchantAdapter.notifyDataSetChanged();
				findViewById(R.id.loadingbar).setVisibility(View.GONE);
				findViewById(R.id.serverdata).setVisibility(View.VISIBLE);
				
				if (null == merchants ||merchants.isEmpty()) {
					Toast.makeText(MerchantListActivity.this, R.string.no_result_found, Toast.LENGTH_SHORT).show();
				}
				
				mPullToRefreshView.onHeaderRefreshComplete();
				mPullToRefreshView.onFooterRefreshComplete();
				if (!needToLoad) {
					mPullToRefreshView.setEnableFooterView(false);
				} else {
					mPullToRefreshView.setEnableFooterView(true);
				}
				
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			}

		}

	};

	private AdapterView.OnItemClickListener merchantItemClickListener = new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			Merchant merchant = merchants.get(position);
			Intent intent = new Intent();
			intent.putExtra("merchantId", merchant.id);
			intent.setClass(MerchantListActivity.this, MerchantDetailActivity.class);
			startActivity(intent);
		}
	};

	private void initView() {
		merchantsListView = (ListView) findViewById(R.id.merchantsListView);
		merchantsListView.setNextFocusDownId(R.id.merchantsListView);
		merchantsListView.setVisibility(View.GONE);
		merchantsListView.setOnItemClickListener(merchantItemClickListener);
		
		getMerchants();
	}

	private void initExpandView() {

		categoryNames = new ArrayList<String>();
		categoryTypes = new ArrayList<String>();
		categoryNames.add("全部分类");
		categoryTypes.add("-1");
		if (categoryList!=null && !categoryList.isEmpty()) {
			for (int i = 0; i < categoryList.size(); i++) {
				categoryNames.add(categoryList.get(i).cateName);
				categoryTypes.add(categoryList.get(i).categoryType);
			}
		}
		
		if (StringUtils.isNull(categoryType) || "-1".equals(categoryType)) {
			categoryType = "-1";
			categoryNameView.setText("全部分类");
		}
		
		viewLeft = new ViewLeft(this, categoryNames, categoryTypes, categoryType);
		sortByItems = new ArrayList<String>();
		sortByItems.add("默认排序");
		sortByItems.add("按评分排序");
		sortByItems.add("按人气排序");
		
		sortByValues = new ArrayList<String>();
		sortByValues.add("-1");
		sortByValues.add("grade");
		sortByValues.add("markedCount");
		
		
		if (StringUtils.isNull(defaultSortBy) || "-1".equals(defaultSortBy)) {
			defaultSortBy = "-1";
			queueNameView.setText("默认排序");
		}
		viewRight = new ViewRight(this, sortByItems, sortByValues, defaultSortBy);
		
		mViewArray = new ArrayList<View>();
		
		final RelativeLayout viewLeftLayout = new RelativeLayout(this);
		int maxHeight = (int) (displayHeight * 0.5);
		RelativeLayout.LayoutParams viewLeftParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, maxHeight);
		viewLeftParams.leftMargin = 10;
		viewLeftParams.rightMargin = 10;
		viewLeftLayout.addView(viewLeft, viewLeftParams);
		if(viewLeftLayout.getParent()!=null) {
			ViewGroup vg = (ViewGroup) viewLeftLayout.getParent();
			vg.removeView(viewLeftLayout);
		}
		viewLeftLayout.setBackgroundColor(this.getResources().getColor(R.color.popup_main_background));
		viewLeftLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				onPressBack();
			}
		});
		mViewArray.add(viewLeftLayout);
		
		final RelativeLayout viewRightLayout = new RelativeLayout(this);
		RelativeLayout.LayoutParams viewRightLP = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, maxHeight);
		viewRightLP.leftMargin = 10;
		viewRightLP.rightMargin = 10;
		viewRightLayout.addView(viewRight, viewRightLP);
		if(viewRightLayout.getParent()!=null) {
			ViewGroup vg = (ViewGroup) viewRightLayout.getParent();
			vg.removeView(viewRightLayout);
		}
		viewRightLayout.setBackgroundColor(this.getResources().getColor(R.color.popup_main_background));
		viewRightLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				onPressBack();
			}
		});
		mViewArray.add(viewRightLayout);
		
		viewLeft.setOnSelectListener(new ViewLeft.OnSelectListener() {
			@Override
			public void getValue(String distance, String showText) {
				onRefresh(0, showText);
			}
		});
		
		viewRight.setOnSelectListener(new ViewRight.OnSelectListener() {
			@Override
			public void getValue(String distance, String showText) {
				onRefresh(1, showText);
			}
		});
	}
	
	private void onRefresh(int position, String showText) {
		
		if (position >= 0 && position == 0) {
			categoryNameView.setText(showText);
		}
		
		if (position >= 0 && position == 1) {
			queueNameView.setText(showText);
		}

		if (0 == position) {
			categoryType = categoryTypes.get(categoryNames.indexOf(showText));
			if (null != popupWindow1 && popupWindow1.isShowing()) {
				popupWindow1.dismiss();
			}
		}
		else if(1 == position) {
			if (null != popupWindow2 && popupWindow2.isShowing()) {
				popupWindow2.dismiss();
			}
			
			defaultSortBy = sortByValues.get(sortByItems.indexOf(showText));
		}
		
		MerchantListActivity.this.page = 1;
		isFirst = true;
		needToLoad = true;

		// merchantsListView.setSelectionFromTop(0, 0);// 滑动到第一项
		MerchantListActivity.this.merchants = new ArrayList<Merchant>();
		mPullToRefreshView.headerRefreshing();
//		getMerchants();

	}
	
	private Handler queryErrorHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 200) {
				super.handleMessage(msg);
				needToLoad = false;
				findViewById(R.id.loadingbar).setVisibility(View.GONE);
				findViewById(R.id.serverdata).setVisibility(View.VISIBLE);
				mPullToRefreshView.onHeaderRefreshComplete();
				mPullToRefreshView.onFooterRefreshComplete();
				mPullToRefreshView.setEnableFooterView(false);
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			}

		}

	};
	
	@Override
	public void onBackPressed() {
		
		if (null != popupWindow1 && popupWindow1.isShowing()) {
			popupWindow1.dismiss();
			return;
		}
		else if(null != popupWindow2 && popupWindow2.isShowing())
		{
			popupWindow2.dismiss();
			return;
		}
		else
		{
			finish();
		}
	}
	
	private int getPositon(View tView) {
		for (int i = 0; i < mViewArray.size(); i++) {
			if (mViewArray.get(i) == tView) {
				return i;
			}
		}
		return -1;
	}

	private void getMerchants() {

		Thread merchantsThread = new Thread(merchantsRunnable);
		merchantsThread.start();
	}

	private Runnable merchantsRunnable = new Runnable() {
		@Override
		public void run() {
			try {
				Looper.prepare();
				String type = categoryType;
				QuhaoLog.d(LOGTAG, "get categorys data form server begin");
				if (null == type || "-1".equals(type)) {
					type = "";
				}
				String sortBy = defaultSortBy;
				if (null == sortBy || "-1".equals(sortBy)) {
					sortBy = "";
				}
				
				String url = "nextPage?page=" + page + "&cateType=" + type + "&cityCode=" + QHClientApplication.getInstance().defaultCity.cityCode + "&sortBy=" + sortBy;
				AMapLocation location = QHClientApplication.getInstance().location;
				if (location != null) {
					url = url + "&userX=" + location.getLongitude() + "&userY=" + location.getLatitude();
				} else {
					url = url + "&userX=0.000000&userY=0.000000";
				}
				QuhaoLog.d(LOGTAG, "the request url is : " + url);
				if (!ActivityUtil.isNetWorkAvailable(getApplicationContext())) {
					Toast.makeText(getApplicationContext(), R.string.network_error_info, Toast.LENGTH_SHORT).show();
//					merchants = new ArrayList<Merchant>();
					queryErrorHandler.sendEmptyMessage(200);
					return;
				}
				
				String buf = CommonHTTPRequest.get(url);
				if (StringUtils.isNull(buf) || "[]".endsWith(buf)) {
					if (null == merchants) {
						merchants = new ArrayList<Merchant>();
					}
					List<Merchant> mers = ParseJson.getMerchants(buf);
					if (mers.size() < 10) {
						needToLoad = false;
					}
					merchants.addAll(mers);
					merchantsUpdateHandler.obtainMessage(200, merchants).sendToTarget();
				} else {
					if (null == merchants) {
						merchants = new ArrayList<Merchant>();
					}
					List<Merchant> mers = ParseJson.getMerchants(buf);
					if (mers.size() < 10) {
						needToLoad = false;
					}
					merchants.addAll(mers);
					merchantsUpdateHandler.obtainMessage(200, merchants).sendToTarget();
				}

			} catch (Exception e) {
				queryErrorHandler.sendEmptyMessage(200);
			} finally {
				Looper.loop();
			}
		}
	};

	@Override
	public void onClick(View v) {

		// 已经点过，直接返回
		if (isClick) {
			return;
		}

		// 设置已点击标志，避免快速重复点击
		isClick = true;
		
		switch (v.getId()) {
		case R.id.category_layout:
			if (mViewArray == null || mViewArray.isEmpty()) {
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 0);
				return;
			}
			
			if (popupWindow2 != null && popupWindow2.isShowing()) {
				popupWindow2.dismiss();
			}
			View view = mViewArray.get(0);
			if (popupWindow1 == null) {
				
				popupWindow1 = new PopupWindow(view, displayWidth, displayHeight);
				popupWindow1.setAnimationStyle(R.style.PopupWindowAnimation);
				popupWindow1.setFocusable(false);
				popupWindow1.setOutsideTouchable(true);
			}
			
			if (!popupWindow1.isShowing()) {
				popupWindow1.showAsDropDown(categoryLayout);
//				showPopup(selectPosition);
			} else {
//				popupWindow.setOnDismissListener(this);
				popupWindow1.dismiss();
//				popupWindow.
//				hideView();
			}
			
			unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 0);
			break;
		case R.id.queue_layout:
			if (mViewArray == null || mViewArray.isEmpty()) {
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 0);
				return;
			}
			if (popupWindow1 != null && popupWindow1.isShowing()) {
				popupWindow1.dismiss();
			}
			
			View view2 = mViewArray.get(1);
			if (popupWindow2 == null) {
				
				popupWindow2 = new PopupWindow(view2, displayWidth, displayHeight);
				popupWindow2.setAnimationStyle(R.style.PopupWindowAnimation);
				popupWindow2.setFocusable(false);
				popupWindow2.setOutsideTouchable(true);
			}
			
			if (!popupWindow2.isShowing()) {
				popupWindow2.showAsDropDown(queueLayout);
//				showPopup(selectPosition);
			} else {
//				popupWindow.setOnDismissListener(this);
				popupWindow2.dismiss();
//				popupWindow.
//				hideView();
			}
			
			unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 0);
			break;
		default:
			unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			break;
		}
	}

	/**
	 * 如果菜单成展开状态，则让菜单收回去
	 */
	public boolean onPressBack() {
		if (popupWindow1 != null && popupWindow1.isShowing()) {
			popupWindow1.dismiss();
			return true;
		}
		else if(popupWindow2 != null && popupWindow2.isShowing())
		{
			popupWindow2.dismiss();
			return true;
		}else {
			return false;
		}

	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return false;
	}

	@Override
	protected void onResume() {
		backClicked = false;
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
	}
	
	@Override
	public void onFooterRefresh(PullToRefreshView view) {
		mPullToRefreshView.post(new Runnable() {

			@Override
			public void run() {
				MerchantListActivity.this.page += 1;
				Thread merchantsThread = new Thread(merchantsRunnable);
				merchantsThread.start();
			}
		});
	}

	@Override
	public void onHeaderRefresh(PullToRefreshView view) {
		mPullToRefreshView.post(new Runnable() {

			@Override
			public void run() {
				MerchantListActivity.this.page = 1;
				isFirst = true;
				needToLoad = true;

				// merchantsListView.setSelectionFromTop(0, 0);// 滑动到第一项
				MerchantListActivity.this.merchants = new ArrayList<Merchant>();
				Thread merchantsThread = new Thread(merchantsRunnable);
				merchantsThread.start();
			}
		});

	}

}
