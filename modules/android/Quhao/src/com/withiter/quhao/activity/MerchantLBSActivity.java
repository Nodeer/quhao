package com.withiter.quhao.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.InfoWindowAdapter;
import com.amap.api.maps.AMap.OnInfoWindowClickListener;
import com.amap.api.maps.AMap.OnMapLoadedListener;
import com.amap.api.maps.AMap.OnMarkerClickListener;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.withiter.quhao.R;
import com.withiter.quhao.util.QuhaoLog;
import com.withiter.quhao.util.StringUtils;
import com.withiter.quhao.util.http.CommonHTTPRequest;
import com.withiter.quhao.util.tool.ParseJson;
import com.withiter.quhao.vo.Merchant;

public class MerchantLBSActivity extends QuhaoBaseActivity implements OnMarkerClickListener, OnMapLoadedListener, OnInfoWindowClickListener, InfoWindowAdapter {

	private static final String TAG = MerchantLBSActivity.class.getName();

	private Button btnBack;

	private TextView merchantNameView;

	private String merchantName;

	private String merchantId;

	private Merchant merchant;

	private MapView mMapView;

	private AMap mAMap;

	private CameraUpdate update = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.merchant_lbs_map);

		this.merchantName = this.getIntent().getStringExtra("merchantName");
		this.merchantId = this.getIntent().getStringExtra("merchantId");

		merchantNameView = (TextView) findViewById(R.id.name);
		merchantNameView.setText(merchantName);
		if(StringUtils.isNotNull(merchantName) && merchantName.length()>10)
		{
			merchantNameView.setText(merchantName.substring(0, 10) + "...");
		}

		mMapView = (MapView) findViewById(R.id.mapView);
		mMapView.onCreate(savedInstanceState);
		// markerText = (TextView) findViewById(R.id.mark_listenter_text);
		init();
	}

	private void init() {
		btnBack = (Button) findViewById(R.id.back_btn);
		btnBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				MerchantLBSActivity.this.finish();
			}
		});

		if (mAMap == null) {
			mAMap = mMapView.getMap();
			mAMap.setOnMapLoadedListener(this);// 设置amap加载成功事件监听器
			mAMap.setOnMarkerClickListener(this);// 设置点击marker事件监听器
			mAMap.setOnInfoWindowClickListener(this);// 设置点击infoWindow事件监听器
			mAMap.setInfoWindowAdapter(this);// 设置自定义InfoWindow样式
		}

		Thread initLocation = new Thread(getLocationRunnable);
		initLocation.start();
	}

	private Runnable getLocationRunnable = new Runnable() {
		@Override
		public void run() {
			try {
				Looper.prepare();
				QuhaoLog.v(TAG, "get categorys data form server begin");
				String buf = CommonHTTPRequest.get("merchant?id=" + MerchantLBSActivity.this.merchantId);
				// + MerchantDetailActivity.this.merchantId);
				if (StringUtils.isNull(buf) && "[]".equals(buf)) {
				} else {

					merchant = ParseJson.getMerchant(buf);
					// merchant.lat = 31.678109;
					// merchant.lng = 31.678109;
					// List<ReservationVO> rvos =
					// ParseJson.getReservations(buf);
					// locations = new ArrayList<MerchantLocation>();
					// MerchantLocation location = new
					// MerchantLocation("51e563feae4d165869fda38c", "name111",
					// 31.678109, 31.678109, "address11");
					// MerchantLocation location2 = new
					// MerchantLocation("51e563feae4d165869fda382", "name222",
					// 31.678109, 50.678109, "address22");
					// locations.add(location);
					// locations.add(location2);
					getLocationUpdateHandler.obtainMessage(200, merchant).sendToTarget();
				}

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				Looper.loop();
			}
		}
	};

	private Handler getLocationUpdateHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 200) {
				super.handleMessage(msg);

				if(null!=merchant)
				{
					merchantNameView.setText(merchant.name);
					if(StringUtils.isNotNull(merchant.name) && merchant.name.length()>10)
					{
						merchantNameView.setText(merchant.name.substring(0, 10) + "...");
					}
					LatLng latLng = new LatLng(merchant.lat, merchant.lng);
					MarkerOptions options = new MarkerOptions();
					options.position(latLng).title(merchant.name).snippet(merchant.address).icon(BitmapDescriptorFactory.fromResource(R.drawable.menu_merchant_nearby)).perspective(true);

					mAMap.addMarker(options);
					update = CameraUpdateFactory.changeLatLng(latLng);

					if (null != update) {
						mAMap.moveCamera(update);
					}
				}
				

				/*
				 * MerchantLocation location = null; for (int i = 0; i <
				 * locations.size(); i++) { location = locations.get(i); LatLng
				 * latLng = new LatLng(location.lat, location.lng);
				 * MarkerOptions options = new MarkerOptions();
				 * options.position(
				 * latLng).title(location.name).snippet(location.address)
				 * .icon(BitmapDescriptorFactory
				 * .fromResource(R.drawable.menu_merchant_nearby
				 * )).perspective(true);
				 * 
				 * mAMap.addMarker(options); if(!latLngs.contains(latLng)) {
				 * latLngs.add(latLng); } if (i==0) { update =
				 * CameraUpdateFactory.changeLatLng(latLng); } }
				 */

			}

		}

	};

	@Override
	public void finish() {
		super.finish();
	}

	/**
	 * 把一个xml布局文件转化成view
	 */
	/*
	 * public View getView(String title, String text) { View view =
	 * getLayoutInflater().inflate(R.layout.marker, null); TextView text_title =
	 * (TextView) view.findViewById(R.id.marker_title); TextView text_text =
	 * (TextView) view.findViewById(R.id.marker_text);
	 * text_title.setText(title); text_text.setText(text); return view; }
	 */

	/**
	 * 把一个view转化成bitmap对象
	 */
	/*
	 * public static Bitmap getViewBitmap(View view) {
	 * view.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
	 * MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)); view.layout(0,
	 * 0, view.getMeasuredWidth(), view.getMeasuredHeight());
	 * view.buildDrawingCache(); Bitmap bitmap = view.getDrawingCache(); return
	 * bitmap; }
	 */

	@Override
	public void onClick(View v) {

	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return false;
	}

	@Override
	protected void onResume() {

		super.onResume();
		mMapView.onResume();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {

		super.onSaveInstanceState(outState);
		mMapView.onSaveInstanceState(outState);
	}

	@Override
	protected void onDestroy() {

		super.onDestroy();
		mMapView.onDestroy();
	}

	@Override
	protected void onPause() {

		super.onPause();
		mMapView.onPause();
	}

	@Override
	public void onMapLoaded() {
		// 设置所有maker显示在View中
		if (null != update) {
			mAMap.moveCamera(update);
		}
	}

	/**
	 * 对marker标注点点击响应事件
	 */
	@Override
	public boolean onMarkerClick(Marker marker) {
		// markerText.setText("你点击的是" + marker.getTitle());
		return false;

	}

	/**
	 * 监听自定义infowindow窗口的infocontents事件回调
	 */
	@Override
	public View getInfoContents(Marker marker) {
		View infoContent = getLayoutInflater().inflate(R.layout.custom_info_contents, null);
		render(marker, infoContent);
		return infoContent;
	}

	/**
	 * 自定义infowinfow窗口
	 */
	public void render(Marker marker, View view) {
		String title = marker.getTitle();
		TextView titleUi = ((TextView) view.findViewById(R.id.title));
		if (title != null) {
			SpannableString titleText = new SpannableString(title);
			titleText.setSpan(new ForegroundColorSpan(Color.RED), 0, titleText.length(), 0);
			titleUi.setText(titleText);

		} else {
			titleUi.setText("");
		}
		String snippet = marker.getSnippet();
		TextView snippetUi = ((TextView) view.findViewById(R.id.snippet));
		if (snippet != null) {
			SpannableString snippetText = new SpannableString(snippet);
			snippetText.setSpan(new ForegroundColorSpan(Color.GREEN), 0, snippetText.length(), 0);
			snippetUi.setText(snippetText);
		} else {
			snippetUi.setText("");
		}
	}

	@Override
	public View getInfoWindow(Marker marker) {
		View infoWindow = getLayoutInflater().inflate(R.layout.custom_info_window, null);
		render(marker, infoWindow);
		return infoWindow;
	}

	@Override
	public void onInfoWindowClick(Marker marker) {
		Toast.makeText(this, "你点击了infoWindow窗口" + marker.getTitle(), Toast.LENGTH_LONG).show();
	}
}
