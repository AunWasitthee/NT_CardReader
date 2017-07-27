package com.example.aunnie_iw.ntcardreader;

import java.io.Serializable;

/**
 * Created by Aunnie-IW on 19/6/2560.
 */

public class AddressData implements Serializable {
    private String houseNumber;
    private String moo;
    private String soi;
    private String road;
    private String tambon;
    private String amphur;
    private String province;
    private String postcode;
    private String landmark;
    private Double latitude;
    private Double longitude;
    private String address;
    private String photourl;

    public AddressData() {
    }

    public String getHouseNumber() {
        return this.houseNumber;
    }

    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }

    public String getMoo() {
        return this.moo;
    }

    public void setMoo(String moo) {
        this.moo = moo;
    }


    public String getSoi() {
        return this.soi;
    }

    public void setSoi(String soi) {
        this.soi = soi;
    }

    public String getRoad() {
        return this.road;
    }

    public void setRoad(String road) {
        this.road = road;
    }

    public String getTambon() {
        return this.tambon;
    }

    public void setTambon(String tambon) {
        this.tambon = tambon;
    }

    public String getAmphur() {
        return this.amphur;
    }

    public void setAmphur(String amphur) {
        this.amphur = amphur;
    }

    public String getProvince() {
        return this.province;
    }

    public void setProvince(String province) {
        this.province = province;
    }
    public String getPostcode() {
        return this.postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getLandmark() {
        return this.landmark;
    }

    public void setLandmark(String landmark) {
        this.landmark = landmark;
    }

    public Double getLatitude() {
        return this.latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return this.longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhotourl() {
        return this.photourl;
    }

    public void setPhotourl(String photourl) {
        this.photourl = photourl;
    }
}
