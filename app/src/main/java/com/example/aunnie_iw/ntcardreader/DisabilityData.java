package com.example.aunnie_iw.ntcardreader;

import java.io.Serializable;

/**
 * Created by Aunnie-IW on 19/6/2560.
 */

public class DisabilityData implements Serializable {
    private String disability;
    private String haveHearingAids;
    private String signLangTH;
    private String spokenTH;
    private String readTH;
    private String writeTH;
    private String lipRead;
    public DisabilityData() {
    }

    public String getDisability() {
        return this.disability;
    }

    public void setDisability(String disability) {
        this.disability = disability;
    }

    public String getHaveHearingAids() {
        return this.haveHearingAids;
    }

    public void setHaveHearingAids(String haveHearingAids) {
        this.haveHearingAids = haveHearingAids;
    }

    public String getSignLangTH() {
        return this.signLangTH;
    }

    public void setSignLangTH(String signLangTH) {
        this.signLangTH = signLangTH;
    }

    public String getSpokenTH() {
        return this.spokenTH;
    }

    public void setSpokenTH(String spokenTH) {
        this.spokenTH = spokenTH;
    }

    public String getReadTH() {
        return this.readTH;
    }

    public void setReadTH(String readTH) {
        this.readTH = readTH;
    }

    public String getWriteTH() {
        return this.writeTH;
    }

    public void setWriteTH(String writeTH) {
        this.writeTH = writeTH;
    }

    public String getLipRead() {
        return this.lipRead;
    }

    public void setLipRead(String lipRead) {
        this.lipRead = lipRead;
    }
}
