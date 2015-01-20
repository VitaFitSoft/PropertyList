package com.example.propertylist.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Property implements Parcelable {

    public String mFullAddress;

    public String mDescription;

    public String mThumbnailUrl;

    public String mMediumThumbnailUrl;

    public String mLargeThumbnailUrl;

    public String mDaftPropertyUrl;

    public Property(){

    }

    /**
     * Retrieving Property data from Parcel object
     * This constructor is invoked by the method createFromParcel(Parcel source) of
     * the object CREATOR
     **/
    public Property(Parcel source){
        readFromParcel(source);
    }

    public void readFromParcel(Parcel source){
        mFullAddress = source.readString();
        mDescription = source.readString();
        mThumbnailUrl = source.readString();
        mMediumThumbnailUrl = source.readString();
        mLargeThumbnailUrl = source.readString();
        mDaftPropertyUrl = source.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mFullAddress);
        dest.writeString(mDescription);
        dest.writeString(mThumbnailUrl);
        dest.writeString(mMediumThumbnailUrl);
        dest.writeString(mLargeThumbnailUrl);
        dest.writeString(mDaftPropertyUrl);
    }

    public static final Creator<Property> CREATOR = new Creator<Property>() {
        public Property createFromParcel(Parcel source) {
            return new Property(source);
        }

        @Override
        public Property[] newArray(int size) {
            return new Property[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    public void setFullAddress(String fullAddress){
        this.mFullAddress = fullAddress;
    }

    public void setDescription(String description){
        this.mDescription = description;
    }

    public void setThumbnailUrl(String thumbnail_url){
        this.mThumbnailUrl = thumbnail_url;
    }

    public void setMediumThumbnailUrl(String medium_thumbnail_url){
        this.mMediumThumbnailUrl = medium_thumbnail_url;
    }

    public void setLargeThumbnailUrl(String large_thumbnail_url){
        this.mLargeThumbnailUrl = large_thumbnail_url;
    }

    public void setDaftPropertyUrl(String daft_property_url){
        this.mDaftPropertyUrl = daft_property_url;
    }

    public String getFullAddress(){
        return this.mFullAddress;
    }

    public String getDescription(){
        return this.mDescription;
    }

    public String getThumbnailUrl(){
        return this.mThumbnailUrl;
    }

    public String getMediumThumbnailUrl(){
        return this.mMediumThumbnailUrl;
    }

    public String getLargeThumbnailUrl(){
        return this.mLargeThumbnailUrl;
    }

    public String getDaftPropertyUrl(){
        return this.mDaftPropertyUrl;
    }

}