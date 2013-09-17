package com.withiter.quhao.activity;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.EditText;

import com.withiter.quhao.R;
import com.withiter.quhao.util.QuhaoLog;
import com.withiter.quhao.util.StringUtils;
import com.withiter.quhao.util.tool.QuhaoConstant;

public class PersonCenterActivity extends AppStoreActivity {

	private final static String TAG = PersonCenterActivity.class.getName();

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

		SharedPreferences settings = getSharedPreferences(
				QuhaoConstant.SHARED_PREFERENCES, 0);
		String isLogin = settings.getString(QuhaoConstant.IS_LOGIN, "false");

		// check already login or not
		if (StringUtils.isNull(isLogin) || "false".equalsIgnoreCase(isLogin)) {
			// TODO add not login business here
			QuhaoLog.i(TAG, "login check: not login");
			LayoutInflater inflater = (LayoutInflater) getApplicationContext()
					.getSystemService(LAYOUT_INFLATER_SERVICE);
			View view = inflater.inflate(R.layout.login_layout, null);

//			final EditText username = (EditText) view
//					.findViewById(R.id.txt_username);
//			final EditText password = (EditText) view
//					.findViewById(R.id.txt_password);
//			username.setText("3001");
//			password.setText("3001");
			AlertDialog.Builder ad = new AlertDialog.Builder(PersonCenterActivity.this);
			ad.setView(view);
			ad.setTitle("账号登陆");
			ad.show();

			SharedPreferences sharedPreferences = getSharedPreferences(
					QuhaoConstant.SHARED_PREFERENCES, 0);
			SharedPreferences.Editor editor = sharedPreferences.edit();
			editor.putString(QuhaoConstant.IS_LOGIN, "true");
			editor.commit();

		} else {
			// TODO add already login business here
			QuhaoLog.i(TAG, "login check: login");
		}

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		return false;
	}

}
