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
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

public class LoginActivity extends QuhaoBaseActivity {

	private final static String TAG = LoginActivity.class.getName();
	private TextView pannelLoginName;
	private EditText loginNameText;
	private EditText passwordText;
	private Button btnClose;
	private Button btnLogin;
	private Button forgetPasswordBtn;
	private TextView loginResult;
	private final int UNLOCK_CLICK = 1000;
	private ProgressDialogUtil progressLogin;

	private ImageView isAutoLoginView;
	private String isAutoLogin = "false";

	private String activityName;

	private Map<String, Object> transfortParams = new HashMap<String, Object>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.login_layout);
		super.onCreate(savedInstanceState);

		// TODO : There is a issue here
		
		activityName = getIntent().getStringExtra("activityName");
		if (StringUtils.isNotNull(activityName)) {
			if (MerchantDetailActivity.class.getName().equals(activityName)) {
				String merchantId = getIntent().getStringExtra("merchantId");
				transfortParams.put("merchantId", merchantId);
			}
		}
		loginResult = (TextView) this.findViewById(R.id.person_center_login_result);

		isAutoLoginView = (ImageView) findViewById(R.id.isAutoLogin);
		isAutoLoginView.setOnClickListener(this);
		isAutoLogin = SharedprefUtil.get(this, QuhaoConstant.IS_AUTO_LOGIN, "false");

		if ("true".equals(isAutoLogin)) {
			isAutoLoginView.setImageResource(R.drawable.checkbox_checked);
		} else {
			isAutoLoginView.setImageResource(R.drawable.checkbox_unchecked);
		}

		// phone label
		pannelLoginName = (TextView) findViewById(R.id.pannel_login_name);
		// phone text filed
		String phone = SharedprefUtil.get(LoginActivity.this, QuhaoConstant.PHONE, "");
		loginNameText = (EditText) findViewById(R.id.login_name);
		loginNameText.setText(phone);
		
		passwordText = (EditText) findViewById(R.id.edit_pass);

		btnClose = (Button) findViewById(R.id.close);
		btnLogin = (Button) findViewById(R.id.login);
		forgetPasswordBtn = (Button) findViewById(R.id.forgetPassword);

		btnClose.setOnClickListener(this);
		btnLogin.setOnClickListener(this);
		forgetPasswordBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// 隐藏软键盘
		
		InputMethodManager m = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		if (m != null) {
//			if(this.getCurrentFocus()!=null && this.getCurrentFocus().getWindowToken() != null)
//			{
//				m.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
//			}
			
			//R.id.login
			//m.hideSoftInputFromWindow(passwordText.getWindowToken(), 0);
			//m.hideSoftInputFromWindow(loginNameText.getWindowToken(), 0);
			if(m.isActive()){
				m.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS);
			}
			
			
		}
		
//		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
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
		case R.id.isAutoLogin:
			if ("true".equals(isAutoLogin)) {
				isAutoLogin = "false";
				isAutoLoginView.setImageResource(R.drawable.checkbox_unchecked);
			} else {
				isAutoLogin = "true";
				isAutoLoginView.setImageResource(R.drawable.checkbox_checked);
			}
			unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			break;
		case R.id.login:
			QuhaoLog.i(TAG, "login clcick");
			progressLogin = new ProgressDialogUtil(this, R.string.empty, R.string.waitting, false);
			progressLogin.showProgress();

			String url = "AccountController/login?phone=" + loginNameText.getText().toString().trim() + "&email=&password=" + passwordText.getText().toString();
			QuhaoLog.i(TAG, "the login url is : " + url);
			try {
				String result = CommonHTTPRequest.post(url);
				QuhaoLog.i(TAG, result);
				if (StringUtils.isNull(result)) {
					Toast.makeText(this, "网络不是很好，登陆失败，稍等片刻就好", Toast.LENGTH_LONG).show();
					unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				} else {
					LoginInfo loginInfo = ParseJson.getLoginInfo(result);
					AccountInfo account = new AccountInfo();

					account.build(loginInfo);
					account.isAuto = isAutoLogin;
					QuhaoLog.i(TAG, "account.msg : " + account.msg);
					
					if (account.msg.equals("fail")) {
						loginResult.setText("用户名或密码错误，登陆失败");
						passwordText.setText("");
						return;
					}
					if (account.msg.equals("success")) {
						loginResult.setText("登陆成功");
						SharedprefUtil.put(this, QuhaoConstant.ACCOUNT_ID, loginInfo.accountId);
						SharedprefUtil.put(this, QuhaoConstant.PHONE, account.phone);
						
						String HexedPwd = new DesUtils().encrypt(passwordText.getText().toString());
						SharedprefUtil.put(this, QuhaoConstant.PASSWORD, HexedPwd);
						
						SharedprefUtil.put(this, QuhaoConstant.IS_AUTO_LOGIN, isAutoLogin.trim());
						
						// login state will store in QHClientApplication
						QHClientApplication.getInstance().accountInfo = account;
						QHClientApplication.getInstance().isLogined = true;

						loginUpdateHandler.obtainMessage(200, account).sendToTarget();
						return;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			} finally {
				progressLogin.closeProgress();
			}
			break;
		case R.id.forgetPassword:
			unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			Intent intent = new Intent(this, ForgetPasswordActivity.class);
			startActivity(intent);
			System.gc();
			finish();
			break;
		default:
			break;
		}
	}

	private Handler loginUpdateHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 200) {
				super.handleMessage(msg);

				QuhaoLog.d(TAG, "login call back to " + activityName);

				if (StringUtils.isNotNull(activityName)) {
					Intent intent = new Intent();
					if (MerchantDetailActivity.class.getName().equals(activityName)) {
						intent.putExtra("merchantId", (String) transfortParams.get("merchantId"));
						intent.setClass(LoginActivity.this, GetNumberActivity.class);
					} else if (PersonCenterActivity.class.getName().equals(activityName)) {
						intent.setClass(LoginActivity.this, PersonCenterActivity.class);
						Bundle mBundle = new Bundle();  
						
						QuhaoLog.d(TAG, "(AccountInfo)msg.obj : " + ((AccountInfo)msg.obj).password);
				        mBundle.putSerializable("account", (AccountInfo)msg.obj);  
				        intent.putExtras(mBundle);  
					} else if ("com.withiter.quhao.activity.MoreActivity".equals(activityName)) {
						intent.setClass(LoginActivity.this, MoreActivity.class);
					} else if ("com.withiter.quhao.activity.RegisterActivity".equals(activityName)) {
						intent.setClass(LoginActivity.this, PersonCenterActivity.class);
						Bundle mBundle = new Bundle();  
						
						QuhaoLog.d(TAG, "(AccountInfo)msg.obj : " + ((AccountInfo)msg.obj).password);
				        mBundle.putSerializable("account", (AccountInfo)msg.obj);  
				        intent.putExtras(mBundle);  
					} else {
						intent.setClass(LoginActivity.this, PersonCenterActivity.class);
					}
					startActivity(intent);
				}
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				finish();
			}
		}
	};

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return false;
	}
}
