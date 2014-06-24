package com.withiter.quhao.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.withiter.quhao.QHClientApplication;
import com.withiter.quhao.R;
import com.withiter.quhao.adapter.CreditAdapter;
import com.withiter.quhao.adapter.CreditCostHolder;
import com.withiter.quhao.task.DeleteCreditTask;
import com.withiter.quhao.util.ActivityUtil;
import com.withiter.quhao.util.StringUtils;
import com.withiter.quhao.util.http.CommonHTTPRequest;
import com.withiter.quhao.util.tool.ParseJson;
import com.withiter.quhao.vo.Credit;

public class CreditCostListActivity extends QuhaoBaseActivity{

	protected static boolean backClicked = false;
	private static String TAG = CreditCostListActivity.class.getName();
	private List<Credit> credits;
	private ListView creditsListView;
	private CreditAdapter creditAdapter;
	private boolean isFirstLoad = true;
	

	/**
	 * 删除面板的layout
	 */
	private LinearLayout deleteLayout;
	
	/**
	 * 删除按钮
	 */
	private Button deleteBtn;
	
	/**
	 * 选择所有按钮
	 */
	private Button selectAllBtn;
	
	/**
	 * 反向选择
	 */
	private Button deselectAllBtn;
	
	/**
	 * 取消
	 */
	private Button cancelBtn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.credit_cost_list_layout);
		super.onCreate(savedInstanceState);

		creditsListView = (ListView) this.findViewById(R.id.creditsListView);
		
		btnBack.setOnClickListener(goBack(this, this.getClass().getName()));
		
		deleteBtn = (Button) this.findViewById(R.id.btn_delete);
		deleteBtn.setOnClickListener(this);
		deleteBtn.setVisibility(View.GONE);
		selectAllBtn = (Button) this.findViewById(R.id.bt_selectall);
		selectAllBtn.setOnClickListener(this);
		deselectAllBtn = (Button) this.findViewById(R.id.bt_deselectall);
		deselectAllBtn.setOnClickListener(this);
		cancelBtn = (Button) this.findViewById(R.id.bt_cancel);
		cancelBtn.setOnClickListener(this);
		
		deleteLayout = (LinearLayout) this.findViewById(R.id.deleteMenuLayout);
		deleteLayout.setVisibility(View.GONE);
		
		CreditCostListActivity.this.findViewById(R.id.loadingbar).setVisibility(View.VISIBLE);
		CreditCostListActivity.this.findViewById(R.id.serverdata).setVisibility(View.GONE);
	}

	private void initListView() {
		Thread thread = new Thread(getCreditsRunnable);
		thread.start();
	}

	private Handler creditsUpdateHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 200) {
				super.handleMessage(msg);

				if(null == credits || credits.isEmpty())
				{
					deleteBtn.setVisibility(View.GONE);
				}
				else
				{
					deleteBtn.setVisibility(View.VISIBLE);
				}
				
				if (isFirstLoad) {

					creditAdapter = new CreditAdapter(CreditCostListActivity.this, creditsListView, credits);
					creditsListView.setAdapter(creditAdapter);
					isFirstLoad = false;
				} else {
					creditAdapter.credits = credits;
				}
				
				if (null == credits ||credits.isEmpty()) {
					Toast.makeText(CreditCostListActivity.this, R.string.no_result_4_credit_cost, Toast.LENGTH_SHORT).show();
				}
				
				creditAdapter.notifyDataSetChanged();
				creditsListView.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						Log.e(TAG, "onclick : " + position);
						if(null!=creditAdapter && "true".equals(creditAdapter.isShowDelete))
						{
							// 取得ViewHolder对象，这样就省去了通过层层的findViewById去实例化我们需要的cb实例的步骤
							CreditCostHolder holder = (CreditCostHolder) view.getTag();
							
							// 改变CheckBox的状态
							holder.cb.toggle();
							// 将CheckBox的选中状况记录下来
							// 调整选定条目
							if (holder.cb.isChecked() == true) {
								credits.get(position).isChecked = "true";
//									checkNum++;
							} else {
								credits.get(position).isChecked = "false";
//									checkNum--;
							}
						}
						
					}
					
				});
				
				CreditCostListActivity.this.findViewById(R.id.loadingbar).setVisibility(View.GONE);
				CreditCostListActivity.this.findViewById(R.id.serverdata).setVisibility(View.VISIBLE);
				
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			}

		}

	};

	private Runnable getCreditsRunnable = new Runnable() {
		@Override
		public void run() {
			try {
				Looper.prepare();
				if (!ActivityUtil.isNetWorkAvailable(getApplicationContext())) {
					Toast.makeText(getApplicationContext(), R.string.network_error_info, Toast.LENGTH_SHORT).show();
					unlockHandler.sendEmptyMessage(UNLOCK_CLICK);
					findViewById(R.id.loadingbar).setVisibility(View.GONE);
					findViewById(R.id.commentsLayout).setVisibility(View.VISIBLE);
					return;
				}
				String accountId = QHClientApplication.getInstance().accountInfo.accountId;
				String buf = CommonHTTPRequest.get("getCreditCost?accountId=" + accountId);
				if (StringUtils.isNull(buf) || "[]".equals(buf)) {
					if (isFirstLoad || null == credits) {
						credits = new ArrayList<Credit>();
					}
					
					creditsUpdateHandler.obtainMessage(200, credits).sendToTarget();
					unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
//					throw new NoResultFromHTTPRequestException();
				} else {
					
					if (isFirstLoad || null == credits) {
						credits = new ArrayList<Credit>();
					}
					
					List<Credit> credits1 = ParseJson.getCredits(buf);
					credits.addAll(credits1);
					creditsUpdateHandler.obtainMessage(200, credits).sendToTarget();
				}
			} catch (Exception e) {
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				e.printStackTrace();
			} finally {
				Looper.loop();
			}
		}
	};

	@Override
	public void onClick(View v) {

		// 已经点过，直接返回
		if (isClick) {
			return;
		}

		// 设置已点击标志，避免快速重复点击
		isClick = true;
		
		switch (v.getId()) {
			case R.id.btn_delete:
				
				if(null!=creditAdapter && "true".equals(creditAdapter.isShowDelete))
				{
					deleteBtn.setText(R.string.delete);
					deleteLayout.setVisibility(View.GONE);
					creditAdapter.isShowDelete = "false";
					
					/*
					final List<Credit> creditsTemp = new ArrayList<Credit>(credits.size());
	//				Collections.copy(rvosTemp, reservations);
	//				System.arraycopy(reservations, 0, rvosTemp, 0, reservations.size());
					if (null != credits && !credits.isEmpty()) {
						for (int i = 0; i < credits.size(); i++) {
							creditsTemp.add(new Credit(credits.get(i).creditId, credits.get(i).accountId, credits.get(i).merchantId, credits.get(i).merchantName, credits.get(i).merchantAddress, credits.get(i).reservationId, credits.get(i).cost, credits.get(i).status, credits.get(i).created));
						}
					}
					
					List<String> rIds = new ArrayList<String>();
					
					Iterator<Credit> iterator = creditsTemp.iterator();
					String ridStr = "";
					while (iterator.hasNext()) {
						Credit temp = iterator.next();
						if ("true".equals(temp.isChecked)) {
							rIds.add(temp.creditId);
							ridStr = ridStr + temp.creditId + ",";
							iterator.remove();
						}
					}
					Log.e(TAG, ridStr);
					*/
					
					String ridStr = "";
					if (null != credits && !credits.isEmpty()) {
						for (int i = 0; i < credits.size(); i++) {
							if ("true".equals(credits.get(i).isChecked)) {
								ridStr = ridStr + credits.get(i).creditId + ",";
							}
						}
					}
					// 解锁
					unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
//					String url = "delCredit?id=" + ridStr;
					String url = "delCredit";
					Map<String, String> params = new HashMap<String, String>();
					params.put("id", ridStr);
					final DeleteCreditTask task = new DeleteCreditTask(R.string.waitting,this,url,params);
					task.execute(new Runnable(){
	
						@Override
						public void run() {
							
//							credits = creditsTemp;
							
							Iterator<Credit> iterator = credits.iterator();
							while (iterator.hasNext()) {
								Credit temp = iterator.next();
								if ("true".equals(temp.isChecked)) {
									iterator.remove();
								}
							}
							
							if(!credits.isEmpty())
							{
								for (int i = 0; i < credits.size(); i++) {
									credits.get(i).isChecked = "false";
								}
							}
							creditAdapter.credits = credits;
							creditAdapter.notifyDataSetChanged();
							Toast.makeText(CreditCostListActivity.this, R.string.delete_success, Toast.LENGTH_SHORT).show();
						}
						
					},new Runnable() {
						
						@Override
						public void run() {
							if(!credits.isEmpty())
							{
								for (int i = 0; i < credits.size(); i++) {
									credits.get(i).isChecked = "false";
								}
							}
							creditAdapter.credits = credits;
							creditAdapter.notifyDataSetChanged();
							Toast.makeText(CreditCostListActivity.this, R.string.delete_failed, Toast.LENGTH_SHORT).show();
							
						}
					});
					
					
	//				reservationForPaiduiAdapter.notifyDataSetChanged();
				}
				else if(null!=creditAdapter && "false".equals(creditAdapter.isShowDelete))
				{
					deleteBtn.setText(R.string.confirm_delete);
					deleteLayout.setVisibility(View.VISIBLE);
					creditAdapter.isShowDelete = "true";
					creditAdapter.notifyDataSetChanged();
					// 解锁
					unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 10);
				}
				
				break;
			case R.id.bt_selectall:
				if(null!=creditAdapter && "true".equals(creditAdapter.isShowDelete))
				{
					deleteLayout.setVisibility(View.VISIBLE);
					if(null == credits)
					{
						credits = new ArrayList<Credit>();
					}
					
					if(!credits.isEmpty())
					{
						for (int i = 0; i < credits.size(); i++) {
							credits.get(i).isChecked = "true";
						}
						creditAdapter.credits = credits;
						creditAdapter.notifyDataSetChanged();
					}
					
				}
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 10);
				break;
			case R.id.bt_deselectall:
				if(null!=creditAdapter && "true".equals(creditAdapter.isShowDelete))
				{
					deleteLayout.setVisibility(View.VISIBLE);
					if(null == credits)
					{
						credits = new ArrayList<Credit>();
					}
					
					if(!credits.isEmpty())
					{
						for (int i = 0; i < credits.size(); i++) {
							if("true".equals(credits.get(i).isChecked))
							{
								credits.get(i).isChecked = "false";
							}
							else
							{
								credits.get(i).isChecked = "true";
							}
						}
						creditAdapter.credits = credits;
						creditAdapter.notifyDataSetChanged();
					}
					
				}
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 10);
				break;
			case R.id.bt_cancel:
				if(null!=creditAdapter && "true".equals(creditAdapter.isShowDelete))
				{
					deleteBtn.setText(R.string.delete);
					deleteLayout.setVisibility(View.GONE);
					if(null == credits)
					{
						credits = new ArrayList<Credit>();
					}
					
					creditAdapter.isShowDelete = "false";
					if(!credits.isEmpty())
					{
						for (int i = 0; i < credits.size(); i++) {
							credits.get(i).isChecked = "false";
						}
						creditAdapter.credits = credits;
						creditAdapter.notifyDataSetChanged();
					}
					
				}
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 10);
				break;
			default:
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 10);
				break;
		}

	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return false;
	}
	
	@Override
	protected void onResume() {
		backClicked = false;
		super.onResume();
		
		credits = new ArrayList<Credit>();
		initListView();
	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.i(TAG, "backClicked: " + backClicked);
		if (backClicked) {
		}
	}

}
