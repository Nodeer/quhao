package com.withiter.quhao.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
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
import org.apache.http.util.EntityUtils;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.ScrollView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;

import com.withiter.quhao.R;
import com.withiter.quhao.adapter.CategoryAdapter;
import com.withiter.quhao.util.tool.ParseJson;
import com.withiter.quhao.util.tool.ProgressDialogUtil;
import com.withiter.quhao.util.tool.QuhaoConstant;
import com.withiter.quhao.view.InnerListView;
import com.withiter.quhao.vo.Category;

public class MainActivity extends AppStoreActivity
{

	private String LOGTAG = MainActivity.class.getName();
	
	protected ListView topMerchantListView;
	
	protected InnerListView categorysListView;
	
	protected ProgressDialogUtil progressDialogUtil;
	
	private final int UNLOCK_CLICK = 1000;
	
	private boolean isClick = false;
	
	private List<Category> categorys = null;
	
	/**
	 * handler处理 解锁的时候可能会关闭其他的等待提示框
	 */
	private Handler unlockHandler = new Handler() 
	{
		public void handleMessage(Message msg) {
			if (msg.what == UNLOCK_CLICK) {
				// 解锁
				isClick = false;
			}
		}
	};
	
	private Handler categorysUpdateHandler = new Handler()
	{

		@Override
		public void handleMessage(Message msg)
		{
			if(msg.what == 200)
			super.handleMessage(msg);
			updateCategorys();
		}

	};
	
	private void updateCategorys()
	{
		LinearLayout.LayoutParams categorysParams = (LayoutParams) categorysListView.getLayoutParams();
		
		//设置自定义的layout
		
		categorysListView.setLayoutParams(categorysParams);
		categorysListView.invalidate();
		categorysListView.setVisibility(View.VISIBLE);
		
		CategoryAdapter adapter = new CategoryAdapter(MainActivity.this, categorysListView, categorys);
		categorysListView.setAdapter(adapter);
		adapter.notifyDataSetChanged();
		
		
		
		/*
		LinearLayout.LayoutParams topMerchantListParams = (LayoutParams) topMerchantListView.getLayoutParams();
		topMerchantListView.setLayoutParams(topMerchantListParams);
		topMerchantListView.invalidate();
		topMerchantListView.setVisibility(View.VISIBLE);
		
		CategoryAdapter adapter1 = new CategoryAdapter(MainActivity.this, topMerchantListView, categorys);
		topMerchantListView.setAdapter(adapter1);
		adapter1.notifyDataSetChanged();
		
		*/
		unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		setContentView(R.layout.main_layout);
		super.onCreate(savedInstanceState);
		initView();
		
		btnPerson.setOnClickListener(goPersonCenterListener(this));
		btnMarchent.setOnClickListener(getMarchentListListener(this));
	}

	private void initView()
	{
		WindowManager windowManager = getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		int screenHeight = display.getHeight();
		
		Log.d(LOGTAG, "the height of screen is : " + screenHeight);
		LinearLayout topMerchantLayout = (LinearLayout) findViewById(R.id.topMerchantLayout); 
		LinearLayout.LayoutParams topMerchantLayoutParams = (LayoutParams) topMerchantLayout.getLayoutParams();
		Log.d(LOGTAG, "the height of topMerchantLayout is : " + topMerchantLayoutParams.height);
		topMerchantLayoutParams.height =  (int) Math.floor(screenHeight/3);
		topMerchantLayout.setLayoutParams(topMerchantLayoutParams);
		
		LinearLayout categorysLayout = (LinearLayout) findViewById(R.id.categorysLayout); 
		LinearLayout.LayoutParams categorysLayoutParams = (LayoutParams) categorysLayout.getLayoutParams();
		Log.d(LOGTAG, "the height of categorysLayout is : " + categorysLayoutParams.height);
		categorysLayoutParams.height =  (int) Math.floor(screenHeight/3);
		categorysLayout.setLayoutParams(topMerchantLayoutParams);
		
		
		categorysListView = (InnerListView) findViewById(R.id.categorysListView);
		ScrollView parentScroll = (ScrollView) findViewById(R.id.parentScroll);
		categorysListView.setParentScroll(parentScroll);
		categorysListView.setMaxHeight(200);
		categorysListView.setNextFocusDownId(R.id.categorysListView);
		categorysListView.setOnScrollListener(categorysListScrollListener);
		categorysListView.setVisibility(View.GONE);
		
		/*
		topMerchantListView = (ListView) findViewById(R.id.topMerchantListView);
		topMerchantListView.setNextFocusDownId(R.id.categorysListView);
		topMerchantListView.setOnScrollListener(categorysListScrollListener);
		topMerchantListView.setVisibility(View.GONE);
		
		*/
		//获取数据
		getCateGorys();
		
	}
	
	
	
	private void getCateGorys()
	{
		if(isClick)
		{
			return;
		}
		
		isClick = true;
		
		progressDialogUtil = new ProgressDialogUtil(this, R.string.empty, R.string.querying, false);
		progressDialogUtil.showProgress();
		Thread categoryThread = new Thread(categoryRunnable);
		categoryThread.start();
	}

	private Runnable categoryRunnable = new Runnable()
	{
		
		@Override
		public void run()
		{
			try
			{
				Log.v(LOGTAG,"get categorys data form server begin");
				/**
				 * 
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
				httpClient.setHttpRequestRetryHandler(new DefaultHttpRequestRetryHandler(3, false));
				HttpGet request = new HttpGet(QuhaoConstant.HTTP_URL + "MerchantController/allCategories");
				// request.setHeader("User-Agent", Constant.UserAgent);
				request.setHeader("Accept-Language", "zh-cn");
				request.setHeader("Accept", "");*/
				HttpGet request = new HttpGet(QuhaoConstant.HTTP_URL + "MerchantController/allCategories");
				HttpClient httpClient = new DefaultHttpClient();
				HttpResponse response = httpClient.execute(request);
				Log.v(LOGTAG, "get categorys data form server : " + response.getStatusLine().getStatusCode());
				if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
				{
					progressDialogUtil.closeProgress();
					String buf = EntityUtils.toString(response.getEntity());
					Log.v(LOGTAG, "get categorys data form server buf : " + buf);
					// 返回HTML页面
					if (buf.indexOf("<html>") != -1
							|| buf.indexOf("<HTML>") != -1) {
						//mGetHandler.sendMessage(mGetHandler
						//		.obtainMessage(-2));
						throw new Exception("session timeout!");
					}
					
					if(null == categorys)
					{
						categorys = new ArrayList<Category>();
					}
					
					categorys.addAll(ParseJson.getCategorys(buf));
					
					categorysUpdateHandler.obtainMessage(200,categorys).sendToTarget();
					
				}
				
				
			} catch (Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				progressDialogUtil.closeProgress();
			}
		}
	};
	@Override
	protected void sendRequest()
	{
		super.sendRequest();
	}



	private OnScrollListener categorysListScrollListener = new OnScrollListener()
	{
		
		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState)
		{
			
		}
		
		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount)
		{
			
			
		}
	};

	@Override
	public void HttpClientCallBack(String buf)
	{
		
	}

	@Override
	public void onClick(View v)
	{
		
	}

	@Override
	public boolean onTouch(View v, MotionEvent event)
	{
		return false;
	}

	@Override
	public void HttpCallBack(String buf)
	{
		
	}

}
