package com.withiter.quhao.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

import com.withiter.quhao.QHClientApplication;
import com.withiter.quhao.R;
import com.withiter.quhao.util.ActivityUtil;
import com.withiter.quhao.util.StringUtils;
import com.withiter.quhao.util.http.CommonHTTPRequest;
import com.withiter.quhao.util.tool.ParseJson;
import com.withiter.quhao.util.tool.ProgressDialogUtil;
import com.withiter.quhao.vo.AppVersionVO;

public class MoreActivity extends QuhaoBaseActivity {

	private LinearLayout settings;
	private LinearLayout aboutUs;
	private LinearLayout opinion;
	private LinearLayout version;
	private LinearLayout moreShare;
	private LinearLayout help;
	private LinearLayout loginStatus;

	private ImageView loginStatusImg;

	private TextView loginStatusTxt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.more_layout);
		super.onCreate(savedInstanceState);

		btnCategory.setOnClickListener(goCategory(this));
		btnNearby.setOnClickListener(goNearby(this));
		btnPerson.setOnClickListener(goPersonCenter(this));
		btnMore.setOnClickListener(goMore(this));

		settings = (LinearLayout) this.findViewById(R.id.more_settings);
		version = (LinearLayout) this.findViewById(R.id.more_version);
		opinion = (LinearLayout) this.findViewById(R.id.more_opinion);
		moreShare = (LinearLayout) this.findViewById(R.id.more_share);
		help = (LinearLayout) this.findViewById(R.id.more_help);
		aboutUs = (LinearLayout) this.findViewById(R.id.more_aboutus);
		loginStatus = (LinearLayout) this.findViewById(R.id.more_login_status);
		settings.setOnClickListener(this);
		version.setOnClickListener(this);
		opinion.setOnClickListener(this);
		moreShare.setOnClickListener(this);
		help.setOnClickListener(this);
		aboutUs.setOnClickListener(this);
		loginStatus.setOnClickListener(this);

		loginStatusImg = (ImageView) this.findViewById(R.id.more_login_status_img);
		loginStatusTxt = (TextView) this.findViewById(R.id.more_login_status_txt);

		// TODO add default view here
		if (!networkOK) {
			unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			Builder dialog = new AlertDialog.Builder(MoreActivity.this);
			dialog.setTitle("温馨提示").setMessage("Wifi/蜂窝网络未打开，或者网络情况不是很好哟").setPositiveButton("确定", null);
			dialog.show();
			
			return;
		}
		
		refreshLoginStatus();
		ShareSDK.initSDK(this);
	}

	private void refreshLoginStatus() {
		boolean loginStatus = QHClientApplication.getInstance().isLogined;
		if (loginStatus) {
			loginStatusImg.setImageResource(R.drawable.login_status);
			loginStatusTxt.setText(R.string.more_logout);
		} else {
			loginStatusImg.setImageResource(R.drawable.logout_status);
			loginStatusTxt.setText(R.string.more_no_login);
		}
	}

	@Override
	public void onClick(View v) {
		if (isClick) {
			return;
		}
		progressDialogUtil = new ProgressDialogUtil(this, R.string.empty, R.string.querying, false);
		progressDialogUtil.showProgress();
		switch (v.getId()) {
		case R.id.more_settings:// 系统设置
			progressDialogUtil.closeProgress();
			unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			Intent intent = new Intent(this, SettingsActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
			break;
		case R.id.more_aboutus:// 关于我们
			progressDialogUtil.closeProgress();
			unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			Intent intent1 = new Intent(this, AboutUsActivity.class);
			startActivity(intent1);
			overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
			break;
		case R.id.more_opinion:// 反馈
			progressDialogUtil.closeProgress();
			unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			Intent intent2 = new Intent(this, OpinionActivity.class);
			startActivity(intent2);
			overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
			break;
		case R.id.more_help:// 帮助
			progressDialogUtil.closeProgress();
			unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			Intent intent3 = new Intent(this, HelpActivity.class);
			startActivity(intent3);
			overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
			break;
		case R.id.more_version:// 版本检测
			progressDialogUtil.closeProgress();
			unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			int currentVersion = ActivityUtil.getVersionCode(this);
			String url = "app/appCode";
			try {
				String result = CommonHTTPRequest.get(url);
				if (StringUtils.isNull(result)) {
					Builder dialog = new AlertDialog.Builder(MoreActivity.this);
					dialog.setTitle("温馨提示").setMessage("网络情况不是很好哟").setPositiveButton("确定", null);
					dialog.show();
					return;
				}

				final AppVersionVO avo = ParseJson.convertToAppVersionVO(result);
				if (avo == null) {
					Builder dialog = new AlertDialog.Builder(MoreActivity.this);
					dialog.setTitle("温馨提示").setMessage("网络情况不是很好哟").setPositiveButton("确定", null);
					dialog.show();
					return;
				}

				if (avo.android == currentVersion) {
					Builder dialog = new AlertDialog.Builder(MoreActivity.this);
					dialog.setTitle("温馨提示").setMessage("APP已经是最新版").setPositiveButton("确定", null);
					dialog.show();
					return;
				}

				if (avo.android > currentVersion) {
					// TODO there is bug here
					Dialog dialog = new AlertDialog.Builder(MoreActivity.this).setTitle("软件更新").setMessage("软件有更新，建议更新到最新版本")
					// 设置内容
							.setPositiveButton("更新",// 设置确定按钮
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which) {
											ProgressDialog pBar = new ProgressDialog(MoreActivity.this);
											pBar.setTitle("正在下载");
											pBar.setMessage("请稍候...");
											pBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);

											// downFile();
											pBar.show();
											new Thread() {
												public void run() {
													HttpClient client = new DefaultHttpClient();
													HttpGet get = new HttpGet("app/down?t=android");
													HttpResponse response;
													try {
														response = client.execute(get);
														HttpEntity entity = response.getEntity();
														long length = entity.getContentLength();
														InputStream is = entity.getContent();
														FileOutputStream fileOutputStream = null;
														if (is != null) {
															File file = new File(Environment.getExternalStorageDirectory(), "quhaola-v" + avo.android + ".apk");
															fileOutputStream = new FileOutputStream(file);
															byte[] buf = new byte[1024];
															int ch = -1;
															int count = 0;
															while ((ch = is.read(buf)) != -1) {
																fileOutputStream.write(buf, 0, ch);
																count += ch;
																if (length > 0) {
																}
															}
														}
														fileOutputStream.flush();
														if (fileOutputStream != null) {
															fileOutputStream.close();
														}

//														down();
														
														Intent intent = new Intent(Intent.ACTION_VIEW);  
													    intent.setDataAndType(Uri.fromFile(new File(Environment  
													            .getExternalStorageDirectory(), "quhaola-v" + avo.android + ".apk")),  
													            "application/vnd.android.package-archive");  
													    startActivity(intent);  
														
													} catch (ClientProtocolException e) {
														e.printStackTrace();
													} catch (IOException e) {
														e.printStackTrace();
													}
												}
											}.start();

										}
									}).setNegativeButton("暂不更新", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int whichButton) {
									// 点击"取消"按钮之后退出程序
									finish();
								}
							}).create();// 创建
					// 显示对话框
					dialog.show();
				}

			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			break;
		case R.id.more_share:// 分享给好友
			// 显示分享界面
			progressDialogUtil.closeProgress();
			unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			/*
			OnekeyShare oks = new OnekeyShare();
			oks.setNotification(R.drawable.ic_launcher, "quhao");
			oks.setTitle("nihao, share sdk quhao application");
			oks.setTitleUrl("http://www.withiter.com");
			oks.setText("welcome to share quhao application");
			oks.setImageUrl("http://f1.sharesdk.cn/imgs/2014/02/26/owWpLZo_638x960.jpg");
			oks.setUrl("http://www.withiter.com");
			oks.setSilent(false);
			oks.setCallback(new PlatformActionListener(){

				@Override
				public void onCancel(Platform arg0, int arg1) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void onComplete(Platform arg0, int arg1,
						HashMap<String, Object> arg2) {
					
				}

				@Override
				public void onError(Platform arg0, int arg1, Throwable arg2) {
					
				}
				
			});
			oks.show(this);*/
			final OnekeyShare oks = new OnekeyShare();
			oks.setNotification(R.drawable.ic_launcher, getResources().getString(R.string.app_name));
//			oks.setAddress("12345678901");
			oks.setTitle("取号啦");
			oks.setTitleUrl("http://service.quhao.la/");
			oks.setText("取号啦--让你排队不用等！");
//			oks.setImagePath(MainActivity.TEST_IMAGE);
			oks.setImageUrl("http://b.hiphotos.baidu.com/baike/c0%3Dbaike80%2C5%2C5%2C80%2C26/sign=e2d3e87841a98226accc2375ebebd264/a044ad345982b2b7d9b553c933adcbef77094b36acafa3a8.jpg");
			oks.setUrl("http://service.quhao.la/");
//			oks.setFilePath(MainActivity.TEST_IMAGE);
//			oks.setComment(getResources().getString(R.string.share));
//			oks.setSite(getResources().getString(R.string.app_name));
//			oks.setSiteUrl("http://sharesdk.cn");
//			oks.setVenueName("ShareSDK");
//			oks.setVenueDescription("This is a beautiful place!");
//			oks.setLatitude(23.056081f);
//			oks.setLongitude(113.385708f);
			oks.setSilent(false);
			oks.setCallback(new PlatformActionListener(){

				@Override
				public void onCancel(Platform arg0, int arg1) {
					Map<String, Object> toastParams = new HashMap<String, Object>();
					toastParams.put("activity", MoreActivity.this);
					toastParams.put("text", R.string.share_cancel);
					toastParams.put("toastLength", Toast.LENGTH_SHORT);
					toastHandler.obtainMessage(1000, toastParams).sendToTarget();
					
				}

				@Override
				public void onComplete(Platform arg0, int arg1,
						HashMap<String, Object> arg2) {
					Map<String, Object> toastParams = new HashMap<String, Object>();
					toastParams.put("activity", MoreActivity.this);
					toastParams.put("text", R.string.share_success);
					toastParams.put("toastLength", Toast.LENGTH_SHORT);
					toastHandler.obtainMessage(1000, toastParams).sendToTarget();
				}

				@Override
				public void onError(Platform arg0, int arg1, Throwable arg2) {
					Map<String, Object> toastParams = new HashMap<String, Object>();
					toastParams.put("activity", MoreActivity.this);
					toastParams.put("text", R.string.share_error);
					toastParams.put("toastLength", Toast.LENGTH_SHORT);
					toastHandler.obtainMessage(1000, toastParams).sendToTarget();
				}

				
				
			});
			oks.show(this);
//			oks.setapp
//			Intent intent4 = new Intent(this, ShareDialogActivity.class);
//			startActivity(intent4);
//			overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
			break;

		case R.id.more_login_status:// 分享给好友
			// 显示分享界面
			progressDialogUtil.closeProgress();
			unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			
			// String loginStatus = SharedprefUtil.get(MoreActivity.this,
			// QuhaoConstant.IS_LOGIN, "false");
			if (QHClientApplication.getInstance().isLogined) {
				loginHandler.obtainMessage(200, loginStatus).sendToTarget();
			} else {
				Intent intent5 = new Intent(MoreActivity.this, LoginActivity.class);
				intent5.putExtra("activityName", this.getClass().getName());
				intent5.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent5);
				overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
			}
			break;

		default:
			break;
		}

	}

	// TODO : add static to the all Handler class, because such class may be
	// caused leaks
	// http://stackoverflow.com/questions/11407943/this-handler-class-should-be-static-or-leaks-might-occur-incominghandler
	protected Handler loginHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == 200) {
				super.handleMessage(msg);
				// SharedprefUtil.put(MoreActivity.this, QuhaoConstant.IS_LOGIN,
				// "false");
				QHClientApplication.getInstance().isLogined = false;
				loginStatusImg.setImageResource(R.drawable.login_status);
				loginStatusTxt.setText(R.string.more_no_login);
			}
		}
	};

	@Override
	protected void onResume() {
		refreshLoginStatus();
		super.onResume();
	}
	
	@Override
	protected void onDestroy() {
		ShareSDK.stopSDK(this);
		super.onDestroy();
	}
	
	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {
		return false;
	}

}
