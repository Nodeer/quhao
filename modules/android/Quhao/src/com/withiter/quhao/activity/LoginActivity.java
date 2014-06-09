package com.withiter.quhao.activity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.withiter.quhao.util.tool.QuhaoConstant;
import com.withiter.quhao.util.tool.SharedprefUtil;
import com.withiter.quhao.vo.LoginInfo;
import com.withiter.quhao.vo.ReservationVO;

public class LoginActivity extends QuhaoBaseActivity {

	private final static String TAG = LoginActivity.class.getName();
	private TextView pannelLoginName;
	private EditText loginNameText;
	private EditText passwordText;
	private Button btnBack;
	private Button btnLogin;
	private Button forgetPasswordBtn;
	private TextView loginResult;
	private final int UNLOCK_CLICK = 1000;

	private ImageView isAutoLoginView;
	private String isAutoLogin = "false";

	private String activityName;

	private Map<String, Object> transfortParams = new HashMap<String, Object>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.login_layout);
		super.onCreate(savedInstanceState);

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

		btnBack = (Button) findViewById(R.id.back_btn);
		btnLogin = (Button) findViewById(R.id.login);
		forgetPasswordBtn = (Button) findViewById(R.id.forgetPassword);

		btnBack.setOnClickListener(this);
		btnLogin.setOnClickListener(this);
		forgetPasswordBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// 隐藏软键盘

//		InputMethodManager m = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//		if (m != null) {
			// if(this.getCurrentFocus()!=null &&
			// this.getCurrentFocus().getWindowToken() != null)
			// {
			// m.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(),
			// InputMethodManager.HIDE_NOT_ALWAYS);
			// }

			// R.id.login
			// m.hideSoftInputFromWindow(passwordText.getWindowToken(), 0);
			// m.hideSoftInputFromWindow(loginNameText.getWindowToken(), 0);
//			if (m.isActive()) {
//				m.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS);
//			}

//		}

		// getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		// 已经点过，直接返回
		if (isClick) {
			return;
		}
		// 设置已点击标志，避免快速重复点击
		isClick = true;

		// 解锁
		
		switch (v.getId()) {
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
			String url = "login?phone=" + loginNameText.getText().toString().trim() + "&email=&password=" + passwordText.getText().toString();
			final LoginTask task = new LoginTask(R.string.waitting, this, url);
			task.execute(new Runnable() {
				
				@Override
				public void run() {
					String result = task.result;


					LoginInfo loginInfo = ParseJson.getLoginInfo(result);
					AccountInfo account = new AccountInfo();

					account.build(loginInfo);
					account.isAuto = isAutoLogin;
					QuhaoLog.i(TAG, "account.msg : " + account.msg);

					if (account.msg.equals("fail")) {

						loginFailedHandler.obtainMessage(200, null).sendToTarget();

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
						return;
					}
					else if (account.msg.equals("success")) {

						SharedprefUtil.put(LoginActivity.this, QuhaoConstant.ACCOUNT_ID, loginInfo.accountId);
						SharedprefUtil.put(LoginActivity.this, QuhaoConstant.PHONE, account.phone);

						String HexedPwd = new DesUtils().encrypt(passwordText.getText().toString());
						QuhaoLog.d("cross: login hexed password: ", HexedPwd);
						SharedprefUtil.put(LoginActivity.this, QuhaoConstant.PASSWORD, HexedPwd);

						SharedprefUtil.put(LoginActivity.this, QuhaoConstant.IS_AUTO_LOGIN, isAutoLogin.trim());

						// login state will store in QHClientApplication
						QHClientApplication.getInstance().accountInfo = account;
						QHClientApplication.getInstance().isLogined = true;

						loginUpdateHandler.obtainMessage(200, account).sendToTarget();
						return;
					}
					else
					{
						unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
					}
				}
			},new Runnable() {
				
				@Override
				public void run() {
					Handler handler = new Handler();
					handler.post(new Runnable() {

						@Override
						public void run() {

							Toast.makeText(LoginActivity.this, "亲，网络不是很好哦", Toast.LENGTH_SHORT).show();
							unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
						}
					});
					
				}
			});
			unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			break;
		case R.id.back_btn:
			// Intent intent = new Intent();
			// intent.setClass(this, PersonCenterActivity.class);
			// startActivity(intent);
			onBackPressed();
			unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1);
			this.finish();
			overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
			break;
		case R.id.forgetPassword:
			unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			Intent intent1 = new Intent(this, ForgetPasswordActivity.class);
			startActivity(intent1);
			System.gc();
			finish();
//			overridePendingTransition(R.anim.out_to_right, R.anim.out_to_left);
			break;
		default:
			unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			break;
		}
	}

	private Handler loginFailedHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == 200) {
				super.handleMessage(msg);
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				loginResult.setText("用户名或密码错误，登陆失败");
				passwordText.setText("");
			}
		}
	};

	private Handler loginUpdateHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 200) {
				super.handleMessage(msg);
				loginResult.setText("登陆成功");
				QuhaoLog.d(TAG, "login call back to " + activityName);

				if(StringUtils.isNull(activityName) || !MerchantDetailActivity.class.getName().equals(activityName))
				{
					unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
					finish();
				} else {
					unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
					if("true".equals(transfortParams.get("notGetNumber")))
					{
						finish();
					}
					else
					{
						Thread thread = new Thread(new Runnable() {

							@Override
							public void run() {
								try {
									Looper.prepare();
									Intent intent = new Intent();
									intent.putExtra("merchantId", (String) transfortParams.get("merchantId"));
									String accountId = QHClientApplication.getInstance().accountInfo.accountId;
									if (!ActivityUtil.isNetWorkAvailable(getApplicationContext())) {
										Toast.makeText(getApplicationContext(), R.string.network_error_info, Toast.LENGTH_SHORT).show();
										unlockHandler.sendEmptyMessage(UNLOCK_CLICK);
										return;
									}
									String buf = CommonHTTPRequest.get("getReservations?accountId=" + accountId + "&mid=" + transfortParams.get("merchantId"));
									if (StringUtils.isNull(buf) || "[]".equals(buf)) {
										unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
										intent.setClass(LoginActivity.this, GetNumberActivity.class);
										startActivity(intent);
									} else {
										List<ReservationVO> rvos = ParseJson.getReservations(buf);
										if (null != rvos && !rvos.isEmpty()) {
											Toast.makeText(LoginActivity.this, "已有该商家的排队号码！", Toast.LENGTH_SHORT).show();
											// LoginActivity.this.onBackPressed();
										} else {
											intent.setClass(LoginActivity.this, GetNumberActivity.class);
											startActivity(intent);
										}
									}
									unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
									LoginActivity.this.finish();
								} catch (Exception e) {
									unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
									Toast.makeText(LoginActivity.this, "网络异常，请稍候取号", Toast.LENGTH_SHORT).show();
									LoginActivity.this.onBackPressed();
									LoginActivity.this.finish();
								} finally {
									Looper.loop();
								}

							}
						});
						thread.start();
					}
				}

			}
		}
	};

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
                loginNameText.clearFocus();
                passwordText.clearFocus();
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
}
