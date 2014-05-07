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
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.withiter.quhao.QHClientApplication;
import com.withiter.quhao.R;
import com.withiter.quhao.adapter.MerchantAdapter;
import com.withiter.quhao.util.QuhaoLog;
import com.withiter.quhao.util.StringUtils;
import com.withiter.quhao.util.http.CommonHTTPRequest;
import com.withiter.quhao.util.tool.ParseJson;
import com.withiter.quhao.util.tool.ProgressDialogUtil;
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
	private String cateName;
	private String categoryCount;
	private TextView categoryTypeTitle;
	private boolean isFirst = true;
	private boolean needToLoad = true;
	public static boolean backClicked = false;

	private PullToRefreshView mPullToRefreshView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.merchants);
		super.onCreate(savedInstanceState);

		this.merchants = new ArrayList<Merchant>();

		this.page = getIntent().getIntExtra("page", 1);
		QuhaoLog.i(LOGTAG, "init page is : " + this.page);
		this.categoryType = getIntent().getStringExtra("categoryType");
		this.cateName = getIntent().getStringExtra("cateName");

		this.categoryTypeTitle = (TextView) findViewById(R.id.categoryTypeTitle);
		this.categoryTypeTitle.setText(cateName);

		btnBack.setOnClickListener(goBack(this, this.getClass().getName()));

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
			overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
		}
	};

	private void initView() {
		merchantsListView = (ListView) findViewById(R.id.merchantsListView);
		merchantsListView.setNextFocusDownId(R.id.merchantsListView);
		merchantsListView.setVisibility(View.GONE);
		merchantsListView.setOnItemClickListener(merchantItemClickListener);
		getMerchants();
	}

	private void getMerchants() {
		if (isClick) {
			return;
		}
		isClick = true;

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
				String url = "MerchantController/nextPage?page=" + page + "&cateType=" + categoryType + "&cityCode=" + QHClientApplication.getInstance().defaultCity.cityCode;
				AMapLocation location = QHClientApplication.getInstance().location;
				if (location != null) {
					url = url + "&userX=" + location.getLatitude() + "&userY=" + location.getLongitude();
				} else {
					url = url + "&userX=0.000000&userY=0.000000";
				}
				QuhaoLog.d(LOGTAG, "the request url is : " + url);
				String buf = CommonHTTPRequest.get(url);
				if (StringUtils.isNull(buf) || "[]".endsWith(buf)) {
					unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
					needToLoad = false;
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

	/*
	 * @Override public void onScrollStateChanged(AbsListView view, int
	 * scrollState) {
	 * 
	 * if(scrollState == OnScrollListener.SCROLL_STATE_IDLE && lastVisibleIndex
	 * == merchantAdapter.getCount()) { pg.setVisibility(View.VISIBLE);
	 * bt.setVisibility(View.GONE); MerchantListActivity.this.page += 1; Thread
	 * merchantsThread = new Thread(merchantsRunnable); merchantsThread.start();
	 * }
	 * 
	 * }
	 * 
	 * @Override public void onScroll(AbsListView view, int firstVisibleItem,
	 * int visibleItemCount, int totalItemCount) { // check hit the bottom of
	 * current loaded data
	 * 
	 * lastVisibleIndex = firstVisibleItem + visibleItemCount -1;
	 * if(!needToLoad) { merchantsListView.removeFooterView(moreView);
	 * //Toast.makeText(MerchantListActivity.this, "the data load completely",
	 * Toast.LENGTH_LONG).show();
	 * 
	 * }
	 * 
	 * // if (firstVisibleItem + visibleItemCount == totalItemCount &&
	 * totalItemCount > 0 && needToLoad) { // MerchantListActivity.this.page +=
	 * 1; // Thread merchantsThread = new Thread(merchantsRunnable); //
	 * merchantsThread.start(); // } }
	 */
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
		QuhaoLog.i(LOGTAG, LOGTAG + " on pause");
		if (backClicked) {
			overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
		}
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
				getMerchants();
			}
		}, 1000);

	}

}
