package com.example.bodyboost.model;

import java.io.Serializable;

public class WorkoutModel implements Serializable {
    String id, userId, type, date, startTime, endTime, isReminderEnable, firstPic, secondPic, status;
    public WorkoutModel() {
    }

    public WorkoutModel(String id, String userId, String type, String date, String startTime, String endTime, String isReminderEnable, String firstPic, String secondPic, String status) {
        this.id = id;
        this.userId = userId;
        this.type = type;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isReminderEnable = isReminderEnable;
        this.firstPic = firstPic;
        this.secondPic = secondPic;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getIsReminderEnable() {
        return isReminderEnable;
    }

    public void setIsReminderEnable(String isReminderEnable) {
        this.isReminderEnable = isReminderEnable;
    }

    public String getFirstPic() {
        return firstPic;
    }

    public void setFirstPic(String firstPic) {
        this.firstPic = firstPic;
    }

    public String getSecondPic() {
        return secondPic;
    }

    public void setSecondPic(String secondPic) {
        this.secondPic = secondPic;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
