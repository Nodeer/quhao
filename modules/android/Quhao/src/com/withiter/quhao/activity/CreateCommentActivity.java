package com.withiter.quhao.activity;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.Toast;

import com.withiter.quhao.R;
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

		progressDialogUtil = new ProgressDialogUtil(this, R.string.empty, R.string.committing, false);
		progressDialogUtil.showProgress();
		switch (v.getId()) {
		case R.id.submit:
			try {
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
					Toast.makeText(this, "请填写评论", Toast.LENGTH_LONG).show();
					progressDialogUtil.closeProgress();
					unlockHandler.sendEmptyMessage(UNLOCK_CLICK);
					return;
				}
			
				CommonHTTPRequest.get("updateComment?rid=" + rId + "&kouwei=" + kouwei + "&huanjing=" + huanjing + "&fuwu=" + fuwu + "&xingjiabi=" + xingjiabi + "&grade=" + grade + "&averageCost=" + averageCost +  "&content=" + comment);
			} catch (Exception e) {
				e.printStackTrace();
			}
			finally
			{
				progressDialogUtil.closeProgress();
				unlockHandler.sendEmptyMessage(UNLOCK_CLICK);
				Toast.makeText(this, "评论成功", Toast.LENGTH_LONG).show();
				this.finish();
			}

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
