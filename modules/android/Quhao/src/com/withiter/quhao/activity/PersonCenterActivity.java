package com.withiter.quhao.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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

public class PersonCenterActivity extends AppStoreActivity {

	private final static String TAG = PersonCenterActivity.class.getName();
	
	private TextView nickName;
	private TextView mobile;
	private TextView jifen;
	private TextView value_qiandao;
	private TextView value_dianpin;
	private TextView value_zhaopian;
	
	
	private TextView loginResult;
	private EditText loginNameText;
	private EditText passwordText;
	private Button btnClose;
	private Button btnLogin;
	private Button btnRegister;

	private final int UNLOCK_CLICK = 1000;
	private ProgressDialogUtil progressLogin;
	
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
		
		SharedprefUtil.put(this, QuhaoConstant.IS_LOGIN, "false");
		String isLogin = SharedprefUtil.get(this,QuhaoConstant.IS_LOGIN, "false");

		// check already login or not
		if (StringUtils.isNull(isLogin) || "false".equalsIgnoreCase(isLogin)) {
			// TODO add not login business here
			QuhaoLog.i(TAG, "login check: not login");
			LayoutInflater inflater = (LayoutInflater) getApplicationContext()
					.getSystemService(LAYOUT_INFLATER_SERVICE);
			View view = inflater.inflate(R.layout.login_layout, null);

			ad = new AlertDialog.Builder(
					PersonCenterActivity.this).setView(view).setTitle("账号登陆").show();

			loginResult = (TextView) view.findViewById(R.id.person_center_login_result);
			loginNameText = (EditText) view.findViewById(R.id.login_name);
			passwordText = (EditText) view.findViewById(R.id.edit_pass);

			btnClose = (Button) view.findViewById(R.id.close);
			btnLogin = (Button) view.findViewById(R.id.login);
			btnRegister = (Button) view.findViewById(R.id.zhuce);

			btnClose.setOnClickListener(this);
			btnLogin.setOnClickListener(this);
			btnRegister.setOnClickListener(this);

		} else {
			// TODO add already login business here
			QuhaoLog.i(TAG, "login check: login");
		}

	}

	@Override
	public void onClick(View v) {
//		// 隐藏软键盘
//		InputMethodManager m = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//		if (m != null) {
//			m.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(),
//					InputMethodManager.HIDE_NOT_ALWAYS);
//		}
//		// 已经点过，直接返回
//		if (isClick) {
//			return;
//		}
//
//		// 设置已点击标志，避免快速重复点击
//		isClick = true;
//		// 解锁
//		unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
		switch (v.getId()) {
		case R.id.close:
			System.gc();
			finish();
			break;
		case R.id.login:
			QuhaoLog.i(TAG, "login clicked");
			progressLogin = new ProgressDialogUtil(this, R.string.empty,
					R.string.querying, false);
			progressLogin.showProgress();
			String url = "AccountController/login?";
			url = url + "phone="
						+ loginNameText.getText().toString().trim() + "&";
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
					QuhaoLog.i(TAG, account.msg);
					if(account.msg.equals("fail")){
						loginResult.setText("用户名或密码错误，登陆失败");
						passwordText.setText("");
						return;
					}
					if(account.msg.equals("success")){
						loginResult.setText("登陆成功");
						nickName.setText(account.nickName);
						mobile.setText(account.phone);
						
						// TODO add jifen from backend
						jifen.setText(account.jifen);
						
						value_qiandao.setText(account.qiandao);
						value_dianpin.setText(account.dianpin);
						value_zhaopian.setText(account.zhaopian);
						
						QHClientApplication.getInstance().accessInfo = account;
						QHClientApplication.getInstance().isLogined = true;
						ad.dismiss();
						return;
					}
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

	private void displayAccountInfo(){
//		TextView nickName = (TextView) findViewById("nickName");
	}
}
