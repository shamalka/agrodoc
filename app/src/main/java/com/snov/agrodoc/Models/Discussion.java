package com.snov.agrodoc.Models;

public class Discussion {
    String UserName;
    String UserID;
    String Type;
    String Title;
    String Body;
    String ImageUrl;
    String Date;
    String Time;
    String DocID;

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }

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

    public void setDocID(String docID) {
        DocID = docID;
    }

    public String getDocID() {
        return DocID;
    }
}
