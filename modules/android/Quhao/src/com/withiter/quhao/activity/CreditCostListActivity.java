package com.withiter.quhao.activity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.withiter.quhao.exception.NoResultFromHTTPRequestException;
import com.withiter.quhao.task.DeleteCreditTask;
import com.withiter.quhao.util.StringUtils;
import com.withiter.quhao.util.http.CommonHTTPRequest;
import com.withiter.quhao.util.tool.ParseJson;
import com.withiter.quhao.vo.Credit;

public class CreditCostListActivity extends QuhaoBaseActivity implements OnItemClickListener{

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
		creditsListView.setOnItemClickListener(CreditCostListActivity.this);
		
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
				
				if (isFirstLoad) {

					creditAdapter = new CreditAdapter(CreditCostListActivity.this, creditsListView, credits);
					creditsListView.setAdapter(creditAdapter);
					isFirstLoad = false;
				} else {
					creditAdapter.credits = credits;
				}
				
				creditAdapter.notifyDataSetChanged();
				creditsListView.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						Log.e(TAG, "onclick : " + position);
						// 取得ViewHolder对象，这样就省去了通过层层的findViewById去实例化我们需要的cb实例的步骤
						CreditCostHolder holder = (CreditCostHolder) view.getTag();
						
						// 改变CheckBox的状态
						holder.cb.toggle();
						// 将CheckBox的选中状况记录下来
						// 调整选定条目
						if (holder.cb.isChecked() == true) {
							credits.get(position).isChecked = "true";
//								checkNum++;
						} else {
							credits.get(position).isChecked = "true";
//								checkNum--;
						}
					}
					
				});
				CreditCostListActivity.this.findViewById(R.id.loadingbar).setVisibility(View.GONE);
				CreditCostListActivity.this.findViewById(R.id.serverdata).setVisibility(View.VISIBLE);
				deleteBtn.setVisibility(View.VISIBLE);
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			}

		}

	};

	private Runnable getCreditsRunnable = new Runnable() {
		@Override
		public void run() {
			try {
				Looper.prepare();
				String accountId = QHClientApplication.getInstance().accountInfo.accountId;
				String buf = CommonHTTPRequest.get("getCreditCost?accountId=" + accountId);
				if (StringUtils.isNull(buf) || "[]".equals(buf)) {
					unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
					throw new NoResultFromHTTPRequestException();
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
		
		
		// 解锁
		unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);

		switch (v.getId()) {
			case R.id.btn_delete:
				
				if(null!=creditAdapter && "true".equals(creditAdapter.isShowDelete))
				{
					deleteBtn.setText(R.string.delete);
					deleteLayout.setVisibility(View.GONE);
					creditAdapter.isShowDelete = "false";
					
					final List<Credit> creditsTemp = new ArrayList<Credit>(credits.size());
	//				Collections.copy(rvosTemp, reservations);
	//				System.arraycopy(reservations, 0, rvosTemp, 0, reservations.size());
					for (int i = 0; i < credits.size(); i++) {
						creditsTemp.add(credits.get(i));
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
					String url = "delCredit?id=" + ridStr;
					final DeleteCreditTask task = new DeleteCreditTask(R.string.waitting,this,url);
					task.execute(new Runnable(){
	
						@Override
						public void run() {
							
							credits = creditsTemp;
							if(!credits.isEmpty())
							{
								for (int i = 0; i < credits.size(); i++) {
									credits.get(i).isChecked = "false";
								}
							}
							creditAdapter.credits = credits;
							creditAdapter.notifyDataSetChanged();
							Toast.makeText(CreditCostListActivity.this, R.string.delete_success, Toast.LENGTH_LONG).show();
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
							Toast.makeText(CreditCostListActivity.this, R.string.delete_failed, Toast.LENGTH_LONG).show();
							
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
				}
				
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
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
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
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
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
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
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				break;
			default:
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
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
		CreditCostListActivity.this.findViewById(R.id.loadingbar).setVisibility(View.VISIBLE);
		CreditCostListActivity.this.findViewById(R.id.serverdata).setVisibility(View.GONE);
		initListView();
	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.i(TAG, "backClicked: " + backClicked);
		if (backClicked) {
			overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		
		// 已经点过，直接返回
		if (isClick) {
			return;
		}

		// 设置已点击标志，避免快速重复点击
		isClick = true;
		// 解锁
		unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
		
		Credit credit = credits.get(position);
		if(StringUtils.isNotNull(credit.merchantId))
		{
			Intent intent = new Intent();
			intent.putExtra("merchantId", credit.merchantId);
			intent.setClass(CreditCostListActivity.this, MerchantDetailActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
		}
		else
		{
			AlertDialog.Builder builder = new Builder(this);
			builder.setTitle("温馨提示");
			builder.setMessage("对不起，亲，不是商家不能查看哦。");
			builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			builder.create().show();
		}
		
	}

}
