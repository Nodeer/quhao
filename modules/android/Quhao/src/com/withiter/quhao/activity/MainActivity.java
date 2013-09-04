package com.withiter.quhao.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.ClientProtocolException;

import android.app.ActionBar.LayoutParams;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
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
import com.withiter.quhao.view.CommonFloatView;
import com.withiter.quhao.vo.Category;
import com.withiter.quhao.vo.Merchant;
import com.withiter.quhao.vo.TopMerchant;

public class MainActivity extends AppStoreActivity {

	private String TAG = MainActivity.class.getName();
	protected ListView topMerchantListView;
	private GridView topMerchantsGird;
	private TextView searchTextView;
	private List<TopMerchant> topMerchants;
	private GridView categorysGird;
	protected ProgressDialogUtil progressCategory;
	protected ProgressDialogUtil progressTopMerchant;
	private static final int UNLOCK_CLICK = 1000;
	private List<Category> categorys = null;

	/** Called when the activity is first created. */
	private WindowManager wm = null;
	private WindowManager.LayoutParams wmParams = null;

	private CommonFloatView searchResultView = null;

	private void createView() {
		// get the absolute position of searchTextView
		int[] location = new int[2];
		searchTextView.getLocationOnScreen(location);
		int x = location[0];
		int y = location[1];
		int height = searchTextView.getHeight();
		QuhaoLog.i(TAG, "x: "+x);
		QuhaoLog.i(TAG, "y: "+y);

		searchResultView = new CommonFloatView(getApplicationContext());
		searchResultView.setBackgroundColor(Color.RED);
		searchResultView.getBackground().setAlpha(80);
//		searchResultView.setLeft(left)
		// searchResultView.setImageResource(R.drawable.icon);
		// 获取WindowManager
		wm = (WindowManager) getApplicationContext().getSystemService("window");
		// 设置LayoutParams(全局变量）相关参数
		wmParams = ((QHClientApplication) getApplication()).getMywmParams();

		/**
		 * 以下都是WindowManager.LayoutParams的相关属性 具体用途可参考SDK文档
		 */
		wmParams.type = LayoutParams.MATCH_PARENT; // 设置window type
		wmParams.format = PixelFormat.RGBA_8888; // 设置图片格式，效果为背景透明

		/**
		 * 下面的flags属性的效果形同“锁定”。 悬浮窗不可触摸，不接受任何事件,同时不影响后面的事件响应。
		 * wmParams.flags=LayoutParams.FLAG_NOT_TOUCH_MODAL |
		 * LayoutParams.FLAG_NOT_FOCUSABLE | LayoutParams.FLAG_NOT_TOUCHABLE;
		 */

		wmParams.gravity = Gravity.LEFT | Gravity.TOP; // 调整悬浮窗口至左上角
		// 以屏幕左上角为原点，设置x、y初始值
		wmParams.x = 0;
		wmParams.y = height;

		QuhaoLog.i(TAG, "float y: " + wmParams.y);
		
		// 设置悬浮窗口长宽数据
		wmParams.width = LayoutParams.MATCH_PARENT;
		wmParams.height = 500;

		// 显示myFloatView图像
		wm.addView(searchResultView, wmParams);

	}

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
		searchTextView = (TextView) findViewById(R.id.edit_search);
		searchTextView.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable arg0) {
				String keyword = searchTextView.getText().toString().trim();
				QuhaoLog.i(TAG, keyword);
				try {
					String result = CommonHTTPRequest
							.get("MerchantController/getMerchantsByName?name="
									+ keyword);
					if (result.equalsIgnoreCase("null")) {
						QuhaoLog.i(TAG, "no result");
					} else {
						QuhaoLog.i(TAG, result);
						@SuppressWarnings("unchecked")
						List<Merchant> mList = (List<Merchant>) ParseJson
								.getMerchants(result);
						for (Merchant m : mList) {
							QuhaoLog.i(TAG, m.name);
						}
						createView();
					}
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub
			}

		});
		// searchTextView.setOnClickListener(goMerchantsSearch(MainActivity.this));

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
					if (StringUtils.isNull(result)) {
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
					Builder dialog = new AlertDialog.Builder(MainActivity.this);
					dialog.setTitle("温馨提示1")
							.setMessage("使用\"取号\"人数火爆，服务器处理不过来了，亲，稍等片刻")
							.setPositiveButton("确定", null);
					dialog.show();
				} catch (IOException e) {
					unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
					// Log.e(TAG, e.getCause().toString(), e);
					e.printStackTrace();
					Builder dialog = new AlertDialog.Builder(MainActivity.this);
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
					if (StringUtils.isNull(result)) {
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
