package com.withiter.quhao.activity;

import android.content.Context;
import android.os.Bundle;
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
	
	private EditText commentEdit;
	
	private EditText avgCostEdit;
	
	private String rId;
	
	private int kouwei;
	
	private int huanjing;
	
	private int fuwu;
	
	private int xingjiabi;
	
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

		InputMethodManager m = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		if (m != null) {
//			if(this.getCurrentFocus()!=null && this.getCurrentFocus().getWindowToken() != null)
//			{
//				m.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
//			}
			
			//R.id.login
			//m.hideSoftInputFromWindow(passwordText.getWindowToken(), 0);
			//m.hideSoftInputFromWindow(loginNameText.getWindowToken(), 0);
			if(m.isActive()){
				m.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS);
			}
			
			
		}
		
		switch (v.getId()) {
		case R.id.submit:
			Thread thread = new Thread(new Runnable() {
				
				@Override
				public void run() {
					
					try {
						Looper.prepare();
						progressDialogUtil = new ProgressDialogUtil(CreateCommentActivity.this, R.string.empty, R.string.committing, false);
						progressDialogUtil.showProgress();
						averageCost = avgCostEdit.getText().toString().trim();
						comment = commentEdit.getText().toString().trim();
						kouwei = (int) kouweiRatingBar.getRating();
						huanjing = (int) huanjingRatingBar.getRating();
						fuwu = (int) fuwuRatingBar.getRating();
						xingjiabi = (int) xingjiabiRatingBar.getRating();
						float gradeAvg = (kouwei + huanjing + fuwu + xingjiabi)/4;
						int grade = Math.round(gradeAvg);
						if(StringUtils.isNull(comment))
						{
							Toast.makeText(CreateCommentActivity.this, "请填写评论", Toast.LENGTH_SHORT).show();
							progressDialogUtil.closeProgress();
							unlockHandler.sendEmptyMessage(UNLOCK_CLICK);
							return;
						}
					
						if (!ActivityUtil.isNetWorkAvailable(CreateCommentActivity.this)) {
							Toast.makeText(CreateCommentActivity.this, R.string.network_error_info, Toast.LENGTH_SHORT).show();
							progressDialogUtil.closeProgress();
							unlockHandler.sendEmptyMessage(UNLOCK_CLICK);
							return;
						}
						
						CommonHTTPRequest.get("updateComment?rid=" + rId + "&kouwei=" + kouwei + "&huanjing=" + huanjing + "&fuwu=" + fuwu + "&xingjiabi=" + xingjiabi + "&grade=" + grade + "&averageCost=" + averageCost +  "&content=" + comment);
						progressDialogUtil.closeProgress();
						unlockHandler.sendEmptyMessage(UNLOCK_CLICK);
						Toast.makeText(CreateCommentActivity.this, "评论成功", Toast.LENGTH_SHORT).show();
						CreateCommentActivity.this.finish();
					} catch (Exception e) {
						e.printStackTrace();
						Toast.makeText(CreateCommentActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
						progressDialogUtil.closeProgress();
						unlockHandler.sendEmptyMessage(UNLOCK_CLICK);
						CreateCommentActivity.this.finish();
					}
					finally
					{
						Looper.loop();
					}
				}
			});
			thread.start();
			break;
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
			default:
				unlockHandler.sendEmptyMessage(UNLOCK_CLICK);
			break;
		}
	}

}
