package com.withiter.quhao.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.withiter.quhao.QHClientApplication;
import com.withiter.quhao.R;
import com.withiter.quhao.adapter.ReservationForHistoryPaiduiAdapter;
import com.withiter.quhao.exception.NoResultFromHTTPRequestException;
import com.withiter.quhao.util.StringUtils;
import com.withiter.quhao.util.http.CommonHTTPRequest;
import com.withiter.quhao.util.tool.ParseJson;
import com.withiter.quhao.util.tool.ProgressDialogUtil;
import com.withiter.quhao.vo.ReservationVO;

/**
 * Quhao states of Current/History
 * 
 */
public class QuhaoHistoryStatesActivity extends QuhaoBaseActivity{

	protected static boolean backClicked = false;
	private static String TAG = QuhaoHistoryStatesActivity.class.getName();

	private List<ReservationVO> reservations;
	private ListView paiduiListView;
	private ReservationForHistoryPaiduiAdapter reservationForPaiduiAdapter;

	private ProgressDialogUtil progress;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.paidui_history_list_layout);
		super.onCreate(savedInstanceState);

		paiduiListView = (ListView) this.findViewById(R.id.paiduiListView);
		btnBack.setOnClickListener(goBack(this));

		initData();
	}

	private void initData() {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				Looper.prepare();
				try {
					progressDialogUtil = new ProgressDialogUtil(QuhaoHistoryStatesActivity.this, R.string.empty, R.string.waitting, false);
					progressDialogUtil.showProgress();
					
					String url = "";
					String accountId = QHClientApplication.getInstance().accountInfo.accountId;
					url = "getHistoryMerchants?accountId=" + accountId;
					
					String buf = CommonHTTPRequest.get(url);
					if (StringUtils.isNull(buf) || "[]".equals(buf)) {
						unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
						throw new NoResultFromHTTPRequestException();
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

	private Handler reservationsUpdateHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 200) {
				super.handleMessage(msg);
				reservationForPaiduiAdapter = new ReservationForHistoryPaiduiAdapter(QuhaoHistoryStatesActivity.this, paiduiListView, reservations);
				paiduiListView.setAdapter(reservationForPaiduiAdapter);
				reservationForPaiduiAdapter.notifyDataSetChanged();
				
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
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
	
	private void initDataForResume() {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				Looper.prepare();
				try {
					progress = new ProgressDialogUtil(QuhaoHistoryStatesActivity.this, R.string.empty, R.string.waitting, false);
					progress.showProgress();
					
					String url = "";
					String accountId = QHClientApplication.getInstance().accountInfo.accountId;
					url = "getHistoryMerchants?accountId=" + accountId;
					
					String buf = CommonHTTPRequest.get(url);
					if (StringUtils.isNull(buf) || "[]".equals(buf)) {
						unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
						throw new NoResultFromHTTPRequestException();
					} else {
						reservations = new ArrayList<ReservationVO>();
						reservations = ParseJson.getReservations(buf);
						reservationsUpdateHandler.obtainMessage(200, reservations).sendToTarget();
					}

				} catch (Exception e) {
					unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
					e.printStackTrace();
				} finally {
					progress.closeProgress();
					Looper.loop();
				}

			}
		});
		thread.start();

	}
	@Override
	protected void onResume() {
		backClicked = false;
		initDataForResume();
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
	protected void onDestroy() {
		
		super.onDestroy();
		if (progressDialogUtil!=null) {
			progressDialogUtil.closeProgress();
		}
	}

}
