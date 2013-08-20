package com.withiter.quhao.activity;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.withiter.quhao.R;
import com.withiter.quhao.util.tool.ParseJson;
import com.withiter.quhao.util.tool.ProgressDialogUtil;
import com.withiter.quhao.util.tool.QuhaoConstant;
import com.withiter.quhao.vo.Merchant;

public class MerchantDetailActivity extends AppStoreActivity {
	private String LOGTAG = MerchantDetailActivity.class.getName();
	private String merchantId;
	private boolean isClick;
	private final int UNLOCK_CLICK = 1000;
	private ProgressDialogUtil progress;
	private Merchant merchant;
	
	private Button btnGetNumber;
	
	private  LinearLayout info;
	
	private TextView merchantName;
	private ImageView merchantImg;
	private TextView merchantAddress;
	private TextView merchantPhone;
	private TextView merchantTags;
	private TextView merchantAverageCost;
	private TextView xingjiabi;
	private TextView kouwei;
	private TextView huanjing;
	private TextView fuwu;

	/**
	 * handler处理 解锁的时候可能会关闭其他的等待提示框
	 */
	private Handler unlockHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == UNLOCK_CLICK) {
				// 解锁
				isClick = false;
			}
		}
	};

	private Handler merchantUpdateHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 200) {
				super.handleMessage(msg);

				info.findViewById(R.id.loadingbar).setVisibility(View.GONE);
				info.findViewById(R.id.serverdata).setVisibility(View.VISIBLE);

				if (null != MerchantDetailActivity.this.merchant) {
					MerchantDetailActivity.this.merchantName
							.setText(MerchantDetailActivity.this.merchant.name);
					// MerchantDetailActivity.this.merchantImg = (ImageView)
					// info.findViewById(R.id.merchantImg);
					MerchantDetailActivity.this.merchantAddress
							.setText(MerchantDetailActivity.this.merchant.address);
					MerchantDetailActivity.this.merchantPhone
							.setText(MerchantDetailActivity.this.merchant.phone);
					MerchantDetailActivity.this.merchantTags
							.setText(MerchantDetailActivity.this.merchant.tags);
					MerchantDetailActivity.this.merchantAverageCost
							.setText(MerchantDetailActivity.this.merchant.averageCost);
					MerchantDetailActivity.this.xingjiabi
							.setText(String
									.valueOf(MerchantDetailActivity.this.merchant.xingjiabi));
					MerchantDetailActivity.this.kouwei
							.setText(String
									.valueOf(MerchantDetailActivity.this.merchant.kouwei));
					MerchantDetailActivity.this.fuwu
							.setText(String
									.valueOf(MerchantDetailActivity.this.merchant.fuwu));
					MerchantDetailActivity.this.huanjing
							.setText(String
									.valueOf(MerchantDetailActivity.this.merchant.huanjing));
				}
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			}

		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.merchant_detail);
		super.onCreate(savedInstanceState);

		
		this.merchantId = getIntent().getStringExtra("merchantId");
		
		btnGetNumber = (Button) findViewById(R.id.btn_GetNumber);
		
		btnGetNumber.setOnClickListener(getNumberClickListener());
		
		LayoutInflater inflater = LayoutInflater.from(this);
        info = (LinearLayout)inflater.inflate(R.layout.poiinfo, null);
        LinearLayout scroll = (LinearLayout)findViewById(R.id.lite_list);
        
        LayoutParams layoutParams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
        
        scroll.addView(info, layoutParams);
    	
    	this.merchantName = (TextView) findViewById(R.id.merchantName);
    	this.merchantImg = (ImageView) info.findViewById(R.id.merchantImg);
    	this.merchantAddress = (TextView) info.findViewById(R.id.merchantAddress);
    	this.merchantPhone = (TextView) info.findViewById(R.id.merchantPhone);
    	this.merchantTags = (TextView) info.findViewById(R.id.merchantTags);
    	this.merchantAverageCost = (TextView) info.findViewById(R.id.merchantAverageCost);
    	this.xingjiabi = (TextView) info.findViewById(R.id.xingjiabi);
    	this.kouwei = (TextView) info.findViewById(R.id.kouwei);
    	this.fuwu = (TextView) info.findViewById(R.id.fuwu);
    	this.huanjing = (TextView) info.findViewById(R.id.huanjing);

		btnPerson.setOnClickListener(goPersonCenterListener(this));
		btnMarchent.setOnClickListener(getMarchentListListener(this));

		initView();
	}


	/**
	 * 
	 * 取号按钮的 click listener
	 * 
	 * @return 取号按钮的listener R.id.btn_GetNumber
	 */
	private OnClickListener getNumberClickListener()
	{
		OnClickListener listener = new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent();
				intent.putExtra("merchantId", MerchantDetailActivity.this.merchantId);
				intent.setClass(MerchantDetailActivity.this, GetNumberActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.main_enter, R.anim.main_exit);
			}
		};
		return listener;
	}

	private void initView()
	{
		if(isClick)
		{
			return;
		}
		isClick = true;

		progress = new ProgressDialogUtil(this, R.string.empty,
				R.string.querying, false);
		progress.showProgress();
		Thread merchantsThread = new Thread(merchantDetailRunnable);
		merchantsThread.start();

	}

	private Runnable merchantDetailRunnable = new Runnable() {
		@Override
		public void run() {
			try {
				Log.v(LOGTAG, "get categorys data form server begin");
				HttpGet request = new HttpGet(QuhaoConstant.HTTP_URL
						+ "merchant?id="
						+ MerchantDetailActivity.this.merchantId);
				HttpClient httpClient = new DefaultHttpClient();
				HttpResponse response = httpClient.execute(request);
				Log.v(LOGTAG, "get top merchant data form server : "
						+ response.getStatusLine().getStatusCode());
				if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					String buf = EntityUtils.toString(response.getEntity());
					Log.v(LOGTAG, "get top merchant data form server buf : "
							+ buf);
					// 返回HTML页面
					if (buf.indexOf("<html>") != -1
							|| buf.indexOf("<HTML>") != -1) {
						// mGetHandler.sendMessage(mGetHandler
						// .obtainMessage(-2));
						throw new Exception("session timeout!");
					}

					merchant = ParseJson.getMerchant(buf);

					merchantUpdateHandler.obtainMessage(200, merchant)
							.sendToTarget();

				}

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				progress.closeProgress();
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
