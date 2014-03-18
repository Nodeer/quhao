package com.withiter.quhao.activity;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.withiter.quhao.R;
import com.withiter.quhao.adapter.CommentMerchantAdapter;
import com.withiter.quhao.util.QuhaoLog;
import com.withiter.quhao.util.StringUtils;
import com.withiter.quhao.util.http.CommonHTTPRequest;
import com.withiter.quhao.util.tool.ParseJson;
import com.withiter.quhao.vo.Comment;

public class CommentsMerchantActivity extends QuhaoBaseActivity implements OnScrollListener{

	private static final String TAG = CommentsMerchantActivity.class.getName();

	private String merchantName;

	private String merchantId;
	
	private String rId;

	/**
	 * the critiques queried from merchant
	 */
	private List<Comment> comments;

	/**
	 * back button
	 */
	private Button btnBack;

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
	
	private View moreView;
	
	private Button bt;
	
	private ProgressBar pg;
	
	private int lastVisibleIndex;

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
				bt.setVisibility(View.VISIBLE);
				pg.setVisibility(View.GONE);
				commentsView.setOnScrollListener(CommentsMerchantActivity.this);
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.comments_merchant);

		this.merchantName = getIntent().getStringExtra("merchantName");

		this.merchantId = getIntent().getStringExtra("merchantId");
		this.rId = getIntent().getStringExtra("rId");
		this.page = getIntent().getIntExtra("page", 1);

		moreView = getLayoutInflater().inflate(R.layout.moredata, null);
		bt = (Button) moreView.findViewById(R.id.bt_load);
		pg = (ProgressBar) moreView.findViewById(R.id.pg);
		
		bt.setOnClickListener(this);
		commentsView = (ListView) findViewById(R.id.commentsView);
		commentsView.addFooterView(moreView);
		commentsView.setNextFocusDownId(R.id.commentsView);

		btnBack = (Button) findViewById(R.id.back_btn);
		btnBack.setOnClickListener(this);
		
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
		case R.id.back_btn:
			onBackPressed();
			this.finish();
			break;
		case R.id.bt_load:
			pg.setVisibility(View.VISIBLE);
			bt.setVisibility(View.GONE);
			CommentsMerchantActivity.this.page +=1;
			getComments();
			
			break;
		default:
			break;
		}

	}

	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {
		return false;
	}

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

	@Override
	protected void onResume() {
		
		super.onResume();
		findViewById(R.id.loadingbar).setVisibility(View.VISIBLE);
		findViewById(R.id.commentsLayout).setVisibility(View.GONE);
		isFirstLoad = true;
		needToLoad = true;
		lastVisibleIndex=0;
		this.page = 1;
		this.comments = new ArrayList<Comment>();
		getComments();
		
	}
}
