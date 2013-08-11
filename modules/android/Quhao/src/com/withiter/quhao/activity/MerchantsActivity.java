package com.withiter.quhao.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

import com.withiter.quhao.R;
import com.withiter.quhao.adapter.MerchantAdapter;
import com.withiter.quhao.util.tool.ParseJson;
import com.withiter.quhao.util.tool.ProgressDialogUtil;
import com.withiter.quhao.util.tool.QuhaoConstant;
import com.withiter.quhao.vo.Merchant;

public class MerchantsActivity extends AppStoreActivity
{

	private String LOGTAG = MerchantsActivity.class.getName();
	
	protected ListView merchantsListView;
	
	private List<Merchant> merchants;
	
	private MerchantAdapter merchantAdapter;
	
	private final int UNLOCK_CLICK = 1000;
	
	private boolean isClick = false;
	
	private ProgressDialogUtil progressMerchants; 
	
	private int page;
	
	private String categoryType;
	
	private String categoryTypeStr;

	private String categoryCount;
	
	private TextView categoryTypeView;
	
	private TextView categoryCountView;
	
	private boolean isFirst = true;
	
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
	
	private Handler merchantsUpdateHandler = new Handler()
	{

		@Override
		public void handleMessage(Message msg)
		{
			if(msg.what == 200)
			{
				super.handleMessage(msg);
				
				LinearLayout.LayoutParams merchantsParams = (LayoutParams) merchantsListView.getLayoutParams();
				
				//设置自定义的layout
				
				merchantsListView.setLayoutParams(merchantsParams);
				merchantsListView.invalidate();
				merchantsListView.setVisibility(View.VISIBLE);
				//merchantsListView.addFocusables(merchants, merchants.size()-9);
				if(isFirst)
				{
					merchantAdapter = new MerchantAdapter(MerchantsActivity.this, merchantsListView, merchants);
					merchantsListView.setAdapter(merchantAdapter);
					isFirst = false;
				}
				else
				{
					merchantAdapter.merchants = merchants;
				}
				
				merchantAdapter.notifyDataSetChanged();
				merchantsListView.setOnItemClickListener(merchantItemClickListener);
				
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
			
		}

	};

	private AdapterView.OnItemClickListener merchantItemClickListener = new AdapterView.OnItemClickListener()
	{

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id)
		{
			Merchant merchant = merchants.get(position);
			Intent intent = new Intent();
			intent.putExtra("merchantId", merchant.id);
			intent.setClass(MerchantsActivity.this, MerchantDetailActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.main_enter, R.anim.main_exit);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.merchants);
		super.onCreate(savedInstanceState);
		
		this.page = getIntent().getIntExtra("page", 1);
		this.categoryType = getIntent().getStringExtra("categoryType");
		
		
		this.categoryTypeStr = getIntent().getStringExtra("categoryTypeStr");
		this.categoryTypeView = (TextView) findViewById(R.id.categoryType);
		this.categoryTypeView.setText(categoryTypeStr);
		
		this.categoryCount = getIntent().getStringExtra("categoryCount");
		this.categoryCountView = (TextView) findViewById(R.id.categoryCount);
		this.categoryCountView.setText("[共"+ categoryCount + "家]");
		btnPerson.setOnClickListener(goPersonCenterListener(this));
		btnMarchent.setOnClickListener(getMarchentListListener(this));
		initView();
	}

	private void initView()
	{
		merchantsListView = (ListView) findViewById(R.id.merchantsListView);
		
		merchantsListView.setNextFocusDownId(R.id.merchantsListView);
		merchantsListView.setOnScrollListener(merchantsListScrollListener);
		merchantsListView.setVisibility(View.GONE);
		
		getMerchants();
		
	}

	private void getMerchants()
	{
		if(isClick)
		{
			return;
		}
		isClick = true;
		
		progressMerchants = new ProgressDialogUtil(this, R.string.empty, R.string.querying, false);
		progressMerchants.showProgress();
		Thread merchantsThread = new Thread(merchantsRunnable);
		merchantsThread.start();
	}

	private Runnable merchantsRunnable = new Runnable()
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
				HttpGet request = new HttpGet(QuhaoConstant.HTTP_URL + "MerchantController/nextPage?page="+ page +"&cateType=" + categoryType);
				HttpClient httpClient = new DefaultHttpClient();
				HttpResponse response = httpClient.execute(request);
				Log.v(LOGTAG, "get top merchant data form server : " + response.getStatusLine().getStatusCode());
				if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
				{
					String buf = EntityUtils.toString(response.getEntity());
					Log.v(LOGTAG, "get top merchant data form server buf : " + buf);
					// 返回HTML页面
					if (buf.indexOf("<html>") != -1
							|| buf.indexOf("<HTML>") != -1) {
						//mGetHandler.sendMessage(mGetHandler
						//		.obtainMessage(-2));
						throw new Exception("session timeout!");
					}
					
					if(null == merchants)
					{
						merchants = new ArrayList<Merchant>();
					}
					
					merchants.addAll(ParseJson.getMerchants(buf));
					
					merchantsUpdateHandler.obtainMessage(200,merchants).sendToTarget();
					
				}
				
				
			} catch (Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				progressMerchants.closeProgress();
			}
		}
	};
	private OnScrollListener merchantsListScrollListener = new OnScrollListener()
	{
		
		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState)
		{
			
		}
		
		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount)
		{
			if(view.getLastVisiblePosition()==totalItemCount-1)
			{
				MerchantsActivity.this.page += 1;
				getMerchants();
			}
			
		}
	};

	@Override
	public void onClick(View v)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onTouch(View v, MotionEvent event)
	{
		// TODO Auto-generated method stub
		return false;
	}

}
