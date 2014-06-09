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
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.Toast;

import com.withiter.quhao.R;
import com.withiter.quhao.task.CreateCommentTask;
import com.withiter.quhao.util.ActivityUtil;
import com.withiter.quhao.util.StringUtils;
import com.withiter.quhao.util.http.CommonHTTPRequest;
import com.withiter.quhao.util.tool.ProgressDialogUtil;

public class CreateCommentActivity extends QuhaoBaseActivity implements OnRatingBarChangeListener{

	private Button submit;
	
	private RatingBar kouweiRatingBar;
	
	private RatingBar huanjingRatingBar;
	
	private RatingBar fuwuRatingBar;
	
	private RatingBar xingjiabiRatingBar;
	
	private RatingBar gradeRatingbar;
	
	private EditText commentEdit;
	
	private EditText avgCostEdit;
	
	private String rId;
	
	private int kouwei;
	
	private int huanjing;
	
	private int fuwu;
	
	private int xingjiabi;
	
	private int grade;
	
	private String comment;
	
	private String averageCost;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.comment_create_layout);
		super.onCreate(savedInstanceState);
		btnBack.setOnClickListener(goBack(this));
		rId = getIntent().getStringExtra("rId");
		submit = (Button) this.findViewById(R.id.submit);
		kouweiRatingBar = (RatingBar) this.findViewById(R.id.kouwei_ratingbar);
		fuwuRatingBar = (RatingBar) this.findViewById(R.id.fuwu_ratingbar);
		huanjingRatingBar = (RatingBar) this.findViewById(R.id.huanjing_ratingbar);
		xingjiabiRatingBar = (RatingBar) this.findViewById(R.id.xingjiabi_ratingbar);
		kouweiRatingBar.setOnRatingBarChangeListener(this);
		fuwuRatingBar.setOnRatingBarChangeListener(this);
		huanjingRatingBar.setOnRatingBarChangeListener(this);
		xingjiabiRatingBar.setOnRatingBarChangeListener(this);
		gradeRatingbar = (RatingBar) this.findViewById(R.id.grade_ratingbar);
		gradeRatingbar.setOnRatingBarChangeListener(this);
		avgCostEdit = (EditText) this.findViewById(R.id.avg_cost_edit);
		commentEdit = (EditText) this.findViewById(R.id.comment_edit);
		
		submit.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {

		if (isClick) {
			return;
		}
		isClick = true;

		switch (v.getId()) {
		case R.id.submit:
			averageCost = avgCostEdit.getText().toString().trim();
			comment = commentEdit.getText().toString().trim();
			kouwei = (int) kouweiRatingBar.getRating();
			huanjing = (int) huanjingRatingBar.getRating();
			fuwu = (int) fuwuRatingBar.getRating();
			xingjiabi = (int) xingjiabiRatingBar.getRating();
			grade = (int) gradeRatingbar.getRating();
//			float gradeAvg = (kouwei + huanjing + fuwu + xingjiabi)/4;
//			int grade = Math.round(gradeAvg);
			if(StringUtils.isNull(comment))
			{
				Toast.makeText(CreateCommentActivity.this, "请填写评论", Toast.LENGTH_SHORT).show();
				unlockHandler.sendEmptyMessage(UNLOCK_CLICK);
				return;
			}
		
			if (!ActivityUtil.isNetWorkAvailable(CreateCommentActivity.this)) {
				Toast.makeText(CreateCommentActivity.this, R.string.network_error_info, Toast.LENGTH_SHORT).show();
				unlockHandler.sendEmptyMessage(UNLOCK_CLICK);
				return;
			}
			CreateCommentTask task = new CreateCommentTask(R.string.waitting, this, "updateComment?rid=" + rId + "&kouwei=" + kouwei + "&huanjing=" + huanjing + "&fuwu=" + fuwu + "&xingjiabi=" + xingjiabi + "&grade=" + grade + "&cost=" + averageCost +  "&content=" + comment);
			task.execute(new Runnable() {
				
				@Override
				public void run() {
					
					unlockHandler.sendEmptyMessage(UNLOCK_CLICK);
					Toast.makeText(CreateCommentActivity.this, "评论成功", Toast.LENGTH_SHORT).show();
					CreateCommentActivity.this.finish();
				}
			},new Runnable() {
				
				@Override
				public void run() {
					unlockHandler.sendEmptyMessage(UNLOCK_CLICK);
					Toast.makeText(CreateCommentActivity.this, "亲，评论失败，请重新提交。", Toast.LENGTH_SHORT).show();
					return;
//					CreateCommentActivity.this.finish();
					
				}
			});
			
			break;
		}

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

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return false;
	}

	@Override
	public void onRatingChanged(RatingBar ratingBar, float rating,
			boolean fromUser) {
		
		if (isClick) {
			return;
		}
		
		isClick = true;
		
		switch(ratingBar.getId())
		{
			case R.id.kouwei_ratingbar:
				kouweiRatingBar.setRating(rating);
				unlockHandler.sendEmptyMessage(UNLOCK_CLICK);
			break;
			case R.id.fuwu_ratingbar:
				fuwuRatingBar.setRating(rating);
				unlockHandler.sendEmptyMessage(UNLOCK_CLICK);
			break;
			case R.id.huanjing_ratingbar:
				huanjingRatingBar.setRating(rating);
				unlockHandler.sendEmptyMessage(UNLOCK_CLICK);
			break;
			case R.id.xingjiabi_ratingbar:
				xingjiabiRatingBar.setRating(rating);
				unlockHandler.sendEmptyMessage(UNLOCK_CLICK);
			break;
			case R.id.grade_ratingbar:
				gradeRatingbar.setRating(rating);
				unlockHandler.sendEmptyMessage(UNLOCK_CLICK);
			break;
			default:
				unlockHandler.sendEmptyMessage(UNLOCK_CLICK);
			break;
		}
	}

}
