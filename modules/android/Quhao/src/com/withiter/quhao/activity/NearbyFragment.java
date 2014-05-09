package com.withiter.quhao.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.withiter.quhao.QHClientApplication;
import com.withiter.quhao.R;
import com.withiter.quhao.adapter.MerchantNearByAdapter;
import com.withiter.quhao.task.NearbyMerchantsTask;
import com.withiter.quhao.util.ActivityUtil;
import com.withiter.quhao.util.StringUtils;
import com.withiter.quhao.util.tool.ParseJson;
import com.withiter.quhao.view.expandtab.ExpandTabView;
import com.withiter.quhao.view.expandtab.ViewLeft;
import com.withiter.quhao.view.refresh.PullToRefreshView;
import com.withiter.quhao.view.refresh.PullToRefreshView.OnFooterRefreshListener;
import com.withiter.quhao.view.refresh.PullToRefreshView.OnHeaderRefreshListener;
import com.withiter.quhao.vo.Merchant;

public class NearbyFragment extends Fragment implements AMapLocationListener, OnItemClickListener, OnClickListener, OnHeaderRefreshListener, OnFooterRefreshListener {

	private LocationManagerProxy mAMapLocationManager = null;
	
	private Handler locationHandler = new Handler();
	
	private int page = 1;
	private ListView merchantsListView;
	private MerchantNearByAdapter nearByAdapter;
	private boolean isFirstLoad = true;
	private boolean needToLoad = true;
	private List<Merchant> merchantList;

	private boolean isFirstLocation = false;

	private AMapLocation firstLocation = null;

	private ExpandTabView expandTabView;
	private ArrayList<View> mViewArray = new ArrayList<View>();
	private ViewLeft viewLeft;

	private int searchDistence;

	private String[] distanceItems;

	private String[] distanceItemsValue;

	private boolean isClick;

	private static final int UNLOCK_CLICK = 1000;

	private View contentView;

	private ViewGroup group;

	private PullToRefreshView mPullToRefreshView;

	protected Handler unlockHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == UNLOCK_CLICK) {
				isClick = false;
			}
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		this.group = container;
		page = 1;
		isFirstLoad = true;
		needToLoad = true;
		isFirstLocation = false;
		firstLocation = null;
		contentView = inflater.inflate(R.layout.nearby_fragment_layout, container, false);
		merchantsListView = (ListView) contentView.findViewById(R.id.merchantsListView);

		mPullToRefreshView = (PullToRefreshView) contentView.findViewById(R.id.main_pull_refresh_view);
		mPullToRefreshView.setEnableFooterView(true);
		mPullToRefreshView.setOnHeaderRefreshListener(this);
		mPullToRefreshView.setOnFooterRefreshListener(this);

		merchantsListView.setNextFocusDownId(R.id.merchantsListView);

		initExpandView();

		if (!ActivityUtil.isNetWorkAvailable(getActivity())) {
			Builder dialog = new AlertDialog.Builder(getActivity());
			dialog.setTitle("温馨提示").setMessage("Wifi/蜂窝网络未打开，或者网络情况不是很好哟").setPositiveButton("确定", null);
			dialog.show();
		}

		if (mAMapLocationManager == null) {
			mAMapLocationManager = LocationManagerProxy.getInstance(getActivity());
			/*
			 * mAMapLocManager.setGpsEnable(false);//
			 * 1.0.2版本新增方法，设置true表示混合定位中包含gps定位，false表示纯网络定位，默认是true
			 */
			// Location SDK定位采用GPS和网络混合定位方式，时间最短是5000毫秒，否则无效
			mAMapLocationManager.requestLocationUpdates(LocationProviderProxy.AMapNetwork, 5000, 10, this);
			
			locationHandler.postDelayed(new Runnable() {
				
				@Override
				public void run() {
					if (firstLocation == null) {
						Toast.makeText(getActivity(), "亲，定位失败，请检查网络状态！", Toast.LENGTH_SHORT).show();
						contentView.findViewById(R.id.loadingbar).setVisibility(View.GONE);
						contentView.findViewById(R.id.serverdata).setVisibility(View.VISIBLE);
						stopLocation();// 销毁掉定位
					}
				}
			}, 10000);// 设置超过12秒还没有定位到就停止定位
		}
		contentView.findViewById(R.id.loadingbar).setVisibility(View.VISIBLE);
		contentView.findViewById(R.id.serverdata).setVisibility(View.GONE);
		return contentView;
	}

	/**
	 * 销毁定位
	 */
	private void stopLocation() {
		if (mAMapLocationManager != null) {
			mAMapLocationManager.removeUpdates(this);
			mAMapLocationManager.destory();
		}
		mAMapLocationManager = null;
	}
	
	private void initExpandView() {

		if (searchDistence == 0) {
			searchDistence = 1;
			distanceItems = new String[] { "1000米", "2000米", "3000米", "4000米", "5000米", "搜全城" };// 显示字段
			distanceItemsValue = new String[] { "1", "2", "3", "4", "5", "-1" };// 显示字段
		}

		expandTabView = (ExpandTabView) contentView.findViewById(R.id.expandtab_view);
		viewLeft = new ViewLeft(contentView.getContext(), distanceItems, distanceItemsValue, String.valueOf(searchDistence));

		mViewArray = new ArrayList<View>();
		mViewArray.add(viewLeft);
		ArrayList<String> mTextArray = new ArrayList<String>();
		mTextArray.add("距离");
		expandTabView.setValue(mTextArray, mViewArray);
		expandTabView.setTitle(viewLeft.getShowText(), 0);

		viewLeft.setOnSelectListener(new ViewLeft.OnSelectListener() {
			@Override
			public void getValue(String distance, String showText) {
				onRefresh(viewLeft, showText);
			}
		});
	}

	private void onRefresh(View view, String showText) {

		expandTabView.onPressBack();
		int position = getPositon(view);
		if (position >= 0 && !expandTabView.getTitle(position).equals(showText)) {
			expandTabView.setTitle(showText, position);
		}

		for (int i = 0; i < distanceItems.length; i++) {
			if (showText.equals(distanceItems[i])) {
				searchDistence = Integer.valueOf(distanceItemsValue[i]);
				break;
			}
		}
		// setPoiSearch();
		// buildTask();
		merchantsListView.setSelectionFromTop(0, 0);// 滑动到第一项
		page = 1;
		// isFirstLoad = true;
		needToLoad = true;
		// isFirstLocation = false;
		// firstLocation = null;

		merchantList = new ArrayList<Merchant>();
		queryNearbyMerchants();

		Toast.makeText(getActivity(), showText, Toast.LENGTH_SHORT).show();

	}

	private int getPositon(View tView) {
		for (int i = 0; i < mViewArray.size(); i++) {
			if (mViewArray.get(i) == tView) {
				return i;
			}
		}
		return -1;
	}

	protected Handler updateMerchantsHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			super.handleMessage(msg);

			if (msg.what == 200) {

				if (isFirstLoad) {

					nearByAdapter = new MerchantNearByAdapter(getActivity(), merchantsListView, merchantList);
					merchantsListView.setAdapter(nearByAdapter);
					isFirstLoad = false;
				} else {
					nearByAdapter.merchants = merchantList;
				}
				nearByAdapter.notifyDataSetChanged();
				// merchantsListView.setOnScrollListener(NearbyFragment.this);
				merchantsListView.setOnItemClickListener(NearbyFragment.this);
				contentView.findViewById(R.id.loadingbar).setVisibility(View.GONE);
				contentView.findViewById(R.id.serverdata).setVisibility(View.VISIBLE);
				mPullToRefreshView.onHeaderRefreshComplete();
				mPullToRefreshView.onFooterRefreshComplete();
				if (!needToLoad) {
					mPullToRefreshView.setEnableFooterView(false);
				} else {
					mPullToRefreshView.setEnableFooterView(true);
				}
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			}
		}

	};

	@Override
	public void onPause() {
		super.onPause();
		if (mAMapLocationManager != null) {
			mAMapLocationManager.removeUpdates(this);
		}
	}

	@Override
	public void onDestroyView() {
		if (mAMapLocationManager != null) {
			mAMapLocationManager.removeUpdates(this);
			mAMapLocationManager.destory();
		}
		mAMapLocationManager = null;
		super.onDestroyView();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onViewStateRestored(Bundle savedInstanceState) {
		super.onViewStateRestored(savedInstanceState);
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	@Override
	public void onDetach() {
		super.onDetach();
	}

	@Override
	public void onDestroy() {
		if (mAMapLocationManager != null) {
			mAMapLocationManager.removeUpdates(this);
			mAMapLocationManager.destory();
		}
		mAMapLocationManager = null;
		super.onDestroy();
	}

	@Override
	public void onLocationChanged(Location location) {

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {

	}

	@Override
	public void onProviderEnabled(String provider) {

	}

	@Override
	public void onProviderDisabled(String provider) {

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

		// 已经点过，直接返回
		if (isClick) {
			return;
		}

		// 设置已点击标志，避免快速重复点击
		isClick = true;
		// 解锁
		try {
			if (null != merchantList && !merchantList.isEmpty() && null != merchantList.get(position) && StringUtils.isNotNull(merchantList.get(position).id) && merchantList.get(position).enable) {
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				Intent intent = new Intent();
				intent.setClass(getActivity(), MerchantDetailActivity.class);
				intent.putExtra("merchantId", merchantList.get(position).id);
				getActivity().startActivity(intent);
				getActivity().overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);

			} else {
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				AlertDialog.Builder builder = new Builder(getActivity());
				builder.setTitle("温馨提示");
				builder.setMessage("对不起，该商家未在取号系统注册。");
				builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				builder.create().show();
			}

		} catch (Exception e) {
			unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			AlertDialog.Builder builder = new Builder(getActivity());
			builder.setTitle("温馨提示");
			builder.setMessage("对不起，网络异常。");
			builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			builder.create().show();
			e.printStackTrace();
		}

	}

	@Override
	public void onLocationChanged(AMapLocation location) {

		if (null != location) {
			if (!isFirstLocation) {
				isFirstLocation = true;
				firstLocation = location;
				merchantList = new ArrayList<Merchant>();
				queryNearbyMerchants();
			} else {
				float distance = firstLocation.distanceTo(location);
				if (distance > 100) {
					firstLocation = location;
					merchantList = new ArrayList<Merchant>();
					queryNearbyMerchants();
				} else {
					return;
				}

			}

		}
	}

	private void queryNearbyMerchants() {
		if(null == firstLocation)
		{
			Toast.makeText(getActivity(), "亲，现在没有定位信息，不能查看哦。", Toast.LENGTH_LONG).show();
			return;
		}
		String url = "getNearMerchants?userX=" + firstLocation.getLongitude() + "&userY=" + firstLocation.getLatitude() + "&cityCode=" + QHClientApplication.getInstance().defaultCity.cityCode
				+ "&page=" + page + "&maxDis=" + searchDistence;
		final NearbyMerchantsTask task = new NearbyMerchantsTask(R.string.waitting, getActivity(), url);
		task.execute(new Runnable() {

			@Override
			public void run() {

				String result = task.result;
				List<Merchant> tempList = ParseJson.getMerchants(result);
				if (null == tempList || tempList.isEmpty() || tempList.size() < 20) {
					needToLoad = false;
				}
				if (null == merchantList) {
					merchantList = new ArrayList<Merchant>();
				}
				merchantList.addAll(tempList);
				Log.e("wjzwjz ", " success merchant list size : " + merchantList.size());
				updateMerchantsHandler.obtainMessage(200, null).sendToTarget();
			}
		}, new Runnable() {

			@Override
			public void run() {
				Log.e("wjzwjz ", " error merchant list size : " + merchantList.size());
				needToLoad = false;
				updateMerchantsHandler.obtainMessage(200, null).sendToTarget();
			}
		});
	}

	@Override
	public void onResume() {
		super.onResume();
		// contentView.findViewById(R.id.loadingbar).setVisibility(View.VISIBLE);
		// contentView.findViewById(R.id.serverdata).setVisibility(View.GONE);

		// buildTask();
	};

	@Override
	public void onClick(View v) {

		// 已经点过，直接返回
		if (isClick) {
			return;
		}

		// 设置已点击标志，避免快速重复点击
		isClick = true;
		// 解锁
		unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);

		switch (v.getId()) {
		default:
			break;
		}
	}

	@Override
	public void onHeaderRefresh(PullToRefreshView view) {
		page = 1;
		// isFirstLoad = true;
		needToLoad = true;
		// isFirstLocation = false;
		// firstLocation = null;

		merchantList = new ArrayList<Merchant>();
		queryNearbyMerchants();
		// mPullToRefreshView.postDelayed(new Runnable() {
		//
		// @Override
		// public void run() {
		//
		// page = 1;
		// isFirstLoad = true;
		// needToLoad = true;
		// isFirstLocation = false;
		// // firstLocation = null;
		//
		// merchantList = new ArrayList<Merchant>();
		// queryNearbyMerchants();
		// // mAMapLocationManager = LocationManagerProxy.getInstance(this);
		// // mAMapLocationManager.requestLocationUpdates(
		// // LocationProviderProxy.AMapNetwork, 5000, 10, this);
		//
		// // buildTask();
		//
		// // mPullToRefreshView.onHeaderRefreshComplete("更新于:"+new
		// Date().toLocaleString());
		//
		// // mPullToRefreshView.onHeaderRefreshComplete();
		// }
		// }, 1000);

	}

	@Override
	public void onFooterRefresh(PullToRefreshView view) {
		page++;
		queryNearbyMerchants();
		// mPullToRefreshView.postDelayed(new Runnable() {
		//
		// @Override
		// public void run() {
		// // 处理下拉刷新最新数据
		// // merchantList = new ArrayList<Merchant>();
		//
		//
		// }
		// }, 1000);

	}

}