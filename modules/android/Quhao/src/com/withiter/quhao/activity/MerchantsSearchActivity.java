package com.withiter.quhao.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;

import com.amap.api.location.AMapLocation;
import com.withiter.quhao.QHClientApplication;
import com.withiter.quhao.R;
import com.withiter.quhao.adapter.MerchantSearchAdapter;
import com.withiter.quhao.util.StringUtils;
import com.withiter.quhao.util.http.CommonHTTPRequest;
import com.withiter.quhao.util.tool.ParseJson;
import com.withiter.quhao.util.tool.ProgressDialogUtil;
import com.withiter.quhao.vo.Merchant;

public class MerchantsSearchActivity extends QuhaoBaseActivity {

	private String LOGTAG = MerchantsSearchActivity.class.getName();
	protected ListView merchantsListView;
	private List<Merchant> merchants;
	private MerchantSearchAdapter merchantAdapter;
	private EditText editSearch;
	private Button searchBtn;
	private final int UNLOCK_CLICK = 1000;
	private ProgressDialogUtil progressMerchants;
	private boolean isFirst = true;
	public static boolean backClicked = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.merchants_search);
		super.onCreate(savedInstanceState);

		editSearch = (EditText) findViewById(R.id.edit_search1);
		editSearch.setOnKeyListener(new OnKeyListener() {
			
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				
				if (keyCode == KeyEvent.KEYCODE_ENTER) {
					
					if (isClick) {
						return false;
					}
					isClick = true;
					editSearch.clearFocus();

					// 让软键盘消失
					InputMethodManager m = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					if (m != null) {
						if (m.isActive()) {
							m.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS);
						}

					}
					merchants = new ArrayList<Merchant>();
					getMerchants();
					
					return true;
				}
				
				return false;
			}
		});
		searchBtn = (Button) findViewById(R.id.search_btn);
		searchBtn.setOnClickListener(goSearchMerchantsListener(this));

		merchantsListView = (ListView) findViewById(R.id.merchantsListView);
		merchantsListView.setNextFocusDownId(R.id.merchantsListView);
		merchantsListView.setVisibility(View.GONE);

		btnBack.setOnClickListener(goBack(this, this.getClass().getName()));
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
					merchantAdapter = new MerchantSearchAdapter(MerchantsSearchActivity.this, merchantsListView, merchants);
					merchantsListView.setAdapter(merchantAdapter);
					isFirst = false;
				} else {
					merchantAdapter.merchants = merchants;
				}

				merchantAdapter.notifyDataSetChanged();
				merchantsListView.setOnItemClickListener(merchantItemClickListener);
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
			intent.setClass(MerchantsSearchActivity.this, MerchantDetailActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
		}
	};

	@Override
	protected void onResume() {
		backClicked = false;
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.i(LOGTAG, "backClicked: " + backClicked);
		if (backClicked) {
			overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
		}
	}

	private OnClickListener goSearchMerchantsListener(MerchantsSearchActivity merchantsSearchActivity) {
		OnClickListener listener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isClick) {
					return;
				}
				isClick = true;
				editSearch.clearFocus();

				// 让软键盘消失
				InputMethodManager m = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				if (m != null) {
					if (m.isActive()) {
						m.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS);
					}

				}
				merchants = new ArrayList<Merchant>();
				getMerchants();

			}
		};
		return listener;
	}

	private void getMerchants() {
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
				Log.v(LOGTAG, "search merchants data from server begin : " + MerchantsSearchActivity.this.editSearch.getText());
				if (null == MerchantsSearchActivity.this.editSearch.getText() || "".equals(MerchantsSearchActivity.this.editSearch.getText().toString())) {
					unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
					progressMerchants.closeProgress();

				}
				
				String result = CommonHTTPRequest.get("search?name=" + MerchantsSearchActivity.this.editSearch.getText().toString().trim() + "&cityCode="
						+ QHClientApplication.getInstance().defaultCity.cityCode);
				
				AMapLocation location = QHClientApplication.getInstance().location;
				if (location != null) {
					result = result + "&userX=" + location.getLongitude() + "&userY=" + location.getLatitude();
				} else {
					result = result + "&userX=0.000000&userY=0.000000";
				}
				
				if (StringUtils.isNull(result) || "null".equals(result) || "[]".equals(result)) {
					merchants = new ArrayList<Merchant>();
					merchantsUpdateHandler.obtainMessage(200, merchants).sendToTarget();
					unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				} else {
					if (null == merchants) {
						merchants = new ArrayList<Merchant>();
					}

					merchants.addAll(ParseJson.getMerchants(result));

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

	@Override
	public void onClick(View v) {

	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		return false;
	}

}
