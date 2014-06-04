package com.withiter.quhao.data;

import android.os.Parcel;
import android.os.Parcelable;

public class MerchantData implements Parcelable{

	private String id;
	private String merchantImage = "";
	private String name;
	private String address;
	
	/**
	 * 经度
	 */
	private double lat;
	
	/**
	 * 纬度
	 */
	private double lng;
	
	public static final Parcelable.Creator<MerchantData> CREATOR = new Creator<MerchantData>() {
		
		@Override
		public MerchantData[] newArray(int size) {
			return new MerchantData[size];
		}
		
		@Override
		public MerchantData createFromParcel(Parcel source) {
			
			MerchantData data = new MerchantData();
			data.id = source.readString();
			data.merchantImage = source.readString();
			data.name = source.readString();
			data.address = source.readString();
			data.lat = source.readDouble();
			data.lng = source.readDouble();
			return data;
		}
	};
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		
		dest.writeString(id);
		dest.writeString(merchantImage);
		dest.writeString(name);
		dest.writeString(address);
		dest.writeDouble(lat);
		dest.writeDouble(lng);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMerchantImage() {
		return merchantImage;
	}

	public void setMerchantImage(String merchantImage) {
		this.merchantImage = merchantImage;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLng() {
		return lng;
	}

	public void setLng(double lng) {
		this.lng = lng;
	}
}
