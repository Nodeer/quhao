package com.withiter.quhao.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.withiter.quhao.QHClientApplication;
import com.withiter.quhao.R;
import com.withiter.quhao.adapter.CommentAccountAdapter;
import com.withiter.quhao.adapter.CreditAdapter;
import com.withiter.quhao.exception.NoResultFromHTTPRequestException;
import com.withiter.quhao.util.StringUtils;
import com.withiter.quhao.util.http.CommonHTTPRequest;
import com.withiter.quhao.util.tool.ParseJson;
import com.withiter.quhao.util.tool.ProgressDialogUtil;
import com.withiter.quhao.vo.Comment;
import com.withiter.quhao.vo.Credit;

public class CreditCostListActivity extends QuhaoBaseActivity implements OnItemClickListener{

	protected static boolean backClicked = false;
	private static String TAG = CreditCostListActivity.class.getName();
	private List<Credit> credits;
	private ListView creditsListView;
	private CreditAdapter creditAdapter;
	private int page;
	private boolean needToLoad = true;
	private boolean isFirstLoad = true;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.credit_cost_list_layout);
		super.onCreate(savedInstanceState);

		creditsListView = (ListView) this.findViewById(R.id.creditsListView);
		creditsListView.setOnItemClickListener(CreditCostListActivity.this);
		creditsListView.setOnScrollListener(creditScroller);
		btnBack.setOnClickListener(goBack(this, this.getClass().getName()));
		this.page = getIntent().getIntExtra("page", 1);
		initListView();
	}

	private void initListView() {
		progressDialogUtil = new ProgressDialogUtil(this, R.string.empty, R.string.waitting, false);
		progressDialogUtil.showProgress();
		Thread thread = new Thread(getCreditsRunnable);
		thread.start();
	}

	private Handler creditsUpdateHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 200) {
				super.handleMessage(msg);

				if (isFirstLoad) {

					creditAdapter = new CreditAdapter(CreditCostListActivity.this, creditsListView, credits);
					creditsListView.setAdapter(creditAdapter);
					isFirstLoad = false;
				} else {
					creditAdapter.credits = credits;
				}
				
				creditAdapter.notifyDataSetChanged();
				
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			}

		}

	};

	private OnScrollListener creditScroller = new OnScrollListener() {
		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {

		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
			// check hit the bottom of current loaded data
			if (firstVisibleItem + visibleItemCount == totalItemCount && totalItemCount > 0 && needToLoad) {
				CreditCostListActivity.this.page += 1;
				initListView();
			}
		}
	};
	
	private Runnable getCreditsRunnable = new Runnable() {
		@Override
		public void run() {
			try {
				String accountId = QHClientApplication.getInstance().accountInfo.accountId;
				String buf = CommonHTTPRequest.get("getCreditCost?accountId=" + accountId + "&page=" + page);
				if (StringUtils.isNull(buf) || "[]".equals(buf)) {
					needToLoad = false;
					unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
					throw new NoResultFromHTTPRequestException();
				} else {
					if (isFirstLoad || null == credits) {
						credits = new ArrayList<Credit>();
					}
					
					List<Credit> credits1 = ParseJson.getCredits(buf);
					if(credits1.size()<10)
					{
						needToLoad = false;
					}
					credits.addAll(credits1);
					creditsUpdateHandler.obtainMessage(200, credits).sendToTarget();
				}
			} catch (Exception e) {
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				e.printStackTrace();
			} finally {
				progressDialogUtil.closeProgress();
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
	protected void onPause() {
		super.onPause();
		Log.i(TAG, "backClicked: " + backClicked);
		if (backClicked) {
			overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		
		// 已经点过，直接返回
		if (isClick) {
			return;
		}

		// 设置已点击标志，避免快速重复点击
		isClick = true;
		// 解锁
		unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
		
		Credit credit = credits.get(position);
		if(StringUtils.isNotNull(credit.merchantId))
		{
			Intent intent = new Intent();
			intent.putExtra("merchantId", credit.merchantId);
			intent.setClass(CreditCostListActivity.this, MerchantDetailActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
		}
		
	}
}
