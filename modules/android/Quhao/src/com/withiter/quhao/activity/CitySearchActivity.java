package com.withiter.quhao.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.withiter.quhao.QHClientApplication;
import com.withiter.quhao.R;
import com.withiter.quhao.domain.CityInfo;
import com.withiter.quhao.util.StringUtils;
import com.withiter.quhao.util.tool.QuhaoConstant;
import com.withiter.quhao.util.tool.SharedprefUtil;

public class CitySearchActivity extends QuhaoBaseActivity {

	/**
	 * 取消按钮
	 */
	private Button cancelBtn;
	
	private List<CityInfo> cityData = new ArrayList<CityInfo>();
	
	/**
	 * 查询输入框
	 */
	private EditText searchEdit;
	
	private FrameLayout citySearchLayout;
	
	private ListView citySearchListView;
	
	private Button searchBtn;
	private SearchCityAdapter searchCityAdapter;
	
	private List<CityInfo> searchCityList = new ArrayList<CityInfo>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.city_search_layout);
		super.onCreate(savedInstanceState);
		
		cancelBtn = (Button) this.findViewById(R.id.cancel_btn);
		cancelBtn.setOnClickListener(this);
		
		//
		searchEdit = (EditText) this.findViewById(R.id.search_edit);
		
		citySearchLayout = (FrameLayout) this.findViewById(R.id.citySearchLayout);
		citySearchListView = (ListView) this.findViewById(R.id.citySearchListView);
		searchBtn = (Button) this.findViewById(R.id.search_btn);
		searchBtn.setOnClickListener(this);
		searchBtn.setVisibility(View.GONE);
		
		citySearchListView.setOnItemClickListener(citySearchListener);
		
		searchEdit.setOnClickListener(this);
		searchEdit.setOnFocusChangeListener(searchEditListener);
		searchEdit.addTextChangedListener(searchWatcher);
		
		initView();
		
	}

	private OnFocusChangeListener searchEditListener = new OnFocusChangeListener() {
		
		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			
			if(hasFocus)
			{
				citySearchLayout.setVisibility(View.VISIBLE);
//				searchCityList = new ArrayList<CityInfo>();
				searchCityAdapter = new SearchCityAdapter(CitySearchActivity.this);
				searchBtn.setVisibility(View.VISIBLE);
				citySearchListView.setAdapter(searchCityAdapter);
				searchCityAdapter.notifyDataSetChanged();
			}
			
		}
	};
	private OnItemClickListener citySearchListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view,
				int position, long id) {
			if(isClick)
			{
				return;
			}
			isClick = true;
			CityInfo defaultCity = searchCityList.get(position);
			QHClientApplication.getInstance().defaultCity = defaultCity;
			SharedprefUtil.put(CitySearchActivity.this, QuhaoConstant.CITY_CODE, defaultCity.cityCode);
			SharedprefUtil.put(CitySearchActivity.this, QuhaoConstant.CITY_NAME, defaultCity.cityName);
			SharedprefUtil.put(CitySearchActivity.this, QuhaoConstant.CITY_PINYIN, defaultCity.cityPinyin);
//			Log.e("wjzwjz", nData.get(position).cityPinyin + "--" + nData.get(position).cityName);
//			unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
			CitySearchActivity.this.finish();
		}
	};
	private TextWatcher searchWatcher = new TextWatcher() {
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		}

		@Override
		public void afterTextChanged(Editable s) {
			String content = searchEdit.getText().toString();
			
			if(StringUtils.isNotNull(content))
			{
				content = content.substring(0, 1).toUpperCase() + content.substring(1);
				searchCityList = new ArrayList<CityInfo>();
				for (int i = 0; i < cityData.size(); i++) {
					CityInfo cityInfo = cityData.get(i);
					if(cityInfo.cityPinyin.indexOf(content)>=0)
					{
						if(searchCityList.contains(cityInfo))
						{
							continue;
						}
						searchCityList.add(cityInfo);
					}
				}
				
				for (int i = 0; i < cityData.size(); i++) {
					CityInfo cityInfo = cityData.get(i);
					if(cityInfo.cityCode.indexOf(content)>=0)
					{
						if(searchCityList.contains(cityInfo))
						{
							continue;
						}
						searchCityList.add(cityInfo);
					}
				}
				
				searchCityAdapter.notifyDataSetChanged();
			}

		}
	};
	private void initView() {
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
				Intent intent1 = new Intent();
				intent1.setClass(this, CitySelectActivity.class);
				startActivity(intent1);
				this.finish();
				break;
			case R.id.search_edit:
				Intent intent = new Intent();
				intent.setClass(this, CitySearchActivity.class);
				startActivity(intent);
				break;
			case R.id.search_btn:
				citySearchLayout.setVisibility(View.GONE);
				searchBtn.setVisibility(View.GONE);
				initView();
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
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

	/** 适配器 */
	private class SearchCityAdapter extends BaseAdapter {

		Context context;

		public SearchCityAdapter(Context context) {
			this.context = context;
		}

		@Override
		public int getCount() {	
			return searchCityList.size();
		}

		@Override
		public Object getItem(int position) {
			
			return searchCityList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			CityInfo item = searchCityList.get(position);

			convertView = getLayoutInflater().inflate(R.layout.city_list_item, null);
				
			TextView tv = (TextView) convertView.findViewById(R.id.textView1);

			tv.setText(item.cityName);
			return convertView;
		}

	}
}
