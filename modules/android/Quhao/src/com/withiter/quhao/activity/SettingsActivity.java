package com.withiter.quhao.activity;

import java.io.IOException;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.withiter.quhao.R;
import com.withiter.quhao.util.tool.ImageUtil;
import com.withiter.quhao.util.tool.ProgressDialogUtil;
import com.withiter.quhao.util.tool.SharedprefUtil;

public class SettingsActivity extends AppStoreActivity {

	private LinearLayout cleanPicture;
	private LinearLayout cleanCache;
	private LinearLayout imageShow;
	private ImageView imageView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.more_setting_layout);
		super.onCreate(savedInstanceState);
		
		cleanPicture = (LinearLayout) this.findViewById(R.id.more_settings_cleanpicture);
		cleanCache = (LinearLayout) this.findViewById(R.id.more_settings_cleancache);
		imageShow = (LinearLayout) this.findViewById(R.id.more_settings_imageshow);
		
		imageView = (ImageView) this.findViewById(R.id.more_settings_image);
		
		String isWifi = SharedprefUtil.get(this, "com.withiter.settings.wifi", "false");
		
		if("true".equals(isWifi))
		{
			imageView.setImageResource(R.drawable.checkbox_checked);
		}
		else
		{
			imageView.setImageResource(R.drawable.checkbox_unchecked);
		}
		
		cleanPicture.setOnClickListener(this);
		cleanCache.setOnClickListener(this);
		imageShow.setOnClickListener(this);
		
		btnBack.setOnClickListener(goBack(this));
	}

	@Override
	public void onClick(View v) {
		
		switch(v.getId())
		{
			case R.id.more_settings_cleanpicture:
				new CleanPicTask().execute();
				break;
			case R.id.more_settings_cleancache:
					
				break;
			case R.id.more_settings_imageshow:
				
				String isWifi = SharedprefUtil.get(this, "com.withiter.settings.wifi", "false");
				if("true".equals(isWifi))
				{
					imageView.setImageResource(R.drawable.checkbox_unchecked);
					isWifi = "false";
				}
				else
				{
					imageView.setImageResource(R.drawable.checkbox_checked);
					isWifi = "true";
				}
				
				SharedprefUtil.put(SettingsActivity.this,
						"com.withiter.settings.wifi", isWifi);
				
				break;
			default:
				break;
		}
	}

	class CleanPicTask extends AsyncTask<Void, Void, Boolean> {
		ProgressDialogUtil progress;

		@Override
		protected void onPreExecute() {
			progress = new ProgressDialogUtil(SettingsActivity.this, R.string.empty,
					R.string.deleting, false);
			progress.showProgress();
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				ImageUtil.getInstance().cleanPictureCache();
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			progress.closeProgress();
			if (result) {
				Toast.makeText(SettingsActivity.this, "清除成功", Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(SettingsActivity.this, "清除失败", Toast.LENGTH_LONG).show();
			}
		}
	}
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		
		return false;
	}

}
