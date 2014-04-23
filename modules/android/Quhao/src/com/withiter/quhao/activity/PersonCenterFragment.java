package com.withiter.quhao.activity;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.withiter.quhao.QHClientApplication;
import com.withiter.quhao.R;
import com.withiter.quhao.domain.AccountInfo;
import com.withiter.quhao.util.ActivityUtil;
import com.withiter.quhao.util.QuhaoLog;
import com.withiter.quhao.util.StringUtils;
import com.withiter.quhao.util.http.CommonHTTPRequest;
import com.withiter.quhao.util.tool.ParseJson;
import com.withiter.quhao.util.tool.QuhaoConstant;
import com.withiter.quhao.util.tool.SharedprefUtil;
import com.withiter.quhao.vo.LoginInfo;

public class PersonCenterFragment extends Fragment implements OnClickListener{

	private final static String TAG = PersonCenterFragment.class.getName();

	private TextView nickName;
	private TextView mobile;
	private TextView jifen;
	private TextView value_qiandao;
	private TextView value_dianpin;

	private LoginInfo loginInfo;

	private LinearLayout signInLayout;
	private LinearLayout dianpingLayout;
	private LinearLayout currentPaiduiLayout;
	private LinearLayout historyPaiduiLayout;
	private LinearLayout creditCostLayout;
	private LinearLayout personInfoLayout;

	private Button loginBtn;
	private Button regBtn;

	private final int UNLOCK_CLICK = 1000;
	
	private View contentView;
	
	private boolean isClick;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {	
		contentView = inflater.inflate(R.layout.person_center_fragment_layout, null);
		
		QuhaoLog.d(TAG, "PersonCenterFragment onCreateView");
		isClick = false;
		
		nickName = (TextView) contentView.findViewById(R.id.nickName);
		mobile = (TextView) contentView.findViewById(R.id.mobile);
		jifen = (TextView) contentView.findViewById(R.id.jifen);
		
		value_qiandao = (TextView) contentView.findViewById(R.id.value_qiandao);
		value_dianpin = (TextView) contentView.findViewById(R.id.value_dianpin);

		signInLayout = (LinearLayout) contentView.findViewById(R.id.signInLayout);
		dianpingLayout = (LinearLayout) contentView.findViewById(R.id.dianpingLayout);

		currentPaiduiLayout = (LinearLayout) contentView.findViewById(R.id.current_paidui_layout);
		historyPaiduiLayout = (LinearLayout) contentView.findViewById(R.id.history_paidui_layout);
		creditCostLayout = (LinearLayout) contentView.findViewById(R.id.credit_cost_layout);
		personInfoLayout = (LinearLayout) contentView.findViewById(R.id.person_info);
		signInLayout.setOnClickListener(this);
		dianpingLayout.setOnClickListener(this);
		personInfoLayout.setOnClickListener(this);
		currentPaiduiLayout.setOnClickListener(this);
		historyPaiduiLayout.setOnClickListener(this);
		creditCostLayout.setOnClickListener(this);

		loginBtn = (Button) contentView.findViewById(R.id.login);
		regBtn = (Button) contentView.findViewById(R.id.register);

		loginBtn.setOnClickListener(this);
		regBtn.setOnClickListener(this);

		// other activity will invoke this method
		refreshUI();
		
		return contentView;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// TODO add default view here
		if (!ActivityUtil.isNetWorkAvailable(getActivity())) {
			Builder dialog = new AlertDialog.Builder(getActivity());
			dialog.setTitle("温馨提示").setMessage("Wifi/蜂窝网络未打开，或者网络情况不是很好哟").setPositiveButton("确定", null);
			dialog.show();
			
		}
	}
	
	public void refreshUI() {
		// if haven't login, prompt the login dialog
		// no need to check auto login from SharedPreference
		// because when APP start up, the action had been performed
		QuhaoLog.d(TAG, "QHClientApplication.getInstance().isLogined : " + QHClientApplication.getInstance().isLogined);
		if (QHClientApplication.getInstance().isLogined) {
			AccountInfo account = QHClientApplication.getInstance().accountInfo;
			if (account != null) {
				loginBtn.setVisibility(View.GONE);
				regBtn.setVisibility(View.GONE);
				updateUIData(account);
			} else {
				loginBtn.setVisibility(View.VISIBLE);
				regBtn.setVisibility(View.VISIBLE);
			}
		}

	}

	// update UI according to the account object
	private void updateUIData(AccountInfo account) {
		mobile.setText(account.phone);
		
		nickName.setText(account.nickName);
		if(StringUtils.isNull(account.nickName))
		{
			nickName.setText(R.string.noname);
		}

		QuhaoLog.d(TAG, "account.jifen : " + account.jifen);
		mobile.setText(account.phone);
		jifen.setText(account.jifen);

		value_qiandao.setText(account.signIn);
		value_dianpin.setText(account.dianping);
		if ("true".equals(account.isSignIn)) {
			signInLayout.setEnabled(false);
		}
		else
		{
			signInLayout.setEnabled(true);
		}
	}
	
	private void signIn() {
		String accountId = SharedprefUtil.get(getActivity(), QuhaoConstant.ACCOUNT_ID, "");
		try {
			String result = CommonHTTPRequest.get("AccountController/signIn?accountId=" + accountId);
			QuhaoLog.i(TAG, result);
			if (StringUtils.isNull(result)) {
			} else {
				loginInfo = ParseJson.getLoginInfo(result);
				AccountInfo account = new AccountInfo();
				account.build(loginInfo);
//						SharedprefUtil.put(PersonCenterActivity.this, QuhaoConstant.IS_LOGIN, "true");
				QHClientApplication.getInstance().accountInfo = account;
				QHClientApplication.getInstance().isLogined = true;

				QuhaoLog.i(TAG, loginInfo.msg);
				if (loginInfo.msg.equals("fail")) {
//							SharedprefUtil.put(PersonCenterActivity.this, QuhaoConstant.IS_LOGIN, "false");
					QHClientApplication.getInstance().isLogined = false;
					Toast.makeText(getActivity(),"签到失败", Toast.LENGTH_LONG).show();
					return;
				}
				if (loginInfo.msg.equals("success")) {
					
					QHClientApplication.getInstance().isLogined = true;
					Toast.makeText(getActivity(), R.string.sign_in_success, Toast.LENGTH_LONG).show();
					accountUpdateHandler.obtainMessage(200, account).sendToTarget();
				}
			}
		} catch (Exception e) {
			accountUpdateHandler.obtainMessage(200, null).sendToTarget();
			Toast.makeText(getActivity(), "签到失败", Toast.LENGTH_LONG).show();
			e.printStackTrace();
		} finally {
			
			unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
		}
	}
	
	private void queryAccountByAccountId() {
		if(QHClientApplication.getInstance().isLogined)
		{
			String accountId = SharedprefUtil.get(getActivity(), QuhaoConstant.ACCOUNT_ID, "");
			if (StringUtils.isNull(accountId)) {
				QHClientApplication.getInstance().isLogined = false;
				Toast.makeText(getActivity(), "帐号超时，请重新登录", Toast.LENGTH_LONG).show();
			}
			else
			{
				String url = "AccountController/queryByAccountId?accountId=" + accountId;
				try {
					String result = CommonHTTPRequest.post(url);
					if(StringUtils.isNull(result)){
						QHClientApplication.getInstance().isLogined = false;
						Toast.makeText(getActivity(), "帐号超时，请重新登录", Toast.LENGTH_LONG).show();
					}
					else
					{
						LoginInfo loginInfo = ParseJson.getLoginInfo(result);
						AccountInfo account = new AccountInfo();
						account.build(loginInfo);
						QuhaoLog.d(TAG, account.msg);

						if (account.msg.equals("fail")) {
//							SharedprefUtil.put(this, QuhaoConstant.IS_LOGIN, "false");
							QHClientApplication.getInstance().isLogined = false;
							Toast.makeText(getActivity(), "帐号超时，请重新登录", Toast.LENGTH_LONG).show();
						}
						else if (account.msg.equals("success")) 
						{
							SharedprefUtil.put(getActivity(), QuhaoConstant.ACCOUNT_ID, loginInfo.accountId);
							SharedprefUtil.put(getActivity(), QuhaoConstant.PHONE, loginInfo.phone);
//							String encryptPassword = new DesUtils().decrypt(loginInfo.password);
							SharedprefUtil.put(getActivity(), QuhaoConstant.PASSWORD, loginInfo.password);
							String isAutoLogin = SharedprefUtil.get(getActivity(), QuhaoConstant.IS_AUTO_LOGIN, "false");
							SharedprefUtil.put(getActivity(), QuhaoConstant.IS_AUTO_LOGIN, isAutoLogin);
							QHClientApplication.getInstance().accountInfo = account;
							QHClientApplication.getInstance().phone = loginInfo.phone;
							QHClientApplication.getInstance().isLogined = true;
						}
						else
						{
							QHClientApplication.getInstance().isLogined = false;
							Toast.makeText(getActivity(), "帐号超时，请重新登录", Toast.LENGTH_LONG).show();
						}
					}
					
				} catch (Exception e) {
					e.printStackTrace();
					QuhaoLog.e(TAG, e);
					QHClientApplication.getInstance().isLogined = false;
					Toast.makeText(getActivity(), "帐号超时，请重新登录", Toast.LENGTH_LONG).show();
					
				}
			}
			
		}
	}
	
	@Override
	public void onResume() {
		Thread thread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				try
				{
					Looper.prepare();
					queryAccountByAccountId();
					refreshUIHandler.sendEmptyMessage(UNLOCK_CLICK);
					
				}catch(Exception e)
				{
					QuhaoLog.e(TAG, e.getMessage());
				}finally
				{
					Looper.loop();
					unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				}
				
				
			}
		});
		thread.start();
		super.onResume();
	}

	private Handler accountUpdateHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 200) {
				super.handleMessage(msg);
				if (loginInfo.msg.equals("fail")) {
					QHClientApplication.getInstance().isLogined = false;
//					SharedprefUtil.put(PersonCenterActivity.this, QuhaoConstant.IS_LOGIN, "false");
					Toast.makeText(getActivity(), "登陆失败", Toast.LENGTH_LONG).show();
					return;
				}
				if (loginInfo.msg.equals("success")) {
					nickName.setText(loginInfo.nickName);
					mobile.setText(loginInfo.phone);

					// TODO add jifen from backend
					jifen.setText(loginInfo.jifen);

					value_qiandao.setText(loginInfo.signIn);
					value_dianpin.setText(loginInfo.dianping);
					if ("true".equals(loginInfo.isSignIn)) {
						signInLayout.setEnabled(false);
					}
					
				}
			}
		}
	};
	
	protected Handler refreshUIHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == UNLOCK_CLICK) {
				refreshUI();
			}
		}
	};
	
	protected Handler unlockHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == UNLOCK_CLICK) {
				
				isClick = false;
			}
		}
	};
	
	@Override
	public void onClick(View v) {
		// 已经点过，直接返回
		if (isClick) {
			return;
		}

		// 设置已点击标志，避免快速重复点击
		isClick = true;

		switch (v.getId()) {
		case R.id.login:
			
			unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			Intent intent = new Intent(getActivity(), LoginActivity.class);
			intent.putExtra("activityName", this.getClass().getName());
			intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivity(intent);
//			this.finish();
			break;
		case R.id.person_info:
			
			unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
//			Intent intent1 = new Intent(getActivity(), PersonDetailActivity.class);
//			intent1.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//			startActivity(intent1);
//			this.finish();
			break;
		case R.id.register:
			
			unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			Intent intentReg = new Intent(getActivity(), RegisterActivity.class);
			intentReg.putExtra("activityName", this.getClass().getName());
			intentReg.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivity(intentReg);
			break;
		case R.id.signInLayout:
			if (QHClientApplication.getInstance().isLogined) {
				
				Thread thread = new Thread(new Runnable() {
					
					@Override
					public void run() {
						try
						{
							Looper.prepare();
							signIn();
						}
						catch(Exception e)
						{
							QuhaoLog.e(TAG, e.getMessage());
						}
						finally
						{
							Looper.loop();
						}
						
						
					}
				});
				thread.start();
			} else {
				
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				AlertDialog.Builder builder = new Builder(getActivity());
				builder.setTitle("温馨提示");
				builder.setMessage("请先登录");
				builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				builder.create().show();
			}
			break;
		case R.id.dianpingLayout:
			if (QHClientApplication.getInstance().isLogined) {
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				
				Intent intentComment = new Intent();
				intentComment.putExtra("accountId", QHClientApplication.getInstance().accountInfo.accountId);
				intentComment.setClass(getActivity(), CommentsAccountActivity.class);
				startActivity(intentComment);
				getActivity().overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
				
			} else {
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				AlertDialog.Builder builder = new Builder(getActivity());
				builder.setTitle("温馨提示");
				builder.setMessage("请先登录");
				builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				builder.create().show();
			}
			break;
		case R.id.current_paidui_layout:
			if (QHClientApplication.getInstance().isLogined) {
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);

				Intent intentCurrent = new Intent();
				intentCurrent.putExtra("queryCondition", "current");
				intentCurrent.setClass(getActivity(), QuhaoCurrentStatesActivity.class);
				startActivity(intentCurrent);
				getActivity().overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
			} else {
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				AlertDialog.Builder builder = new Builder(getActivity());
				builder.setTitle("温馨提示");
				builder.setMessage("请先登录");
				builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				builder.create().show();
			}
			break;
		case R.id.history_paidui_layout:
			if (QHClientApplication.getInstance().isLogined) {
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				Intent intentHistory = new Intent();
				intentHistory.putExtra("queryCondition", "history");
				intentHistory.setClass(getActivity(), QuhaoHistoryStatesActivity.class);
				startActivity(intentHistory);
				getActivity().overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
			} else {
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				Builder dialog = new AlertDialog.Builder(getActivity());
				dialog.setTitle("温馨提示");
				dialog.setMessage("请先登录");
				dialog.setPositiveButton("确认", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				dialog.create().show();
			}
			break;
		case R.id.credit_cost_layout:
			if (QHClientApplication.getInstance().isLogined) {
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				Intent intentCredit = new Intent();
				intentCredit.setClass(getActivity(), CreditCostListActivity.class);
				startActivity(intentCredit);
				getActivity().overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
			} else {
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				Builder builder = new AlertDialog.Builder(getActivity());
				builder.setTitle("温馨提示");
				builder.setMessage("请先登录");
				builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				builder.create().show();
				
//				Builder dialog = new AlertDialog.Builder(this);
//				dialog.setTitle("温馨提示").setMessage("请先登录").setPositiveButton("确定", null);
//				dialog.show();
			}
			break;
		default:
			unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			break;
		}
	}	
}