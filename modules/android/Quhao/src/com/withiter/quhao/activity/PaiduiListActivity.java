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
import android.widget.ListView;
import android.widget.TextView;

import com.withiter.quhao.R;
import com.withiter.quhao.adapter.ReservationForPaiduiAdapter;
import com.withiter.quhao.util.StringUtils;
import com.withiter.quhao.util.http.CommonHTTPRequest;
import com.withiter.quhao.util.tool.ParseJson;
import com.withiter.quhao.util.tool.ProgressDialogUtil;
import com.withiter.quhao.util.tool.QuhaoConstant;
import com.withiter.quhao.util.tool.SharedprefUtil;
import com.withiter.quhao.vo.ReservationVO;

public class PaiduiListActivity extends QuhaoBaseActivity {

	private List<ReservationVO> reservations;

	private TextView titleView;

	private ListView paiduiListView;

	private String queryCondition;

	private ReservationForPaiduiAdapter reservationForPaiduiAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.paidui_list_layout);
		super.onCreate(savedInstanceState);

		queryCondition = this.getIntent().getStringExtra("queryCondition");
		titleView = (TextView) findViewById(R.id.title);
		if ("current".equals(queryCondition)) {
			titleView.setText("当前取号情况");
		} else if ("history".equals(queryCondition)) {
			titleView.setText("历史取号情况");
		}

		paiduiListView = (ListView) this.findViewById(R.id.paiduiListView);
		btnBack.setOnClickListener(goBack(this));

		initData();

	}

	private Handler reservationsUpdateHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 200) {
				super.handleMessage(msg);

				reservationForPaiduiAdapter = new ReservationForPaiduiAdapter(PaiduiListActivity.this, paiduiListView, reservations);
				paiduiListView.setAdapter(reservationForPaiduiAdapter);
				reservationForPaiduiAdapter.notifyDataSetChanged();
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			}

		}

	};

	private void initData() {

		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				Looper.prepare();
				try {

					progressDialogUtil = new ProgressDialogUtil(PaiduiListActivity.this, R.string.empty, R.string.waitting, false);
					progressDialogUtil.showProgress();
					String url = "";
					String accountId = SharedprefUtil.get(PaiduiListActivity.this, QuhaoConstant.ACCOUNT_ID, "false");
					if ("current".equals(queryCondition)) {
						url = "getCurrentMerchants?accountId=" + accountId;
					} else if ("history".equals(queryCondition)) {
						url = "getHistoryMerchants?accountId=" + accountId;
					}
					String buf = CommonHTTPRequest.get(url);
					if (StringUtils.isNull(buf) || "[]".equals(buf)) {
						unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
					} else {
						reservations = new ArrayList<ReservationVO>();
						reservations = ParseJson.getReservations(buf);
						reservationsUpdateHandler.obtainMessage(200, reservations).sendToTarget();
					}

				} catch (Exception e) {
					unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
					e.printStackTrace();
				} finally {
					progressDialogUtil.closeProgress();
					Looper.loop();
				}

			}
		});
		thread.start();

	}

	@Override
	public void onClick(View v) {

	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return false;
	}

}
