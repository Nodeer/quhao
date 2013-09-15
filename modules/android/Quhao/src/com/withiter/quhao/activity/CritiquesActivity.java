package com.withiter.quhao.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.ClientProtocolException;

import com.withiter.quhao.QHClientApplication;
import com.withiter.quhao.R;
import com.withiter.quhao.adapter.CritiqueAdapter;
import com.withiter.quhao.domain.AccountInfo;
import com.withiter.quhao.util.QuhaoLog;
import com.withiter.quhao.util.StringUtils;
import com.withiter.quhao.util.http.CommonHTTPRequest;
import com.withiter.quhao.util.tool.ParseJson;
import com.withiter.quhao.util.tool.ProgressDialogUtil;
import com.withiter.quhao.vo.Critique;
import com.withiter.quhao.vo.LoginInfo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class CritiquesActivity extends AppStoreActivity {

	private static final String TAG = CritiquesActivity.class.getName();
	
	private String merchantName;
	
	private String merchantId;
	
	private TextView merchantNameView;
	/**
	 * the critiques queried from merchant
	 */
	private List<Critique> critiques;
	
	/**
	 * back button
	 */
	private Button btnBack;
	
	/**
	 * list view for critiques
	 */
	private ListView critiquesView;
	
	/**
	 * critique adapter
	 */
	private CritiqueAdapter critiqueAdapter;
	
	/**
	 * when the page is first loaded, the critiques will be initialize , the value isFirstLoad will be true
	 * when the page is not first loaded, the critiques list have been there, we just add list into the adapter.
	 */
	private boolean isFirstLoad = true;

	protected Handler updateCritiquesHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			
			super.handleMessage(msg);
			
			if(msg.what == 200){
				
				if(isFirstLoad){
					
					findViewById(R.id.loadingbar).setVisibility(View.GONE);
					findViewById(R.id.critiquesLayout).setVisibility(View.VISIBLE);
					critiqueAdapter = new CritiqueAdapter(critiques);
					critiquesView.setAdapter(critiqueAdapter);
					isFirstLoad = false;
				}else{
					
					critiqueAdapter.critiques = critiques;
				}
				critiqueAdapter.notifyDataSetChanged();
				
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			}
		}
		
	};
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.critiques);
		
		this.merchantName = getIntent().getStringExtra("merchantName");
		
		this.merchantId = getIntent().getStringExtra("merchantId");
		
		merchantNameView = (TextView) findViewById(R.id.merchantName);
		merchantNameView.setText(merchantName);
		
		critiquesView = (ListView) findViewById(R.id.critiquesView);
		
		btnBack = (Button) findViewById(R.id.back_btn);
		btnBack.setOnClickListener(this);
		getCritiques();
		
	}

	/**
	 * 
	 * query critiques from web service via merchant ID
	 */
	private void getCritiques() {
		
		Thread getCritiquesRunnable = new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				try {
					QuhaoLog.v(TAG, "query critiques from web service, the merchant id is : " + merchantId);
					String buf = CommonHTTPRequest.get("getReservations?accountId=51e563feae4d165869fda38c&mid=51efe7d8ae4dca7b4c281754");
					
					if(StringUtils.isNull(buf)){
						unlockHandler.sendEmptyMessageAtTime(UNLOCK_CLICK, 1000);
					}else{
						//critiques = ParseJson.getCritiques(buf);
						critiques = new ArrayList<Critique>();
						Critique critique1 = new Critique("111", "nick11", 1, 1, 10.25, "很像日本的居酒屋。服务态度超赞，点餐的时候都“半蹲”着，上菜的时候“会提醒你”趁热吃或小心烫。菜都“很精致”，不过量“很小”，种类也“不是很多”。环境挺好，座位空间比较大，也“不是那么嘈杂”，“两三个人小聚、随便聊聊，挺合适的”。", "12-02-27");
						Critique critique2 = new Critique("111", "nick22", 2, 2, 101.25, "比我想象中便宜一点。。。牛肉火锅很好吃~不过不管哪家店的这种豆腐肥牛锅我都很喜欢~一口牛肉也是我觉得最好吃的~还没撒胡椒粉什么的就已经觉得味道满进去了~而且肉不老不塞牙~三文鱼刺身没什么大感觉。。。倒是芥末酱给的好少。。而且感觉干掉了芝士焗年糕。。。筷子弄起来困难。。。而且其实并没什么好吃的~", "12-02-27");
						critiques.add(critique1);
						critiques.add(critique2);
						updateCritiquesHandler .obtainMessage(200, critiques).sendToTarget();
					}
				} catch (Exception e) {
					unlockHandler.sendEmptyMessageAtTime(UNLOCK_CLICK, 1000);
					Toast.makeText(CritiquesActivity.this, R.string.network_error_info, Toast.LENGTH_SHORT).show();
					QuhaoLog.e(TAG, "Error for querying critiques from web service, the error is : " + e.getMessage());
				}finally{
					unlockHandler.sendEmptyMessageAtTime(UNLOCK_CLICK, 1000);
				}
				
				
			}
		});
		getCritiquesRunnable.start();
	}

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
		case R.id.back_btn:
			onBackPressed();
			break;
		default:
			break;
		}

	}

	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {
		return false;
	}

}
