package com.withiter.quhao.view;

import com.withiter.quhao.R;
import com.withiter.quhao.util.tool.CommonTool;
import com.withiter.quhao.view.adapter.ArrayWheelAdapter;
import com.withiter.quhao.view.adapter.NumericWheelAdapter;
import com.withiter.quhao.view.adapter.OnWheelChangedListener;
import com.withiter.quhao.view.adapter.WheelView;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.ViewFlipper;

public class SelectSeatNo extends PopupWindow implements OnClickListener {
	/**
	 * 主activity
	 */
	private Activity mContext;

	/**
	 * 选择的view
	 */
	private View selectView;
	private ViewFlipper viewfipper;
	private Button submitBtn;
	private Button cancelBtn;
	private String seatNo;
	private SeatNumericAdapter seatNoAdapter;
	private WheelView seatView;
	private int mCurSeatNo = 0;
	private String[] seats;
	private String[] seatType;

	public SelectSeatNo(Activity context) {
		super(context);
		mContext = context;

		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		selectView = inflater.inflate(R.layout.seat_select, null);
		viewfipper = new ViewFlipper(context);
		viewfipper.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));

		seatView = (WheelView) selectView.findViewById(R.id.seat);
		submitBtn = (Button) selectView.findViewById(R.id.submit);
		cancelBtn = (Button) selectView.findViewById(R.id.cancel);

		submitBtn.setOnClickListener(this);
		cancelBtn.setOnClickListener(this);

		OnWheelChangedListener listener = new OnWheelChangedListener() {

			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				updateSeatNo(seatView);
			}

		};

		if (CommonTool.isNotNull(seatNo)) {
			mCurSeatNo = Integer.parseInt(seatNo);
		}
		seatType = mContext.getResources().getStringArray(R.array.seat);

		seats = new String[] { "1w", "2w", "3w", "6w", "7w", "8w", "9w", "10w",
				"14w", "15w", "16w", "17zz", "18", "19", "110" };
		seatNoAdapter = new SeatNumericAdapter(mContext, seats, 0);
		seatNoAdapter.setTextType(seatType[0]);
		seatView.setViewAdapter(seatNoAdapter);
		seatView.setCurrentItem(mCurSeatNo);
		seatView.addChangingListener(listener);
		updateSeatNo(seatView);

		viewfipper.addView(selectView);
		viewfipper.setFlipInterval(6000000);
		this.setContentView(viewfipper);
		this.setWidth(LayoutParams.MATCH_PARENT);
		this.setHeight(LayoutParams.WRAP_CONTENT);
		this.setFocusable(true);
		ColorDrawable dw = new ColorDrawable(0x00000000);
		this.setBackgroundDrawable(dw);
		this.update();
	}

	@Override
	public void showAtLocation(View parent, int gravity, int x, int y) {
		super.showAtLocation(parent, gravity, x, y);
		viewfipper.startFlipping();
	}

	private void updateSeatNo(WheelView seatView) {
		seatNo = seats[seatView.getCurrentItem()];
	}

	private class SeatNumericAdapter extends ArrayWheelAdapter<String> {
		// Index of current item
		int currentItem;
		// Index of item to be highlighted
		int currentValue;

		/**
		 * Constructor
		 */
		public SeatNumericAdapter(Context context, String[] items, int current) {
			super(context, items);
			this.currentItem = current;
			setTextSize(24);
		}

		protected void configureTextView(TextView view) {
			super.configureTextView(view);
			view.setTypeface(Typeface.SANS_SERIF);
		}

		public CharSequence getItemText(int index) {
			currentItem = index;
			return super.getItemText(index);
		}

	}

	@Override
	public void onClick(View v) {
		TextView text = (TextView) mContext.findViewById(R.id.seatNo);
		text.setText(seatNo);
		this.dismiss();

	}

}
