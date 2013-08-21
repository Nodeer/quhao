package com.withiter.quhao.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.withiter.quhao.R;
import com.withiter.quhao.adapter.CategoryGridAdapter;
import com.withiter.quhao.adapter.TopMerchantGridAdapter;
import com.withiter.quhao.util.http.CommonHTTPRequest;
import com.withiter.quhao.util.tool.ParseJson;
import com.withiter.quhao.util.tool.ProgressDialogUtil;
import com.withiter.quhao.util.tool.QuhaoConstant;
import com.withiter.quhao.vo.Category;
import com.withiter.quhao.vo.TopMerchant;

public class MainActivity extends AppStoreActivity {

	private String TAG = MainActivity.class.getName();
	protected ListView topMerchantListView;
	private GridView topMerchantsGird;
	private Button searchBtn;
	private List<TopMerchant> topMerchants;
	private GridView categorysGird;
	private DisplayMetrics localDisplayMetrics;
	protected ProgressDialogUtil progressCategory;
	protected ProgressDialogUtil progressTopMerchant;
	private final int UNLOCK_CLICK = 1000;
	private boolean isClick = false;
	private List<Category> categorys = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main_layout);
		super.onCreate(savedInstanceState);

		TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        StringBuilder sb = new StringBuilder();
        sb.append("\nDeviceId(IMEI) = " + tm.getDeviceId());
        sb.append("\nDeviceSoftwareVersion = " + tm.getDeviceSoftwareVersion());
        sb.append("\nLine1Number = " + tm.getLine1Number());
        sb.append("\nNetworkCountryIso = " + tm.getNetworkCountryIso());
        sb.append("\nNetworkOperator = " + tm.getNetworkOperator());
        sb.append("\nNetworkOperatorName = " + tm.getNetworkOperatorName());
        sb.append("\nNetworkType = " + tm.getNetworkType());
        sb.append("\nPhoneType = " + tm.getPhoneType());
        sb.append("\nSimCountryIso = " + tm.getSimCountryIso());
        sb.append("\nSimOperator = " + tm.getSimOperator());
        sb.append("\nSimOperatorName = " + tm.getSimOperatorName());
        sb.append("\nSimSerialNumber = " + tm.getSimSerialNumber());
        sb.append("\nSimState = " + tm.getSimState());
        sb.append("\nSubscriberId(IMSI) = " + tm.getSubscriberId());
        sb.append("\nVoiceMailNumber = " + tm.getVoiceMailNumber());
        Log.i(TAG, sb.toString()); 
        
		
		// initView();
		localDisplayMetrics = getResources().getDisplayMetrics();
		topMerchants = new ArrayList<TopMerchant>();
		getTopMerchants();
//		
//		searchBtn = (Button) findViewById(R.id.edit_search);
//		searchBtn.setOnClickListener(goMerchantsSearch(MainActivity.this));
//		
//		topMerchantsGird = (GridView) findViewById(R.id.topMerchants);
//
//		topMerchantsGird.setOnItemClickListener(topMerchantClickListener);
//		categorys = new ArrayList<Category>();
//		categorysGird = (GridView) findViewById(R.id.categorys);
//		getCateGorys();
//
//		categorysGird.setOnItemClickListener(categorysClickListener);
//		btnPerson.setOnClickListener(goPersonCenterListener(this));
//		btnMarchent.setOnClickListener(getMarchentListListener(this));
	}
	
	/**
	 * handler处理 解锁的时候可能会关闭其他的等待提示框
	 */
	private Handler unlockHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == UNLOCK_CLICK) {
				// 解锁
				isClick = false;
			}
		}
	};

	private Handler topMerchantsUpdateHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 200) {
				super.handleMessage(msg);
				ListAdapter adapter = new TopMerchantGridAdapter(topMerchants,
						topMerchantsGird, MainActivity.this);
				topMerchantsGird.setAdapter(adapter);
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			}
		}
	};

	private Handler categorysUpdateHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 200) {
				super.handleMessage(msg);

				ListAdapter adapter = new CategoryGridAdapter(categorys,
						categorysGird, MainActivity.this);
				categorysGird.setAdapter(adapter);

				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			}
		}
	};

	

	private OnItemClickListener topMerchantClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			String topMerchantId = topMerchants.get(position).id;
			Intent intent = new Intent();
			intent.putExtra("merchantId", topMerchantId);
			intent.setClass(MainActivity.this, MerchantDetailActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.main_enter, R.anim.main_exit);
		}
	};

	private OnItemClickListener categorysClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Category category = categorys.get(position);
			Log.d(TAG, "the category is : " + category.categoryType
					+ "the count is : " + category.count);
			Intent intent = new Intent();
			intent.putExtra("categoryType", category.categoryType);
			intent.putExtra("categoryTypeStr", category.categoryTypeStr);
			intent.putExtra("categoryCount", String.valueOf(category.count));
			intent.setClass(MainActivity.this, MerchantsActivity.class);
			
			startActivity(intent);
			overridePendingTransition(R.anim.main_enter, R.anim.main_exit);
		}
	};

	private void getTopMerchants() {
		progressTopMerchant = new ProgressDialogUtil(this, R.string.empty,
				R.string.querying, false);
		progressTopMerchant.showProgress();

		Thread topMerchantsThread = new Thread(topMerchantsRunnable);
		topMerchantsThread.start();
	}

	private void getCateGorys() {
		if (isClick) {
			return;
		}

		isClick = true;
		progressCategory = new ProgressDialogUtil(this, R.string.empty,
				R.string.querying, false);
		progressCategory.showProgress();

		Thread categoryThread = new Thread(categoryRunnable);
		categoryThread.start();
	}

	private Runnable topMerchantsRunnable = new Runnable() {
		@Override
		public void run() {
			try {
				Log.i(TAG, "Start to get categorys data form server.");
				
//				String result = CommonHTTPRequest.request(QuhaoConstant.HTTP_URL
//						+ "MerchantController/getTopMerchants?x=6");
				String result = CommonHTTPRequest.request(QuhaoConstant.HTTP_URL_TEST_CROSS
						+ "MerchantController/getTopMerchants?x=6");
				Log.d(TAG, result);
				if (null == topMerchants) {
					topMerchants = new ArrayList<TopMerchant>();
				}
				topMerchants.addAll(ParseJson.getTopMerchants(result));
//				topMerchantsUpdateHandler.obtainMessage(200, topMerchants)
//						.sendToTarget();
				
//				HttpGet request = new HttpGet(QuhaoConstant.HTTP_URL
//						+ "MerchantController/getTopMerchants?x=6");
//				HttpClient httpClient = new DefaultHttpClient();
//				HttpResponse response = httpClient.execute(request);
//				Log.i(TAG, "get top merchant data form server : "
//						+ response.getStatusLine().getStatusCode());
//				if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
//					String buf = EntityUtils.toString(response.getEntity());
//					Log.v(TAG, "get top merchant data form server buf : "
//							+ buf);
//					// 返回HTML页面
//					if (buf.indexOf("<html>") != -1
//							|| buf.indexOf("<HTML>") != -1) {
//						// mGetHandler.sendMessage(mGetHandler
//						// .obtainMessage(-2));
//						throw new Exception("session timeout!");
//					}
//
//					if (null == topMerchants) {
//						topMerchants = new ArrayList<TopMerchant>();
//					}
//
//					topMerchants.addAll(ParseJson.getTopMerchants(buf));
//
//					topMerchantsUpdateHandler.obtainMessage(200, topMerchants)
//							.sendToTarget();
//				}

			} catch (Exception e) {
				Log.e(TAG, e.getCause().toString(), e);
				e.printStackTrace();
			} finally {
				progressTopMerchant.closeProgress();
			}
		}
	};

	private Runnable categoryRunnable = new Runnable() {
		@Override
		public void run() {
			try {
				Log.v(TAG, "get categorys data form server begin");
				String userHome = System.getProperty("user.home");
				System.out.println(userHome);
				if (userHome.contains("eacfgjl")) {
					QuhaoConstant.HTTP_URL = "http://146.11.24.199:9081/";
				}
				HttpGet request = new HttpGet(QuhaoConstant.HTTP_URL
						+ "MerchantController/allCategories");
				HttpClient httpClient = new DefaultHttpClient();
				HttpResponse response = httpClient.execute(request);
				Log.v(TAG, "get categorys data form server : "
						+ response.getStatusLine().getStatusCode());
				if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					String buf = EntityUtils.toString(response.getEntity());
					Log.v(TAG, "get categorys data form server buf : " + buf);
					// 返回HTML页面
					if (buf.indexOf("<html>") != -1
							|| buf.indexOf("<HTML>") != -1) {
						// mGetHandler.sendMessage(mGetHandler
						// .obtainMessage(-2));
						throw new Exception("session timeout!");
					}

					if (null == categorys) {
						categorys = new ArrayList<Category>();
					}

					categorys.addAll(ParseJson.getCategorys(buf));
					categorysUpdateHandler.obtainMessage(200, categorys)
							.sendToTarget();
				}

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				progressCategory.closeProgress();
			}
		}
	};

	@Override
	public void onClick(View v) {

	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return false;
	}
}
