package com.example.aunnie_iw.ntcardreader;

import java.io.Serializable;

/**
 * Created by Aunnie-IW on 19/6/2560.
 */

public class ContactData implements Serializable {
    private AddressData addressData;
    private String citizenID;
    private String prefixThai;

    private String firstNameThai;
    private String lastNameThai;

    private String relationship;
    private String sex;
    private String tell;
    private String hometell;

    public ContactData() {
    }

    public AddressData getAddressData() {
        return this.addressData;
    }

    public void setAddressData(AddressData addressData) {
        this.addressData = addressData;
    }

    public String getCitizenID() {
        return this.citizenID;
    }

    public void setCitizenID(String citizenID) {
        this.citizenID = citizenID;
    }

    public String getPrefixThai() {
        return this.prefixThai;
    }

    public void setPrefixThai(String prefixThai) {
        this.prefixThai = prefixThai;
    }

    public String getFirstNameThai() {
        return this.firstNameThai;
    }

    public void setFirstNameThai(String firstNameThai) {
        this.firstNameThai = firstNameThai;
    }

    public String getLastNameThai() {
        return this.lastNameThai;
    }

    public void setLastNameThai(String lastNameThai) {
        this.lastNameThai = lastNameThai;
    }

    public String getRelationship() {
        return this.relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    public String getSex() {
        return this.sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getTell() {
        return this.tell;
    }

    public void setTell(String tell) {
        this.tell = tell;
    }

    public String getHometell() {
        return this.hometell;
    }

    public void setHometell(String hometell) {
        this.hometell = hometell;
    }


}
