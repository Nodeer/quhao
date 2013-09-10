package com.withiter.quhao.util.baidu;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

import com.baidu.mapapi.GeoPoint;
import com.baidu.mapapi.ItemizedOverlay;
import com.baidu.mapapi.MapView;
import com.baidu.mapapi.OverlayItem;
import com.withiter.quhao.R;
import com.withiter.quhao.vo.MerchantLocation;

public class MerchantItemizedOverlay extends ItemizedOverlay<OverlayItem>
{
	public List<OverlayItem> mGeoList = new ArrayList<OverlayItem>();
	private Drawable marker;
	private Context mContext;
	private MapView mMapView;
	private View mPopView = null;
	private List<MerchantLocation> locations;

	public MerchantItemizedOverlay(Drawable marker, Context context,
			List<MerchantLocation> locations, MapView mapView) {
		super(boundCenterBottom(marker));
		this.marker = marker;
		this.mContext = context;
		this.mMapView = mapView;
		this.locations = locations;
		mPopView = LayoutInflater.from(mContext).inflate(R.layout.merchant_map_prompt,
				null);

		// 创建点击mark时的弹出泡泡
		mMapView.addView(mPopView, new MapView.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, null,
				MapView.LayoutParams.BOTTOM_CENTER));
		mPopView.setVisibility(View.GONE);

		for (MerchantLocation location : locations) {
			GeoPoint point = new GeoPoint((int) (location.lat * 1E6),
					(int) (location.lng * 1E6));
			// 构造OverlayItem的三个参数依次为：item的位置，标题文本，文字片段
			mGeoList.add(new OverlayItem(point, location.name, location.address));
		}

		populate(); // createItem(int)方法构造item。一旦有了数据，在调用其它方法前，首先调用这个方法
	}

	public void updateOverlay() {
		populate();
	}

	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {

		// Projection接口用于屏幕像素坐标和经纬度坐标之间的变换
		// Projection projection = mapView.getProjection();
		// for (int index = size() - 1; index >= 0; index--) { // 遍历mGeoList
		// OverlayItem overLayItem = getItem(index); // 得到给定索引的item
		//
		// String title = overLayItem.getTitle();
		// // 把经纬度变换到相对于MapView左上角的屏幕像素坐标
		// Point point = projection.toPixels(overLayItem.getPoint(), null);
		//
		// // 可在此处添加您的绘制代码
		// Paint paintText = new Paint();
		// paintText.setColor(Color.BLUE);
		// paintText.setTextSize(15);
		// canvas.drawText(title, point.x - 30, point.y, paintText); // 绘制文本
		// }

		super.draw(canvas, mapView, shadow);
		// 调整一个drawable边界，使得（0，0）是这个drawable底部最后一行中心的一个像素
		boundCenterBottom(marker);
	}

	@Override
	protected OverlayItem createItem(int i) {
		// TODO Auto-generated method stub
		return mGeoList.get(i);
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return mGeoList.size();
	}

	@Override
	// 处理当点击事件
	protected boolean onTap(final int i) {

		MerchantLocation location = locations.get(i);

		setFocus(mGeoList.get(i));
		// 更新气泡位置,并使之显示
		GeoPoint pt = mGeoList.get(i).getPoint();
		mMapView.updateViewLayout(mPopView, new MapView.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, pt,
				MapView.LayoutParams.BOTTOM_CENTER));

		((TextView) mPopView.findViewById(R.id.name)).setText(location.name);// 名称
		((TextView) mPopView.findViewById(R.id.address)).setText("地址："
				+ location.address);

		mPopView.setVisibility(View.VISIBLE);

		return true;
	}


	@Override
	public boolean onTap(GeoPoint arg0, MapView arg1) {
		// TODO Auto-generated method stub
		// 消去弹出的气泡
		mPopView.setVisibility(View.GONE);
		return super.onTap(arg0, arg1);
	}
}
