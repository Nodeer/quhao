package com.withiter.quhao.activity;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.withiter.quhao.R;
import com.withiter.quhao.adapter.CommentMerchantAdapter;
import com.withiter.quhao.util.QuhaoLog;
import com.withiter.quhao.util.StringUtils;
import com.withiter.quhao.util.http.CommonHTTPRequest;
import com.withiter.quhao.util.tool.ParseJson;
import com.withiter.quhao.vo.Comment;

public class CommentsMerchantActivity extends QuhaoBaseActivity {

	private static final String TAG = CommentsMerchantActivity.class.getName();

	private String merchantName;

	private String merchantId;

	private TextView merchantNameView;
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
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.comments_merchant);

		this.merchantName = getIntent().getStringExtra("merchantName");

		this.merchantId = getIntent().getStringExtra("merchantId");
		this.page = getIntent().getIntExtra("page", 1);
		merchantNameView = (TextView) findViewById(R.id.merchantName);
		merchantNameView.setText(merchantName);

		commentsView = (ListView) findViewById(R.id.commentsView);
		commentsView.setNextFocusDownId(R.id.commentsView);
		commentsView.setOnScrollListener(commentsScrollListener);

		btnBack = (Button) findViewById(R.id.back_btn);
		btnBack.setOnClickListener(this);
		getCritiques();

	}

	private OnScrollListener commentsScrollListener = new OnScrollListener() {
		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {

		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
			// check hit the bottom of current loaded data
			if (firstVisibleItem + visibleItemCount == totalItemCount && totalItemCount > 0 && needToLoad) {
				CommentsMerchantActivity.this.page += 1;
				getCritiques();
			}
		}
	};

	/**
	 * 
	 * query critiques from web service via merchant ID
	 */
	private void getCritiques() {

		Thread getCritiquesRunnable = new Thread(new Runnable() {

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
		getCritiquesRunnable.start();
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
		default:
			break;
		}

	}

	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {
		return false;
	}

}
