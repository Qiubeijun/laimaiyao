package com.laimaiyao.model;

/**
 * Created by MI on 2019/4/26
 */
public class ProductCategory {
    private String CategoryName;
    private int ParentID;
    private String URL;

    public String getCategoryName() {
        return CategoryName;
    }

    public void setCategoryName(String name) {
        CategoryName = name;
    }

    public int getParentID() {
        return ParentID;
    }

    public void setParentID(int parentID) {
        ParentID = parentID;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }
}
