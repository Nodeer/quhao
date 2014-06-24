package com.withiter.quhao.activity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.ShareSDK;

import com.withiter.quhao.QHClientApplication;
import com.withiter.quhao.R;
import com.withiter.quhao.domain.AccountInfo;
import com.withiter.quhao.task.LoginTask;
import com.withiter.quhao.util.ActivityUtil;
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
	private String loginName;
	private String verifyCode;
	private String password;
	private String password2;
	private SignupVO signup;
	
	private boolean userAgreementFlag = true;
	
	private ImageView userAgreementCheckBox;
	
	private TextView userAgreementText;
	
	private String firstAccountId;
	
	private TimeCount timeCount;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.register_layout);
		super.onCreate(savedInstanceState);

		timeCount = new TimeCount(60000, 1000);
		loginNameText = (EditText) this.findViewById(R.id.login_name);
		verifyCodeText = (EditText) this.findViewById(R.id.verify_code);
		passwordText = (EditText) this.findViewById(R.id.edit_pass);
		password2Text = (EditText) this.findViewById(R.id.edit_pass_2);
		verifyCodeBtn = (Button) this.findViewById(R.id.verify_code_button);
		registerBtn = (Button) this.findViewById(R.id.register_btn);
		userAgreementCheckBox = (ImageView) this.findViewById(R.id.agreement_check_box);
		
		if(userAgreementFlag)
		{
			userAgreementCheckBox.setImageResource(R.drawable.checkbox_on);
		}
		else
		{
			userAgreementCheckBox.setImageResource(R.drawable.checkbox_off);
		}
		
		userAgreementCheckBox.setOnClickListener(this);
		
		userAgreementText = (TextView) this.findViewById(R.id.user_agreement);
		userAgreementText.setOnClickListener(this);
		
		registerBtn.setOnClickListener(this);
		verifyCodeBtn.setOnClickListener(this);
		btnBack.setOnClickListener(goBack(this));
		TelephonyManager telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
		String phone = telephonyManager.getLine1Number();

		if (StringUtils.isNotNull(phone)) {
			if (phone.contains("+86")) {
				phone = phone.substring(3);
			}
			loginNameText.setText(phone);
		}

		firstAccountId = SharedprefUtil.get(this, QuhaoConstant.ACCOUNT_ID, "");
		
		loginNameText.setFocusableInTouchMode(true);
		
		ShareSDK.initSDK(this);
	}

	private Handler verifyUpdateHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 200) {
				super.handleMessage(msg);
				progressDialogUtil.closeProgress();
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				if (null == signup) {
					Toast.makeText(RegisterActivity.this, "发送验证码失败， 请重按验证码按钮。", Toast.LENGTH_SHORT).show();
				} else {
					if (!"mobile".equals(signup.errorKey)) {
						Toast.makeText(RegisterActivity.this, "发送验证码成功， 稍后请输入验证码，24小时有效。", Toast.LENGTH_SHORT).show();
						timeCount.start();
					} else {
						Toast.makeText(RegisterActivity.this, signup.errorText, Toast.LENGTH_SHORT).show();
					}
				}

			}
		}
	};

	@Override
	protected void onResume() {
		ShareSDK.initSDK(this);
		super.onResume();
		
	}
	
	@Override
	protected void onPause() {
		super.onPause();
//		ShareSDK.stopSDK(this);
	}
	
	@Override
	protected void onDestroy() {
		ShareSDK.stopSDK(this);
		super.onDestroy();
	}
	
	private Handler signupUpdateHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 200) {
				super.handleMessage(msg);
				progressDialogUtil.closeProgress();
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				if (null == signup) {
					Toast.makeText(RegisterActivity.this, "注册失败， 请重新注册。", Toast.LENGTH_SHORT).show();
				} else {
					if ("1".equals(signup.errorKey)) {
						Toast.makeText(RegisterActivity.this, "注册成功。", Toast.LENGTH_SHORT).show();
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

						String url = "login?phone=" + loginName + "&email=&password=" + password;
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

									Toast.makeText(RegisterActivity.this, "亲，网络不好哦，请重新登录！", Toast.LENGTH_SHORT).show();
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

									if (!firstAccountId.equals(loginInfo.accountId)) {
										try
										{
											Platform sina = ShareSDK.getPlatform(RegisterActivity.this, "SinaWeibo");
											if (sina != null && sina.isValid()) {
												sina.removeAccount();
											}
										}catch (Exception e) {
											e.printStackTrace();
											Log.e("", "cancel sina failed");
										}
										
									}
									
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

										Toast.makeText(RegisterActivity.this, "亲，网络不是很好哦", Toast.LENGTH_SHORT).show();
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
						Toast.makeText(RegisterActivity.this, signup.errorText, Toast.LENGTH_SHORT).show();
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

						if(!userAgreementFlag)
						{
							progressDialogUtil.closeProgress();
							unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
							Toast.makeText(RegisterActivity.this, "请选择同意取号啦用户使用协议。", Toast.LENGTH_SHORT).show();
							return;
						}
						
						loginName = loginNameText.getText().toString().trim();
						if (StringUtils.isNotNull(loginName)) {
							if(validatePhoneNumber(loginName))
							{
								password = passwordText.getText().toString().trim();
								password2 = password2Text.getText().toString().trim();
								verifyCode = verifyCodeText.getText().toString().trim();
								if (StringUtils.isNull(password)) {
									progressDialogUtil.closeProgress();
									Toast.makeText(RegisterActivity.this, "请输入密码。", Toast.LENGTH_SHORT).show();
									unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
									return;
								}

								if (StringUtils.isNull(password2)) {
									progressDialogUtil.closeProgress();
									Toast.makeText(RegisterActivity.this, "请输入确认密码。", Toast.LENGTH_SHORT).show();
									unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
									return;
								}

								if (StringUtils.isNull(verifyCode)) {
									progressDialogUtil.closeProgress();
									Toast.makeText(RegisterActivity.this, "请输入验证码。", Toast.LENGTH_SHORT).show();
									unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
									return;
								}

								if (!password.equals(password2)) {
									progressDialogUtil.closeProgress();
									Toast.makeText(RegisterActivity.this, "密码与确认密码必须一致。", Toast.LENGTH_SHORT).show();
									unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
									return;
								}

								String url = "signupWithMobile?mobile=" + loginName + "&code=" + verifyCode + "&password=" + password + "&os=ANDROID";
								QuhaoLog.d("cross", "signup password:" + password);
								QuhaoLog.d("cross", "signup url is:" + url);
								if (!ActivityUtil.isNetWorkAvailable(getApplicationContext())) {
									progressDialogUtil.closeProgress();
									Toast.makeText(getApplicationContext(), R.string.network_error_info, Toast.LENGTH_SHORT).show();
									unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
									return;
								}
								
								String buf = CommonHTTPRequest.get(url);
								if (StringUtils.isNull(buf) || "[]".equals(buf)) {
									progressDialogUtil.closeProgress();
									Toast.makeText(RegisterActivity.this, "亲，注册失败了，请重新注册。", Toast.LENGTH_SHORT).show();
									unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
									return;
								}

								signup = ParseJson.getSignup(buf);
								signupUpdateHandler.obtainMessage(200, signup).sendToTarget();
							}
							else
							{
								progressDialogUtil.closeProgress();
								Toast.makeText(RegisterActivity.this, "请填写正确手机号。", Toast.LENGTH_SHORT).show();
								unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
							}

						} else {
							progressDialogUtil.closeProgress();
							Toast.makeText(RegisterActivity.this, "请填写手机号。", Toast.LENGTH_SHORT).show();
							unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
						}
					} catch (Exception e) {
						e.printStackTrace();
						progressDialogUtil.closeProgress();
						Toast.makeText(RegisterActivity.this, "亲，注册失败了，请重新注册。", Toast.LENGTH_SHORT).show();
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
						
						if (StringUtils.isNotNull(loginName)) {
							if(validatePhoneNumber(loginName))
							{
								String url = "generateAuthCode?mobile=" + loginName + "&os=ANDROID";

								if (!ActivityUtil.isNetWorkAvailable(getApplicationContext())) {
									progressDialogUtil.closeProgress();
									Toast.makeText(getApplicationContext(), R.string.network_error_info, Toast.LENGTH_SHORT).show();
									unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
									return;
								}
								
								String buf = CommonHTTPRequest.get(url);
								if (StringUtils.isNull(buf) || "[]".equals(buf)) {
									progressDialogUtil.closeProgress();
									Toast.makeText(RegisterActivity.this, "发送验证码失败， 请重按验证码按钮。", Toast.LENGTH_SHORT).show();
									unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
									return;
								}

								signup = ParseJson.getSignup(buf);
								verifyUpdateHandler.obtainMessage(200, signup).sendToTarget();
							}
							else
							{
								progressDialogUtil.closeProgress();
								Toast.makeText(RegisterActivity.this, "请填写正确手机号。", Toast.LENGTH_SHORT).show();
								unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
							}
							

						} else {
							progressDialogUtil.closeProgress();
							Toast.makeText(RegisterActivity.this, "请填写手机号。", Toast.LENGTH_SHORT).show();
							unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
						}
					} catch (Exception e) {
						e.printStackTrace();
						progressDialogUtil.closeProgress();
						Toast.makeText(RegisterActivity.this, "发送验证码失败， 请重发", Toast.LENGTH_SHORT).show();
						unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);

					} finally {

						Looper.loop();
					}

				}
			});
			threadVerify.start();
			break;
		case R.id.agreement_check_box:
			if (userAgreementFlag) 
			{
				userAgreementFlag = false;
				userAgreementCheckBox.setImageResource(R.drawable.checkbox_off);
			}
			else
			{
				userAgreementFlag = true;
				userAgreementCheckBox.setImageResource(R.drawable.checkbox_on);
			}
			progressDialogUtil.closeProgress();
			unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			break;
		case R.id.user_agreement:
			progressDialogUtil.closeProgress();
			unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			Intent intent = new Intent();
			intent.setClass(this, UserAgreementActivity.class);
			startActivity(intent);
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

	@Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
        	
            // 获得当前得到焦点的View，一般情况下就是EditText（特殊情况就是轨迹求或者实体案件会移动焦点）
            View v = getCurrentFocus();

            if (isShouldHideInput(v, ev)) {
                hideSoftInput(v.getWindowToken());
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时没必要隐藏
     * 
     * @param v
     * @param event
     * @return
     */
    private boolean isShouldHideInput(View v, MotionEvent event) {
    	
        if (v != null && (v instanceof EditText)) {
            int[] l = { 0, 0 };
            v.getLocationInWindow(l);
            int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left
                    + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击EditText的事件，忽略它。
                return false;
            } else {
                return true;
            }
        }
        // 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditView上，和用户用轨迹球选择其他的焦点
        return false;
    }

    /**
     * 多种隐藏软件盘方法的其中一种
     * 
     * @param token
     */
    private void hideSoftInput(IBinder token) {
        if (token != null) {
            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(token,
                    InputMethodManager.HIDE_NOT_ALWAYS);
            
//            if(im.isActive()){
//            	im.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS);
//			}
        }
    }
    
    class TimeCount extends CountDownTimer
    {
    	
		public TimeCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onFinish() {
			
			verifyCodeBtn.setText("获取验证码");
			verifyCodeBtn.setClickable(true);
			
		}

		@Override
		public void onTick(long millisUntilFinished) {
			
			verifyCodeBtn.setClickable(false);
			verifyCodeBtn.setText(millisUntilFinished/1000 + "秒");
		}
    	
    }
}
