package com.withiter.quhao.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;

import com.withiter.quhao.QHClientApplication;
import com.withiter.quhao.R;
import com.withiter.quhao.adapter.MerchantAdapter;
import com.withiter.quhao.task.MyAttentionListTask;
import com.withiter.quhao.util.QuhaoLog;
import com.withiter.quhao.util.tool.ParseJson;
import com.withiter.quhao.view.refresh.PullToRefreshView;
import com.withiter.quhao.view.refresh.PullToRefreshView.OnFooterRefreshListener;
import com.withiter.quhao.view.refresh.PullToRefreshView.OnHeaderRefreshListener;
import com.withiter.quhao.vo.Merchant;

/**
 * 商家列表页面
 */
public class MyAttentionListActivity extends QuhaoBaseActivity implements OnHeaderRefreshListener,OnFooterRefreshListener{

	private String LOGTAG = MyAttentionListActivity.class.getName();
	protected ListView merchantsListView;
	private List<Merchant> merchants;
	private MerchantAdapter merchantAdapter;
	private final int UNLOCK_CLICK = 1000;
	private int page;
	private boolean isFirst = true;
	private boolean needToLoad = true;
	public static boolean backClicked = false;
	
	private PullToRefreshView mPullToRefreshView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.my_attention_list_layout);
		super.onCreate(savedInstanceState);

		this.merchants = new ArrayList<Merchant>();

		this.page = getIntent().getIntExtra("page", 1);
		QuhaoLog.i(LOGTAG, "init page is : " + this.page);

		btnBack.setOnClickListener(goBack(this, this.getClass().getName()));
		
		merchantsListView = (ListView) findViewById(R.id.merchantsListView);
		
		merchantsListView.setNextFocusDownId(R.id.merchantsListView);
		
		merchantsListView.setVisibility(View.GONE);
		merchantsListView.setOnItemClickListener(merchantItemClickListener);
		
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
					merchantAdapter = new MerchantAdapter(MyAttentionListActivity.this, merchantsListView, merchants);
					merchantsListView.setAdapter(merchantAdapter);
					isFirst = false;
				} else {
					merchantAdapter.merchants = merchants;
				}

				merchantAdapter.notifyDataSetChanged();
				mPullToRefreshView.onHeaderRefreshComplete();
				mPullToRefreshView.onFooterRefreshComplete();
				if (!needToLoad) 
				{
					mPullToRefreshView.setEnableFooterView(false);
				}
				else
				{
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
			intent.setClass(MyAttentionListActivity.this, MerchantDetailActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
		}
	};

	private void initView() {
		
		getMerchants();
	}

	private void getMerchants() {

		String url = "app/marked?aid=" + QHClientApplication.getInstance().accountInfo.accountId;
		final MyAttentionListTask task = new MyAttentionListTask(R.string.waitting, this, url);
		
		task.execute(new Runnable() {
			
			@Override
			public void run() {
				String buf = task.result;
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
		}, new Runnable() {
			
			@Override
			public void run() {
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				needToLoad = false;
				
			}
		});
		
	}

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
				MyAttentionListActivity.this.page += 1;
				
				getMerchants();
			}
		}, 1000);
	}

	@Override
	public void onHeaderRefresh(PullToRefreshView view) {
		mPullToRefreshView.postDelayed(new Runnable() {

			@Override
			public void run() {
				MyAttentionListActivity.this.page = 1;
				isFirst = true;
				needToLoad = true;
				
//				merchantsListView.setSelectionFromTop(0, 0);// 滑动到第一项
				MyAttentionListActivity.this.merchants = new ArrayList<Merchant>();
				getMerchants();
			}
		}, 1000);
		
	}

}
