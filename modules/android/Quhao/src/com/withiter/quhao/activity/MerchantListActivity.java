package com.withiter.quhao.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.withiter.quhao.R;
import com.withiter.quhao.adapter.MerchantAdapter;
import com.withiter.quhao.util.QuhaoLog;
import com.withiter.quhao.util.StringUtils;
import com.withiter.quhao.util.http.CommonHTTPRequest;
import com.withiter.quhao.util.tool.ParseJson;
import com.withiter.quhao.util.tool.ProgressDialogUtil;
import com.withiter.quhao.vo.Merchant;

/**
 * 商家列表页面
 */
public class MerchantListActivity extends QuhaoBaseActivity implements OnScrollListener{

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
	
	private View moreView;
	
	private Button bt;
	
	private ProgressBar pg;
	
	private int lastVisibleIndex;

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
		this.categoryCount = getIntent().getStringExtra("categoryCount");

		this.categoryTypeTitle = (TextView) findViewById(R.id.categoryTypeTitle);
		this.categoryTypeTitle.setText(cateName + "[" + categoryCount + "家]");
		
		btnBack.setOnClickListener(goBack(this, this.getClass().getName()));
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
				
				merchantsListView.setOnScrollListener(MerchantListActivity.this);
				bt.setVisibility(View.VISIBLE);
				pg.setVisibility(View.GONE);

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
		
		moreView = getLayoutInflater().inflate(R.layout.moredata, null);
		bt = (Button) moreView.findViewById(R.id.bt_load);
		pg = (ProgressBar) moreView.findViewById(R.id.pg);
		
		merchantsListView = (ListView) findViewById(R.id.merchantsListView);
		
		merchantsListView.addFooterView(moreView);
		merchantsListView.setNextFocusDownId(R.id.merchantsListView);
		
		merchantsListView.setVisibility(View.GONE);
		merchantsListView.setOnItemClickListener(merchantItemClickListener);
		
		bt.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				pg.setVisibility(View.VISIBLE);
				bt.setVisibility(View.GONE);
				MerchantListActivity.this.page += 1;
				Thread merchantsThread = new Thread(merchantsRunnable);
				merchantsThread.start();
			}
		});
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
				QuhaoLog.v(LOGTAG, "get categorys data form server begin");
				String url = "MerchantController/nextPage?page=" + page + "&cateType=" + categoryType;
				QuhaoLog.i(LOGTAG, "the request url is : " + url);
				String buf = CommonHTTPRequest.get(url);
				if (StringUtils.isNull(buf) || "[]".endsWith(buf)) {
					unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
					needToLoad = false;
				} else {
					if (null == merchants) {
						merchants = new ArrayList<Merchant>();
					}
					List<Merchant> mers = ParseJson.getMerchants(buf);
					if(mers.size()<10)
					{
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
			}
		}
	};
	
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		
		if(scrollState == OnScrollListener.SCROLL_STATE_IDLE
				&& lastVisibleIndex == merchantAdapter.getCount())
		{
			pg.setVisibility(View.VISIBLE);
			bt.setVisibility(View.GONE);
			MerchantListActivity.this.page += 1;
			Thread merchantsThread = new Thread(merchantsRunnable);
			merchantsThread.start();
		}
		
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		// check hit the bottom of current loaded data
		
		lastVisibleIndex = firstVisibleItem + visibleItemCount -1;
		if(!needToLoad)
		{
			merchantsListView.removeFooterView(moreView);
			//Toast.makeText(MerchantListActivity.this, "the data load completely", Toast.LENGTH_LONG).show();
			
		}
		
//		if (firstVisibleItem + visibleItemCount == totalItemCount && totalItemCount > 0 && needToLoad) {
//			MerchantListActivity.this.page += 1;
//			Thread merchantsThread = new Thread(merchantsRunnable);
//			merchantsThread.start();
//		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
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

}
