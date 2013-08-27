package com.withiter.quhao.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;
import android.widget.ScrollView;

import com.withiter.quhao.util.QuhaoLog;

public class InnerListView extends ListView {

	private static String TAG = "InnerListView";
	private ScrollView parentScroll;
	private int maxHeight;

	public InnerListView(Context context) {
		super(context);
	}

	public InnerListView(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			setParentScrollAble(false);// 当手指触到listview的时候，让父ScrollView交出ontouch权限，也就是让父scrollview
										// 停住不能滚动
			QuhaoLog.d(TAG, "onInterceptTouchEvent down");
		case MotionEvent.ACTION_MOVE:
			QuhaoLog.d(TAG, "onInterceptTouchEvent move");
			break;
		case MotionEvent.ACTION_UP:
			QuhaoLog.d(TAG, "onInterceptTouchEvent up");
		case MotionEvent.ACTION_CANCEL:
			QuhaoLog.d(TAG, "onInterceptTouchEvent cancel");
			setParentScrollAble(true);// 当手指松开时，让父ScrollView重新拿到onTouch权限
			break;
		default:
			break;
		}
		return super.onInterceptTouchEvent(ev);
	}

	/**
	 * 是否把滚动事件交给父scrollview
	 * 
	 * @param flag
	 */
	private void setParentScrollAble(boolean flag) {
		parentScroll.requestDisallowInterceptTouchEvent(!flag);// 这里的parentScrollView就是listview外面的那个scrollview
	}

	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		if (maxHeight > -1) {
			heightMeasureSpec = MeasureSpec.makeMeasureSpec(maxHeight,
					MeasureSpec.AT_MOST);
		}
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		System.out.println(getChildAt(0));
	}

	public ScrollView getParentScroll() {
		return parentScroll;
	}

	public void setParentScroll(ScrollView parentScroll) {
		this.parentScroll = parentScroll;
	}

	public int getMaxHeight() {
		return maxHeight;
	}

	public void setMaxHeight(int maxHeight) {
		this.maxHeight = maxHeight;
	}

}
