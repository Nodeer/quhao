package com.withiter.quhao.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.withiter.quhao.R;
import com.withiter.quhao.adapter.PaiduiAdapter;
import com.withiter.quhao.adapter.ViewHolderGetNoPaidui;
import com.withiter.quhao.task.GetHaomaTask;
import com.withiter.quhao.task.JsonPack;
import com.withiter.quhao.task.NahaoTask;
import com.withiter.quhao.util.ActivityUtil;
import com.withiter.quhao.util.tool.ParseJson;
import com.withiter.quhao.util.tool.QuhaoConstant;
import com.withiter.quhao.util.tool.SharedprefUtil;
import com.withiter.quhao.vo.Haoma;
import com.withiter.quhao.vo.Paidui;
import com.withiter.quhao.vo.ReservationVO;

/**
 * 取号activity
 * 
 * @author Wang Jie Ze
 */
public class GetNumber2Activity extends QuhaoBaseActivity implements OnItemClickListener{

	/**
	 * 传递过来的merchant ID
	 */
	private String merchantId;

	private Button btnGetNo;
	private LinearLayout btnGetNumberLayout;
	private Haoma haoma;
	private ReservationVO reservation;
	
	private ListView haomaListView;
	
	private PaiduiAdapter paiduiAdapter;
	
	private Paidui selectedPaidui;
	
	private boolean canClickItem;

	private TextView seatNoView;
	
	private TextView myNoView;
	
	private TextView beforeYouView;
	
	private TextView nextNoView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.get_number2);
		super.onCreate(savedInstanceState);

		merchantId = getIntent().getStringExtra("merchantId");

		// 设置回退
		btnBack.setOnClickListener(goBack(this, this.getClass().getName()));

		btnGetNumberLayout = (LinearLayout) findViewById(R.id.btn_GetNumberLayout);
		btnGetNo = (Button) findViewById(R.id.btn_GetNumber);
		btnGetNo.setOnClickListener(this);
		haomaListView = (ListView) this.findViewById(R.id.haoma_list_view);
		haomaListView.setOnItemClickListener(this);
		canClickItem = true;
		
		seatNoView = (TextView) this.findViewById(R.id.rvo_seat_no);
		myNoView = (TextView) this.findViewById(R.id.rvo_my_number);
		beforeYouView = (TextView) this.findViewById(R.id.rvo_before_you);
		nextNoView = (TextView) this.findViewById(R.id.rvo_next_number);
		
		if (!ActivityUtil.isNetWorkAvailable(getApplicationContext())) {
			Toast.makeText(getApplicationContext(), R.string.network_error_info, Toast.LENGTH_SHORT).show();
			unlockHandler.sendEmptyMessage(UNLOCK_CLICK);
			return;
		}
		
		getSeatNos();
	}

	/**
	 * 根据merchant显示在界面上的handler
	 */
	private Handler getNoUpdateHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 200) {
				super.handleMessage(msg);
//				canClickItem = false;
				haomaListView.setEnabled(false);
				btnGetNumberLayout.setVisibility(View.GONE);
				findViewById(R.id.btn_GetNumberLayout).setVisibility(View.GONE);
				findViewById(R.id.my_reservation_layout).setVisibility(View.VISIBLE);
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				
				seatNoView.setText(reservation.seatNumber);
				myNoView.setText(reservation.myNumber);
				beforeYouView.setText(reservation.beforeYou);
				nextNoView.setText(reservation.currentNumber);
				
				Builder dialog = new AlertDialog.Builder(GetNumber2Activity.this);
				
				if(Integer.parseInt(reservation.beforeYou)<5)
				{
					dialog.setTitle("温馨提示").setMessage(R.string.nahao_success_tip_5_less).setPositiveButton("确定", null);
				}
				else
				{
					dialog.setTitle("温馨提示").setMessage(R.string.nahao_success_tip_5_more).setPositiveButton("确定", null);
				}
				dialog.show();

			}
		}
	};

	/**
	 * 
	 * get seat numbers by merchant ID from server
	 * 
	 */
	private void getSeatNos() {
		
		if(null != merchantId)
		{
			final GetHaomaTask task = new GetHaomaTask(R.string.waitting, this, "quhao?id=" + merchantId);
			task.execute(new Runnable() {
				
				@Override
				public void run() {
					JsonPack jsonPack = task.jsonPack;
					
					haoma = ParseJson.getHaoma(jsonPack.getObj());
					
					haomaUpdateHandler.obtainMessage(200, haoma).sendToTarget();
					
				}
			}, new Runnable() {
				
				@Override
				public void run() {
					haoma = new Haoma();
					List<Paidui> list = new ArrayList<Paidui>();
					haoma.paiduiList = list;
					haomaUpdateHandler.obtainMessage(200, haoma).sendToTarget();
				}
			});
		}
		
	}

	private Handler haomaUpdateHandler= new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 200) {
				super.handleMessage(msg);

				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				
				if (null != haoma && haoma.paiduiList != null && !haoma.paiduiList.isEmpty())
				{
					if (paiduiAdapter == null) {
						paiduiAdapter = new PaiduiAdapter(GetNumber2Activity.this, haomaListView, haoma.paiduiList);
						haomaListView.setAdapter(paiduiAdapter);
					}
					else
					{
						paiduiAdapter.paiduis = haoma.paiduiList;
					}
					paiduiAdapter.notifyDataSetChanged();
				}
				else
				{
					if (null != paiduiAdapter) {
						paiduiAdapter.notifyDataSetChanged();
					}
					Toast.makeText(GetNumber2Activity.this, "暂时没有可用的桌位", Toast.LENGTH_SHORT).show();
				}

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
		case R.id.btn_GetNumber:
			unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			String accountId = SharedprefUtil.get(this, QuhaoConstant.ACCOUNT_ID, "");
			if (!ActivityUtil.isNetWorkAvailable(getApplicationContext())) {
				Toast.makeText(getApplicationContext(), R.string.network_error_info, Toast.LENGTH_SHORT).show();
				unlockHandler.sendEmptyMessage(UNLOCK_CLICK);
				return;
			}
			
			if (selectedPaidui == null) {
				Toast.makeText(getApplicationContext(), "亲，要选择号码哦！", Toast.LENGTH_SHORT).show();
				unlockHandler.sendEmptyMessage(UNLOCK_CLICK);
				return;
			}
			
			String url = "nahao?accountId=" + accountId + "&mid=" + merchantId + "&seatNumber=" + selectedPaidui.seatNo;
			// + GetNumberActivity.this.merchantId);
			final NahaoTask task = new NahaoTask(R.string.waitting, this, url);
			task.execute(new Runnable() {
				
				@Override
				public void run() {
					JsonPack jsonPack = task.jsonPack;
					reservation = ParseJson.getReservation(jsonPack.getObj());
					if ("NO_MORE_JIFEN".equalsIgnoreCase(reservation.tipValue)) {
						Toast.makeText(GetNumber2Activity.this, "您没有更多的积分了..", Toast.LENGTH_SHORT).show();
						GetNumber2Activity.this.finish();
						return;
					}
					else
					{
						getNoUpdateHandler.obtainMessage(200, reservation).sendToTarget();
					}
				}
			}, new Runnable() {
				
				@Override
				public void run() {

					Toast.makeText(GetNumber2Activity.this, "当前网络异常，请重新拿号。", Toast.LENGTH_SHORT).show();
					unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
					
				}
			});
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
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		
		if (!canClickItem) {
			return;
		}
		// 取得ViewHolder对象，这样就省去了通过层层的findViewById去实例化我们需要的cb实例的步骤
		ViewHolderGetNoPaidui holder = (ViewHolderGetNoPaidui) view.getTag();
		
		// 改变CheckBox的状态
		holder.cb.toggle();
		// 将CheckBox的选中状况记录下来
		// 调整选定条目
		if (holder.cb.isChecked() == true) {
			haoma.paiduiList.get(position).isChecked = true;
//				checkNum++;
		} else {
			haoma.paiduiList.get(position).isChecked = true;
//				checkNum--;
		}
		
		for (int i = 0; i < haoma.paiduiList.size(); i++) {
			if (i == position) {
				selectedPaidui = haoma.paiduiList.get(i);
				continue;
			}
			haoma.paiduiList.get(i).isChecked = false;
		}
		
		haomaUpdateHandler.obtainMessage(200, haoma.paiduiList).sendToTarget();
	}
}