package com.hellotheworld.mineclearance.Common.Rank.Bean;

public class RankBean {
    private String name;
    private Integer time;
    private String screenShot;

    public RankBean() {
    }

    public RankBean(String name, Integer time, String screenShot) {
        this.name = name;
        this.time = time;
        this.screenShot = screenShot;
    }

    public RankBean(String name, Float time, String screenShot) {
        this.name = name;
        this.time = time.intValue();
        this.screenShot = screenShot;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getTime() {
        return time;
    }

    public void setTime(Integer time) {
        this.time = time;
    }

    public String getScreenShot() {
        return screenShot;
    }

    public void setScreenShot(String screenShot) {
        this.screenShot = screenShot;
    }
}
