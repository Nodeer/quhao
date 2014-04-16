package com.withiter.quhao.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.ClientProtocolException;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.withiter.quhao.QHClientApplication;
import com.withiter.quhao.R;
import com.withiter.quhao.adapter.CategoryGridAdapter;
import com.withiter.quhao.adapter.TopMerchantGridAdapter;
import com.withiter.quhao.util.QuhaoLog;
import com.withiter.quhao.util.StringUtils;
import com.withiter.quhao.util.http.CommonHTTPRequest;
import com.withiter.quhao.util.tool.ParseJson;
import com.withiter.quhao.util.tool.ProgressDialogUtil;
import com.withiter.quhao.vo.Category;
import com.withiter.quhao.vo.TopMerchant;

@SuppressLint("InlinedApi")
public class MainActivity extends QuhaoBaseActivity {

	private String TAG = MainActivity.class.getName();
	protected ListView topMerchantListView;
	private GridView topMerchantsGird;
	private Button searchTextView;
	private List<TopMerchant> topMerchants;
	private GridView categorysGird;
	protected ProgressDialogUtil progressCategory;
	protected ProgressDialogUtil progressTopMerchant;
	private static final int UNLOCK_CLICK = 1000;
	private List<Category> categorys = null;
	private TextView cityBtn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main_layout);
		super.onCreate(savedInstanceState);

		// bind menu button function
		btnCategory.setOnClickListener(goCategory(this));
		btnNearby.setOnClickListener(goNearby(this));
		btnPerson.setOnClickListener(goPersonCenter(this));
		btnMore.setOnClickListener(goMore(this));
		
		// search function
		searchTextView = (Button) findViewById(R.id.edit_search);
		searchTextView.setOnClickListener(goMerchantsSearch(MainActivity.this));
		InputMethodManager inputMethodManager = (InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(searchTextView.getWindowToken(), 0);

		// top merchant function
		topMerchants = new ArrayList<TopMerchant>();
		topMerchantsGird = (GridView) findViewById(R.id.topMerchants);
		topMerchantsGird.setOnItemClickListener(topMerchantClickListener);
		
		// all categories
		categorys = new ArrayList<Category>();
		categorysGird = (GridView) findViewById(R.id.categorys);
		
		categorysGird.setOnItemClickListener(categorysClickListener);
		
		cityBtn = (TextView) this.findViewById(R.id.city);
		
		cityBtn.setOnClickListener(this);
		// TODO add default view here
		if (!networkOK) {
			unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			Builder dialog = new AlertDialog.Builder(MainActivity.this);
			dialog.setTitle("温馨提示").setMessage("Wifi/蜂窝网络未打开，或者网络情况不是很好哟").setPositiveButton("确定", null);
			dialog.show();
			
			return;
		}

	}

	/**
	 * 处理top merchant的UI更新
	 */
	private Handler topMerchantsUpdateHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 200) {
				super.handleMessage(msg);
				ListAdapter adapter = new TopMerchantGridAdapter(topMerchants, MainActivity.this);
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
				ListAdapter adapter = new CategoryGridAdapter(categorys, MainActivity.this);
				categorysGird.setAdapter(adapter);
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			}
		}
	};

	private OnItemClickListener topMerchantClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			String mid = topMerchants.get(position).mid;
			QuhaoLog.d(TAG, "mid:" + mid);
			if (StringUtils.isNull(mid)) {
				Builder dialog = new AlertDialog.Builder(MainActivity.this);
				dialog.setTitle("温馨提示").setMessage("推荐商家虚席以待").setPositiveButton("确定", null);
				dialog.show();
				return;
			}
			Intent intent = new Intent();
			intent.putExtra("merchantId", mid);
			intent.setClass(MainActivity.this, MerchantDetailActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
		}
	};

	private OnItemClickListener categorysClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			Category category = categorys.get(position);
			QuhaoLog.d(TAG, "the category is : " + category.categoryType + ", the count is : " + category.count);
			Intent intent = new Intent();
			intent.putExtra("categoryType", category.categoryType);
			intent.putExtra("cateName", category.cateName);
			intent.putExtra("categoryCount", String.valueOf(category.count));

			intent.setClass(MainActivity.this, MerchantListActivity.class);

			startActivity(intent);
			overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
		}
	};

	/**
	 * get top merchants from server side and display
	 */
	public void getTopMerchantsFromServerAndDisplay() {
		progressTopMerchant = new ProgressDialogUtil(this, R.string.empty, R.string.querying, false);
		progressTopMerchant.showProgress();
		Thread t = new Thread() {
			@Override
			public void run() {
				Looper.prepare();
				try {
					QuhaoLog.d(TAG, "Start to get Top Merchants data form server.");
					String result = CommonHTTPRequest.get("MerchantController/getTopMerchants?x=6");
					QuhaoLog.d(TAG, result);
					if (StringUtils.isNull(result)) {
						// TODO display error page here
						unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
					} else {
						if (null == topMerchants) {
							topMerchants = new ArrayList<TopMerchant>();
						}
						topMerchants.clear();
						topMerchants.addAll(ParseJson.getTopMerchants(result));

						// check the numbers of top merchant
						int topMerchantCount = topMerchants.size();
						if (topMerchantCount < 6) {
							for (int i = 0; i < 6 - topMerchantCount; i++) {
								TopMerchant topMerchant = new TopMerchant();
								topMerchants.add(topMerchant);
							}
						}
						topMerchantsUpdateHandler.obtainMessage(200, topMerchants).sendToTarget();
					}
				} catch (ClientProtocolException e) {
					// TODO display error page here
					unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
					e.printStackTrace();
					Builder dialog = new AlertDialog.Builder(MainActivity.this);
					dialog.setTitle("温馨提示").setMessage("使用\"取号\"人数火爆，亲，稍等片刻").setPositiveButton("确定", null);
					dialog.show();
				} catch (IOException e) {
					// TODO display error page here
					unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
					// Log.e(TAG, e.getCause().toString(), e);
					e.printStackTrace();
					Builder dialog = new AlertDialog.Builder(MainActivity.this);
					dialog.setTitle("温馨提示").setMessage("使用\"取号\"人数火爆，亲，稍等片刻").setPositiveButton("确定", null);
					dialog.show();
				} finally {
					progressTopMerchant.closeProgress();
				}
				Looper.loop();
			}
		};
		t.start();
	}

	/**
	 * get all categories from server and display them
	 */
	public void getCategoriesFromServerAndDisplay() {
		progressCategory = new ProgressDialogUtil(this, R.string.empty, R.string.querying, false);
		progressCategory.showProgress();
		Thread t = new Thread() {
			@Override
			public void run() {
				Looper.prepare();
				try {
					QuhaoLog.v(TAG, "get categorys data form server begin");
					String result = CommonHTTPRequest.get("MerchantController/allCategories");
					if (StringUtils.isNull(result)) {
						unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
					} else {
						if (null == categorys) {
							categorys = new ArrayList<Category>();
						}
						categorys.clear();
						categorys.addAll(ParseJson.getCategorys(result));
						categorysUpdateHandler.obtainMessage(200, categorys).sendToTarget();
					}

				} catch (Exception e) {
					unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
					e.printStackTrace();
				} finally {
					progressCategory.closeProgress();
				}
				Looper.loop();
			}
		};
		t.start();
	}

	@Override
	protected void onResume() {
		
		super.onResume();
		cityBtn.setText(QHClientApplication.getInstance().defaultCity.cityName);
		getTopMerchantsFromServerAndDisplay();
		getCategoriesFromServerAndDisplay();
	}
	
	@Override
	public void onClick(View v) {

		if(isClick)
		{
			return;
		}
		
		isClick = true;
		
		switch(v.getId())
		{
			case R.id.city:
				Intent intent = new Intent();
				intent.setClass(this, CitySelectActivity.class);
				startActivity(intent);
 			default:
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			break;
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return false;
	}
}
