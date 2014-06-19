package com.withiter.quhao.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.withiter.quhao.QHClientApplication;
import com.withiter.quhao.R;
import com.withiter.quhao.adapter.MerchantChatRoomAdapter;
import com.withiter.quhao.task.GetChatRoomsTask;
import com.withiter.quhao.task.JsonPack;
import com.withiter.quhao.util.QuhaoLog;
import com.withiter.quhao.util.tool.ParseJson;
import com.withiter.quhao.view.refresh.PullToRefreshView;
import com.withiter.quhao.view.refresh.PullToRefreshView.OnFooterRefreshListener;
import com.withiter.quhao.view.refresh.PullToRefreshView.OnHeaderRefreshListener;
import com.withiter.quhao.vo.ReservationVO;

/**
 * 商家列表页面
 */
public class MerchantChatRoomsActivity extends QuhaoBaseActivity implements OnHeaderRefreshListener, OnFooterRefreshListener, OnItemClickListener {

	private String LOGTAG = MerchantChatRoomsActivity.class.getName();
	protected ListView rvoListView;
	private List<ReservationVO> rvos;
	private MerchantChatRoomAdapter merchantChatRoomAdapter;
	private int page;
	private boolean isFirst = true;
	private boolean needToLoad = true;
	public static boolean backClicked = false;

	private PullToRefreshView mPullToRefreshView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.merchant_chat_rooms_layout);
		super.onCreate(savedInstanceState);

		this.rvos = new ArrayList<ReservationVO>();

		this.page = getIntent().getIntExtra("page", 1);
		QuhaoLog.i(LOGTAG, "init page is : " + this.page);

		btnBack.setOnClickListener(goBack(this, this.getClass().getName()));

		mPullToRefreshView = (PullToRefreshView) this.findViewById(R.id.main_pull_refresh_view);
		mPullToRefreshView.setOnHeaderRefreshListener(this);
		mPullToRefreshView.setOnFooterRefreshListener(this);
		mPullToRefreshView.setEnableFooterView(true);
		initView();
	}

	private Handler reservationsUpdateHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 200) {
				super.handleMessage(msg);

				// 默认isFirst是true.
				if (isFirst) {
					merchantChatRoomAdapter = new MerchantChatRoomAdapter(MerchantChatRoomsActivity.this, rvoListView, rvos);
					rvoListView.setAdapter(merchantChatRoomAdapter);
					isFirst = false;
				} else {
					merchantChatRoomAdapter.rvos = rvos;
				}

				merchantChatRoomAdapter.notifyDataSetChanged();
				mPullToRefreshView.onHeaderRefreshComplete();
				mPullToRefreshView.onFooterRefreshComplete();
				
				if (null == rvos ||rvos.isEmpty()) {
					Toast.makeText(MerchantChatRoomsActivity.this, R.string.no_result_4_chat_room, Toast.LENGTH_SHORT).show();
				}
				
				findViewById(R.id.loadingbar).setVisibility(View.GONE);
				findViewById(R.id.serverdata).setVisibility(View.VISIBLE);
				
				if (!needToLoad) {
					mPullToRefreshView.setEnableFooterView(false);
				} else {
					mPullToRefreshView.setEnableFooterView(true);
				}
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			}

		}

	};

	private void initView() {
		rvoListView = (ListView) findViewById(R.id.rvos_list);
		rvoListView.setNextFocusDownId(R.id.rvos_list);
		rvoListView.setOnItemClickListener(this);
		findViewById(R.id.loadingbar).setVisibility(View.VISIBLE);
		findViewById(R.id.serverdata).setVisibility(View.GONE);
//		rvoListView.setOnItemClickListener(itemClickListener);
		getReservations();
	}

	private void getReservations() {

		String url = "";
		String accountId = QHClientApplication.getInstance().accountInfo.accountId;
		url = "getCurrentMerchants?accountId=" + accountId;
		
		final GetChatRoomsTask task = new GetChatRoomsTask(0, this, url);
		task.execute(new Runnable() {
			
			@Override
			public void run() {
				
				JsonPack jsonPack = task.jsonPack;
				rvos = ParseJson.getReservations(jsonPack.getObj());
				needToLoad = false;
				reservationsUpdateHandler.obtainMessage(200, rvos).sendToTarget();
				
			}
		}, new Runnable() {
			
			@Override
			public void run() {
				
				rvos = new ArrayList<ReservationVO>();
				needToLoad = false;
				reservationsUpdateHandler.obtainMessage(200, rvos).sendToTarget();
				
			}
		});
	}

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
	public void onPause() {
		super.onPause();
		QuhaoLog.i(LOGTAG, LOGTAG + " on pause");
	}

	@Override
	public void onFooterRefresh(PullToRefreshView view) {
		mPullToRefreshView.postDelayed(new Runnable() {

			@Override
			public void run() {
				MerchantChatRoomsActivity.this.page += 1;
				getReservations();
			}
		}, 1000);
	}

	@Override
	public void onHeaderRefresh(PullToRefreshView view) {
		mPullToRefreshView.postDelayed(new Runnable() {

			@Override
			public void run() {
				MerchantChatRoomsActivity.this.page = 1;
				isFirst = true;
				needToLoad = true;

				// merchantsListView.setSelectionFromTop(0, 0);// 滑动到第一项
				MerchantChatRoomsActivity.this.rvos = new ArrayList<ReservationVO>();
				getReservations();
			}
		}, 1000);

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		
		if (isClick) {
			return;
		}
		isClick = false;
		
		unlockHandler.sendEmptyMessage(UNLOCK_CLICK);
		if (rvos != null && !rvos.isEmpty()) {
			ReservationVO rvo = rvos.get(arg2);
			Intent intent = new Intent();
			intent.setClass(MerchantChatRoomsActivity.this, MerchantDetailActivity.class);
			intent.putExtra("merchantId", rvo.merchantId);
			startActivity(intent);
		}
		
	}

}
