package com.withiter.quhao.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.ClientProtocolException;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
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
import com.withiter.quhao.util.QuhaoLog;
import com.withiter.quhao.util.http.CommonHTTPRequest;
import com.withiter.quhao.util.tool.CommonTool;
import com.withiter.quhao.util.tool.ParseJson;
import com.withiter.quhao.util.tool.ProgressDialogUtil;
import com.withiter.quhao.vo.Category;
import com.withiter.quhao.vo.TopMerchant;

public class MainActivity extends AppStoreActivity {

	private String TAG = MainActivity.class.getName();
	protected ListView topMerchantListView;
	private GridView topMerchantsGird;
	private Button searchBtn;
	private List<TopMerchant> topMerchants;
	private GridView categorysGird;
	protected ProgressDialogUtil progressCategory;
	protected ProgressDialogUtil progressTopMerchant;
	private static final int UNLOCK_CLICK = 1000;
	private List<Category> categorys = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main_layout);
		super.onCreate(savedInstanceState);

		// TODO add default view here
		if (!networkOK) {
			Builder dialog = new AlertDialog.Builder(MainActivity.this);
			dialog.setTitle("温馨提示").setMessage("Wifi/蜂窝网络未打开，或者网络情况不是很好哟")
					.setPositiveButton("确定", null);
			dialog.show();
			return;
		}

		// initView();
		topMerchantsGird = (GridView) findViewById(R.id.topMerchants);

		// top merchant function
		topMerchants = new ArrayList<TopMerchant>();
		getTopMerchantsFromServerAndDisplay();
		topMerchantsGird.setOnItemClickListener(topMerchantClickListener);

		// search function
		searchBtn = (Button) findViewById(R.id.edit_search);
		searchBtn.setOnClickListener(goMerchantsSearch(MainActivity.this));

		// all categories
		categorys = new ArrayList<Category>();
		categorysGird = (GridView) findViewById(R.id.categorys);
		getCategoriesFromServerAndDisplay();
		categorysGird.setOnItemClickListener(categorysClickListener);

		// bind menu button function
		btnCategory.setOnClickListener(goCategory(this));
		btnNearby.setOnClickListener(goNearby(this));
		btnPerson.setOnClickListener(goPersonCenter(this));
		btnMore.setOnClickListener(goMore(this));
		
	}

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
			QuhaoLog.d(TAG, "the category is : " + category.categoryType
					+ ", the count is : " + category.count);
			Intent intent = new Intent();
			intent.putExtra("categoryType", category.categoryType);
			intent.putExtra("categoryTypeStr", category.categoryTypeStr);
			intent.putExtra("categoryCount", String.valueOf(category.count));
			intent.setClass(MainActivity.this, MerchantListActivity.class);

			startActivity(intent);
			overridePendingTransition(R.anim.main_enter, R.anim.main_exit);
		}
	};

	/**
	 * get top merchants from server side and display
	 */
	public void getTopMerchantsFromServerAndDisplay() {
		progressTopMerchant = new ProgressDialogUtil(this, R.string.empty,
				R.string.querying, false);
		progressTopMerchant.showProgress();
		Thread t = new Thread() {
			@Override
			public void run() {
				Looper.prepare();
				try {
					QuhaoLog.i(TAG, "Start to get categorys data form server.");
					String result = CommonHTTPRequest
							.get("MerchantController/getTopMerchants?x=6");
					QuhaoLog.d(TAG, result);
					if (CommonTool.isNull(result)) {
						unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK,
								1000);
					} else {
						if (null == topMerchants) {
							topMerchants = new ArrayList<TopMerchant>();
						}
						topMerchants.clear();
						topMerchants.addAll(ParseJson.getTopMerchants(result));
						topMerchantsUpdateHandler.obtainMessage(200,
								topMerchants).sendToTarget();
					}
				} catch (ClientProtocolException e) {
					unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
					e.printStackTrace();
					 Builder dialog = new
					 AlertDialog.Builder(MainActivity.this);
					 dialog.setTitle("温馨提示1")
					 .setMessage("使用\"取号\"人数火爆，服务器处理不过来了，亲，稍等片刻")
					 .setPositiveButton("确定", null);
					 dialog.show();
				} catch (IOException e) {
					unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
					// Log.e(TAG, e.getCause().toString(), e);
					e.printStackTrace();
					 Builder dialog = new
					 AlertDialog.Builder(MainActivity.this);
					 dialog.setTitle("温馨提示2")
					 .setMessage("使用\"取号\"人数火爆，服务器处理不过来了，亲，稍等片刻")
					 .setPositiveButton("确定", null);
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
		if (isClick) {
			return;
		}

		isClick = true;
		progressCategory = new ProgressDialogUtil(this, R.string.empty,
				R.string.querying, false);
		progressCategory.showProgress();
		Thread t = new Thread() {
			@Override
			public void run() {
				Looper.prepare();
				try {
					QuhaoLog.v(TAG, "get categorys data form server begin");
					String result = CommonHTTPRequest
							.get("MerchantController/allCategories");
					if (CommonTool.isNull(result)) {
						unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK,
								1000);
					} else {
						if (null == categorys) {
							categorys = new ArrayList<Category>();
						}
						categorys.clear();
						categorys.addAll(ParseJson.getCategorys(result));
						categorysUpdateHandler.obtainMessage(200, categorys)
								.sendToTarget();
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

	private Runnable categoryRunnable = new Runnable() {
		@Override
		public void run() {

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
