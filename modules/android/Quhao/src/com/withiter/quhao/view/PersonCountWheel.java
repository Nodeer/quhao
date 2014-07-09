package com.withiter.quhao.view;

import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.withiter.quhao.R;
import com.withiter.quhao.view.wheel.OnWheelChangedListener;
import com.withiter.quhao.view.wheel.WheelView;
import com.withiter.quhao.view.wheel.adapters.AbstractWheelTextAdapter;


public class PersonCountWheel extends RelativeLayout {

	private List<String> personCounts;
	
	private int selectedItem;
	private Context mContext;
	private WheelView wheelView;
	private Button personCountSubmit;
	
	private OnPersonCountWheelListener onPersonCountWheelListener;
	public List<String> getItems() {
		return personCounts;
	}

	public void setItems(List<String> personCounts) {
		this.personCounts = personCounts;
	}

	public PersonCountWheel(Context context) {
		super(context);
		init(context);
	}
	
	public PersonCountWheel(Context context,List<String> personCounts,int selectedItem) {
		super(context);
		this.personCounts = personCounts;
		this.selectedItem = selectedItem;
		init(context);
	}

	public PersonCountWheel(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public PersonCountWheel(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	private void init(Context context) {
		mContext = context;
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.person_count_wheel_layout, this, true);
		wheelView = (WheelView) findViewById(R.id.person_count_wheel);
		wheelView.setVisibleItems(5); // Number of items
		wheelView.setWheelBackground(R.drawable.wheel_bg_holo);
		wheelView.setWheelForeground(R.drawable.wheel_val_holo);
		wheelView.setShadowColor(0xFF000000, 0x88000000, 0x00000000);
		wheelView.setViewAdapter(new PersonCountWheelAdapter(mContext,personCounts));
//		wheelView.setCurrentItem(3);
		wheelView.addChangingListener(new OnWheelChangedListener() {
			
			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				
				selectedItem = newValue;
				if (null != onPersonCountWheelListener) {
					onPersonCountWheelListener.onPersonCountChanged(oldValue, newValue);
				}
			}
		});
		
		if (selectedItem < personCounts.size()) {
			wheelView.setCurrentItem(selectedItem);
		}
		
		
		personCountSubmit = (Button) findViewById(R.id.person_count_submit);
		personCountSubmit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				if (onPersonCountWheelListener != null) {
					onPersonCountWheelListener.onPersonCountSubmitClick(view, selectedItem);
				}
			}
		});
	}
	
	/**
	 * Adapter for countries
	 */
	private class PersonCountWheelAdapter extends AbstractWheelTextAdapter {
		// City names
		final List<String> personCounts;

		/**
		 * Constructor
		 */
		protected PersonCountWheelAdapter(Context context,List<String> personCounts) {
			super(context, R.layout.person_count_wheel_holo_layout, NO_RESOURCE);

			setItemTextResource(R.id.person_count_item_name);
			this.personCounts = personCounts;
		}

		@Override
		public View getItem(int index, View cachedView, ViewGroup parent) {
			View view = super.getItem(index, cachedView, parent);
			return view;
		}

		@Override
		public int getItemsCount() {
			return personCounts.size();
		}

		@Override
		protected CharSequence getItemText(int index) {
			return personCounts.get(index);
		}
	}

	public OnPersonCountWheelListener getOnPersonCountWheelListener() {
		return onPersonCountWheelListener;
	}

	public void setOnPersonCountWheelListener(
			OnPersonCountWheelListener onPersonCountWheelListener) {
		this.onPersonCountWheelListener = onPersonCountWheelListener;
	}
}
