package com.withiter.quhao.activity;

import java.util.HashMap;
import java.util.Map;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
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
import com.withiter.quhao.task.MoreVersionCheckTask;
import com.withiter.quhao.util.ActivityUtil;
import com.withiter.quhao.util.tool.ParseJson;
import com.withiter.quhao.util.tool.ProgressDialogUtil;
import com.withiter.quhao.vo.AppVersionVO;

public class MoreFragment extends Fragment implements OnClickListener{

	private LinearLayout settings;
	private LinearLayout aboutUs;
	private LinearLayout opinion;
	private LinearLayout version;
	private LinearLayout moreShare;
	private LinearLayout help;
	private LinearLayout loginStatus;

	private ImageView loginStatusImg;

	private TextView loginStatusTxt;
	
	private View contentView;
	
	private final int UNLOCK_CLICK = 1000;
	
	private boolean isClick;
	
	private ProgressDialogUtil progressDialogUtil;
	
	protected Handler unlockHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == UNLOCK_CLICK) {
				
				isClick = false;
			}
		}
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// TODO add default view here
		if (!ActivityUtil.isNetWorkAvailable(getActivity())) {
			Builder dialog = new AlertDialog.Builder(getActivity());
			dialog.setTitle("温馨提示").setMessage("Wifi/蜂窝网络未打开，或者网络情况不是很好哟").setPositiveButton("确定", null);
			dialog.show();
			
		}
		
		ShareSDK.initSDK(getActivity());
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		if(contentView != null)
		{
			ViewGroup vg = (ViewGroup) contentView.getParent();
			vg.removeView(contentView);
			return contentView;
		}
		
		contentView = inflater.inflate(R.layout.more_fragment_layout, container,false);
		
		settings = (LinearLayout) contentView.findViewById(R.id.more_settings);
		version = (LinearLayout) contentView.findViewById(R.id.more_version);
		opinion = (LinearLayout) contentView.findViewById(R.id.more_opinion);
		moreShare = (LinearLayout) contentView.findViewById(R.id.more_share);
		help = (LinearLayout) contentView.findViewById(R.id.more_help);
		aboutUs = (LinearLayout) contentView.findViewById(R.id.more_aboutus);
		loginStatus = (LinearLayout) contentView.findViewById(R.id.more_login_status);
		settings.setOnClickListener(this);
		version.setOnClickListener(this);
		opinion.setOnClickListener(this);
		moreShare.setOnClickListener(this);
		help.setOnClickListener(this);
		aboutUs.setOnClickListener(this);
		loginStatus.setOnClickListener(this);

		loginStatusImg = (ImageView) contentView.findViewById(R.id.more_login_status_img);
		loginStatusTxt = (TextView) contentView.findViewById(R.id.more_login_status_txt);
		refreshLoginStatus();
		return contentView;		
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
	
	protected Handler toastHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == UNLOCK_CLICK) {
				
				Map<String, Object> toastParams = (Map<String, Object>) msg.obj;
//				Toast.makeText((Context)toastParams.get("activity"), toastParams.get("text"), );
				
				Toast.makeText((Context)toastParams.get("activity"), Integer.parseInt(String.valueOf(toastParams.get("text"))), Integer.parseInt(String.valueOf(toastParams.get("toastLength")))).show();
			}
		}
	};

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
	public void onResume() {
		refreshLoginStatus();
		super.onResume();
	}
	
	@Override
	public void onDestroy() {
		ShareSDK.stopSDK(getActivity());
		super.onDestroy();
	}
	
	@Override
	public void onClick(View v) {
		if (isClick) {
			return;
		}
		isClick = true;
		progressDialogUtil = new ProgressDialogUtil(getActivity(), R.string.empty, R.string.querying, false);
		progressDialogUtil.showProgress();
		switch (v.getId()) {
		case R.id.more_settings:// 系统设置
			progressDialogUtil.closeProgress();
			unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			Intent intent = new Intent(getActivity(), SettingsActivity.class);
			startActivity(intent);
			getActivity().overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
			break;
		case R.id.more_aboutus:// 关于我们
			progressDialogUtil.closeProgress();
			unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			Intent intent1 = new Intent(getActivity(), AboutUsActivity.class);
			startActivity(intent1);
			getActivity().overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
			break;
		case R.id.more_opinion:// 反馈
			progressDialogUtil.closeProgress();
			unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			Intent intent2 = new Intent(getActivity(), OpinionActivity.class);
			startActivity(intent2);
			getActivity().overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
			break;
		case R.id.more_help:// 帮助
			progressDialogUtil.closeProgress();
			unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			Intent intent3 = new Intent(getActivity(), HelpActivity.class);
			startActivity(intent3);
			getActivity().overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
			break;
		case R.id.more_version:// 版本检测
			progressDialogUtil.closeProgress();
			unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			
			final int currentVersion = ActivityUtil.getVersionCode(getActivity());
			String url = "app/appCode";
			final MoreVersionCheckTask task = new MoreVersionCheckTask(R.string.waitting, getActivity(), url);
			task.execute(new Runnable() {
				
				@Override
				public void run() {
					
					String result = task.result;
					final AppVersionVO avo = ParseJson.convertToAppVersionVO(result);
					if (avo == null) {
						Builder dialog = new AlertDialog.Builder(getActivity());
						dialog.setTitle("温馨提示").setMessage("网络情况不是很好哟").setPositiveButton("确定", null);
						dialog.show();
						return;
					}

					if (avo.android == currentVersion) {
						Builder dialog = new AlertDialog.Builder(getActivity());
						dialog.setTitle("温馨提示").setMessage("APP已经是最新版").setPositiveButton("确定", null);
						dialog.show();
						return;
					}

					if (avo.android > currentVersion) {
						// TODO there is bug here
						Dialog dialog = new AlertDialog.Builder(getActivity()).setTitle("软件更新").setMessage("软件有更新，建议更新到最新版本")
						// 设置内容
								.setPositiveButton("更新",// 设置确定按钮
										new DialogInterface.OnClickListener() {
											@Override
											public void onClick(DialogInterface dialog, int which) {
												dialog.dismiss();
												Uri uri = Uri.parse("http://www.quhao.la/");
												Intent intent = new Intent(Intent.ACTION_VIEW, uri);
												startActivity(intent);
												/*
												final ProgressDialog pBar = new ProgressDialog(getActivity());
												pBar.setTitle("正在下载");
												pBar.setMessage("请稍候...");
												pBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);

												// downFile();
												pBar.show();
												new Thread() {
													public void run() {
														HttpClient client = new DefaultHttpClient();
														HttpGet get = new HttpGet(QuhaoConstant.HTTP_URL + "app/down?t=android");
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
															pBar.dismiss();
//															down();
															
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
														catch (Exception e)
														{
															e.printStackTrace();
														}
													}
												}.start();
												*/

											}
										}).setNegativeButton("暂不更新", new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int whichButton) {
										// 点击"取消"按钮之后退出程序
//										getActivity().finish();
									}
								}).create();// 创建
						// 显示对话框
						dialog.show();
					}
					unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				}
			},new Runnable() {
				
				@Override
				public void run() {
					
					Builder dialog = new AlertDialog.Builder(getActivity());
					dialog.setTitle("温馨提示").setMessage("网络情况不是很好哟").setPositiveButton("确定", null);
					dialog.show();
					unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				}
			});
			/*
			try {
				String result = CommonHTTPRequest.get(url);
				if (StringUtils.isNull(result)) {
					Builder dialog = new AlertDialog.Builder(getActivity());
					dialog.setTitle("温馨提示").setMessage("网络情况不是很好哟").setPositiveButton("确定", null);
					dialog.show();
					return;
				}

				final AppVersionVO avo = ParseJson.convertToAppVersionVO(result);
				if (avo == null) {
					Builder dialog = new AlertDialog.Builder(getActivity());
					dialog.setTitle("温馨提示").setMessage("网络情况不是很好哟").setPositiveButton("确定", null);
					dialog.show();
					return;
				}

				if (avo.android == currentVersion) {
					Builder dialog = new AlertDialog.Builder(getActivity());
					dialog.setTitle("温馨提示").setMessage("APP已经是最新版").setPositiveButton("确定", null);
					dialog.show();
					return;
				}

				if (avo.android > currentVersion) {
					// TODO there is bug here
					Dialog dialog = new AlertDialog.Builder(getActivity()).setTitle("软件更新").setMessage("软件有更新，建议更新到最新版本")
					// 设置内容
							.setPositiveButton("更新",// 设置确定按钮
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which) {
											ProgressDialog pBar = new ProgressDialog(getActivity());
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
									getActivity().finish();
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
			 */
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
			oks.setImageUrl("http://www.quhao.la/public/images/home/site_iphone.png");
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
					toastParams.put("activity", getActivity());
					toastParams.put("text", R.string.share_cancel);
					toastParams.put("toastLength", Toast.LENGTH_LONG);
					toastHandler.obtainMessage(1000, toastParams).sendToTarget();
					
				}

				@Override
				public void onComplete(Platform arg0, int arg1,
						HashMap<String, Object> arg2) {
					Map<String, Object> toastParams = new HashMap<String, Object>();
					toastParams.put("activity", getActivity());
					toastParams.put("text", R.string.share_success);
					toastParams.put("toastLength", Toast.LENGTH_LONG);
					toastHandler.obtainMessage(1000, toastParams).sendToTarget();
				}

				@Override
				public void onError(Platform arg0, int arg1, Throwable arg2) {
					Map<String, Object> toastParams = new HashMap<String, Object>();
					toastParams.put("activity", getActivity());
					toastParams.put("text", R.string.share_error);
					toastParams.put("toastLength", Toast.LENGTH_LONG);
					toastHandler.obtainMessage(1000, toastParams).sendToTarget();
				}

				
				
			});
			oks.show(getActivity());
//			oks.setapp
 
 
//			Intent intent4 = new Intent(getActivity(), SinaInfoActivity.class);
//			startActivity(intent4);
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
				Intent intent5 = new Intent(getActivity(), LoginActivity.class);
				intent5.putExtra("activityName", this.getClass().getName());
				intent5.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent5);
				getActivity().overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
			}
			break;

		default:
			break;
		}

	}	
}