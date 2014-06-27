package com.withiter.quhao.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;

import com.withiter.quhao.R;
import com.withiter.quhao.util.tool.QuhaoConstant;
import com.withiter.quhao.util.tool.SharedprefUtil;

public class LaunchActivity extends Activity {

	private static final int GO_HOME = 1000;
	private static final int GO_GUIDE = 1001;
	// 延迟3秒
	private static final long SPLASH_DELAY_MILLIS = 3000;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.launch);
        
        // 读取SharedPreferences中需要的数据
        // 取得相应的值，如果没有该值，说明还未写入，用true作为默认值
        String isFirstIn = SharedprefUtil.get(this, QuhaoConstant.IS_FIRST_IN, "true");
        if (!"true".equals(isFirstIn)) {
        	mHandler.sendEmptyMessageDelayed(GO_HOME, SPLASH_DELAY_MILLIS);
//        	mHandler.sendEmptyMessageDelayed(GO_GUIDE, SPLASH_DELAY_MILLIS);
		}
        else
        {
        	mHandler.sendEmptyMessageDelayed(GO_GUIDE, SPLASH_DELAY_MILLIS);
        }
	}

	/**
	 * Handler:跳转到不同界面
	 */
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case GO_HOME:
				goHome();
				break;
			case GO_GUIDE:
				goGuide();
				break;
			}
			super.handleMessage(msg);
		}
	};
	
	private void goHome() {
		Intent intent = new Intent(LaunchActivity.this, MainTabActivity.class);
		LaunchActivity.this.startActivity(intent);
		LaunchActivity.this.finish();
	}

	private void goGuide() {
		Intent intent = new Intent(LaunchActivity.this, GuideActivity.class);
		LaunchActivity.this.startActivity(intent);
		LaunchActivity.this.finish();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.launch, menu);
		return true;
	}

}
