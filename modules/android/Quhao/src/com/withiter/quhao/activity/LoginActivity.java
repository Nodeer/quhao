package com.withiter.quhao.activity;

import java.util.ArrayList;

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
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.withiter.quhao.QHClientApplication;
import com.withiter.quhao.R;
import com.withiter.quhao.adapter.MerchantAdapter;
import com.withiter.quhao.domain.AccountInfo;
import com.withiter.quhao.util.db.AccountInfoHelper;
import com.withiter.quhao.util.http.CommonHTTPRequest;
import com.withiter.quhao.util.tool.CommonTool;
import com.withiter.quhao.util.tool.ParseJson;
import com.withiter.quhao.util.tool.ProgressDialogUtil;
import com.withiter.quhao.vo.LoginInfo;
import com.withiter.quhao.vo.TopMerchant;

public class LoginActivity extends AppStoreActivity {

	private final static String LOG_TAG = LoginActivity.class.getName();
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
	private boolean isClick = false;
	private ProgressDialogUtil progressLogin;

	/**
	 * handler处理 解锁的时候可能会关闭其他的等待提示框
	 */
	private Handler unlockHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == UNLOCK_CLICK) {
				// 解锁
				isClick = false;
			}
		}
	};

	private Handler loginUpdateHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 200) {
				super.handleMessage(msg);
				
				Intent intent = new Intent();
				AccountInfo account = (AccountInfo) msg.obj;
				Bundle bundle = new Bundle();
				bundle.putSerializable("account", account);
				intent.putExtras(bundle);
				//intent.pute
				intent.setClass(LoginActivity.this, PersonCenterActivity.class);
				startActivity(intent);
				
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
			if(radioPhone.getId() == radioGroup.getCheckedRadioButtonId())
			{
				url = url + "phone="+loginNameText.getText().toString().trim() + "&";
			}
			else
			{
				url = url + "phone=" + "&";
			}
			if(radioEmail.getId() == radioGroup.getCheckedRadioButtonId())
			{
				url = url + "email="+loginNameText.getText().toString().trim() + "&";
			}
			else
			{
				url = url + "email=" + "&";
			}
			url = url + "password=" + passwordText.getText().toString();
			try
			{
				String result = CommonHTTPRequest.get(url);
				if(CommonTool.isNull(result))
				{
					unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				}
				else
				{
					LoginInfo loginInfo = ParseJson.getLoginInfo(result);
					AccountInfo account = new AccountInfo();
					account.setPhone(loginInfo.phone);
					account.setEmail(loginInfo.email);
					account.setPassword(loginInfo.password);
					account.setNickName(loginInfo.nickName);
					account.setBirthday(loginInfo.birthday);
					account.setUserImage(loginInfo.userImage);
					account.setEnable(loginInfo.enable);
					account.setMobileOS(loginInfo.mobileOS);
					account.setLastLogin(loginInfo.lastLogin);
					QHClientApplication.getInstance().accessInfo = account;
					QHClientApplication.getInstance().isLogined = true;
					loginUpdateHandler.obtainMessage(200, account)
							.sendToTarget();
				}
			} catch (Exception e)
			{
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				e.printStackTrace();
			}
			finally
			{
				progressLogin.closeProgress();
			}
//			if (!CommonTool.isNameAdressFormat(email)) {
//				CommonTool.hintDialog(LoginActivity.this,
//						getString(R.string.email_warning));
//				return;
//			}
//			if ("".equals(password)) {
//				CommonTool.hintDialog(LoginActivity.this,
//						getString(R.string.pass_warning));
//				return;
//			}

			// http
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