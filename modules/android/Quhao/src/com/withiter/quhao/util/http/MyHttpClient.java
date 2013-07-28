package com.withiter.quhao.util.http;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.scheme.SocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.withiter.quhao.R;
import com.withiter.quhao.util.tool.CommonTool;
import com.withiter.quhao.util.tool.ProgressDialogUtil;

public class MyHttpClient
{

	private static final int RESPONCECODE = -1;
	private static final int SESSION_TIMEOUT = -2;
	private static final int POST_RESPONCECODE = -3;
	
	private Thread myThread;
	
	private ProgressDialogUtil progressDialogUtil;
	
	private Context context;
	
	private String url;
	
	private String body;
	
	private boolean isGetReConnect = false;
	
	private boolean isShow;
	
	private MyHttpClientListener listener;
	
	private Handler myGetHandler = new Handler()
	{

		@Override
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
			case RESPONCECODE:
				if(!isGetReConnect)
				{
					isGetReConnect = false;
					startGetThread(isShow);
				}
				break;

			case SESSION_TIMEOUT:
				// 跳转到登录界面，重新登录
				// LoginAction.getInstance().reLogin(cx);
				break;
				
			case -11:
				CommonTool.hintDialog(context, context.getString(R.string.net_error));
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
		
	};
	
	public MyHttpClient(Context context,
			MyHttpClientListener listener)
	{
		this.context = context;
		this.listener = listener;
		
		progressDialogUtil = new ProgressDialogUtil(this.context, R.string.empty, R.string.connecting, false);
	}

	public void getHttpBuf(String url, boolean isShow)
	{
		this.url = url;
		Log.d("url : ", url);
		isGetReConnect = false;
		this.isShow = isShow;
		
		startGetThread(isShow);
		
	}

	private void startGetThread(boolean isShow)
	{
		if(isShow)
		{
			progressDialogUtil.showProgress();
		}
		
		myThread = new Thread()
		{

			@Override
			public void run()
			{
				try
				{
					SchemeRegistry schemeRegistry = new SchemeRegistry();
					SocketFactory sf = PlainSocketFactory.getSocketFactory();
					schemeRegistry.register(new Scheme("http", sf, 80));
					HttpParams params = new BasicHttpParams();
					HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
					HttpProtocolParams.setContentCharset(params, "UTF-8");
					HttpProtocolParams.setHttpElementCharset(params, "UTF-8");
					HttpProtocolParams.setUseExpectContinue(params, false);
					HttpConnectionParams.setConnectionTimeout(params, 30000);
					
					ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, schemeRegistry);
					DefaultHttpClient httpClient = new DefaultHttpClient(ccm,params);
					httpClient.setHttpRequestRetryHandler(new DefaultHttpRequestRetryHandler(3,false));
					HttpGet request = new HttpGet(url);
					request.setHeader("Accept-Language", "zh-cn");
					request.setHeader("Accept", "*/*");
					HttpResponse response = httpClient.execute(request);
					if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
					{
						progressDialogUtil.closeProgress();
						String buf = EntityUtils.toString(response.getEntity());
						
						if(buf.indexOf("<html>")!=-1 || buf.indexOf("<HTML>")!=-1)
						{
							myGetHandler.sendMessage(myGetHandler.obtainMessage(SESSION_TIMEOUT));
							throw new Exception("session timeout!");
						}
						listener.HttpClientCallBack(buf);
						
					}
					else if(response.getStatusLine().getStatusCode() == RESPONCECODE)
					{
						Thread tempThread = myThread;
						tempThread.interrupt();
						myThread = null;
						myGetHandler.sendMessage(myGetHandler.obtainMessage(
								RESPONCECODE, new Object()));
						throw new Exception("response code = -1");
					}
				} catch (Exception e)
				{
					myGetHandler.sendEmptyMessage(-11);
					e.printStackTrace();
				}
				finally
				{
					progressDialogUtil.closeProgress();
				}
			}
			
		};
		myThread.start();
	}
}
