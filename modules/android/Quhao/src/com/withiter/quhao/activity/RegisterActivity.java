package com.withiter.quhao.activity;

import android.content.Context;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.withiter.quhao.R;

public class RegisterActivity extends QuhaoBaseActivity {

	private EditText loginNameText;
	
	private EditText verifyCodeText;
	
	private EditText passwordText;
	
	private EditText password2Text;
	
	private Button verifyCodeBtn;
	
	private Button registerBtn;
	
	private Button colseBtn;
	
	private TextView regResult;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.register_layout);
		super.onCreate(savedInstanceState);
		
		loginNameText = (EditText) this.findViewById(R.id.login_name);
		verifyCodeText = (EditText) this.findViewById(R.id.verify_code);
		passwordText = (EditText) this.findViewById(R.id.edit_pass);
		password2Text = (EditText) this.findViewById(R.id.edit_pass_2);
		verifyCodeBtn = (Button) this.findViewById(R.id.verify_code_button);
		registerBtn = (Button) this.findViewById(R.id.register_btn);
		colseBtn = (Button) this.findViewById(R.id.close);
		
		regResult = (TextView) this.findViewById(R.id.register_result);
		
		colseBtn.setOnClickListener(this);
		registerBtn.setOnClickListener(this);
		verifyCodeBtn.setOnClickListener(this);
		
		TelephonyManager telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
		String phone = telephonyManager.getLine1Number();
		loginNameText.setText(phone);
	}

	@Override
	public void onClick(View v) {
		
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return false;
	}

}
