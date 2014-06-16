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
import android.view.Window;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.withiter.quhao.QHClientApplication;
import com.withiter.quhao.R;
import com.withiter.quhao.adapter.MerchantAdapter;
import com.withiter.quhao.data.CategoryData;
import com.withiter.quhao.util.ActivityUtil;
import com.withiter.quhao.util.QuhaoLog;
import com.withiter.quhao.util.StringUtils;
import com.withiter.quhao.util.http.CommonHTTPRequest;
import com.withiter.quhao.util.tool.ParseJson;
import com.withiter.quhao.util.tool.ProgressDialogUtil;
import com.withiter.quhao.view.expandtab.ExpandTabView;
import com.withiter.quhao.view.expandtab.ViewLeft;
import com.withiter.quhao.view.expandtab.ViewRight;
import com.withiter.quhao.view.refresh.PullToRefreshView;
import com.withiter.quhao.view.refresh.PullToRefreshView.OnFooterRefreshListener;
import com.withiter.quhao.view.refresh.PullToRefreshView.OnHeaderRefreshListener;
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
	private ProgressDialogUtil progressMerchants;
	private int page;
	private String categoryType;
	private boolean isFirst = true;
	private boolean needToLoad = true;
	public static boolean backClicked = false;

	private PullToRefreshView mPullToRefreshView;
	
	private ExpandTabView expandTabView; 
	
	private ArrayList<View> mViewArray = new ArrayList<View>();
	private ViewLeft viewLeft;
	
	private ArrayList<CategoryData> categorys;
	
	private List<String> categoryTypes;
	
	private List<String> categoryNames;
	
	private ViewRight viewRight;
	
	private List<String> sortByValues;
	
	private List<String> sortByItems;
	
	private String defaultSortBy;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.merchants);
		super.onCreate(savedInstanceState);

		this.merchants = new ArrayList<Merchant>();

		this.page = getIntent().getIntExtra("page", 1);
		QuhaoLog.i(LOGTAG, "init page is : " + this.page);
		this.categoryType = getIntent().getStringExtra("categoryType");

		this.categorys = getIntent().getParcelableArrayListExtra("categorys");
		
		btnBack.setOnClickListener(goBack(this, this.getClass().getName()));

		this.findViewById(R.id.loadingbar).setVisibility(View.VISIBLE);
		this.findViewById(R.id.serverdata).setVisibility(View.GONE);
		
		mPullToRefreshView = (PullToRefreshView) this.findViewById(R.id.main_pull_refresh_view);
		mPullToRefreshView.setOnHeaderRefreshListener(this);
		mPullToRefreshView.setOnFooterRefreshListener(this);
		mPullToRefreshView.setEnableFooterView(true);
		initView();
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
					merchantAdapter = new MerchantAdapter(MerchantListActivity.this, merchantsListView, merchants);
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
		
		initExpandView();
		
		getMerchants();
	}

	private void initExpandView() {

		categoryNames = new ArrayList<String>();
		categoryTypes = new ArrayList<String>();
		categoryNames.add("默认排序");
		categoryTypes.add("-1");
		if (categorys!=null && !categorys.isEmpty()) {
			for (int i = 0; i < categorys.size(); i++) {
				categoryNames.add(categorys.get(i).getCateName());
				categoryTypes.add(categorys.get(i).getCategoryType());
			}
		}
		
		if (StringUtils.isNull(categoryType)) {
			categoryType = "benbangcai";
		}
		categoryType = "";
		expandTabView = (ExpandTabView) this.findViewById(R.id.expandtab_view);
		viewLeft = new ViewLeft(this, categoryNames, categoryTypes, categoryType);
		viewLeft.setShowText("菜系");
//		viewLeft.setBackgroundResource(R.drawable.expand_tab_bg);
		sortByItems = new ArrayList<String>();
		sortByItems.add("默认排序");
		sortByItems.add("按评分排序");
		sortByItems.add("按人气排序");
		
		sortByValues = new ArrayList<String>();
		sortByValues.add("-1");
		sortByValues.add("grade");
		sortByValues.add("markedCount");
		
		if (StringUtils.isNull(defaultSortBy)) {
			defaultSortBy = "-1";
		}
		defaultSortBy = "";
		viewRight = new ViewRight(this, sortByItems, sortByValues, defaultSortBy);
		viewRight.setShowText("排序");
		
		mViewArray = new ArrayList<View>();
		mViewArray.add(viewLeft);
		mViewArray.add(viewRight);
		
		ArrayList<String> mTextArray = new ArrayList<String>();
		mTextArray.add("口味");
		mTextArray.add("排序");
		expandTabView.setValue(mTextArray, mViewArray);
		expandTabView.setTitle(viewLeft.getShowText(), 0);
		expandTabView.setTitle(viewRight.getShowText(), 1);

		viewLeft.setOnSelectListener(new ViewLeft.OnSelectListener() {
			@Override
			public void getValue(String distance, String showText) {
				onRefresh(viewLeft, showText);
			}
		});
		
		viewRight.setOnSelectListener(new ViewRight.OnSelectListener() {
			@Override
			public void getValue(String distance, String showText) {
				onRefresh(viewRight, showText);
			}
		});
	}
	
	private void onRefresh(View view, String showText) {
		
		expandTabView.onPressBack();
		int position = getPositon(view);
		if (position >= 0 && !expandTabView.getTitle(position).equals(showText)) {
			expandTabView.setTitle(showText, position);
		}
		
		if (0 == position) {
			categoryType = categoryTypes.get(categoryNames.indexOf(showText));
			if ("-1".equals(categoryType)) {
				categoryType = "";
			}
//			for (int i = 0; i < categoryNames.size(); i++) {
//				
//			}
		}
		else if(1 == position)
		{
			defaultSortBy = sortByValues.get(sortByItems.indexOf(showText));
			if ("-1".equals(defaultSortBy)) {
				defaultSortBy = "";
			}
		}
		
		MerchantListActivity.this.page = 1;
		isFirst = true;
		needToLoad = true;

		// merchantsListView.setSelectionFromTop(0, 0);// 滑动到第一项
		MerchantListActivity.this.merchants = new ArrayList<Merchant>();
		
		getMerchants();

	}
	
	@Override
	public void onBackPressed() {
		
		if (!expandTabView.onPressBack()) {
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

		progressMerchants = new ProgressDialogUtil(this, R.string.empty, R.string.querying, false);
		progressMerchants.showProgress();
		Thread merchantsThread = new Thread(merchantsRunnable);
		merchantsThread.start();
	}

	private Runnable merchantsRunnable = new Runnable() {
		@Override
		public void run() {
			try {
				Looper.prepare();
				QuhaoLog.d(LOGTAG, "get categorys data form server begin");
				String url = "nextPage?page=" + page + "&cateType=" + categoryType + "&cityCode=" + QHClientApplication.getInstance().defaultCity.cityCode + "&sortBy=" + defaultSortBy;
				AMapLocation location = QHClientApplication.getInstance().location;
				if (location != null) {
					url = url + "&userX=" + location.getLongitude() + "&userY=" + location.getLatitude();
				} else {
					url = url + "&userX=0.000000&userY=0.000000";
				}
				QuhaoLog.d(LOGTAG, "the request url is : " + url);
				if (!ActivityUtil.isNetWorkAvailable(getApplicationContext())) {
					Toast.makeText(getApplicationContext(), R.string.network_error_info, Toast.LENGTH_SHORT).show();
					merchants = new ArrayList<Merchant>();
					needToLoad = false;
					merchantsUpdateHandler.obtainMessage(200, merchants).sendToTarget();
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
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				e.printStackTrace();
			} finally {
				progressMerchants.closeProgress();
				Looper.loop();
			}
		}
	};

	@Override
	public void onClick(View v) {

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
		mPullToRefreshView.postDelayed(new Runnable() {

			@Override
			public void run() {
				MerchantListActivity.this.page += 1;
				Thread merchantsThread = new Thread(merchantsRunnable);
				merchantsThread.start();
			}
		}, 1000);
	}

	@Override
	public void onHeaderRefresh(PullToRefreshView view) {
		mPullToRefreshView.postDelayed(new Runnable() {

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
		}, 1000);

	}

}
