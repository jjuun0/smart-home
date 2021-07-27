package com.example.SmartHome;

public class Admin {
    private String allow;
    private String time;

    public Admin(String allow, String time) {
        this.allow = allow;
        this.time = time;
    }

    public String getAllow() {
        return allow;
    }

    public void setAllow(String allow) {
        this.allow = allow;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
