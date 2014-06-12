package com.withiter.quhao.activity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
import com.withiter.quhao.adapter.ReservationForHistoryPaiduiAdapter;
import com.withiter.quhao.adapter.ViewHolderHistoryPaidui;
import com.withiter.quhao.exception.NoResultFromHTTPRequestException;
import com.withiter.quhao.task.DeleteReservationsInHistoryPaiduiTask;
import com.withiter.quhao.util.ActivityUtil;
import com.withiter.quhao.util.StringUtils;
import com.withiter.quhao.util.http.CommonHTTPRequest;
import com.withiter.quhao.util.tool.ParseJson;
import com.withiter.quhao.vo.ReservationVO;

/**
 * Quhao states of Current/History
 * 
 */
public class QuhaoHistoryStatesActivity extends QuhaoBaseActivity{

	protected static boolean backClicked = false;
	private static String TAG = QuhaoHistoryStatesActivity.class.getName();

	private List<ReservationVO> reservations;
	private ListView paiduiListView;
	private ReservationForHistoryPaiduiAdapter reservationForPaiduiAdapter;

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
	
	private int clickNum;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.paidui_history_list_layout);
		super.onCreate(savedInstanceState);

		paiduiListView = (ListView) this.findViewById(R.id.paiduiListView);
		btnBack.setOnClickListener(goBack(this));
		
		deleteBtn = (Button) this.findViewById(R.id.btn_delete);
		deleteBtn.setOnClickListener(this);
		
		selectAllBtn = (Button) this.findViewById(R.id.bt_selectall);
		selectAllBtn.setOnClickListener(this);
		deselectAllBtn = (Button) this.findViewById(R.id.bt_deselectall);
		deselectAllBtn.setOnClickListener(this);
		cancelBtn = (Button) this.findViewById(R.id.bt_cancel);
		cancelBtn.setOnClickListener(this);
		
		deleteLayout = (LinearLayout) this.findViewById(R.id.deleteMenuLayout);
		deleteLayout.setVisibility(View.GONE);
		deleteLayout.setVisibility(View.GONE);
		QuhaoHistoryStatesActivity.this.findViewById(R.id.loadingbar).setVisibility(View.VISIBLE);
		QuhaoHistoryStatesActivity.this.findViewById(R.id.serverdata).setVisibility(View.GONE);
		reservations = new ArrayList<ReservationVO>();
		
//		initData();
	}

	private void initData() {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				Looper.prepare();
				try {
					
					String url = "";
					String accountId = QHClientApplication.getInstance().accountInfo.accountId;
					url = "getHistoryMerchants?accountId=" + accountId;
					
					if (!ActivityUtil.isNetWorkAvailable(getApplicationContext())) {
						Toast.makeText(getApplicationContext(), R.string.network_error_info, Toast.LENGTH_SHORT).show();
						reservations = new ArrayList<ReservationVO>();
						reservationsUpdateHandler.obtainMessage(200, reservations).sendToTarget();
						return;
					}
					
					String buf = CommonHTTPRequest.get(url);
					if (StringUtils.isNull(buf) || "[]".equals(buf)) {
						reservations = new ArrayList<ReservationVO>();
						reservationsUpdateHandler.obtainMessage(200, reservations).sendToTarget();
						unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
						throw new NoResultFromHTTPRequestException();
					} else {
						reservations = new ArrayList<ReservationVO>();
						reservations = ParseJson.getReservations(buf);
						reservationsUpdateHandler.obtainMessage(200, reservations).sendToTarget();
					}

				} catch (Exception e) {
					unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
					e.printStackTrace();
				} finally {
					Looper.loop();
				}

			}
		});
		thread.start();

	}

	private Handler reservationsUpdateHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 200) {
				super.handleMessage(msg);
				if(null == reservations || reservations.isEmpty())
				{
					deleteBtn.setVisibility(View.GONE);
				}
				reservationForPaiduiAdapter = new ReservationForHistoryPaiduiAdapter(QuhaoHistoryStatesActivity.this, paiduiListView, reservations);
				paiduiListView.setAdapter(reservationForPaiduiAdapter);
				reservationForPaiduiAdapter.notifyDataSetChanged();
				
				if (null == reservations ||reservations.isEmpty()) {
					Toast.makeText(QuhaoHistoryStatesActivity.this, R.string.no_result_4_quhao_history, Toast.LENGTH_SHORT).show();
				}
				
				paiduiListView.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						Log.e(TAG, "onclick : " + position);
						if("true".equals(reservationForPaiduiAdapter.isShowDelete))
						{
							// 取得ViewHolder对象，这样就省去了通过层层的findViewById去实例化我们需要的cb实例的步骤
							ViewHolderHistoryPaidui holder = (ViewHolderHistoryPaidui) view.getTag();
							
							// 改变CheckBox的状态
							holder.cb.toggle();
							// 将CheckBox的选中状况记录下来
							// 调整选定条目
							if (holder.cb.isChecked() == true) {
								reservations.get(position).isChecked = "true";
//									checkNum++;
							} else {
								reservations.get(position).isChecked = "true";
//									checkNum--;
							}
						}
						
					}
					
				});
				
				QuhaoHistoryStatesActivity.this.findViewById(R.id.loadingbar).setVisibility(View.GONE);
				QuhaoHistoryStatesActivity.this.findViewById(R.id.serverdata).setVisibility(View.VISIBLE);
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			}
		}
	};

	@Override
	public void onClick(View v) {

		Log.e(TAG, "onclick before : " + isClick + " , clickNum : " + (++clickNum));
		if(isClick)
		{
			return;
		}
		
		isClick = true;
		
		Log.e(TAG, "onclick after : " + isClick);
		switch(v.getId())
		{
		case R.id.btn_delete:
			
			if(null!=reservationForPaiduiAdapter && "true".equals(reservationForPaiduiAdapter.isShowDelete))
			{
				deleteBtn.setText(R.string.delete);
				deleteLayout.setVisibility(View.GONE);
				reservationForPaiduiAdapter.isShowDelete = "false";
				
				final List<ReservationVO> rvosTemp = new ArrayList<ReservationVO>(reservations.size());
//				Collections.copy(rvosTemp, reservations);
//				System.arraycopy(reservations, 0, rvosTemp, 0, reservations.size());
				for (int i = 0; i < reservations.size(); i++) {
					rvosTemp.add(reservations.get(i));
				}
				List<String> rIds = new ArrayList<String>();
				
				Iterator<ReservationVO> iterator = rvosTemp.iterator();
				String ridStr = "";
				while (iterator.hasNext()) {
					ReservationVO temp = iterator.next();
					if ("true".equals(temp.isChecked)) {
						rIds.add(temp.rId);
						ridStr = ridStr + temp.rId + ",";
						iterator.remove();
					}
				}
				Log.e(TAG, ridStr);
				String url = "delHistoryReservation?id=" + ridStr;
				final DeleteReservationsInHistoryPaiduiTask task = new DeleteReservationsInHistoryPaiduiTask(R.string.waitting,this,url);
				task.execute(new Runnable(){

					@Override
					public void run() {
						
						reservations = rvosTemp;
						if(!reservations.isEmpty())
						{
							for (int i = 0; i < reservations.size(); i++) {
								reservations.get(i).isChecked = "false";
							}
						}
						reservationForPaiduiAdapter.rvos = reservations;
						reservationForPaiduiAdapter.notifyDataSetChanged();
						unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 10);
						Toast.makeText(QuhaoHistoryStatesActivity.this, R.string.delete_success, Toast.LENGTH_SHORT).show();
					}
					
				},new Runnable() {
					
					@Override
					public void run() {
						if(!reservations.isEmpty())
						{
							for (int i = 0; i < reservations.size(); i++) {
								reservations.get(i).isChecked = "false";
							}
						}
						reservationForPaiduiAdapter.rvos = reservations;
						reservationForPaiduiAdapter.notifyDataSetChanged();
						Toast.makeText(QuhaoHistoryStatesActivity.this, R.string.delete_failed, Toast.LENGTH_SHORT).show();
						unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 10);
					}
				});
				
				
//				reservationForPaiduiAdapter.notifyDataSetChanged();
			}
			else if(null!=reservationForPaiduiAdapter && "false".equals(reservationForPaiduiAdapter.isShowDelete))
			{
				deleteBtn.setText(R.string.confirm_delete);
				deleteLayout.setVisibility(View.VISIBLE);
				reservationForPaiduiAdapter.isShowDelete = "true";
				reservationForPaiduiAdapter.notifyDataSetChanged();
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 10);
			}
			
			break;
		case R.id.bt_selectall:
			if(null!=reservationForPaiduiAdapter && "true".equals(reservationForPaiduiAdapter.isShowDelete))
			{
				deleteLayout.setVisibility(View.VISIBLE);
				if(null == reservations)
				{
					reservations = new ArrayList<ReservationVO>();
				}
				
				if(!reservations.isEmpty())
				{
					for (int i = 0; i < reservations.size(); i++) {
						reservations.get(i).isChecked = "true";
					}
					reservationForPaiduiAdapter.rvos = reservations;
					reservationForPaiduiAdapter.notifyDataSetChanged();
				}
				
			}
			unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 10);
			break;
		case R.id.bt_deselectall:
			if(null!=reservationForPaiduiAdapter && "true".equals(reservationForPaiduiAdapter.isShowDelete))
			{
				deleteLayout.setVisibility(View.VISIBLE);
				if(null == reservations)
				{
					reservations = new ArrayList<ReservationVO>();
				}
				
				if(!reservations.isEmpty())
				{
					for (int i = 0; i < reservations.size(); i++) {
						if("true".equals(reservations.get(i).isChecked))
						{
							reservations.get(i).isChecked = "false";
						}
						else
						{
							reservations.get(i).isChecked = "true";
						}
					}
					reservationForPaiduiAdapter.rvos = reservations;
					reservationForPaiduiAdapter.notifyDataSetChanged();
				}
				
			}
			unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 10);
			break;
		case R.id.bt_cancel:
			if(null!=reservationForPaiduiAdapter && "true".equals(reservationForPaiduiAdapter.isShowDelete))
			{
				deleteBtn.setText(R.string.delete);
				deleteLayout.setVisibility(View.GONE);
				if(null == reservations)
				{
					reservations = new ArrayList<ReservationVO>();
				}
				
				reservationForPaiduiAdapter.isShowDelete = "false";
				if(!reservations.isEmpty())
				{
					for (int i = 0; i < reservations.size(); i++) {
						reservations.get(i).isChecked = "false";
					}
					reservationForPaiduiAdapter.rvos = reservations;
					reservationForPaiduiAdapter.notifyDataSetChanged();
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
		QuhaoHistoryStatesActivity.this.findViewById(R.id.loadingbar).setVisibility(View.VISIBLE);
		QuhaoHistoryStatesActivity.this.findViewById(R.id.serverdata).setVisibility(View.GONE);
		initData();
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		
		super.onDestroy();
	}

}
