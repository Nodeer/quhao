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

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.more_setting_layout);
		super.onCreate(savedInstanceState);

		cleanPicture = (LinearLayout) this.findViewById(R.id.more_settings_cleanpicture);
		cleanCache = (LinearLayout) this.findViewById(R.id.more_settings_cleancache);
		imageShow = (LinearLayout) this.findViewById(R.id.more_settings_imageshow);

		imageView = (ImageView) this.findViewById(R.id.more_settings_image);

		String isLoadImg = SharedprefUtil.get(this, QuhaoConstant.IS_LOAD_IMG, "false");

		if ("true".equals(isLoadImg)) {
			QHClientApplication.getInstance().canLoadImg = true; 
			imageView.setImageResource(R.drawable.checkbox_checked);
		} else {
			QHClientApplication.getInstance().canLoadImg = false;
			imageView.setImageResource(R.drawable.checkbox_unchecked);
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
			
			String isLoadImg = SharedprefUtil.get(this, QuhaoConstant.IS_LOAD_IMG, "false");
			if ("true".equals(isLoadImg)) {
				imageView.setImageResource(R.drawable.checkbox_checked);
				isLoadImg = "true";
				QHClientApplication.getInstance().canLoadImg = true; 
			} else {
				imageView.setImageResource(R.drawable.checkbox_unchecked);
				isLoadImg = "false";
				QHClientApplication.getInstance().canLoadImg = false; 
			}
			SharedprefUtil.clear(this);
			
			SharedprefUtil.put(this, QuhaoConstant.IS_LOAD_IMG, isLoadImg);
			progressDialogUtil.closeProgress();
			Toast.makeText(SettingsActivity.this, "清除成功", Toast.LENGTH_LONG).show();
			unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);

			break;
		case R.id.more_settings_imageshow:
			progressDialogUtil = new ProgressDialogUtil(SettingsActivity.this, R.string.empty, R.string.deleting, false);
			progressDialogUtil.showProgress();

			String isLoadImg1 = SharedprefUtil.get(this, QuhaoConstant.IS_LOAD_IMG, "false");
			if ("true".equals(isLoadImg1)) {
				imageView.setImageResource(R.drawable.checkbox_unchecked);
				isLoadImg1 = "false";
				QHClientApplication.getInstance().canLoadImg = false;
			} else {
				imageView.setImageResource(R.drawable.checkbox_checked);
				isLoadImg1 = "true";
				QHClientApplication.getInstance().canLoadImg = true;
			}

			SharedprefUtil.put(SettingsActivity.this, QuhaoConstant.IS_LOAD_IMG, isLoadImg1);
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
	}

	@Override
	protected void onResume() {
		backClicked = false;
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
		if (backClicked) {
			overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
		}
	}

}
