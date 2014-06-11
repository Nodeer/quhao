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
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.withiter.quhao.R;
import com.withiter.quhao.adapter.YouhuiAdapter;
import com.withiter.quhao.util.ActivityUtil;
import com.withiter.quhao.util.QuhaoLog;
import com.withiter.quhao.util.StringUtils;
import com.withiter.quhao.util.http.CommonHTTPRequest;
import com.withiter.quhao.util.tool.ParseJson;
import com.withiter.quhao.vo.YouhuiVO;

public class YouhuiListActivity extends QuhaoBaseActivity{

	private static final String TAG = YouhuiListActivity.class.getName();

	private String merchantId;
	
	/**
	 * the critiques queried from merchant
	 */
	private List<YouhuiVO> youhuis;

	/**
	 * back button
	 */
	private Button btnBack;

	/**
	 * list view for critiques
	 */
	private ListView youhuisView;

	/**
	 * critique adapter
	 */
	private YouhuiAdapter youhuiAdapter;

	/**
	 * when the page is first loaded, the critiques will be initialize , the
	 * value isFirstLoad will be true when the page is not first loaded, the
	 * critiques list have been there, we just add list into the adapter.
	 */
	private boolean isFirstLoad = true;

	protected Handler updateYouhuisHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			super.handleMessage(msg);

			if (msg.what == 200) {

				if (isFirstLoad) {

					findViewById(R.id.loadingbar).setVisibility(View.GONE);
					findViewById(R.id.youhui_list_ayout).setVisibility(View.VISIBLE);
					youhuiAdapter = new YouhuiAdapter(YouhuiListActivity.this, youhuisView, youhuis);
					youhuisView.setAdapter(youhuiAdapter);
					isFirstLoad = false;
				} else {
					youhuiAdapter.youhuis = youhuis;
				}
				youhuiAdapter.notifyDataSetChanged();
				
				findViewById(R.id.loadingbar).setVisibility(View.GONE);
				findViewById(R.id.youhui_list_ayout).setVisibility(View.VISIBLE);
				
				if (null == youhuis ||youhuis.isEmpty()) {
					Toast.makeText(YouhuiListActivity.this, R.string.no_result_found, Toast.LENGTH_SHORT).show();
				}
				
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.youhui_list_layout);

		this.merchantId = getIntent().getStringExtra("merchantId");
		
		youhuisView = (ListView) findViewById(R.id.youhuis_listview);
		youhuisView.setNextFocusDownId(R.id.youhuis_listview);

		btnBack = (Button) findViewById(R.id.back_btn);
		btnBack.setOnClickListener(this);
		
	}

	/**
	 * 
	 * query critiques from web service via merchant ID
	 */
	private void getYouhuis() {

		Thread getYouhuisRunnable = new Thread(new Runnable() {

			@Override
			public void run() {

				try {
					Looper.prepare();
					QuhaoLog.v(TAG, "query youhuis from web service, the merchant id is : " + merchantId);
					if (!ActivityUtil.isNetWorkAvailable(getApplicationContext())) {
						Toast.makeText(getApplicationContext(), R.string.network_error_info, Toast.LENGTH_SHORT).show();
						updateYouhuisHandler.obtainMessage(200, null).sendToTarget();
						return;
					}
					
					String buf = CommonHTTPRequest.get("youhui?mid=" + merchantId);

					if (StringUtils.isNull(buf) || "[]".equals(buf)) {
						updateYouhuisHandler.obtainMessage(200, null).sendToTarget();
					} else {
						//
						if (isFirstLoad || null == youhuis) {
							youhuis = new ArrayList<YouhuiVO>();
						}
						List<YouhuiVO> youhuiList = ParseJson.getYouhuis(buf);
						youhuis.addAll(youhuiList);
						updateYouhuisHandler.obtainMessage(200, youhuis).sendToTarget();
					}
				} catch (Exception e) {
					unlockHandler.sendEmptyMessageAtTime(UNLOCK_CLICK, 1000);
					Toast.makeText(YouhuiListActivity.this, R.string.network_error_info, Toast.LENGTH_SHORT).show();
					QuhaoLog.e(TAG, "Error for querying critiques from web service, the error is : " + e.getMessage());
				} finally {
					unlockHandler.sendEmptyMessageAtTime(UNLOCK_CLICK, 1000);
					Looper.loop();
				}

			}
		});
		getYouhuisRunnable.start();
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

	@Override
	protected void onResume() {
		
		super.onResume();
		findViewById(R.id.loadingbar).setVisibility(View.VISIBLE);
		findViewById(R.id.youhui_list_ayout).setVisibility(View.GONE);
		isFirstLoad = true;
		this.youhuis = new ArrayList<YouhuiVO>();
		getYouhuis();
		
	}
}
