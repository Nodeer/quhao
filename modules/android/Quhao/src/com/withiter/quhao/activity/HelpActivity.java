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
import android.widget.Toast;

import com.withiter.quhao.R;
import com.withiter.quhao.adapter.HelpAdapter;
import com.withiter.quhao.util.QuhaoLog;
import com.withiter.quhao.vo.HelpVO;

public class HelpActivity extends QuhaoBaseActivity {

	private List<HelpVO> helpList;
	private ListView helpListView;
	private HelpAdapter helpAdapter;

	public static boolean backClicked = false;
	private String LOGTAG = HelpActivity.class.getName();

	@Override
	public void finish() {
		super.finish();
		QuhaoLog.i(LOGTAG, LOGTAG + " finished");
	}

	@Override
	protected void onResume() {
		backClicked = false;
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	/**
	 * when the page is first loaded, the critiques will be initialize , the
	 * value isFirstLoad will be true when the page is not first loaded, the
	 * critiques list have been there, we just add list into the adapter.
	 */
	private boolean isFirstLoad = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.more_help_layout);
		super.onCreate(savedInstanceState);

		btnBack.setOnClickListener(goBack(this, this.getClass().getName()));
		helpListView = (ListView) this.findViewById(R.id.helpListView);

		helpList = new ArrayList<HelpVO>();
		HelpVO help1 = new HelpVO("为什么需要手机注册？", "手机注册取号会员后，系统才能通过手机短信通知您的排队情况。");
		HelpVO help2 = new HelpVO("如何注册？", "打开取号APP后，进入【我的】，左上角有注册按钮。");
		HelpVO help3 = new HelpVO("积分有什么用？", "积分是用来取号排队用的，每次取号会消耗一定积分。");
		HelpVO help4 = new HelpVO("如何增加积分？", "在某商家消费结束之后，确认消费即可返还取号时消费的积分。另外可以通过完成任务以及签到获得奖励积分。");
		HelpVO help5 = new HelpVO("如何签到？", "打开取号APP后，进入【我的】，点击签到下对应的数字即可签到。（签到每天只能一次，必须登录之后方可签到。）");
		helpList.add(help1);
		helpList.add(help2);
		helpList.add(help3);
		helpList.add(help4);
		helpList.add(help5);
		updateHelpHandler.obtainMessage(200, helpList).sendToTarget();
		
	}

	protected Handler updateHelpHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 200) {
				if (isFirstLoad) {
					findViewById(R.id.loadingbar).setVisibility(View.GONE);
					findViewById(R.id.helpLayout).setVisibility(View.VISIBLE);
					helpAdapter = new HelpAdapter(HelpActivity.this, helpListView, helpList);
					helpListView.setAdapter(helpAdapter);
					isFirstLoad = false;
				} else {
					helpAdapter.helpList = helpList;
				}
				
				helpAdapter.notifyDataSetChanged();
				
				if (null == helpList ||helpList.isEmpty()) {
					Toast.makeText(HelpActivity.this, R.string.no_result_found, Toast.LENGTH_SHORT).show();
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
}
