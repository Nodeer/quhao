package com.withiter.quhao.activity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.withiter.quhao.QHClientApplication;
import com.withiter.quhao.R;
import com.withiter.quhao.domain.AccountInfo;
import com.withiter.quhao.util.QuhaoLog;
import com.withiter.quhao.util.StringUtils;
import com.withiter.quhao.util.db.AccountInfoHelper;
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
	private TextView value_zhaopian;

	private LoginInfo loginInfo;

	private final int UNLOCK_CLICK = 1000;

	AlertDialog ad;

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
		value_zhaopian = (TextView) findViewById(R.id.value_zhaopian);

		initData();
		/*
		String isLogin = SharedprefUtil.get(this, QuhaoConstant.IS_LOGIN,
				"false");

		
		// check already login or not
		if (StringUtils.isNull(isLogin) || "false".equalsIgnoreCase(isLogin)) {
			// TODO add not login business here
			QuhaoLog.i(TAG, "login check: not login");
			LayoutInflater inflater = (LayoutInflater) getApplicationContext()
					.getSystemService(LAYOUT_INFLATER_SERVICE);
			View view = inflater.inflate(R.layout.login_layout, null);

			ad = new AlertDialog.Builder(PersonCenterActivity.this)
					.setView(view).setTitle("账号登陆").show();

			loginResult = (TextView) view
					.findViewById(R.id.person_center_login_result);
			loginNameText = (EditText) view.findViewById(R.id.login_name);
			passwordText = (EditText) view.findViewById(R.id.edit_pass);
			isAutoLoginView = (ImageView) view.findViewById(R.id.isAutoLogin);
			btnClose = (Button) view.findViewById(R.id.close);
			btnLogin = (Button) view.findViewById(R.id.login);
			btnRegister = (Button) view.findViewById(R.id.zhuce);
			isAutoLogin = SharedprefUtil.get(this, QuhaoConstant.IS_AUTO_LOGIN,
					"false");
			if ("true".equals(isAutoLogin)) {
				isAutoLoginView.setImageResource(R.drawable.checkbox_checked);
			} else {
				isAutoLoginView.setImageResource(R.drawable.checkbox_unchecked);
			}

			btnClose.setOnClickListener(this);
			btnLogin.setOnClickListener(this);
			btnRegister.setOnClickListener(this);
			isAutoLoginView.setOnClickListener(this);

		} else {
			// TODO add already login business here , should refresh the
			// information for dianping , jifen, qiandao and any other
			// informations.
			QuhaoLog.i(TAG, "login check: login");

		}

		*/
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
			
			String phone = SharedprefUtil.get(PersonCenterActivity.this, QuhaoConstant.PHONE, "");
			String password = SharedprefUtil.get(PersonCenterActivity.this, QuhaoConstant.PASSWORD, "");
			if (StringUtils.isNotNull(phone) && StringUtils.isNotNull(password)) {
				String url = "AccountController/login?";
				url = url + "phone=" + phone.trim() + "&";
				url = url + "password=" + password.trim();
				QuhaoLog.i(TAG, "the login url is : " + url);
				try {
					String result = CommonHTTPRequest.get(url);
					QuhaoLog.i(TAG, result);
					if (StringUtils.isNull(result)) {
					} else {
						loginInfo = ParseJson.getLoginInfo(result);
						AccountInfo account = new AccountInfo();
						account.setUserId("1");
						account.build(loginInfo);
						AccountInfoHelper accountDBHelper = new AccountInfoHelper(
								PersonCenterActivity.this);
						accountDBHelper.open();
						accountDBHelper.saveAccountInfo(account);
						SharedprefUtil
								.put(PersonCenterActivity.this, QuhaoConstant.IS_LOGIN, "true");
						accountDBHelper.close();
						QHClientApplication.getInstance().accessInfo = account;
						QuhaoLog.i(TAG, loginInfo.msg);
						accountUpdateHandler.obtainMessage(200, loginInfo).sendToTarget();
						
					}
				} catch (Exception e) {
					e.printStackTrace();
					SharedprefUtil.put(PersonCenterActivity.this, QuhaoConstant.IS_LOGIN,"false");
					Toast.makeText(PersonCenterActivity.this, "登陆失败", Toast.LENGTH_LONG).show();
				} finally {
					progressDialogUtil.closeProgress();
				}
				
			} else {
				progressDialogUtil.closeProgress();
				SharedprefUtil.put(PersonCenterActivity.this, QuhaoConstant.IS_LOGIN, "false");
				QuhaoLog.i(TAG, "accessInfo is null");
			}
		}
	};
	
	private Handler accountUpdateHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 200) {
				super.handleMessage(msg);
				if (loginInfo.msg.equals("fail")) {
					
					SharedprefUtil.put(PersonCenterActivity.this, QuhaoConstant.IS_LOGIN,"false");
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
					value_zhaopian.setText(loginInfo.zhaopian);
					
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

		switch (v.getId()) {
		
		default:
			unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			break;
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		
		return false;
	}

	private void displayAccountInfo() {
		// TextView nickName = (TextView) findViewById("nickName");
	}
}
