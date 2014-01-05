package com.withiter.quhao.activity;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
	private Button editPasswordBtn;

	private final int UNLOCK_CLICK = 1000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.person_center_layout);
		super.onCreate(savedInstanceState);

		QuhaoLog.d(TAG, "onCreate");

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

		editPasswordBtn = (Button) this.findViewById(R.id.editPassword);
		loginBtn = (Button) this.findViewById(R.id.login);
		regBtn = (Button) this.findViewById(R.id.register);

		loginBtn.setOnClickListener(this);
		regBtn.setOnClickListener(this);
		editPasswordBtn.setOnClickListener(this);

		// other activity will invoke this method
		refreshUI();
	}

	public void refreshUI() {
		progressDialogUtil = new ProgressDialogUtil(this, R.string.empty, R.string.waitting, false);
		progressDialogUtil.showProgress();
		// if haven't login, prompt the login dialog
		// no need to check auto login from SharedPreference
		// because when APP start up, the action had been performed
		QuhaoLog.d(TAG, "QHClientApplication.getInstance().isLogined : " + QHClientApplication.getInstance().isLogined);
		if (QHClientApplication.getInstance().isLogined) {
			AccountInfo account = QHClientApplication.getInstance().accountInfo;
			if (account != null) {
				loginBtn.setVisibility(View.GONE);
				regBtn.setVisibility(View.GONE);
				editPasswordBtn.setVisibility(View.VISIBLE);
				updateUIData(account);
			} else {
				loginBtn.setVisibility(View.VISIBLE);
				regBtn.setVisibility(View.VISIBLE);
				progressDialogUtil.closeProgress();
			}
		} else {
			progressDialogUtil.closeProgress();
		}

	}

	// update UI according to the account object
	private void updateUIData(AccountInfo account) {
		mobile.setText(account.phone);
		
		nickName.setText(account.nickName);
		if(StringUtils.isNull(account.nickName))
		{
			nickName.setText(R.string.noname);
		}

		QuhaoLog.d(TAG, "account.jifen : " + account.jifen);
		mobile.setText(account.phone);
		jifen.setText(account.jifen);

		value_qiandao.setText(account.signIn);
		value_dianpin.setText(account.dianping);
		if ("true".equals(account.isSignIn)) {
			signInLayout.setEnabled(false);
		}
		progressDialogUtil.closeProgress();
	}

	private Handler accountUpdateHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 200) {
				super.handleMessage(msg);
				if (loginInfo.msg.equals("fail")) {
					QHClientApplication.getInstance().isLogined = false;
//					SharedprefUtil.put(PersonCenterActivity.this, QuhaoConstant.IS_LOGIN, "false");
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
		// 已经点过，直接返回
		if (isClick) {
			return;
		}

		// 设置已点击标志，避免快速重复点击
		isClick = true;

		// 解锁
		progressDialogUtil = new ProgressDialogUtil(this, R.string.empty, R.string.waitting, false);
		progressDialogUtil.showProgress();

		switch (v.getId()) {
		case R.id.login:
			progressDialogUtil.closeProgress();
			unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			Intent intent = new Intent(this, LoginActivity.class);
			intent.putExtra("activityName", this.getClass().getName());
			intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivity(intent);
			break;
		case R.id.register:
			progressDialogUtil.closeProgress();
			unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			Intent intentReg = new Intent(this, RegisterActivity.class);
			intentReg.putExtra("activityName", this.getClass().getName());
			intentReg.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivity(intentReg);
			break;
		case R.id.signInLayout:
			if (QHClientApplication.getInstance().isLogined) {
				String accountId = SharedprefUtil.get(this, QuhaoConstant.ACCOUNT_ID, "false");
				try {
					String result = CommonHTTPRequest.get("AccountController/signIn?accountId=" + accountId);
					QuhaoLog.i(TAG, result);
					if (StringUtils.isNull(result)) {
					} else {
						loginInfo = ParseJson.getLoginInfo(result);
						AccountInfo account = new AccountInfo();
						account.build(loginInfo);
//						SharedprefUtil.put(PersonCenterActivity.this, QuhaoConstant.IS_LOGIN, "true");
						QHClientApplication.getInstance().accountInfo = account;
						QHClientApplication.getInstance().isLogined = true;

						QuhaoLog.i(TAG, loginInfo.msg);
						if (loginInfo.msg.equals("fail")) {
//							SharedprefUtil.put(PersonCenterActivity.this, QuhaoConstant.IS_LOGIN, "false");
							QHClientApplication.getInstance().isLogined = false;
							Toast.makeText(PersonCenterActivity.this, "签到失败", Toast.LENGTH_LONG).show();
							return;
						}
						if (loginInfo.msg.equals("success")) {
							QHClientApplication.getInstance().isLogined = false;
							
							nickName.setText(loginInfo.nickName);
							mobile.setText(loginInfo.phone);
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
			} else {
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
			if (QHClientApplication.getInstance().isLogined) {
				progressDialogUtil.closeProgress();
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				
				Intent intentComment = new Intent();
				intentComment.putExtra("accountId", QHClientApplication.getInstance().accountInfo.accountId);
				intentComment.setClass(this, CommentsAccountActivity.class);
				startActivity(intentComment);
				overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
				
			} else {
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
			if (QHClientApplication.getInstance().isLogined) {
				progressDialogUtil.closeProgress();
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);

				Intent intentCurrent = new Intent();
				intentCurrent.putExtra("queryCondition", "current");
				intentCurrent.setClass(this, QuhaoStatesActivity.class);
				startActivity(intentCurrent);
				overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
			} else {
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
			if (QHClientApplication.getInstance().isLogined) {
				progressDialogUtil.closeProgress();
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				Intent intentHistory = new Intent();
				intentHistory.putExtra("queryCondition", "history");
				intentHistory.setClass(this, QuhaoStatesActivity.class);
				startActivity(intentHistory);
				overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
			} else {
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
			if (QHClientApplication.getInstance().isLogined) {
				progressDialogUtil.closeProgress();
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				Intent intentCredit = new Intent();
				intentCredit.setClass(this, CreditCostListActivity.class);
				startActivity(intentCredit);
				overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
			} else {
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
		case R.id.editPassword:
			if (QHClientApplication.getInstance().isLogined) {
				progressDialogUtil.closeProgress();
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				Intent intentCredit = new Intent();
				intentCredit.setClass(this, CreditCostListActivity.class);
				startActivity(intentCredit);
				overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
			} else {
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
}
