package com.withiter.quhao.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.withiter.quhao.QHClientApplication;
import com.withiter.quhao.R;
import com.withiter.quhao.task.GetChatPortTask;
import com.withiter.quhao.task.JsonPack;
import com.withiter.quhao.util.StringUtils;
import com.withiter.quhao.util.tool.QuhaoConstant;
import com.withiter.quhao.util.tool.SharedprefUtil;
import com.withiter.quhao.view.OnPersonCountWheelListener;
import com.withiter.quhao.view.PersonCountWheel;

public class CreateAppointmentActivity extends QuhaoBaseActivity implements OnPersonCountWheelListener{

	private String merchantId;
	
	private LinearLayout personCountLayout;
	
	private TextView personCountText;
	
	private LinearLayout dateLayout;
	
	private TextView dateText;
	
	private ImageView privateRoomView;

	private EditText lastNameText;
	
	private ImageView lastNameFemale;
	private TextView lastNameFemaleText;
	private ImageView lastNameMale;
	private TextView lastNameMaleText;
	private EditText phoneText;
	
	private Button submit;
	
	private int personCount;
	
	private String date;
	
	private boolean isPrivateRoom;
	
	private String gender;
	
	private String phoneNumber;
	
	private PersonCountWheel personCountWheel;
	
	private PopupWindow popupWindow;
	
	private int displayWidth;
	private int displayHeight;
	
	private RelativeLayout personCountWheelLayout;
	
	private List<String> personCounts;
	
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
		lastNameFemaleText = (TextView) this.findViewById(R.id.last_name_female_text);
		lastNameFemaleText.setOnClickListener(this);
		lastNameMaleText = (TextView) this.findViewById(R.id.last_name_male_text);
		lastNameMaleText.setOnClickListener(this);
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

		personCounts = new ArrayList<String>();
		personCounts.add("1");
		personCounts.add("2");
		personCounts.add("3");
		personCounts.add("4");
		personCounts.add("5");
		personCounts.add("6");
		personCounts.add("7");
		personCounts.add("8");
		personCounts.add("9");
		personCounts.add("10");
		personCounts.add("11");
		personCounts.add("12");
		personCounts.add("13");
		personCounts.add("14");
		
		personCountWheel = new PersonCountWheel(this, personCounts, 5);
		personCountWheel.setOnPersonCountWheelListener(this);
		
		displayWidth = getWindowManager().getDefaultDisplay().getWidth();
		displayHeight = getWindowManager().getDefaultDisplay().getHeight();
		
		personCountWheelLayout = new RelativeLayout(this);
		int maxHeight = (int) (displayHeight * 0.5);
		RelativeLayout.LayoutParams viewLeftParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, maxHeight);
		viewLeftParams.leftMargin = 10;
		viewLeftParams.rightMargin = 10;
		personCountWheelLayout.addView(personCountWheel, viewLeftParams);
		if(personCountWheelLayout.getParent()!=null) {
			ViewGroup vg = (ViewGroup) personCountWheelLayout.getParent();
			vg.removeView(personCountWheelLayout);
		}
		personCountWheelLayout.setBackgroundColor(this.getResources().getColor(R.color.popup_main_background));
		personCountWheelLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				onPressBack();
			}
		});
		
		lastNameText.clearFocus();
        phoneText.clearFocus();
		
//		this.findViewById(R.id.loadingbar).setVisibility(View.VISIBLE);
//		this.findViewById(R.id.serverdata).setVisibility(View.GONE);
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
				
				if (popupWindow == null) {
					
					popupWindow = new PopupWindow(personCountWheelLayout, displayWidth, displayHeight);
					popupWindow.setAnimationStyle(R.style.PopupWindowAnimation);
					popupWindow.setFocusable(false);
					popupWindow.setOutsideTouchable(true);
				}
				
				if (!popupWindow.isShowing()) {
					popupWindow.showAsDropDown(personCountLayout);
//					popupWindow.showAtLocation(personCountLayout, Gravity.CENTER, 0, 0);
//					showPopup(selectPosition);
				} else {
//					popupWindow.setOnDismissListener(this);
					popupWindow.dismiss();
//					popupWindow.
//					hideView();
				}
				unlockHandler.sendEmptyMessage(UNLOCK_CLICK);
				break;
			case R.id.date_layout:
				unlockHandler.sendEmptyMessage(UNLOCK_CLICK);
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
				
				unlockHandler.sendEmptyMessage(UNLOCK_CLICK);
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
				unlockHandler.sendEmptyMessage(UNLOCK_CLICK);
				break;
			case R.id.last_name_female_text:
				
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
				unlockHandler.sendEmptyMessage(UNLOCK_CLICK);
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
				unlockHandler.sendEmptyMessage(UNLOCK_CLICK);
				break;
			case R.id.last_name_male_text:
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
				unlockHandler.sendEmptyMessage(UNLOCK_CLICK);
				break;
			case R.id.btn_submit:
				unlockHandler.sendEmptyMessage(UNLOCK_CLICK);
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
			unlockHandler.sendEmptyMessage(UNLOCK_CLICK);
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
		super.onDestroy();
	}

	@Override
	public void onPersonCountChanged(int oldValue, int newValue) {
		
		personCount = Integer.valueOf(personCounts.get(newValue));
	}

	@Override
	public void onPersonCountSubmitClick(View view, int selectedItem) {
		
		personCount = Integer.valueOf(personCounts.get(selectedItem));
		if (popupWindow != null && popupWindow.isShowing()) {
			popupWindow.dismiss();
		}
		personCountText.setText(personCount + "");
	}
	
	/**
	 * 如果菜单成展开状态，则让菜单收回去
	 */
	public boolean onPressBack() {
		if (popupWindow != null && popupWindow.isShowing()) {
			popupWindow.dismiss();
			return true;
		}
		else {
			return false;
		}

	}
	
	@Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
        	
            // 获得当前得到焦点的View，一般情况下就是EditText（特殊情况就是轨迹求或者实体案件会移动焦点）
            View v = getCurrentFocus();

            if (isShouldHideInput(v, ev)) {
                hideSoftInput(v.getWindowToken());
                lastNameText.clearFocus();
                phoneText.clearFocus();
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时没必要隐藏
     * 
     * @param v
     * @param event
     * @return
     */
    private boolean isShouldHideInput(View v, MotionEvent event) {
    	
        if (v != null && (v instanceof EditText)) {
            int[] l = { 0, 0 };
            v.getLocationInWindow(l);
            int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left
                    + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击EditText的事件，忽略它。
                return false;
            } else {
                return true;
            }
        }
        // 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditView上，和用户用轨迹球选择其他的焦点
        return false;
    }

    /**
     * 多种隐藏软件盘方法的其中一种
     * 
     * @param token
     */
    private void hideSoftInput(IBinder token) {
        if (token != null) {
            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(token,
                    InputMethodManager.HIDE_NOT_ALWAYS);
            
//            if(im.isActive()){
//            	im.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS);
//			}
        }
    }
    
}
