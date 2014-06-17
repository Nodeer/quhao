package com.withiter.quhao.data;

import android.os.Parcel;
import android.os.Parcelable;

public class CategoryData implements Parcelable{

	private long count = 0;
	private String categoryType;
	private String cateName;

	public CategoryData() {

	}

	public static final Parcelable.Creator<CategoryData> CREATOR = new Creator<CategoryData>() {
		
		@Override
		public CategoryData[] newArray(int size) {
			return new CategoryData[size];
		}
		
		@Override
		public CategoryData createFromParcel(Parcel source) {
			
			CategoryData data = new CategoryData();
			data.count = source.readLong();
			data.categoryType = source.readString();
			data.cateName = source.readString();
			return data;
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(count);
		dest.writeString(categoryType);
		dest.writeString(cateName);
	}

	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}

	public String getCategoryType() {
		return categoryType;
	}

	public void setCategoryType(String categoryType) {
		this.categoryType = categoryType;
	}

	public String getCateName() {
		return cateName;
	}

	public void setCateName(String cateName) {
		this.cateName = cateName;
	}
}
