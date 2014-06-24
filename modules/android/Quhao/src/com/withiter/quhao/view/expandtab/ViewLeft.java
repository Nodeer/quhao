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


public class ViewLeft extends RelativeLayout implements ViewBaseAction{

	private ListView mListView;
	private List<String> items = new ArrayList<String>();// { "item1", "item2", "item3", "item4", "item5", "item6" };//显示字段
	private List<String> itemsVaule = new ArrayList<String>();// { "1", "2", "3", "4", "5", "6" };//隐藏id
	private OnSelectListener mOnSelectListener;
	private TextAdapter adapter;
	public List<String> getItems() {
		return items;
	}

	public void setItems(List<String> items) {
		this.items = items;
	}

	public List<String> getItemsVaule() {
		return itemsVaule;
	}

	public void setItemsVaule(List<String> itemsVaule) {
		this.itemsVaule = itemsVaule;
	}

	private String mDistance;
	private String showText = "item1";
	private Context mContext;

	public String getShowText() {
		return showText;
	}

	public ViewLeft(Context context) {
		super(context);
		init(context);
	}
	
	public ViewLeft(Context context,List<String> items,List<String> itemsValue,String mDistence) {
		super(context);
		this.items = items;
		this.itemsVaule = itemsValue;
		this.mDistance = mDistence;
		init(context);
	}

	public ViewLeft(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public ViewLeft(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	private void init(Context context) {
		mContext = context;
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.view_distance, this, true);
		setBackgroundDrawable(getResources().getDrawable(R.drawable.choosearea_bg_left));
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
	
	public void setShowText(String showText) {
		this.showText = showText;
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

	public void changed(Context context) 
	{
		init(context);
	}

}
