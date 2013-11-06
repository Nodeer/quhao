package com.withiter.quhao.activity;

import android.os.Bundle;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.withiter.quhao.R;
import com.withiter.quhao.util.StringUtils;
import com.withiter.quhao.util.http.CommonHTTPRequest;
import com.withiter.quhao.util.tool.ProgressDialogUtil;

public class OpinionActivity extends AppStoreActivity {

	private Button btnOpinion;
	
	private EditText opinionEdit;
	
	private EditText contactEdit;
	
	private String opinion;
	
	private String contact;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.more_opinion_layout);
		super.onCreate(savedInstanceState);
		
		btnOpinion = (Button) findViewById(R.id.btn_opinion);
		
		opinionEdit = (EditText) findViewById(R.id.opinion_edit);
		contactEdit = (EditText) findViewById(R.id.opinion_edit_contact);
		btnOpinion.setOnClickListener(this);
		btnBack.setOnClickListener(goBack(this));
	}

	@Override
	public void onClick(View v) {

		if(isClick)
		{
			return;
		}
		isClick = true;
		
		progressDialogUtil  = new ProgressDialogUtil(OpinionActivity.this, R.string.empty,
				R.string.committing, false);
		progressDialogUtil.showProgress();
		switch(v.getId())
		{
			case R.id.btn_opinion:
				
				String curOpinion = opinionEdit.getText().toString();
				
				if(curOpinion.equals(opinion))
				{
					Toast.makeText(this, "请不要重复提交", Toast.LENGTH_LONG).show();
					progressDialogUtil.closeProgress();
					unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
					return;
				}
				
				opinion = curOpinion;
				contact = contactEdit.getText().toString();
				
				Thread thread = new Thread(opinionRunnable);
				thread.start();
				
			break;
		}
		
	}

	private Runnable opinionRunnable = new Runnable() {
		
		@Override
		public void run() {
			try {
				Looper.prepare();
				opinion = opinion.trim();
				contact = contact.trim();
				String buf = CommonHTTPRequest.get("createOpinion?opinion=" + opinion + "&contact=" + contact);
				if (StringUtils.isNull(buf) || "[]".equals(buf)) {
					Toast.makeText(OpinionActivity.this, "网络不好，请重新提交", Toast.LENGTH_LONG).show();
					unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				} else if("success".equals(buf)){
					Toast.makeText(OpinionActivity.this, "提交成功，多谢您的意见", Toast.LENGTH_LONG).show();
					unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
					OpinionActivity.this.finish();
				}

			} catch (Exception e) {
				
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				Toast.makeText(OpinionActivity.this, "网络不好，请重新提交", Toast.LENGTH_LONG).show();
			} finally {
				progressDialogUtil.closeProgress();
				Looper.loop();
				
			}
		}
	};
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return false;
	}

}
