package com.withiter.quhao.activity;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.InfoWindowAdapter;
import com.amap.api.maps.AMap.OnInfoWindowClickListener;
import com.amap.api.maps.AMap.OnMapLoadedListener;
import com.amap.api.maps.AMap.OnMarkerClickListener;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.withiter.quhao.R;
import com.withiter.quhao.util.QuhaoLog;
import com.withiter.quhao.util.StringUtils;
import com.withiter.quhao.vo.MerchantLocation;

public class MerchantLBSActivity extends AppStoreActivity implements OnMarkerClickListener,
	OnMapLoadedListener,OnInfoWindowClickListener,InfoWindowAdapter
{
	
	private static final String TAG = MerchantLBSActivity.class.getName();

	private Button btnBack;
	
	private TextView merchantNameView;
	
	private String merchantName;
	
	private String merchantId;
	
	private List<MerchantLocation> locations;
	
	private MapView mMapView;
	
	private AMap mAMap;
	
	private TextView markerText;
	
	private RadioGroup radioOption;
	
	private List<LatLng> latLngs = new ArrayList<LatLng>();
	
	private Marker XIAN;
	private Marker CHENGDU;
	private static final LatLng marker1 = new LatLng(39.24426, 100.18322);
	private static final LatLng marker2 = new LatLng(39.24426, 104.18322);
	private static final LatLng marker3 = new LatLng(39.24426, 108.18322);
	private static final LatLng marker4 = new LatLng(39.24426, 112.18322);
	private static final LatLng marker5 = new LatLng(39.24426, 116.18322);
	private static final LatLng marker6 = new LatLng(36.24426, 100.18322);
	private static final LatLng marker7 = new LatLng(36.24426, 104.18322);
	private static final LatLng marker8 = new LatLng(36.24426, 108.18322);
	private static final LatLng marker9 = new LatLng(36.24426, 112.18322);
	private static final LatLng marker10 = new LatLng(36.24426, 116.18322);
	
	public static final LatLng CHENGDU1 = new LatLng(30.679879, 104.064855);// 成都市经纬度
	public static final LatLng XIAN1 = new LatLng(34.341568, 108.940174);// 西安市经纬度
	private MarkerOptions markerOption;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.merchant_lbs_map);
		
		
		this.merchantName = this.getIntent().getStringExtra("merchantName");
		this.merchantId = this.getIntent().getStringExtra("merchantId");
		
		merchantNameView = (TextView) findViewById(R.id.name);
		merchantNameView.setText(merchantName);
		
		mMapView = (MapView) findViewById(R.id.mapView);
		mMapView.onCreate(savedInstanceState);
		markerText = (TextView) findViewById(R.id.mark_listenter_text);
		init();
	}



	private void init()
	{
		btnBack = (Button) findViewById(R.id.back_btn);
		btnBack.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				MerchantLBSActivity.this.finish();
			}
		});
		
		if(mAMap == null)
		{
			mAMap = mMapView.getMap();
			mAMap.setOnMapLoadedListener(this);// 设置amap加载成功事件监听器
			mAMap.setOnMarkerClickListener(this);// 设置点击marker事件监听器
			mAMap.setOnInfoWindowClickListener(this);// 设置点击infoWindow事件监听器
			mAMap.setInfoWindowAdapter(this);// 设置自定义InfoWindow样式
		}
		
		Thread initLocation = new Thread(getLocationRunnable);
		initLocation.start();
	}
	
	private Runnable getLocationRunnable = new Runnable()
	{
		@Override
		public void run()
		{
			try {
				QuhaoLog.v(TAG, "get categorys data form server begin");
				String buf = "success";//CommonHTTPRequest.get("getReservations?accountId=51e563feae4d165869fda38c&mid=51efe7d8ae4dca7b4c281754");
						//+ MerchantDetailActivity.this.merchantId);
				if (StringUtils.isNull(buf) && "[]".equals(buf)) {
				} else {
					
					//List<ReservationVO> rvos = ParseJson.getReservations(buf);
					locations = new ArrayList<MerchantLocation>();
					MerchantLocation location = new MerchantLocation("51e563feae4d165869fda38c", "name111", 31.678109, 31.678109, "address11");
					MerchantLocation location2 = new MerchantLocation("51e563feae4d165869fda382", "name222", 31.678109, 50.678109, "address22");
					locations.add(location);
					locations.add(location2);
					getLocationUpdateHandler.obtainMessage(200, locations)
							.sendToTarget();
				}

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
			}
		}
	};
	
	private Handler getLocationUpdateHandler = new Handler(){

		@Override
		public void handleMessage(Message msg)
		{
			if (msg.what == 200) {
				super.handleMessage(msg);

				addMarkersToMap();// 往地图上添加marker
//				CameraUpdate update = null;
//				MerchantLocation location = null;
				
//				for (int i = 0; i < locations.size(); i++) {
//					location = locations.get(i);
//					LatLng latLng = new LatLng(location.lat, location.lng);
//					MarkerOptions options = new MarkerOptions();
//					options.position(latLng).title(location.name).snippet(location.address)
//						.icon(BitmapDescriptorFactory.defaultMarker()).perspective(true);
//					
//					mAMap.addMarker(options);
//					if(!latLngs.contains(latLng))
//					{
//						latLngs.add(latLng);
//					}
////					if (i==0) {
////						update = CameraUpdateFactory.changeLatLng(latLng);
////					}
//				}
//				if(null != update)
//				{
//					mAMap.moveCamera(update);
//				}
				
			}

		}

	};

	/**
	 * 在地图上添加marker
	 */
	private void addMarkersToMap() {
		CHENGDU = mAMap.addMarker(new MarkerOptions()
				.position(CHENGDU1)
				.title("成都市")
				.snippet("成都市:30.679879, 104.064855")
				.icon(BitmapDescriptorFactory.fromBitmap(getViewBitmap(getView(
						"AMap地图", "对marker自定义view")))).perspective(true)
				.draggable(true));// 设置远小近大效果,2.1.0版本新增
		CHENGDU.showInfoWindow();// 设置默认显示一个infowinfow
		markerOption = new MarkerOptions();
		markerOption.position(XIAN1);
		markerOption.title("西安市").snippet("西安市：34.341568, 108.940174");
		markerOption.perspective(true);
		markerOption.draggable(true);
		markerOption.icon(BitmapDescriptorFactory
				.fromResource(R.drawable.menu_merchant_nearby));
		XIAN = mAMap.addMarker(markerOption);
		drawMarkers();// 添加10个带有系统默认icon的marker
	}
	
	/**
	 * 绘制系统默认的10种marker背景图片
	 */
	public void drawMarkers() {
		mAMap.addMarker(new MarkerOptions()
				.position(marker1)
				.title("Marker1 ")
				.icon(BitmapDescriptorFactory
						.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
				.perspective(true).draggable(true)); // 设置此marker可以拖拽
		mAMap.addMarker(new MarkerOptions()
				.position(marker2)
				.title("Marker2 ")
				.perspective(true)
				.draggable(true)
				.icon(BitmapDescriptorFactory
						.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
		mAMap.addMarker(new MarkerOptions()
				.position(marker3)
				.title("Marker3 ")
				.perspective(true)
				.draggable(true)
				.icon(BitmapDescriptorFactory
						.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));
		mAMap.addMarker(new MarkerOptions()
				.position(marker4)
				.title("Marker4 ")
				.perspective(true)
				.draggable(true)
				.icon(BitmapDescriptorFactory
						.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
		mAMap.addMarker(new MarkerOptions()
				.position(marker5)
				.title("Marker5 ")
				.perspective(true)
				.draggable(true)
				.icon(BitmapDescriptorFactory
						.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));
		mAMap.addMarker(new MarkerOptions()
				.position(marker6)
				.title("Marker6 ")
				.perspective(true)
				.draggable(true)
				.icon(BitmapDescriptorFactory
						.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
		mAMap.addMarker(new MarkerOptions()
				.position(marker7)
				.title("Marker7 ")
				.perspective(true)
				.draggable(true)
				.icon(BitmapDescriptorFactory
						.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
		mAMap.addMarker(new MarkerOptions()
				.position(marker8)
				.title("Marker8 ")
				.perspective(true)
				.draggable(true)
				.icon(BitmapDescriptorFactory
						.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));
		mAMap.addMarker(new MarkerOptions()
				.position(marker9)
				.title("Marker9 ")
				.perspective(true)
				.draggable(true)
				.icon(BitmapDescriptorFactory
						.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));
		mAMap.addMarker(new MarkerOptions()
				.position(marker10)
				.title("Marker10 ")
				.perspective(true)
				.draggable(true)
				.icon(BitmapDescriptorFactory
						.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
	}
	
	@Override
	public void finish() {
		super.finish();
	}

	/**
	 * 把一个xml布局文件转化成view
	 */
	public View getView(String title, String text) {
		View view = getLayoutInflater().inflate(R.layout.marker, null);
		TextView text_title = (TextView) view.findViewById(R.id.marker_title);
		TextView text_text = (TextView) view.findViewById(R.id.marker_text);
		text_title.setText(title);
		text_text.setText(text);
		return view;
	}

	/**
	 * 把一个view转化成bitmap对象
	 */
	public static Bitmap getViewBitmap(View view) {
		view.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
				MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
		view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
		view.buildDrawingCache();
		Bitmap bitmap = view.getDrawingCache();
		return bitmap;
	}
	
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
		LatLngBounds bounds = new LatLngBounds.Builder()
				.include(XIAN1).include(CHENGDU1)
				.include(marker1).include(marker2).include(marker3)
				.include(marker4).include(marker5).include(marker6)
				.include(marker7).include(marker8).include(marker9)
				.include(marker10).build();
		mAMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 10));
	}

	/**
	 * 对marker标注点点击响应事件
	 */
	@Override
	public boolean onMarkerClick(Marker marker) {
		markerText.setText("你点击的是" + marker.getTitle());
		return false;

	}

	/**
	 * 监听自定义infowindow窗口的infocontents事件回调
	 */
	@Override
	public View getInfoContents(Marker marker) {
		View infoContent = getLayoutInflater().inflate(
				R.layout.custom_info_contents, null);
		render(marker, infoContent);
		return infoContent;
	}

	/**
	 * 自定义infowinfow窗口
	 */
	public void render(Marker marker, View view) {
			((ImageView) view.findViewById(R.id.badge))
					.setImageResource(R.drawable.badge_sa);
		String title = marker.getTitle();
		TextView titleUi = ((TextView) view.findViewById(R.id.title));
		if (title != null) {
			SpannableString titleText = new SpannableString(title);
			titleText.setSpan(new ForegroundColorSpan(Color.RED), 0,
					titleText.length(), 0);
			titleUi.setTextSize(15);
			titleUi.setText(titleText);

		} else {
			titleUi.setText("");
		}
		String snippet = marker.getSnippet();
		TextView snippetUi = ((TextView) view.findViewById(R.id.snippet));
		if (snippet != null) {
			SpannableString snippetText = new SpannableString(snippet);
			snippetText.setSpan(new ForegroundColorSpan(Color.GREEN), 0,
					snippetText.length(), 0);
			snippetUi.setTextSize(20);
			snippetUi.setText(snippetText);
		} else {
			snippetUi.setText("");
		}
	}

	@Override
	public View getInfoWindow(Marker marker) {
		View infoWindow = getLayoutInflater().inflate(
				R.layout.custom_info_window, null);

		render(marker, infoWindow);
		return infoWindow;
	}



	@Override
	public void onInfoWindowClick(Marker marker) {
		Toast.makeText(this, "你点击了infoWindow窗口" + marker.getTitle(), Toast.LENGTH_LONG).show();
	}
}
