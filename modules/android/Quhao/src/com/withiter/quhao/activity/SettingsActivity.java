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

import com.withiter.quhao.QHClientApplication;
import com.withiter.quhao.R;
import com.withiter.quhao.util.QuhaoLog;
import com.withiter.quhao.util.tool.ImageUtil;
import com.withiter.quhao.util.tool.ProgressDialogUtil;
import com.withiter.quhao.util.tool.QuhaoConstant;
import com.withiter.quhao.util.tool.SharedprefUtil;

public class SettingsActivity extends QuhaoBaseActivity {

	private LinearLayout cleanPicture;
	private LinearLayout cleanCache;
	private LinearLayout imageShow;
	private ImageView imageView;
	
	public static boolean backClicked = false;
	private String LOGTAG = SettingsActivity.class.getName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.more_setting_layout);
		super.onCreate(savedInstanceState);

		cleanPicture = (LinearLayout) this.findViewById(R.id.more_settings_cleanpicture);
		cleanCache = (LinearLayout) this.findViewById(R.id.more_settings_cleancache);
		imageShow = (LinearLayout) this.findViewById(R.id.more_settings_imageshow);

		imageView = (ImageView) this.findViewById(R.id.more_settings_image);

		String isWifi = SharedprefUtil.get(this, QuhaoConstant.IS_2G3G_READ, "true");

		if ("true".equals(isWifi)) {
			QHClientApplication.getInstance().is2G3GRead = true; 
			imageView.setImageResource(R.drawable.checkbox_unchecked);
		} else {
			QHClientApplication.getInstance().is2G3GRead = false;
			imageView.setImageResource(R.drawable.checkbox_checked);
		}
		
		if(QHClientApplication.getInstance().is2G3GRead || (!QHClientApplication.getInstance().is2G3GRead && QHClientApplication.getInstance().isWifiOpen))
		{
			QHClientApplication.getInstance().canLoadImg = true;
		}
		else
		{
			QHClientApplication.getInstance().canLoadImg = false;
		}

		cleanPicture.setOnClickListener(this);
		cleanCache.setOnClickListener(this);
		imageShow.setOnClickListener(this);

		btnBack.setOnClickListener(goBack(this,this.getClass().getName()));
	}

	@Override
	public void onClick(View v) {

		if (isClick) {
			return;
		}
		isClick = true;
		switch (v.getId()) {
		case R.id.more_settings_cleanpicture:
			new CleanPicTask().execute();
			break;
		case R.id.more_settings_cleancache:
			progressDialogUtil = new ProgressDialogUtil(SettingsActivity.this, R.string.empty, R.string.deleting, false);
			progressDialogUtil.showProgress();
			SharedprefUtil.clear(this);
			String is2G3GRead = SharedprefUtil.get(this, QuhaoConstant.IS_2G3G_READ, "true");
			if ("true".equals(is2G3GRead)) {
				imageView.setImageResource(R.drawable.checkbox_unchecked);
				is2G3GRead = "true";
				QHClientApplication.getInstance().is2G3GRead = true; 
			} else {
				imageView.setImageResource(R.drawable.checkbox_checked);
				is2G3GRead = "false";
				QHClientApplication.getInstance().is2G3GRead = false; 
			}
			
			if(QHClientApplication.getInstance().is2G3GRead || (!QHClientApplication.getInstance().is2G3GRead && QHClientApplication.getInstance().isWifiOpen))
			{
				QHClientApplication.getInstance().canLoadImg = true;
			}
			else
			{
				QHClientApplication.getInstance().canLoadImg = false;
			}
			
			progressDialogUtil.closeProgress();
			Toast.makeText(SettingsActivity.this, "清除成功", Toast.LENGTH_LONG).show();
			unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);

			break;
		case R.id.more_settings_imageshow:
			progressDialogUtil = new ProgressDialogUtil(SettingsActivity.this, R.string.empty, R.string.deleting, false);
			progressDialogUtil.showProgress();

			String is2G3GRead1 = SharedprefUtil.get(this, QuhaoConstant.IS_2G3G_READ, "true");
			if ("true".equals(is2G3GRead1)) {
				imageView.setImageResource(R.drawable.checkbox_checked);
				is2G3GRead1 = "false";
				QHClientApplication.getInstance().is2G3GRead = false;
			} else {
				imageView.setImageResource(R.drawable.checkbox_unchecked);
				is2G3GRead1 = "true";
				QHClientApplication.getInstance().is2G3GRead = true;
			}

			if(QHClientApplication.getInstance().is2G3GRead || (!QHClientApplication.getInstance().is2G3GRead && QHClientApplication.getInstance().isWifiOpen))
			{
				QHClientApplication.getInstance().canLoadImg = true;
			}
			else
			{
				QHClientApplication.getInstance().canLoadImg = false;
			}
			
			SharedprefUtil.put(SettingsActivity.this, QuhaoConstant.IS_2G3G_READ, is2G3GRead1);
			progressDialogUtil.closeProgress();
			unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			break;
		default:
			break;
		}
	}

	class CleanPicTask extends AsyncTask<Void, Void, Boolean> {
		ProgressDialogUtil progress;

		@Override
		protected void onPreExecute() {
			progress = new ProgressDialogUtil(SettingsActivity.this, R.string.empty, R.string.deleting, false);
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
			unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
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

}
