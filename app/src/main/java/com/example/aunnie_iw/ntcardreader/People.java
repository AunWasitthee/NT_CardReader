package com.example.aunnie_iw.ntcardreader;

import java.io.Serializable;

/**
 * Created by Aunnie-IW on 19/6/2560.
 */

public class People implements Serializable {
    private AddressData addressCard;
    private AddressData addressNow;
    private String contactID;
    private ProfileData profileData;
    private DisabilityData disabilityData;
    private String peopleKey;

    public People() {
    }

    public AddressData getAddressCard() {
        return this.addressCard;
    }

    public void setAddressCard(AddressData addressCard) {
        this.addressCard = addressCard;
    }

    public AddressData getAddressNow() {
        return this.addressNow;
    }

    public void setAddressNow(AddressData addressNow) {
        this.addressNow = addressNow;
    }

    public String getContactID() {
        return this.contactID;
    }

    public void setContactID(String contactID) {
        this.contactID = contactID;
    }

    public ProfileData getProfileData() {
        return this.profileData;
    }

    public void setProfileData(ProfileData profileData) {
        this.profileData = profileData;
    }

    public DisabilityData getDisabilityData() {
        return this.disabilityData;
    }

    public void setDisabilityData(DisabilityData disabilityData) {
        this.disabilityData = disabilityData;
    }

    public String getPeopleKey() {
        return this.peopleKey;
    }

    public void setPeopleKey(String peopleKey) {
        this.peopleKey = peopleKey;
    }

}
