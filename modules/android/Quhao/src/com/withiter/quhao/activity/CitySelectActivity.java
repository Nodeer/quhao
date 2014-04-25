package com.withiter.quhao.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

import com.withiter.quhao.QHClientApplication;
import com.withiter.quhao.R;
import com.withiter.quhao.domain.CityInfo;
import com.withiter.quhao.util.tool.QuhaoConstant;
import com.withiter.quhao.util.tool.SharedprefUtil;

public class CitySelectActivity extends QuhaoBaseActivity {

	/**
	 * 城市的listView
	 */
	private ListView cityListView;
	
	/**
	 * 字体高度
	 */
	private int height;
	
	private List<String> strList = new ArrayList<String>();
	private String[] str = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J",
			"K", "L", "M", "N", "O", "P", "Q", "U", "V", "W", "X", "Y", "Z" };
	
	/**
	 * 城市数据
	 */
	/*
	private String data[] = { "android", "java", "news", "baidu", "oberser",
			"mary", "next", "ruby", "money", "lucy", "very", "thunder",
			"object", "lily", "jay", "answer", "layout", "demos", "com",
			"collect", "custom", "blog", "round", "redirect", "ground", "gray",
			"blue", "zone", "james", "zhang", "location" };
	*/
	
	private List<CityInfo> cityData;
	
	private List<CityInfo> remenCity;
	/**
	 *  数据源，整合了索引字母
	 */
	private List<CityInfo> nData;
	
	private LinearLayout layoutIndex;;
	
	private MyAdapter adapter;
	
	private HashMap<String, Integer> selector;// 存放含有索引字母的位置
	
	private TextView tv_show;// 中间显示标题的文本
	
	/**
	 * 取消按钮
	 */
	private Button cancelBtn;
	
	/**
	 * 查询输入框
	 */
	private EditText searchEdit;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.city_select_layout);
		super.onCreate(savedInstanceState);
		
		layoutIndex = (LinearLayout) this.findViewById(R.id.layout);
		layoutIndex.setBackgroundColor(Color.parseColor("#00ffffff"));
		
		cancelBtn = (Button) this.findViewById(R.id.cancel_btn);
		cancelBtn.setOnClickListener(this);
		
		//
		searchEdit = (EditText) this.findViewById(R.id.search_edit);
		
		cityListView = (ListView) this.findViewById(R.id.cityListView);
		tv_show = (TextView) findViewById(R.id.tv);
		tv_show.setVisibility(View.INVISIBLE);
		
		searchEdit.setOnClickListener(this);
		
//		initView();
		
	}

	private void initView() {
//		layoutIndex.removeAllViews();
		String[] citys = this.getResources().getStringArray(R.array.city);
		
		if(null != citys && citys.length>0)
		{
			cityData = new ArrayList<CityInfo>();
			
			String cityStr = "";
			CityInfo cityInfo = null;
			for (int i = 0; i < citys.length; i++) {
				cityStr = citys[i];
				String[] eles = cityStr.split("-");
				cityInfo = new CityInfo(eles[0], eles[2], eles[1]);
				cityData.add(cityInfo);
			}
		}
		
		String[] remenCitys = this.getResources().getStringArray(R.array.remen);
		if(null != remenCitys && remenCitys.length>0)
		{
			remenCity = new ArrayList<CityInfo>();
			
			String cityStr = "";
			CityInfo cityInfo = null;
			for (int i = 0; i < remenCitys.length; i++) {
				cityStr = remenCitys[i];
				String[] eles = cityStr.split("-");
				cityInfo = new CityInfo(eles[0], eles[2], eles[1]);
				remenCity.add(cityInfo);
			}
		}
		
		strList.add("热门");
		for (int i = 0; i < str.length; i++) {
			strList.add(str[i]);
		}
		
		sortIndex();
		
		
	}

	private OnItemClickListener cityListListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view,
				int position, long id) {
			if(isClick)
			{
				return;
			}
			isClick = true;
			CityInfo defaultCity = nData.get(position);
			QHClientApplication.getInstance().defaultCity = defaultCity;
			SharedprefUtil.put(CitySelectActivity.this, QuhaoConstant.CITY_CODE, defaultCity.cityCode);
			SharedprefUtil.put(CitySelectActivity.this, QuhaoConstant.CITY_NAME, defaultCity.cityName);
			SharedprefUtil.put(CitySelectActivity.this, QuhaoConstant.CITY_PINYIN, defaultCity.cityPinyin);
//			Log.e("wjzwjz", nData.get(position).cityPinyin + "--" + nData.get(position).cityName);
//			unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			CitySelectActivity.this.finish();
		}
	};
	/** 获取排序后的新数据 */
	public void sortIndex() {
		TreeSet<String> set = new TreeSet<String>();
		// 获取初始化数据源中的首字母，添加到set中
		for (CityInfo cityInfo : cityData) {
			
			set.add(String.valueOf(cityInfo.cityPinyin.charAt(0)));
			
		}
		// 新数组的长度为原数据加上set的大小
		nData = new ArrayList<CityInfo>();
		List<CityInfo> cityList = new ArrayList<CityInfo>();
		CityInfo cityInfo = null;
		for (String string : set) {
			cityInfo = new CityInfo("", string, string);
			cityList.add(cityInfo);
		}
		// 将原数据拷贝到新数据中
		for (int j = 0; j < cityData.size(); j++) {
			CityInfo temp = new CityInfo(cityData.get(j).cityCode, cityData.get(j).cityName, cityData.get(j).cityPinyin);
			cityList.add(temp);
		}
//		System.arraycopy(data, 0, nData, set.size(), data.length);
//		Arrays.sort(nData, String.CASE_INSENSITIVE_ORDER);// 自动按照首字母排序
		Collections.sort(cityList);
		
		nData.add(new CityInfo("", "热门", "热门"));
		
		nData.addAll(remenCity);
		
		nData.addAll(cityList);
		
		adapter = new MyAdapter(this);
		cityListView.setAdapter(adapter);
		adapter.notifyDataSetChanged();
		cityListView.setOnItemClickListener(cityListListener);
	}
	
	@Override
	protected void onResume() {
		initView();
		super.onResume();
	};
	/** 绘制索引列表 */
	public void getIndexView() {
		LinearLayout.LayoutParams params = new LayoutParams(
				LayoutParams.FILL_PARENT, height);
//		 params.setMargins(10, 5, 10, 0);
		for (int i = 0; i < strList.size(); i++) {
			final TextView tv = new TextView(this);
			tv.setLayoutParams(params);
			tv.setText(strList.get(i));
			// tv.setTextColor(Color.parseColor("#606060"));
			// tv.setTextSize(16);
			tv.setGravity(Gravity.CENTER);
			tv.setPadding(10, 0, 10, 0);
			layoutIndex.addView(tv);
			
			layoutIndex.setBackgroundColor(Color
					.parseColor("#606060"));
			
			tv.setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event)
				{
					TextView vv = (TextView)v;
					String content = vv.getText().toString();
					
					// 防止越界
//					
					if (selector.containsKey(content)) {
						int pos = selector.get(content);
						if (cityListView.getHeaderViewsCount() > 0) {// 防止ListView有标题栏，本例中没有。
							cityListView.setSelectionFromTop(
									pos + cityListView.getHeaderViewsCount(), 0);
						} else {
							cityListView.setSelectionFromTop(pos, 0);// 滑动到第一项
						}
						tv_show.setVisibility(View.VISIBLE);
						tv_show.setText(content);
					}
				
					/*
					 	float y = event.getY();
						int index = (int) (y / height);
						if (index > -1 && index < strList.size()) {// 防止越界
						String key = strList.get(index);
						if (selector.containsKey(key)) {
							int pos = selector.get(key);
							if (cityListView.getHeaderViewsCount() > 0) {// 防止ListView有标题栏，本例中没有。
								cityListView.setSelectionFromTop(
										pos + cityListView.getHeaderViewsCount(), 0);
							} else {
								cityListView.setSelectionFromTop(pos, 0);// 滑动到第一项
							}
							tv_show.setVisibility(View.VISIBLE);
							tv_show.setText(strList.get(index));
						}
					}*/
					switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						layoutIndex.setBackgroundColor(Color
								.parseColor("#606060"));
						break;

					case MotionEvent.ACTION_MOVE:

						break;
					case MotionEvent.ACTION_UP:
						layoutIndex.setBackgroundColor(Color
								.parseColor("#606060"));
//						layoutIndex.setBackgroundColor(Color
//								.parseColor("#00ffffff"));
						tv_show.setVisibility(View.INVISIBLE);
						break;
					}
					return true;
				}
			});
		}
	}
	
	@Override
	public void onClick(View v) {
		if (isClick) {
			return;
		}
		isClick = true;
		
		switch(v.getId())
		{
			case R.id.cancel_btn:
				this.finish();
				break;
			case R.id.search_edit:
				Intent intent = new Intent();
				intent.setClass(this, CitySearchActivity.class);
				startActivity(intent);
				this.finish();
				break;
			default:
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				break;
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return false;
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		// 在oncreate里面执行下面的代码没反应，因为oncreate里面得到的getHeight=0
		height = layoutIndex.getHeight() / strList.size();
		getIndexView();
	}
	
	/** 适配器 */
	private class MyAdapter extends BaseAdapter {

		public MyAdapter(Context context) {
			selector = new HashMap<String, Integer>();
			for (int j = 0; j < strList.size(); j++) {// 循环字母表，找出nData中对应字母的位置
				if("热门".equals(strList.get(j)))
				{
					selector.put("热门", 0);
					continue;
				}
				for (int i = 0; i < nData.size(); i++) {
					
					if (nData.get(i).cityPinyin.equals(strList.get(j))) {
						selector.put(strList.get(j), i);
					}
				}

			}
		}

		@Override
		public int getCount() {	
			return nData.size();
		}

		@Override
		public Object getItem(int position) {
			
			return nData.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public boolean isEnabled(int position) {
			if("热门".equals(nData.get(position).cityPinyin) && "热门".equals(nData.get(position).cityName))
			{
				return false;
			}
			else if("热门".equals(nData.get(position).cityPinyin) && !"热门".equals(nData.get(position).cityName))
			{
				return super.isEnabled(position);
			}
			else if (strList.contains(nData.get(position).cityPinyin))// 如果是字母索引
			{
				return false;// 表示不能点击
			}
			
			return super.isEnabled(position);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			CityInfo item = nData.get(position);

			if("热门".equals(nData.get(position).cityPinyin) && "热门".equals(nData.get(position).cityName))
			{
				convertView = getLayoutInflater().inflate(R.layout.city_list_index, null);
			}
			else if ("热门".equals(nData.get(position).cityPinyin) && !"热门".equals(nData.get(position).cityName))
			{
				convertView = getLayoutInflater().inflate(R.layout.city_list_item, null);
			}
			else if(strList.contains(nData.get(position).cityPinyin))
			{
				convertView = getLayoutInflater().inflate(R.layout.city_list_index, null);
			}
			else
			{
				convertView = getLayoutInflater().inflate(R.layout.city_list_item, null);
			}
				
			TextView tv = (TextView) convertView.findViewById(R.id.textView1);

			tv.setText(item.cityName);
			return convertView;
		}

	}
	
}
