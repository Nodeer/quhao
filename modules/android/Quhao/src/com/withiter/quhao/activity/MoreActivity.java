package com.withiter.quhao.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;

import com.withiter.quhao.R;
import com.withiter.quhao.util.tool.ProgressDialogUtil;

public class MoreActivity extends AppStoreActivity {

	private static final String TAG = MoreActivity.class.getName();
	
	private LinearLayout settings;
	private LinearLayout aboutUs;
	private LinearLayout opinion;
	private LinearLayout version;
	private LinearLayout moreShare; 
	private LinearLayout help;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.more_layout);
		super.onCreate(savedInstanceState);
		
		btnCategory.setOnClickListener(goCategory(this));
		btnNearby.setOnClickListener(goNearby(this));
		btnPerson.setOnClickListener(goPersonCenter(this));
		btnMore.setOnClickListener(goMore(this));
		
		settings = (LinearLayout) this.findViewById(R.id.more_settings);
		version = (LinearLayout) this.findViewById(R.id.more_version);
		opinion = (LinearLayout) this.findViewById(R.id.more_opinion);
		moreShare = (LinearLayout) this.findViewById(R.id.more_share);
		help = (LinearLayout) this.findViewById(R.id.more_help);
		aboutUs = (LinearLayout) this.findViewById(R.id.more_aboutus);
		
		settings.setOnClickListener(this);
		version.setOnClickListener(this);
		opinion.setOnClickListener(this);
		moreShare.setOnClickListener(this);
		help.setOnClickListener(this);
		aboutUs.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		if(isClick)
		{
			return;
		}
		progressDialogUtil = new ProgressDialogUtil(this, R.string.empty,
				R.string.querying, false);
		progressDialogUtil.showProgress();
		switch (v.getId()) {
		case R.id.more_settings:// 系统设置
			progressDialogUtil.closeProgress();
			unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			Intent intent = new Intent(this, SettingsActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.main_enter,
					R.anim.main_exit);
			break;
		case R.id.more_aboutus:// 关于我们
			progressDialogUtil.closeProgress();
			unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			Intent intent1 = new Intent(this, AboutUsActivity.class);
			startActivity(intent1);
			overridePendingTransition(R.anim.main_enter,
					R.anim.main_exit);
			break;
		case R.id.more_opinion:// 反馈
			progressDialogUtil.closeProgress();
			unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			Intent intent2 = new Intent(this, OpinionActivity.class);
			startActivity(intent2);
			overridePendingTransition(R.anim.main_enter,
					R.anim.main_exit);
			break;
		case R.id.more_help://帮助
			progressDialogUtil.closeProgress();
			unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			Intent intent3 = new Intent(this, HelpActivity.class);
			startActivity(intent3);
			overridePendingTransition(R.anim.main_enter,
					R.anim.main_exit);
			break;
		case R.id.more_version:// 版本检测
			progressDialogUtil.closeProgress();
			unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			break;
		case R.id.more_share:// 分享给好友
			// 显示分享界面
			progressDialogUtil.closeProgress();
			unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			Intent intent4 = new Intent(this, ShareDialogActivity.class);
			startActivity(intent4);
			overridePendingTransition(R.anim.main_enter,
					R.anim.main_exit);
			break;

		default:
			break;
		}

	}

	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {
		// TODO Auto-generated method stub
		return false;
	}
}
