package com.jiyehoo.easydmkj;

public class ActCardView {
    private String actCardName;
    private String actCardAid;
    private String actCardTime;
    private String imgUrl;
    private String json;

    public ActCardView(String actCardName, String actCardAid, String actCardTime, String imgUrl, String json) {
        this.actCardName = actCardName;
        this.actCardAid = actCardAid;
        this.actCardTime = actCardTime;
        this.imgUrl = imgUrl;
        this.json = json;
    }

    public String getActCardName() {
        return actCardName;
    }

    public String getActCardAid() {
        return actCardAid;
    }

    public String getActCardTime() {
        return actCardTime;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public String getJson() {
        return json;
    }
}
