package com.withiter.quhao.activity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.withiter.quhao.QHClientApplication;
import com.withiter.quhao.R;
import com.withiter.quhao.task.UpdateNicknameTask;
import com.withiter.quhao.util.StringUtils;
import com.withiter.quhao.util.tool.ParseJson;
import com.withiter.quhao.vo.SignupVO;

public class UpdateNicknameActivity extends QuhaoBaseActivity {

	private EditText nickNameText;
	
	private Button submit;
	
	private String nickname;

	private final int UNLOCK_CLICK = 1000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.update_nickname_layout);
		super.onCreate(savedInstanceState);

		nickNameText = (EditText) this.findViewById(R.id.nick_name);
		if(null != QHClientApplication.getInstance().accountInfo && StringUtils.isNotNull(QHClientApplication.getInstance().accountInfo.nickName))
		{
			nickNameText.setText(QHClientApplication.getInstance().accountInfo.nickName);
		}
		
		submit = (Button) this.findViewById(R.id.submit);
		submit.setOnClickListener(this);
		
		btnBack.setOnClickListener(goBack(this));
		
	}

	@Override
	public void onClick(View v) {
		// 已经点过，直接返回
		if (isClick) {
			return;
		}

		// 设置已点击标志，避免快速重复点击
		isClick = true;

		// 让软键盘消失
		InputMethodManager m = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		if (m != null) {
			if (m.isActive()) {
				m.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS);
			}

		}
		
		switch (v.getId()) {
		case R.id.submit:
			if (QHClientApplication.getInstance().isLogined) {
				nickname = nickNameText.getText().toString().trim();
				if(StringUtils.isNotNull(nickname))
				{
					
					Pattern pat = Pattern.compile("^[\u4E00-\u9FA5A-Za-z0-9_]+$");
					Matcher matcher = pat.matcher(nickname);
				 
					Pattern zhCN = Pattern.compile("^[\u4e00-\u9fa5]$");
				  
					int count = 0;
				  
					if(matcher.matches())
					{
						String str = nickname.substring(0, 1);
						if("_".equals(str))
						{
							AlertDialog.Builder builder = new Builder(this);
							builder.setTitle("温馨提示");
							builder.setMessage("昵称不能以下划线开头。");
							builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									dialog.dismiss();
								}
							});
							builder.create().show();
							unlockHandler.sendEmptyMessageAtTime(UNLOCK_CLICK, 1000);
							return;
						}
						else
						{
							 String[] ss = nickname.split("");
							 
							 for (int j = 0; j < ss.length; j++) {
								 if(zhCN.matcher(ss[j]).matches())
								 {
									 count = count+2;
								 }
								 else
								 {
									 count = count+1;
								 }
							 }
							 
							 if(count<5 || count>17)
							 {
								 AlertDialog.Builder builder = new Builder(this);
									builder.setTitle("温馨提示");
									builder.setMessage("昵称长度只能为4-16个字符。");
									builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which) {
											dialog.dismiss();
										}
									});
									builder.create().show();
									unlockHandler.sendEmptyMessageAtTime(UNLOCK_CLICK, 1000);
									return;
							 }
							 
						}
					}
					else
					{
						AlertDialog.Builder builder = new Builder(this);
						builder.setTitle("温馨提示");
						builder.setMessage("昵称只能使用字母、数字、中文和下划线组成，且不能以下划线开头。");
						builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								dialog.dismiss();
							}
						});
						builder.create().show();
						unlockHandler.sendEmptyMessageAtTime(UNLOCK_CLICK, 1000);
						return;
					}
					
					final UpdateNicknameTask task = new UpdateNicknameTask(R.string.waitting_for_commit, this, "updateUserName?accoutId=" + QHClientApplication.getInstance().accountInfo.accountId + "&name=" + nickname);
					task.execute(new Runnable() {
						
						@Override
						public void run() {
							
							String result = task.result;
							
							SignupVO vo = ParseJson.getSignup(result);
							
							if(null != vo)
							{
								if ("1".equals(vo.errorKey)) {
									QHClientApplication.getInstance().accountInfo.nickName = nickname;
									unlockHandler.sendEmptyMessageAtTime(UNLOCK_CLICK, 1000);
									UpdateNicknameActivity.this.finish();
								}
								else
								{
									AlertDialog.Builder builder = new Builder(UpdateNicknameActivity.this);
									builder.setTitle("温馨提示");
									builder.setMessage(vo.errorText);
									builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which) {
											dialog.dismiss();
										}
									});
									builder.create().show();
									unlockHandler.sendEmptyMessageAtTime(UNLOCK_CLICK, 1000);
								}
								
							}
							else
							{
								AlertDialog.Builder builder = new Builder(UpdateNicknameActivity.this);
								builder.setTitle("温馨提示");
								builder.setMessage(R.string.nickname_update_failed);
								builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										dialog.dismiss();
									}
								});
								builder.create().show();
								
								unlockHandler.sendEmptyMessageAtTime(UNLOCK_CLICK, 1000);
							}
							
						}
					},new Runnable() {
						
						@Override
						public void run() {
							
							AlertDialog.Builder builder = new Builder(UpdateNicknameActivity.this);
							builder.setTitle("温馨提示");
							builder.setMessage(R.string.nickname_update_failed);
							builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									dialog.dismiss();
								}
							});
							builder.create().show();
							
							unlockHandler.sendEmptyMessageAtTime(UNLOCK_CLICK, 1000);
						}
						
					});
				}
				else
				{
					AlertDialog.Builder builder = new Builder(UpdateNicknameActivity.this);
					builder.setTitle("温馨提示");
					builder.setMessage(R.string.nickname_cannot_null);
					builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
					builder.create().show();
					unlockHandler.sendEmptyMessageAtTime(UNLOCK_CLICK, 1000);
				}
				
			} else {
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				AlertDialog.Builder builder = new Builder(this);
				builder.setTitle("温馨提示");
				builder.setMessage("请先登录");
				builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				builder.create().show();
			}
			break;
		default:
			unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			break;
		}
	}

	private boolean checkNickName(String nickName) {
		
		Pattern pat = Pattern.compile("^[\u4E00-\u9FA5A-Za-z0-9_]+$");
		Matcher matcher = pat.matcher(nickName.trim());
	 
		Pattern zhCN = Pattern.compile("^[\u4e00-\u9fa5]$");
	  
		int count = 0;
	  
		if(matcher.matches())
		{
			String str = nickName.substring(0, 1);
			if("_".equals(str))
			{
				return false;
			}
			else
			{
				 String[] ss = nickName.split("");
				 
				 for (int j = 0; j < ss.length; j++) {
					 if(zhCN.matcher(ss[j]).matches())
					 {
						 count = count+2;
					 }
					 else
					 {
						 count = count+1;
					 }
				 }
				 
				 if(count<5 || count>17)
				 {
					 return false;
				 }
				 
				 return true;
			}
		}
	  
		return false;
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return false;
	}
}
