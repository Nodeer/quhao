package com.withiter.quhao.activity;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ListView;

import com.withiter.quhao.R;
import com.withiter.quhao.adapter.CreditAdapter;
import com.withiter.quhao.util.StringUtils;
import com.withiter.quhao.util.http.CommonHTTPRequest;
import com.withiter.quhao.util.tool.ParseJson;
import com.withiter.quhao.util.tool.ProgressDialogUtil;
import com.withiter.quhao.util.tool.QuhaoConstant;
import com.withiter.quhao.util.tool.SharedprefUtil;
import com.withiter.quhao.vo.Credit;

public class CreditCostListActivity extends QuhaoBaseActivity {

	private List<Credit> credits;

	private ListView creditsListView;

	private CreditAdapter creditAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.credit_cost_list_layout);
		super.onCreate(savedInstanceState);

		creditsListView = (ListView) this.findViewById(R.id.creditsListView);
		btnBack.setOnClickListener(goBack(this));

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

				creditAdapter = new CreditAdapter(CreditCostListActivity.this, creditsListView, credits);
				creditsListView.setAdapter(creditAdapter);
				creditAdapter.notifyDataSetChanged();
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			}

		}

	};

	private Runnable getCreditsRunnable = new Runnable() {

		@Override
		public void run() {
			try {
				String accountId = SharedprefUtil.get(CreditCostListActivity.this, QuhaoConstant.ACCOUNT_ID, "");
				String buf = CommonHTTPRequest.get("getCreditCost?accountId=" + accountId);
				if (StringUtils.isNull(buf) || "[]".equals(buf)) {
					unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				} else {
					credits = new ArrayList<Credit>();
					credits = ParseJson.getCredits(buf);
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

}
