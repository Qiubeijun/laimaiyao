package com.laimaiyao.model;

/**
 * Created by MI on 2019/4/24
 */
public class Product {
    private String PID;
    private String PName;
    private String Title;
    private String BestCount;
    private String MediumCount;
    private String BadCount;
    private String Abstract;
    private String SalesVolume;
    private String Price;
    private String Comment;
    private String Brand;
    private String Specification;

    public String getSpecification() {
        return Specification;
    }

    public void setSpecification(String specification) {
        Specification = specification;
    }

    public String getBrand() {
        return Brand;
    }

    public void setBrand(String brand) {
        Brand = brand;
    }

    public String getComment() {
        return Comment;
    }

    public void setComment(String comment) {
        Comment = comment;
    }



    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getAbstract() {
        return Abstract;
    }

    public void setAbstract(String anAbstract) {
        Abstract = anAbstract;
    }

    public String getSalesVolume() {
        return SalesVolume;
    }

    public void setSalesVolume(String salesVolume) {
        SalesVolume = salesVolume;
    }

    public String getPID() {
        return PID;
    }

    public void setPID(String PID) {
        this.PID = PID;
    }

    public String getPName() {
        return PName;
    }

    public void setPName(String PName) {
        this.PName = PName;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getBestCount() {
        return BestCount;
    }

    public void setBestCount(String bestCount) {
        BestCount = bestCount;
    }

    public String getMediumCount() {
        return MediumCount;
    }

    public void setMediumCount(String mediumCount) {
        MediumCount = mediumCount;
    }

    public String getBadCount() {
        return BadCount;
    }

    public void setBadCount(String badCount) {
        BadCount = badCount;
    }
}
