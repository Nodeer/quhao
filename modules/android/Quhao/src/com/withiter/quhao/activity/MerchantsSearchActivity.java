package com.withiter.quhao.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;

import com.withiter.quhao.R;
import com.withiter.quhao.adapter.MerchantAdapter;
import com.withiter.quhao.util.tool.ParseJson;
import com.withiter.quhao.util.tool.ProgressDialogUtil;
import com.withiter.quhao.util.tool.QuhaoConstant;
import com.withiter.quhao.vo.Merchant;

public class MerchantsSearchActivity extends AppStoreActivity {

	private String LOGTAG = MerchantsSearchActivity.class.getName();
	protected ListView merchantsListView;
	private List<Merchant> merchants;
	private MerchantAdapter merchantAdapter;
	private EditText editSearch;
	private Button searchBtn;
	private final int UNLOCK_CLICK = 1000;
	private boolean isClick = false;
	private ProgressDialogUtil progressMerchants;
	private boolean isFirst = true;

	/**
	 * handler处理 解锁的时候可能会关闭其他的等待提示框
	 */
	private Handler unlockHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == UNLOCK_CLICK) {
				// 解锁
				isClick = false;
			}
		}
	};

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
			overridePendingTransition(R.anim.main_enter, R.anim.main_exit);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.merchants_search);
		super.onCreate(savedInstanceState);

		editSearch = (EditText) findViewById(R.id.edit_search);
		// editSearch.addTextChangedListener(searchWatcher);

		searchBtn = (Button) findViewById(R.id.search_btn);
		searchBtn.setOnClickListener(goSearchMerchantsListener(this));

		merchantsListView = (ListView) findViewById(R.id.merchantsListView);

		merchantsListView.setNextFocusDownId(R.id.merchantsListView);
		merchantsListView.setVisibility(View.GONE);

		btnPerson.setOnClickListener(goPersonCenterListener(this));
		btnMarchent.setOnClickListener(getMarchentListListener(this));
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
				Log.v(LOGTAG, "search merchants data form server begin : "
						+ MerchantsSearchActivity.this.editSearch.getText());
				if (null == MerchantsSearchActivity.this.editSearch.getText()
						|| "".equals(MerchantsSearchActivity.this.editSearch
								.getText().toString())) {
					unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
					progressMerchants.closeProgress();

				}
				HttpGet request = new HttpGet(QuhaoConstant.HTTP_URL
						+ "MerchantController/getMerchantsByName?name="
						+ MerchantsSearchActivity.this.editSearch.getText()
								.toString());
				HttpClient httpClient = new DefaultHttpClient();
				HttpResponse response = httpClient.execute(request);
				Log.v(LOGTAG, "get top merchant data form server : "
						+ response.getStatusLine().getStatusCode());
				if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					String buf = EntityUtils.toString(response.getEntity());
					Log.v(LOGTAG, "get top merchant data form server buf : "
							+ buf);
					// 返回HTML页面
					if (buf.indexOf("<html>") != -1
							|| buf.indexOf("<HTML>") != -1) {
						// mGetHandler.sendMessage(mGetHandler
						// .obtainMessage(-2));
						throw new Exception("session timeout!");
					}

					if (null == merchants) {
						merchants = new ArrayList<Merchant>();
					}

					merchants.addAll(ParseJson.getMerchants(buf));

					merchantsUpdateHandler.obtainMessage(200, merchants)
							.sendToTarget();
				}

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				progressMerchants.closeProgress();
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
