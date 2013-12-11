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
import com.withiter.quhao.util.StringUtils;
import com.withiter.quhao.util.http.CommonHTTPRequest;
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
		QuhaoLog.i(LOGTAG, LOGTAG + " on pause");
		if (backClicked) {
			overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
		}
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

		// TODO add help content here
		helpList = new ArrayList<HelpVO>();
		HelpVO help1 = new HelpVO("how to use qhao", "11111111111111111111111");
		HelpVO help2 = new HelpVO("how to use qhao", "11111111111111111111111");
		helpList.add(help1);
		helpList.add(help2);
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
