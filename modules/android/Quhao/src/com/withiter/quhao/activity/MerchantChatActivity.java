package com.withiter.quhao.activity;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;

import com.withiter.quhao.R;
import com.withiter.quhao.util.QuhaoLog;

public class MerchantChatActivity extends QuhaoBaseActivity {
	
	public static boolean backClicked = false;
	private String LOGTAG = MerchantChatActivity.class.getName();
	
	private WebView webView;

	@Override
	public void finish() {
		super.finish();
		QuhaoLog.i(LOGTAG, LOGTAG + " finished");
	}

	@Override
	protected void onResume() {
		backClicked = false;
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
		QuhaoLog.i(LOGTAG, LOGTAG + " on pause");
		if (backClicked) {
			overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.merchant_chat_layout);
		super.onCreate(savedInstanceState);
		btnBack.setOnClickListener(goBack(this,this.getClass().getName()));
		
		webView = (WebView) this.findViewById(R.id.chat_web_view);
		webView.loadUrl("http://www.quhao.la/");
		webView.getSettings().setJavaScriptEnabled(true);

	}

	@Override
	public void onClick(View v) {

	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return false;
	}

}
