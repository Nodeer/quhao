package com.withiter.quhao.activity;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
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
import com.withiter.quhao.util.StringUtils;
import com.withiter.quhao.util.tool.FileUtil;
import com.withiter.quhao.util.tool.ImageUtil;
import com.withiter.quhao.util.tool.ParseJson;
import com.withiter.quhao.util.tool.ProgressDialogUtil;
import com.withiter.quhao.util.tool.QuhaoConstant;
import com.withiter.quhao.util.tool.SharedprefUtil;
import com.withiter.quhao.vo.AppVersionVO;

public class MoreFragment extends Fragment implements OnClickListener{

	private LinearLayout aboutUs;
	private LinearLayout opinion;
	private LinearLayout version;
	private LinearLayout moreShare;
	private LinearLayout help;
	private LinearLayout moreShareAuth;
	private LinearLayout cleanPicture;
	private LinearLayout imageShow;
	private ImageView imageView;
	private Platform sina;
	private ImageView loginStatusImg;
	private TextView loginStatusTxt;
	private View contentView;
	private final int UNLOCK_CLICK = 1000;
	private boolean isClick;
	private ProgressDialogUtil progressDialogUtil;
	
	private String shareImagePath;
	
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
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		if (!ActivityUtil.isNetWorkAvailable(getActivity())) {
			Toast.makeText(getActivity(), R.string.network_error_info, Toast.LENGTH_SHORT).show();
		}
		shareImagePath = FileUtil.saveLogo(getActivity());
		if(contentView != null) {
			ViewGroup vg = (ViewGroup) contentView.getParent();
			vg.removeView(contentView);
			return contentView;
		}
		
		contentView = inflater.inflate(R.layout.more_fragment_layout, container,false);
		version = (LinearLayout) contentView.findViewById(R.id.more_version);
		opinion = (LinearLayout) contentView.findViewById(R.id.more_opinion);
		moreShare = (LinearLayout) contentView.findViewById(R.id.more_share);
		help = (LinearLayout) contentView.findViewById(R.id.more_help);
		aboutUs = (LinearLayout) contentView.findViewById(R.id.more_aboutus);
		moreShareAuth = (LinearLayout) contentView.findViewById(R.id.more_share_auth);
		
		cleanPicture = (LinearLayout) contentView.findViewById(R.id.more_settings_cleanpicture);
		imageShow = (LinearLayout) contentView.findViewById(R.id.more_settings_imageshow);

		imageView = (ImageView) contentView.findViewById(R.id.more_settings_image);

		String isLoadImg = SharedprefUtil.get(getActivity(), QuhaoConstant.IS_LOAD_IMG, "false");

		if ("true".equals(isLoadImg)) {
			QHClientApplication.getInstance().canLoadImg = true; 
			imageView.setImageResource(R.drawable.checkbox_checked);
		} else {
			QHClientApplication.getInstance().canLoadImg = false;
			imageView.setImageResource(R.drawable.checkbox_unchecked);
		}
		
		cleanPicture.setOnClickListener(this);
		imageShow.setOnClickListener(this);
		
		version.setOnClickListener(this);
		opinion.setOnClickListener(this);
		moreShare.setOnClickListener(this);
		help.setOnClickListener(this);
		aboutUs.setOnClickListener(this);
		moreShareAuth.setOnClickListener(this);
		return contentView;
	}
	
	protected Handler toastHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == UNLOCK_CLICK) {
				Map<String, Object> toastParams = (Map<String, Object>) msg.obj;
				Toast.makeText((Context)toastParams.get("activity"), Integer.parseInt(String.valueOf(toastParams.get("text"))), Integer.parseInt(String.valueOf(toastParams.get("toastLength")))).show();
			}
		}
	};

	protected Handler loginHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == 200) {
				super.handleMessage(msg);
				QHClientApplication.getInstance().isLogined = false;
				loginStatusImg.setImageResource(R.drawable.login_status);
				loginStatusTxt.setText(R.string.more_no_login);
			}
		}
	};
	
	private String getName(Platform plat) {
		if (plat == null) {
			return "";
		}

		String name = plat.getName();
		if (name == null) {
			return "";
		}

		int resId = cn.sharesdk.framework.utils.R.getStringRes(getActivity(), plat.getName());
		return getActivity().getString(resId);
	}
	
	@Override
	public void onResume() {
		ShareSDK.initSDK(this.getActivity());
		sina = ShareSDK.getPlatform(getActivity(), "SinaWeibo");
		sina.SSOSetting(true);
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
		switch (v.getId()) {
		case R.id.more_aboutus:// 关于我们
			unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			Intent intent1 = new Intent(getActivity(), AboutUsActivity.class);
			startActivity(intent1);
			break;
		case R.id.more_opinion:// 反馈
			unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			Intent intent2 = new Intent(getActivity(), OpinionActivity.class);
			startActivity(intent2);
			break;
		case R.id.more_help:// 帮助
			unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			Intent intent3 = new Intent(getActivity(), HelpActivity.class);
			startActivity(intent3);
			break;
		case R.id.more_version:// 版本检测
			progressDialogUtil.showProgress();
			progressDialogUtil.closeProgress();
			if(!ActivityUtil.isNetWorkAvailable(getActivity())) {
				Toast.makeText(getActivity(), R.string.network_error_info, Toast.LENGTH_SHORT).show();
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				return;
			}
			
			final String currentVersion = ActivityUtil.getVersionName(getActivity());
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
						unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
						return;
					}

					if (currentVersion.equals(avo.android)) {
						Builder dialog = new AlertDialog.Builder(getActivity());
						dialog.setTitle("温馨提示").setMessage("APP已经是最新版").setPositiveButton("确定", null);
						dialog.show();
						unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
						return;
					}

					if (currentVersion.compareTo(avo.android) < 0) {
						unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
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
			break;
		case R.id.more_share:// 分享给好友
			// 显示分享界面
			unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			showShare(false, null);
			break;
		case R.id.more_share_auth:
			unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			ShareSDK.initSDK(this.getActivity());
			sina = ShareSDK.getPlatform(getActivity(), "SinaWeibo");
			sina.SSOSetting(true);
			if (sina == null) {
				return;
			}

			if (sina.isValid()) {
				Dialog dialog = new AlertDialog.Builder(getActivity()).setMessage("亲，确定要取消授权吗？")
				// 设置内容
				.setPositiveButton("是",// 设置确定按钮
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								dialog.dismiss();
								sina.removeAccount();
							}
						}).setNegativeButton("否", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// 点击"取消"按钮之后退出程序
						dialog.dismiss();
					}
				}).create();// 创建
				// 显示对话框
				dialog.show();	
				return;
			}

			sina.setPlatformActionListener(new PlatformActionListener() {
				@Override
				public void onError(Platform arg0, int arg1, Throwable arg2) {
					
				}
				@Override
				public void onComplete(Platform arg0, int arg1, HashMap<String, Object> arg2) {
//					sinaHandler.sendEmptyMessage(200);
				}
				
				@Override
				public void onCancel(Platform arg0, int arg1) {
					
				}
			});
			sina.showUser(null);
			break;
		case R.id.more_settings_cleanpicture:
			new CleanPicTask().execute();
			break;
		case R.id.more_settings_imageshow:
			progressDialogUtil = new ProgressDialogUtil(getActivity(), R.string.empty, R.string.deleting, false);
			progressDialogUtil.showProgress();

			String isLoadImg1 = SharedprefUtil.get(getActivity(), QuhaoConstant.IS_LOAD_IMG, "false");
			if ("true".equals(isLoadImg1)) {
				imageView.setImageResource(R.drawable.checkbox_unchecked);
				isLoadImg1 = "false";
				QHClientApplication.getInstance().canLoadImg = false;
			} else {
				imageView.setImageResource(R.drawable.checkbox_checked);
				isLoadImg1 = "true";
				QHClientApplication.getInstance().canLoadImg = true;
			}

			SharedprefUtil.put(getActivity(), QuhaoConstant.IS_LOAD_IMG, isLoadImg1);
			progressDialogUtil.closeProgress();
			unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			break;
		default:
			unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			break;
		}

	}
	
	// 使用快捷分享完成分享（请务必仔细阅读位于SDK解压目录下Docs文件夹中OnekeyShare类的JavaDoc）
		/**ShareSDK集成方法有两种</br>
		 * 1、第一种是引用方式，例如引用onekeyshare项目，onekeyshare项目再引用mainlibs库</br>
		 * 2、第二种是把onekeyshare和mainlibs集成到项目中，本例子就是用第二种方式</br>
		 * 请看“ShareSDK 使用说明文档”，SDK下载目录中 </br>
		 * 或者看网络集成文档 http://wiki.sharesdk.cn/Android_%E5%BF%AB%E9%80%9F%E9%9B%86%E6%88%90%E6%8C%87%E5%8D%97
		 * 3、混淆时，把sample或者本例子的混淆代码copy过去，在proguard-project.txt文件中
		 *
		 *
		 * 平台配置信息有三种方式：
		 * 1、在我们后台配置各个微博平台的key
		 * 2、在代码中配置各个微博平台的key，http://sharesdk.cn/androidDoc/cn/sharesdk/framework/ShareSDK.html
		 * 3、在配置文件中配置，本例子里面的assets/ShareSDK.conf,
		 */
		private void showShare(boolean silent, String platform) {
			final OnekeyShare oks = new OnekeyShare();
			oks.setNotification(R.drawable.ic_launcher, getActivity().getString(R.string.app_name));
			oks.setAddress("");
			oks.setTitle("取号啦--让你排队不用等！");
			oks.setUrl("http://www.quhao.la");
			oks.setText("#取号啦# 发现个超牛逼的APP，再也不担心排多长的队了。我用手机直接拿号不用排队，还可以和一起排队的人扯淡聊天，快去体验全新的排队模式吧。@取号啦");
			Log.e("wjzwjz", "share image path : " + shareImagePath);
			if (StringUtils.isNotNull(shareImagePath)) {
				oks.setImagePath(shareImagePath);
			}
//			oks.setImageUrl("http://www.quhao.la/public/images/home/site_iphone.png");
			oks.setSilent(silent);
			if (platform != null) {
				oks.setPlatform(platform);
			}

			// 去除注释，可令编辑页面显示为Dialog模式
//			oks.setDialogMode();

			// 去除注释，在自动授权时可以禁用SSO方式
			oks.disableSSOWhenAuthorize();

			// 去除注释，则快捷分享的操作结果将通过OneKeyShareCallback回调
//			oks.setCallback(new OneKeyShareCallback());
//			oks.setShareContentCustomizeCallback(new ShareContentCustomizeDemo());

			// 去除注释，演示在九宫格设置自定义的图标
//			Bitmap logo = BitmapFactory.decodeResource(menu.getResources(), R.drawable.ic_launcher);
//			String label = menu.getResources().getString(R.string.app_name);
//			OnClickListener listener = new OnClickListener() {
//				public void onClick(View v) {
//					String text = "Customer Logo -- ShareSDK " + ShareSDK.getSDKVersionName();
//					Toast.makeText(menu.getContext(), text, Toast.LENGTH_SHORT).show();
//					oks.finish();
//				}
//			};
//			oks.setCustomerLogo(logo, label, listener);

			// 去除注释，则快捷分享九宫格中将隐藏新浪微博和腾讯微博
//			oks.addHiddenPlatform(SinaWeibo.NAME);
//			oks.addHiddenPlatform(TencentWeibo.NAME);

			oks.show(getActivity());
		}
		
		class CleanPicTask extends AsyncTask<Void, Void, Boolean> {
			ProgressDialogUtil progress;

			@Override
			protected void onPreExecute() {
				progress = new ProgressDialogUtil(getActivity(), R.string.empty, R.string.deleting, false);
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
					Toast.makeText(getActivity(), "清除成功", Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(getActivity(), "清除失败", Toast.LENGTH_SHORT).show();
				}
			}
		}
}