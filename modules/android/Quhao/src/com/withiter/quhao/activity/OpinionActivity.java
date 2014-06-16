package com.withiter.quhao.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.withiter.quhao.R;
import com.withiter.quhao.util.ActivityUtil;
import com.withiter.quhao.util.StringUtils;
import com.withiter.quhao.util.http.CommonHTTPRequest;
import com.withiter.quhao.util.tool.ProgressDialogUtil;

public class OpinionActivity extends QuhaoBaseActivity {

	private Button btnOpinion;
	private EditText opinionEdit;
	private EditText contactEdit;
	private String opinion;
	private String contact;

	public static boolean backClicked = false;

	@Override
	public void finish() {
		super.finish();
	}

	@Override
	protected void onResume() {
		backClicked = false;
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.more_opinion_layout);
		super.onCreate(savedInstanceState);

		btnOpinion = (Button) findViewById(R.id.btn_opinion);

		opinionEdit = (EditText) findViewById(R.id.opinion_edit);
		contactEdit = (EditText) findViewById(R.id.opinion_edit_contact);
		btnOpinion.setOnClickListener(this);
		btnBack.setOnClickListener(goBack(this,this.getClass().getName()));
	}

	@Override
	public void onClick(View v) {

		if (isClick) {
			return;
		}
		isClick = true;

		progressDialogUtil = new ProgressDialogUtil(OpinionActivity.this, R.string.empty, R.string.committing, false);
		progressDialogUtil.showProgress();
		switch (v.getId()) {
		case R.id.btn_opinion:
			unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			opinion = opinionEdit.getText().toString().trim();

			if(StringUtils.isNull(opinion))
			{
				Toast.makeText(this, "亲，写点东西吧。", Toast.LENGTH_SHORT).show();
				progressDialogUtil.closeProgress();
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				return;
			}
			
			if (opinion.length()>400) 
			{
				Toast.makeText(this, "亲，最多为200个汉字哦。", Toast.LENGTH_SHORT).show();
				progressDialogUtil.closeProgress();
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				return;
			}
			
			contact = contactEdit.getText().toString();

			Thread thread = new Thread(opinionRunnable);
			thread.start();

			break;
		}

	}

	private Runnable opinionRunnable = new Runnable() {

		@Override
		public void run() {
			try {
				Looper.prepare();
				opinion = opinion.trim();
				contact = contact.trim();
				if (!ActivityUtil.isNetWorkAvailable(getApplicationContext())) {
					Toast.makeText(getApplicationContext(), R.string.network_error_info, Toast.LENGTH_SHORT).show();
					unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
					return;
				}
				
				String buf = CommonHTTPRequest.get("createOpinion?opinion=" + opinion + "&contact=" + contact);
				if (StringUtils.isNull(buf) || "[]".equals(buf)) {
					Toast.makeText(OpinionActivity.this, "网络不好，请重新提交", Toast.LENGTH_SHORT).show();
					unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				} else if ("success".equals(buf)) {
					Toast.makeText(OpinionActivity.this, "提交成功，多谢您的意见", Toast.LENGTH_SHORT).show();
					unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
					OpinionActivity.this.finish();
				}

			} catch (Exception e) {

				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				Toast.makeText(OpinionActivity.this, "网络不好，请重新提交", Toast.LENGTH_SHORT).show();
			} finally {
				progressDialogUtil.closeProgress();
				Looper.loop();

			}
		}
	};

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return false;
	}

	@Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
        	
            // 获得当前得到焦点的View，一般情况下就是EditText（特殊情况就是轨迹求或者实体案件会移动焦点）
            View v = getCurrentFocus();

            if (isShouldHideInput(v, ev)) {
                hideSoftInput(v.getWindowToken());
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
