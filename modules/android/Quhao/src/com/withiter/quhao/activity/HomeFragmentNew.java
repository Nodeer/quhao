package com.withiter.quhao.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.withiter.quhao.QHClientApplication;
import com.withiter.quhao.R;
import com.withiter.quhao.util.ActivityUtil;
import com.withiter.quhao.util.QuhaoLog;

public class HomeFragmentNew extends Fragment implements OnClickListener {

	private static final int UNLOCK_CLICK = 1000;
	
	private String LOGTAG = HomeFragmentNew.class.getName();

	private boolean isClick;
	private View contentView;
	private ImageView shareView;
	private ImageView merchantChatView;
	private ImageView getNumberView;

	@Override
	public void onAttach(Activity activity) {
		Log.e("wjzwjz", "HomeFragment onAttach");
		super.onAttach(activity);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		Log.e("wjzwjz", "HomeFragment onActivityCreated");
		super.onActivityCreated(savedInstanceState);
	}
	
	@Override
	public void onViewStateRestored(Bundle savedInstanceState) {
		Log.e("wjzwjz", "HomeFragment onViewStateRestored");
		super.onViewStateRestored(savedInstanceState);
	}

	@Override
	public void onStart() {
		Log.e("wjzwjz", "HomeFragment onStart");
		super.onStart();
	}

	@Override
	public void onPause() {
		Log.e("wjzwjz", "HomeFragment onPause");
		super.onPause();
	}

	@Override
	public void onStop() {
		Log.e("wjzwjz", "HomeFragment onStop");
		
		super.onStop();
	}

	@Override
	public void onDestroyView() {
		Log.e("wjzwjz", "HomeFragment onDestroyView");
		super.onDestroyView();
	}

	@Override
	public void onDestroy() {
		Log.e("wjzwjz", "HomeFragment onDestroy");
		super.onDestroy();
	}

	@Override
	public void onDetach() {
		Log.e("wjzwjz", "HomeFragment onDetach");
		super.onDetach();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// 检查网络
		if (!ActivityUtil.isNetWorkAvailable(getActivity())) {
			Toast.makeText(getActivity(), R.string.network_error_info, Toast.LENGTH_SHORT).show();
		}
		
		if(contentView != null) {
			ViewGroup vg = (ViewGroup) contentView.getParent();
			vg.removeView(contentView);
			return contentView;
		}
		
		// 主页面layout
		contentView = inflater.inflate(R.layout.main_fragment_layout_new, container, false);
		
		merchantChatView = (ImageView) contentView.findViewById(R.id.btn_chat_room);
		getNumberView = (ImageView) contentView.findViewById(R.id.btn_get_number);
		shareView = (ImageView) contentView.findViewById(R.id.btn_share);
		merchantChatView.setOnClickListener(this);
		getNumberView.setOnClickListener(this);
		shareView.setOnClickListener(this);

		InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(shareView.getWindowToken(), 0);

		return contentView;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onResume() {
		super.onResume();
		QuhaoLog.i(LOGTAG, LOGTAG + " onResume");
	}

	protected Handler unlockHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == UNLOCK_CLICK) {
				isClick = false;
			}
		}
	};

	@Override
	public void onClick(View v) {

		if (isClick) {
			return;
		}
		isClick = true;

		switch (v.getId()) {
		case R.id.btn_share:
			unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			Intent login1 = new Intent(getActivity(), ShareListActivity.class);
			login1.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivity(login1);
			break;
		// 取号排队按钮事件
		case R.id.btn_get_number:
			unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
//			getCategoriesFromServerAndDisplay();
			Intent intent3 = new Intent();
			intent3.setClass(getActivity(), MerchantListActivity.class);
			startActivity(intent3);

			break;
		// 聊聊天吧按钮事件
		case R.id.btn_chat_room:
			unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			if (QHClientApplication.getInstance().isLogined) {
				Intent attention = new Intent();
				attention.setClass(getActivity(), MerchantChatRoomsActivity.class);
				startActivity(attention);
			} else {
				
				Intent login2 = new Intent(getActivity(), LoginActivity.class);
				login2.putExtra("activityName", this.getClass().getName());
				login2.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				startActivity(login2);
			}

			break;
		default:
			unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			break;
		}

	}
	
}
