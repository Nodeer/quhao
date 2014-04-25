package com.withiter.quhao.activity;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.withiter.quhao.QHClientApplication;
import com.withiter.quhao.R;
import com.withiter.quhao.task.UpdateNicknameTask;
import com.withiter.quhao.util.StringUtils;

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

		switch (v.getId()) {
		case R.id.submit:
			if (QHClientApplication.getInstance().isLogined) {
				nickname = nickNameText.getText().toString();
				if(StringUtils.isNotNull(nickname.trim()))
				{
					UpdateNicknameTask task = new UpdateNicknameTask(R.string.waitting_for_commit, this, "updateUserName?accoutId=" + QHClientApplication.getInstance().accountInfo.accountId + "&name=" + nickname);
					task.execute(new Runnable() {
						
						@Override
						public void run() {
							
							QHClientApplication.getInstance().accountInfo.nickName = nickname;
							
							UpdateNicknameActivity.this.finish();
						}
					},new Runnable() {
						
						@Override
						public void run() {
							
							Toast.makeText(UpdateNicknameActivity.this, R.string.nickname_update_failed, Toast.LENGTH_LONG).show();
							unlockHandler.sendEmptyMessageAtTime(UNLOCK_CLICK, 1000);
						}
					});
				}
				else
				{
					Toast.makeText(this, R.string.nickname_cannot_null, Toast.LENGTH_LONG).show();
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

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return false;
	}
}
