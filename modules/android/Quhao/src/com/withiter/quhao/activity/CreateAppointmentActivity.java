package com.withiter.quhao.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.sharesdk.framework.ShareSDK;

import com.withiter.quhao.QHClientApplication;
import com.withiter.quhao.R;
import com.withiter.quhao.task.GetChatPortTask;
import com.withiter.quhao.task.JsonPack;
import com.withiter.quhao.util.StringUtils;
import com.withiter.quhao.util.tool.QuhaoConstant;
import com.withiter.quhao.util.tool.SharedprefUtil;

public class CreateAppointmentActivity extends QuhaoBaseActivity {

	private String merchantId;
	
	private LinearLayout personCountLayout;
	
	private TextView personCountText;
	
	private LinearLayout dateLayout;
	
	private TextView dateText;
	
	private ImageView privateRoomView;

	private EditText lastNameText;
	
	private ImageView lastNameFemale;
	
	private ImageView lastNameMale;
	
	private EditText phoneText;
	
	private Button submit;
	
	private int personCount;
	
	private String date;
	
	private boolean isPrivateRoom;
	
	private String gender;
	
	private String phoneNumber;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.create_appointment_layout);
		super.onCreate(savedInstanceState);

		this.merchantId = getIntent().getStringExtra("merchantId");
		
		btnBack.setOnClickListener(goBack(this));

		personCountLayout = (LinearLayout) this.findViewById(R.id.person_count_layout);
		personCountLayout.setOnClickListener(this);
		personCountText = (TextView) this.findViewById(R.id.person_count_text);
		
		dateLayout = (LinearLayout) this.findViewById(R.id.date_layout);
		dateLayout.setOnClickListener(this);
		dateText = (TextView) this.findViewById(R.id.date_text);
		
		privateRoomView = (ImageView) this.findViewById(R.id.private_room_view);
		privateRoomView.setOnClickListener(this);
		
		lastNameText = (EditText) this.findViewById(R.id.last_name_text);
		
		lastNameFemale = (ImageView) this.findViewById(R.id.last_name_female);
		lastNameFemale.setOnClickListener(this);
		lastNameMale = (ImageView) this.findViewById(R.id.last_name_male);
		lastNameMale.setOnClickListener(this);
		
		phoneText = (EditText) this.findViewById(R.id.phone_text);
		
		submit = (Button) this.findViewById(R.id.btn_submit);
		submit.setOnClickListener(this);
		
		personCount = 0;
		
		isPrivateRoom = false;
		if (isPrivateRoom) 
		{
			privateRoomView.setImageResource(R.drawable.ic_switch_on);
		}
		else
		{
			privateRoomView.setImageResource(R.drawable.ic_switch_off);
		}
		
		gender = "female";
		if ("female".equals(gender)) 
		{
			lastNameFemale.setImageResource(R.drawable.ic_check_red_on);
			lastNameMale.setImageResource(R.drawable.ic_check_red_off);
		}
		else if("male".equals(gender))
		{
			lastNameFemale.setImageResource(R.drawable.ic_check_red_off);
			lastNameMale.setImageResource(R.drawable.ic_check_red_on);
		}
		
		phoneNumber = SharedprefUtil.get(this, QuhaoConstant.PHONE, "");
		phoneText.setText(phoneNumber);
		
		this.findViewById(R.id.loadingbar).setVisibility(View.VISIBLE);
		this.findViewById(R.id.serverdata).setVisibility(View.GONE);
	}
	
	@Override
	public void onClick(View v) {
		
		if(isClick)
		{
			return;
		}
		isClick = true;
		switch (v.getId()) {
			case R.id.person_count_layout:
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				
				
				break;
			case R.id.date_layout:
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				break;
			case R.id.private_room_view:
				
				if (isPrivateRoom) 
				{
					isPrivateRoom = false;
					privateRoomView.setImageResource(R.drawable.ic_switch_off);
				}
				else
				{
					isPrivateRoom = true;
					privateRoomView.setImageResource(R.drawable.ic_switch_on);
				}
				
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				break;
			case R.id.last_name_female:
				
				gender = "female";
				if ("female".equals(gender)) 
				{
					lastNameFemale.setImageResource(R.drawable.ic_check_red_on);
					lastNameMale.setImageResource(R.drawable.ic_check_red_off);
				}
				else if("male".equals(gender))
				{
					lastNameFemale.setImageResource(R.drawable.ic_check_red_off);
					lastNameMale.setImageResource(R.drawable.ic_check_red_on);
				}
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				break;
			case R.id.last_name_male:
				gender = "male";
				if ("female".equals(gender)) 
				{
					lastNameFemale.setImageResource(R.drawable.ic_check_red_on);
					lastNameMale.setImageResource(R.drawable.ic_check_red_off);
				}
				else if("male".equals(gender))
				{
					lastNameFemale.setImageResource(R.drawable.ic_check_red_off);
					lastNameMale.setImageResource(R.drawable.ic_check_red_on);
				}
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				break;
			case R.id.btn_submit:
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				break;
			case R.id.btn_chat:
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				if (QHClientApplication.getInstance().isLogined) {
					
					final GetChatPortTask task = new GetChatPortTask(R.string.waitting, this, "chat?mid=" );
					task.execute(new Runnable() {
						
						@Override
						public void run() {
							JsonPack jsonPack = task.jsonPack;
							String port = jsonPack.getObj();
							if ("false".equals(port)) {
								Toast.makeText(CreateAppointmentActivity.this, "亲，房间人数已满，请稍后再来。", Toast.LENGTH_SHORT).show();
								return;
							}
							Intent intentChat = new Intent();
							//uid=uid1&image=image1&mid=mid1&user=11
							String image = QHClientApplication.getInstance().accountInfo.userImage;
							if(StringUtils.isNotNull(image) && image.contains(QuhaoConstant.HTTP_URL))
							{
								image = "/" + image.substring(QuhaoConstant.HTTP_URL.length());
							}
							if (QHClientApplication.getInstance().accountInfo == null) {
								Toast.makeText(CreateAppointmentActivity.this, "亲，账号登录过期了哦", Toast.LENGTH_SHORT).show();
								return;
							}
							intentChat.putExtra("uid", QHClientApplication.getInstance().accountInfo.accountId);
							intentChat.putExtra("image", image);
							intentChat.putExtra("user", QHClientApplication.getInstance().accountInfo.phone);
							intentChat.putExtra("port", port);
							intentChat.setClass(CreateAppointmentActivity.this, MerchantChatActivity.class);
							startActivity(intentChat);
						}
					},new Runnable() {
						
						@Override
						public void run() {
							Toast.makeText(CreateAppointmentActivity.this, "亲，房间人数已满，请稍后再来。", Toast.LENGTH_SHORT).show();
							return;
						}
					});
		
				} else {
					Intent intentChat = new Intent(CreateAppointmentActivity.this, LoginActivity.class);
					intentChat.putExtra("activityName", CreateAppointmentActivity.class.getName());
					intentChat.putExtra("merchantId", CreateAppointmentActivity.this.merchantId);
					intentChat.putExtra("notGetNumber", "true");
					intentChat.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intentChat);
				}
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
	public void finish() {
		if(null != progressDialogUtil)
		{
			progressDialogUtil.closeProgress();
		}
		super.finish();
	}

	@Override
	protected void onResume() {
		
		super.onResume();
	}

	@Override
	public void onPause() {
		if(null != progressDialogUtil)
		{
			progressDialogUtil.closeProgress();
		}
		super.onPause();
	}
	
	@Override
	public void onStop() {
		
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		ShareSDK.stopSDK(this);
		super.onDestroy();
	}
	
}
