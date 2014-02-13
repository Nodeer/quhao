package com.withiter.quhao.activity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.withiter.quhao.QHClientApplication;
import com.withiter.quhao.R;
import com.withiter.quhao.util.StringUtils;
import com.withiter.quhao.util.encrypt.DesUtils;
import com.withiter.quhao.util.http.CommonHTTPRequest;
import com.withiter.quhao.util.tool.ParseJson;
import com.withiter.quhao.util.tool.ProgressDialogUtil;
import com.withiter.quhao.util.tool.QuhaoConstant;
import com.withiter.quhao.util.tool.SharedprefUtil;
import com.withiter.quhao.vo.SignupVO;

public class ForgetPasswordActivity extends QuhaoBaseActivity {

	private EditText loginNameText;

	private EditText verifyCodeText;

	private EditText passwordText;

	private EditText password2Text;

	private Button verifyCodeBtn;

	private Button submitBtn;

	private Button colseBtn;

	private String loginName;
	private String verifyCode;
	private String password;
	private String password2;
	private TextView repassResult;

	private SignupVO signup;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.forget_password_layout);
		super.onCreate(savedInstanceState);

		loginNameText = (EditText) this.findViewById(R.id.login_name);
		verifyCodeText = (EditText) this.findViewById(R.id.verify_code);
		passwordText = (EditText) this.findViewById(R.id.new_pass);
		password2Text = (EditText) this.findViewById(R.id.new_pass2);
		verifyCodeBtn = (Button) this.findViewById(R.id.verify_code_button);
		submitBtn = (Button) this.findViewById(R.id.submit);
		colseBtn = (Button) this.findViewById(R.id.close);
		
		repassResult = (TextView) this.findViewById(R.id.repassword_result);

		colseBtn.setOnClickListener(this);
		submitBtn.setOnClickListener(this);
		verifyCodeBtn.setOnClickListener(this);
		String phone = SharedprefUtil.get(this, QuhaoConstant.PHONE, "");
		loginNameText.setText(phone);
	}

	private Handler verifyUpdateHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 200) {
				super.handleMessage(msg);
				progressDialogUtil.closeProgress();
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				if (null == signup) {
					Toast.makeText(ForgetPasswordActivity.this, "发送验证码失败， 请重按验证码按钮。", Toast.LENGTH_LONG).show();
				}
				else
				{
					if("1".equals(signup.errorKey))
					{
						Toast.makeText(ForgetPasswordActivity.this, "发送验证码成功， 稍后请输入验证码，24小时有效。", Toast.LENGTH_LONG).show();
					}
					else
					{
						Toast.makeText(ForgetPasswordActivity.this, signup.errorText, Toast.LENGTH_LONG).show();
					}
				}
				
			}
		}
	};
	
	private Handler signupUpdateHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 200) {
				super.handleMessage(msg);
				progressDialogUtil.closeProgress();
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				if (null == signup) {
					Toast.makeText(ForgetPasswordActivity.this, "注册失败， 请重新注册。", Toast.LENGTH_LONG).show();
				}
				else
				{
					if("1".equals(signup.errorKey))
					{
						Toast.makeText(ForgetPasswordActivity.this, "注册成功。", Toast.LENGTH_LONG).show();
						SharedprefUtil.remove(ForgetPasswordActivity.this, QuhaoConstant.ACCOUNT_ID);
						SharedprefUtil.remove(ForgetPasswordActivity.this, QuhaoConstant.IS_AUTO_LOGIN);
//						SharedprefUtil.remove(ForgetPasswordActivity.this, QuhaoConstant.IS_LOGIN);
						SharedprefUtil.put(ForgetPasswordActivity.this, QuhaoConstant.PHONE, loginName);
						
						String HexedPwd = new DesUtils().encrypt(password.trim());
						SharedprefUtil.put(ForgetPasswordActivity.this, QuhaoConstant.PASSWORD, HexedPwd);
						QHClientApplication.getInstance().isLogined = false;
						Intent intent = new Intent();
						intent.putExtra("activityName", ForgetPasswordActivity.class.getName());
						intent.setClass(ForgetPasswordActivity.this, LoginActivity.class);
						startActivity(intent);
						ForgetPasswordActivity.this.finish();
					}
					else
					{
						Toast.makeText(ForgetPasswordActivity.this, signup.errorText, Toast.LENGTH_LONG).show();
					}
				}
				
			}
		}
	};
	
	@Override
	public void onClick(View v) {

		if (isClick) {
			return;
		}
		isClick = true;
		
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
		
		progressDialogUtil = new ProgressDialogUtil(this, R.string.empty,
				R.string.waitting, false);
		progressDialogUtil.showProgress();

		switch (v.getId()) {
		case R.id.submit:
			Thread thread = new Thread(new Runnable() {

				@Override
				public void run() {
					Looper.prepare();
					try {
						
						loginName = loginNameText.getText().toString().trim();
						if (StringUtils.isNotNull(loginName)
								|| validatePhoneNumber(loginName)) {
							password = passwordText.getText().toString().trim();
							password2 = password2Text.getText().toString().trim();
							verifyCode = verifyCodeText.getText().toString().trim();
							if(StringUtils.isNull(password))
							{
								progressDialogUtil.closeProgress();
								Toast.makeText(ForgetPasswordActivity.this, "请输入密码。", Toast.LENGTH_LONG).show();
								unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK,
										1000);
								return;
							}
							
							if(StringUtils.isNull(password2))
							{
								progressDialogUtil.closeProgress();
								Toast.makeText(ForgetPasswordActivity.this, "请输入确认密码。", Toast.LENGTH_LONG).show();
								unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK,
										1000);
								return;
							}
							
							if(StringUtils.isNull(verifyCode))
							{
								progressDialogUtil.closeProgress();
								Toast.makeText(ForgetPasswordActivity.this, "请输入验证码。", Toast.LENGTH_LONG).show();
								unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK,
										1000);
								return;
							}
							
							if(!password.equals(password2))
							{
								progressDialogUtil.closeProgress();
								Toast.makeText(ForgetPasswordActivity.this, "密码与确认密码必须一致。", Toast.LENGTH_LONG).show();
								unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK,
										1000);
								return;
							}
							
							String url = "updatePassCode?mobile=" + loginName
									+ "&code=" + verifyCode + "&password="+ password;

							String buf = CommonHTTPRequest.get(url);
							if(StringUtils.isNull(buf)||"[]".equals(buf))
							{
								progressDialogUtil.closeProgress();
								Toast.makeText(ForgetPasswordActivity.this, "发送验证码失败， 请重按验证码按钮。", Toast.LENGTH_LONG).show();
								unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK,
										1000);
								return;
							}
							
							signup = ParseJson.getSignup(buf);
							signupUpdateHandler.obtainMessage(200, signup).sendToTarget();
							
						}
						else
						{
							progressDialogUtil.closeProgress();
							Toast.makeText(ForgetPasswordActivity.this, "请填写手机号。", Toast.LENGTH_LONG).show();
							unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK,
									1000);
						}
					} catch (Exception e) {
						e.printStackTrace();
						progressDialogUtil.closeProgress();
						Toast.makeText(ForgetPasswordActivity.this, "发送验证码失败， 请重发", Toast.LENGTH_LONG).show();
						unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK,
								1000);
						
					}
					finally
					{
						Looper.loop();
					}

				}
			});
			thread.start();
			break;
		case R.id.close:
			progressDialogUtil.closeProgress();
			unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			this.finish();
			break;
		case R.id.verify_code_button:
			Thread threadVerify = new Thread(new Runnable() {

				@Override
				public void run() {
					Looper.prepare();
					try {
						
						loginName = loginNameText.getText().toString().trim();
						if (StringUtils.isNotNull(loginName)
								|| validatePhoneNumber(loginName)) {
							String url = "getAuthCode?mobile=" + loginName;

							String buf = CommonHTTPRequest.get(url);
							if(StringUtils.isNull(buf)||"[]".equals(buf))
							{
								progressDialogUtil.closeProgress();
								Toast.makeText(ForgetPasswordActivity.this, "发送验证码失败， 请重按验证码按钮。", Toast.LENGTH_LONG).show();
								unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK,
										1000);
								return;
							}
							
							signup = ParseJson.getSignup(buf);
							verifyUpdateHandler.obtainMessage(200, signup).sendToTarget();
							
						}
						else
						{
							progressDialogUtil.closeProgress();
							Toast.makeText(ForgetPasswordActivity.this, "请填写手机号。", Toast.LENGTH_LONG).show();
							unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK,
									1000);
						}
					} catch (Exception e) {
						e.printStackTrace();
						progressDialogUtil.closeProgress();
						Toast.makeText(ForgetPasswordActivity.this, "发送验证码失败， 请重发", Toast.LENGTH_LONG).show();
						unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK,
								1000);
						
					}
					finally
					{
						
						Looper.loop();
					}

				}
			});
			threadVerify.start();
			break;
		default:
			progressDialogUtil.closeProgress();
			unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			break;
		}

	}

	private boolean validatePhoneNumber(String loginName) {
		Pattern p = Pattern
				.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");

		Matcher m = p.matcher(loginName);
		return m.matches();
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return false;
	}

}
