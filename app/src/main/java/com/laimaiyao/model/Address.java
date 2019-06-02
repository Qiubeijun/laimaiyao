package com.laimaiyao.model;

/**
 * Created by MI on 2019/6/1
 */
public class Address {
    int AID;
    int UID;
    String Name;
    String Phone;
    String District;
    String DetailedAddress;
    String Tag;


    public int getAID() {
        return AID;
    }

    public int getUID() {
        return UID;
    }

    public void setAID(int AID) {
        this.AID = AID;
    }

    public void setUID(int UID) {
        this.UID = UID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getDistrict() {
        return District;
    }

    public void setDistrict(String district) {
        District = district;
    }

    public String getDetailedAddress() {
        return DetailedAddress;
    }

    public void setDetailedAddress(String detailedAddress) {
        DetailedAddress = detailedAddress;
    }

    public String getTag() {
        return Tag;
    }

    public void setTag(String tag) {
        Tag = tag;
    }
}
