package com.withiter.quhao.data;

import android.os.Parcel;
import android.os.Parcelable;

public class ReservationData implements Parcelable{

	public String rId;
	public String accountId;
	public String merchantId;
	public String seatNumber;
	public String myNumber;
	public String beforeYou;
	public String currentNumber;
	
	public String merchantName;
	
	public String merchantAddress;
	
	public String merchantImage;
	
	public ReservationData()
	{
		
	}

	public String getrId() {
		return rId;
	}

	public void setrId(String rId) {
		this.rId = rId;
	}

	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	public String getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}

	public String getSeatNumber() {
		return seatNumber;
	}

	public void setSeatNumber(String seatNumber) {
		this.seatNumber = seatNumber;
	}

	public String getMyNumber() {
		return myNumber;
	}

	public void setMyNumber(String myNumber) {
		this.myNumber = myNumber;
	}

	public String getBeforeYou() {
		return beforeYou;
	}

	public void setBeforeYou(String beforeYou) {
		this.beforeYou = beforeYou;
	}

	public String getCurrentNumber() {
		return currentNumber;
	}

	public void setCurrentNumber(String currentNumber) {
		this.currentNumber = currentNumber;
	}

	public String getMerchantName() {
		return merchantName;
	}

	public void setMerchantName(String merchantName) {
		this.merchantName = merchantName;
	}

	public String getMerchantAddress() {
		return merchantAddress;
	}

	public void setMerchantAddress(String merchantAddress) {
		this.merchantAddress = merchantAddress;
	}

	public String getMerchantImage() {
		return merchantImage;
	}

	public void setMerchantImage(String merchantImage) {
		this.merchantImage = merchantImage;
	}
	
	public static final Parcelable.Creator<ReservationData> CREATOR = new Creator<ReservationData>() {
		
		@Override
		public ReservationData[] newArray(int size) {
			return new ReservationData[size];
		}
		
		@Override
		public ReservationData createFromParcel(Parcel source) {
			
			ReservationData data = new ReservationData();
			data.rId = source.readString();
			data.accountId = source.readString();
			data.merchantId = source.readString();
			data.seatNumber = source.readString();
			data.myNumber = source.readString();
			data.beforeYou = source.readString();
			data.currentNumber = source.readString();
			data.merchantName = source.readString();
			data.merchantAddress = source.readString();
			data.merchantImage = source.readString();
			return data;
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		
		dest.writeString(rId);
		dest.writeString(accountId);
		dest.writeString(merchantId);
		dest.writeString(seatNumber);
		dest.writeString(myNumber);
		dest.writeString(beforeYou);
		dest.writeString(currentNumber);
		dest.writeString(merchantName);
		dest.writeString(merchantAddress);
		dest.writeString(merchantImage);
		
	}
	
}
