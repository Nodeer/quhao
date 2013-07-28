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
import com.withiter.quhao.util.http.MyHttpClient;
import com.withiter.quhao.util.http.MyHttpClientListener;
import com.withiter.quhao.util.tool.InfoHelper;
import com.withiter.quhao.util.tool.ProgressDialogUtil;
import com.withiter.quhao.util.tool.QuhaoConstant;

public abstract class AppStoreActivity extends QuhaoActivity implements MyHttpClientListener, OnClickListener, OnTouchListener
{
	
	protected MyHttpClient myHttp;
	
	private boolean isClick = false;
	
	protected String action = "";
	
	private final int UNLOCK_CLICK = 1000;
	
	protected ProgressDialogUtil progressDialogUtil;
	
	protected Button btnPerson;
	
	protected Button btnMarchent;
	
	private Handler unlockHandler = new Handler()
	{
		public void handleMessage(Message msg)
		{
			if(msg.what == UNLOCK_CLICK)
			{
				isClick = false;
			}
		}
	};
	
	public abstract void HttpCallBack(String buf);

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		//initData();
		
		//检查网络
		//if(checkDevice() && autoLogin())
		if(checkDevice())
		{
			//sendRequest();
		}
		
		btnMarchent = (Button) findViewById(R.id.btnMerchantList);
		btnPerson = (Button) findViewById(R.id.btnPerson);
		
	}

	protected OnClickListener goPersonCenterListener(final Activity activity)
	{
		OnClickListener clickListener = new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent(activity, PersonCenterActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				activity.finish();
			}
		};
		return clickListener;
	}
	
	protected OnClickListener getMarchentListListener(final Activity activity)
	{
		OnClickListener clickListener = new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent(activity, MainActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				activity.finish();
			}
		};
		return clickListener;
	}
	
	protected void sendRequest()
	{
		
		action = "";
		QuhaoConstant.NEXT_CMD = "";
		QuhaoConstant.TIME_STAMP = 0;
		QuhaoConstant.TIME_STAMP_OLD = 0;
		myHttp.getHttpBuf(QuhaoConstant.HTTP_URL + QuhaoConstant.NEW_BODY, true);
	}

	private boolean autoLogin()
	{
		if(QHClientApplication.getInstance().isLogined)
		{
			return true;
		}
		
		progressDialogUtil = new ProgressDialogUtil(this, R.string.empty, R.string.logining, false);
		
		if(null == QHClientApplication.getInstance().accessInfo)
		{
			return true;
		}
		else
		{
			QHClientApplication.getInstance().isAuto = true;
			progressDialogUtil.showProgress();
			myHttp.getHttpBuf(QuhaoConstant.HTTP_URL + "",false);
		}
		return false;
	}

	private boolean checkDevice()
	{
		
		if(!InfoHelper.checkNetwork(this))
		{
			Toast.makeText(this, R.string.network_error_info, Toast.LENGTH_LONG).show();
			return false;
		}
		
		return true;
	}

	private void initData()
	{
		if(null == myHttp)
		{
			myHttp = new MyHttpClient(this,this);
		}
		
	}
	
	
	
	
}
