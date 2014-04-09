package com.withiter.quhao.domain;


/**
 * 
 * 城市信息
 * 
 * @author Jazze
 *
 */
public class CityInfo implements Comparable<CityInfo>{

	/**
	 * 城市编码
	 */
	public String cityCode;
	
	/**
	 * 城市名称
	 */
	public String cityName;
	
	/**
	 * 城市拼音
	 */
	public String cityPinyin;
	
	public CityInfo(String cityCode, String cityName, String cityPinyin)
	{
		this.cityCode = cityCode;
		this.cityName = cityName;
		this.cityPinyin = cityPinyin;
	}

	@Override
	public int compareTo(CityInfo another) {
		
		if("热门".equals(this.cityPinyin))
		{
			if("热门".equals(another.cityPinyin))
			{
				return this.cityName.compareTo(another.cityName);
			}
			else
			{
				return -1;
			}
		}
		return this.cityPinyin.compareTo(another.cityPinyin);
	}
}
