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
import android.widget.Toast;

import com.withiter.quhao.QHClientApplication;
import com.withiter.quhao.R;
import com.withiter.quhao.adapter.ReservationForCurrentPaiduiAdapter;
import com.withiter.quhao.exception.NoResultFromHTTPRequestException;
import com.withiter.quhao.util.ActivityUtil;
import com.withiter.quhao.util.StringUtils;
import com.withiter.quhao.util.http.CommonHTTPRequest;
import com.withiter.quhao.util.tool.ParseJson;
import com.withiter.quhao.util.tool.ProgressDialogUtil;
import com.withiter.quhao.vo.ReservationVO;

/**
 * Quhao states of Current/History
 * 
 */
public class QuhaoCurrentStatesActivity extends QuhaoBaseActivity{

	protected static boolean backClicked = false;
	private static String TAG = QuhaoCurrentStatesActivity.class.getName();

	private List<ReservationVO> reservations;
	private ListView paiduiListView;
	private ReservationForCurrentPaiduiAdapter reservationForPaiduiAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.paidui_current_list_layout);
		super.onCreate(savedInstanceState);

		paiduiListView = (ListView) this.findViewById(R.id.paiduiListView);
//		paiduiListView.setOnItemClickListener(QuhaoCurrentStatesActivity.this);
		btnBack.setOnClickListener(goBack(this));

		initData();
	}

	public void initData() {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				Looper.prepare();
				try {
					progressDialogUtil = new ProgressDialogUtil(QuhaoCurrentStatesActivity.this, R.string.empty, R.string.waitting, false);
					progressDialogUtil.showProgress();
					
					String url = "";
					String accountId = QHClientApplication.getInstance().accountInfo.accountId;
					url = "getCurrentMerchants?accountId=" + accountId;
					
					if (!ActivityUtil.isNetWorkAvailable(getApplicationContext())) {
						Toast.makeText(getApplicationContext(), R.string.network_error_info, Toast.LENGTH_SHORT).show();
						unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
						return;
					}
					
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
					reservations = new ArrayList<ReservationVO>();
					reservationsUpdateHandler.obtainMessage(200, reservations).sendToTarget();
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
				reservationForPaiduiAdapter = new ReservationForCurrentPaiduiAdapter(QuhaoCurrentStatesActivity.this, paiduiListView, reservations);
				paiduiListView.setAdapter(reservationForPaiduiAdapter);
				reservationForPaiduiAdapter.notifyDataSetChanged();
				
				if (null == reservations ||reservations.isEmpty()) {
					Toast.makeText(QuhaoCurrentStatesActivity.this, R.string.no_result_4_quhao_current, Toast.LENGTH_SHORT).show();
				}
				
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
	
	
	@Override
	protected void onResume() {
		backClicked = false;
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		
		super.onDestroy();
		if (progressDialogUtil!=null) {
			progressDialogUtil.closeProgress();
		}
	}

}
