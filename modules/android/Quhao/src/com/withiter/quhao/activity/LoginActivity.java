package com.withiter.quhao.activity;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.withiter.quhao.QHClientApplication;
import com.withiter.quhao.R;
import com.withiter.quhao.domain.AccountInfo;
import com.withiter.quhao.util.QuhaoLog;
import com.withiter.quhao.util.StringUtils;
import com.withiter.quhao.util.http.CommonHTTPRequest;
import com.withiter.quhao.util.tool.ParseJson;
import com.withiter.quhao.util.tool.ProgressDialogUtil;
import com.withiter.quhao.vo.LoginInfo;

public class LoginActivity extends AppStoreActivity {

	private final static String TAG = LoginActivity.class.getName();
	private RadioGroup radioGroup;
	private RadioButton radioPhone;
	private RadioButton radioEmail;
	private TextView pannelLoginName;
	private EditText loginNameText;
	private EditText passwordText;
	private Button btnClose;
	private Button btnLogin;
	private Button btnRegister;
	private final int UNLOCK_CLICK = 1000;
	private ProgressDialogUtil progressLogin;

	private String activityName;

	private Map<String, Object> transfortParams = new HashMap<String, Object>();

	private Handler loginUpdateHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 200) {
				super.handleMessage(msg);

				// intent.pute
				if (StringUtils.isNotNull(activityName)) {
					Intent intent = new Intent();
					if ("com.withiter.quhao.activity.MerchantDetailActivity"
							.equals(activityName)) {
						intent.putExtra("merchantId",
								(String) transfortParams.get("merchantId"));
						intent.setClass(LoginActivity.this,
								GetNumberActivity.class);
					} else if ("com.withiter.quhao.activity.PersonCenterActivity"
							.equals(activityName)) {
						intent.setClass(LoginActivity.this,
								PersonCenterActivity.class);
					} else {
						intent.setClass(LoginActivity.this,
								PersonCenterActivity.class);
					}
					startActivity(intent);
				}
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				finish();
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.login_layout);
		super.onCreate(savedInstanceState);

		activityName = getIntent().getStringExtra("activityName");
		if (StringUtils.isNotNull(activityName)) {
			if ("com.withiter.quhao.activity.MerchantDetailActivity"
					.equals(activityName)) {
				String merchantId = getIntent().getStringExtra("merchantId");
				transfortParams.put("merchantId", merchantId);
			}
		}
		radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
		radioPhone = (RadioButton) findViewById(R.id.radioPhone);
		radioEmail = (RadioButton) findViewById(R.id.radioEmail);
		pannelLoginName = (TextView) findViewById(R.id.pannel_login_name);
		loginNameText = (EditText) findViewById(R.id.login_name);
		passwordText = (EditText) findViewById(R.id.edit_pass);
		btnClose = (Button) findViewById(R.id.close);
		btnClose.setOnClickListener(this);
		btnLogin = (Button) findViewById(R.id.login);
		btnLogin.setOnClickListener(this);
		btnRegister = (Button) findViewById(R.id.zhuce);
		btnRegister.setOnClickListener(this);
		
		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (checkedId == radioPhone.getId()) {
					pannelLoginName.setText(R.string.radioPhone);
				} else if (checkedId == radioEmail.getId()) {
					pannelLoginName.setText(R.string.radioEmail);
				}

			}
		});

	}

	@Override
	public void onClick(View v) {
		// 隐藏软键盘
		InputMethodManager m = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		if (m != null) {
			m.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(),
					InputMethodManager.HIDE_NOT_ALWAYS);
		}
		// 已经点过，直接返回
		if (isClick) {
			return;
		}

		// 设置已点击标志，避免快速重复点击
		isClick = true;
		// 解锁
		unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
		switch (v.getId()) {
		case R.id.close:
			System.gc();
			finish();
			break;
		case R.id.login:
			progressLogin = new ProgressDialogUtil(this, R.string.empty,
					R.string.querying, false);
			progressLogin.showProgress();
			String url = "AccountController/login?";
			if (radioPhone.getId() == radioGroup.getCheckedRadioButtonId()) {
				url = url + "phone="
						+ loginNameText.getText().toString().trim() + "&";
			} else {
				url = url + "phone=" + "&";
			}
			if (radioEmail.getId() == radioGroup.getCheckedRadioButtonId()) {
				url = url + "email="
						+ loginNameText.getText().toString().trim() + "&";
			} else {
				url = url + "email=" + "&";
			}
			url = url + "password=" + passwordText.getText().toString();
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
					QHClientApplication.getInstance().accessInfo = account;
					QHClientApplication.getInstance().isLogined = true;
					loginUpdateHandler.obtainMessage(200, account)
							.sendToTarget();
				}
			} catch (Exception e) {
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				e.printStackTrace();
			} finally {
				progressLogin.closeProgress();
			}
			break;
		case R.id.zhuce:
			Intent intent = new Intent(this, RegisterActivity.class);
			startActivity(intent);
			System.gc();
			finish();
			break;
		default:
			break;
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		return false;
	}

}
