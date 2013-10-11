package com.withiter.quhao.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;

import com.withiter.quhao.R;
import com.withiter.quhao.adapter.MerchantAdapter;
import com.withiter.quhao.util.StringUtils;
import com.withiter.quhao.util.http.CommonHTTPRequest;
import com.withiter.quhao.util.tool.ParseJson;
import com.withiter.quhao.util.tool.ProgressDialogUtil;
import com.withiter.quhao.vo.Merchant;

public class MerchantsSearchActivity extends AppStoreActivity {

	private String LOGTAG = MerchantsSearchActivity.class.getName();
	protected ListView merchantsListView;
	private List<Merchant> merchants;
	private MerchantAdapter merchantAdapter;
	private EditText editSearch;
	private Button searchBtn;
	private final int UNLOCK_CLICK = 1000;
	private ProgressDialogUtil progressMerchants;
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
							MerchantsSearchActivity.this, merchantsListView,
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
			intent.setClass(MerchantsSearchActivity.this,
					MerchantDetailActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.merchants_search);
		super.onCreate(savedInstanceState);

		editSearch = (EditText) findViewById(R.id.edit_search1);
		// editSearch.addTextChangedListener(searchWatcher);

		searchBtn = (Button) findViewById(R.id.search_btn);
		searchBtn.setOnClickListener(goSearchMerchantsListener(this));

		merchantsListView = (ListView) findViewById(R.id.merchantsListView);

		merchantsListView.setNextFocusDownId(R.id.merchantsListView);
		merchantsListView.setVisibility(View.GONE);

		btnBack.setOnClickListener(goBack(this));
		// initView();
	}

	private OnClickListener goSearchMerchantsListener(
			MerchantsSearchActivity merchantsSearchActivity) {
		OnClickListener listener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isClick) {
					return;
				}
				isClick = true;
				editSearch.clearFocus();

				// 让软键盘消失
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(editSearch.getWindowToken(), 0);
				getMerchants();

			}
		};
		return listener;
	}

	private void getMerchants() {
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
				Log.v(LOGTAG, "search merchants data from server begin : "
						+ MerchantsSearchActivity.this.editSearch.getText());
				if (null == MerchantsSearchActivity.this.editSearch.getText()
						|| "".equals(MerchantsSearchActivity.this.editSearch
								.getText().toString())) {
					unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
					progressMerchants.closeProgress();

				}
				String result = CommonHTTPRequest
						.get("MerchantController/getMerchantsByName?name="
								+ MerchantsSearchActivity.this.editSearch
										.getText().toString());
				if (StringUtils.isNull(result)) {
					unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				} else {
					if (null == merchants) {
						merchants = new ArrayList<Merchant>();
					}

					merchants.addAll(ParseJson.getMerchants(result));

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

	@Override
	public void onClick(View v) {

	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		return false;
	}

}
