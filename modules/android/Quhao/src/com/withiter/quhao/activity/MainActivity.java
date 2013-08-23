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
	private DisplayMetrics localDisplayMetrics;
	protected ProgressDialogUtil progressCategory;
	protected ProgressDialogUtil progressTopMerchant;
	private static final int UNLOCK_CLICK = 1000;
	private boolean isClick = false;
	private List<Category> categorys = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main_layout);
		super.onCreate(savedInstanceState);

		if (!networkOK) {
			Builder dialog = new AlertDialog.Builder(MainActivity.this);
			dialog.setTitle("温馨提示").setMessage("Wifi/蜂窝网络未打开，或者网络情况不是很好哟")
					.setPositiveButton("确定", null);
			dialog.show();
			return;
		}

		// initView();
		topMerchantsGird = (GridView) findViewById(R.id.topMerchants);
		localDisplayMetrics = getResources().getDisplayMetrics();

		// top merchant function
		topMerchants = new ArrayList<TopMerchant>();
		getTopMerchants();
		topMerchantsGird.setOnItemClickListener(topMerchantClickListener);

		// search function
		searchBtn = (Button) findViewById(R.id.edit_search);
		searchBtn.setOnClickListener(goMerchantsSearch(MainActivity.this));

		// all categories
		categorys = new ArrayList<Category>();
		categorysGird = (GridView) findViewById(R.id.categorys);
		getCateGorys();
		categorysGird.setOnItemClickListener(categorysClickListener);

		// bind menu button function
		btnCategory.setOnClickListener(goCategory(this));
		btnNearby.setOnClickListener(goNearby(this));
		btnPerson.setOnClickListener(goPersonCenter(this));
		btnMore.setOnClickListener(goMore(this));
	}

	/**
	 * handler处理 解锁的时候可能会关闭其他的等待提示框
	 */
	private Handler unlockHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == UNLOCK_CLICK) {
				// 解锁
				MainActivity.this.isClick = false;
			}
		}
	};

	private Handler topMerchantsUpdateHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 200) {
				super.handleMessage(msg);
				Object obj = msg.obj;
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

	/**
	 * 获取top merchant线程
	 */
	private Runnable topMerchantsRunnable = new Runnable() {
		@Override
		public void run() {
			try {
				Log.i(TAG, "Start to get categorys data form server.");
				String result = CommonHTTPRequest
						.get("MerchantController/getTopMerchants?x=6");
				Log.d(TAG, result);
				if (CommonTool.isNull(result)) {
					Looper.prepare();
					unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
					Looper.loop();
				} else {
					if (null == topMerchants) {
						topMerchants = new ArrayList<TopMerchant>();
					}
					topMerchants.addAll(ParseJson.getTopMerchants(result));
					topMerchantsUpdateHandler.obtainMessage(200, topMerchants)
							.sendToTarget();
				}
			} catch (ClientProtocolException e) {
//				Looper.prepare();
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
//				Log.e(TAG, e.getCause().toString(), e);
				e.printStackTrace();
//				Builder dialog = new AlertDialog.Builder(MainActivity.this);
//				dialog.setTitle("温馨提示")
//						.setMessage("使用\"取号\"人数火爆，服务器处理不过来了，亲，稍等片刻")
//						.setPositiveButton("确定", null);
//				dialog.show();
//				Looper.loop();
			} catch (IOException e) {
//				Looper.prepare();
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
//				Log.e(TAG, e.getCause().toString(), e);
				e.printStackTrace();
//				Builder dialog = new AlertDialog.Builder(MainActivity.this);
//				dialog.setTitle("温馨提示")
//						.setMessage("使用\"取号\"人数火爆，服务器处理不过来了，亲，稍等片刻")
//						.setPositiveButton("确定", null);
//				dialog.show();
//				Looper.loop();
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
				String result = CommonHTTPRequest
						.get("MerchantController/allCategories");
				if (CommonTool.isNull(result)) {
					unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				} else {
					if (null == categorys) {
						categorys = new ArrayList<Category>();
					}

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
