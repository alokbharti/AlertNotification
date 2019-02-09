package com.example.alertnotifications;

public class NotificationList {

    private String category;
    private String title;
    private String time_stamp;
    private boolean flag;

    public NotificationList( String category, String title, String time_stamp,boolean flag) {

        this.category = category;
        this.title = title;
        this.time_stamp = time_stamp;
        this.flag=flag;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTime_stamp() {
        return time_stamp;
    }

    public void setTime_stamp(String time_stamp) {
        this.time_stamp = time_stamp;
    }
}
