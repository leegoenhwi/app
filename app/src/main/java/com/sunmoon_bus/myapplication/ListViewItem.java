package com.sunmoon_bus.myapplication;

public class ListViewItem {
    private String titleStr ;
    private String descStr ;
    private String dayStr;

    public void setTitle(String title) {
        titleStr = title ;
    }
    public void setDesc(String desc) {
        descStr = desc ;
    }
    public void setDay(String day){ dayStr = day;}

    public String getTitle() {
        return this.titleStr ;
    }
    public String getDay() {
        return this.dayStr ;
    }
    public String getDesc() {
        return this.descStr ;
    }
}