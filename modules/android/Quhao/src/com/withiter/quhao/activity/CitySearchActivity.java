package com.withiter.quhao.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
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
				if(null == searchCityAdapter)
				{
					searchCityAdapter = new SearchCityAdapter(CitySearchActivity.this);
					citySearchListView.setAdapter(searchCityAdapter);
				}
				
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
			Intent intent = new Intent(QuhaoConstant.ACTION_CITY_CHANGED);
			sendBroadcast(intent);
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
				
				for (int i = 0; i < cityData.size(); i++) {
					CityInfo cityInfo = cityData.get(i);
					if(cityInfo.cityName.indexOf(content)>=0)
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
			else
			{
				searchCityList = new ArrayList<CityInfo>();
				for (int i = 0; i < cityData.size(); i++) {
					CityInfo cityInfo = cityData.get(i);
					searchCityList.add(cityInfo);
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
			searchCityList = new ArrayList<CityInfo>();
			String cityStr = "";
			CityInfo cityInfo = null;
			for (int i = 0; i < citys.length; i++) {
				cityStr = citys[i];
				String[] eles = cityStr.split("-");
				cityInfo = new CityInfo(eles[0], eles[2], eles[1]);
				cityData.add(cityInfo);
				searchCityList.add(cityInfo);
			}
			
			if(null == searchCityAdapter)
			{
				searchCityAdapter = new SearchCityAdapter(CitySearchActivity.this);
				citySearchListView.setAdapter(searchCityAdapter);
			}
			
			searchCityAdapter.notifyDataSetChanged();
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
				unlockHandler.sendEmptyMessageDelayed(UNLOCK_CLICK, 1000);
				Intent intent1 = new Intent();
				intent1.setClass(this, CitySelectActivity.class);
				startActivity(intent1);
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
	
	@Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
        	
            // 获得当前得到焦点的View，一般情况下就是EditText（特殊情况就是轨迹求或者实体案件会移动焦点）
            View v = getCurrentFocus();

            if (isShouldHideInput(v, ev)) {
                hideSoftInput(v.getWindowToken());
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时没必要隐藏
     * 
     * @param v
     * @param event
     * @return
     */
    private boolean isShouldHideInput(View v, MotionEvent event) {
    	
        if (v != null && (v instanceof EditText)) {
            int[] l = { 0, 0 };
            v.getLocationInWindow(l);
            int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left
                    + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击EditText的事件，忽略它。
                return false;
            } else {
                return true;
            }
        }
        // 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditView上，和用户用轨迹球选择其他的焦点
        return false;
    }

    /**
     * 多种隐藏软件盘方法的其中一种
     * 
     * @param token
     */
    private void hideSoftInput(IBinder token) {
        if (token != null) {
            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(token,
                    InputMethodManager.HIDE_NOT_ALWAYS);
            
//            if(im.isActive()){
//            	im.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS);
//			}
        }
    }
}
