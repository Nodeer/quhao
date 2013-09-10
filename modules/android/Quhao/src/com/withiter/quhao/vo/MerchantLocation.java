package com.withiter.quhao.vo;


public class MerchantLocation
{
	public String id;
	public String name;// 站点名称
	public double lat;// 经度
	public double lng;// 纬度
	public String address;// 地址

	public MerchantLocation(String id, String name,double lat,double lng,String address)
	{
		this.id = id;
		this.name = name;
		this.lat = lat;
		this.lng = lng;
		this.address = address;
	}
	
}
