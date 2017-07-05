package com.example.aunnie_iw.ntcardreader;

import java.io.Serializable;

/**
 * Created by Aunnie-IW on 19/6/2560.
 */

public class ProfileData implements Serializable {
    private String alive;
    private String img;
    private String citizenID;
    private String prefixThai;
    private String firstNameThai;
    private String lastNameThai;
    private String birthday;
    private String marriage;
    private String sex;
    private String bloodType;
    private String religion;
    private String tell;
    private String hometell;
    private String email;
    private String disease;
    private String allergy;
    private String hospitalNear;
    private String hospitalUse;


    public ProfileData() {
    }

    public String getAlive() {
        return this.alive;
    }

    public void setAlive(String alive) {
        this.alive = alive;
    }

    public String getImg() {
        return this.img;
    }

    public void setImg(String img) {
        this.img = img;
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

    public String getBirthday() {
        return this.birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getMarriage() {
        return this.marriage;
    }

    public void setMarriage(String marriage) {
        this.marriage = marriage;
    }

    public String getSex() {
        return this.sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getBloodType() {
        return this.bloodType;
    }

    public void setBloodType(String bloodType) {
        this.bloodType = bloodType;
    }

    public String getReligion() {
        return this.religion;
    }

    public void setReligion(String religion) {
        this.religion = religion;
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

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDisease() {
        return this.disease;
    }

    public void setDisease(String disease) {
        this.disease = disease;
    }

    public String getAllergy() {
        return this.allergy;
    }

    public void setAllergy(String allergy) {
        this.allergy = allergy;
    }

    public String getHospitalNear() {
        return this.hospitalNear;
    }

    public void setHospitalNear(String hospitalNear) {
        this.hospitalNear = hospitalNear;
    }

    public String getHospitalUse() {
        return this.hospitalUse;
    }

    public void setHospitalUse(String hospitalUse) {
        this.hospitalUse = hospitalUse;
    }


}
