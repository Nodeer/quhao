package com.withiter.quhao.activity;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.withiter.quhao.QHClientApplication;
import com.withiter.quhao.R;
import com.withiter.quhao.domain.AccountInfo;
import com.withiter.quhao.util.QuhaoLog;
import com.withiter.quhao.util.StringUtils;
import com.withiter.quhao.util.encrypt.DesUtils;
import com.withiter.quhao.util.http.CommonHTTPRequest;
import com.withiter.quhao.util.tool.ParseJson;
import com.withiter.quhao.util.tool.ProgressDialogUtil;
import com.withiter.quhao.util.tool.QuhaoConstant;
import com.withiter.quhao.util.tool.SharedprefUtil;
import com.withiter.quhao.vo.LoginInfo;

public class PersonCenterActivity extends QuhaoBaseActivity {

	private final static String TAG = PersonCenterActivity.class.getName();

	private TextView nickName;
	private TextView mobile;
	private TextView jifen;
	private TextView value_qiandao;
	private TextView value_dianpin;

	private LoginInfo loginInfo;

	private LinearLayout signInLayout;

	private LinearLayout dianpingLayout;

	private LinearLayout currentPaiduiLayout;

	private LinearLayout historyPaiduiLayout;

	private LinearLayout creditCostLayout;
	
	private Button loginBtn;
	
	private Button regBtn;

	private final int UNLOCK_CLICK = 1000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.person_center_layout);
		super.onCreate(savedInstanceState);

		// bind menu button function
		btnCategory.setOnClickListener(goCategory(this));
		btnNearby.setOnClickListener(goNearby(this));
		btnPerson.setOnClickListener(goPersonCenter(this));
		btnMore.setOnClickListener(goMore(this));

		nickName = (TextView) findViewById(R.id.nickName);
		mobile = (TextView) findViewById(R.id.mobile);
		jifen = (TextView) findViewById(R.id.jifen);
		
		value_qiandao = (TextView) findViewById(R.id.value_qiandao);
		value_dianpin = (TextView) findViewById(R.id.value_dianpin);

		signInLayout = (LinearLayout) findViewById(R.id.signInLayout);
		dianpingLayout = (LinearLayout) findViewById(R.id.dianpingLayout);

		currentPaiduiLayout = (LinearLayout) findViewById(R.id.current_paidui_layout);
		historyPaiduiLayout = (LinearLayout) findViewById(R.id.history_paidui_layout);
		creditCostLayout = (LinearLayout) findViewById(R.id.credit_cost_layout);

		signInLayout.setOnClickListener(this);
		dianpingLayout.setOnClickListener(this);

		currentPaiduiLayout.setOnClickListener(this);
		historyPaiduiLayout.setOnClickListener(this);
		creditCostLayout.setOnClickListener(this);

		
		loginBtn = (Button) this.findViewById(R.id.login);
		regBtn = (Button) this.findViewById(R.id.register);
		
		loginBtn.setOnClickListener(this);
		regBtn.setOnClickListener(this);
		
		// if haven't login, prompt the login dialog
		String isAutoLogin = SharedprefUtil.get(this, QuhaoConstant.IS_AUTO_LOGIN, "false");
		if(!QHClientApplication.getInstance().isLogined && "false".equals(isAutoLogin)){

			
		}
		else if(!QHClientApplication.getInstance().isLogined && "true".equals(isAutoLogin))
		{
			initData();
		}else if(QHClientApplication.getInstance().isLogined){
			loginBtn.setVisibility(View.GONE);
			regBtn.setVisibility(View.GONE);
			AccountInfo account = QHClientApplication.getInstance().accessInfo;
			if(account != null){
				updateUIData(account);
			}
			else
			{
				loginBtn.setVisibility(View.VISIBLE);
				regBtn.setVisibility(View.VISIBLE);
			}
		}
		
	}
	
	private void updateUIData(AccountInfo account){
		mobile.setText(account.phone);
		nickName.setText(account.nickName);
		
		QuhaoLog.d(TAG, "account.jifen : " + account.jifen);
		nickName.setText(account.nickName);
		mobile.setText(account.phone);

		// TODO add jifen from backend
		jifen.setText(account.jifen);

		value_qiandao.setText(account.signIn);
		value_dianpin.setText(account.dianping);
		if ("true".equals(account.isSignIn)) {
			signInLayout.setEnabled(false);
		}
		
	}

	
	private void initData() {

		progressDialogUtil = new ProgressDialogUtil(this, R.string.empty, R.string.waitting, false);
		progressDialogUtil.showProgress();
		Thread accountThread = new Thread(accountRunnable);
		accountThread.start();
	}

	private Runnable accountRunnable = new Runnable() {

		@Override
		public void run() {
			Looper.prepare();
			String phone = SharedprefUtil.get(PersonCenterActivity.this, QuhaoConstant.PHONE, "");
			String password = SharedprefUtil.get(PersonCenterActivity.this, QuhaoConstant.PASSWORD, "");

			String decripedPwd = new DesUtils().decrypt(password);

			if (StringUtils.isNotNull(phone) && StringUtils.isNotNull(decripedPwd)) {
				String url = "AccountController/login?phone=" + phone.trim() + "&password=" + password.trim();
				QuhaoLog.i(TAG, "the login url is : " + url);
				try {
					String result = CommonHTTPRequest.get(url);
					QuhaoLog.i(TAG, result);
					if (StringUtils.isNull(result)) {
						unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
					} else {
						LoginInfo loginInfo = ParseJson.getLoginInfo(result);
						AccountInfo account = new AccountInfo();

						account.build(loginInfo);
						QuhaoLog.i(TAG, "account.msg : " + account.msg);
						
						if (account.msg.equals("fail")) {
							QuhaoLog.i(TAG, "auto login failed.");
							SharedprefUtil.put(PersonCenterActivity.this, QuhaoConstant.IS_LOGIN, "false");
							Toast.makeText(PersonCenterActivity.this, "自动登录失败，请检查用户名密码是否正确", Toast.LENGTH_LONG).show();
							return;
						}
						if (account.msg.equals("success")) {
							QuhaoLog.i(TAG, "auto login successfully.");
							SharedprefUtil.put(PersonCenterActivity.this, QuhaoConstant.IS_LOGIN, "true");
							QHClientApplication.getInstance().accessInfo = account;
							QHClientApplication.getInstance().isLogined = true;
							accountUpdateHandler.obtainMessage(200, account).sendToTarget();
						}
						
						
//						loginInfo = ParseJson.getLoginInfo(result);
//						AccountInfo account = new AccountInfo();
						// account.setUserId("1");
//						account.build(loginInfo);
//						SharedprefUtil.put(PersonCenterActivity.this, QuhaoConstant.IS_LOGIN, "true");

//						QHClientApplication.getInstance().accessInfo = account;
//						QuhaoLog.i(TAG, loginInfo.msg);
//						accountUpdateHandler.obtainMessage(200, loginInfo).sendToTarget();

					}
				} catch (Exception e) {
					e.printStackTrace();
					SharedprefUtil.put(PersonCenterActivity.this, QuhaoConstant.IS_LOGIN, "false");
					Toast.makeText(PersonCenterActivity.this, "登陆失败", Toast.LENGTH_LONG).show();
				} finally {
					progressDialogUtil.closeProgress();
					Looper.loop();
				}

			} else {
				progressDialogUtil.closeProgress();
				SharedprefUtil.put(PersonCenterActivity.this, QuhaoConstant.IS_LOGIN, "false");
				QuhaoLog.i(TAG, "accessInfo is null");
				Looper.loop();
			}
		}
	};

	private Handler accountUpdateHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 200) {
				super.handleMessage(msg);
				if (loginInfo.msg.equals("fail")) {

					SharedprefUtil.put(PersonCenterActivity.this, QuhaoConstant.IS_LOGIN, "false");
					Toast.makeText(PersonCenterActivity.this, "登陆失败", Toast.LENGTH_LONG).show();
					return;
				}
				if (loginInfo.msg.equals("success")) {
					nickName.setText(loginInfo.nickName);
					mobile.setText(loginInfo.phone);

					// TODO add jifen from backend
					jifen.setText(loginInfo.jifen);

					value_qiandao.setText(loginInfo.signIn);
					value_dianpin.setText(loginInfo.dianping);
					if ("true".equals(loginInfo.isSignIn)) {
						signInLayout.setEnabled(false);
					}
				}

			}
		}
	};

	
	
	@Override
	public void onClick(View v) {
		// // 隐藏软键盘
		// InputMethodManager m = (InputMethodManager)
		// getSystemService(Context.INPUT_METHOD_SERVICE);
		// if (m != null) {
		// m.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(),
		// InputMethodManager.HIDE_NOT_ALWAYS);
		// }
		// // 已经点过，直接返回
		if (isClick) {
			return;
		}

		// // 设置已点击标志，避免快速重复点击
		isClick = true;
		// // 解锁
		progressDialogUtil = new ProgressDialogUtil(this, R.string.empty, R.string.waitting, false);
		progressDialogUtil.showProgress();
		switch (v.getId()) {
		case R.id.login:
			progressDialogUtil.closeProgress();
			unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			Intent intent = new Intent(this, LoginActivity.class);
			intent.putExtra("activityName", this.getClass().getName());
			intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//			overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
			startActivity(intent);
			break;
		case R.id.register:
			progressDialogUtil.closeProgress();
			unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			Intent intentReg = new Intent(this, RegisterActivity.class);
			intentReg.putExtra("activityName", this.getClass().getName());
			intentReg.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//			overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
			startActivity(intentReg);
			break;
		case R.id.signInLayout:
			if(QHClientApplication.getInstance().isLogined)
			{
				String accountId = SharedprefUtil.get(this, QuhaoConstant.ACCOUNT_ID, "false");
				try {
					String result = CommonHTTPRequest.get("AccountController/signIn?accountId=" + accountId);
					QuhaoLog.i(TAG, result);
					if (StringUtils.isNull(result)) {
					} else {
						loginInfo = ParseJson.getLoginInfo(result);
						AccountInfo account = new AccountInfo();
//						account.setUserId("1");
						account.build(loginInfo);
						SharedprefUtil.put(PersonCenterActivity.this, QuhaoConstant.IS_LOGIN, "true");
						QHClientApplication.getInstance().accessInfo = account;
						QuhaoLog.i(TAG, loginInfo.msg);

						if (loginInfo.msg.equals("fail")) {

							SharedprefUtil.put(PersonCenterActivity.this, QuhaoConstant.IS_LOGIN, "false");
							Toast.makeText(PersonCenterActivity.this, "签到失败", Toast.LENGTH_LONG).show();
							return;
						}
						if (loginInfo.msg.equals("success")) {
							nickName.setText(loginInfo.nickName);
							mobile.setText(loginInfo.phone);

							// TODO add jifen from backend
							jifen.setText(loginInfo.jifen);

							value_qiandao.setText(loginInfo.signIn);
							value_dianpin.setText(loginInfo.dianping);
							signInLayout.setEnabled(false);
						}

					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					progressDialogUtil.closeProgress();
					unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
					
				}

			}
			else
			{
				progressDialogUtil.closeProgress();
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				AlertDialog.Builder builder = new Builder(this);
				builder.setMessage("请先登录");
				builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						
						dialog.dismiss();
						
					}
				});
				builder.create().show();
			}
			break;
		case R.id.dianpingLayout:
			if(QHClientApplication.getInstance().isLogined)
			{
				progressDialogUtil.closeProgress();
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			}else
			{
				
				progressDialogUtil.closeProgress();
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				AlertDialog.Builder builder = new Builder(this);
				builder.setMessage("请先登录");
				builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						
						dialog.dismiss();
						
					}
				});
				builder.create().show();
			}
			
			break;
		case R.id.current_paidui_layout:
			if(QHClientApplication.getInstance().isLogined)
			{
				progressDialogUtil.closeProgress();
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				Intent intentCurrent = new Intent();
				intentCurrent.putExtra("queryCondition", "current");
				intentCurrent.setClass(this, PaiduiListActivity.class);
				startActivity(intentCurrent);
			}
			else
			{
				progressDialogUtil.closeProgress();
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				AlertDialog.Builder builder = new Builder(this);
				builder.setMessage("请先登录");
				builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						
						dialog.dismiss();
						
					}
				});
				builder.create().show();
			}
			break;
		case R.id.history_paidui_layout:
			if(QHClientApplication.getInstance().isLogined)
			{
				progressDialogUtil.closeProgress();
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				Intent intentHistory = new Intent();
				intentHistory.putExtra("queryCondition", "history");
				intentHistory.setClass(this, PaiduiListActivity.class);
				startActivity(intentHistory);
			}
			else
			{
				progressDialogUtil.closeProgress();
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				AlertDialog.Builder builder = new Builder(this);
				builder.setMessage("请先登录");
				builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						
						dialog.dismiss();
						
					}
				});
				builder.create().show();
			}
			
			break;
		case R.id.credit_cost_layout:
			if(QHClientApplication.getInstance().isLogined)
			{
				progressDialogUtil.closeProgress();
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				Intent intentCredit = new Intent();
				intentCredit.setClass(this, CreditCostListActivity.class);
				startActivity(intentCredit);
			}
			else
			{
				progressDialogUtil.closeProgress();
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				AlertDialog.Builder builder = new Builder(this);
				builder.setMessage("请先登录");
				builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						
						dialog.dismiss();
						
					}
				});
				builder.create().show();
			}
			break;
		default:
			progressDialogUtil.closeProgress();
			unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			break;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {

		return false;
	}

	private void displayAccountInfo() {
		// TextView nickName = (TextView) findViewById("nickName");
	}
}
