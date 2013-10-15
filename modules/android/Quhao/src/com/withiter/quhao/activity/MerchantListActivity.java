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
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
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
public class MerchantListActivity extends AppStoreActivity {

	private String LOGTAG = MerchantListActivity.class.getName();
	protected ListView merchantsListView;
	private List<Merchant> merchants;
	private MerchantAdapter merchantAdapter;
	private final int UNLOCK_CLICK = 1000;
	private ProgressDialogUtil progressMerchants;
	private int page;
	private String categoryType;
	private String categoryTypeStr;
	private String categoryCount;
	private TextView categoryTypeView;
	private TextView categoryCountView;
	private boolean isFirst = true;

	private Handler merchantsUpdateHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 200) {
				super.handleMessage(msg);

				LinearLayout.LayoutParams merchantsParams = (LayoutParams) merchantsListView
						.getLayoutParams();

				// 设置自定义的layout

				merchantsListView.setLayoutParams(merchantsParams);
				merchantsListView.invalidate();
				merchantsListView.setVisibility(View.VISIBLE);
				// merchantsListView.addFocusables(merchants,
				// merchants.size()-9);

				// 默认isFirst是true.
				if (isFirst) {
					merchantAdapter = new MerchantAdapter(
							MerchantListActivity.this, merchantsListView,
							merchants);
					merchantsListView.setAdapter(merchantAdapter);
					isFirst = false;
				} else {
					merchantAdapter.merchants = merchants;
				}

				merchantAdapter.notifyDataSetChanged();
				merchantsListView
						.setOnItemClickListener(merchantItemClickListener);

				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			}

		}

	};

	private AdapterView.OnItemClickListener merchantItemClickListener = new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Merchant merchant = merchants.get(position);
			Intent intent = new Intent();
			intent.putExtra("merchantId", merchant.id);
			intent.setClass(MerchantListActivity.this,
					MerchantDetailActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.main_enter, R.anim.main_exit);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.merchants);
		super.onCreate(savedInstanceState);
		this.merchants = new ArrayList<Merchant>();
		this.page = getIntent().getIntExtra("page", 1);
		this.categoryType = getIntent().getStringExtra("categoryType");

		this.categoryTypeStr = getIntent().getStringExtra("categoryTypeStr");
		this.categoryTypeView = (TextView) findViewById(R.id.categoryType);
		this.categoryTypeView.setText(categoryTypeStr);

		this.categoryCount = getIntent().getStringExtra("categoryCount");
		this.categoryCountView = (TextView) findViewById(R.id.categoryCount);
		this.categoryCountView.setText("[共" + categoryCount + "家]");
		
		btnBack.setOnClickListener(goBack(this));
		initView();
	}

	private void initView() {
		merchantsListView = (ListView) findViewById(R.id.merchantsListView);

		merchantsListView.setNextFocusDownId(R.id.merchantsListView);
		merchantsListView.setOnScrollListener(merchantsListScrollListener);
		merchantsListView.setVisibility(View.GONE);

		getMerchants();
	}

	private void getMerchants() {
		if (isClick) {
			return;
		}
		isClick = true;

		progressMerchants = new ProgressDialogUtil(this, R.string.empty,
				R.string.querying, false);
		progressMerchants.showProgress();
		Thread merchantsThread = new Thread(merchantsRunnable);
		merchantsThread.start();
	}

	private Runnable merchantsRunnable = new Runnable() {
		@Override
		public void run() {
			try {
				QuhaoLog.v(LOGTAG, "get categorys data form server begin");
				String buf = CommonHTTPRequest.get("MerchantController/nextPage?page=" + page
						+ "&cateType=" + categoryType);
				if(StringUtils.isNull(buf))
				{
					unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				}
				else
				{
					if(null == merchants)
					{
						merchants = new ArrayList<Merchant>();
					}
					merchants.addAll(ParseJson.getMerchants(buf));

					merchantsUpdateHandler.obtainMessage(200, merchants)
							.sendToTarget();
				}
				
			} catch (Exception e) {
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				e.printStackTrace();
			} finally {
				progressMerchants.closeProgress();
			}
		}
	};
	private OnScrollListener merchantsListScrollListener = new OnScrollListener() {
		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {

		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
			if (view.getLastVisiblePosition() == totalItemCount - 1) {
				MerchantListActivity.this.page += 1;
				Thread merchantsThread = new Thread(merchantsRunnable);
				merchantsThread.start();
			}

		}
	};

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		return false;
	}

}
