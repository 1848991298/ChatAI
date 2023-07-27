package com.arixo.arixochat.bean;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public class FindContentResponse {

    @SerializedName("title")
    private String mTitle;
    @SerializedName("content")
    private String mContent;
    @SerializedName("score")
    private Integer mScore;

    public static FindContentResponse objectFromData(String str) {

        return new Gson().fromJson(str, FindContentResponse.class);
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        this.mContent = content;
    }

    public Integer getScore() {
        return mScore;
    }

    public void setScore(Integer score) {
        this.mScore = score;
    }
}
