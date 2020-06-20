package com.example.onlineassignment;

import com.google.firebase.database.Exclude;

public class TestModel {
    String mLink;
    String mName;
    String mKey;
    public TestModel(String testName, String testLink) {
        this.mName = testName;
        this.mLink = testLink;
    }
    public TestModel() {

    }



    public String getTestName() {
        return  mName;
    }

    public void setTestName(String testName) {
        this.mName = testName;
    }

    public String getTestLink() {
        return mLink;
    }

    public void setTestLink(String testLink) {
        this.mLink = testLink;
    }
    @Exclude
    public String getKey() {
        return mKey;
    }
    @Exclude
    public void setKey(String key) {
        mKey = key;
    }

}
