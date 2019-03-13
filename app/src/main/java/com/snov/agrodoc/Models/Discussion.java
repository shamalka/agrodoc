package com.snov.agrodoc.Models;

public class Discussion {
    String UserName;
    String UserID;
    String Type;
    String Body;
    String Date;
    String Time;

    public String getUserName(){
        return UserName;
    }

    public void setUserName(String UserName){
        this.UserName=UserName;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getBody() {
        return Body;
    }

    public void setBody(String body) {
        Body = body;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }
}
