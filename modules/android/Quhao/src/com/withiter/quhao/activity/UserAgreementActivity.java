package com.withiter.quhao.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.withiter.quhao.R;
import com.withiter.quhao.task.GetUserAgreementTask;
import com.withiter.quhao.util.StringUtils;
import com.withiter.quhao.util.tool.ParseJson;
import com.withiter.quhao.vo.UserAgreementVO;

public class UserAgreementActivity extends QuhaoBaseActivity {

	public static boolean backClicked = false;

	private TextView contentView;
	
	private UserAgreementVO userAgreement;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.user_agreement_layout);
		super.onCreate(savedInstanceState);

		contentView = (TextView) this.findViewById(R.id.content);
		
		btnBack.setOnClickListener(goBack(this, this.getClass().getName()));
		
	}
	
	@Override
	public void finish() {
		super.finish();
	}

	@Override
	protected void onResume() {
		backClicked = false;
		
		findViewById(R.id.loadingbar).setVisibility(View.VISIBLE);
		findViewById(R.id.scrollViewLayout).setVisibility(View.GONE);
		
		initView();
		
		super.onResume();
	}

	private Handler signupUpdateHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 200) {
				super.handleMessage(msg);
				if(userAgreement != null && StringUtils.isNotNull(userAgreement.content))
				{
					contentView.setText(userAgreement.content);
				}
				else
				{
					contentView.setText("暂无协议");
				}
				findViewById(R.id.loadingbar).setVisibility(View.GONE);
				findViewById(R.id.scrollViewLayout).setVisibility(View.VISIBLE);
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				
			}
		}
	};
	
	private void initView() {
		
		String url = "getUserAgreement";
		final GetUserAgreementTask task = new GetUserAgreementTask(R.string.waitting, this, url);
		task.execute(new Runnable() {
			
			@Override
			public void run() {
				String result = task.result;
				userAgreement = ParseJson.getUserAgreement(result);

				signupUpdateHandler.obtainMessage(200, userAgreement).sendToTarget();
			}
		}, new Runnable() {
			
			@Override
			public void run() {
				
				signupUpdateHandler.obtainMessage(200, userAgreement).sendToTarget();
				
			}
		});
		
	}

	@Override
	public void onPause() {
		super.onPause();
		if (backClicked) {
			overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
		}
	}

	@Override
	public void onClick(View v) {

	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return false;
	}
}
