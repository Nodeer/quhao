package com.withiter.quhao.activity;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.sina.weibo.sdk.WeiboSDK;
import com.sina.weibo.sdk.api.IWeiboAPI;
import com.sina.weibo.sdk.api.SendMessageToWeiboRequest;
import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.Weibo;
import com.weibo.sdk.android.WeiboAuthListener;
import com.weibo.sdk.android.WeiboDialogError;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.util.AccessTokenKeeper;
import com.withiter.quhao.R;
import com.withiter.quhao.util.tool.ProgressDialogUtil;
import com.withiter.quhao.util.tool.QuhaoConstant;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SinaInfoActivity extends AppStoreActivity {

	private Button btnSinaShare;
	
	private EditText sinaEditText;
	
	private String shareInfo;
	
	private Weibo mWeibo;
	
	private Oauth2AccessToken mAccessToken;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.more_sina_info);
		super.onCreate(savedInstanceState);
		
		btnSinaShare = (Button) findViewById(R.id.share_sina_btn);
		sinaEditText = (EditText) findViewById(R.id.share_edit);
		
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		
		shareInfo = bundle.getString("SHARE_INFO");
		sinaEditText.setText(shareInfo);
		
		btnSinaShare.setOnClickListener(this);
		btnBack.setOnClickListener(goBack(this));
	}
	

	@Override
	public void onClick(View v) {

		if(isClick)
		{
			return;
		}
		isClick = true;
		progressDialogUtil = new ProgressDialogUtil(this, R.string.empty,
				R.string.waitting, false);
		
		progressDialogUtil.showProgress();
		
		switch (v.getId()) {
			case R.id.share_sina_btn:
				// 新浪微博
				isClick = false;
				progressDialogUtil.closeProgress();
				sendSina();
				break;
			default:
				break;
		}
	}

	private void sendSina() {
		mWeibo = Weibo.getInstance(QuhaoConstant.SINA_APP_KEY, QuhaoConstant.SINA_REDIRECT_URL, QuhaoConstant.SINA_SCOPE);
		mAccessToken = AccessTokenKeeper.readAccessToken(SinaInfoActivity.this);
		mWeibo.anthorize(SinaInfoActivity.this, new AuthDialogListener());
	}

	class AuthDialogListener implements WeiboAuthListener{

		@Override
		public void onComplete(Bundle bundle) {
			
			String token = bundle.getString("access_token");
			String expiresIn = bundle.getString("expires_in");
			mAccessToken = new Oauth2AccessToken(token, expiresIn);
			
			sinaEditText.setText("ok");
			
			if(mAccessToken.isSessionValid())
			{
				String date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date(mAccessToken.getExpiresTime()));
				sinaEditText.setText("认证成功: \r\n access_token : " + token + "\r\n" + 
						"expires_in : " +expiresIn + "\r\n有效期:" + date);
				
				AccessTokenKeeper.keepAccessToken(SinaInfoActivity.this, mAccessToken);
				IWeiboAPI iweibo = WeiboSDK.createWeiboAPI(SinaInfoActivity.this, QuhaoConstant.SINA_APP_KEY);
				boolean flag = iweibo.registerApp();
				Log.w("", String.valueOf(flag));
				
				Toast.makeText(SinaInfoActivity.this, "success", Toast.LENGTH_LONG).show();
			}
		}

		@Override
		public void onError(WeiboDialogError error) {
			Toast.makeText(SinaInfoActivity.this, "Auth error: " + error.getMessage(), Toast.LENGTH_LONG).show();
		}

		@Override
		public void onCancel() {
			Toast.makeText(SinaInfoActivity.this, "Auth cancel ", Toast.LENGTH_LONG).show();
		}
		
		@Override
		public void onWeiboException(WeiboException exception) {
			Toast.makeText(SinaInfoActivity.this, "Auth exception:" + exception.getMessage(), Toast.LENGTH_LONG).show();
		}
		
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return false;
	}

}
