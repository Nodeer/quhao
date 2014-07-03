package com.withiter.quhao.activity;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.withiter.quhao.R;
import com.withiter.quhao.util.tool.PhoneTool;
import com.withiter.quhao.util.tool.ProgressDialogUtil;

@SuppressLint("NewApi")
public abstract class QuhaoBaseActivity extends QuhaoActivity implements OnClickListener, OnTouchListener {

	private final String TAG = QuhaoBaseActivity.class.getName();

	protected boolean isClick = false;
	protected String action = "";
	protected final int UNLOCK_CLICK = 1000;
	protected ProgressDialogUtil progressDialogUtil;

	protected Button btnCategory;
	protected Button btnNearby;
	protected Button btnPerson;
	protected Button btnMore;
	protected Button btnBack;
//	protected LinearLayout btnBackLayout;

	protected static final int FIRST_REQUEST_CODE = 1;
	// 网络是否可用
	protected static boolean networkOK = false;
	protected static String uid = "";
	protected static boolean autoLogin = false;
	
	protected ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

	protected Handler unlockHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == UNLOCK_CLICK) {
				
				isClick = false;
			}
		}
	};
	
	protected Handler toastHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == UNLOCK_CLICK) {
				
				Map<String, Object> toastParams = (Map<String, Object>) msg.obj;
//				Toast.makeText((Context)toastParams.get("activity"), toastParams.get("text"), );
				
				Toast.makeText((Context)toastParams.get("activity"), Integer.parseInt(String.valueOf(toastParams.get("text"))), Integer.parseInt(String.valueOf(toastParams.get("toastLength")))).show();
			}
		}
	};
	
	protected Handler toastStringHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == UNLOCK_CLICK) {
				
				Map<String, Object> toastParams = (Map<String, Object>) msg.obj;
//				Toast.makeText((Context)toastParams.get("activity"), toastParams.get("text"), );
				
				Toast.makeText((Context)toastParams.get("activity"), (String) toastParams.get("text"), Integer.parseInt(String.valueOf(toastParams.get("toastLength")))).show();
			}
		}
	};
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		networkOK = PhoneTool.isNetworkAvailable(this);
		btnCategory = (Button) findViewById(R.id.btnMerchantList);
		btnNearby = (Button) findViewById(R.id.btnNearby);
		btnPerson = (Button) findViewById(R.id.btnPerson);
		btnMore = (Button) findViewById(R.id.btnMore);
		btnBack = (Button) findViewById(R.id.back_btn);
//		btnBackLayout = (LinearLayout) findViewById(R.id.back_btn_layout);
	}
	
	protected OnClickListener clickBackBtn(Activity activity) {
		OnClickListener clickListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				btnBack.performClick();
//				btnBack.requestFocus();
//				btnBack.callOnClick();
				
			}
		};
		return clickListener;
	}

	/**
	 * 商家列表按钮绑定事件，点击进入商家列表页面
	 * 
	 * @param activity
	 *            需要跳转到的页面
	 * @return 绑定事件
	 */
	protected OnClickListener goBack(final Activity activity, final Object... params) {
		OnClickListener clickListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (params != null && params.length > 0) {
					Log.i(TAG, "params[0] is " + params[0]);
					if (params[0].equals(MerchantsSearchActivity.class.getName())) {
						Log.i(TAG, "backClicked: " + MerchantsSearchActivity.backClicked);
						MerchantsSearchActivity.backClicked = true;
					}
					if (params[0].equals(MerchantListActivity.class.getName())) {
						Log.i(TAG, "backClicked: " + MerchantListActivity.backClicked);
						MerchantListActivity.backClicked = true;
					}
					if (params[0].equals(CreditCostListActivity.class.getName())) {
						Log.i(TAG, "backClicked: " + CreditCostListActivity.backClicked);
						CreditCostListActivity.backClicked = true;
					}
					if (params[0].equals(SettingsActivity.class.getName())) {
						Log.i(TAG, "backClicked: " + SettingsActivity.backClicked);
						SettingsActivity.backClicked = true;
					}
					if (params[0].equals(AboutUsActivity.class.getName())) {
						Log.i(TAG, "backClicked: " + AboutUsActivity.backClicked);
						AboutUsActivity.backClicked = true;
					}
					if (params[0].equals(OpinionActivity.class.getName())) {
						Log.i(TAG, "backClicked: " + OpinionActivity.backClicked);
						OpinionActivity.backClicked = true;
					}
					if (params[0].equals(HelpActivity.class.getName())) {
						Log.i(TAG, "backClicked: " + HelpActivity.backClicked);
						HelpActivity.backClicked = true;
					}
				}
				onBackPressed();
				AnimateFirstDisplayListener.displayedImages.clear();
				activity.finish();
			}
		};
		return clickListener;
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	protected OnClickListener goMerchantsSearch(final Activity activity) {
		OnClickListener clickListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(activity, MerchantsSearchActivity.class);
				startActivity(intent);
			}
		};
		return clickListener;
	}
	
//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//	    if(keyCode == KeyEvent.KEYCODE_BACK) {
//	        // 监控返回键
//	        new Builder(QuhaoBaseActivity.this).setTitle("提示")
//	                .setIconAttribute(android.R.attr.alertDialogIcon)
//	                .setMessage("确定要退出吗?")
//	                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
//	                    @Override
//	                    public void onClick(DialogInterface dialog, int which) {
////	                    	QuhaoBaseActivity.this.finish();
////	                    	finish();
////	                    	System.exit(0);
////	                    	android.os.Process.killProcess(android.os.Process.myPid());
////	                    	ActivityManager activityMgr=(ActivityManager)QuhaoBaseActivity.this.getSystemService(ACTIVITY_SERVICE);
////	                        activityMgr.killBackgroundProcesses("com.withiter.quhao");
////	                    	QuhaoBaseActivity.this.moveTaskToBack(true);
////	                    	Intent intent = new Intent(Intent.ACTION_MAIN);
////	                    	intent.addCategory(Intent.CATEGORY_HOME);
////	                    	intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////	                    	startActivity(intent);
//	                    }})
//	                .setNegativeButton("取消", null)
//	                .create().show();
//	        return false;
//	    } else if(keyCode == KeyEvent.KEYCODE_MENU) {
//	        // 监控菜单键
////	        Toast.makeText(QuhaoBaseActivity.this, "Menu", Toast.LENGTH_SHORT).show();
//	        return false;
//	    }
//	    return super.onKeyDown(keyCode, event);
//	}
	
	private static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

		static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

		@Override
		public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
			if (loadedImage != null) {
				ImageView imageView = (ImageView) view;
				boolean firstDisplay = !displayedImages.contains(imageUri);
				if (firstDisplay) {
					FadeInBitmapDisplayer.animate(imageView, 500);
					displayedImages.add(imageUri);
				}
			}
		}
	}
}
