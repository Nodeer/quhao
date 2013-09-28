package com.withiter.quhao.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.ClientProtocolException;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.withiter.quhao.QHClientApplication;
import com.withiter.quhao.R;
import com.withiter.quhao.adapter.CategoryGridAdapter;
import com.withiter.quhao.adapter.SearchAdapter;
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

@SuppressLint("InlinedApi")
public class MainActivity extends AppStoreActivity {

	private String TAG = MainActivity.class.getName();
	protected ListView topMerchantListView;
	private GridView topMerchantsGird;
	private EditText searchTextView;
	private List<TopMerchant> topMerchants;
	private GridView categorysGird;
	protected ProgressDialogUtil progressCategory;
	protected ProgressDialogUtil progressTopMerchant;
	private static final int UNLOCK_CLICK = 1000;
	private List<Category> categorys = null;

	private WindowManager wm = null;
	private WindowManager.LayoutParams wmParams = null;
	private CommonFloatView searchResultView = null;
	private List<Merchant> mList = null;
	/**
	 * called when user input something in search box
	 * @param mList
	 */
	private void createView() {
		int[] location = new int[2];
		searchTextView.getLocationOnScreen(location);
		int height = searchTextView.getHeight();

		searchResultView = new CommonFloatView(getApplicationContext());
		searchResultView.setBackgroundColor(Color.RED);
		searchResultView.getBackground().setAlpha(80);
		searchResultView.setVerticalScrollBarEnabled(true);
		searchResultView.setAdapter(new SearchAdapter(searchResultView, mList));
		searchResultView.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int index,
					long arg3) {
				destroyView();
				Merchant merchant = mList.get(index);
				QuhaoLog.i(TAG, "merchant.name: "+merchant.name);
				Intent intent = new Intent();
				intent.putExtra("merchantId", merchant.id);
				intent.setClass(MainActivity.this,
						MerchantDetailActivity.class);
				startActivity(intent);
			}
		});

		// 获取WindowManager
		wm = (WindowManager) getApplicationContext().getSystemService("window");
		wmParams = ((QHClientApplication) getApplication()).getMywmParams();

		// 设置LayoutParams(全局变量）相关参数
		wmParams.type = LayoutParams.MATCH_PARENT; // 设置window type
		wmParams.format = PixelFormat.RGBA_8888; // 设置图片格式，效果为背景透明
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

	private void destroyView(){
		// 获取WindowManager
		wm = (WindowManager) getApplicationContext().getSystemService("window");
		wm.removeView(searchResultView);
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
		//topMerchantsGird.setOnItemClickListener(topMerchantClickListener);

		// search function
		searchTextView = (EditText) findViewById(R.id.edit_search);
		searchTextView.addTextChangedListener(new TextWatcher() {
			@SuppressWarnings("unchecked")
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
						mList = (List<Merchant>) ParseJson
								.getMerchants(result);
						for (Merchant m : mList) {
							QuhaoLog.i(TAG, m.name);
						}
						createView();
					}
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
			}

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
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
					dialog.setTitle("温馨提示")
							.setMessage("使用\"取号\"人数火爆，亲，稍等片刻")
							.setPositiveButton("确定", null);
					dialog.show();
				} catch (IOException e) {
					unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
					// Log.e(TAG, e.getCause().toString(), e);
					e.printStackTrace();
					Builder dialog = new AlertDialog.Builder(MainActivity.this);
					dialog.setTitle("温馨提示")
							.setMessage("使用\"取号\"人数火爆，亲，稍等片刻")
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

	@Override
	public void onClick(View v) {

	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return false;
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		
		InputMethodManager imm= (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		
		if(ev.getAction() == MotionEvent.ACTION_DOWN){
			if(MainActivity.this.getCurrentFocus()!=null && MainActivity.this.getCurrentFocus().getWindowToken()!=null){
				imm.hideSoftInputFromWindow(MainActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			}
		}
		
		return super.dispatchTouchEvent(ev);
	}
}
