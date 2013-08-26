package com.withiter.quhao.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.Toast;

import com.withiter.quhao.QHClientApplication;
import com.withiter.quhao.R;
import com.withiter.quhao.domain.AccountInfo;
import com.withiter.quhao.util.tool.CommonTool;
import com.withiter.quhao.util.tool.InfoHelper;
import com.withiter.quhao.util.tool.ProgressDialogUtil;

public abstract class AppStoreActivity extends QuhaoActivity implements
		OnClickListener, OnTouchListener {

	private final String TAG = AppStoreActivity.class.getName();
	
	protected boolean isClick = false;
	protected String action = "";
	private final int UNLOCK_CLICK = 1000;
	protected ProgressDialogUtil progressDialogUtil;
	
	protected Button btnCategory;
	protected Button btnNearby;
	protected Button btnPerson;
	protected Button btnMore;
	protected Button btnBack;
	
	protected static final int FIRST_REQUEST_CODE = 1;
	// 网络是否可用
	protected static boolean networkOK = false;

	protected Handler unlockHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == UNLOCK_CLICK) {
				isClick = false;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 检查网络
		networkOK = CommonTool.isNetworkAvailable(this);
		// if(checkDevice() && autoLogin())
		btnCategory = (Button) findViewById(R.id.btnMerchantList);
		btnNearby = (Button) findViewById(R.id.btnNearby);
		btnPerson = (Button) findViewById(R.id.btnPerson);
		btnMore = (Button) findViewById(R.id.btnMore);
		btnBack = (Button) findViewById(R.id.back_btn);
		
	}

	/**
	 * 商家列表按钮绑定事件，点击进入商家列表页面
	 * @param activity 需要跳转到的页面
	 * @return 绑定事件
	 */
	protected OnClickListener goBack(final Activity activity) {
		OnClickListener clickListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		};
		return clickListener;
	}
	
	/**
	 * 商家列表按钮绑定事件，点击进入商家列表页面
	 * @param activity 需要跳转到的页面
	 * @return 绑定事件
	 */
	protected OnClickListener goCategory(final Activity activity) {
		OnClickListener clickListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 判断是否在当前页面
				if(activity instanceof MainActivity){
					Log.i(TAG, "refresh category page");
					((MainActivity) activity).getTopMerchantsFromServerAndDisplay();
					((MainActivity) activity).getCategoriesFromServerAndDisplay();
//					((MainActivity) activity).onCreate(null);
				}
				
//				Intent intent = new Intent(activity, MainActivity.class);
//				intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//				startActivity(intent);
//				overridePendingTransition(R.anim.main_enter, R.anim.main_exit);
			}
		};
		return clickListener;
	}
	
	/**
	 * 附近商家按钮绑定事件，点击进入附近页面
	 * @param activity 需要跳转到的页面
	 * @return 绑定事件
	 */
	protected OnClickListener goNearby(final Activity activity) {
		OnClickListener clickListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(activity, NearbyActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				startActivity(intent);
				overridePendingTransition(R.anim.main_enter, R.anim.main_exit);
			}
		};
		return clickListener;
	}
	
	/**
	 * 个人中心按钮绑定事件，点击进入个人中心页面
	 * @param activity 需要跳转到的页面
	 * @return 绑定事件
	 */
	protected OnClickListener goPersonCenter(final Activity activity) {
		OnClickListener clickListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (QHClientApplication.getInstance().isLogined) {
					Intent intent = new Intent(activity,
							PersonCenterActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
					startActivity(intent);
				} else {
					Intent intent = new Intent(activity, LoginActivity.class);
					intent.putExtra("activityName", activity.getClass().getName());
					Log.d("ok", " activity.getClass().getName() : " + activity.getClass().getName());
					intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
					startActivity(intent);
				}

				overridePendingTransition(R.anim.main_enter, R.anim.main_exit);
			}
		};
		return clickListener;
	}
	
	/**
	 * 更多按钮绑定事件，点击进入更多页面
	 * @param activity 需要跳转到的页面
	 * @return 绑定事件
	 */
	protected OnClickListener goMore(final Activity activity) {
		OnClickListener clickListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(activity, MoreActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				startActivity(intent);
				overridePendingTransition(R.anim.main_enter, R.anim.main_exit);
			}
		};
		return clickListener;
	}

	// TODO 点击search的时候不要进入新的页面，需在当前页面添加一个下拉页面
	protected OnClickListener goMerchantsSearch(final Activity activity) {
		OnClickListener clickListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(activity,
						MerchantsSearchActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.main_enter, R.anim.main_exit);
			}
		};
		return clickListener;
	}

	private boolean autoLogin() {
		if (QHClientApplication.getInstance().isLogined) {
			return true;
		}

		progressDialogUtil = new ProgressDialogUtil(this, R.string.empty,
				R.string.logining, false);

		if (null == QHClientApplication.getInstance().accessInfo) {
			return true;
		} else {
			QHClientApplication.getInstance().isAuto = true;
			progressDialogUtil.showProgress();
		}
		return false;
	}

//	private boolean checkDevice() {
//		if (!InfoHelper.checkNetwork(this)) {
//			Toast.makeText(this, R.string.network_error_info, Toast.LENGTH_LONG)
//					.show();
//			return false;
//		}
//		return true;
//	}
}
