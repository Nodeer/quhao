package com.withiter.quhao.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.Toast;

import com.withiter.quhao.QHClientApplication;
import com.withiter.quhao.R;
import com.withiter.quhao.util.tool.InfoHelper;
import com.withiter.quhao.util.tool.ProgressDialogUtil;

public abstract class AppStoreActivity extends QuhaoActivity implements
		OnClickListener, OnTouchListener {

	private boolean isClick = false;
	protected String action = "";
	private final int UNLOCK_CLICK = 1000;
	protected ProgressDialogUtil progressDialogUtil;
	
	protected Button btnPerson;
	protected Button btnMarchent;
	protected Button btnNearby;

	private Handler unlockHandler = new Handler() {
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
		// if(checkDevice() && autoLogin())
		if (checkDevice()) {
			// sendRequest();
		}

		btnMarchent = (Button) findViewById(R.id.btnMerchantList);
		btnPerson = (Button) findViewById(R.id.btnPerson);
	}

	protected OnClickListener goPersonCenterListener(final Activity activity) {
		OnClickListener clickListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (QHClientApplication.getInstance().isLogined) {
					Intent intent = new Intent(activity,
							PersonCenterActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);

				} else {
					Intent intent = new Intent(activity, LoginActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
				}

				overridePendingTransition(R.anim.main_enter, R.anim.main_exit);
				// activity.finish();

			}
		};
		return clickListener;
	}

	protected OnClickListener goCategoryListener(final Activity activity) {
		OnClickListener clickListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(activity, MainActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				overridePendingTransition(R.anim.main_enter, R.anim.main_exit);
			}
		};
		return clickListener;
	}

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

	private boolean checkDevice() {
		if (!InfoHelper.checkNetwork(this)) {
			Toast.makeText(this, R.string.network_error_info, Toast.LENGTH_LONG)
					.show();
			return false;
		}
		return true;
	}

}
