package com.withiter.quhao.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.withiter.quhao.R;
import com.withiter.quhao.util.tool.ProgressDialogUtil;

public class ShareDialogActivity extends AppStoreActivity implements OnItemClickListener{

	private GridView shareGridView;
	
	private Button btnCopy;
	
	private Button btnCancel;
	
	private ArrayList<Map<String, Object>> mDatas = new ArrayList<Map<String, Object>>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.more_share_layout);
		super.onCreate(savedInstanceState);
		
		shareGridView = (GridView) findViewById(R.id.share_icon);
		btnCopy = (Button) findViewById(R.id.share_btn_copy);
		btnCancel = (Button) findViewById(R.id.share_btn_cancel);
		
		initView();
	}

	private void initView() {
		
		btnCopy.setOnClickListener(this);
		btnCancel.setOnClickListener(this);
		shareGridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
		
		// 图片资源
		int[] imageId = new int[] { R.drawable.logo_sinaweibo,
				R.drawable.logo_qzone, R.drawable.logo_weixin,
				R.drawable.logo_weixin_timeline, R.drawable.logo_email,
				R.drawable.logo_sms };
		// 从XML资源站红获取字符串数组
		String[] names = getResources().getStringArray(R.array.share_title);

		for (int i = 0; i < 6; i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("PIC", imageId[i]);
			map.put("TITLE", names[i]);
			mDatas.add(map);
		}
		SimpleAdapter adapter = new SimpleAdapter(this, mDatas,
				R.layout.share_grid_item, new String[] { "PIC", "TITLE" },
				new int[] { R.id.griditem_pic, R.id.griditem_title, });

		shareGridView.setAdapter(adapter);
		shareGridView.setOnItemClickListener(this);
	}

	private void sendSina() {
		
		isClick = false;
		progressDialogUtil.closeProgress();
		Bundle bundle = new Bundle();
		bundle.putString("SHARE_INFO", "(www.withiter.com \n 分享自@王介泽)");
		
		Intent intent = new Intent(this, SinaInfoActivity.class);
		if (bundle != null) {
			intent.putExtras(bundle);
		}

		startActivity(intent);
		// 仿Iphone切换效果
		overridePendingTransition(R.anim.zoomin, R.anim.zoomout);
		ShareDialogActivity.this.finish();
	}
	
	@Override
	public void onClick(View v) {

		if(isClick)
		{
			return;
		}
		isClick = true;
		progressDialogUtil = new ProgressDialogUtil(this, R.string.empty,
				R.string.waitting, false);
		
		progressDialogUtil.showProgress();
		switch(v.getId())
		{
			case R.id.share_btn_copy:
				progressDialogUtil.closeProgress();
				isClick = false;
				ClipboardManager clip = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
				clip.setText("www.withiter.com"); // 复制
				Toast.makeText(this, "已将链接复制到剪贴板", Toast.LENGTH_SHORT).show();
				ShareDialogActivity.this.finish();
				break;
			case R.id.share_btn_cancel:
				progressDialogUtil.closeProgress();
				isClick = false;
				ShareDialogActivity.this.finish();
				break;
			default:
				break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		
		if(isClick)
		{
			return;
		}
		isClick = true;
		progressDialogUtil = new ProgressDialogUtil(this, R.string.empty,
				R.string.waitting, false);
		
		progressDialogUtil.showProgress();
		
		switch (position) {
		case 0:
			// 新浪微博
			sendSina();
			break;
		case 1:
			// QQ空间
			// finish();
			break;
		case 2:
			// 微信
			// finish();
			break;
		case 3:
			// 朋友圈
			// finish();
			break;
		case 4:
			// 邮件
			// finish();
			break;
		case 5:
			// 短信
			// finish();
			break;

		}
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		
		return false;
	}

}
