package com.withiter.quhao.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;

import com.withiter.quhao.QHClientApplication;
import com.withiter.quhao.R;
import com.withiter.quhao.util.QuhaoLog;
import com.withiter.quhao.util.tool.PhoneTool;
import com.withiter.quhao.util.tool.ProgressDialogUtil;

@SuppressLint("NewApi")
public abstract class AppStoreActivity extends QuhaoActivity implements
		OnClickListener, OnTouchListener {

	private final String TAG = AppStoreActivity.class.getName();

	protected boolean isClick = false;
	protected String action = "";
	protected final int UNLOCK_CLICK = 1000;
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
		networkOK = PhoneTool.isNetworkAvailable(this);
		// if(checkDevice() && autoLogin())
		btnCategory = (Button) findViewById(R.id.btnMerchantList);
		btnNearby = (Button) findViewById(R.id.btnNearby);
		btnPerson = (Button) findViewById(R.id.btnPerson);
		btnMore = (Button) findViewById(R.id.btnMore);
		btnBack = (Button) findViewById(R.id.back_btn);

	}

	/**
	 * 商家列表按钮绑定事件，点击进入商家列表页面
	 * 
	 * @param activity
	 *            需要跳转到的页面
	 * @return 绑定事件
	 */
	protected OnClickListener goBack(final Activity activity, final Object...params) {
		OnClickListener clickListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(params != null && params.length > 0){
					Log.i(TAG, "params[0] is " + params[0]);
					if(params[0].equals(MerchantsSearchActivity.class.getName())){
						Log.i(TAG, "backClicked: " + MerchantsSearchActivity.backClicked);
						MerchantsSearchActivity.backClicked = true;
					}
					if(params[0].equals(MerchantDetailActivity.class.getName())){
						Log.i(TAG, "backClicked: " + MerchantDetailActivity.backClicked);
						MerchantDetailActivity.backClicked = true;
					}
					if(params[0].equals(MerchantListActivity.class.getName())){
						Log.i(TAG, "backClicked: " + MerchantListActivity.backClicked);
						MerchantListActivity.backClicked = true;
					}
					if(params[0].equals(GetNumberActivity.class.getName())){
						Log.i(TAG, "backClicked: " + GetNumberActivity.backClicked);
						GetNumberActivity.backClicked = true;
					}
				}
				onBackPressed();
				activity.finish();
			}
		};
		return clickListener;
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}
	
	/**
	 * 商家列表按钮绑定事件，点击进入商家列表页面
	 * 
	 * @param activity
	 *            需要跳转到的页面
	 * @return 绑定事件
	 */
	protected OnClickListener goCategory(final Activity activity) {
		OnClickListener clickListener = new OnClickListener() {
			@Override
			public void onClick(View v) {

				// do not change the code below
				// 判断是否在当前页面, 需要刷新页面，重新加载数据，而不是调整activity的显示顺序。 Add by Cross
				if (activity instanceof MainActivity) {
					QuhaoLog.i(TAG, "refresh category page");
					((MainActivity) activity)
							.getTopMerchantsFromServerAndDisplay();
					((MainActivity) activity)
							.getCategoriesFromServerAndDisplay();
				} else {
					Intent intent = new Intent(activity, MainActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
					startActivity(intent);
					overridePendingTransition(R.anim.main_enter,
							R.anim.main_exit);
				}
			}
		};
		return clickListener;
	}

	/**
	 * 附近商家按钮绑定事件，点击进入附近页面
	 * 
	 * @param activity
	 *            需要跳转到的页面
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
	 * 
	 * @param activity
	 *            需要跳转到的页面
	 * @return 绑定事件
	 */
	protected OnClickListener goPersonCenter(final Activity activity) {
		OnClickListener clickListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (activity instanceof PersonCenterActivity) {
					QuhaoLog.i(TAG, "refresh personal center page");
					activity.recreate();
					// TODO add refresh personal page
				} else {
					Intent intent = new Intent(activity,
							PersonCenterActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
					startActivity(intent);
					overridePendingTransition(R.anim.main_enter,
							R.anim.main_exit);
				}

				// if (QHClientApplication.getInstance().isLogined) {
				// Intent intent = new Intent(activity,
				// PersonCenterActivity.class);
				// intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				// startActivity(intent);
				// } else {
				// Intent intent = new Intent(activity, LoginActivity.class);
				// intent.putExtra("activityName", activity.getClass()
				// .getName());
				// QuhaoLog.d(TAG, " activity.getClass().getName() : "
				// + activity.getClass().getName());
				// intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				// startActivity(intent);
				// }

			}
		};
		return clickListener;
	}

	/**
	 * 更多按钮绑定事件，点击进入更多页面
	 * 
	 * @param activity
	 *            需要跳转到的页面
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
				overridePendingTransition(R.anim.in_from_right,
						R.anim.out_to_left);
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

	// private boolean checkDevice() {
	// if (!InfoHelper.checkNetwork(this)) {
	// Toast.makeText(this, R.string.network_error_info, Toast.LENGTH_LONG)
	// .show();
	// return false;
	// }
	// return true;
	// }
}
