package com.withiter.quhao.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
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
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.withiter.quhao.QHClientApplication;
import com.withiter.quhao.R;
import com.withiter.quhao.adapter.CreditAdapter;
import com.withiter.quhao.exception.NoResultFromHTTPRequestException;
import com.withiter.quhao.util.StringUtils;
import com.withiter.quhao.util.http.CommonHTTPRequest;
import com.withiter.quhao.util.tool.ParseJson;
import com.withiter.quhao.util.tool.ProgressDialogUtil;
import com.withiter.quhao.vo.Credit;

public class CreditCostListActivity extends QuhaoBaseActivity implements OnItemClickListener,OnScrollListener{

	protected static boolean backClicked = false;
	private static String TAG = CreditCostListActivity.class.getName();
	private List<Credit> credits;
	private ListView creditsListView;
	private CreditAdapter creditAdapter;
	private int page;
	private boolean needToLoad = true;
	private boolean isFirstLoad = true;
	
	private View moreView;
	
	private Button bt;
	
	private ProgressBar pg;
	
	private int lastVisibleIndex;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.credit_cost_list_layout);
		super.onCreate(savedInstanceState);

		creditsListView = (ListView) this.findViewById(R.id.creditsListView);
		creditsListView.setOnItemClickListener(CreditCostListActivity.this);
		
		moreView = getLayoutInflater().inflate(R.layout.moredata, null);
		bt = (Button) moreView.findViewById(R.id.bt_load);
		pg = (ProgressBar) moreView.findViewById(R.id.pg);
		bt.setOnClickListener(this);
		
		creditsListView.addFooterView(moreView);
		
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
				
				creditsListView.setOnScrollListener(CreditCostListActivity.this);
				bt.setVisibility(View.VISIBLE);
				pg.setVisibility(View.GONE);
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
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

		// 已经点过，直接返回
		if (isClick) {
			return;
		}

		// 设置已点击标志，避免快速重复点击
		isClick = true;
		// 解锁
		unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);

		switch (v.getId()) {
		case R.id.bt_load:
			pg.setVisibility(View.VISIBLE);
			bt.setVisibility(View.GONE);
			CreditCostListActivity.this.page += 1;
			Thread thread = new Thread(getCreditsRunnable);
			thread.start();
			break;
		default:
			break;
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
		if(StringUtils.isNotNull(credit.reservationId))
		{
			Intent intent = new Intent();
			intent.putExtra("rId", credit.reservationId);
			intent.setClass(CreditCostListActivity.this, CreateCommentActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
		}
		else
		{
			AlertDialog.Builder builder = new Builder(this);
			builder.setMessage("对不起，这条不能评论。");
			builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			builder.create().show();
		}
		
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {

		if (scrollState == OnScrollListener.SCROLL_STATE_IDLE
				&& lastVisibleIndex == creditAdapter.getCount()) {
			bt.setVisibility(View.GONE);
			pg.setVisibility(View.VISIBLE);
			CreditCostListActivity.this.page +=1;
			Thread thread = new Thread(getCreditsRunnable);
			thread.start();
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		// check hit the bottom of current loaded data
		lastVisibleIndex = firstVisibleItem + visibleItemCount -1;
		if(!needToLoad)
		{
			creditsListView.removeFooterView(moreView);
		}
//		if (firstVisibleItem + visibleItemCount == totalItemCount && totalItemCount > 0 && needToLoad) {
//			CreditCostListActivity.this.page += 1;
//			initListView();
//		}
	}
}
