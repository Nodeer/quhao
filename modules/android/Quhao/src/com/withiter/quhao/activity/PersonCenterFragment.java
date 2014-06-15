package com.withiter.quhao.activity;

import java.io.File;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.withiter.quhao.QHClientApplication;
import com.withiter.quhao.R;
import com.withiter.quhao.domain.AccountInfo;
import com.withiter.quhao.util.ActivityUtil;
import com.withiter.quhao.util.QuhaoLog;
import com.withiter.quhao.util.StringUtils;
import com.withiter.quhao.util.http.CommonHTTPRequest;
import com.withiter.quhao.util.tool.AsynImageLoader;
import com.withiter.quhao.util.tool.FileUtil;
import com.withiter.quhao.util.tool.ImageUtil;
import com.withiter.quhao.util.tool.ParseJson;
import com.withiter.quhao.util.tool.QuhaoConstant;
import com.withiter.quhao.util.tool.SharedprefUtil;
import com.withiter.quhao.vo.LoginInfo;

public class PersonCenterFragment extends Fragment implements OnClickListener{

	private final static String TAG = PersonCenterFragment.class.getName();

	private TextView nickName;
	private TextView jifen;
	private ImageView label_qiandao;
	private TextView value_qiandao;
	private TextView value_dianpin;
	private TextView myAttention;

	private LoginInfo loginInfo;

	private LinearLayout signInLayout;
	private LinearLayout dianpingLayout;
	private LinearLayout currentPaiduiLayout;
	private LinearLayout historyPaiduiLayout;
	private LinearLayout creditCostLayout;
	private RelativeLayout personInfoLayout;
	private LinearLayout myAttentionLayout;
	
	private RelativeLayout personInfoLogoutLayout;
	
	private ImageView avatar;

	private ImageView loginBtn;
	private TextView regBtn;

	private final int UNLOCK_CLICK = 1000;
	
	private View contentView;
	
	private boolean isClick;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		
		if(contentView != null)
		{
			ViewGroup vg = (ViewGroup) contentView.getParent();
			vg.removeView(contentView);
			return contentView;
		}
		
		contentView = inflater.inflate(R.layout.person_center_fragment_layout, null);
		
		QuhaoLog.d(TAG, "PersonCenterFragment onCreateView");
		isClick = false;
		
		nickName = (TextView) contentView.findViewById(R.id.nickName);
		jifen = (TextView) contentView.findViewById(R.id.jifen);
		
		label_qiandao = (ImageView) contentView.findViewById(R.id.qiandao_label);
		value_qiandao = (TextView) contentView.findViewById(R.id.value_qiandao);
		value_dianpin = (TextView) contentView.findViewById(R.id.value_dianpin);
		myAttention = (TextView) contentView.findViewById(R.id.my_attention);
		avatar = (ImageView) contentView.findViewById(R.id.avatar);
		
		signInLayout = (LinearLayout) contentView.findViewById(R.id.signInLayout);
		dianpingLayout = (LinearLayout) contentView.findViewById(R.id.dianpingLayout);

		currentPaiduiLayout = (LinearLayout) contentView.findViewById(R.id.current_paidui_layout);
		historyPaiduiLayout = (LinearLayout) contentView.findViewById(R.id.history_paidui_layout);
		creditCostLayout = (LinearLayout) contentView.findViewById(R.id.credit_cost_layout);
		personInfoLayout = (RelativeLayout) contentView.findViewById(R.id.person_info);
		myAttentionLayout = (LinearLayout) contentView.findViewById(R.id.my_attention_layout);
		signInLayout.setOnClickListener(this);
		dianpingLayout.setOnClickListener(this);
		currentPaiduiLayout.setOnClickListener(this);
		personInfoLayout.setOnClickListener(this);
		myAttentionLayout.setOnClickListener(this);
		historyPaiduiLayout.setOnClickListener(this);
		creditCostLayout.setOnClickListener(this);
		
		personInfoLogoutLayout = (RelativeLayout) contentView.findViewById(R.id.person_info_logout);
		loginBtn = (ImageView) contentView.findViewById(R.id.login);
		regBtn = (TextView) contentView.findViewById(R.id.register);

		loginBtn.setOnClickListener(this);
		regBtn.setOnClickListener(this);

		// other activity will invoke this method
		refreshUI();
		
		return contentView;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	public void refreshUI() {
		// if haven't login, prompt the login dialog
		// no need to check auto login from SharedPreference
		// because when APP start up, the action had been performed
		QuhaoLog.d(TAG, "QHClientApplication.getInstance().isLogined : " + QHClientApplication.getInstance().isLogined);
		if (QHClientApplication.getInstance().isLogined) {
			AccountInfo account = QHClientApplication.getInstance().accountInfo;
			if (account != null) {
				personInfoLogoutLayout.setVisibility(View.GONE);
				personInfoLayout.setVisibility(View.VISIBLE);
				updateUIData(account);
			} else {

				nickName.setText(R.string.noname);

				jifen.setText("0");

				avatar.setImageResource(R.drawable.person_avatar);
				value_qiandao.setText("签到(0)");
				value_dianpin.setText("点评(0)");
				myAttention.setText("关注(0)");
				personInfoLogoutLayout.setVisibility(View.VISIBLE);
				personInfoLayout.setVisibility(View.GONE);
			}
		}
		else
		{
			nickName.setText(R.string.noname);

			jifen.setText("0");

			avatar.setImageResource(R.drawable.person_avatar);
			value_qiandao.setText("签到(0)");
			value_dianpin.setText("点评(0)");
			myAttention.setText("关注(0)");
			
			personInfoLogoutLayout.setVisibility(View.VISIBLE);
			personInfoLayout.setVisibility(View.GONE);
		}

	}

	// update UI according to the account object
	private void updateUIData(AccountInfo account) {
		nickName.setText(account.nickName);
		if(StringUtils.isNull(account.nickName))
		{
			nickName.setText(R.string.noname);
		}

		QuhaoLog.d(TAG, "account.jifen : " + account.jifen);
		jifen.setText(account.jifen);

		Bitmap bitmap = null;
		String fileName = "";
		if(StringUtils.isNotNull(account.userImage))
		{
			if (FileUtil.hasSdcard()) {
				
				String[] strs = account.userImage.split("fileName=");
				if(strs != null && strs.length>1)
				{
					fileName = account.userImage.split("fileName=")[1];
					String localFileName = SharedprefUtil.get(getActivity(), QuhaoConstant.USER_IMAGE, "");
					if(localFileName.equals(fileName))
					{
						File f = new File(Environment.getExternalStorageDirectory() + "/" + 
								QuhaoConstant.IMAGES_SD_URL + "/" + fileName);
						QuhaoLog.d(TAG, "f.exists():" + f.exists());
						File folder = f.getParentFile();
						if (!folder.exists()) {
							folder.mkdirs();
						}
						
						if(f.exists()){
							bitmap = ImageUtil.decodeFile(f.getPath(),-1,128*128);
							if (null != bitmap) {
								avatar.setImageBitmap(bitmap);
							}
						}
					}
					else
					{
						File f = new File(Environment.getExternalStorageDirectory() + "/" + 
								QuhaoConstant.IMAGES_SD_URL + "/" + localFileName);
						QuhaoLog.d(TAG, "f.exists():" + f.exists());
						File folder = f.getParentFile();
						if (!folder.exists()) {
							folder.mkdirs();
						}
						
						if(f.exists()){
							f.delete();
						}
					}
				}
				
			}
		}
		
		if(null == bitmap)
		{
			if(StringUtils.isNotNull(fileName))
			{
				SharedprefUtil.put(getActivity(), QuhaoConstant.USER_IMAGE, fileName);
				AsynImageLoader.getInstance().showImageAsyn(avatar, 0, account.userImage, R.drawable.person_avatar);
			}
			
		}
		
		
		value_qiandao.setText("签到(" + account.signIn + ")");
		if ("true".equals(account.isSignIn)) {
			label_qiandao.setImageResource(R.drawable.ic_sign_up_gray);
//			label_qiandao.setTextColor(this.getResources().getColor(R.color.black));
			value_qiandao.setTextColor(this.getResources().getColor(R.color.black));
		}
		else
		{
			label_qiandao.setImageResource(R.drawable.ic_sign_up_red);
//			label_qiandao.setTextColor(this.getResources().getColor(R.color.red));
			value_qiandao.setTextColor(this.getResources().getColor(R.color.red));
		}
		value_dianpin.setText("点评( "+ account.dianping + ")");
		myAttention.setText("关注(" + account.guanzhu + ")");
	}
	
	private void signIn() {
		String accountId = SharedprefUtil.get(getActivity(), QuhaoConstant.ACCOUNT_ID, "");
		try {
			
			String result = CommonHTTPRequest.get("signIn?accountId=" + accountId);
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
					Toast.makeText(getActivity(),"签到失败", Toast.LENGTH_SHORT).show();
					return;
				}
				if (loginInfo.msg.equals("success")) {
					
					QHClientApplication.getInstance().isLogined = true;
					Toast.makeText(getActivity(), R.string.sign_in_success, Toast.LENGTH_SHORT).show();
					accountUpdateHandler.obtainMessage(200, account).sendToTarget();
				}
			}
		} catch (Exception e) {
			accountUpdateHandler.obtainMessage(200, null).sendToTarget();
			Toast.makeText(getActivity(), "签到失败", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		} finally {
			
			unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
		}
	}
	
	private void queryAccountByAccountId() {
		
		if (!ActivityUtil.isNetWorkAvailable(getActivity())) {
			Toast.makeText(getActivity(), R.string.network_error_info, Toast.LENGTH_SHORT).show();
			return;
		}
		
		if(QHClientApplication.getInstance().isLogined)
		{
			String accountId = SharedprefUtil.get(getActivity(), QuhaoConstant.ACCOUNT_ID, "");
			if (StringUtils.isNull(accountId)) {
				QHClientApplication.getInstance().isLogined = false;
				Toast.makeText(getActivity(), "帐号超时，请重新登录", Toast.LENGTH_SHORT).show();
			}
			else
			{
				String url = "queryByAccountId?accountId=" + accountId;
				try {
					String result = CommonHTTPRequest.get(url);
					if(StringUtils.isNull(result)){
						QHClientApplication.getInstance().isLogined = false;
						Toast.makeText(getActivity(), "帐号超时，请重新登录", Toast.LENGTH_SHORT).show();
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
							Toast.makeText(getActivity(), "帐号超时，请重新登录", Toast.LENGTH_SHORT).show();
						}
						else if (account.msg.equals("success")) 
						{
							SharedprefUtil.put(getActivity(), QuhaoConstant.ACCOUNT_ID, loginInfo.accountId);
							SharedprefUtil.put(getActivity(), QuhaoConstant.PHONE, loginInfo.phone);
//							String encryptPassword = new DesUtils().decrypt(loginInfo.password);
//							SharedprefUtil.put(getActivity(), QuhaoConstant.PASSWORD, loginInfo.password);
							String isAutoLogin = SharedprefUtil.get(getActivity(), QuhaoConstant.IS_AUTO_LOGIN, "false");
							SharedprefUtil.put(getActivity(), QuhaoConstant.IS_AUTO_LOGIN, isAutoLogin);
							QHClientApplication.getInstance().accountInfo = account;
							QHClientApplication.getInstance().phone = loginInfo.phone;
							QHClientApplication.getInstance().isLogined = true;
							refreshUIHandler.sendEmptyMessage(UNLOCK_CLICK);
						}
						else
						{
							QHClientApplication.getInstance().isLogined = false;
							Toast.makeText(getActivity(), "帐号超时，请重新登录", Toast.LENGTH_SHORT).show();
						}
					}
					
				} catch (Exception e) {
					e.printStackTrace();
					QuhaoLog.e(TAG, e);
					QHClientApplication.getInstance().isLogined = false;
					Toast.makeText(getActivity(), "帐号超时，请重新登录", Toast.LENGTH_SHORT).show();
					
				}
			}
			
		}
		
		refreshUIHandler.sendEmptyMessage(UNLOCK_CLICK);
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
					Toast.makeText(getActivity(), "登陆失败", Toast.LENGTH_SHORT).show();
					return;
				}
				if (loginInfo.msg.equals("success")) {
					nickName.setText(loginInfo.nickName);

					jifen.setText(loginInfo.jifen);

					if ("true".equals(loginInfo.isSignIn)) {
						label_qiandao.setImageResource(R.drawable.ic_sign_up_gray);
//						label_qiandao.setTextColor(getActivity().getResources().getColor(R.color.black));
						value_qiandao.setTextColor(getActivity().getResources().getColor(R.color.black));
						
					}
					else
					{
						label_qiandao.setImageResource(R.drawable.ic_sign_up_red);
//						label_qiandao.setTextColor(getActivity().getResources().getColor(R.color.red));
						value_qiandao.setTextColor(getActivity().getResources().getColor(R.color.red));
					}
					
					value_qiandao.setText("签到(" + loginInfo.signIn + ")");
					value_dianpin.setText("点评( "+ loginInfo.dianping + ")");
					myAttention.setText("关注(" + loginInfo.guanzhu + ")");
					
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
			if (QHClientApplication.getInstance().isLogined) {
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				Intent intent1 = new Intent(getActivity(), PersonDetailActivity.class);
				intent1.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				startActivity(intent1);
				
			} else {
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				Intent login1 = new Intent(getActivity(), LoginActivity.class);
				login1.putExtra("activityName", this.getClass().getName());
				login1.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				startActivity(login1);
			}
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
				AccountInfo account = QHClientApplication.getInstance().accountInfo;
				if(account!=null)
				{
					if("true".equals(account.isSignIn))
					{
						unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
						Builder dialog = new AlertDialog.Builder(getActivity());
						dialog.setTitle("温馨提示").setMessage("亲，今天已经签过了哦！").setPositiveButton("确定", null);
						dialog.show();
					}
					else
					{
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
					}
				}
				
			} else {
				
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				Intent login2 = new Intent(getActivity(), LoginActivity.class);
				login2.putExtra("activityName", this.getClass().getName());
				login2.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				startActivity(login2);
			}
			break;
		case R.id.dianpingLayout:
			if (QHClientApplication.getInstance().isLogined) {
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				
				if ("0".equals(QHClientApplication.getInstance().accountInfo.dianping)) {
					return;
				}
				
				Intent intentComment = new Intent();
				intentComment.putExtra("accountId", QHClientApplication.getInstance().accountInfo.accountId);
				intentComment.setClass(getActivity(), CommentsAccountActivity.class);
				startActivity(intentComment);
				
			} else {
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				Intent login3 = new Intent(getActivity(), LoginActivity.class);
				login3.putExtra("activityName", this.getClass().getName());
				login3.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				startActivity(login3);
			}
			break;
		case R.id.my_attention_layout:
			if (QHClientApplication.getInstance().isLogined) {
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				
				Intent intentAttention = new Intent();
				intentAttention.putExtra("accountId", QHClientApplication.getInstance().accountInfo.accountId);
				intentAttention.setClass(getActivity(), MyAttentionListActivity.class);
				startActivity(intentAttention);
				
			} else {
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				Intent login3 = new Intent(getActivity(), LoginActivity.class);
				login3.putExtra("activityName", this.getClass().getName());
				login3.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				startActivity(login3);
			}
			break;
		case R.id.current_paidui_layout:
			if (QHClientApplication.getInstance().isLogined) {
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);

				Intent intentCurrent = new Intent();
				intentCurrent.putExtra("queryCondition", "current");
				intentCurrent.setClass(getActivity(), QuhaoCurrentStatesActivity.class);
				startActivity(intentCurrent);
			} else {
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				Intent login4 = new Intent(getActivity(), LoginActivity.class);
				login4.putExtra("activityName", this.getClass().getName());
				login4.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				startActivity(login4);
			}
			break;
		case R.id.history_paidui_layout:
			if (QHClientApplication.getInstance().isLogined) {
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				Intent intentHistory = new Intent();
				intentHistory.putExtra("queryCondition", "history");
				intentHistory.setClass(getActivity(), QuhaoHistoryStatesActivity.class);
				startActivity(intentHistory);
			} else {
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				Intent login4 = new Intent(getActivity(), LoginActivity.class);
				login4.putExtra("activityName", this.getClass().getName());
				login4.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				startActivity(login4);
			}
			break;
		case R.id.credit_cost_layout:
			if (QHClientApplication.getInstance().isLogined) {
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				Intent intentCredit = new Intent();
				intentCredit.setClass(getActivity(), CreditCostListActivity.class);
				startActivity(intentCredit);
			} else {
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				Intent login5 = new Intent(getActivity(), LoginActivity.class);
				login5.putExtra("activityName", this.getClass().getName());
				login5.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				startActivity(login5);
				
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