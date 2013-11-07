package com.withiter.quhao.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.withiter.quhao.R;
import com.withiter.quhao.util.tool.ProgressDialogUtil;
import com.withiter.quhao.util.tool.QuhaoConstant;
import com.withiter.quhao.util.tool.SharedprefUtil;

public class MoreActivity extends QuhaoBaseActivity {

	private static final String TAG = MoreActivity.class.getName();
	
	private LinearLayout settings;
	private LinearLayout aboutUs;
	private LinearLayout opinion;
	private LinearLayout version;
	private LinearLayout moreShare; 
	private LinearLayout help;
	private LinearLayout loginStatus;
	
	private ImageView loginStatusImg;
	
	private TextView loginStatusTxt;
	
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
		loginStatus = (LinearLayout) this.findViewById(R.id.more_login_status);
		settings.setOnClickListener(this);
		version.setOnClickListener(this);
		opinion.setOnClickListener(this);
		moreShare.setOnClickListener(this);
		help.setOnClickListener(this);
		aboutUs.setOnClickListener(this);
		loginStatus.setOnClickListener(this);
		
		loginStatusImg = (ImageView) this.findViewById(R.id.more_login_status_img);
		loginStatusTxt = (TextView) this.findViewById(R.id.more_login_status_txt);
		
		String loginStatus = SharedprefUtil.get(this, QuhaoConstant.IS_LOGIN, "false");
		if("true".equals(loginStatus))
		{
			loginStatusImg.setImageResource(R.drawable.logout_status);
			loginStatusTxt.setText(R.string.more_logout);
		}
		else
		{
			loginStatusImg.setImageResource(R.drawable.login_status);
			loginStatusTxt.setText(R.string.more_no_login);
		}
		
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
			overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
			break;
		case R.id.more_aboutus:// 关于我们
			progressDialogUtil.closeProgress();
			unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			Intent intent1 = new Intent(this, AboutUsActivity.class);
			startActivity(intent1);
			overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
			break;
		case R.id.more_opinion:// 反馈
			progressDialogUtil.closeProgress();
			unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			Intent intent2 = new Intent(this, OpinionActivity.class);
			startActivity(intent2);
			overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
			break;
		case R.id.more_help://帮助
			progressDialogUtil.closeProgress();
			unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			Intent intent3 = new Intent(this, HelpActivity.class);
			startActivity(intent3);
			overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
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
			overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
			break;
			
		case R.id.more_login_status:// 分享给好友
			// 显示分享界面
			progressDialogUtil.closeProgress();
			unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			
			String loginStatus = SharedprefUtil.get(MoreActivity.this, QuhaoConstant.IS_LOGIN, "false");
			if("true".equals(loginStatus))
			{
				loginHandler.obtainMessage(200, loginStatus)
				.sendToTarget();
			}
			else
			{
				Intent intent5 = new Intent(MoreActivity.this, LoginActivity.class);
				intent5.putExtra("activityName",
						this.getClass().getName());
				intent5.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent5);
				overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
			}
			break;

		default:
			break;
		}

	}

	protected Handler loginHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == 200) {
				super.handleMessage(msg);
				SharedprefUtil.put(MoreActivity.this, QuhaoConstant.IS_LOGIN, "false");
				loginStatusImg.setImageResource(R.drawable.login_status);
				loginStatusTxt.setText(R.string.more_no_login);
			}
		}
	};
	
	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {
		// TODO Auto-generated method stub
		return false;
	}
}
