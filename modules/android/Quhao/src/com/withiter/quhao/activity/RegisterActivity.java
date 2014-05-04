package com.withiter.quhao.activity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.withiter.quhao.QHClientApplication;
import com.withiter.quhao.R;
import com.withiter.quhao.domain.AccountInfo;
import com.withiter.quhao.task.LoginTask;
import com.withiter.quhao.util.QuhaoLog;
import com.withiter.quhao.util.StringUtils;
import com.withiter.quhao.util.encrypt.DesUtils;
import com.withiter.quhao.util.http.CommonHTTPRequest;
import com.withiter.quhao.util.tool.ParseJson;
import com.withiter.quhao.util.tool.ProgressDialogUtil;
import com.withiter.quhao.util.tool.QuhaoConstant;
import com.withiter.quhao.util.tool.SharedprefUtil;
import com.withiter.quhao.vo.LoginInfo;
import com.withiter.quhao.vo.SignupVO;

public class RegisterActivity extends QuhaoBaseActivity {

	private EditText loginNameText;
	private EditText verifyCodeText;
	private EditText passwordText;
	private EditText password2Text;
	private Button verifyCodeBtn;
	private Button registerBtn;
	private Button backBtn;
	private String loginName;
	private String verifyCode;
	private String password;
	private String password2;
	private TextView regResult;
	private SignupVO signup;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.register_layout);
		super.onCreate(savedInstanceState);

		loginNameText = (EditText) this.findViewById(R.id.login_name);
		verifyCodeText = (EditText) this.findViewById(R.id.verify_code);
		passwordText = (EditText) this.findViewById(R.id.edit_pass);
		password2Text = (EditText) this.findViewById(R.id.edit_pass_2);
		verifyCodeBtn = (Button) this.findViewById(R.id.verify_code_button);
		registerBtn = (Button) this.findViewById(R.id.register_btn);
		regResult = (TextView) this.findViewById(R.id.register_result);
		backBtn = (Button) this.findViewById(R.id.back_btn);

		registerBtn.setOnClickListener(this);
		verifyCodeBtn.setOnClickListener(this);
		backBtn.setOnClickListener(goBack(this));
		TelephonyManager telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
		String phone = telephonyManager.getLine1Number();

		if (StringUtils.isNotNull(phone)) {
			if (phone.contains("+86")) {
				phone = phone.substring(3);
			}
			loginNameText.setText(phone);
		}

		loginNameText.setFocusableInTouchMode(true);
		loginNameText.requestFocus();
	}

	private Handler verifyUpdateHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 200) {
				super.handleMessage(msg);
				progressDialogUtil.closeProgress();
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				if (null == signup) {
					Toast.makeText(RegisterActivity.this, "发送验证码失败， 请重按验证码按钮。", Toast.LENGTH_LONG).show();
				} else {
					if ("1".equals(signup.errorKey)) {
						Toast.makeText(RegisterActivity.this, "发送验证码成功， 稍后请输入验证码，24小时有效。", Toast.LENGTH_LONG).show();
					} else {
						Toast.makeText(RegisterActivity.this, signup.errorText, Toast.LENGTH_LONG).show();
					}
				}

			}
		}
	};

	private Handler signupUpdateHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 200) {
				super.handleMessage(msg);
				progressDialogUtil.closeProgress();
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				if (null == signup) {
					Toast.makeText(RegisterActivity.this, "注册失败， 请重新注册。", Toast.LENGTH_LONG).show();
				} else {
					if ("1".equals(signup.errorKey)) {
						Toast.makeText(RegisterActivity.this, "注册成功。", Toast.LENGTH_LONG).show();
						SharedprefUtil.remove(RegisterActivity.this, QuhaoConstant.ACCOUNT_ID);
						SharedprefUtil.remove(RegisterActivity.this, QuhaoConstant.IS_AUTO_LOGIN);
						// SharedprefUtil.remove(RegisterActivity.this,
						// QuhaoConstant.IS_LOGIN);
						SharedprefUtil.put(RegisterActivity.this, QuhaoConstant.PHONE, loginName);
						String HexedPwd = new DesUtils().encrypt(password.trim());
						QuhaoLog.d("cross", "HexedPwd password:" + HexedPwd);
						SharedprefUtil.put(RegisterActivity.this, QuhaoConstant.PASSWORD, HexedPwd);

//						SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//						SharedPreferences.Editor editor = sharedPreferences.edit();
//						editor.putString(QuhaoConstant.PASSWORD, HexedPwd);
//						editor.commit();

						String url = "AccountController/login?phone=" + loginName + "&email=&password=" + password;
						final LoginTask task = new LoginTask(R.string.waitting, RegisterActivity.this, url);
						task.execute(new Runnable() {
							
							@Override
							public void run() {
								String result = task.result;

								LoginInfo loginInfo = ParseJson.getLoginInfo(result);
								AccountInfo account = new AccountInfo();

								account.build(loginInfo);
								account.isAuto = "true";
								SharedprefUtil.put(RegisterActivity.this, QuhaoConstant.IS_AUTO_LOGIN, "true");
								if (account.msg.equals("fail")) {

									Toast.makeText(RegisterActivity.this, "亲，网络不好哦，请重新登录！", Toast.LENGTH_LONG).show();
									QHClientApplication.getInstance().accountInfo = null;
									QHClientApplication.getInstance().isLogined = false;
									RegisterActivity.this.finish();
									// Handler handler = new Handler();
									// handler.post(new Runnable() {
									//
									// @Override
									// public void run() {
									//
									// loginResult.setText("用户名或密码错误，登陆失败");
									// passwordText.setText("");
									//
									// }
									// });
								}
								if (account.msg.equals("success")) {

									SharedprefUtil.put(RegisterActivity.this, QuhaoConstant.ACCOUNT_ID, loginInfo.accountId);
									SharedprefUtil.put(RegisterActivity.this, QuhaoConstant.PHONE, account.phone);

									String HexedPwd = new DesUtils().encrypt(passwordText.getText().toString());
									QuhaoLog.d("cross: login hexed password: ", HexedPwd);
//									SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//									SharedPreferences.Editor editor = sharedPreferences.edit();
//									editor.putString(QuhaoConstant.PASSWORD, HexedPwd);
//									editor.commit();
									SharedprefUtil.put(RegisterActivity.this, QuhaoConstant.PASSWORD, HexedPwd);

									// login state will store in QHClientApplication
									QHClientApplication.getInstance().accountInfo = account;
									QHClientApplication.getInstance().isLogined = true;
									RegisterActivity.this.finish();
									
								}
							
							}
						},new Runnable() {
							
							@Override
							public void run() {
								Handler handler = new Handler();
								handler.post(new Runnable() {

									@Override
									public void run() {

										Toast.makeText(RegisterActivity.this, "亲，网络不是很好哦", Toast.LENGTH_LONG).show();
										unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
									}
								});
								
							}
						});
						
//						QHClientApplication.getInstance().isLogined = false;
//						Intent intent = new Intent();
//						intent.putExtra("activityName", RegisterActivity.class.getName());
//						intent.setClass(RegisterActivity.this, LoginActivity.class);
//						startActivity(intent);
//						RegisterActivity.this.finish();
					} else {
						Toast.makeText(RegisterActivity.this, signup.errorText, Toast.LENGTH_LONG).show();
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
		progressDialogUtil = new ProgressDialogUtil(this, R.string.empty, R.string.waitting, false);
		progressDialogUtil.showProgress();

		switch (v.getId()) {
		case R.id.register_btn:
			Thread threadReg = new Thread(new Runnable() {

				@Override
				public void run() {
					Looper.prepare();
					try {

						loginName = loginNameText.getText().toString().trim();
						if (StringUtils.isNotNull(loginName) && validatePhoneNumber(loginName)) {
							password = passwordText.getText().toString().trim();
							password2 = password2Text.getText().toString().trim();
							verifyCode = verifyCodeText.getText().toString().trim();
							if (StringUtils.isNull(password)) {
								progressDialogUtil.closeProgress();
								Toast.makeText(RegisterActivity.this, "请输入密码。", Toast.LENGTH_LONG).show();
								unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
								return;
							}

							if (StringUtils.isNull(password2)) {
								progressDialogUtil.closeProgress();
								Toast.makeText(RegisterActivity.this, "请输入确认密码。", Toast.LENGTH_LONG).show();
								unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
								return;
							}

							if (StringUtils.isNull(verifyCode)) {
								progressDialogUtil.closeProgress();
								Toast.makeText(RegisterActivity.this, "请输入验证码。", Toast.LENGTH_LONG).show();
								unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
								return;
							}

							if (!password.equals(password2)) {
								progressDialogUtil.closeProgress();
								Toast.makeText(RegisterActivity.this, "密码与确认密码必须一致。", Toast.LENGTH_LONG).show();
								unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
								return;
							}

							String url = "signupWithMobile?mobile=" + loginName + "&code=" + verifyCode + "&password=" + password + "&os=ANDROID";
							QuhaoLog.d("cross", "signup password:" + password);
							QuhaoLog.d("cross", "signup url is:" + url);
							String buf = CommonHTTPRequest.get(url);
							if (StringUtils.isNull(buf) || "[]".equals(buf)) {
								progressDialogUtil.closeProgress();
								Toast.makeText(RegisterActivity.this, "亲，注册失败了，请重新注册。", Toast.LENGTH_LONG).show();
								unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
								return;
							}

							signup = ParseJson.getSignup(buf);
							signupUpdateHandler.obtainMessage(200, signup).sendToTarget();

						} else {
							progressDialogUtil.closeProgress();
							Toast.makeText(RegisterActivity.this, "请填写手机号。", Toast.LENGTH_LONG).show();
							unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
						}
					} catch (Exception e) {
						e.printStackTrace();
						progressDialogUtil.closeProgress();
						Toast.makeText(RegisterActivity.this, "亲，注册失败了，请重新注册。", Toast.LENGTH_LONG).show();
						unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);

					} finally {
						Looper.loop();
					}

				}
			});
			threadReg.start();
			break;
		case R.id.verify_code_button:
			Thread threadVerify = new Thread(new Runnable() {

				@Override
				public void run() {
					Looper.prepare();
					try {

						loginName = loginNameText.getText().toString().trim();
						if (StringUtils.isNull(loginName) || validatePhoneNumber(loginName)) {
							String url = "generateAuthCode?mobile=" + loginName + "&os=ANDROID";

							String buf = CommonHTTPRequest.get(url);
							if (StringUtils.isNull(buf) || "[]".equals(buf)) {
								progressDialogUtil.closeProgress();
								Toast.makeText(RegisterActivity.this, "发送验证码失败， 请重按验证码按钮。", Toast.LENGTH_LONG).show();
								unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
								return;
							}

							signup = ParseJson.getSignup(buf);
							verifyUpdateHandler.obtainMessage(200, signup).sendToTarget();

						} else {
							progressDialogUtil.closeProgress();
							Toast.makeText(RegisterActivity.this, "请填写手机号。", Toast.LENGTH_LONG).show();
							unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
						}
					} catch (Exception e) {
						e.printStackTrace();
						progressDialogUtil.closeProgress();
						Toast.makeText(RegisterActivity.this, "发送验证码失败， 请重发", Toast.LENGTH_LONG).show();
						unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);

					} finally {

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
		Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");

		Matcher m = p.matcher(loginName);
		return m.matches();
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return false;
	}

}
