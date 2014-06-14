package com.withiter.quhao.view.expandtab;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.withiter.quhao.R;


public class ViewRight extends RelativeLayout implements ViewBaseAction{

	private ListView mListView;
	private List<String> items = new ArrayList<String>();// { "item1", "item2", "item3", "item4", "item5", "item6" };//显示字段
	private List<String> itemsVaule = new ArrayList<String>();//[] { "1", "2", "3", "4", "5", "6" };//隐藏id
	private OnSelectListener mOnSelectListener;
	private TextAdapter adapter;
	private String mDistance;
	private String showText = "item1";
	private Context mContext;

	public String getShowText() {
		return showText;
	}

	public void setShowText(String showText) {
		this.showText = showText;
	}
	
	public ViewRight(Context context) {
		super(context);
		init(context);
	}

	public ViewRight(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public ViewRight(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public ViewRight(Context context, List<String> sortByItems, List<String> sortByValues,
			String defaultSortBy) {
		super(context);
		this.items = sortByItems;
		this.itemsVaule = sortByValues;
		this.mDistance = defaultSortBy;
		init(context);
	}

	private void init(Context context) {
		mContext = context;
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.view_distance, this, true);
		setBackgroundDrawable(getResources().getDrawable(R.drawable.choosearea_bg_right));
		mListView = (ListView) findViewById(R.id.listView);
		adapter = new TextAdapter(context, items, R.drawable.choose_item_right, R.drawable.choose_eara_item_selector);
		adapter.setTextSize(17);
		if (mDistance != null) {
			for (int i = 0; i < itemsVaule.size(); i++) {
				if (itemsVaule.get(i).equals(mDistance)) {
					adapter.setSelectedPositionNoNotify(i);
					showText = items.get(i);
					break;
				}
			}
		}
		mListView.setAdapter(adapter);
		adapter.setOnItemClickListener(new TextAdapter.OnItemClickListener() {

			@Override
			public void onItemClick(View view, int position) {

				if (mOnSelectListener != null) {
					showText = items.get(position);
					mOnSelectListener.getValue(itemsVaule.get(position), items.get(position));
				}
			}
		});
	}

	public void setOnSelectListener(OnSelectListener onSelectListener) {
		mOnSelectListener = onSelectListener;
	}

	public interface OnSelectListener {
		public void getValue(String distance, String showText);
	}

	@Override
	public void hide() {
		
	}

	@Override
	public void show() {
		
	}

}
