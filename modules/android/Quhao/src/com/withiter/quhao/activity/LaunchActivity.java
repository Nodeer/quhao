package com.withiter.quhao.activity;

import com.withiter.quhao.R;
import com.withiter.quhao.R.layout;
import com.withiter.quhao.R.menu;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class LaunchActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.launch);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.launch, menu);
		return true;
	}

}
