package com.withiter.quhao.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import cn.jpush.android.api.InstrumentedActivity;

import com.withiter.quhao.R;

public class LaunchActivity extends InstrumentedActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.launch);
        Handler x = new Handler();
        x.postDelayed(new splashhandler(), 3000);
	}

	class splashhandler implements Runnable{
        public void run() {
            startActivity(new Intent(getApplication(),MainTabActivity.class));
            LaunchActivity.this.finish();
        }
    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.launch, menu);
		return true;
	}

}
