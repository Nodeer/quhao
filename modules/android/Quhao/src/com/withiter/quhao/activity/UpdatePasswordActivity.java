package com.withiter.quhao.activity;

import android.content.Context;
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
import android.widget.Toast;

import com.withiter.quhao.QHClientApplication;
import com.withiter.quhao.R;
import com.withiter.quhao.domain.AccountInfo;
import com.withiter.quhao.util.StringUtils;
import com.withiter.quhao.util.encrypt.DesUtils;
import com.withiter.quhao.util.http.CommonHTTPRequest;
import com.withiter.quhao.util.tool.ParseJson;
import com.withiter.quhao.util.tool.ProgressDialogUtil;
import com.withiter.quhao.util.tool.QuhaoConstant;
import com.withiter.quhao.util.tool.SharedprefUtil;
import com.withiter.quhao.vo.SignupVO;

public class UpdatePasswordActivity extends QuhaoBaseActivity {

	private EditText currentPasswordText;
	
	private EditText newPasswordText;

	private EditText newPassword2Text;

	private Button submitBtn;

	private Button backBtn;

	private String currentPassword;
	private String newPassword;
	private String newPassword2;

	private AccountInfo accountInfo;
	
	private SignupVO signup;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.update_password_layout);
		super.onCreate(savedInstanceState);

		currentPasswordText = (EditText) this.findViewById(R.id.current_password);
		newPasswordText = (EditText) this.findViewById(R.id.new_pass);
		newPassword2Text = (EditText) this.findViewById(R.id.new_pass2);
		submitBtn = (Button) this.findViewById(R.id.submit);
		backBtn = (Button) this.findViewById(R.id.back_btn);
		
		backBtn.setOnClickListener(this);
		submitBtn.setOnClickListener(this);
		
		accountInfo = QHClientApplication.getInstance().accountInfo;
	}

	private Handler signupUpdateHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 200) {
				super.handleMessage(msg);
				progressDialogUtil.closeProgress();
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				if (null == signup) {
					Toast.makeText(UpdatePasswordActivity.this, "亲，修改密码失败！", Toast.LENGTH_LONG).show();
				}
				else
				{
					if("1".equals(signup.errorKey))
					{
						Toast.makeText(UpdatePasswordActivity.this, "亲，修改密码成功！", Toast.LENGTH_LONG).show();
						
						SharedprefUtil.put(UpdatePasswordActivity.this, QuhaoConstant.PASSWORD, newPassword);
						QHClientApplication.getInstance().accountInfo.password = newPassword;
						UpdatePasswordActivity.this.finish();
					}
					else
					{
						Toast.makeText(UpdatePasswordActivity.this, signup.errorText, Toast.LENGTH_LONG).show();
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
						currentPassword = currentPasswordText.getText().toString().trim();
//						String HexedPwd = new DesUtils().decrypt(accountInfo.password); TODO : 密码加密问题
//						SharedprefUtil.put(this, QuhaoConstant.PASSWORD, HexedPwd); 1711bd2d3d6441525f4826681a8b4d9a
						if (StringUtils.isNotNull(currentPassword)
								&& null != accountInfo && currentPassword.equals(new DesUtils().decrypt(accountInfo.password))) {
							newPassword = newPasswordText.getText().toString().trim();
							newPassword2 = newPassword2Text.getText().toString().trim();
							if(StringUtils.isNull(newPassword))
							{
								progressDialogUtil.closeProgress();
								Toast.makeText(UpdatePasswordActivity.this, "请输入新密码。", Toast.LENGTH_LONG).show();
								unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK,
										1000);
								return;
							}
							
							if(StringUtils.isNull(newPassword2))
							{
								progressDialogUtil.closeProgress();
								Toast.makeText(UpdatePasswordActivity.this, "请输入确认新密码。", Toast.LENGTH_LONG).show();
								unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK,
										1000);
								return;
							}
							

							if(!currentPassword.equals(newPassword))
							{
								progressDialogUtil.closeProgress();
								Toast.makeText(UpdatePasswordActivity.this, "新密码与旧密码必须不同。", Toast.LENGTH_LONG).show();
								unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK,
										1000);
								return;
							}
							
							if(!newPassword.equals(newPassword2))
							{
								progressDialogUtil.closeProgress();
								Toast.makeText(UpdatePasswordActivity.this, "新密码与确认新密码必须一致。", Toast.LENGTH_LONG).show();
								unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK,
										1000);
								return;
							}
							
							String url = "updatePassword?accoutId=" + accountInfo.accountId
									+ "&newPassWord=" + newPassword + "&oldPass="+ currentPassword;

							String buf = CommonHTTPRequest.get(url);
							if(StringUtils.isNull(buf)||"[]".equals(buf))
							{
								progressDialogUtil.closeProgress();
								Toast.makeText(UpdatePasswordActivity.this, "发送验证码失败， 请重按验证码按钮。", Toast.LENGTH_LONG).show();
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
							Toast.makeText(UpdatePasswordActivity.this, "亲，请填写正确密码！", Toast.LENGTH_LONG).show();
							unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK,
									1000);
						}
					} catch (Exception e) {
						e.printStackTrace();
						progressDialogUtil.closeProgress();
						Toast.makeText(UpdatePasswordActivity.this, "发送验证码失败， 请重发", Toast.LENGTH_LONG).show();
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
		case R.id.back_btn:
			progressDialogUtil.closeProgress();
			unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			this.finish();
			break;
		default:
			progressDialogUtil.closeProgress();
			unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			break;
		}

	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return false;
	}

}
