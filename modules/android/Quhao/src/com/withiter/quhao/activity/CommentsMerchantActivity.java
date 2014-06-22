package com.withiter.quhao.activity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.Toast;

import com.withiter.quhao.R;
import com.withiter.quhao.adapter.CommentMerchantAdapter;
import com.withiter.quhao.util.ActivityUtil;
import com.withiter.quhao.util.QuhaoLog;
import com.withiter.quhao.util.StringUtils;
import com.withiter.quhao.util.http.CommonHTTPRequest;
import com.withiter.quhao.util.tool.ParseJson;
import com.withiter.quhao.view.refresh.PullToRefreshView;
import com.withiter.quhao.view.refresh.PullToRefreshView.OnFooterRefreshListener;
import com.withiter.quhao.view.refresh.PullToRefreshView.OnHeaderRefreshListener;
import com.withiter.quhao.vo.Comment;

public class CommentsMerchantActivity extends QuhaoBaseActivity implements OnHeaderRefreshListener,OnFooterRefreshListener{

	private static final String TAG = CommentsMerchantActivity.class.getName();

	private String merchantId;
	
	private String grade;
	
	/**
	 * the critiques queried from merchant
	 */
	private List<Comment> comments;

	/**
	 * list view for critiques
	 */
	private ListView commentsView;

	/**
	 * critique adapter
	 */
	private CommentMerchantAdapter commentAdapter;

	/**
	 * when the page is first loaded, the critiques will be initialize , the
	 * value isFirstLoad will be true when the page is not first loaded, the
	 * critiques list have been there, we just add list into the adapter.
	 */
	private boolean isFirstLoad = true;

	private boolean needToLoad = true;

	private int page;
	
	private PullToRefreshView mPullToRefreshView;
	
	private RatingBar gradeRatingBar;
	
	protected Handler updateCommentsHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			super.handleMessage(msg);

			if (msg.what == 200) {

				if (null == msg.obj) {
					findViewById(R.id.loadingbar).setVisibility(View.GONE);
					findViewById(R.id.commentsLayout).setVisibility(View.VISIBLE);
					unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
					return;
				}
				if (isFirstLoad) {

					findViewById(R.id.loadingbar).setVisibility(View.GONE);
					findViewById(R.id.commentsLayout).setVisibility(View.VISIBLE);
					commentAdapter = new CommentMerchantAdapter(CommentsMerchantActivity.this, commentsView, comments);
					commentsView.setAdapter(commentAdapter);
					isFirstLoad = false;
				} else {
					commentAdapter.comments = comments;
				}
				commentAdapter.notifyDataSetChanged();
				mPullToRefreshView.onHeaderRefreshComplete();
				mPullToRefreshView.onFooterRefreshComplete();
				
				if (null == comments || comments.isEmpty()) {
					Toast.makeText(CommentsMerchantActivity.this, R.string.no_result_found, Toast.LENGTH_SHORT).show();
				}
				
				if(!needToLoad)
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.comments_merchant);
		super.onCreate(savedInstanceState);

		this.merchantId = getIntent().getStringExtra("merchantId");
		this.grade = getIntent().getStringExtra("grade");
		this.page = getIntent().getIntExtra("page", 1);

		gradeRatingBar = (RatingBar) this.findViewById(R.id.grade);
		
		if (StringUtils.isNotNull(grade)) {
			int scale = 2;//设置位数 

			int roundingMode = 4;//表示四舍五入，可以选择其他舍值方式，例如去尾，等等. 

			BigDecimal bd = new BigDecimal(grade); 

			bd = bd.setScale(scale,roundingMode); 
			
			gradeRatingBar.setRating(bd.floatValue());
		}
		else
		{
			gradeRatingBar.setRating(0);
		}
		
		mPullToRefreshView = (PullToRefreshView) this.findViewById(R.id.main_pull_refresh_view);
		mPullToRefreshView.setOnHeaderRefreshListener(this);
		mPullToRefreshView.setOnFooterRefreshListener(this);
		
		commentsView = (ListView) findViewById(R.id.commentsView);
		commentsView.setNextFocusDownId(R.id.commentsView);

		btnBack.setOnClickListener(goBack(this));
		
	}

	/**
	 * 
	 * query critiques from web service via merchant ID
	 */
	private void getComments() {

		Thread getCommentsRunnable = new Thread(new Runnable() {

			@Override
			public void run() {

				try {
					Looper.prepare();
					QuhaoLog.v(TAG, "query critiques from web service, the merchant id is : " + merchantId);
					if (!ActivityUtil.isNetWorkAvailable(getApplicationContext())) {
						Toast.makeText(getApplicationContext(), R.string.network_error_info, Toast.LENGTH_SHORT).show();
						return;
					}
					String buf = CommonHTTPRequest.get("getCommentsByMid?page=" + page + "&mid=" + merchantId);

					if (StringUtils.isNull(buf) || "[]".equals(buf)) {
						needToLoad = false;
						updateCommentsHandler.obtainMessage(200, null).sendToTarget();
					} else {
						//
						if (isFirstLoad || null == comments) {
							comments = new ArrayList<Comment>();
						}
						List<Comment> commentList = ParseJson.getComments(buf);
						if(commentList.size()<10)
						{
							needToLoad = false;
						}
						comments.addAll(commentList);
						updateCommentsHandler.obtainMessage(200, comments).sendToTarget();
					}
				} catch (Exception e) {
					unlockHandler.sendEmptyMessageAtTime(UNLOCK_CLICK, 1000);
					Toast.makeText(CommentsMerchantActivity.this, R.string.network_error_info, Toast.LENGTH_SHORT).show();
					QuhaoLog.e(TAG, "Error for querying critiques from web service, the error is : " + e.getMessage());
				} finally {
					unlockHandler.sendEmptyMessageAtTime(UNLOCK_CLICK, 1000);
					Looper.loop();
				}

			}
		});
		getCommentsRunnable.start();
	}

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
		default:
			break;
		}

	}

	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {
		return false;
	}

	/*
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (scrollState == OnScrollListener.SCROLL_STATE_IDLE
				&& lastVisibleIndex == commentAdapter.getCount()) {
			pg.setVisibility(View.VISIBLE);
			bt.setVisibility(View.GONE);
			CommentsMerchantActivity.this.page +=1;
			getComments();
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		
		// check hit the bottom of current loaded data
		lastVisibleIndex = firstVisibleItem + visibleItemCount -1;
		if (!needToLoad) {
			commentsView.removeFooterView(moreView);
		}
//		if (firstVisibleItem + visibleItemCount == totalItemCount && totalItemCount > 0 && needToLoad) {
//			CommentsMerchantActivity.this.page += 1;
//			getCritiques();
//		}
	}
	*/

	@Override
	protected void onResume() {
		
		super.onResume();
		findViewById(R.id.loadingbar).setVisibility(View.VISIBLE);
		findViewById(R.id.commentsLayout).setVisibility(View.GONE);
		isFirstLoad = true;
		needToLoad = true;
		this.page = 1;
		this.comments = new ArrayList<Comment>();
		getComments();
		
	}

	@Override
	public void onFooterRefresh(PullToRefreshView view) {
		mPullToRefreshView.postDelayed(new Runnable() {

			@Override
			public void run() {
				page += 1;
				getComments();
			}
		}, 1000);
	}

	@Override
	public void onHeaderRefresh(PullToRefreshView view) {
		mPullToRefreshView.postDelayed(new Runnable() {

			@Override
			public void run() {
				page = 1;
				isFirstLoad = true;
				needToLoad = true;
				
				comments = new ArrayList<Comment>();
				getComments();
			}
		}, 1000);
	}
}
